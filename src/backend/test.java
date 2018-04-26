package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.*;
public class test {

	public static void main(String[] args) {
		StringBuffer json = null;
		try{
		URL url = new URL("http://127.0.0.1:8123/alarms/get/0");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
			String inputLine;
			json = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
						json.append(inputLine);
					}
				in.close();
		}
		catch(IOException e){
			
		}
		System.out.println(json.toString().charAt(0));
		String j = json.toString();
		System.out.println(j);
		JSONArray test = new JSONArray(j);
		System.out.println(test.toString());
	}

}
