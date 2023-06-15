package com.nionios.uniwatune.data.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver to implement media buttons on notification
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationBroadcastReceiver";
    public static final int REQUEST_CODE_NOTIFICATION = 1111;
    public static final String NOTIFICATION_ACTION_TOGGLE = "NOTIFICATION_ACTION_TOGGLE";

    @Override
    public void onReceive(Context context, Intent intent) {

        StringBuilder sb = new StringBuilder();
        sb.append("Action: " + intent.getAction() + "\n");
        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
        String log = sb.toString();
        System.out.println(TAG + log);
    }
}
