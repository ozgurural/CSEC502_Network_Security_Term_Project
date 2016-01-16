package com.csec.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import sun.org.mozilla.javascript.internal.json.JsonParser;

@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Data userData = new Data();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
        
        InputThread th = new InputThread();
        th.start();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.print("<html><body>Swarm</body></html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("POST request received..");
		
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		JSONObject responseJson = new JSONObject();
		
		// Read from request
	    StringBuilder buffer = new StringBuilder();
		InputStream inputStream = request.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream , StandardCharsets.UTF_8));
		
	    String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();
	    System.out.println(data);
		
		try {
			JSONObject input = new JSONObject(data);
			writeToFile("C:/Users/justburcel/workspace/Csec/input1.txt", input.toString());
			responseJson.put("status", "SUCCESS");
			out.print(responseJson);
			
			userData.addToIpList(input.getString("ip"));
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public static JSONArray readFromFile(String fileName){
		
		JSONArray tmp = new JSONArray();
		try {
	    	BufferedReader br = new BufferedReader(new FileReader(fileName));
	        String line = br.readLine();

	        tmp = new JSONArray(line);
	        
	        br.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return tmp;
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
