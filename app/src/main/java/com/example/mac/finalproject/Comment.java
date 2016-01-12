package com.example.mac.finalproject;

import android.graphics.Bitmap;

public class Comment {

    private String cmt;

    private Bitmap host;

    private String name;

    private long time;

    public String getCmt() {
        return cmt;
    }

    public void setCmt(String cmt) {
        this.cmt = cmt;
    }

    public Bitmap getHost() {
        return host;
    }

    public void setHost(Bitmap host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
