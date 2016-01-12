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
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentFragment extends Fragment {

    private CommentAdapter adapter;
    private ArrayList<Comment> list = null;
    private boolean hide = true;
    @Bind(R.id.listCmt) ListView lv;
    @Bind(R.id.input_cmt) EditText edt;
    @Bind(R.id.send) ImageButton send;
    private Work w;

    public void setData(ArrayList<Comment> list, Work w) {
        this.list = list;
        this.w = w;
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
                Date d1 = new Date();
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
                Date d2 = new Date();
                newComment.setTime((d2.getTime() - d1.getTime())/1000);
                list.add(newComment);
                ParseObject object = new ParseObject("Comment");
                object.put("forWork", w.getId());
                object.put("ofUser", ParseUser.getCurrentUser().getObjectId());
                object.put("Comment", cmt);
                object.saveInBackground();
                RefreshData();

                ParseQuery<ParseObject> obs = ParseQuery.getQuery("Work");

                obs.whereEqualTo("objectId", w.getId());
                try {

                    ParseObject ob = obs.find().get(0);
                    List<String> l = ob.getList("ListMember");
                    l.remove(ParseUser.getCurrentUser().getEmail());

                    Collection<String> list_member = l;
                    ParseQuery pushQuery = ParseInstallation.getQuery();
                    pushQuery.whereContainedIn("UserEmail", list_member);

                    JSONObject data = new JSONObject();
                    try {
                        data.put("title", "Comment");
                        data.put("alert", "I comment in a work");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ParsePush push = new ParsePush();
                    push.setData(data);
                    push.setQuery(pushQuery);
                    push.sendInBackground();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


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
