package com.example.mac.finalproject;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ProjectAdapter extends ArrayAdapter<Project> {

    private Activity context = null;
    private int layoutId;
    ArrayList<Project> lisProject = null;

    public ProjectAdapter(Activity context, int layoutId, ArrayList<Project> list) {
        super(context, layoutId, list);
        this.context = context;
        this.layoutId = layoutId;
        this.lisProject = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();

        convertView = layoutInflater.inflate(layoutId, null);

        final TextView name = (TextView) convertView.findViewById(R.id.nameProject);

        final Project currentProject = lisProject.get(position);

        name.setText(currentProject.getName());

        //LinearLayout container = (LinearLayout) convertView.findViewById(R.id.progess);
        //ImageView img = new ImageView(context);
        //container.addView(img);

        CircleImageView host = (CircleImageView) convertView.findViewById(R.id.host);



        return convertView;

    }
}
