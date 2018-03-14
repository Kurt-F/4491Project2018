package backend;

import com.pi4j.io.gpio.GpioPinDigitalInput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AlarmPlayer {

    public static void loopAlarm(GpioPinDigitalInput[] controlPanel){
        String fileList[] = {"Alarm_Beep_01.ogg", "Alarm_Beep_02.ogg",
                "Alarm_Beep_03.ogg", "Alarm_Buzzer.ogg", "Alarm_Classic.ogg"};
        Random rand = new Random();
        int fileIndex = rand.nextInt(4);
        String filePath = "/home/pi/alarms/" + fileList[fileIndex];

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
            return;
        }

        boolean running = true;
        while (running){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(controlPanel[0].isLow()){
                playbackProcess.destroy();
                running = false;
                while(controlPanel[3].isLow()){
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

}
