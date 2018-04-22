package backend;

import java.time.LocalTime;
import org.json.*;

public class Alarm implements Comparable<Alarm>{
    /*This is a new implementation of Alarm for consideration.
    The main highlight is the repeatDays variable as a different way of dealing with repeating alarms.
     */
    LocalTime time;
    Integer primarykey; // Used for synchronising with the clock
    boolean Repeat;
    boolean[] days;
    boolean AutoAdvance;  //Enables or disables early wakeup based on traffic/weather.
    String origin;
    String destination;

    boolean randomAudio;
    String alarmAudio;


    public Alarm(LocalTime time, boolean Repeat, Integer k, boolean[] d) {
    	primarykey = k;
        this.time = time;
        this.Repeat = Repeat;
        this.AutoAdvance = false;
        days = d;
    }

    public Alarm(LocalTime time, boolean Repeat, Integer k, boolean[] d, String origin, String destination) {
    	primarykey = k;
        this.time = time;
        this.Repeat = Repeat;
        this.AutoAdvance = true;
        this.origin = origin;
        this.destination = destination;
        days = d;
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
    
    //Create an alarm object from a JSONObject
    public static Alarm fromJSON(JSONObject j) {
    	//Instantiate alarm
    	Alarm a = null;
    	LocalTime t = LocalTime.parse(j.get("time").toString());
    	boolean r = Boolean.parseBoolean(j.get("repeat").toString());
    	//Get the days
    	boolean[] days = new boolean[7];
    	days[0] = j.getBoolean("sunRepeat");
    	days[1] = j.getBoolean("monRepeat");
    	days[2] = j.getBoolean("tueRepeat");
    	days[3] = j.getBoolean("wedRepeat");
    	days[4] = j.getBoolean("thuRepeat");
    	days[5] = j.getBoolean("friRepeat");
    	days[6] = j.getBoolean("satRepeat");

    	Integer i = null;
    	try {
    		i = Integer.parseInt(j.get("pk").toString());
    	}
    	catch (JSONException e){
    		//No primary key
    	}
    	//If no further information then skip the options
    	if(j.length() < 4) {
    	a = new Alarm(t, r, i, days);
    	}
    	//Else we need the options
    	else {
    		String o = j.get("origin").toString();
    		String d = j.get("destination").toString();
    		a = new Alarm(t, r, i, days, o, d);
    	}
		return a;
    }
}
