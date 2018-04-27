package backend;

import com.pi4j.io.gpio.GpioPinDigitalInput;

import java.time.LocalTime;

import org.json.*;


public class Alarm implements Comparable<Alarm>{
    /*This is a new implementation of Alarm for consideration.
    The main highlight is the repeatDays variable as a different way of dealing with repeating alarms.
    
    Changes 4/21/18:
    	Added label
    	
    Changes 4/24/18:
    	Added to and from JSON methods
    	Removed "repeat" value in constructor, class now infers repeat status from boolean array
    	Other things I probably forgot
     */
	final String label;
    LocalTime time;
    Integer primarykey; // Used for synchronising with the clock
    boolean Repeat;
    boolean[] days;
    boolean AutoAdvance;  //Enables or disables early wakeup based on traffic/weather.
    String origin;
    String destination;
    boolean randomAudio;
    String alarmAudio;


    public Alarm(LocalTime time, Integer k, boolean[] d) {
    	label = "";
    	primarykey = k;
        this.time = time;
        if(d == null){
        	d = new boolean[7];
        }
        else
        	days = d;
        //If the alarm is not set to repeat for any day then it doesn't repeat at all
        for(boolean b : d)
        	if(b)
        		this.Repeat = true;
        this.AutoAdvance = false;
    }

    public Alarm(LocalTime time, Integer k, boolean[] d, String origin, String destination, String s) {
    	label = s;
    	primarykey = k;
        this.time = time;        
        if(d == null)
        	days = new boolean[7];
        else
        	days = d;

        //If the alarm is not set to repeat for any day then it doesn't repeat at all
        for(boolean b : d)
        	if(b)
        		this.Repeat = true;
        this.AutoAdvance = true;
        this.origin = origin;
        this.destination = destination;

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
        for (boolean b : days)
            if(b)
                return false;
        return true;
    }
    /*
    public boolean isAutoAdvance() {
        return autoAdvance;
    }

    public void setAutoAdvance(boolean autoAdvance) {
        this.autoAdvance = autoAdvance;
    }
    */
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
    
    //Convert an alarm object to a JSONObject
    public JSONObject toJSON() {
    	JSONObject j = new JSONObject();
    	j.append("time", this.getTime());
    	j.append("repeat", this.isRepeat());
    	j.append("pk", primarykey);
    	//TODO: Create JSONArray of days or something
    	if(this.AutoAdvance) {
    		j.append("origin", this.getOrigin());
    		j.append("destination", this.getDestination());
    	}
		return j;
    }
    
    //Create an alarm object from a JSONObject, should only be the fields key + pk
    public static Alarm fromJSON(JSONObject j, Integer p) {
    	//Instantiate alarm
    	Alarm a = null;
    	String l = j.getString("label");
   	 	// TODO: Make sure this works with the new 24 hour format 
    	LocalTime t = LocalTime.parse(j.get("time").toString());
    	//Get the days
    	boolean[] days = new boolean[7];
    	days[0] = j.getBoolean("sunRepeat");
    	days[1] = j.getBoolean("monRepeat");
    	days[2] = j.getBoolean("tueRepeat");
    	days[3] = j.getBoolean("wedRepeat");
    	days[4] = j.getBoolean("thuRepeat");
    	days[5] = j.getBoolean("friRepeat");
    	days[6] = j.getBoolean("satRepeat");
    	// If the alarm doesn't repeat any days it doesn't repeat at all
    	boolean r = false;
    	for(boolean b : days)
    		if(b)
    			r = true;

    	// TODO: redo the following with try/catch
    	//If no further information then skip the options
    	if(j.length() < 4) {
    	a = new Alarm(t, p, days);
    	}
    	//Else we need the options
    	else {
    		String s = j.getString("label");
    		String o = j.get("origin").toString();
    		String d = j.get("destination").toString();
    		a = new Alarm(t, p, days, o, d, s);
    	}
		return a;
    }
    
    @Override
    public String toString(){
    	String s = "\n";
    	s += "Alarm Name: " + this.label + "\n";
    	s += "\tTime: " + this.time.toString() + "\n";
    	s += "\tRepeat: " + this.Repeat + "\n";
    	s += "\tPrimary Key: " + this.primarykey + "\n";
    	s += "\tRepeat days: ";
	    	for(boolean b : this.days){
	    		if(b)
	    			s += "X";
	    		else
	    			s+= "0";
	    	}
	    s += "\n";
	    s += "\t Autoadvance: " + this.AutoAdvance + "\n";

    	
    	return s;
    }
}
