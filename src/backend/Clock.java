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
	//A unique identifier for this particular clock. Randomized in production.
	private static final int clockID = 0;
	//Debug flag
	private static final boolean DEBUG = true;
	// The cloud server name
	//private static final String HOSTNAME = "http://www.smart-clock.xyz.com";
	// Temporary hostname for the development server
	private static final String HOSTNAME = "http://192.168.1.69";
	//An array of Lists of alarms
	private LinkedList<Alarm> alarms;
	// The current time shift
	private Duration timeshift;
	
	
	public Clock(){
		alarms = new LinkedList<Alarm>();
		timeshift = Duration.ZERO;
	}
	
	/**
	 * Checks the given date and time to see if an alarm time has been reached. Only the soonest alarm is checked.
	 */
	public void tick(LocalTime t, LocalDate d, boolean s){
		Alarm a = alarms.element();
		int day = getDayCode(d);
		if(s) {
			timeshift = getShift(a);
		}
		//Get the current day 
		if(a != null){
		//Add the time shift
		t = t.plus(timeshift);
		//If the shifted time either passed by or is at the soonest alarm time(s), trip it/them.
		while(!alarms.isEmpty() && a.getTime().compareTo(t) <= 0 && a.days[day]){
			if(a.Repeat){
				if(a.AutoAdvance)
					this.setAlarm(a.getTime(), a.isRepeat(), a.primarykey, a.days);
				else
					this.setAlarm(
							a.getTime(), 
							a.isRepeat(), 
							a.primarykey, 
							a.days,
							a.getOrigin(), 
							a.getDestination());
				}
			Alarm alarm = alarms.removeFirst();
			this.tripAlarm(alarm);
			System.out.println(alarms.size());
			//If the alarm is set to repeat, handle that
			}
		}
	}
	

	
	/**
	 * Set alarm
	 * NOTE: No way to add alarms with origin/destination from
	 * inside the clock class unless they are pulled from server
	 * 
	 */
	public void setAlarm(LocalTime t, boolean r, Integer k, boolean[] days) {
		Alarm a = new Alarm(t, r, k, days);
		alarms.add(a);
		this.sync();
		//Sort the alarms
		for(int i = 0; i < alarms.size(); i++) 
			Collections.sort(alarms);
	}
	
	/**
	 * Set an autoadvance alarm
	 * @param t
	 * @param r
	 * @param k
	 * @param days
	 * @param o
	 * @param d
	 */
	public void setAlarm(LocalTime t, boolean r, Integer k, boolean[] days, String o, String d) {
		Alarm a = new Alarm(t, r, k, days, o, d);
		for(int i = 0; i < days.length; i++) {
			if(days[i]) {
				alarms.add(a);
			}
		}
		this.sync();
		//Sort the alarms
		for(int i = 0; i < alarms.size(); i++) 
			Collections.sort(alarms);
	}
	
	/**
	 * Set alarm from JSON
	 * TODO: Finish this
	 * @param j JSON encoded alarm
	 */
	public void setAlarm(JSONObject j) {
		
	}
	
	/**
	 * Remove an alarm from the alarm list
	 * @param t All alarms that go off at this time will be removed
	 * TODO: Add day checking too
	 * TODO: Implement more efficient search
	 */
	public void unSetAlarm(LocalTime t) {
		for(int i = 0; i < alarms.size();i++){
			if(alarms.get(i).getTime().equals(t)){
				alarms.remove(alarms.get(i));
				//Decrement as the new ith alarm has not been checked
				i--;
			}
		}
		this.sync();
	}
	
	private void tripAlarm(Alarm a){
		//Code to actually make alarm sound etc goes here
		//Placeholder to show that an alarm has been tripped
		System.out.println("Alarm set for " + a.getTime().toString() + " has been tripped!");
	}
		
	/**
	 * Fetch the alarms from the cloud
	 */
	private void getAlarms() {
		try {
			// Use <host>/alarms/get/<clockid> to make GET request
			String url = HOSTNAME + "alarms/get/" + clockID;
			if(DEBUG)
				print("url");
			BufferedReader read = getReader(url, "GET");
			String result = "";
			//Read all lines
			while(read.ready()) {
				result += read.readLine();
			}
			//Attempt to parse as JSON
			JSONArray newAlarms = new JSONObject(result).getJSONArray("alarms");
			//Load each Alarm into queue 
			for(Object j : newAlarms) {
				this.setAlarm((JSONObject) j);
			}
		}
		catch(JSONException e) {
			e.printStackTrace();
			return;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Synchronises the alarm list with that of the server
	 */
	private void sync() {
		try {
			String url = HOSTNAME + "/alarms/set/" + clockID;
			BufferedReader reader = getReader(url, "PUT");
			
			// Encode list of alarms into JSONObject
			// Make POST request using <host>/alarms/synch/<clockID>, send alarms
		}
		catch(JSONException e) {
			//This should never happen
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
	
	/**
	 * Uses the APIs to find the additional time needed for an alarm
	 * TODO: Finish this and the user data method
	 * @return
	 */	
	private static Duration getShift(Alarm a){
		//Base duration
		Duration d = Duration.ZERO;
		//If the alarm does not auto advance, return zero seconds
		if(a.AutoAdvance)
			return d;
		URL url;
		//In production only one APIkey per API, can be defined here
		String weatherAPIKey = "82fb18f2447c8171ee812653fb3be5ce"; 
		String googleAPIKey = "";
		String home = a.getOrigin();
		String destination = a.getDestination();
		String rootWURL = "http://api.openweathermap.org/data/2.5/weather?q=";
		String rootGURL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
		try {
			/* --- Make OpenWeather request --- */
			// TODO: A lot. Finish getShift method
			// Parse address and extract zipcode
			String origin = a.getOrigin();
			String zipcode = origin.substring(origin.length() - 6, origin.length());
			// Make request to weather API 
			String rawurl = rootWURL += zipcode;
			BufferedReader read = getReader(rawurl, "GET");
			// Parse response, add time
			// Determine google parameters
			/* --- Make Google request --- */
			// Replace address spaces with plusses
			home.replaceAll(" ", "+");
			destination.replaceAll(" ", "+");
			// Make request to Google API
			rawurl = rootGURL += home + "&destinations=" + destination;		
			rawurl += "&departure_time=" + Long.toString(System.currentTimeMillis()); 
			rawurl += "&traffic_model=" + ""; //TODO: Change model based on weather
			rawurl += "&key=" + googleAPIKey;
			read = getReader(rawurl, "GET");
			
			// Parse response, add time
			
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
	
	//Get day of week shifted to 0-6
	private static int getDayCode(LocalDate d) {
		return d.getDayOfWeek().getValue() - 1;
	}
	
	//Helper to make printing easier
	private static void print(String s) {
		System.out.println(s);
	}

}
