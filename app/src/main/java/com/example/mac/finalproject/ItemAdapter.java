package com.example.mac.finalproject;


import android.app.Activity;
import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<String>{

    private Activity context;
    private ArrayList<String> Listdetail;
    private int layoutId;

    public ItemAdapter(Activity ctx, ArrayList<String> detail, int layoutId) {
        super(ctx, layoutId, detail);
        this.context = ctx;
        this.Listdetail = detail;
        this.layoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();

        convertView = layoutInflater.inflate(layoutId, null);

        TextView detail = (TextView) convertView.findViewById(R.id.detail);
        ImageView icon = (ImageView) convertView.findViewById(R.id.iconforprofile);

        switch (position) {
            case 0:
                icon.setImageResource(R.drawable.name);
                break;
            case 1:
                icon.setImageResource(R.drawable.email);
                break;
            case 2:
                icon.setImageResource(R.drawable.phone);
                break;
        }

        detail.setText(Listdetail.get(position));

        return convertView;

    }
}
