package com.csec.servlet;

import java.util.List;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class InputThread extends Thread {
	Scanner scanner;
	String command;
	String commandOutputParser = "> ";
	
	//commands
	String getAllIpAddresses = "ip";
	String getUtils = "util";
	String getContacts = "contacts";
	String getGps = "gps";
	String sendCommand = "send";
	
	
	
	List<String> ipList;
	String sendCommandPort = ":8080";
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
			
			if(command.equals(getAllIpAddresses)){
				if(ipList.size() == 0){
					System.out.println(commandOutputParser + "No active IP is found.");
				}
				else {
					for(String ip : ipList){
						System.out.println(commandOutputParser + ip);
					}
				}
			}
			else if(command.equals(getUtils)){
				command = scanner.next();
				JSONObject allUtils = userData.getUtils(command);
				
				System.out.println(commandOutputParser + "Printing all utils info for IP: " + command);
				try {
					System.out.println(allUtils.toString(4));
				} catch (JSONException e) {
					e.printStackTrace();
				} 
			}
			else if(command.equals(getContacts)){
				command = scanner.next();
				JSONObject allUtils = userData.getContacts(command);
				
				System.out.println(commandOutputParser + "Printing all contacts for IP: " + command);
				try {
					System.out.println(allUtils.toString(4));
				} catch (JSONException e) {
					e.printStackTrace();
				} 
			}
			else if(command.equals(getGps)){
				command = scanner.next();
				JSONObject allUtils = userData.getGps(command);
				
				System.out.println(commandOutputParser + "Printing gps for IP: " + command);
				try {
					System.out.println(allUtils.toString(4));
				} catch (JSONException e) {
					e.printStackTrace();
				} 
			}
			else if(command.equals(sendCommand)){
				String sendIp =  scanner.next();
				String message =  scanner.next();
				
				ClientSocket clientSocket = new ClientSocket();
				clientSocket.sendToSocket(sendIp, message);
			}
			else {
				// Help command
				System.out.println(commandOutputParser + "Get all IP addresses: " + getAllIpAddresses);
				System.out.println(commandOutputParser + "Get util info about ip: " + getUtils + " <IP>");
				System.out.println(commandOutputParser + "Get contacts info about ip: " + getContacts + " <IP>");
				System.out.println(commandOutputParser + "Get gps info about ip: " + getGps + " <IP>");
				System.out.println(commandOutputParser + "Send command to ip: " + sendCommand + " <IP> ");
			}
		}
	}

}
