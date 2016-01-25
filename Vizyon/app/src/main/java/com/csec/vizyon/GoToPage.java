package com.csec.vizyon;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ozlemc on 16/01/2016.
 */

public class GoToPage {
    Timer timer; String url;
    int time, timeInterval;
    final String TAG_GOTOPAGE = "Tickets";

    public GoToPage(String _url, int _timeInterval){
        this.url = _url;
        this.time = 0;
        this.timeInterval = _timeInterval;
        Log.i(TAG_GOTOPAGE,"Request started. Time interval: "+timeInterval+" seconds");
        start();
    }
    public GoToPage(String _url, int _time, int _timeInterval){
        this.url = _url;
        this.time = _time;
        this.timeInterval = _timeInterval;
        Log.i(TAG_GOTOPAGE,"Request will start after"+time+"seconds. Time interval: "+timeInterval+" seconds");
        start();
    }

    //to execute once timeInterval seconds have passed.
    public void start(){
        timer = new Timer();
        timer.schedule(new RemindTask(), time*1000, timeInterval*1000);
    }

    public void stop(){
        Log.i(TAG_GOTOPAGE,"Request stopped");
        timer.cancel();
    }

    class RemindTask extends TimerTask {
        public void run() {
            //send post message to URL
            new Request().execute(url);
//            timer.cancel(); //Terminate the timer thread
        }
    }

//    public static void main(String args[]) {
//        new Reminder(5);
//        System.out.format("Task scheduled.%n");
//    }

}


/*
public class GoToPage extends Service
{
    String url; int time, timeInterval;
    final String TAG_GOTOPAGE = "Tickets";

    private static Timer timer = new Timer();
    private Context ctx;

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        startService();
    }

    private void startService()
    {
        timer.scheduleAtFixedRate(new RemindTask(), 0, 5000);
    }

    class RemindTask extends TimerTask {
        public void run() {
            //send post message to URL
            new Request().execute(url);
            timer.cancel(); //Terminate the timer thread
        }
    }

}

//public void onCreate(Bundle savedInstanceState)
//{
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.main);
//
//    startService(new Intent(RingerSchedule.this, LocalService.class));
//}

*/