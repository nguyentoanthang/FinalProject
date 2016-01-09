package com.example.mac.finalproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<Comment> {

    private Activity context;
    private ArrayList<Comment> listComment;
    private int layoutId;

    public CommentAdapter(Activity ctx, ArrayList<Comment> list, int layoutId) {
        super(ctx,layoutId, list);
        this.context = ctx;
        this.listComment = list;
        this.layoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        convertView = inflater.inflate(layoutId, null);

        CircleImageView avatar = (CircleImageView) convertView.findViewById(R.id.avatar);
        TextView name = (TextView) convertView.findViewById(R.id.nameofcomment);
        TextView comment = (TextView) convertView.findViewById(R.id.comment);

        Comment cmt = listComment.get(position);

        avatar.setImageBitmap(cmt.getHost());
        name.setText(cmt.getName());
        comment.setText(cmt.getCmt());

        return convertView;

    }
}
