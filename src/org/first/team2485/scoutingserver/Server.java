package org.first.team2485.scoutingserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Server {
	
	public static void main(String[] args) {
		while (true){
			File pathToDownloads = new File("C:\\Users\\sahan\\Downloads");
			for (File curFile : pathToDownloads.listFiles()){
				if (curFile.canRead() && curFile.isFile()){
					if (curFile.getName().startsWith("ScoutingData:")){
						try {
							FileReader fileReader = new FileReader(curFile);
							BufferedReader bufferedReader = new BufferedReader(fileReader);
							String scoutingData = bufferedReader.readLine();
							
							String url = ""; // set this at some point
							URL obj = new URL(url);
							HttpURLConnection con = (HttpURLConnection) obj.openConnection();
							con.setRequestMethod("GET");
							con.setRequestProperty("Send Scouting Data", scoutingData);
							
							int responseCode = con.getResponseCode();
							System.out.println("\nSending 'GET' request to URL : " + url);
							System.out.println("Response Code : " + responseCode);

							BufferedReader in = new BufferedReader(
							        new InputStreamReader(con.getInputStream()));
							String inputLine;
							StringBuffer response = new StringBuffer();

							while ((inputLine = in.readLine()) != null) {
								response.append(inputLine);
							}
							in.close();

							//print result
							System.out.println(response.toString());

						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
}

