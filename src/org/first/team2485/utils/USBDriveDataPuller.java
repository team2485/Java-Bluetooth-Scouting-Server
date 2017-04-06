package org.first.team2485.utils;

import java.io.File;

import javax.swing.JOptionPane;

import org.first.team2485.scoutingserver.Server;

public class USBDriveDataPuller {

	private static File[] oldListRoot = File.listRoots();

	public static void start() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (File.listRoots().length > oldListRoot.length) {
						oldListRoot = File.listRoots();

						int input = JOptionPane.showConfirmDialog(null, "Do you want to push data from this drive?",
								"USB Drive", JOptionPane.YES_NO_OPTION);

						if (input == JOptionPane.YES_OPTION) {

							File[] foldersInDrive = oldListRoot[oldListRoot.length - 1].listFiles();

							for (File f : foldersInDrive) {
								if (f.getName().startsWith("ScoutingRecords")) {
									File[] dataFiles = f.listFiles();

									for (File curDataFile : dataFiles) {

										if (curDataFile.getName().startsWith(Server.DATA_STARTS_WITH)) {
											Server.sendDataAndMoveFile(curDataFile);
										}
									}
								}
							}

							JOptionPane.showMessageDialog(null, "All data sent");
						}

					} else if (File.listRoots().length < oldListRoot.length) {
						oldListRoot = File.listRoots();
					}
				}
			}
		});
		t.setName("USB-Data-Pusher-Thread");
		t.setDaemon(true);
		t.start();
	}
}