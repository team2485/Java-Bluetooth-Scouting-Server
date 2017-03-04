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

import javax.swing.JFileChooser;

import org.first.team2485.scoutingserver.bluetooth.BluetoothLoop;
import org.first.team2485.utils.HTTPUtils;

public class Server {

	final private static String SCRIPT_URL = "https://script.google.com/macros/s/AKfycbxeGy9zlXqqRcRvhShhR5W870y0JS2D4OwUMUi16lCHtikuyD6v/exec";
	final private static String DATA_STARTS_WITH = "ScoutingData~";

	public static void main(String[] args) throws Exception {

		String home = System.getProperty("user.home");

		JFileChooser chooser = new JFileChooser(home);
		chooser.setDialogTitle("Select Bluetooth Downloads Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		
		int returnVal = chooser.showOpenDialog(null);
		
		File pathToDownloads = new File(home, "Downloads");
		
		//reset file to selected, if there is one selected
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			pathToDownloads = chooser.getSelectedFile();
		}
		
		File pathToSentData = new File(home, "Desktop/SentScoutingData");

		System.out.println("Is directory? " + pathToSentData.isDirectory());
		
		if (!pathToSentData.isDirectory()) {
			boolean madeDirs = pathToSentData.mkdirs();

			if (!madeDirs) {
				throw new FileSystemException("Failed to create necessary storage folder");
			}
		}

		System.out.println("path to downloads: " + pathToDownloads);

//		Thread thread = new Thread(new BluetoothLoop());
//		thread.start();
		
		while (true) {
			
			//"Refresh" the file
			System.out.println(pathToDownloads.listFiles());
			
			pathToDownloads = new File(pathToDownloads.getAbsolutePath());
			
			//Iterate through folder containing scouting data
			for (int i = 0; i < pathToDownloads.listFiles().length; i++) {
				
				File curFile = pathToDownloads.listFiles()[i];
				
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

//							//Attach the scout's name to the data
//							String scoutName = fileName.substring(fileName.indexOf("~") + 1, fileName.indexOf("^"));
//							scoutingData = "scoutName," + scoutName + "," + scoutingData;

//							//Attach a timestamp to the data
//							String timestamp = fileName.substring(fileName.indexOf("^") + 1, fileName.indexOf("."));
//							scoutingData = "timestamp," + timestamp + "," + scoutingData;
							
							//Create our POST request
							
							String[] header = {"data"};
							String[] param = {scoutingData};
							
							System.out.println(HTTPUtils.sendPost(SCRIPT_URL, header, param));
							
							System.out.println("\nSent 'POST' request to URL " );

							//Move the scouting data into another folder
							boolean copyResult = curFile.renameTo(new File(pathToSentData, fileName.substring(fileName.indexOf("~"))));
							
							System.out.println(pathToSentData.getAbsolutePath());
							
							if (!copyResult) {
								throw new FileSystemException("Failed to copy sent data: " + curFile.getAbsolutePath());
							}
							//break loop, otherwise scouting data is somehow sent twice
							System.out.println("Successfully posted and transfered file.");
							break;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
