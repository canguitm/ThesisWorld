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

<<<<<<< HEAD
import static android.support.v4.app.ActivityCompat.startActivity;

=======
>>>>>>> 56758e1060b54d1eee659f9192454bc65795767a
/**
 * Created by user on 28/12/2016.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

<<<<<<< HEAD
=======
    DatabaseHelper db;
>>>>>>> 56758e1060b54d1eee659f9192454bc65795767a
    public static final String SMS_BUNDLE = "pdus";
    public static String timestamp;
    public static Double lng;
    public static Double lat;
    public static String severity;
<<<<<<< HEAD
    public static String cause, sender;
=======
    public static String cause;
>>>>>>> 56758e1060b54d1eee659f9192454bc65795767a
    activity_offline_simple workples = new activity_offline_simple();



    public void onReceive(Context context, Intent intent) {
        workples = new activity_offline_simple();
        Bundle intentExtras = intent.getExtras();

<<<<<<< HEAD

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

=======
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
>>>>>>> 56758e1060b54d1eee659f9192454bc65795767a
            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);
                }

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();
<<<<<<< HEAD
                sender = address;

                if(address.equals("+639057767601")) {
                    StringTokenizer tokens = new StringTokenizer(smsBody, "/");
                    timestamp = tokens.nextToken();
                    lat = Double.parseDouble(tokens.nextToken());
                    lng = Double.parseDouble(tokens.nextToken());
                    severity = tokens.nextToken();
                    cause = tokens.nextToken();

                    Toast.makeText(context, "Received New Traffic Report", Toast.LENGTH_LONG).show();
                }
            }

            if(sender.equals("+639057767601")) {
                DatabaseHelper.putSmsToDatabase(timestamp, lat, lng, severity, cause, context);
                workples.update_location();

            }
        //    workples.update_location();

        }

      //  workples.update_location();
    }

=======

                StringTokenizer tokens = new StringTokenizer(smsBody, "/");
                timestamp = tokens.nextToken();
                lat = Double.parseDouble(tokens.nextToken());
                lng = Double.parseDouble(tokens.nextToken());
                severity = tokens.nextToken();
                cause = tokens.nextToken();

                Toast.makeText(context, "New Traffic Report Added", Toast.LENGTH_LONG).show();
            }




            DatabaseHelper.putSmsToDatabase(timestamp, lat, lng, severity, cause, context);
            workples.update_location();

        }
    }
>>>>>>> 56758e1060b54d1eee659f9192454bc65795767a

}