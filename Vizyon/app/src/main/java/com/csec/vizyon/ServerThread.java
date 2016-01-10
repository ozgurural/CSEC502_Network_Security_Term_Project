package com.csec.vizyon;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

/**
 * Created by justburcel on 10/01/2016.
 */
public class ServerThread extends Thread {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    final String TAG_SERVER = "ServerThread";

    public void run(){

        try{

            Log.i(TAG_SERVER, "IP:" + getLocalIpAddress());
            // Open a server socket listening on port 8080
            InetAddress addr = InetAddress.getByName(getLocalIpAddress());
            serverSocket = new ServerSocket(8080, 0, addr);
            clientSocket = serverSocket.accept();

            // Client established connection.
            // Create input and output streams
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Read received data and echo it back
            String input = in.readLine();
            out.println("received: " + input);
            Log.i(TAG_SERVER, "input: " + input);

            // Perform cleanup
            in.close();
            out.close();

        } catch(Exception e) {
            // Omitting exception handling for clarity
        }
    }

    private String getLocalIpAddress() throws Exception {
        String resultIpv6 = "";
        String resultIpv4 = "";

        for (Enumeration en = NetworkInterface.getNetworkInterfaces();
             en.hasMoreElements();) {

            NetworkInterface intf = (NetworkInterface) en.nextElement();
            for (Enumeration enumIpAddr = intf.getInetAddresses();
                 enumIpAddr.hasMoreElements();) {

                InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                if(!inetAddress.isLoopbackAddress()){
                    if (inetAddress instanceof Inet4Address) {
                        resultIpv4 = inetAddress.getHostAddress().toString();
                    } else if (inetAddress instanceof Inet6Address) {
                        resultIpv6 = inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        return ((resultIpv4.length() > 0) ? resultIpv4 : resultIpv6);
    }
}