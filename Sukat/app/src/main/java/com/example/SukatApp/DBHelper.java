package com.example.SukatApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


//This java class is responsible for all DB-related functions
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "Measurements.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Measurements(ObJectName TEXT primary key, dimension TEXT,measurement FLOAT, unit TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Measurements");

    }

    /**
     * Inserts an entry to the database with the following data:
     * @param name of the object to be inserted
     * @param dimension of the object to be measured(Height or Width)
     * @param numerical measurement of the object's dimension.
     * @param unit of measurement
     */
    public Boolean insertMeasurement(String ObjectName, String dimension, float measurement, String unit) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ObjectName", ObjectName);
        contentValues.put("dimension", dimension);
        contentValues.put("measurement", measurement);
        contentValues.put("unit", unit);

        long result = DB.insert("Measurements", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

   /**
     * Deletes an entry from the database
     * @param name of the object to be deleted
     */
    public Boolean deleteMeasurement(String ObjectName) {
        SQLiteDatabase DB = this.getWritableDatabase();


        Cursor cursor = DB.rawQuery("Select * from Measurements where ObjectName = ?", new String[]{ObjectName});
        if (cursor.getCount() > 0) {


            long result = DB.delete("Measurements", "ObjectName=?", new String[]{ObjectName});

            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }else {
            return false;
        }

    }

    public Cursor getData() {
        SQLiteDatabase DB = this.getWritableDatabase();


        Cursor cursor = DB.rawQuery("Select * from Measurements ", null);
        return cursor;

    }

}
