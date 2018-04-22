package backend;

import com.pi4j.io.gpio.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class Settings {
    Lcd2UsbClient lcd;
    GpioPinDigitalInput controlPanel[];
    Clock clock;
    boolean dimScreen;
    int listOffset;
    final String topLevelEntries[] = {"Set Time", "Set Date", "Set New Alarm", "Toggle Brightness"};
    final Runnable topLevelCommands[] = {this::timeSettings,this::dateSettings,
            this::newAlarmSettings, this::toggleBrightness};
    final String cursorString = "-> ";
    final String selectedCursorString = "* ";



    public Settings(Lcd2UsbClient lcd, GpioPinDigitalInput[] controlPanel, Clock clock){
        this.lcd = lcd;
        this.controlPanel = controlPanel;
        this.clock = clock;
        dimScreen = false;
        listOffset = 0;

    }

    public void start() throws InterruptedException, IOException {
        resumeOnRelease(controlPanel[0]);

        int cursorPos = 0;
        displayMenu(cursorPos, topLevelEntries);
        boolean active = true;

        while (active){
            Thread.sleep(100);

            if (controlPanel[2].isLow()){               //down
                if (cursorPos < topLevelEntries.length - 1){
                    cursorPos ++;
                }
                displayMenu(cursorPos, topLevelEntries);
                resumeOnRelease(controlPanel[2]);
            }
            else if (controlPanel[3].isLow()){          //up
                if (cursorPos > 0){
                    cursorPos --;
                }
                displayMenu(cursorPos, topLevelEntries);
                resumeOnRelease(controlPanel[3]);
            }
            else if (controlPanel[1].isLow()){          //select
                resumeOnRelease(controlPanel[1]);
                topLevelCommands[cursorPos].run();
                displayMenu(cursorPos, topLevelEntries);
            }
            else if(controlPanel[0].isLow()){           //menu
                active = false;
                resumeOnRelease(controlPanel[0]);
            }

        }

        //Return display to default state
        lcd.setText(0,"%I:%M%p");
        lcd.setText(1," ");
        lcd.setText(2, "%x");
        lcd.setText(3," ");

    }
    /*
    ***Top Menu Commands***
    * (cannot throw exceptions)
     */
    private void dateSettings() {
        LocalDate currentDate = LocalDate.now();

        int newY = currentDate.getYear();
        int newM = currentDate.getMonthValue();
        int newD = currentDate.getDayOfMonth();

        String textEntry[] = {"Year: ", "Month: ", "Day: ", "OK"};
        String activeEntry[] = {Integer.toString(newY), Integer.toString(newM), Integer.toString(newD), " "};

        int cursorPos = 0;
        boolean cursorSelected = false;
        boolean active = true;

        displayMenu(cursorPos, textEntry, activeEntry);

        while (active){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (controlPanel[2].isLow()){//down
                if (cursorSelected){
                    if (cursorPos == 0 && newY > LocalDate.MIN.getYear()){
                        newY--;
                        activeEntry[0] = Integer.toString(newY);
                        lcd.setText(0, selectedCursorString + textEntry[0] + activeEntry[0]);
                    }
                    else if (cursorPos == 1 && newM > 0){
                        newM--;
                        activeEntry[1] = Integer.toString(newM);
                        lcd.setText(1, selectedCursorString + textEntry[1] + activeEntry[1]);
                    }
                    else if (cursorPos == 2 && newD > 0){
                        newD--;
                        activeEntry[2] = Integer.toString(newD);
                        lcd.setText(2, selectedCursorString + textEntry[2] + activeEntry[2]);
                    }
                }
                else{
                    if (cursorPos < 3){
                        cursorPos ++;
                        displayMenu(cursorPos, textEntry, activeEntry);
                    }
                }

                resumeOnRelease(controlPanel[2]);
            }
            else if (controlPanel[3].isLow()){//up
                if (cursorSelected){
                    if (cursorPos == 0 && newY < LocalDate.MAX.getYear() ){
                        newY++;
                        activeEntry[0] = Integer.toString(newY);;
                        lcd.setText(0, selectedCursorString + textEntry[0] + activeEntry[0]);
                    }
                    else if (cursorPos == 1 && newM < 12){
                        newM++;
                        activeEntry[1] = Integer.toString(newM);
                        lcd.setText(1, selectedCursorString + textEntry[1] + activeEntry[1]);
                    }
                    else if (cursorPos == 2 && newD < 31){ //Need to account for months with less than 31 days
                        newD++;
                        activeEntry[2] = Integer.toString(newD);
                        lcd.setText(2, selectedCursorString + textEntry[2] + activeEntry[2]);
                    }
                }
                else{
                    if (cursorPos > 0){
                        cursorPos --;
                        displayMenu(cursorPos, textEntry, activeEntry);
                    }
                }
                resumeOnRelease(controlPanel[3]);
            }
            else if (controlPanel[1].isLow()){//select
                if (cursorPos == 3){
                    try {
                        LinuxTimeControl.setDate(newY, newM, newD);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    active = false;
                }
                else{
                    cursorSelected = true;
                    lcd.setText(cursorPos, selectedCursorString + textEntry[cursorPos]+ activeEntry[cursorPos]);
                }
                resumeOnRelease(controlPanel[1]);

            }
            else if(controlPanel[0].isLow()){//menu
                if (cursorSelected){
                    cursorSelected = false;
                    displayMenu(cursorPos, textEntry, activeEntry);
                }
                else{
                    active = false;
                }
                resumeOnRelease(controlPanel[0]);
            }

        }
    }

    private void timeSettings() {
        LocalTime currentTime = LocalTime.now();

        int newHr = currentTime.getHour();
        int newMin = currentTime.getMinute();

        String textEntry[] = {"Hour: ", "Minute: ", "OK", " "};
        String activeEntry[] = {Integer.toString(newHr), Integer.toString(newMin), " ", " "};

        int cursorPos = 0;
        boolean cursorSelected = false;
        boolean active = true;

        displayMenu(cursorPos, textEntry, activeEntry);

        while (active){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (controlPanel[2].isLow()){//down
                if (cursorSelected){
                    if (cursorPos == 0 && newHr > 0){
                        newHr--;
                        activeEntry[0] = Integer.toString(newHr);
                        lcd.setText(0, selectedCursorString + textEntry[0] + activeEntry[0]);
                    }
                    else if (cursorPos == 1 && newMin > 0){
                        newMin--;
                        activeEntry[1] = Integer.toString(newMin);
                        lcd.setText(1, selectedCursorString + textEntry[1] + activeEntry[1]);
                    }
                }
                else{
                    if (cursorPos < 3){
                        cursorPos ++;
                        displayMenu(cursorPos, textEntry, activeEntry);
                    }
                }

                resumeOnRelease(controlPanel[2]);
            }
            else if (controlPanel[3].isLow()){//up
                if (cursorSelected){
                    if (cursorPos == 0 && newHr < 23 ){
                        newHr++;
                        activeEntry[0] = Integer.toString(newHr);;
                        lcd.setText(0, selectedCursorString + textEntry[0] + activeEntry[0]);
                    }
                    else if (cursorPos == 1 && newMin < 59){
                        newMin++;
                        activeEntry[1] = Integer.toString(newMin);
                        lcd.setText(1, selectedCursorString + textEntry[1] + activeEntry[1]);
                    }
                }
                else{
                    if (cursorPos > 0){
                        cursorPos --;
                        displayMenu(cursorPos, textEntry, activeEntry);
                    }
                }
                resumeOnRelease(controlPanel[3]);
            }
            else if (controlPanel[1].isLow()){//select
                if (cursorPos == 2){
                    try {
                        LinuxTimeControl.setTime(newHr, newMin);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    active = false;
                }
                else{
                    cursorSelected = true;
                    lcd.setText(cursorPos, selectedCursorString + textEntry[cursorPos]+ activeEntry[cursorPos]);
                }
                resumeOnRelease(controlPanel[1]);

            }
            else if(controlPanel[0].isLow()){//menu
                if (cursorSelected){
                    cursorSelected = false;
                    displayMenu(cursorPos, textEntry, activeEntry);
                }
                else{
                    active = false;
                }
                resumeOnRelease(controlPanel[0]);
            }

        }
    }

    private void newAlarmSettings() {
        LocalTime currentTime = LocalTime.now();

        int newHr = currentTime.getHour();
        int newMin = currentTime.getMinute();

        String textEntry[] = {"Hour: ", "Minute: ", "Set for today", "Set for tomorrow"};
        String activeEntry[] = {Integer.toString(newHr), Integer.toString(newMin), " ", " "};

        int cursorPos = 0;
        boolean cursorSelected = false;
        boolean active = true;

        displayMenu(cursorPos, textEntry, activeEntry);

        while (active){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (controlPanel[2].isLow()){//down
                if (cursorSelected){
                    if (cursorPos == 0 && newHr > 0){
                        newHr--;
                        activeEntry[0] = Integer.toString(newHr);
                        lcd.setText(0, selectedCursorString + textEntry[0] + activeEntry[0]);
                    }
                    else if (cursorPos == 1 && newMin > 0){
                        newMin--;
                        activeEntry[1] = Integer.toString(newMin);
                        lcd.setText(1, selectedCursorString + textEntry[1] + activeEntry[1]);
                    }
                }
                else{
                    if (cursorPos < 3){
                        cursorPos ++;
                        displayMenu(cursorPos, textEntry, activeEntry);
                    }
                }

                resumeOnRelease(controlPanel[2]);
            }
            else if (controlPanel[3].isLow()){//up
                if (cursorSelected){
                    if (cursorPos == 0 && newHr < 23 ){
                        newHr++;
                        activeEntry[0] = Integer.toString(newHr);;
                        lcd.setText(0, selectedCursorString + textEntry[0] + activeEntry[0]);
                    }
                    else if (cursorPos == 1 && newMin < 59){
                        newMin++;
                        activeEntry[1] = Integer.toString(newMin);
                        lcd.setText(1, selectedCursorString + textEntry[1] + activeEntry[1]);
                    }
                }
                else{
                    if (cursorPos > 0){
                        cursorPos --;
                        displayMenu(cursorPos, textEntry, activeEntry);
                    }
                }
                resumeOnRelease(controlPanel[3]);
            }
            else if (controlPanel[1].isLow()){//select
                if (cursorPos == 2){
                    LocalTime alarmTime = LocalTime.of(newHr, newMin);
                    clock.setAlarm(alarmTime,LocalDate.now(),false, "Atlanta, GA", "Rome, GA"); //STRINGS ARE PLACEHOLDERS!!!
                    active = false;
                }
                else if(cursorPos == 3){
                    LocalTime alarmTime = LocalTime.of(newHr, newMin);
                    LocalDate alarmDate = LocalDate.now();
                    alarmDate.plusDays(1);

                    clock.setAlarm(alarmTime,alarmDate,false, "Atlanta, GA", "Rome, GA"); //SRINGS ARE PLACEHOLDERS!!!
                    active = false;
                }
                else{
                    cursorSelected = true;
                    lcd.setText(cursorPos, selectedCursorString + textEntry[cursorPos]+ activeEntry[cursorPos]);
                }
                resumeOnRelease(controlPanel[1]);

            }
            else if(controlPanel[0].isLow()){//menu
                if (cursorSelected){
                    cursorSelected = false;
                    displayMenu(cursorPos, textEntry, activeEntry);
                }
                else{
                    active = false;
                }
                resumeOnRelease(controlPanel[0]);
            }

        }

    }

    private void toggleBrightness(){
        if(dimScreen){
            lcd.setBrightness(200);
            dimScreen = false;
        }
        else{
            lcd.setBrightness(0);
            dimScreen = true;
        }
    }

    private void NTPSettings(){
        int cursorPos = 0;
        String textEntry[] = {"Turn on time sync", "Turn off time sync", "Set Timezone", " "};
        Boolean active = true;

        displayMenu(cursorPos, textEntry);
        while (active) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (controlPanel[2].isLow()){               //down
                if (cursorPos >= 2)
                {
                    cursorPos++;
                }
                displayMenu(cursorPos, textEntry);
                resumeOnRelease(controlPanel[2]);
            }
            else if (controlPanel[3].isLow()){          //up
                if (cursorPos > 0){
                    cursorPos --;
                }
                displayMenu(cursorPos, textEntry);
                resumeOnRelease(controlPanel[3]);
            }
            else if (controlPanel[1].isLow()){          //select
                resumeOnRelease(controlPanel[1]);
                if(cursorPos ==0) {
                    try {
                        LinuxTimeControl.enableNTP();
                    } catch (IOException e) {
                        e.printStackTrace();   //display error on lcd instead?
                    }
                }
                else if (cursorPos ==1){
                    try {
                        LinuxTimeControl.disableNTP();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (cursorPos == 2){
                    //not yet available
                }
                displayMenu(cursorPos, textEntry);
            }
            else if(controlPanel[0].isLow()){           //menu
                active = false;
                resumeOnRelease(controlPanel[0]);
            }
        }
    }
    /*
    ***Other Functions***
     */
    private void displayMenu(int cursor, String entries[]){
        if (cursor < listOffset){
            listOffset--;    //scrolls entries list up
        }
        else if (cursor > listOffset + 3){
            listOffset++;    //scrolls entries list down
        }
        for (int i = 0;i < 4; i++){
            if (i == cursor){
                lcd.setText(i,cursorString + entries[i + listOffset]);
            }
            else{
                lcd.setText(i, entries[i + listOffset]);
            }
        }
    }

    private void displayMenu(int cursor, String entries[], String secondaryEntries[]){
        if (cursor < listOffset){
            listOffset--;    //scrolls entries list up
        }
        else if (cursor > listOffset + 3){
            listOffset++;    //scrolls entries list down
        }
        for (int i = 0;i < 4; i++){
            if (i == cursor){
                lcd.setText(i,cursorString + entries[i + listOffset] + secondaryEntries[i + listOffset]);
            }
            else{
                lcd.setText(i, entries[i + listOffset] + secondaryEntries[i + listOffset]);
            }
        }
    }

    private void resumeOnRelease(GpioPinDigitalInput pressedButton){
        while(pressedButton.isLow()){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
