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
		//Main loop
		do {
			LocalTime today = LocalTime.now();
			System.out.println(today.toString());
			
			//Get input (running should be set to true or false here)
			//Resolve Input
			//Check to see if an alarm would usually be set 
			//Check alarms
		} while (running);


	}
	
	

}
