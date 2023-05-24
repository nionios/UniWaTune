package com.nionios.uniwatune.data.services.MediaPlayerService;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.HandlerThread;

import com.nionios.uniwatune.MainActivity;
import com.nionios.uniwatune.data.singletons.AudioScanned;
import com.nionios.uniwatune.data.types.AudioFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

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
    private Queue<AudioFile> AudioFileQueue = new LinkedList<>();
    public AudioFile getCurrentAudioFile() {
        return CurrentAudioFile;
    }
    public MediaPlayer getCurrentMediaPlayer() {return CurrentMediaPlayer;}

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
                    //TODO: make this autodetect the queue! Comment
                    // setQueue();
                    // Get AudioScanned singleton instance so we are able to search for the file
                    AudioScanned localAudioScannedInstance = AudioScanned.getInstance();
                    ArrayList<AudioFile> localAudioFileList = localAudioScannedInstance.getAudioFileList();
                    // The first (an only) match is our instantiated object on memory, set it as
                    // current song with setCurrentAudioFile
                    List<AudioFile> tempRetrievedCurrentAudioFiles = localAudioFileList.stream()
                            .filter(file -> Objects.equals(file.getPath(), inputAudioFilePath))
                            .collect(Collectors.toList());
                    setCurrentAudioFile(tempRetrievedCurrentAudioFiles.get(0));
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

        if (Objects.equals(intent.getAction(), ACTION_GET_CURRENT_SONG)) {
            if (CurrentAudioFile != null) {
                //TODO: Make new activity to play audio! Then on create
                // make something like this: https://stackoverflow.com/questions/18146614/how-to-send-string-from-one-activity-to-another
                Intent returnedAudioFile = new Intent(this, MainActivity.class);
                returnedAudioFile.putExtra("audioFileName", CurrentAudioFile.getName());
                returnedAudioFile.putExtra("audioFileArtist", CurrentAudioFile.getArtist());
                returnedAudioFile.putExtra("audioFileAlbum", CurrentAudioFile.getAlbum());
                startActivity(returnedAudioFile);
            } else {
                //TODO: send "no current song playing or something
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

}
