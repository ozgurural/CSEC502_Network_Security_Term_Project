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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Data {
	
	static String FILESEPARATOR = File.separator;
	static List<String> ipList = new ArrayList<String>();
	
	String dataRootFileLocation = "Data";
	String ipAddressesFileLocation = dataRootFileLocation + FILESEPARATOR + "IP.txt";
	String inputRootFileLocation = dataRootFileLocation + FILESEPARATOR + "input";
	
	String jsonUtilsTag = "utils";
	
	public Data(){
		createDirectory(dataRootFileLocation);
		createDirectory(inputRootFileLocation);
	}
	
	public void addToIpList(String ip){
		
		//ip already exists
		if(ipList.contains(ip)){
			return;
		}
		
		//add ip
		ipList.add(ip);
		
		//Save ips to file
		StringBuilder sb = new StringBuilder();
		for(String item : ipList){
			sb.append(item + "\n");
		}
		writeToFile(ipAddressesFileLocation, sb.toString());
	}
	
	public List<String> getIpList(){
		return ipList;
	}
	
	public void saveInput(String ip, JSONObject content){
		DateFormat dateFormat = new SimpleDateFormat("---yyyy-MM-dd--HH-mm-ss");
		String currDate = dateFormat.format(new Date());
		String inputFileLocation = inputRootFileLocation + FILESEPARATOR + ip + currDate + ".txt";
		
		writeToFile(inputFileLocation, content.toString());
		
	}
	
	public JSONObject getUtils(String ip){
		JSONObject returnJson = new JSONObject();
		
		File inputDir = new File(inputRootFileLocation);
		for(File file : inputDir.listFiles()) {
		    if(file.getName().startsWith(ip)){
		    	String filePath = file.getAbsolutePath();
		    	String fileInput = readFile(filePath);
		    	String fileTime = getTimeFromFileName(filePath);
		    	
		    	try {
		    		JSONObject fileInputJson = new JSONObject(fileInput);
		    		if(fileInputJson.has(jsonUtilsTag)){
		    			
		    			returnJson.put(fileTime, fileInputJson.get(jsonUtilsTag));
		    		}
		    	} catch (JSONException e) {
					System.out.println(e.getMessage());
				}
		    }
		}
		return returnJson;
	}
	
	public String getTimeFromFileName(String fileName){
		String[] fileNameSplitted = fileName.split("---");
		if(fileNameSplitted.length > 1){
			return fileNameSplitted[1];
		}
		else {
			return "TIMEUNKNOWN";
		}
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
	
	public boolean readIpAddresses(){
		BufferedReader br;
		try{
			//check if ip addresses file exists
			File ipFile = new File(ipAddressesFileLocation);
			if(!ipFile.exists()) { 
			    return false;
			}
			
			br = new BufferedReader(new FileReader(ipAddressesFileLocation));
			StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    while (line != null) {
		    	ipList.add(line);
		        line = br.readLine();
		    }
		    br.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return true;
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
