package com.example.mac.finalproject;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by mac on 12/10/15.
 */
public class FinalProject extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "jJ8QrqEcY6pcPWkpcFM72gAH8DRCQ2OL8yltmsif", "MVCSDgySMy6h71zkLFSKlLURqHgiJT4WvH3dPGbH");
    }
}
