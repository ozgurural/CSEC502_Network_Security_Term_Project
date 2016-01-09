package com.csec.vizyon;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by justburcel on 09/01/2016.
 */
public class Contacts extends Activity {

    final String TAG_CONTACTS = "Contacts";
    ContentResolver contentResolver;

    Contacts(ContentResolver contentResolver){
        this.contentResolver = contentResolver;
    }

    public JSONArray getAllContacts(){

        JSONArray contactList = new JSONArray();

        String phoneNumber = new String();
        String email = new String();
        String contact_id = new String();
        String name = new String();

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);


        try {
            // Loop for every contact in the phone
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    JSONObject person = new JSONObject();

                    contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                    name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                    if (hasPhoneNumber > 0) {

                        person.put("name", name);

                        // Query and loop for every phone number of the contact
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            person.put("phone_number", phoneNumber);

                        }
                        phoneCursor.close();

                        // Query and loop for every email of the contact
                        Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
                        while (emailCursor.moveToNext()) {
                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                            person.put("email", email);
                        }
                        emailCursor.close();
                    }
                    contactList.put(person);
                }
                Log.i(TAG_CONTACTS, contactList.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return contactList;
    }
}
