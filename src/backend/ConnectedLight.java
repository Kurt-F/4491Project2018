package backend;

public class ConnectedLight {
    private String ip;
    private int socket;
    private int red = 0;
    private int green = 0;
    private int blue = 0;



    public ConnectedLight(String ip, int socket) {
        this.ip = ip;
        this.socket = socket;
    }

    public void increaseBrightness(int increment) {
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

        if(red + increment * proportions[0] <= 255) {
            double realIncrease = increment * proportions[0];
            red+= realIncrease;
        }
        else {
            red = 255;
        }

        if(green + increment * proportions[1] <= 255) {
            double realIncrease = increment * proportions[1];
            green+= realIncrease;
        }
        else {
            green = 255;
        }

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

    public void sendUpdate(){

    }
}
