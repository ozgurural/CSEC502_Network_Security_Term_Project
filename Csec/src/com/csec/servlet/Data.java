package com.csec.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Data {
	
	static String FILESEPARATOR = File.pathSeparator;
	static List<String> ipList = new ArrayList<String>();
	
	public Data(){
		
	}
	
	public void addToIpList(String ip){
		ipList.add(ip);
	}
	
	public List<String> getIpList(){
		return ipList;
	}

	//return true if directory is created, false otherwise
	public boolean createDirectory(String directoryName){
		boolean returnBool = false;
		try{
			File theDir = new File(directoryName);
			if (!theDir.exists()) {
				theDir.mkdir();
				returnBool = true;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return returnBool;
	}
	
	public String readFile(String fileName){
		BufferedReader br;
		String returnString = new String();
		try{
			br = new BufferedReader(new FileReader(fileName));
			StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    returnString = sb.toString();
		    br.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return returnString;
	}
	
	public void writeToFile(String fileName, String content){
		Writer out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
		    out.write(content);
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
