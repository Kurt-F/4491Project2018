import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class lcd2usbClient
{
    private Socket lcdSocket;
    private PrintWriter socketOut;
    private BufferedReader socketIn;

    public lcd2usbClient() throws IOException //localhost:8080 default
    {
        lcdSocket = new Socket("localhost", 8080);
        socketOut = new PrintWriter(lcdSocket.getOutputStream(), true);
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
