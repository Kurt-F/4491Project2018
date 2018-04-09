package backend;

import java.time.LocalTime;

public class Alarm implements Comparable<Alarm>{
    /*This is a new implementation of Alarm for consideration.
    The main highlight is the repeatDays variable as a different way of dealing with repeating alarms.
     */
    LocalTime time;

    boolean Repeat;
    boolean[] repeatDays; //Days of the week the alarm should run. Each day is represented by a boolean with Sunday
                          //at index 0 and Saturday at index 6. (boolean array of size 7)
    boolean AutoAdvance;  //Enables or disables early wakeup based on traffic/weather.
    String origin;
    String destination;

    boolean randomAudio;
    String alarmAudio;


    public Alarm(LocalTime time, boolean Repeat, boolean[] repeatDays) {
        this.time = time;
        this.Repeat = Repeat;
        this.repeatDays = repeatDays;
        this.AutoAdvance = false;
    }

    public Alarm(LocalTime time, boolean Repeat, boolean[] repeatDays, String origin, String destination) {
        this.time = time;
        this.Repeat = Repeat;
        this.repeatDays = repeatDays;
        this.AutoAdvance = true;
        this.origin = origin;
        this.destination = destination;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public boolean isRepeat() {
        return Repeat;
    }

    public void setRepeat(boolean repeat) {
        this.Repeat = repeat;
    }

    public boolean[] getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(boolean[] repeatDays) {
        this.repeatDays = repeatDays;
    }

    public boolean isAutoAdvance() {
        return AutoAdvance;
    }

    public void setAutoAdvance(boolean autoAdvance) {
        this.AutoAdvance = autoAdvance;
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
