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

    ArrayList<String> sinemalarTitle;
    ArrayList<String> sinemalarImg;
    ArrayList<String> sinemalarDesc;

    public JSONArray sinemalarContent = new JSONArray();
    final String SINEMALAR_URL = "http://www.beyazperde.com/filmler/vizyondakiler/sinema-sayisi/";

    final String TAG_MAIN = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //new Request().execute("http://192.168.1.103:8080/Csec/Servlet");

        sinemalarTitle = new ArrayList<String>();
        sinemalarImg = new ArrayList<String>();
        sinemalarDesc = new ArrayList<String>();

        new SinemalarRequest().execute(SINEMALAR_URL);

//        Log.i(TAG_MAIN, sinemalarContent.toString());

        context = this;
        //checkIfPermissonExists();

//        Utils utils = new Utils(context);
//        Log.i("XXXXXXXX", utils.get_IMEI());
//        new Tickets().getTickets(context);

    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private void checkIfPermissonExists() {
        int hasWriteContactsPermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
            else {
                readContacts();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    readContacts();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void readContacts(){

        ContentResolver contentResolver = getContentResolver();
        Contacts contacts= new Contacts(contentResolver);
        contacts.getAllContacts();
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