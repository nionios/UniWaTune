package com.nionios.uniwatune.data.services.MediaPlayerService;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.core.content.ContextCompat;

import com.nionios.uniwatune.R;
import com.nionios.uniwatune.data.broadcast.NotificationBroadcastReceiver;
import com.nionios.uniwatune.data.controllers.MediaPlayerController;
import com.nionios.uniwatune.data.singletons.AudioQueueStorage;
import com.nionios.uniwatune.data.singletons.AudioScanned;
import com.nionios.uniwatune.data.singletons.MediaPlayerStorage;
import com.nionios.uniwatune.data.types.AudioFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
    private static final int ONGOING_NOTIFICATION_ID = 13;

    private final int SET_QUEUE_ONLY_FLAG = 1;
    private final int SET_BOTH_QUEUE_AND_SHADOW_QUEUE_FLAG = 2;
    // Binder for giving info to clients
    private final IBinder binder = (IBinder) new MediaPlayerServiceBinder();

    private MediaPlayer CurrentMediaPlayer;
    private AudioFile CurrentAudioFile;
    private boolean isAudioPlaying;

    public AudioFile getCurrentAudioFile() {
        return CurrentAudioFile;
    }
    public MediaPlayer getCurrentMediaPlayer() {return CurrentMediaPlayer;}

    public LinkedList<AudioFile> trimQueue (LinkedList<AudioFile> inputQueue, String filePath) {
        while (inputQueue.iterator().hasNext()) {
            //assert inputQueue.peek() != null;
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
        // Make the placeholder notification through our notification factory
        MediaPlayerServiceNotificationFactory notificationFactory =
                new MediaPlayerServiceNotificationFactory();
        Notification createdNotification = notificationFactory.makePlaceHolderNotification(this);
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
                    // We are playing a completely new file manually, set shadow queue too.
                    playAudioFile(inputAudioFilePath, SET_BOTH_QUEUE_AND_SHADOW_QUEUE_FLAG);
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

    public void playAudioFile (String inputAudioFilePath, int mode) {
        MediaPlayer localMediaPlayer =
                MediaPlayer.create( this, Uri.parse(inputAudioFilePath));
        /* Switch out any currently playing mediaPlayer with this one if they exist,
         * set this one as current. Also set the song as currently loaded on
         * our MediaPlayer*/
        setMediaPlayer(localMediaPlayer);
        // Get the queue storage singleton to store the queue there and check if queue is shuffled.
        AudioQueueStorage localAudioQueueStorage = AudioQueueStorage.getInstance();
        // Don't touch queue if it is shuffled.
        if (!localAudioQueueStorage.isQueueShuffled()) {
            // Get AudioScanned singleton instance so we are able to search for the file
            AudioScanned localAudioScannedInstance = AudioScanned.getInstance();
            ArrayList<AudioFile> localAudioFileList = localAudioScannedInstance.getAudioFileList();
            // Trim the played file from the queue
            LinkedList<AudioFile> NewQueue = new LinkedList<>(localAudioFileList);
            NewQueue = trimQueue(NewQueue, inputAudioFilePath);
            switch (mode) {
                case SET_QUEUE_ONLY_FLAG:
                    localAudioQueueStorage.setAudioQueue(NewQueue);
                    break;
                case SET_BOTH_QUEUE_AND_SHADOW_QUEUE_FLAG:
                default:
                    localAudioQueueStorage.setAudioAndShadowQueue(NewQueue);
                    break;
            }
        } else {
            // If queue is shuffled, then just remove first element of queue.
            LinkedList<AudioFile> NewQueue = new LinkedList<>(localAudioQueueStorage.getAudioQueue());
            localAudioQueueStorage.setAudioQueue(NewQueue);
        }
        localAudioQueueStorage.setIsQueueActive(true);
        // Set the looping as it was
        localMediaPlayer.setLooping(localAudioQueueStorage.isQueueLooped());
        // Start the media player finally
        localMediaPlayer.start();
        localMediaPlayer.setOnCompletionListener(this);
        //Update the placeholder notification now that we have updated info for the song.
        MediaPlayerServiceNotificationFactory notificationFactory =
                new MediaPlayerServiceNotificationFactory();
        Notification updatedNotification = notificationFactory.makeNotification(this);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ONGOING_NOTIFICATION_ID, updatedNotification);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // Remove audio file that just played (it's in front of the queue)
        AudioQueueStorage localAudioQueueStorage = AudioQueueStorage.getInstance();
        Queue<AudioFile> localAudioQueueInstance = localAudioQueueStorage.getAudioQueue();
        // Play audio file now in front of the queue, make sure the element is not zero
        try {
            // TODO: peek or poll?
            AudioFile nextAudioFileInQueue = localAudioQueueInstance.poll();
            assert nextAudioFileInQueue != null;
            getCurrentMediaPlayer().release();
            MediaPlayerController localMediaPlayerController = new MediaPlayerController();
            localMediaPlayerController.playNextAudioFile(this.getApplicationContext());
            //localAudioQueueStorage.setIsQueueActive(true);
        } catch (AssertionError e) {
            // Do not actually stop app, just stop playing
            //TODO: let user know?
            System.out.println("END OF QUEUE REACHED!");
            localAudioQueueStorage.setIsQueueActive(false);
        }
    }

    // If there is a mediaPlayer active, release it and set the new one as current.
    public void setMediaPlayer (MediaPlayer inputMediaPlayer) {
        MediaPlayerStorage localMediaPlayerStorage = MediaPlayerStorage.getInstance();
        if (CurrentMediaPlayer == null) {
            CurrentMediaPlayer = inputMediaPlayer;
            localMediaPlayerStorage.setMediaPlayer(inputMediaPlayer);
        } else {
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

    public void playNextAudioFile () {
        AudioQueueStorage localAudioQueueStorage = AudioQueueStorage.getInstance();
        Queue<AudioFile> localAudioQueueInstance = localAudioQueueStorage.getAudioQueue();
        // Remove already played file from queue. If queue is empty, stop playing anything
        // TODO: signal user somehow?
        if (localAudioQueueInstance.poll() == null) return;
        AudioFile nextFile = localAudioQueueInstance.peek();
        assert nextFile != null;
        // Keep shadow queue intact to be able to go to previous song
        playAudioFile(nextFile.getPath(), SET_QUEUE_ONLY_FLAG);
    }

    public void playPreviousAudioFile () {
        AudioQueueStorage localAudioQueueStorage = AudioQueueStorage.getInstance();
        /* On previous song, we are getting ShadowQueue too in order to access the audio files
         * played in the past. */
        LinkedList<AudioFile> localShadowQueueInstance = localAudioQueueStorage.getShadowQueue();
        LinkedList<AudioFile> localAudioQueueInstance = localAudioQueueStorage.getAudioQueue();
        // Get the position of the file in the shadow queue based on the pos on the playing queue
        int previousFilePosition = localShadowQueueInstance.indexOf(localAudioQueueInstance.peek()) - 1;
        // TODO: let user know?
        if (previousFilePosition < 0) return;
        AudioFile previousFile =
                localShadowQueueInstance.get(previousFilePosition);
        assert previousFile != null;
        // Keep shadow queue intact to be able to go to previous song
        playAudioFile(previousFile.getPath(), SET_QUEUE_ONLY_FLAG);
    }

    public void toggleMediaPlayerPlayState () {
        //Set the variable in the queue singleton and play/pause the audio
        AudioQueueStorage localAudioQueueStorageInstance = AudioQueueStorage.getInstance();
        if (CurrentMediaPlayer.isPlaying()) {
            CurrentMediaPlayer.pause();
            localAudioQueueStorageInstance.setIsQueueActive(false);
        } else {
            CurrentMediaPlayer.start();
            localAudioQueueStorageInstance.setIsQueueActive(true);
        }
    }

    public void restartMediaPlayer () {
        if (CurrentMediaPlayer != null) {
            CurrentMediaPlayer.start();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
