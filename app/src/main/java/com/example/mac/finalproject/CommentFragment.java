package com.example.mac.finalproject;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentFragment extends Fragment {

    private CommentAdapter adapter;
    private ArrayList<Comment> list = null;
    private boolean hide = true;
    @Bind(R.id.listCmt) ListView lv;
    @Bind(R.id.input_cmt) EditText edt;
    @Bind(R.id.send) ImageButton send;
    private String workId;

    public void setData(ArrayList<Comment> list, String workId) {
        this.list = list;
        this.workId = workId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.comment_fragment, container, false);
        ButterKnife.bind(this, v);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmt = edt.getText().toString();
                edt.setText("");
                Comment newComment = new Comment();
                newComment.setCmt(cmt);
                newComment.setName(ParseUser.getCurrentUser().getString("Name"));
                ParseFile fileImage = (ParseFile) ParseUser.getCurrentUser().get("avatar");
                if (fileImage != null) {
                    try {
                        byte[] data = fileImage.getData();
                        newComment.setHost(BitmapFactory.decodeByteArray(data, 0, data.length));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        newComment.setHost(BitmapFactory.decodeResource(getResources(), R.drawable.profile_image));
                    }
                } else {
                    newComment.setHost(BitmapFactory.decodeResource(getResources(), R.drawable.profile_image));
                }
                list.add(newComment);
                ParseObject object = new ParseObject("Comment");
                object.put("forWork", workId);
                object.put("ofUser", ParseUser.getCurrentUser().getObjectId());
                object.put("Comment", cmt);
                object.saveInBackground();
                RefreshData();

            }
        });
        adapter = new CommentAdapter(getActivity(), list, R.layout.cmt_row);
        lv.setAdapter(adapter);

        return v;
    }


    public boolean isHide() {
        return hide;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hide = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        hide = false;
    }

    public void RefreshData() {
        adapter.notifyDataSetChanged();
    }

    public void Update(Comment comment) {
        this.list.add(comment);
    }
}
