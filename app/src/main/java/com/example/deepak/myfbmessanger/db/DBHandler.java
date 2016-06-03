package com.example.deepak.myfbmessanger.db;

import android.content.Context;
import android.util.Log;

import com.example.deepak.myfbmessanger.Message.MessageIn;
import com.example.deepak.myfbmessanger.Message.MessageOut;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by deepak on 7/6/15.
 */
public class DBHandler {

    private static final String TAG = DBHandler.class.getSimpleName();
    //  private static final Long LIMIT_ONE = 1L;
    public static DBHandler dbHandler;
    private DbHelper dbHelper = null;
    private Context context;

    private DBHandler(DbHelper databaseHelper, Context context) {
        this.dbHelper = databaseHelper;
        this.context = context;
    }

    public static void start(Context context) {
        if (dbHandler == null) {
            synchronized (DBHandler.class) {
                if (dbHandler == null) {
                    synchronized (DBHandler.class) {
                        DbHelper Dbhelper1 = OpenHelperManager.getHelper(context, DbHelper.class);
                        dbHandler = new DBHandler(Dbhelper1, context);
                    }
                }
            }
        }
    }


    public static void startIfNotStarted(Context context){
        if(dbHandler == null){
            start(context);
        }
    }

    public boolean saveUserProfile(UserProfile userProfile) {
        try {
            return dbHelper.geUserProfilesDao().createOrUpdate(userProfile).getNumLinesChanged() == 1;
        } catch (SQLException e) {
            Log.e(TAG, " save UserProfile......................");
            return false;
        }

    }

    public boolean saveMessagesOut(MessageOut messageOut) {
        try {
            return dbHelper.getMessageOutsDao().createOrUpdate(messageOut).getNumLinesChanged() == 1;
        } catch (SQLException e) {
            Log.e(TAG, " save  Mesage outing......................");
            return false;
        }
    }

    public UserProfile getuserProfileList() {
        try {
            return dbHelper.geUserProfilesDao().queryForId(1);
        } catch (Exception e) {
            Log.e(TAG, " getUserProfile..................");
            return null;
        }
    }

    public void delete() {
        try {
           int k =  dbHelper.geUserProfilesDao().deleteById(1);
            Log.i("",k+"");
        } catch (Exception e) {
            Log.e(TAG, " delete UserProfile..................");
        }
    }


    public boolean saveMessagesIn(MessageIn messageIn) {
        try {
            return dbHelper.getMessageInsDao().createOrUpdate(messageIn).getNumLinesChanged() == 1;
        } catch (SQLException e) {
            Log.e(TAG, " save Mesage Incoming......................");
            return false;
        }
    }

    public List<MessageIn> getMessageIns(String userId) {
        try {
            return dbHelper.getMessageInsDao().queryForEq(MessageIn.FIELD_FROM_USER_ID, userId);
        } catch (Exception e) {
            Log.e(TAG, " getMessages incoming..................");
            return null;
        }
    }


    public List<MessageOut> getMessageOuts(String userName) {
        try {
            return dbHelper.getMessageOutsDao().queryForEq(MessageOut.FIELD_TO_USER_NAME, userName);
        } catch (Exception e) {
            Log.e(TAG, " getMessage outgoing.........");
            return null;
        }
    }

    public List<MessageOut> getMessageOutsNotSent() {
        try {
            return dbHelper.getMessageOutsDao().queryForEq(MessageOut.FIELD_IS_SENT, false);
        } catch (Exception e) {
            Log.e(TAG, " getMessage outgoing.........");
            return null;
        }
    }


}






