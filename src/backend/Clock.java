package backend;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
/**
 * 
 * @author Kurt Floyd
 *
 */
public class Clock {
	//A queue of all alarms.
	private LinkedList<Alarm> alarms;
	private Lcd2UsbClient out;
	
	Clock(){
		try {
			out = new Lcd2UsbClient();
		} catch (IOException e) {
			System.err.println("GUI connection failed");
		}
		
		alarms = new LinkedList<Alarm>();
	}
	
	/**
	 * Checks the given date and time to see if an alarm time has been reached. Only the soonest alarm is checked.
	 */
	public void tick(LocalTime t, LocalDate d){
		//Change the time based on current weather/traffic conditions
		//Only the "Soonest" alarm is checked for traffic conditions.
		Duration traffic = getTrafficShift(alarms.peek());
		Duration weather = getWeatherShift(alarms.peek());
		
		//If there are no alarms set to go off today, don't bother checking. 
		if(alarms.peek() != null){
		if(traffic != null)
			t.plus(traffic);
		if(weather != null)
			t.plus(weather);
		
		//If the shifted time either passed by or is at the soonest alarm time(s), trip it/them.
		while(!alarms.isEmpty() && alarms.getFirst().getTime().compareTo(t) <= 0 && alarms.getFirst().getDay().equals(d)){
			Alarm alarm = alarms.removeFirst();
			this.tripAlarm(alarm);
			System.out.println(alarms.size());
			//If the alarm is set to repeat, re-add it but set for tomorrow. 
			if(alarm.doesRepeat()){
				this.setAlarm(alarm.getTime(), alarm.getDay().plusDays(1), true, alarm.getStart(), alarm.getEnd());
				System.out.println("Alarm re-added");
				}
			}
		}
	}
	
	/**
	 * Set alarm with the default tone
	 * @param t the time and date the alarm should go off
	 * @param d the date the alarm is set for
	 * @param r whether the alarm repeats or not
	 * @param h Home address
	 * @param e Destination address
	 * 
	 */
	public void setAlarm(LocalTime t, LocalDate d, boolean r, String h, String e) {
		Alarm a = new Alarm(t, d, r, h, e);
		alarms.add(a);
		//Sort the alarms
		Collections.sort(alarms);
	}
	
	/** 
	 * Set alarm with a custom tone
	 * @param t the time and date the alarm should go off
	 * @param s placeholder variable for setting a different alarm tone
	 * @param d the date the alarm is set for
	 * @param r whether the alarm repeats or not
	 * @param h Home address
	 * @param e Destination address
	 * 
	 */
	public void setAlarm(LocalTime t, LocalDate d, boolean r, int s, String h, String e) {
		Alarm a = new Alarm(t, d, r, h, e);
		alarms.add(a);
		Collections.sort(alarms);
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
	}
	

	
	private void tripAlarm(Alarm a){
		//Code to actually make alarm sound etc goes here
		//Placeholder to show that an alarm has been tripped 
		System.out.println("Alarm set for " + a.getTime().toString() + " has been tripped!");
	}
	
	/**
	 * Uses the google API to find the additional time needed to 
	 * @return
	 */
	private Duration getTrafficShift(Alarm a){
		String APIKey = "";
		return null;
	}
	
	private Duration getWeatherShift(Alarm a){
		String APIKey = "";
		String start = a.getStart();
		String end = a.getEnd();
		return null;
	}
	
	//A way to add extra time to whatever time google says it'll take to get there.
	//Unimplemented atm, may not be useful
	private LocalTime getTimeTransform(LocalTime t){
		return null;
	}
	
	
	/**
	 * 
	 * @author Kurt Floyd
	 * Uses the comparable interface to sort based on time. 
	 *
	 */
	private class Alarm implements Comparable<Alarm> {
		private final LocalTime time; //The time the alarm goes off
		private final boolean repeat; //Whether the alarm repeats
		private final LocalDate day; //The date the alarm is supposed to trigger
		private final String start; //The start location of the route associated with each alarm
		private final String end; //The end location of the route assocaited with each alarm
		//Private final int tone; Dummy variable, different tones to be implemented
		
		//Constructor
		Alarm(LocalTime t, LocalDate d, boolean r, String s, String e){
			time = t;
			repeat = r;
			day = d;
			start = s;
			end = e;
		}
				
		public String getEnd() {
			return end;
		}

		public String getStart() {
			return start;
		}

		public int getTone() {
			return 0; //Does nothing, to be implemented
		}

		public boolean doesRepeat() {
			return repeat;
		}
		
		public LocalTime getTime() {
			return this.time;
		}
		
		public LocalDate getDay(){
			return this.day;
		}

		@Override
		public int compareTo(Alarm o) {
			int dateDif = this.getDay().compareTo(o.getDay());
			if(dateDif == 0)
				return this.getTime().compareTo(o.getTime());
			else 
				return dateDif;
		}
		
	}

}
