package com.nionios.uniwatune.data.singletons;

import android.media.MediaPlayer;
import android.provider.MediaStore;

import com.nionios.uniwatune.data.types.AudioFile;

import java.util.ArrayList;

/**
 * @description This is a singleton in which we store the currently active media player.
 * If another audio file needs to play, then we release the previous media player, making the
 * new one the current one.
 */
public class MediaPlayerController {
    private static MediaPlayerController MEDIA_PLAYER_CONTROLLER_INSTANCE;

    private MediaPlayer CurrentMediaPlayer;
    private AudioFile CurrentAudioFile;
    private ArrayList<AudioFile> AudioFileQueue = new ArrayList<AudioFile>();

    private MediaPlayerController() {}

    public static MediaPlayerController getInstance() {
        if(MEDIA_PLAYER_CONTROLLER_INSTANCE == null) {
            MEDIA_PLAYER_CONTROLLER_INSTANCE = new MediaPlayerController();
        }
        return MEDIA_PLAYER_CONTROLLER_INSTANCE;
    }

    public AudioFile getCurrentAudioFile() {
        return CurrentAudioFile;
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

    public void setCurrentAudioFile (AudioFile inputAudioFile) {
        CurrentAudioFile = inputAudioFile;
    }

    public void setQueue (ArrayList<AudioFile> inputAudioFileQueue) {
        AudioFileQueue = inputAudioFileQueue;
    }

    public void addToQueue (AudioFile inputAudioFile) {
        AudioFileQueue.add(inputAudioFile);
    }

    public void pauseMediaPlayer () {
        if (CurrentMediaPlayer != null) {
            CurrentMediaPlayer.pause();
        }
    }

    public void restartMediaPlayer () {
        if (CurrentMediaPlayer != null) {
            CurrentMediaPlayer.start();
        }
    }

}
