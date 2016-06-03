package com.example.deepak.myfbmessanger.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by deepak on 11/6/15.
 */
@DatabaseTable(tableName = "UserProfile")
public class UserProfile {
 public static final String TAG= UserProfile.class.getSimpleName();
    public static final String FIELD_ID ="id";
    @DatabaseField(id =  true)
    private int id;
    @DatabaseField
    private String UserEmailid ;
    @DatabaseField
    private String UserPassword;


    public UserProfile(String UserEmailid,String UserPassword){
        this.id = 1;
        this.UserEmailid = UserEmailid;
        this.UserPassword = UserPassword;
    }
public UserProfile(){

}
    public String getUserEmailid() {
        return UserEmailid;
    }
    public void setUserEmailid(String userEmailid) {
        UserEmailid = userEmailid;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }


}
