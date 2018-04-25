package backend;

import java.time.LocalDate;
import java.time.LocalTime;

public class TestMain {

	public static void main(String[] args) {
		Clock clock = new Clock();
		// Check alarms every second. If ten minutes have passed since we checked the APIs, check them
		while(true){
			boolean api = false;
			if(/*Insert time check here*/false)
				api = true;
			clock.tick(LocalTime.now(), LocalDate.now(), api);
			
		}
	}

}
