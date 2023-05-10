package com.nionios.uniwatune.data.singletons;

import android.media.MediaPlayer;

/**
 * @description This is a singleton in which we store the currently active media player.
 * If another audio file needs to play, then we release the previous media player, making the
 * new one the current one.
 */
public class MediaPlayerController {
    private static MediaPlayerController MEDIA_PLAYER_CONTROLLER_INSTANCE;

    private MediaPlayer CurrentMediaPlayer;

    private MediaPlayerController() {}

    public static MediaPlayerController getInstance() {
        if(MEDIA_PLAYER_CONTROLLER_INSTANCE == null) {
            MEDIA_PLAYER_CONTROLLER_INSTANCE = new MediaPlayerController();
        }
        return MEDIA_PLAYER_CONTROLLER_INSTANCE;
    }


    // If there is a mediaPlayer active, release it and set the new one as current.
    public void setMediaPlayer (MediaPlayer inputMediaPlayer) {
        if (CurrentMediaPlayer == null) CurrentMediaPlayer = inputMediaPlayer;
        else {
            CurrentMediaPlayer.release();
            CurrentMediaPlayer = null;
            this.setMediaPlayer(inputMediaPlayer);
        }
    }
}
