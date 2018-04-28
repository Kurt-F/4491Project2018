package backend;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import com.pi4j.io.gpio.*;

public class TestMain {
private static final long SYNC_TIME = 30000;
private static final long API_TIME = 60000;
private static final long ERROR = 500;

	public static void main(String[] args) throws InterruptedException {
		final GpioController gpio = GpioFactory.getInstance();
		Lcd2UsbClient lcd;
		Clock clock;
		Settings settingsMenu;
		String lightIP; //Possibly accept commandline argument

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
		/* This creates an array containing the buttons on the physical control panel.
		 * Currently it must be passed though to any object that requires input.
		 * Object "lcd" above must be treated in a similar manner at this time.
		 * Buttons physically connect their GPIO pin to ground when pressed, so pull-up resistor is used here.
		 */
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
		clock = new Clock(controlPanel);
        settingsMenu = new Settings(lcd, controlPanel, clock);
		/*
		boolean[] days = {false, false, false, false, false ,false, false};
		clock.setAlarm(LocalTime.now().plusSeconds(5), null, days);
		clock.setAlarm(LocalTime.now().plusSeconds(7), null, days);
		clock.setAlarm(LocalTime.now().plusSeconds(8), null, days);
		clock.setAlarm(LocalTime.now().plusSeconds(2), null, days);
        */

		// Check alarms every second. 
		long total = 0;
		long initial = System.currentTimeMillis();
		while(true){
			total += System.currentTimeMillis() - initial;
			Thread.sleep(1000);
			boolean api = false;
			//If 30 seconds have passed, check the server for new alarms
			if(total >= SYNC_TIME && total % SYNC_TIME < ERROR)
				clock.sync();
			//If five minutes have passed since we checked the APIs, check them
			if(total >= API_TIME && total % API_TIME < ERROR){
				System.out.println("API Access");
				api = true;
				total = 0;
				initial = System.currentTimeMillis();
			}
			//If menu button is pressed, enter settings
			if(controlPanel[0].isLow()){
				try {
					settingsMenu.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			clock.tick(LocalTime.now(), LocalDate.now(), api);			
		}
	}
}
