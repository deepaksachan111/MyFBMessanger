package com.example.deepak.myfbmessanger.Message;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by deepak on 5/6/15.
 */

@DatabaseTable(tableName = "MessageOut")
public class MessageOut implements MessageCompare{
    public static String FIELD_TO_USER_NAME = "toUserName";
    public static String FIELD_IS_SENT = "isSent";

    @DatabaseField(id =  true)
    private Long timestamp;

    @DatabaseField
    private String message;

    @DatabaseField
    private String toUserName;

    @DatabaseField
    private String toId;

    @DatabaseField(index = true)
    private  Boolean isSent;

    public MessageOut(Long timestamp, String message, String toUserName, String toId, Boolean isSent) {
        this.timestamp = timestamp;
        this.message = message;
        this.toUserName = toUserName;
        this.toId = toId;
        this.isSent = isSent;
    }

    public MessageOut() {
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public Boolean getIsSent() {
        return isSent;
    }

    public void setIsSent(Boolean isSent) {
        this.isSent = isSent;
    }

    @Override
    public int compareTo(MessageCompare messageCompare) {
        if(messageCompare == null){
            return 0;
        }
        return timestamp > messageCompare.getTimestamp() ? 1 : -1;
    }
}
