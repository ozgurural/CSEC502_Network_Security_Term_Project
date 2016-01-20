package com.csec.vizyon;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Ozgur on 1/19/2016.
 */
public class CallLogs extends Activity {

    final String TAG_CALLLOGS = "CallLogs";

    ContentResolver contentResolver;

    CallLogs(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }


    public JSONArray getCallLogs() {

        JSONArray callLogList = new JSONArray();

        Uri CONTENT_URI = CallLog.Calls.CONTENT_URI;
        String Duration = new String();
        String callName = new String();
        String dateString = new String();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
        try {
            // Loop for every contact in the phone
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    JSONObject person = new JSONObject();

                    callName = cursor.getString(cursor
                            .getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));
                    person.put("CallName", callName);

                    int colPhoneNo = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                    person.put("phone_number", cursor.getString(colPhoneNo));

                    int colType = cursor.getColumnIndex(CallLog.Calls.TYPE);
                    String valType = cursor.getString(colType);
                    if (valType.equals(String.valueOf(CallLog.Calls.INCOMING_TYPE))) {
                        person.put("TYPE", "Incoming");
                    } else if (valType.equals(String.valueOf(CallLog.Calls.OUTGOING_TYPE))) {
                        person.put("TYPE", "Outgoing");
                    } else if (valType.equals(String.valueOf(CallLog.Calls.MISSED_TYPE))) {
                        person.put("TYPE", "Missed");
                    }
                    String callDate = cursor.getString(cursor
                            .getColumnIndex(android.provider.CallLog.Calls.DATE));
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "dd-MMM-yyyy HH:mm");
                    dateString = formatter.format(new Date(Long.parseLong(callDate)));
                    person.put("Date", dateString);

                    Duration = cursor.getString(cursor
                            .getColumnIndex(android.provider.CallLog.Calls.DURATION));
                    person.put("Duration", Duration);

                    callLogList.put(person);
                }
                Log.i(TAG_CALLLOGS, callLogList.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return callLogList;

    }

}
