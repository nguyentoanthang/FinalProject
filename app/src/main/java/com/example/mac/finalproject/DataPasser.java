package com.example.mac.finalproject;

import java.io.Serializable;
import java.util.ArrayList;

public class DataPasser implements Serializable {

    private ArrayList<Project> listProject;

    public DataPasser() {

    }


    public ArrayList<Project> getListProject() {
        return listProject;
    }

    public void setListProject(ArrayList<Project> listProject) {
        this.listProject = listProject;
    }
}
