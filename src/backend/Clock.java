package backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.json.*;


/**
 *  @author Kurt Floyd
 * This is the trimmed down version of the class I used to implement the API 
 * code, when merged with master it should ONLY insert the getShift and 
 * getProfile methods, and the previous getWeather and getAlarm methods should 
 * be deleted. 
 * 
 */
public class Clock {
	//Variables used for keeping track of alarms
	DayOfWeek clockday;
	LinkedList<Integer> alarmsTripped;
	//A unique identifier for this particular clock. Randomized in production.
	private static final int clockID = 0;
	//Debug flag
	private static final boolean DEBUG = true;
	// The cloud server name
	//private static final String HOSTNAME = "http://www.smart-clock.xyz.com";
	// Temporary hostname for the development server
	private static final String HOSTNAME = "http://192.168.1.69:8123";
	//An array of Lists of alarms
	private LinkedList<Alarm> alarms;
	// The current time shift
	private Duration timeshift;
	// The primary keys of the alarms fetched from the server
	private LinkedList<Integer> keys;
	
	
	public Clock(){
		alarmsTripped = new LinkedList<Integer>();
		clockday = LocalDate.now().getDayOfWeek();
		alarms = new LinkedList<Alarm>();
		timeshift = Duration.ZERO;
		keys = new LinkedList<Integer>();
	}
	
