package com.example.sony.test3;

import android.content.ContentValues;
import android.content.Context;
<<<<<<< HEAD
import android.content.Intent;
=======
>>>>>>> 56758e1060b54d1eee659f9192454bc65795767a
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by user on 31/12/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
<<<<<<< HEAD
    public static final String DATABASE_NAME = "Reports.db";
=======
    public static final String DATABASE_NAME = "Student.db";
>>>>>>> 56758e1060b54d1eee659f9192454bc65795767a


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table report_table (ID INTEGER PRIMARY KEY AUTOINCREMENT, TIMESTAMP TEXT, LAT REAL, LNG REAL, SEVERITY TEXT, CAUSE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists report_table");
        onCreate(db);
    }

    public static void putSmsToDatabase(String timestamp, Double lat, Double lng, String severity, String cause, Context context ) {
        DatabaseHelper dataBaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
<<<<<<< HEAD
=======

>>>>>>> 56758e1060b54d1eee659f9192454bc65795767a
        ContentValues values = new ContentValues();

        values.put("timestamp", timestamp);
        values.put("lat", lat);
        values.put("lng", lng);
        values.put("severity", severity);
        values.put("cause", cause);

        long result = db.insert("report_table", null, values);
        if(result != -1)
<<<<<<< HEAD
            Toast.makeText(context, "Inserted data to DB", Toast.LENGTH_SHORT).show();
=======
            Toast.makeText(context, "Inserted data", Toast.LENGTH_SHORT).show();
>>>>>>> 56758e1060b54d1eee659f9192454bc65795767a
        else
            Toast.makeText(context, "Error in inserting SMS to database", Toast.LENGTH_SHORT).show();

        db.close();
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from report_table", null);
        return res;
    }

}