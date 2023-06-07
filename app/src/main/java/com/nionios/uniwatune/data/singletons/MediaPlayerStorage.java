package com.nionios.uniwatune.data.singletons;

import android.media.MediaPlayer;

public class MediaPlayerStorage {
    private static MediaPlayerStorage MEDIA_PLAYER_INSTANCE;
    private MediaPlayer StoredMediaPlayer;

    public static MediaPlayerStorage getInstance() {
        if (MEDIA_PLAYER_INSTANCE == null) {
            MEDIA_PLAYER_INSTANCE = new MediaPlayerStorage();
        }
        return MEDIA_PLAYER_INSTANCE;
    }

    public void setMediaPlayer (MediaPlayer inputMediaPlayer) {
        StoredMediaPlayer = inputMediaPlayer;
    }

    public MediaPlayer getMediaPlayer () {
        return StoredMediaPlayer;
    }

}
