package com.example.mac.finalproject;

import java.util.Date;

public class Work {

    private boolean forCurrentUser;

    private boolean permission;

    private String id;

    private boolean done;

    private String name;

    private int numberOfMember;

    private Date deadLine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfMember() {
        return numberOfMember;
    }

    public void setNumberOfMember(int numberOfMember) {
        this.numberOfMember = numberOfMember;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Date getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine;
    }

    public boolean isForCurrentUser() {
        return forCurrentUser;
    }

    public void setForCurrentUser(boolean forCurrentUser) {
        this.forCurrentUser = forCurrentUser;
    }

    public boolean isPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }
}
