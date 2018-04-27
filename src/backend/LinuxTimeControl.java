package backend;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LinuxTimeControl {
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
        LocalTime currentTime = LocalTime.now();
        int currentHr = currentTime.getHour();
        int currentMin = currentTime.getMinute();

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
        setTime(currentHr, currentMin);

    }

    public static void setNTP(boolean newStatus) throws IOException {
        ArrayList<String> argumentList = new ArrayList<>();
        //Build argument list for ProcessBuilder
        //argumentList.add("sudo"); //requires the NOPASSWD option in sudoer's file
        //argumentList.add("-n");
        argumentList.add("systemctl");
        if(newStatus){
            argumentList.add("start");
        }
        else{
            argumentList.add("stop");
        }

        argumentList.add("ntp.service");

        ProcessBuilder dateProcess = new ProcessBuilder(argumentList);
        dateProcess.start();
    }

}