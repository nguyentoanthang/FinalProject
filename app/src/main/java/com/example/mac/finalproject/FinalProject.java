package com.example.mac.finalproject;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseInstallation;

public class FinalProject extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "jJ8QrqEcY6pcPWkpcFM72gAH8DRCQ2OL8yltmsif", "MVCSDgySMy6h71zkLFSKlLURqHgiJT4WvH3dPGbH");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
