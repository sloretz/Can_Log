package edu.sjsu.canlog.app.backend;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
/**
 * Created by Brian on 4/27/2014.
 */
public class SQLlogging {

    public class DatabaseHandler extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "CON_LOG";
        private String tableVIN;

        public DatabaseHandler(Context context, String car) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            tableVIN=car;
        }
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            String CREATE_TABLE= "CREATE TABLE " + tableVIN + "( time Integer, 0x03 Integer, 0x04 Integer, 0x05 Integer, 0x0c Integer, 0x0d Integer, 0x11 Integer, 0x1f Integer, 0x21 Integer, 0x2f Integer, 0x30 Integer, 0x31 Integer, 0x4d Integer, 0x5c Integer, 0x5e Integer);";
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + tableVIN);
            onCreate(db);
        }

    }
}
