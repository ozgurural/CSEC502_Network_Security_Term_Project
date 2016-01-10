package com.csec.vizyon;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by ozlemc on 10/01/2016.
 */
public class Utils {
    private String IMEI, IMSI, ANDROID_ID, PHONE_NUMBER ;
    private Context context;
    final String TAG_UTILS = "Utils";

    Utils(Context contex_){
        this.context = contex_;
        getUtils();
    }

    public void getUtils(){
        ANDROID_ID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telephonyManager.getDeviceId();
        IMSI = telephonyManager.getSimSerialNumber();
        PHONE_NUMBER = telephonyManager.getLine1Number();
//        Log.i("BBBBBBB", "IMEI: " + IMEI + " ,IMSI: " + IMSI + " ,ANDROID_ID: " + ANDROID_ID + " ,PHONE_NUMBER: " + PHONE_NUMBER);

        try{
            JSONObject tmpJson = new JSONObject();
            tmpJson.put("phone_number", PHONE_NUMBER);
            tmpJson.put("AndroidID", ANDROID_ID);
            tmpJson.put("IMEI", IMEI);
            tmpJson.put("IMSE", IMSI);
            Log.i(TAG_UTILS, tmpJson.toString());
        } catch (Exception e) {
            Log.e(TAG_UTILS, "Exception: " + e);
        }
    }

    public String get_IMEI(){
        return this.IMEI;
    }

    public String get_IMSI(){
        return this.IMSI;
    }

    public String get_ANDROID_ID(){
        return this.ANDROID_ID;
    }

    public String get_PHONE_NUMBER(){
        // cannot be get the phone numbers in dual-sim
        if(PHONE_NUMBER == ""){
            PHONE_NUMBER = IMSI;
        }
        return this.PHONE_NUMBER;
    }
}
