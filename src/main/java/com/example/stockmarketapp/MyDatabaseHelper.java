package com.example.stockmarketapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "MyDatabaseHelper";

    Context mContext ;


    public static final String DATABASE_NAME = "stock_info.db";
    public static final int DATABASE_VERSION = 3;
    public static final String TABLE_NAME_BSE = "BSE";
    public static final String TABLE_NAME_NSE = "NSE";
    public static final String TABLE_NAME_RELIANCE = "RELIANCE";
    public static final String TABLE_NAME_ASHOKELEY = "ASHOKLEY";
    public static final String TABLE_NAME_TATA_STEEL = "TATA_STEEL";
    public static final String TABLE_NAME_EICHER = "EICHER";
    public static final String TABLE_NAME_CIPLA = "CIPLA";
    public static final String TABLE_NAME_BSESN = "BSENS";
    public static final String TABLE_NAME_NSEI = "NSEI";

    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String OPEN = "open";
    public static final String HIGH = "high";
    public static final String LOW = "low";
    public static final String CLOSE = "close";
    public static final String ADJUST_CLOSE = "adj_close";
    public static final String VOLUME = "volume";


    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ TABLE_NAME_BSE + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, open FLOAT, high FLOAT, low FLOAT, close FLOAT, adj_close FLOAT, volume INTEGER)");
        db.execSQL("create table "+ TABLE_NAME_NSE + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, open FLOAT, high FLOAT, low FLOAT, close FLOAT, adj_close FLOAT, volume INTEGER)");
        db.execSQL("create table "+ TABLE_NAME_RELIANCE + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, open FLOAT, high FLOAT, low FLOAT, close FLOAT, adj_close FLOAT, volume INTEGER)");
        db.execSQL("create table "+ TABLE_NAME_ASHOKELEY + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, open FLOAT, high FLOAT, low FLOAT, close FLOAT, adj_close FLOAT, volume INTEGER)");
        db.execSQL("create table "+ TABLE_NAME_TATA_STEEL + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, open FLOAT, high FLOAT, low FLOAT, close FLOAT, adj_close FLOAT, volume INTEGER)");
        db.execSQL("create table "+ TABLE_NAME_NSEI + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, open FLOAT, high FLOAT, low FLOAT, close FLOAT, adj_close FLOAT, volume INTEGER)");
        db.execSQL("create table "+ TABLE_NAME_EICHER + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, open FLOAT, high FLOAT, low FLOAT, close FLOAT, adj_close FLOAT, volume INTEGER)");
        db.execSQL("create table "+ TABLE_NAME_CIPLA + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, open FLOAT, high FLOAT, low FLOAT, close FLOAT, adj_close FLOAT, volume INTEGER)");
        db.execSQL("create table "+ TABLE_NAME_BSESN + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, open FLOAT, high FLOAT, low FLOAT, close FLOAT, adj_close FLOAT, volume INTEGER)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "  + TABLE_NAME_BSE);
        db.execSQL("DROP TABLE IF EXISTS "  + TABLE_NAME_NSE);
        db.execSQL("DROP TABLE IF EXISTS "  + TABLE_NAME_RELIANCE);
        db.execSQL("DROP TABLE IF EXISTS "  + TABLE_NAME_ASHOKELEY);
        db.execSQL("DROP TABLE IF EXISTS "  + TABLE_NAME_TATA_STEEL);
        db.execSQL("DROP TABLE IF EXISTS "  + TABLE_NAME_NSEI);
        db.execSQL("DROP TABLE IF EXISTS "  + TABLE_NAME_BSESN);
        db.execSQL("DROP TABLE IF EXISTS "  + TABLE_NAME_EICHER);
        db.execSQL("DROP TABLE IF EXISTS "  + TABLE_NAME_CIPLA);
        onCreate(db);
    }


    public boolean insertData (String table_name, String date, float open, float high, float low,
                               float close, float adj_close, int volume){


        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DATE, date );
        cv.put(OPEN, open );
        cv.put(HIGH, high );
        cv.put(LOW, low );
        cv.put(CLOSE, close );
        cv.put(ADJUST_CLOSE, adj_close );
        cv.put(VOLUME, volume );

        long result = db.insert(table_name, null, cv);

        /*if(result < 0){
            Toast.makeText(mContext, "Error while Adding data", Toast.LENGTH_SHORT).show();
            return true;
        }else {
            Toast.makeText(mContext, "SUCCESSFUL", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        return false;
    }


    public Cursor getAllData(String table_name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + table_name , null  );
        return cursor;
    }


    public StockData getData(String table_name, String date){
        Log.d(TAG, "getData: called");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name + " WHERE date"+ " = " + date, null );

        StockData data = new StockData();
        Log.d(TAG, "getData: cursor size " + cursor.getCount());
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String datetest = cursor.getString(1);
            float open = cursor.getFloat(2);
            float high = cursor.getFloat(3);
            float adj = cursor.getFloat(4);
            Log.d(TAG, "getData: " + open +" "+ high+" "+ adj);
            data.setDate(cursor.getString(1).toString());
            data.setOpen(cursor.getFloat(2));
            data.setHigh(cursor.getFloat(3));
            data.setLow(cursor.getFloat(4));
            data.setClose(cursor.getFloat(5));
            data.setAdj_close(cursor.getFloat(6));
            data.setVolume(cursor.getInt(7));


            cursor.moveToNext();
        }
        return data;
    }


}
