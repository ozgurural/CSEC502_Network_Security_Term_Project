package com.csec.servlet;

import java.util.Scanner;

public class InputThread extends Thread {
	Scanner scanner;
	String command;
	
	String getPhoneNumbersCommand = "get_numbers";
	
	public InputThread(){
		System.out.println("Input thread is started..");
		
		// create a scanner so we can read the command-line input
	    scanner = new Scanner(System.in);
	}
	
	public void run(){
		
		while(true){
			
			command = scanner.next();
			
			System.out.println(command);
		}
	}

}
