package com.example.mac.finalproject;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProjectDetailAdapter extends ArrayAdapter<String> {


    private Activity context;
    private ArrayList<String> list;
    private int layoutId;
    private boolean permission;

    public ProjectDetailAdapter(Activity ctx, ArrayList<String> detail, int layoutId, boolean permission) {
        super(ctx, layoutId, detail);
        this.context = ctx;
        this.list = detail;
        this.layoutId = layoutId;
        this.permission = permission;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();

        convertView = layoutInflater.inflate(layoutId, null);

        TextView detail = (TextView) convertView.findViewById(R.id.detail_project);
        TextView title = (TextView) convertView.findViewById(R.id.title_detail);
        ImageView edit = (ImageView) convertView.findViewById(R.id.editable);

        if (getInches() > 5.5) {
            if (permission) {
                switch (position) {
                    case 0:
                        title.setText("Name:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 1:
                        title.setText("Description:");
                        edit.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        title.setText("Number of member:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 3:
                        title.setText("Number of task:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 4:
                        title.setText("Number of done task:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 5:
                        title.setText("Start on:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 6:
                        title.setText("Finish on:");
                        edit.setVisibility(View.VISIBLE);
                        break;
                }
            } else {
                switch (position) {
                    case 0:
                        title.setText("Name:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 1:
                        title.setText("Description:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 2:
                        title.setText("Number of member:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 3:
                        title.setText("Number of task:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 4:
                        title.setText("Number of done task:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 5:
                        title.setText("Start on:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 6:
                        title.setText("Finish on:");
                        edit.setVisibility(View.GONE);
                        break;
                }
            }
        } else {
            if (permission) {
                switch (position) {
                    case 0:
                        title.setText("Name:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 1:
                        title.setText("Descrip:");
                        edit.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        title.setText("Member:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 3:
                        title.setText("Task:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 4:
                        title.setText("Done task:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 5:
                        title.setText("Start on:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 6:
                        title.setText("Finish on:");
                        edit.setVisibility(View.VISIBLE);
                        break;
                }
            } else {
                switch (position) {
                    case 0:
                        title.setText("Name:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 1:
                        title.setText("Descrip:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 2:
                        title.setText("Member:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 3:
                        title.setText("Task:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 4:
                        title.setText("Done task:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 5:
                        title.setText("Start on:");
                        edit.setVisibility(View.GONE);
                        break;
                    case 6:
                        title.setText("Finish on:");
                        edit.setVisibility(View.GONE);
                        break;
                }
            }
        }

        detail.setText(list.get(position));

        return convertView;
    }

    private double getInches() {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        int dens=dm.densityDpi;
        double wi=(double)width/(double)dens;
        double hi=(double)height/(double)dens;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi, 2);
        return Math.sqrt(x+y);
    }
}
