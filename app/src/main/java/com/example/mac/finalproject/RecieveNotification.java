package com.example.mac.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecieveNotification extends ParsePushBroadcastReceiver {

    public RecieveNotification() {
        super();
    }

    void showAlertDialog() {

    }

    @Override
    protected Bitmap getLargeIcon(Context context, Intent intent) {
        if (intent == null) {
            return null;
        }

        try {
            JSONObject data = new JSONObject((intent.getExtras().getString("com.parse.Data")));

            Bitmap bitmap = getAvatarOfUser(context, data.getString("sender"));

            return bitmap;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image);
    }

    @Override
    protected int getSmallIconId(Context context, Intent intent) {

        return R.drawable.logo;
    }

    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        if (intent == null) {
            return;
        }

        try {

            JSONObject data = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            if (data.getString("title").equals("Invite")) {
                getLargeIcon(context, intent);
                ParseUser.getCurrentUser().increment("Badge");
                ParseUser.getCurrentUser().saveInBackground();
            } else if (data.getString("title").equals("Delete")){
                List<String> list = new ArrayList<>();
                list.add(data.getString("id"));
                Collection<String> l = list;
                ParseUser.getCurrentUser().removeAll("Project", l);
                ParseUser.getCurrentUser().saveInBackground();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);

        if (intent == null) {
            return;
        } else {

        }

    }

    public Bitmap getAvatarOfUser(Context context, String userId) {

        ParseUser user;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", userId);

        try {
            user = query.find().get(0);
            ParseFile imageFile = (ParseFile) user.get("avatar");
            if (imageFile != null) {
                try {
                    byte[] data = imageFile.getData();
                    return BitmapFactory.decodeByteArray(data, 0, data.length);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image);
                }
            } else {
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image);
    }
}
