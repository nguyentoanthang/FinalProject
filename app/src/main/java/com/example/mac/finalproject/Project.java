package com.example.mac.finalproject;

import android.graphics.Bitmap;

import com.parse.ParseUser;

import java.util.Date;

public class Project {

    private Bitmap host;

    private String name;

    private boolean permission;

    private int numOfMember;

    private int numOfWork;

    private Date createDate;

    private int numOfDone;

    public Project() {

    }
    public Project(String name, int numOfDone) {
        this.name = name;
        this.numOfDone = numOfDone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfDone() {
        return numOfDone;
    }

    public void setNumOfDone(int numOfDone) {
        this.numOfDone = numOfDone;
    }

    public Bitmap getHost() {
        return host;
    }

    public void setHost(Bitmap host) {
        this.host = host;
    }

    public int getNumOfWork() {
        return numOfWork;
    }

    public void setNumOfWork(int numOfWork) {
        this.numOfWork = numOfWork;
    }

    public int getNumOfMember() {
        return numOfMember;
    }

    public void setNumOfMember(int numOfMember) {
        this.numOfMember = numOfMember;
    }

    public boolean isPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }
}
