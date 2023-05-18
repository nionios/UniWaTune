package com.nionios.uniwatune.data.services.MediaPlayerService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.nionios.uniwatune.R;

public class MediaPlayerServiceNotificationFactory {
    private static final String CHANNEL_DEFAULT_IMPORTANCE = "MediaPlayerServiceNotificationChannel";

    public Notification makeNotification (Context context) {
        // Create NotificationChannel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_DEFAULT_IMPORTANCE,
                    "Foreground Service of player of UniWaTunes",
                    NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(notificationChannel);
        }
        /* We need a notification, since this is a foreground activity after all */
        // If the notification supports a direct reply action, use
        // PendingIntent.FLAG_MUTABLE instead.
        Intent notificationIntent = new Intent(context, MediaPlayerService.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);
        // Build the notification
        Notification notification = null;
        // TODO: Set Large Icon and make it bigger and with buttons, look into categories for notifs
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(context, CHANNEL_DEFAULT_IMPORTANCE)
                    .setContentTitle(context.getText(R.string.notification_title))
                    .setContentText(context.getText(R.string.notification_message))
                    // TODO: album art here
                    .setSmallIcon(R.drawable.baseline_audio_file_24)
                    .setContentIntent(pendingIntent)
                    .setTicker(context.getText(R.string.ticker_text))
                    .build();
        }
        return notification;
    }
}
