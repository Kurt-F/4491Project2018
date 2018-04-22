package backend;

import com.pi4j.io.gpio.GpioPinDigitalInput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
//Enables playing ogg audio files by using the linux command line and ogg123
public class AlarmPlayer {

    public static boolean loopAlarm(GpioPinDigitalInput[] controlPanel, String[] alarmFiles){
        boolean snooze = true;

        Random rand = new Random();
        int fileIndex = rand.nextInt(alarmFiles.length - 1);
        String filePath = "/home/pi/alarms/" + alarmFiles[fileIndex]; //file path specific to default raspbian config.

        ArrayList<String> argumentList = new ArrayList<>();

        //Build argument list for ProcessBuilder
        argumentList.add("ogg123");
        argumentList.add("-r");
        argumentList.add(filePath);

        ProcessBuilder playbackProcessBuilder = new ProcessBuilder(argumentList);
        Process playbackProcess;
        try {
            playbackProcess = playbackProcessBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
            return snooze;              //makes the alarm attempt to run again if playback fails.
        }

        boolean running = true;
        while (running){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(controlPanel[0].isLow()){        //dismiss
                playbackProcess.destroy();
                running = false;
                snooze = false;
                while(controlPanel[0].isLow()){
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(controlPanel[1].isLow()){   //snooze
                playbackProcess.destroy();
                running = false;
                snooze = true;
                while(controlPanel[0].isLow()){
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return snooze;

    }

    public static boolean loopAlarm(GpioPinDigitalInput[] controlPanel) {
        boolean snooze = true;
        String alarmFiles[] = {"Alarm_Beep_01.ogg", "Alarm_Beep_02.ogg",
                "Alarm_Beep_03.ogg", "Alarm_Buzzer.ogg", "Alarm_Classic.ogg"};

        Random rand = new Random();
        int fileIndex = rand.nextInt(4);
        String filePath = "/home/pi/alarms/" + alarmFiles[fileIndex];

        ArrayList<String> argumentList = new ArrayList<>();

        //Build argument list for ProcessBuilder
        argumentList.add("ogg123");
        argumentList.add("-r");
        argumentList.add(filePath);


        ProcessBuilder playbackProcessBuilder = new ProcessBuilder(argumentList);
        Process playbackProcess;
        try {
            playbackProcess = playbackProcessBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
            return snooze;              //makes the alarm attempt to run again if playback fails.
        }

        boolean running = true;
        while (running){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(controlPanel[0].isLow()){        //dismiss
                playbackProcess.destroy();
                running = false;
                snooze = false;
                while(controlPanel[0].isLow()){
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(controlPanel[1].isLow()){   //snooze
                playbackProcess.destroy();
                running = false;
                snooze = true;
                while(controlPanel[0].isLow()){
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return snooze;

    }
}
