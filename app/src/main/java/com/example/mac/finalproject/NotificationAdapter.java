package com.example.mac.finalproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;

public class NotificationAdapter extends ArrayAdapter<ParseObject> {

    private Activity context;
    private ArrayList<ParseObject> listNotify;
    private int layoutId;

    public NotificationAdapter(Activity ctx, ArrayList<ParseObject> detail, int layoutId) {
        super(ctx, layoutId, detail);
        this.context = ctx;
        this.listNotify = detail;
        this.layoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();

        convertView = layoutInflater.inflate(layoutId, null);

        TextView message = (TextView) convertView.findViewById(R.id.message);

        message.setText(listNotify.get(position).getString("Message"));

        return convertView;

    }
}

