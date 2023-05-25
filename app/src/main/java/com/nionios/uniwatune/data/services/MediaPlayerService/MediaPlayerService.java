package com.nionios.uniwatune.data.services.MediaPlayerService;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.nionios.uniwatune.data.singletons.AudioQueueStorage;
import com.nionios.uniwatune.data.singletons.AudioScanned;
import com.nionios.uniwatune.data.types.AudioFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Queue;

/**
 * @description This is a service in which we store references pertaining the  currently
 * active media player and its queue, etc.
 * This service can (and will be) started and bound too.
 * If another audio file needs to play, then we release the previous media player, making the
 * new one the current one.
 */
public class MediaPlayerService
        extends Service
        implements MediaPlayer.OnCompletionListener {

    private static final String ACTION_PLAY = "com.uniwatune.action.PLAY";
    private static final String ACTION_TOGGLE_PLAY_STATE = "com.uniwatune.action.TOGGLE_PLAY_STATE";
    private static final String ACTION_GET_CURRENT_SONG =
            "com.uniwatune.action.ACTION_GET_CURRENT_SONG";
    private static final int ONGOING_NOTIFICATION_ID = 13;
    // Binder for giving info to clients
    private final IBinder binder = (IBinder) new MediaPlayerServiceBinder();

    private MediaPlayer CurrentMediaPlayer;
    private AudioFile CurrentAudioFile;

    public AudioFile getCurrentAudioFile() {
        return CurrentAudioFile;
    }
    public MediaPlayer getCurrentMediaPlayer() {return CurrentMediaPlayer;}

    public Queue<AudioFile> trimQueue (Queue<AudioFile> inputQueue, String filePath) {
        while (inputQueue.iterator().hasNext()) {
            assert inputQueue.peek() != null;
            if (inputQueue.peek().getPath().compareTo(filePath) != 0) {
                // Remove head element from queue
                inputQueue.poll();
            } else {
                // Getting here means that we found our current file in the queue and have trimmed
                // everything else before that.
                break;
            }
        }
        return inputQueue;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaPlayerServiceNotificationFactory notificationFactory =
                new MediaPlayerServiceNotificationFactory();
        Notification createdNotification = notificationFactory.makeNotification(this);
        // Notification ID cannot be 0.
        startForeground(ONGOING_NOTIFICATION_ID, createdNotification);

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
                    // Get AudioScanned singleton instance so we are able to search for the file
                    AudioScanned localAudioScannedInstance = AudioScanned.getInstance();
                    ArrayList<AudioFile> localAudioFileList = localAudioScannedInstance.getAudioFileList();
                    // The first (an only) match is our instantiated object on memory
                    // TODO: set it as current song with setCurrentAudioFile or just front of queue?
                    /*List<AudioFile> tempRetrievedCurrentAudioFiles = localAudioFileList.stream()
                            .filter(file -> Objects.equals(file.getPath(), inputAudioFilePath))
                            .collect(Collectors.toList());
                    setCurrentAudioFile(tempRetrievedCurrentAudioFiles.get(0));*/
                    // Cut off the queue up to the current audio file (no previous songs)
                    Queue<AudioFile> NewQueue = new LinkedList<>(localAudioFileList);
                    NewQueue = trimQueue(NewQueue, inputAudioFilePath);
                    // Get the queue storage singleton and store the queue there.
                    AudioQueueStorage localAudioQueueStorage = AudioQueueStorage.getInstance();
                    localAudioQueueStorage.setAudioQueue(NewQueue);
                    // Start the media player finally
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

    public class MediaPlayerServiceBinder extends Binder {
        public MediaPlayerService getService() {
            // Return instance to clients for calling methods
            return MediaPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // Remove audio file that just played (it's in front of the queue)
        AudioQueueStorage localAudioQueueStorage = AudioQueueStorage.getInstance();
        Queue<AudioFile> localAudioQueueInstance = localAudioQueueStorage.getAudioQueue();
        localAudioQueueInstance.remove();
        // Play audio file now in front of the queue, make sure the element is not zero
        try {
            AudioFile nextAudioFileInQueue = localAudioQueueInstance.peek();
            assert nextAudioFileInQueue != null;
            mediaPlayer.setDataSource(nextAudioFileInQueue.getPath());
            // Set our variable too TODO: set this or nah?
            //setCurrentAudioFile(nextAudioFileInQueue);
        } catch (IOException e) {
            // Do not actually stop app, just stop playing
            //TODO: let user know?
            System.out.println("END OF QUEUE REACHED!");
            return;
        }
        mediaPlayer.start();
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

}
