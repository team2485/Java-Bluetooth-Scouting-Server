package org.first.team2485.scoutingserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Server {

	final private static String SCRIPT_URL = "https://script.google.com/macros/s/AKfycbz9crlL01Qs2531MZDMRqJxBdMAwGPhpT1PvG7-sJJrxCLAfafI/exec";
	final private static String DATA_STARTS_WITH = "ScoutingData~";

	public static void main(String[] args) throws FileSystemException {

		String home = System.getProperty("user.home");
		File pathToDownloads = new File(home, "Downloads");
		File pathToSentData = new File(home, "Desktop/SentScoutingData");

		if (!pathToSentData.isDirectory()) {
			boolean madeDirs = pathToSentData.mkdirs();

			if (!madeDirs) {
				throw new FileSystemException("Failed to create necessary storage folder");
			}
		}

		System.out.println("path to downloads: " + pathToDownloads);

		while (true) {
			
			//Iterate through folder containing scouting data
			for (File curFile : pathToDownloads.listFiles()) {
				
				//If it is a file (not a dir) and is readable
				if (curFile.canRead() && curFile.isFile()) {

					String fileName = curFile.getName();

					//If it is scouting data (name matches)
					if (fileName.startsWith(DATA_STARTS_WITH)) {
						
						BufferedReader bufferedReader = null;
						try {
							
							//Read the scouting data
							FileReader fileReader = new FileReader(curFile);
							bufferedReader = new BufferedReader(fileReader);
							String scoutingData = bufferedReader.readLine();
							bufferedReader.close();

							//Attach the scout's name to the data
							String scoutName = fileName.substring(fileName.indexOf("~") + 1, fileName.indexOf("^"));
							scoutingData = "scoutName," + scoutName + "," + scoutingData;

							//Attach a timestamp to the data
							String timestamp = fileName.substring(fileName.indexOf("^") + 1, fileName.indexOf("."));
							scoutingData = "timestamp," + timestamp + "," + scoutingData;

							//Create our GET request
							String request = SCRIPT_URL + "?data=" + scoutingData;
							URL url = new URL(request);
							
							//Open the connection
							url.openConnection();
							// Completely ignore the connection, our work is done :P

							System.out.println("\nSent 'GET' request to URL : " + url);

							//Move the scouting data into another folder
							boolean copyResult = curFile.renameTo(new File(pathToSentData, fileName));

							if (!copyResult) {
								throw new FileSystemException("Failed to copy sent data");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
