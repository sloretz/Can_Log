package edu.sjsu.canlog.app.backend;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import java.util.List;
import java.util.ArrayList;
/**
* Created by Brian on 4/27/2014.
*/

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CAN_LOG";
    private final String tableVIN;

    public DatabaseHandler(Context context, String car) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tableVIN="_" + car;
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TABLE= "CREATE TABLE IF NOT EXISTS" + tableVIN + " (time Integer PRIMARY KEY, x03 Integer, x04 Integer, x05 Integer, x0c Integer, x0d Integer, x11 Integer, x1f Integer, x21 Integer, x2f Integer, x30 Integer, x31 Integer, x4d Integer, x5c Integer, x5e Integer);";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + tableVIN);
        onCreate(db);
    }

    public void addRow(int time, int x03, int x04, int x05, int x0c, int x0d, int x11, int x2f, int x5c, int x5e)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("time", time);
        values.put("x03",x03);
        values.put("x04",x04);
        values.put("x05",x05);
        values.put("x0c",x0c);
        values.put("x0d",x0d);
        values.put("x11",x11);
        values.put("x1f",x1f);
        values.put("x21",x21);
        values.put("x2f",x30);
        values.put("x30",x30);
        values.put("x31",x31);
        values.put("x4d",x4d);
        values.put("x5c",x5c);
        values.put("x5e",x5e);

        db.insert(tableVIN, null, values);
    }

    public List<SQLdata> getAllData(String column){

        List<SQLdata> data = new ArrayList<SQLdata>();
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
    public List<SQLdata> getAllDataRange(String column, long begin, long end)
    {
        List<SQLdata> data = new ArrayList<SQLdata>();
        String selectQuery= "SELECT time, " + column + " FROM " + tableVIN + " WHERE time > " + begin +" AND time < " + end + ";";
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

