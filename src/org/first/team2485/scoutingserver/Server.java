package org.first.team2485.scoutingserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.JFileChooser;

import org.first.team2485.utils.HTTPUtils;
import org.first.team2485.utils.USBDriveDataPuller;

public class Server {

	final private static String SCRIPT_URL = "https://script.google.com/macros/s/AKfycbxdQigOa6DuajIWo7HWFyCAGS0hLCPkcB-HbfSkYuk29qEQqEq3/exec";
	final public static String DATA_STARTS_WITH = "ScoutingData~";
	
	private static File sentDataFolder;

	public static void main(String[] args) throws Exception {

		String home = System.getProperty("user.home");

		JFileChooser chooser = new JFileChooser(home);
		chooser.setDialogTitle("Select Incoming Data Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		int returnVal = chooser.showOpenDialog(null);

		// Default to the user's Downloads folder
		File incomingDataFolder = new File(home, "Downloads");

		// Change the folder to the folder selected
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			incomingDataFolder = chooser.getSelectedFile();
		}

		System.out.println("Reading incoming data from: " + incomingDataFolder.getAbsolutePath());

		sentDataFolder = new File(incomingDataFolder, "SentScoutingData");

		if (!sentDataFolder.isDirectory()) {
			boolean madeDirs = sentDataFolder.mkdirs();

			if (!madeDirs) {
				throw new FileSystemException("Failed to create necessary storage folder");
			}
		}
		
		USBDriveDataPuller.start();

		while (true) {

			// "Refresh" the file
			incomingDataFolder = new File(incomingDataFolder.getAbsolutePath());

			// Iterate through folder containing scouting data
			for (int i = 0; i < incomingDataFolder.listFiles().length; i++) {

				File curFile = incomingDataFolder.listFiles()[i];

				// If it is a file (not a dir) and is readable
				if (curFile.canRead() && curFile.isFile()) {

					String fileName = curFile.getName();

					// If it is scouting data (name matches)
					if (fileName.startsWith(DATA_STARTS_WITH)) {
						sendDataAndMoveFile(curFile);
					}
				}
			}

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void sendDataAndMoveFile(File curFile) {
		
		String fileName = curFile.getName();
		
		BufferedReader bufferedReader = null;
		try {

			// Read the scouting data
			FileReader fileReader = new FileReader(curFile);
			bufferedReader = new BufferedReader(fileReader);
			String scoutingData = bufferedReader.readLine();
			bufferedReader.close();

			// Create our POST request

			String[] header = { "data" };
			String[] param = { scoutingData };

			HTTPUtils.sendPost(SCRIPT_URL, header, param);

			System.out.println("Sent 'POST' request to URL");

			// Move the scouting data into another folder

			Files.move(curFile.toPath(), new File(sentDataFolder, fileName).toPath(),
					StandardCopyOption.REPLACE_EXISTING);

			System.out.println("Successfully posted and transfered file.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
