package com.example.mac.finalproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MemberAdapter extends ArrayAdapter<Member> {

    private Activity context = null;
    private int layoutId;
    ArrayList<Member> lisMember = null;

    public MemberAdapter(Activity context, int layoutId, ArrayList<Member> list) {
        super(context, layoutId, list);
        this.context = context;
        this.layoutId = layoutId;
        this.lisMember = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Member currentMember = lisMember.get(position);

        LayoutInflater inflater = context.getLayoutInflater();

        convertView = inflater.inflate(layoutId, null);

        CircleImageView avatar = (CircleImageView) convertView.findViewById(R.id.avatar);

        TextView name = (TextView) convertView.findViewById(R.id.name);

        avatar.setImageBitmap(currentMember.getAvatar());

        name.setText(currentMember.getName());

        return convertView;
    }
}
