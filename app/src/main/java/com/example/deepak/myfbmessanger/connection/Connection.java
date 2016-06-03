package com.example.deepak.myfbmessanger.connection;

import com.example.deepak.myfbmessanger.db.DBHandler;
import com.example.deepak.myfbmessanger.db.UserProfile;

/**
 * Created by deepak on 15/5/15.
 */
public class Connection {
   static UserProfile userProfile = DBHandler.dbHandler.getuserProfileList();
    public static final String HOST = "chat.facebook.com";
    public static final int PORT = 5222;
    public static final String SERVICE = "facebook.com";
    public static final String USERNAME  = userProfile.getUserEmailid();
    public static final  String PASSWORD = userProfile.getUserPassword();







}
