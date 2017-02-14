package com.example.sony.test3;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    Button btnViewAll;
    private RadioButton CauseRadioButton1, CauseRadioButton2, CauseRadioButton3;
    private RadioButton SeverityRadioButton1, SeverityRadioButton2, SeverityRadioButton3;
    String severityResult, causeResult;

    ArrayList<String> smsMessagesList = new ArrayList<>();
    ListView messages;
    ArrayAdapter arrayAdapter;
    SmsManager smsManager = SmsManager.getDefault();
    private static MainActivity inst;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    public static String res;

    //String mydate = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(Calendar.getInstance().getTime());

    String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);
        btnViewAll = (Button) findViewById(R.id.all);

        messages = (ListView) findViewById(R.id.messages);

        SeverityRadioButton1=(RadioButton)findViewById(R.id.light);
        SeverityRadioButton2=(RadioButton)findViewById(R.id.moderate);
        SeverityRadioButton3=(RadioButton)findViewById(R.id.heavy);

        CauseRadioButton1=(RadioButton)findViewById(R.id.weather);
        CauseRadioButton2=(RadioButton)findViewById(R.id.accident);
        CauseRadioButton3=(RadioButton)findViewById(R.id.undetermined);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        messages.setAdapter(arrayAdapter);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        }
    }

    public void onAllClick(View view) {
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0){
            Toast.makeText(this, "FAIL!", Toast.LENGTH_SHORT).show();
            showMessage("No Data found", "Error");
            //startActivity(new Intent(MainActivity.this, MapsActivity.class));
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()){
            buffer.append("Id: " + res.getString(0) +"\n");
            buffer.append("Timestamp: " + res.getString(1) +"\n");
            buffer.append("Lat: " + res.getString(2) +"\n");
            buffer.append("Lang: " + res.getString(3) +"\n");
            buffer.append("Severity: " + res.getString(4) +"\n");
            buffer.append("Cause: " + res.getString(5) +"\n\n");
        }

        //show all data
        showMessage("Data", buffer.toString());


    }

    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new
                AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_SMS)) {
                    Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS},
                        READ_SMS_PERMISSIONS_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onSendClick(View view) {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {


            if (SeverityRadioButton1.isChecked())
                severityResult="Light";
            else if (SeverityRadioButton2.isChecked())
                severityResult="Moderate";
            else
                severityResult="Heavy";

            if (CauseRadioButton1.isChecked())
                causeResult="Weather";
            else if (CauseRadioButton2.isChecked())
                causeResult="Accident";
            else
                causeResult="Unknown";


            //Change number to SMS gateway number
            //smsManager.sendTextMessage("+639268247123", null, mydate + "/" + Double.toString(MapsActivity.getLong()) + "/" + Double.toString(MapsActivity.getLat()) + "/" + radioSeverityButton.getText().toString() + "/" + radioCauseButton.getText().toString(), null, null);
            // Toast.makeText(this, "Message send!", Toast.LENGTH_SHORT).show();
            smsManager.sendTextMessage("+639283148474", null, mydate + "/" + activity_offline_simple.getLat() + "/" + activity_offline_simple.getLong() + "/" + severityResult + "/" + causeResult + "/Report", null, null);

            startActivity(new Intent(MainActivity.this, activity_offline_simple.class));

        }
    }

}
