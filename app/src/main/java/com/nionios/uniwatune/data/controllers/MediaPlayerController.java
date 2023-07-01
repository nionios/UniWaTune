package com.nionios.uniwatune.data.controllers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.view.View;

import com.nionios.uniwatune.data.services.MediaPlayerService.MediaPlayerService;
import com.nionios.uniwatune.data.singletons.AudioQueueStorage;

/**@description this is our media player controller. Ui communicates with this class
 * so this could communicate with the MediaPlayerService (This is middleware) */
public class MediaPlayerController {
    boolean isItBound;
    MediaPlayerService localMediaPlayerService;
    final int TOGGLE_CURRENT_AUDIO_FILE_PLAY_STATE = 1;
    final int PLAY_NEXT_AUDIO_FILE = 2;
    final int PLAY_PREVIOUS_AUDIO_FILE = 3;

    /**
     * * @param context
     * Communicates with the service to pause or play the song currently playing
     */
    public void toggleCurrentlyPlayingAudioFilePlayState(Context context) {
        communicateWithService(context, TOGGLE_CURRENT_AUDIO_FILE_PLAY_STATE);
    }

    public void playNextAudioFile (Context context) {
        communicateWithService(context, PLAY_NEXT_AUDIO_FILE);
    }

    public void playPreviousAudioFile (Context context) {
        communicateWithService(context, PLAY_PREVIOUS_AUDIO_FILE);
    }

    /**
     * Method that handles the binding to the service and runs the appropriate method on service.
     * @param context - The appropriate Context
     * @param internalCommand - The appropriate int that signals the wanted action of service
     */
    public void communicateWithService (Context context, int internalCommand) {
        // Make the new service connection and set isItBound flag appropriately.
        ServiceConnection localServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance.
                MediaPlayerService.MediaPlayerServiceBinder serviceBinder =
                        (MediaPlayerService.MediaPlayerServiceBinder) iBinder ;
                localMediaPlayerService = serviceBinder.getService();
                // Initialize a MediaButtonsController to change the UI buttons appropriately
                MediaButtonsController localMediaButtonsController = new MediaButtonsController();
                // According to given command from previous methods, run diffent bound
                // service method
                switch (internalCommand) {
                    case TOGGLE_CURRENT_AUDIO_FILE_PLAY_STATE:
                        localMediaPlayerService.toggleMediaPlayerPlayState();
                        localMediaButtonsController.togglePlayButtons(context);
                        break;
                    case PLAY_NEXT_AUDIO_FILE:
                        localMediaPlayerService.playNextAudioFile();
                        break;
                    case PLAY_PREVIOUS_AUDIO_FILE:
                        localMediaPlayerService.playPreviousAudioFile();
                        break;
                }
                isItBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isItBound = false;
            }
        };
        // Make the new explicit intent for getting the current song.
        Intent communicationWithServiceIntent = new Intent(context, MediaPlayerService.class);
        //bind service is asynchronous, so we do not return anything
        context.bindService(
                communicationWithServiceIntent,
                localServiceConnection,
                // This is zero because service is running at this point.
                0
        );
    }

    // If the queue is active, this means the current song is playing.
    public boolean isAudioPlaying() {
        AudioQueueStorage localAudioQueueStorageInstance = AudioQueueStorage.getInstance();
        return localAudioQueueStorageInstance.getIsQueueActive();
    }

    /** File Path instead of serializing the whole object is used because Native Android serializing
     *  is slow and not needed in this case, we can find the object with its unique path anyway. */
    public void playSelectedAudioFile(View view, String filePath) {
        Context context = view.getContext();
        /* Start the MediaPlayerControllerService and send the path as an extra*/
        Intent serviceIntent = new Intent(context, MediaPlayerService.class);
        serviceIntent.putExtra("PATH", filePath);
        serviceIntent.setAction("com.uniwatune.action.PLAY");
        /* Start our MediaPlayerControllerService */
        context.startService(serviceIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        }
    }
}
