package backend;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A class to handle connections to the LCD server
 * @author Kurt Floyd
 *
 */
public class Connection {
	
	private final int portNum = 8080;
	private Socket sock;
	
	Connection(){
		InetAddress addr;
		try{
			//Get the address of localhost
			addr = InetAddress.getByName(null);
			//Create the socket;
			sock = new Socket(addr, portNum);
		}
		catch(UnknownHostException e){
			System.err.println("Cannot find host for some reason, this should never happen");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Local server not working probably");
		}
		//TODO: Finish actually setting up the socket so that it works with Jacob's protocol
	}
	
	

}