	/**
	 * Checks the given date and time to see if an alarm time has been reached. Only the soonest alarm is checked.
	 */
	public void tick(LocalTime t, LocalDate d, boolean s){
		if(alarms.isEmpty())
			return;
		//print("All alarms added so far: " + showAlarms());
		Alarm a = alarms.element();
		print("Alarm a: " + a.toString());
		int day = getDayCode(d);
		//Only gets the shift for the soonest alarm, should be fine in most cases but would have to work better in production
		if(s && !alarms.isEmpty() && a.AutoAdvance) {
			timeshift = getShift(a);
		}
		if(a != null){
			//Add the time shift
			t = t.plus(timeshift);
			print("# alarms set: " + alarms.size());
			print("Time comparison: " + a.getTime().compareTo(t));
			// Ensure there is at least one alarm
			if(!alarms.isEmpty()){
				// New loop: Iterate through and get all matching alarms, put them in a separate queue
				LinkedList<Alarm> alarmsToTrip = new LinkedList<Alarm>();
				int index = alarms.indexOf(alarms.element());
				print("# alarms set: " + alarms.size());
				// While the time and day are valid
				while(index < alarms.size() && alarms.get(index).getTime().compareTo(t) <= 0){
					print("# alarms set: " + alarms.size());
					System.out.print("Index"+index);
					// If it repeats and we aren't on the correct day then don't trip
					if((alarms.get(index).Repeat && !alarms.get(index).days[day])){
						print("WRONG DAY. Today is: " + day);
						//do nothing
					}
					// Otherwise, trip
					else{
						print("adding alarm to triplist, " + alarmsToTrip.size() + " total alarms added so far.");
						alarmsToTrip.add(alarms.get(index));
					}
					index++;
				}
				// After new queue is full, trip each and re-add if neccessary 
				for(Alarm alarm : alarmsToTrip){
					print("# alarms set: " + alarms.size());
					print("Tripping alarm " + alarm.label);
					alarms.removeIf(sameAlarm(alarm));
					tripAlarm(alarm);
					if(alarm.Repeat){
						this.setAlarm(alarm);
					}
				}
				print("# alarms set: " + alarms.size());

			}
		else {
			print("Null alarm somehow");
			System.exit(1);
			}
		}
		}
	}
	

	/**
	 * Set alarm
	 * NOTE: No way to add alarms with origin/destination from
	 * inside the clock class unless they are pulled from server
	 * 
	 */
	public void setAlarm(LocalTime t, Integer k, boolean[] days) {
		Alarm a = new Alarm(t, k, days);
		alarms.add(a);
		//Sort the alarms
		Collections.sort(alarms);
	}
	
	/**
	 * Set an autoadvance alarm
	 * @param t
	 * @param r
	 * @param k
	 * @param days
	 * @param o Origin Address 
	 * @param d Destination address
	 * @param s The label of the string
	 */
	public void setAlarm(LocalTime t, Integer k, boolean[] days, String o, String d, String s) {
		Alarm a = new Alarm(t, k, days, o, d, s);
		for(int i = 0; i < days.length; i++) {
			if(days[i]) {
				alarms.add(a);
			}
		}
		//Sort the alarms
		Collections.sort(alarms);
	}
	
	/**
	 * Set alarm from JSON
	 * TODO: Finish this
	 * @param j JSON encoded alarm
	 */
	public void setAlarm(JSONObject j, int p) {
		alarms.add(Alarm.fromJSON(j, p));
		Collections.sort(alarms);
	}
	
	
	/**
	 * Set an alarm that already exists
	 * @param a
	 */
	public void setAlarm(Alarm a){
		alarms.add(a);
		Collections.sort(alarms);
	}
	
	/**
	 * Unset alarm with the primary key k
	 * 
	 * Because repeating alarms can only come from the server, they will always have 
	 * a primary key, which makes removing alarms with null keys pointless as
	 * they will be removed automatically. This may change when alarms sync upstream
	 * to the server. 
	 * @param k Key of the alarm to be deleted
	 */
	public void unSetAlarm(int k){
		for(Alarm a : alarms){
			// Assume you never get a local alarm
			if(a.primarykey != null && a.primarykey == k){
				alarms.remove(a);
				keys.removeIf(sameKey(k));
			}
		}
		Collections.sort(alarms);
	}
	
	/**
	 * Synchronises the alarm list with that of the server
	 */
	public void sync() {
		if(DEBUG)
			print("in Sync()");
		try {
			//Get all alarms
			String url = HOSTNAME + "/alarms/get/" + clockID;
			if(DEBUG)
				print(url);
			BufferedReader reader = getReader(url, "GET");
			String data = getData(reader);
			// Fix data
			data = data.substring(1, data.length() - 1);
			data = data.replaceAll("\\\\", "");
			JSONArray array = new JSONArray(data);
			LinkedList<Integer> checked = new LinkedList<Integer>();
			// For each alarm in the array, add it to the queue
			if(DEBUG)
				print("Array length in sync(): " + array.length());
			for(int i = 0; i < array.length(); i++){
				// Call Alarm.getFromJSON with ("fields") and ("pk")
				JSONObject base = new JSONObject(array.get(i).toString());
				// Get the primary key
				Integer pk = base.getInt("pk");
				checked.add(new Integer(pk));
				// Don't add alarms more than once
				if(keys.contains(pk)){
					if(DEBUG)
						print("Redundant alarm");
					continue;
				}
				else {
					if(DEBUG)
						print("Adding alarm from server");
					//Add the alarm to the keys list
					keys.add(pk);
					// Get the alarm fields 
					JSONObject fields = new JSONObject(base.get("fields").toString());
					if(DEBUG)
						print("Fields of alarm pulled from server: " + fields);
					this.setAlarm(fields, pk);
				}
				// Search the keys for any unset alarms
				for(Integer k : keys){
					if(!checked.contains(k)){
						if(DEBUG)
							print("Removing dead alarm");
						unSetAlarm(k);
					}						
				}
			}
			// TODO: Make POST request using <host>/alarms/sync/<clockID>, send alarms
		}
		catch(JSONException e) {
			e.printStackTrace();
			return;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		} catch (ProtocolException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
	}
	

	private void tripAlarm(Alarm a){
		//Code to actually make alarm sound etc goes here
		//Placeholder to show that an alarm has been tripped
		System.out.println("Alarm set for " + a.getTime().toString() + " has been tripped!");
	}
				
	
	private boolean wasTripped(Alarm a) {
		return alarmsTripped.contains(a.primarykey);
	}
	
	/**
	 * Uses the APIs to find the additional time needed for an alarm
	 * TODO: Finish this and the user data method
	 * @return
	 */	
	private static Duration getShift(Alarm a){
		if(DEBUG)
			print("In getShift");
		//Base duration
		Duration d = Duration.ZERO;
		//If the alarm does not auto advance, return zero seconds
		//getShift is off for now
		if(!a.AutoAdvance)
			return d;
		URL url;
		//In production only one APIkey per API, can be defined here
		String weatherAPIKey = "82fb18f2447c8171ee812653fb3be5ce"; 
		String googleAPIKey = "AIzaSyC9AjnyGBzJevIwzwof50tVznn7gt-4sHk";
		//The units to be returned
		String units = "imperial";
		// Whether to make a pessimistic Google Maps request or not
		boolean pessimism = false;
		String home = a.getOrigin();
		String destination = a.getDestination();
		String rootWURL = "http://api.openweathermap.org/data/2.5/weather?";
		String rootGURL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
		try {
			/* --- Make OpenWeather request --- */
			// TODO: A lot. Finish getShift method
			// Parse address and extract zipcode
			String origin = a.getOrigin();
			String zipcode = origin.substring(origin.length() - 5, origin.length());
			if(DEBUG)
				print(zipcode);
			try{
			Integer.parseInt(zipcode);
			}
			catch(NumberFormatException e){
				if(DEBUG)
					print("Invalid Zipcode in origin address for alarm " + a.label);
				return Duration.ZERO;
			}
			// If the zipcode can be parsed, add it to the URL
			String rawurl = rootWURL + "zip=" + zipcode;
			// Add the API key
			rawurl += "&APPID=" + weatherAPIKey;
			// Add the units parameter
			rawurl += "&units=" + units;
			// Make request to weather API 
			BufferedReader read = getReader(rawurl, "GET");
			//Get all data
			String data = getData(read);
			print(data);
			// Get initial response
			JSONObject response = new JSONObject(data);
			// Get the weather data field
			JSONArray array = response.getJSONArray("weather");
			JSONObject weather = array.getJSONObject(0);
			// If the weather is anything other than "clear," make google use a pessimistic estimate
			if(!weather.get("main").toString().equals("Clear"))
				pessimism = true;
			// TODO: Add more advanced weather data interpretation and duration extension
			/* --- Make Google request --- */
			// Replace address spaces with plusses
			home = home.replaceAll(" ", "+");
			destination = destination.replaceAll(" ", "+");
			// Make request to Google API
			rawurl = rootGURL += home + "&destinations=" + destination;		
			rawurl += "&departure_time=" + Long.toString(System.currentTimeMillis()); 
			if(pessimism)
				rawurl += "&traffic_model=pessimistic"; 
			rawurl += "&units=" + units;
			rawurl += "&key=" + googleAPIKey;
			read = getReader(rawurl, "GET");
			// Parse into JSON
			JSONObject result = new JSONObject(getData(read));
			JSONArray rows = result.getJSONArray("rows");
			JSONObject first = rows.getJSONObject(0);
			JSONArray element = first.getJSONArray("elements");
			JSONObject finaldata = element.getJSONObject(0);
			JSONObject base = finaldata.getJSONObject("duration");
			JSONObject traffic = finaldata.getJSONObject("duration_in_traffic");
			// Get the base time, the time with traffic, and find the difference
			Duration baseTime = Duration.ZERO.plusSeconds(base.getLong("value"));
			Duration trafficTime = Duration.ZERO.plusSeconds(traffic.getLong("value"));
			Duration delta = trafficTime.minus(baseTime);
			d = d.plus(delta);	
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;
	}
	
	
	private static BufferedReader getReader(String url, String m) throws IOException {
		URL u = new URL(url);
		if(DEBUG)
			print("Getting a a reader with URL = " + url);
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod(m);
		//Put the request data into a BufferedReader
		BufferedReader read = 
				new BufferedReader(
						new InputStreamReader(con.getInputStream()));
		return read;	
	}
	
	
	private static BufferedWriter getWriter(String url, String m) throws IOException{
		URL u = new URL(url);
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod(m);
		//Get the writer
		BufferedWriter write = 
				new BufferedWriter(
						new OutputStreamWriter(
								con.getOutputStream()));
		return write;
	}
	private static String getData(BufferedReader r) throws IOException{
		StringBuilder s = new StringBuilder();
		int i;
		while((i = r.read()) != -1){
			s.append((char) i);
		}
		return s.toString();
	}

	
	//Get day of week shifted to 0-6
	private static int getDayCode(LocalDate d) {
		return d.getDayOfWeek().getValue() - 1;
	}
	
	//Helper to make printing easier
	private static void print(Object s) {
		System.out.println(s);
	}
	
	/* Predicates */	
	private static Predicate<Integer> sameKey(int k){
		return p -> p.intValue() == k;
	}
	private Predicate<Alarm> sameAlarm(Alarm a) {
		return p -> (p.primarykey == a.primarykey && !(p.primarykey == null) && p.primarykey != null) || Math.abs(p.time.toNanoOfDay() - a.time.toNanoOfDay()) <= 500;
	}
	
	// Used for debugging only
	private String showAlarms(){
		String base = "\n";
		String sep = "\t----";
		for(Alarm a : this.alarms){
			base += a.toString() + "\n";
		}
		return base;
	}
}
