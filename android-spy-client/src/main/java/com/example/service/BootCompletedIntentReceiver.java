package com.example.service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import  com.example.service.Camera2Service;

public class BootCompletedIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // throw new UnsupportedOperationException("Not yet implemented");
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, Camera2Service.class);
            context.startService(pushIntent);
        }

        if ("bc".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, Camera2Service.class);
            context.startService(pushIntent);
        } else {}
    }
}
