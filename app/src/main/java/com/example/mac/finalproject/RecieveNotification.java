package com.example.mac.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

public class RecieveNotification extends ParsePushBroadcastReceiver {

    public RecieveNotification() {
        super();
    }

    void showAlertDialog() {

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
