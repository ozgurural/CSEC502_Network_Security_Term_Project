package com.csec.vizyon;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.provider.Settings.Secure;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by ozlemc on 10/01/2016.
 */
public class Tickets {

    JSONArray Tickets_received;
    JSONArray Tickets_sent;
    JSONObject tickets;
    String IMEI, IMSI, AndroidID;
    String[] columns = new String[] { "address", "person", "date", "body", "type", "status",
                                        "read", "seen", "service_center", "protocol"};
    Utils utils;
    Tickets(){
        Tickets_received = new JSONArray();
        Tickets_sent = new JSONArray();
        tickets = new JSONObject();
    }

    public JSONObject getTickets(Context context){
        final String TAG_TICKETS = "Tickets";
        try{
            getTicketsReceived(context);
            getTicketsSent(context);
            tickets.put("Received", Tickets_received);
            tickets.put("Sent", Tickets_sent);

//            context.getContentResolver().registerContentObserver(Uri.parse("content://sms/out"), true, new SentTicketsObserver(new Handler(),context,phone_number));
        } catch (Exception e) {
            Log.e(TAG_TICKETS, "Exception: " + e);
        }
        return tickets;
    }

    private JSONArray getTicketsReceived(Context context){
        final String TAG_TICKETS = "TicketsReceived";

        Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(
                mSmsinboxQueryUri,
                new String[]{"_id", "thread_id", "address", "person", "date",
                        "body", "type", "status", "read", "seen", "service_center", "protocol"}, null, null, null);
        try {
            if (cursor.getCount() > 0) {
                String count = Integer.toString(cursor.getCount());
                //Log.i("Count", count);
                JSONObject tmpJson = new JSONObject();
                while (cursor.moveToNext()) {
//                    tmpJson.put("phone_number_to", phone_number);
                    tmpJson.put("phone_number_from",cursor.getString(cursor
                            .getColumnIndex(columns[0])));
                    tmpJson.put("date",cursor.getString(cursor
                            .getColumnIndex(columns[2])));
                    tmpJson.put("message", cursor.getString(cursor
                            .getColumnIndex(columns[3])));
                    tmpJson.put("status", cursor.getString(cursor
                            .getColumnIndex(columns[5])));
                    tmpJson.put("service_center", cursor.getString(cursor
                            .getColumnIndex(columns[8])));
                    tmpJson.put("protocol", cursor.getString(cursor
                            .getColumnIndex(columns[9])));

                    //Log.i(TAG_TICKETS, tmpJson.toString());
                    Tickets_received.put(tmpJson);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG_TICKETS, "Exception: " + e);
        }
        return Tickets_received;
    }

    private JSONArray getTicketsSent(Context context){
        final String TAG_TICKETS = "TicketsSent";
        Uri mSmsinboxQueryUri = Uri.parse("content://sms/sent");
        Cursor cursor = context.getContentResolver().query(
                mSmsinboxQueryUri,
                new String[]{"_id", "thread_id", "address", "person", "date",
                        "body", "type", "status", "read", "seen", "service_center", "protocol"}, null, null, null);
        try {
            if (cursor.getCount() > 0) {
                String count = Integer.toString(cursor.getCount());
                //Log.i("Count", count);
                JSONObject tmpJson = new JSONObject();
                while (cursor.moveToNext()) {
//                    tmpJson.put("phone_number_from", phone_number);
                    tmpJson.put("phone_number_to",cursor.getString(cursor
                            .getColumnIndex(columns[0])));
                    tmpJson.put("date",cursor.getString(cursor
                            .getColumnIndex(columns[2])));
                    tmpJson.put("message", cursor.getString(cursor
                            .getColumnIndex(columns[3])));
                    tmpJson.put("status", cursor.getString(cursor
                            .getColumnIndex(columns[5])));
                    tmpJson.put("service_center", cursor.getString(cursor
                            .getColumnIndex(columns[8])));
                    tmpJson.put("protocol", cursor.getString(cursor
                            .getColumnIndex(columns[9])));

                    //Log.i(TAG_TICKETS, tmpJson.toString());
                    Tickets_sent.put(tmpJson);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG_TICKETS, "Exception: " + e);
        }
        return Tickets_sent;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static class TicketsReceiverListener extends BroadcastReceiver{
        final String TAG_TICKETS = "TicketsReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                    JSONObject tmpJson = new JSONObject();
                    for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                        tmpJson.put("phone_number_from", smsMessage.getOriginatingAddress().toString());
                        // TODO phone_number eklenecek
//                        tmpJson.put("phone_number_to", "");
                        tmpJson.put("date", smsMessage.getTimestampMillis());
                        tmpJson.put("message", smsMessage.getMessageBody().toString());
                        tmpJson.put("status", smsMessage.getStatus());
                        tmpJson.put("service_center", smsMessage.getServiceCenterAddress().toString());
                        tmpJson.put("protocol", smsMessage.getProtocolIdentifier());

                        //Log.i(TAG_TICKETS, tmpJson.toString());
                    }
                }
            }catch (JSONException e){
                Log.e(TAG_TICKETS, "Exception: " + e.toString());
            }
        }
    }

    public class TicketsSenderListener extends BroadcastReceiver {
        static final String ACTION = "android.provider.Telephony.SMS_SENT";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION)) {
                Log.i("ÇALIŞMADI", "-----------------------------");
            }
        }
    }

 /*
    *   MAY BE USED
    */

    private void sendBilet(){
        final String TAG_TICKETS = "SendTickets";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("write number", null, "OK", null, null);
        }catch (Exception e) {
            Log.e(TAG_TICKETS, "Exception: " + e);
        }
    }

    class SentTicketsObserver extends ContentObserver {
        final String TAG_TICKETS = "TicketsSent";
        Context context;
        String phone_number;
        public SentTicketsObserver(Handler handler, Context context, String phone_number) {
            super(handler);
            this.context = context;
            this.phone_number = phone_number;
        }

        @Override
        public void onChange(boolean selfChange) {
            // save the message to the SD card here
            Uri mSmsinboxQueryUri = Uri.parse("content://sms/out");
            Cursor cursor = context.getContentResolver().query(
                    mSmsinboxQueryUri,
                    new String[] { "_id", "thread_id", "address", "person", "date",
                            "body", "type", "status", "read", "seen", "service_center", "protocol" }, null, null, null);
            String[] columns = new String[] { "address", "person", "date", "body", "type", "status", "read", "seen", "service_center", "protocol"};
            try {
                // this will make it point to the first record, which is the last SMS sent
                JSONObject tmpJson = new JSONObject();
                if (cursor.moveToNext()) {
                    tmpJson.put("phone_number_from", phone_number);
                    tmpJson.put("phone_number_to", cursor.getString(cursor
                            .getColumnIndex(columns[0])));
                    tmpJson.put("date", cursor.getString(cursor
                            .getColumnIndex(columns[2])));
                    tmpJson.put("message", cursor.getString(cursor
                            .getColumnIndex(columns[3])));
                    tmpJson.put("status", cursor.getString(cursor
                            .getColumnIndex(columns[5])));
                    tmpJson.put("serivce_center", cursor.getString(cursor
                            .getColumnIndex(columns[8])));
                    tmpJson.put("protocol", cursor.getString(cursor
                            .getColumnIndex(columns[9])));

                    //Log.i(TAG_TICKETS, tmpJson.toString());
                }
            } catch (Exception e) {
                Log.e(TAG_TICKETS, "Exception: " + e);
            }
            super.onChange(selfChange);
        }
    }

    public class getTicketsReceiver2 extends BroadcastReceiver {
        final String TAG_TICKETS = "TicketsReceived";

        public getTicketsReceiver2(){}

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle myBundle = intent.getExtras();
            SmsMessage [] messages = null;
            String strMessage = "";

            try {

                if (myBundle != null)
                {
                    Object [] pdus = (Object[]) myBundle.get("pdus");
                    messages = new SmsMessage[pdus.length];

                    for (int i = 0; i < messages.length; i++)
                    {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        strMessage += "SMS From: " + messages[i].getOriginatingAddress();
                        strMessage += " : ";
                        strMessage += messages[i].getMessageBody();
                        strMessage += "\n";
                    }
                    //Log.i(TAG_TICKETS, "+++++++++++++++++++++++++++++++");
                    //Log.i(TAG_TICKETS, strMessage);
                } // bundle is null

            } catch (Exception e) {
                Log.e(TAG_TICKETS, "Exception: " + e);
            }
        }
    }

    public static class FirstService extends Service{

        final String TAG_TICKETS = "SERVICEEEEEEE";
        FirstService(){}
        @Override
        public IBinder onBind(Intent arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onStart(Intent intent, int startId) {
            // TODO Auto-generated method stub
            super.onStart(intent, startId);
            Log.d(TAG_TICKETS, "--- FirstService started ---");
            this.stopSelf();
        }

        @Override
        public void onDestroy() {
            // TODO Auto-generated method stub
            super.onDestroy();
            Log.d(TAG_TICKETS, "--- FirstService destroyed ---");
        }

    }
}
