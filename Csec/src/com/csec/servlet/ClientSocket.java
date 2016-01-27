package com.csec.servlet;

import java.io.DataOutputStream;
import java.net.Socket;

public class ClientSocket {
	
	int PhoneSocket = 6666;
	
	public void sendToSocket(String urlInput, String message){
		try {
			Socket socket = new Socket(urlInput, PhoneSocket);
			
			DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

			dOut.writeUTF(message);
			dOut.close();
			socket.close();
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
