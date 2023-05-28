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

    /**
     * * @param context
     * Communicates with the service to pause or play the song currently playing
     */
    public void toggleCurrentlyPlayingSongPlayState (Context context) {
        // Make the new service connection and set isItBound flag appropriately.
        ServiceConnection localServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance.
                MediaPlayerService.MediaPlayerServiceBinder serviceBinder =
                        (MediaPlayerService.MediaPlayerServiceBinder) iBinder ;
                localMediaPlayerService = serviceBinder.getService();
                localMediaPlayerService.toggleMediaPlayerPlayState();
                isItBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isItBound = false;
            }
        };
        // Make the new explicit intent for getting the current song.
        Intent getCurrentSongIntent = new Intent(context, MediaPlayerService.class);
        //getCurrentSongIntent.setAction("ACTION_GET_CURRENT_SONG");
        //getCurrentSongIntent.setPackage("com.uniwatune");
        boolean connected = false;
        //bind service is asynchronous, so we do not return anything
        connected = context.bindService(
                getCurrentSongIntent,
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
        /* Start the MediaPlayerControllerService and send the path as an extra*/
        Context context = view.getContext().getApplicationContext();
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
