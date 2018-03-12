package backend;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.PriorityQueue;
/**
 * 
 * @author Kurt Floyd
 *
 */
public class Clock {
	
	/**
	 * 
	 * @author Kurt Floyd
	 * Uses a comparator to sort based on time. 
	 *
	 */
	private class Alarm implements Comparator<Alarm> {
		private final LocalTime time; //The time the alarm goes off
		private final boolean repeat; //Whether the alarm repeats
		//private final int tone; Dummy variable, different tones to be implemented
		
		Alarm(LocalTime t, boolean r){
			time = t;
			repeat = r;
		}
		
		public void setTone(){
			//Do nothing, to be implemented
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

		@Override
		public int compare(Alarm a1, Alarm a2) {
			return a1.getTime().compareTo(a2.getTime());
		}
		
	}
	
	//A queue of all alarms.
	private PriorityQueue<Alarm> alarms;
	
	
	
	Clock(){
		alarms = new PriorityQueue<Alarm>();
	}
	
	/**
	 * Set alarm with the default tone
	 * @param t
	 * @param r whether the alarm should repeat or not
	 */
	public void setAlarm(LocalTime t, boolean r) {
		Alarm a = new Alarm(t, r);
		alarms.add(a);
	}
	
	/** 
	 * Set alarm with a custom tone
	 * @param t the time and date the alarm should go off
	 */
	public void unSetAlarm(LocalTime t, boolean r) {
		
	}
	
	public void killAlarm(int i){
		
	}
	
	private LocalTime getTime(){
		//Dummy variabe since I'm not sure how it works on the RPI
		return LocalTime.MAX;
	}

}
