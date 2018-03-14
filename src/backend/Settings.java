package backend;

import com.pi4j.io.gpio.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Settings {
    Lcd2UsbClient lcd;
    GpioPinDigitalInput controlPanel[];
    final String topLevelEntries[] = {"Set Time", "Set Date", " ", " "};
    final String cursorString = "-> ";
    final String selectedCursorString = "* ";



    public Settings(Lcd2UsbClient lcd, GpioPinDigitalInput[] controlPanel) {
        this.lcd = lcd;
        this.controlPanel = controlPanel;
    }

    public void start() throws InterruptedException, IOException {
        while(controlPanel[0].isLow()){    //Wait for user to release the button
            Thread.sleep(50);           //Used for each button press below
        }

        int cursorPos = 0;
        displayMenu(cursorPos, topLevelEntries);
        boolean active = true;

        while (active){
            Thread.sleep(100);

            if (controlPanel[2].isLow()){//down
                if (cursorPos < 3){
                    cursorPos ++;
                }
                displayMenu(cursorPos, topLevelEntries);
                while(controlPanel[2].isLow()){
                    Thread.sleep(50);
                }
            }
            else if (controlPanel[3].isLow()){//up
                if (cursorPos > 0){
                    cursorPos --;
                }
                displayMenu(cursorPos, topLevelEntries);
                while(controlPanel[3].isLow()){
                    Thread.sleep(50);
                }
            }
            else if (controlPanel[1].isLow()){//select
                if(cursorPos == 0){
                    timeSettings();
                }
                else if (cursorPos == 1){//menu
                    dateSettings();
                }
                displayMenu(cursorPos, topLevelEntries);
                while(controlPanel[1].isLow()){
                    Thread.sleep(50);
                }
            }
            else if(controlPanel[0].isLow()){
                active = false;
                while(controlPanel[0].isLow()){
                    Thread.sleep(50);
                }
            }

        }

        lcd.setText(0,"%I:%M%p");
        lcd.setText(1," ");
        lcd.setText(2, "%x");
        lcd.setText(3," ");

    }

    private void dateSettings() {

    }

    private void timeSettings() throws InterruptedException, IOException {
        String currentHr = new SimpleDateFormat("HH").format(new Date());
        String currentMin = new SimpleDateFormat("mm").format(new Date());
        int newHr = Integer.parseInt(currentHr);
        int newMin = Integer.parseInt(currentMin);

        String textEntry[] = {"Hour: ", "Minute: ", " ", " "};
        int activeEntry[] = {newHr, newMin, 0,0};

        int cursorPos = 0;
        boolean cursorSelected = false;
        boolean active = true;

        displayMenu(cursorPos, textEntry, activeEntry);

        while (active){
            Thread.sleep(100);

            if (controlPanel[2].isLow()){//down
                if (cursorSelected){
                    if (cursorPos == 0 && activeEntry[0] > 0){
                        activeEntry[0]--;
                        lcd.setText(0, selectedCursorString + textEntry[0] + activeEntry[0]);
                    }
                    else if (cursorPos == 1 && activeEntry[1] > 0){
                        activeEntry[1]--;
                        lcd.setText(1, selectedCursorString + textEntry[1] + activeEntry[1]);
                    }
                }
                else{
                    if (cursorPos < 3){
                        cursorPos ++;
                        displayMenu(cursorPos, textEntry, activeEntry);
                    }
                }

                while(controlPanel[2].isLow()){
                    Thread.sleep(50);
                }
            }
            else if (controlPanel[3].isLow()){//up
                if (cursorSelected){
                    if (cursorPos == 0 && activeEntry[0] < 23 ){
                        activeEntry[0]++;
                        lcd.setText(0, selectedCursorString + textEntry[0] + activeEntry[0]);
                    }
                    else if (cursorPos == 1 && activeEntry[0] < 59){
                        activeEntry[1]++;
                        lcd.setText(1, selectedCursorString + textEntry[1] + activeEntry[1]);
                    }
                }
                else{
                    if (cursorPos > 0){
                        cursorPos --;
                        displayMenu(cursorPos, textEntry, activeEntry);
                    }
                }
                while(controlPanel[3].isLow()){
                    Thread.sleep(50);
                }
            }
            else if (controlPanel[1].isLow()){//select
                cursorSelected = true;
                lcd.setText(cursorPos, selectedCursorString + textEntry[cursorPos]+ activeEntry[cursorPos]);

            }
            else if(controlPanel[0].isLow()){//menu
                if (cursorSelected){
                    cursorSelected = false;
                    displayMenu(cursorPos, textEntry, activeEntry);
                }
                else{
                    active = false;
                }
                while(controlPanel[0].isLow()){
                    Thread.sleep(50);
                }
            }

        }
        LinuxTimeControl.setTime(activeEntry[0], activeEntry[1]);
    }

    private void displayMenu(int cursor, String entries[]){
        System.out.println("Drawing menu...");
        for (int i = 0;i < 4; i++){
            if (i == cursor){
                lcd.setText(i,cursorString + entries[i]);
            }
            else{
                lcd.setText(i, entries[i]);
            }
        }
    }

    private void displayMenu(int cursor, String entries[], int secondaryEntries[]){
        System.out.println("Drawing menu...");
        for (int i = 0;i < 4; i++){
            if (i == cursor){
                lcd.setText(i,cursorString + entries[i] + secondaryEntries[i]);
            }
            else{
                lcd.setText(i, entries[i] + secondaryEntries[i]);
            }
        }
    }


}
