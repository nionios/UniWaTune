package com.nionios.uniwatune.data.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

import com.nionios.uniwatune.R;
import com.nionios.uniwatune.data.controllers.MediaPlayerController;

/**
 * Broadcast receiver to implement media buttons on notification.
 * This class basically relays the intents that it receives to our MediaPlayerService through our
 * MediaPlayerServiceController.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationBroadcastReceiver";
    public static final int REQUEST_CODE_NOTIFICATION = 1111;
    public static final String NOTIFICATION_ACTION_TOGGLE = "NOTIFICATION_ACTION_TOGGLE";
    public static final String NOTIFICATION_ACTION_NEXT = "NOTIFICATION_ACTION_NEXT";
    public static final String NOTIFICATION_ACTION_PREVIOUS = "NOTIFICATION_ACTION_PREVIOUS";

    @Override
    public void onReceive(Context context, Intent intent) {
        Context applicationContext = context.getApplicationContext();
        // Depending on the action received from the intent, command media player accordingly.
        if (intent.getAction() != null) {
            MediaPlayerController localMediaPlayerController = new MediaPlayerController();
            switch (intent.getAction()) {
                case NOTIFICATION_ACTION_TOGGLE:
                    /* Use application context because broadcast receivers are not allowed to bind
                     * to services, which happens in localMediaPlayerController methods here */
                    localMediaPlayerController.toggleCurrentlyPlayingAudioFilePlayState(applicationContext);
                    break;
                case NOTIFICATION_ACTION_NEXT:
                    localMediaPlayerController.playNextAudioFile(applicationContext);
                    break;
                case NOTIFICATION_ACTION_PREVIOUS:
                    localMediaPlayerController.playPreviousAudioFile(applicationContext);
                    break;
            }
        }
    }
}
