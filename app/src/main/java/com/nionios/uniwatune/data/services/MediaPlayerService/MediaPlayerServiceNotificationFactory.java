package com.nionios.uniwatune.data.services.MediaPlayerService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;

import com.nionios.uniwatune.R;
import com.nionios.uniwatune.data.singletons.AudioQueueStorage;
import com.nionios.uniwatune.data.types.AudioFile;

import java.net.ConnectException;

public class MediaPlayerServiceNotificationFactory {
    private static final String CHANNEL_DEFAULT_IMPORTANCE = "MediaPlayerServiceNotificationChannel";

    /**
     * @description This is where we make our notification with the info of the song
     * @param context
     * @return
     */
    public Notification makeNotification (Context context) {
        PendingIntent pendingIntent = makePendingIntent(context);
        // Build the notification
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Get the layout to use in the custom notification
            RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.audio_player_notification);
            // RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);
            // Now get the song and set the images and text on the notification appropriately.
            AudioQueueStorage localAudioQueueStorage = AudioQueueStorage.getInstance();
            AudioFile currentAudioFile = localAudioQueueStorage.getAudioQueue().peek();
            notificationLayout.setTextViewText(R.id.notification_title_text_view,  currentAudioFile.getName());
            notificationLayout.setTextViewText(R.id.notification_artist_text_view, currentAudioFile.getArtist());
            notificationLayout.setImageViewBitmap(R.id.notification_album_image,   currentAudioFile.getAlbumArt());

            notification = new Notification.Builder(context, CHANNEL_DEFAULT_IMPORTANCE)
                    // .setContentTitle(context.getText(R.string.notification_title))
                    // .setContentText(context.getText(R.string.notification_message))
                    .setContentIntent(pendingIntent)
                    .setTicker(context.getText(R.string.ticker_text))
                    .setCustomContentView(notificationLayout)
                    .setSmallIcon(R.drawable.baseline_audio_file_24)
                    // .setCustomBigContentView(notificationLayoutExpanded)
                    .build();
        }
        return notification;
    }

    /**
     * @description This is where we make our placeholder notification until the song is on the
     * mediaPlayer, then replace with real notification.
     * @param context
     * @return
     */
    public Notification makePlaceHolderNotification (Context context) {
        PendingIntent pendingIntent = makePendingIntent(context);
        // Build the notification
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(context, CHANNEL_DEFAULT_IMPORTANCE)
                    .setContentTitle(context.getText(R.string.notification_title))
                    .setContentText(context.getText(R.string.notification_message))
                    .setSmallIcon(R.drawable.baseline_audio_file_24)
                    .setContentIntent(pendingIntent)
                    .setTicker(context.getText(R.string.ticker_text))
                    .build();
        }
        return notification;
    }

    public PendingIntent makePendingIntent (Context context) {
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
        return pendingIntent;
    }
}
