package com.example.sony.test3;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.Calendar;
import java.util.StringTokenizer;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by user on 28/12/2016.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";
    public static String timestamp;
    public static Double lng;
    public static Double lat;
    public static String severity;
    public static String cause, sender;
    activity_offline_simple addMarkers = new activity_offline_simple();



    public void onReceive(Context context, Intent intent) {
        addMarkers = new activity_offline_simple();
        Bundle intentExtras = intent.getExtras();


        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);
                }

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                sender = address;

                if(address.equals("+639268247123")) {
                    abortBroadcast();
                    StringTokenizer tokens = new StringTokenizer(smsBody, "/");
                    timestamp = tokens.nextToken();
                    lat = Double.parseDouble(tokens.nextToken());
                    lng = Double.parseDouble(tokens.nextToken());
                    severity = tokens.nextToken();
                    cause = tokens.nextToken();

                   // Toast.makeText(getContext(), "Received New Traffic Report", Toast.LENGTH_LONG).show();
                }
            }

            if(sender.equals("+639268247123")) {
                DatabaseHelper.putSmsToDatabase(timestamp, lat, lng, severity, cause, context);
                //start activity
                Intent i = new Intent();
                i.setClassName("com.example.sony.test3", "com.example.sony.test3.activity_offline_simple");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                //addMarkers.update_location();

            }

        }

    }

}