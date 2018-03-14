package backend;

import com.pi4j.io.gpio.GpioPinDigitalInput;

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
	private final Connection out;
	private GpioPinDigitalInput controlPanel[];
	
	public Clock(GpioPinDigitalInput controlPanel[]){
		out = new Connection();
		alarms = new LinkedList<Alarm>();
		this.controlPanel = controlPanel;
	}
	
	/**
	 * Checks the given date and time to see if an alarm time has been reached. Only the soonest alarm is checked.
	 */
	public void tick(LocalTime t, LocalDate d){
		//Change the time based on current weather/traffic conditions
		Duration traffic = getTrafficShift();
		Duration weather = getWeatherShift();
		//If there are no alarms set to go off today, don't bother checking. 
		if(alarms.peek() != null && alarms.getFirst().getDay().equals(d)){
		if(traffic != null)
			t.plus(traffic);
		if(weather != null)
			t.plus(weather);
		//If the shifted time either passed by or is at the soonest alarm time(s), trip it/them.
		while(!alarms.isEmpty() && alarms.getFirst().getTime().compareTo(t) <= 0){
			Alarm alarm = alarms.removeFirst();
			this.tripAlarm(alarm);
			//If the alarm is set to repeat, re-add it but set for tomorrow. 
			if(alarm.doesRepeat())
				this.setAlarm(alarm.getTime(), alarm.getDay().plusDays(1), true);
			}
		}
	}
	
	/**
	 * Set alarm with the default tone
	 * @param t the time and date the alarm should go off
	 * @param d the date the alarm is set for
	 * @param r whether the alarm repeats or not
	 */
	public void setAlarm(LocalTime t, LocalDate d, boolean r) {
		Alarm a = new Alarm(t, d, r);
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
	 * 
	 */
	public void setAlarm(LocalTime t, LocalDate d, boolean r, int s) {
		Alarm a = new Alarm(t, d, r);
		alarms.add(a);
	}
	
	public void unSetAlarm(LocalTime t) {
		
	}
	

	
	private void tripAlarm(Alarm a){
		//Code to actually make alarm sound etc goes here
		//Placeholder to show that an alarm has been tripped
		AlarmPlayer.loopAlarm(controlPanel);
		System.out.println("Alarm set for " + a.getTime().toString() + " has been tripped!");
	}
	
	private LocalTime getTime(){
		//Dummy value since I'm not sure how it works on the RPI
		return LocalTime.MAX;
	}
	
	private Duration getTrafficShift(){
		return null;
	}
	
	private Duration getWeatherShift(){
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
		private final LocalDate day;
		//Private final int tone; Dummy variable, different tones to be implemented
		
		//Constructor
		Alarm(LocalTime t, LocalDate d, boolean r){
			time = t;
			repeat = r;
			day = d;
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
