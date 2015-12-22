package com.example.mac.finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/*
 * Class to check internet connection
 */
public class NetworkStateChange extends BroadcastReceiver {

    private boolean isConnected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        isNetworkAvailable(context);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            Toast.makeText(context, "Network is connected", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }
}
