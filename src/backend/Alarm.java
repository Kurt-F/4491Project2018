package backend;

import com.pi4j.io.gpio.GpioPinDigitalInput;

import java.time.LocalTime;

public class Alarm implements Comparable<Alarm>{
    /*This is a new implementation of Alarm for consideration.
    The main highlight is the repeatDays variable as a different way of dealing with repeating alarms.
     */
    private LocalTime time;
    private LocalTime alertTime;

    private boolean repeat;
    private boolean[] repeatDays; //Days of the week the alarm should run. Each day is represented by a boolean with Sunday
                          //at index 0 and Saturday at index 6. (boolean array of size 7)
    private boolean autoAdvance;  //Enables or disables early wakeup based on traffic/weather.
    private String origin;
    private String destination;

    private boolean randomAudio;
    private String alarmAudio;


    public Alarm(LocalTime time, boolean repeat, boolean[] repeatDays) {
        this.time = time;
        this.repeat = repeat;
        this.repeatDays = repeatDays;
        this.autoAdvance = false;
        this.alertTime = time;
        randomAudio = true;
    }

    public Alarm(LocalTime time, boolean repeat, boolean[] repeatDays, String origin, String destination) {
        this.time = time;
        this.repeat = repeat;
        this.repeatDays = repeatDays;
        this.autoAdvance = true;
        this.origin = origin;
        this.destination = destination;
        randomAudio = true;
    }

    public void tripAlarm(GpioPinDigitalInput[] controlPanel){
        String fileList[];
        boolean snooze;

        if (!randomAudio){
            fileList = new String[]{alarmAudio};
        }
        else {
            fileList = new String[]{"Alarm_Beep_01.ogg", "Alarm_Beep_02.ogg",
                    "Alarm_Beep_03.ogg", "Alarm_Buzzer.ogg", "Alarm_Classic.ogg"};
        }
        snooze = AlarmPlayer.loopAlarm(controlPanel, fileList);
    }


    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean[] getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(boolean[] repeatDays) {
        this.repeatDays = repeatDays;
    }

    public boolean isAutoAdvance() {
        return autoAdvance;
    }

    public void setAutoAdvance(boolean autoAdvance) {
        this.autoAdvance = autoAdvance;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isRandomAudio() {
        return randomAudio;
    }

    public void setRandomAudio(boolean randomAudio) {
        this.randomAudio = randomAudio;
    }

    public String getAlarmAudio() {
        return alarmAudio;
    }

    public void setAlarmAudio(String alarmAudio) {
        this.alarmAudio = alarmAudio;
    }

    @Override
    public int compareTo(Alarm alarm) {
        return this.getTime().compareTo(alarm.getTime());
    }
}
