package backend;

import java.time.LocalDate;
import java.time.LocalTime;

public class MainClockProcess {
	
	/**
	 * Notes:
	 * Sleep every minute?
	 * Uses RPI system time
	 * @param args
	 */
	
	//Placeholder for variable that will determine if the clock is on
	static boolean running;

	public static void main(String[] args) {
		Clock c = new Clock();
		c.setAlarm(LocalTime.now().plusSeconds(30), LocalDate.now(), true);
		c.setAlarm(LocalTime.now().plusSeconds(3), LocalDate.now(), true);

		//Main loop
		// Ideally, there'd be a check to see when the next alarm was and sleep until then or until an input interrupt woke it, but this works
		do {
			LocalTime instant = LocalTime.now();
			LocalDate today = LocalDate.now();
			//Get input (running should be set to true or false here)
			running = true;
			//Resolve Input
			checkInput();
			//Check to see if an alarm would usually be set 
			c.tick(instant, today);
			//Check alarms
			
		} while (running);


	}
	
	//Check physical input as well as APIs. Dummy function for now.
	private static void checkInput(){
		
	}
	
	//Dummy function, needs to 
	private void tripAlarm(){
		
	}

}
