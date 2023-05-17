package com.nionios.uniwatune.data.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.nionios.uniwatune.data.types.AudioFile;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * @description This is a service in which we store references pertaining the  currently
 * active media player and its queue, etc.
 * If another audio file needs to play, then we release the previous media player, making the
 * new one the current one.
 */
public class MediaPlayerControllerService
        extends Service
        implements MediaPlayer.OnCompletionListener {

    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private static final String ACTION_TOGGLE_PLAY_STATE = "com.example.action.TOGGLE_PLAY_STATE";

    private MediaPlayer CurrentMediaPlayer;
    private AudioFile CurrentAudioFile;
    private Queue<AudioFile> AudioFileQueue = new LinkedList<>();
    public AudioFile getCurrentAudioFile() {
        return CurrentAudioFile;
    }
    public MediaPlayer getCurrentMediaPlayer() {return CurrentMediaPlayer;}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();

        // Action play means the user is trying to play an audio file by clicking (not autoplay)
        if (Objects.equals(intent.getAction(), ACTION_PLAY)) {
            // Check the extras, the path of the audio file should have arrived
            if (extras != null) {
                // Get the audio file path from the extras
                String inputAudioFilePath = (String) extras.get("PATH");
                if (inputAudioFilePath != null) {
                    System.out.println("Playing audio file with URI:" + Uri.parse(inputAudioFilePath));
                    MediaPlayer localMediaPlayer =
                            MediaPlayer.create( this, Uri.parse(inputAudioFilePath));
                    /* Switch out any currently playing mediaPlayer with this one if they exist,
                     * set this one as current. Also set the song as currently loaded on
                     * our MediaPlayer*/
                    setMediaPlayer(localMediaPlayer);
                    //TODO: make this autodetect the queue!
                    // setQueue();
                    // setCurrentAudioFile(inputAudioFile);
                    localMediaPlayer.start();
                }
            }
        }

        if (Objects.equals(intent.getAction(), ACTION_TOGGLE_PLAY_STATE)) {
            if (CurrentMediaPlayer != null) {
                toggleMediaPlayerPlayState();
            } else {
                //TODO something?
            }
        }
        // Sticky service!
        return START_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // Remove audio file that just played (it's in front of the queue)
        AudioFileQueue.remove();
        // Play audio file now in front of the queue, make sure the element is not zero
        try {
            AudioFile nextAudioFileInQueue = AudioFileQueue.peek();
            assert nextAudioFileInQueue != null;
            mediaPlayer.setDataSource(nextAudioFileInQueue.getPath());
            // Set our variable too
            setCurrentAudioFile(nextAudioFileInQueue);
        } catch (IOException e) {
            // Do not actually stop app, just stop playing
            //TODO: let user know?
            System.out.println("END OF QUEUE REACHED!");
            return;
        }
        mediaPlayer.start();
    }

    /* Used when user clicks a file to play manually, cold start (not autoplay from queue) */
    public void coldStartPlayingAudioFile (Context context, AudioFile inputAudioFile) {
        System.out.println("Playing audio file with URI:" + Uri.parse(inputAudioFile.getPath()));
        MediaPlayer localMediaPlayer =
                MediaPlayer.create( context, Uri.parse(inputAudioFile.getPath()) );
        /* Switch out any currently playing mediaPlayer with this one if they exist, set this one
         * as current. Also set the song as currently loaded on our MediaPlayer*/
        setMediaPlayer(localMediaPlayer);
        setCurrentAudioFile(inputAudioFile);
        //TODO: make this autodetect the queue! setQueue();
        localMediaPlayer.start();
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

    public void setQueue (LinkedList<AudioFile> inputAudioFileQueue) {
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

    public void toggleMediaPlayerPlayState () {
        if (CurrentMediaPlayer.isPlaying()) {
            CurrentMediaPlayer.pause();
        } else {
            CurrentMediaPlayer.start();
        }
    }

    public void restartMediaPlayer () {
        if (CurrentMediaPlayer != null) {
            CurrentMediaPlayer.start();
        }
    }

    /** Called when MediaPlayer is ready
    public void onPrepared(MediaPlayer player) {
        player.start();
    }
     */

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
