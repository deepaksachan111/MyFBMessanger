package com.example.deepak.myfbmessanger.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by deepak on 1/6/15.
 */
class DBHelper extends SQLiteOpenHelper{
    private SQLiteDatabase db;
    public static final String DATABASENAME = "mydatabase";
    public static final int VERSION = 1;
    public static final String MESSAGES_TABLE_NAME = "messages";
    public static final String  MESSAGE_COLUMN_ID ="id";
    public static final String  MESSAGE_COLUMN_MESSAGE_IN = "messagesin";
    public static final String  MESSAGE_COLUMN_MESSAGE_OUT = "messagesout";


    public DBHelper(Context context) {
        super(context, DATABASENAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + MESSAGES_TABLE_NAME+ "(id Integer primary key, messagesin text, messagesout text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS messages");
        onCreate(db);

    }
    public boolean insertMessages(String messagein) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE_COLUMN_MESSAGE_IN, messagein);
       // contentValues.put(MESSAGE_COLUMN_MESSAGE_OUT, messageout);

        db.insert(MESSAGES_TABLE_NAME, null, contentValues);
        return true;
    }
    public Cursor getMessages() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from messages limit 10", null);
        return cursor;


    }
}
