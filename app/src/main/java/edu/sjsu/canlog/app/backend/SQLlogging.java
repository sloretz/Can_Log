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
            String CREATE_TABLE= "CREATE TABLE " + tableVIN + "(time Integer PRIMARY KEY, x03 Integer, x04 Integer, x05 Integer, x0c Integer, x0d Integer, x11 Integer, x1f Integer, x21 Integer, x2f Integer, x30 Integer, x31 Integer, x4d Integer, x5c Integer, x5e Integer);";
            db.execSQL(CREATE_TABLE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + tableVIN);
            onCreate(db);
        }

    }
}
