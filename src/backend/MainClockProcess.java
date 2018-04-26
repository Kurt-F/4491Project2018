package backend;

import com.pi4j.io.gpio.*;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class MainClockProcess {
	
	static boolean running;	//Placeholder for variable that will determine if the clock is on
	static boolean debug; //Variable to control whether debug information is sent to the console or not

	public static void main(String[] args) {
    
		//Display default display
		Lcd2UsbClient lcd;
		try {
			lcd = new Lcd2UsbClient();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		lcd.setText(0,"%I:%M%p");
		lcd.setText(1,"");
		lcd.setText(2, "%x");
		lcd.setText(3," ");

		//Create GPIO controller and buttons
		/*
		 * This creates an array containing the buttons on the physical control panel.
		 * Currently it must be passed though to any object that requires input.
		 * Object "lcd" above must be treated in a similar manner at this time.
		 */
		final GpioController gpio = GpioFactory.getInstance();
		//Buttons physically connect their GPIO pin to ground when pressed, so pull-up resistor is used here.
		GpioPinDigitalInput menuButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01,
				"Menu", PinPullResistance.PULL_UP);
		GpioPinDigitalInput selectButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04,
				"Menu", PinPullResistance.PULL_UP);
		GpioPinDigitalInput downButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05,
				"Menu", PinPullResistance.PULL_UP);
		GpioPinDigitalInput upButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06,
				"Menu", PinPullResistance.PULL_UP);
		
		//Create ArrayList for buttons to pass for settings menu functions.
		GpioPinDigitalInput controlPanel[] = {menuButton, selectButton, downButton, upButton};

		Clock c = new Clock(controlPanel);
		//c.setAlarm(LocalTime.now().plusSeconds(30), LocalDate.now(), true);
		//c.setAlarm(LocalTime.now().plusSeconds(3), LocalDate.now(), false);
		// Ideally, there'd be a check to see when the next alarm was and sleep until then or until an input interrupt woke it, but this works
		//JSON test
		
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LocalTime instant = LocalTime.now();
			LocalDate today = LocalDate.now();
			//Get input (running should be set to true or false here)
			running = true;
			//Resolve Input
			checkInput(lcd, controlPanel, c);
			//Check to see if an alarm would usually be set 
			c.tick(instant, today);
			//Check alarms
			
		} while (running);


	}
	
	//Check physical input as well as APIs. Dummy function for now.
	private static void checkInput(Lcd2UsbClient display, GpioPinDigitalInput[] buttons, Clock clock){
		if(buttons[0].isLow()){//pressing the physical button connects the gpio pin to ground.
			Settings settingsMenu = new Settings(display, buttons, clock);
			try {
				settingsMenu.start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
