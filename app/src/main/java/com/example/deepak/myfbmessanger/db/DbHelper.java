package com.example.deepak.myfbmessanger.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.deepak.myfbmessanger.Message.MessageIn;
import com.example.deepak.myfbmessanger.Message.MessageOut;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by deepak on 5/6/15.
 */
 class DbHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 8;


    private Dao<UserProfile,Integer> userProfiles = null;
    private RuntimeExceptionDao<UserProfile,Integer> userProfileStringRuntimeExceptionDao= null;

    private Dao<MessageIn,Long> messageIns= null;
    private RuntimeExceptionDao<MessageIn , String> messageInsRuntimeDao= null;

    private Dao<MessageOut,Long> messageOuts= null;
    private RuntimeExceptionDao<MessageOut , String> messageOutRuntimeDao= null;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource cs) {
        try
        {
            TableUtils.createTable(cs, MessageIn.class);
            TableUtils.createTable(cs, MessageOut.class);
            TableUtils.createTable(cs, UserProfile.class);
        }
        catch ( SQLException oops)
        {
            Log.e(TAG,"createtable  ...........");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource cs, int i, int i2) {
        try {
            TableUtils.dropTable(cs, MessageOut.class,true);
            TableUtils.dropTable(cs,MessageIn.class,true);
            TableUtils.dropTable(cs,UserProfile.class,true);
            onCreate(sqLiteDatabase,cs);
        } catch (SQLException e) {
            Log.e(TAG,"droptable e...........");
        }
    }

  public Dao<UserProfile,Integer> geUserProfilesDao() throws SQLException{
      if(userProfiles == null){
          userProfiles = getDao(UserProfile.class);
      }
      return userProfiles;
  }
    public Dao<MessageIn, Long> getMessageInsDao() throws SQLException{
        if(messageIns == null){
            messageIns = getDao(MessageIn.class);
        }
        return messageIns;
    }



   /* public RuntimeExceptionDao<MessageIn, String> getRuntimeMessageIns() throws SQLException{
        if(messageInsRuntimeDao == null){
            messageInsRuntimeDao = getRuntimeExceptionDao(MessageIn.class);
        }
        return messageInsRuntimeDao;
    }*/

    public Dao<MessageOut, Long> getMessageOutsDao() throws SQLException{
        if(messageOuts == null){
            messageOuts = getDao(MessageOut.class);
        }
        return messageOuts;
    }

   /* public RuntimeExceptionDao<MessageOut, String> getRuntimeMessageOuts() throws SQLException{
        if(messageOutRuntimeDao == null){
            messageOutRuntimeDao = getRuntimeExceptionDao(MessageOut.class);
        }
        return messageOutRuntimeDao;
    }*/

    @Override
    public void close() {
        super.close();
        messageIns = null;
        messageOuts = null;
        userProfiles=null;
    }
}
