package linuxTimeControl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class linuxTimeControl {
    public static void setTime(int hr, int min) throws IOException //input 24hr time
    {
        ArrayList<String> argumentList = new ArrayList<>();

        //ensure single digits are 0 padded
        String hrFormat = String.format("%02d", hr);
        String minFormat = String.format("%02d", min);
        String newTime = hrFormat + ":" + minFormat;

        //Build argument list for ProcessBuilder
        argumentList.add("sudo"); //requires the NOPASSWD option for /bin/date in sudoer's file
        argumentList.add("-n");
        argumentList.add("/bin/date");
        argumentList.add("+%H:%M");
        argumentList.add("-s");
        argumentList.add(newTime);

        ProcessBuilder timedatectlProcess = new ProcessBuilder(argumentList);
        timedatectlProcess.start();
    }

    public static void setDate(int year, int month, int day) throws IOException {
        ArrayList<String> argumentList = new ArrayList<>();

        //date command sets time to zero when changing date,
        //so setting the current time when setting the date is required
        String currentHr = new SimpleDateFormat("HH").format(new Date());
        String currentMin = new SimpleDateFormat("mm").format(new Date());

        String monthFormat = String.format("%02d", month);
        String dayFormat = String.format("%02d", day);
        String newDate = year + monthFormat + dayFormat;

        //Build argument list for ProcessBuilder
        argumentList.add("sudo"); //requires the NOPASSWD option for /bin/date in sudoer's file
        argumentList.add("-n");
        argumentList.add("/bin/date");
        argumentList.add("+%Y%m%d");
        argumentList.add("-s");
        argumentList.add(newDate);

        ProcessBuilder dateProcess = new ProcessBuilder(argumentList);
        dateProcess.start();

        //Setting time back to current hour and minute
        setTime(Integer.parseInt(currentHr), Integer.parseInt(currentMin));

    }

}