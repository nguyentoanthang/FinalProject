package com.example.mac.finalproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class WorkDetailAdapter extends ArrayAdapter<String> {

    private Activity context;
    private ArrayList<String> list;
    private int layoutId;

    public WorkDetailAdapter(Activity ctx, ArrayList<String> detail, int layoutId) {
        super(ctx, layoutId, detail);
        this.context = ctx;
        this.list = detail;
        this.layoutId = layoutId;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();

        convertView = layoutInflater.inflate(layoutId, null);

        TextView detail = (TextView) convertView.findViewById(R.id.detail_work);
        TextView title = (TextView) convertView.findViewById(R.id.title_detail);
        ImageView edit = (ImageView) convertView.findViewById(R.id.editable);

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
                title.setText("Done:");
                edit.setVisibility(View.GONE);
                break;
            case 4:
                title.setText("Start on:");
                edit.setVisibility(View.GONE);
                break;
            case 5:
                title.setText("Finish on:");
                edit.setVisibility(View.VISIBLE);
                break;
        }

        detail.setText(list.get(position));

        return convertView;
    }

}
