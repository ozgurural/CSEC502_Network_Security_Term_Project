package com.csec.servlet;

import java.util.List;
import java.util.Scanner;

public class InputThread extends Thread {
	Scanner scanner;
	String command;
	String commandOutputParser = "> ";
	
	String getAllIpAddresses = "ip";
	List<String> ipList;
	
	Data userData;
	
	public InputThread(){
		System.out.println("Input thread is started..");
		
		userData = new Data();
		userData.readIpAddresses();
		ipList = userData.getIpList();
		
		// create a scanner so we can read the command-line input
	    scanner = new Scanner(System.in);
	}
	
	public void run(){
		
		while(true){
			
			command = scanner.next();
			
			String commandType = command.split(" ")[0];
			
			if(commandType.equals(getAllIpAddresses)){
				if(ipList.size() == 0){
					System.out.println(commandOutputParser + "No active IP is found.");
				}
				else {
					for(String ip : ipList){
						System.out.println(commandOutputParser + ip);
					}
				}
			}
			else {
				// Help command
				System.out.println(commandOutputParser + "Get All IP Addresses: " + getAllIpAddresses);
			}
		}
	}

}
