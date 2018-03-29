package backend;

import java.io.*;
import java.net.Socket;

public class Lcd2UsbClient
{
    private Socket lcdSocket;
    private PrintWriter socketOut;
    private BufferedReader socketIn;

    public Lcd2UsbClient() throws IOException //localhost:8080 default
    {
        lcdSocket = new Socket("localhost", 8080);
        socketOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(lcdSocket.getOutputStream())),true);
        //socketOut = new PrintWriter(lcdSocket.getOutputStream(), true);
        socketIn = new BufferedReader(new InputStreamReader(lcdSocket.getInputStream()));
    }

    public void setText(int line, String lineText)
    {
        String lengthString = String.format("%02d", lineText.length());
        String lineString = String.format("%01d", line);
        socketOut.println("" + lineString + lengthString + lineText);
    }

    public void setBrightness(int brightness)
    {
        String brightnessString = String.format("%03d", brightness);
        socketOut.println("@" + "03" + brightnessString);
    }

    public void close() throws IOException
    {
        socketOut.close();
        socketIn.close();
        lcdSocket.close();
    }
}
