package com.example.deepak.myfbmessanger.db;

/**
 * Created by deepak on 19/5/15.
 */
public class Data {
    private String id;
    private String username;
    private boolean online;


    public Data(String username,String id, boolean online) {
        this.username = username;
        this.id = id;
        this.online = online;


    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

   /* public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getImage(){
        return image;
    }*/
}
