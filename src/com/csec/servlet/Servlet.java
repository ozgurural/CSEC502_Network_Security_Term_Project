package com.csec.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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


/**
 * Servlet implementation class Servlet
 */
@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
        // TODO Auto-generated constructor stub
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

		System.out.println("doPost");
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		JSONObject responseJson = new JSONObject();
		
		// Read from request
	    StringBuilder buffer = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();
	    System.out.println(data);
		
		try {
			JSONArray input = new JSONArray(data);
			writeJsonToFile("C:/Users/justburcel/workspace/Csec/input1.txt", input.toString());
			responseJson.put("status", "SUCCESS");
			out.print(responseJson);
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
	
	public static void writeJsonToFile(String fileName, String content){
		
		try {
			PrintWriter outFile = new PrintWriter(new FileWriter(fileName, true)); 
			outFile.write(content);
			outFile.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		finally {
			System.out.println("[FILE]: Created: " + fileName);
		}
	}

}
