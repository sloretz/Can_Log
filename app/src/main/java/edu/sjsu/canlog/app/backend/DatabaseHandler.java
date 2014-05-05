package edu.sjsu.canlog.app.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
/**
* Created by Brian on 4/27/2014.
*/

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "CAN_LOG";
    private static final String TABLE = "LOGS";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //tableVIN="_" + car;
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //if (tableVIN.equals("_")) {
        //   Log.d("DatabaseLogger", "Choosing not to create table " + tableVIN);
        //    return; //hackish, don't create when just using for showAllTables()
        //}
        String CREATE_TABLE= "CREATE TABLE IF NOT EXISTS " + TABLE + " (VIN TEXT, time Integer, x03 Integer, x04 Integer, x05 Integer, x0c Integer, x0d Integer, x11 Integer, x2f Integer, x5c Integer, x5e Integer, PRIMARY KEY (VIN,TIME))";
        Log.d("DatabaseLogger","Creating database table " + TABLE);
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void addRow(String vin, int time, int x03, int x04, int x05, int x0c, int x0d, int x11, int x2f, int x5c, int x5e)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("VIN", vin);
        values.put("time", time);
        values.put("x03",x03);
        values.put("x04",x04);
        values.put("x05",x05);
        values.put("x0c",x0c);
        values.put("x0d",x0d);
        values.put("x11",x11);
        values.put("x2f",x2f);
        values.put("x5c",x5c);
        values.put("x5e",x5e);

        db.insert(TABLE, null, values);
    }
    public Cursor showAllVINs()
    {
        SQLiteDatabase db =this.getReadableDatabase();
        String sql = "SELECT VIN FROM " + TABLE + " GROUP BY VIN";
        return db.rawQuery(sql,null);
    }
    /*
    public ArrayList<SQLdata> getAllData(String column){

        ArrayList<SQLdata> data = new ArrayList<SQLdata>();
        String selectQuery= "SELECT time, " + column + " FROM " + tableVIN +";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do {
                SQLdata sqldata = new SQLdata(cursor.getInt(0), cursor.getInt(1));
                data.add(sqldata);
            } while(cursor.moveToNext());
        }
        return data;
    }
    */
    public ArrayList<SQLdata> getAllDataRange(String VIN, String column, long begin, long end)
    {
        ArrayList<SQLdata> data = new ArrayList<SQLdata>();
        String selectQuery= "SELECT time, " + column + " FROM " + TABLE + " WHERE time > " + begin +" AND time < " + end + " AND VIN = '" + VIN+"' ;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do {
                SQLdata sqldata = new SQLdata(cursor.getInt(0), cursor.getInt(1));
                data.add(sqldata);
            } while(cursor.moveToNext());
        }
        return data;
    }

}

