package com.example.mac.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class RecieveNotification extends ParsePushBroadcastReceiver {

    public RecieveNotification() {
        super();
    }

    void showAlertDialog() {

    }

    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        if (intent == null) {
            return;
        }

        try {
            JSONObject data = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            if (data.getString("title").equals("Invite")) {
                ParseUser.getCurrentUser().increment("Badge");
                ParseUser.getCurrentUser().saveInBackground();
            } else {

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
}
