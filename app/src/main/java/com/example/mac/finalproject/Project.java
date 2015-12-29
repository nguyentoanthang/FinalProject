package com.example.mac.finalproject;

import java.util.Date;

public class Project {

    private String name;

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
}
