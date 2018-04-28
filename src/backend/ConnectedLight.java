package backend;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;


// Only for interfacing with my ESP8266-based led light controller.
public class ConnectedLight {
    private String ip;
    private int portNum = 81;
    private int red = 0;
    private int green = 0;
    private int blue = 0;
    private WebSocket lightSocket;

    public ConnectedLight(String ip) throws IOException {
        this.ip = ip;
        String uri = "ws://" + ip + ":" + portNum;
        this.lightSocket = new WebSocketFactory().createSocket(uri);
    }

    public void increaseBrightness(int increment) {
        double[] proportions = generateProportion();

        // Proportionately increment red
        if(red + increment * proportions[0] <= 255) {
            double realIncrease = increment * proportions[0];
            red+= realIncrease;
        }
        else {
            red = 255;
        }
        // Proportionally increment green
        if(green + increment * proportions[1] <= 255) {
            double realIncrease = increment * proportions[1];
            green+= realIncrease;
        }
        else {
            green = 255;
        }
        // Proportionally increment blue
        if(blue + increment * proportions[2] <= 255) {
            double realIncrease = increment * proportions[2];
            blue+= realIncrease;
        }
        else {
            blue = 255;
        }
        sendUpdate();
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        if(red >=0 && red <=255)
            this.red = red;
        sendUpdate();
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        if(green >=0 && green <=255)
            this.green = green;
        sendUpdate();
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        if(blue >= 0 && blue <=255)
            this.blue = blue;
        sendUpdate();
    }

    public void setColor(int red, int green, int blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
        sendUpdate();
    }

    public void sendUpdate(){
        String output = "#";
        output += String.format("%02X", red);
        output += String.format("%02X", green);
        output += String.format("%02X", blue);
        try {
            lightSocket = lightSocket.recreate();//fixes socket not created error when sending more than one command.
            lightSocket.connect();
            lightSocket.sendText(output);
            lightSocket.disconnect();
        } catch (WebSocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double[] generateProportion(){
        //Generate a proportions so brightness can be increased or decreased while keeping a similar color.
        //This is not perfect at the moment because of the way our eyes interpret light intensity (logarithmic)
        double max = 0;
        double[] values = {red, green, blue};
        double[] proportions;
        for (int i = 0; i < 3; i++ ) {
            if(values[i] > max)
                max = values[i];
        }
        if(max == 0){
            proportions = new double[]{1,1,1};
        }
        else{
            proportions = new double[3];
            for (int i = 0; i < 3; i++ ) {
                proportions[i] = values[i] / max;
            }
        }
        return proportions;
    }
}
