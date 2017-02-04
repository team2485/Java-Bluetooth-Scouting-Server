package org.first.team2485.scoutingserver.bluetooth;

import javax.swing.JOptionPane;

import org.json.JSONObject;

public class BluetoothLoop implements Runnable {
	
	private final String eventKey = "2016cars";
	private final int maxBet = 100;
	private int pool;
	private int nextMatch = 0;
	
	
	@Override
	public void run() {
		do {
			nextMatch = Integer.parseInt(JOptionPane.showInputDialog(null, "Please input the next match that will be scouted."));
		} while (nextMatch != 0);
		
		while (true) {
			
			

			String[] headers = new String[] {"X-TBA-App-Id"};
			String[] params = new String[] {"frc2485:betting-system:v01"};
				
			System.out.println("Sending HTTP GET...");

			String matchData = "";
				
			try {
				matchData = HTTPUtils.sendGet("https://www.thebluealliance.com/api/v2/match/2016cars_qm" + nextMatch,headers, params);
			} catch (Exception e) {
				System.out.println("Match not played yet, retrying in 15 seconds...");
				
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
			JSONObject JSONMatchData = new JSONObject(matchData);
			
			int redScore = ((JSONObject) ((JSONObject) JSONMatchData.get("alliances")).get("red")).getInt("score");
			int blueScore = ((JSONObject) ((JSONObject) JSONMatchData.get("alliances")).get("blue")).getInt("score");

			
			String toSend = nextMatch + "," + ((redScore > blueScore) ? "Red" : "Blue");
			System.out.println("Match data: " + toSend);
			
			ExpandedRemoteDevice[] devices = BluetoothSystem.pairedDevices();
						
			for ( int i =0; i < devices.length; i ++) {
				try {
					System.out.println("Sending file to " + devices[i].getName());
					BluetoothSystem.sendToDevice(devices[i], "FRCMatch" + nextMatch, toSend);
				} catch(Exception e) {
					e.printStackTrace();
					continue;
				}
					
				//BluetoothSystem.sendToDevice(devices[i], fileName, dataToSend);
			}
				
			try{
				Thread.sleep(60000);

			} catch (Exception e) {
				e.printStackTrace();
			}
			

		}
	}
	


}
