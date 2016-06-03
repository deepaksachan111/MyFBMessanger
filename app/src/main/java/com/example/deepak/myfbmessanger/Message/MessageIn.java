package com.example.deepak.myfbmessanger.Message;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by deepak on 5/6/15.
 */

@DatabaseTable(tableName = "MessageIn")
public class MessageIn implements MessageCompare {
    public static String TAG = MessageIn.class.getSimpleName();
    public static String FIELD_FROM_USER_ID = "fromId";

    @DatabaseField(id =  true)
    private Long timestamp;

    @DatabaseField
    private String message;


    @DatabaseField
    private String fromUserName;

    @DatabaseField(index = true)
    private String fromId;

    public MessageIn(Long timestamp, String message, String fromUserName, String fromId) {
        this.timestamp = timestamp;
        this.message = message;
        this.fromUserName = fromUserName;
        this.fromId = fromId;
    }

    public MessageIn(){
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestampIn(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageIn() {
        return message;
    }

    public void setMessageIn(String message) {
        this.message = message;
    }

    public String getFromUserNameIn() {
        return fromUserName;
    }

    public void setFromUserNameIn(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromIdIn() {
        return fromId;
    }

    public void setFromIdIn(String fromId) {
        this.fromId = fromId;
    }


    @Override
    public int compareTo(MessageCompare messageCompare) {
        return timestamp > messageCompare.getTimestamp() ? 1 : -1;
    }

}
