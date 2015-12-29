package com.example.mac.finalproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WorkAdapter extends ArrayAdapter<Work> {

    private Activity context = null;
    private ArrayList<Work> listWork = null;
    private int layoutId;

    public WorkAdapter(Activity context, int layoutId, ArrayList<Work> list) {
        super(context, layoutId, list);
        this.context = context;
        this.layoutId = layoutId;
        this.listWork = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();

        convertView = layoutInflater.inflate(layoutId, null);

        final TextView name = (TextView) convertView.findViewById(R.id.nameOfWork);

        final Work currentWork = listWork.get(position);

        name.setText(currentWork.getName());

        return convertView;

    }
}
