package com.example.sony.test3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


/**
 * Created by user on 31/12/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Reports.db";
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

        ContentValues values = new ContentValues();

        values.put("timestamp", timestamp);
        values.put("lat", lat);
        values.put("lng", lng);
        values.put("severity", severity);
        values.put("cause", cause);

        long result = db.insert("report_table", null, values);
        if(result != -1)
            Toast.makeText(context, "New Traffic Report", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Error in adding Traffic Report", Toast.LENGTH_SHORT).show();

        db.close();
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from report_table", null);
        return res;
    }

}