package org.first.team2485.scoutingserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
}

