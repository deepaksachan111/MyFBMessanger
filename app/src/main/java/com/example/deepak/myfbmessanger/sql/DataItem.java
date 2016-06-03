package com.example.deepak.myfbmessanger.sql;

/**
 * Created by deepak on 30/5/15.
 */
public class DataItem {

   private  String name;
    private int image;
    public DataItem(String name, int image){
        this.image= image;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
