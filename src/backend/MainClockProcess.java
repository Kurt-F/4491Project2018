package backend;

import java.time.LocalDate;
import java.time.LocalTime;

public class MainClockProcess {
	
	static boolean running;	//Placeholder for variable that will determine if the clock is on
	static boolean debug; //Variable to control whether debug information is sent to the console or not

	public static void main(String[] args) {
		Clock c = new Clock();
		c.setAlarm(LocalTime.now().plusSeconds(5), LocalDate.now(), true, "", "");
		c.setAlarm(LocalTime.now().plusSeconds(3), LocalDate.now(), true, "", "");

		//Main loop
		// Ideally, there'd be a check to see when the next alarm was and sleep until then or until an input interrupt woke it, but this works

		do {
			LocalTime instant = LocalTime.now();
			LocalDate today = LocalDate.now();
			//Get input (running should be set to true or false here)
			running = true;
			//Resolve Input
			checkInput();
			//Check alarms etc
			c.tick(instant, today);
			
		} while (running);


	}
	
	//Check physical input as well as APIs. Dummy function for now.
	private static void checkInput(){
		
	}
	
	/**
	 * Check GPIO devicess
	 */
	private static void getIO(){
		
	}
}
