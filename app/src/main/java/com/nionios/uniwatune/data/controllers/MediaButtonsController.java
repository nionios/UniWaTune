package com.nionios.uniwatune.data.controllers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.nionios.uniwatune.R;
import com.nionios.uniwatune.ui.player.PlayerViewModel;

/**
 * A controller to change all the UI buttons to reflect real time state of media player
 */
public class MediaButtonsController {
    public void togglePlayButtons(Context context) {
        Context applicationContext = context.getApplicationContext();
        // Initialize MediaPlayerController obj to see if song is playing
        MediaPlayerController localMediaPlayerController = new MediaPlayerController();
        Drawable appropriateDrawableOnClick;
        Animation quickPulseAnimation =
                AnimationUtils.loadAnimation(applicationContext, R.anim.quickpulse);
        if (localMediaPlayerController.isAudioPlaying()) {
            appropriateDrawableOnClick = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.baseline_play_circle_24
            );
        } else {
            appropriateDrawableOnClick = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.baseline_pause_circle_24
            );
        }
        View rootView = View.inflate(applicationContext, R.layout.audio_player_notification, null);
        ImageButton imgBtn = rootView.findViewById(R.id.notification_play_button);
        imgBtn.startAnimation(quickPulseAnimation);
        imgBtn.setImageDrawable(appropriateDrawableOnClick);

    }
}
