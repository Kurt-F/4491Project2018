package backend;

import java.time.LocalTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Clock {
	
	private class Alarm{
		private LocalTime t;
		
		
	}
	
	private BlockingQueue<Alarm> alarms;
	
	
	
	Clock(){
		alarms = new LinkedBlockingQueue<Alarm>();
	}
	
	public void setAlarm(LocalTime t) {
		
	}
	
	public void unSetAlarm(LocalTime t) {
		
	}
	
	public void killAlarm(int i){
		
	}
	
	private LocalTime getTime(){
		//Dummy variabe since I'm not sure how it works on the RPI
		return LocalTime.MAX;
	}

}
