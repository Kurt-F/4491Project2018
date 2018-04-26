package backend;

import java.time.LocalDate;
import java.time.LocalTime;

public class TestMain {
private static final long SYNC_TIME = 30000;
private static final long API_TIME = 60000;
private static final long ERROR = 500;

	public static void main(String[] args) throws InterruptedException {
		Clock clock = new Clock();
		boolean[] days = {false, false, false, false, false ,false, false};
		clock.setAlarm(LocalTime.now().plusSeconds(5), null, days);
		clock.setAlarm(LocalTime.now().plusSeconds(7), null, days);
		clock.setAlarm(LocalTime.now().plusSeconds(8), null, days);
		clock.setAlarm(LocalTime.now().plusSeconds(2), null, days);

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
			clock.tick(LocalTime.now(), LocalDate.now(), api);			
		}
	}

}
