package com.csec.vizyon;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by burcel on 31/12/15.
 */
public class Request extends AsyncTask<String, Void, String> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAGREQUEST = "Request";

    @Override
    protected String doInBackground(String... params) {

        String url = params[0];
        String content = params[1];

        try {
            return HttpPost(url, content);
        } catch (IOException e) {
            return "Unable to retrieve data. URL may be invalid.";
        }
    }

    @Override
    protected void onPostExecute(String result) {

        Log.i(TAGREQUEST, "onPostExecute:" + result);

    }

    private String HttpPost(String urlInput, String content) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(urlInput);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", "application/json");
            conn.setDoInput(true);
            conn.connect();

            byte[] outputBytes = content.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputBytes);
            os.close();

            int response = conn.getResponseCode();
            //Log.i(TAGREQUEST, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = convertInputStreamToString(is);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String convertInputStreamToString(InputStream stream) throws IOException, UnsupportedEncodingException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }
}