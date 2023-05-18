package com.nionios.uniwatune.data.controllers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;

import com.nionios.uniwatune.data.services.MediaPlayerService.MediaPlayerService;
import com.nionios.uniwatune.data.types.AudioFile;

/**@description this is our media player controller. Ui communicates with this class
 * so this could communicate with the MediaPlayerService (This is middleware) */
public class MediaPlayerController {
    boolean isItBound;
    MediaPlayerService localMediaPlayerService;
    AudioFile currentFetchedAudioFile;
    public AudioFile getCurrentAudioFileFromService (Context context) {
        // Make the new service connection and set isItBound flag appropriately.
        ServiceConnection localServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance.
                MediaPlayerService.MediaPlayerServiceBinder serviceBinder =
                        (MediaPlayerService.MediaPlayerServiceBinder) iBinder ;
                localMediaPlayerService = serviceBinder.getService();
                // Get the current audio file from our bound service
                currentFetchedAudioFile = localMediaPlayerService.getCurrentAudioFile();
                isItBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isItBound = false;
            }
        };
        // Make the new explicit intent for getting the current song.
        Intent getCurrentSongIntent = new Intent();
        getCurrentSongIntent.setAction("com.uniwatune.action.ACTION_GET_CURRENT_SONG");
        getCurrentSongIntent.setPackage("com.uniwatune");
        context.bindService(
                getCurrentSongIntent,
                localServiceConnection,
                // This is zero because service is running at this point.
                0
        );

        if (isItBound) {
            System.out.println("isItBound is true");
        } else {
            System.out.println("isItBound is false");
        }
        // Make sure the file is fetched

        //TODO: this is always NULL, no connection?
        assert currentFetchedAudioFile != null;
        return currentFetchedAudioFile;
    }

    public void playSelectedAudioFile(View view, String filePath) {
        /* Start the MediaPlayerControllerService and send the path as an extra*/
        Context context = view.getContext();
        Intent serviceIntent = new Intent(view.getContext(), MediaPlayerService.class);
        serviceIntent.putExtra("PATH", filePath);
        serviceIntent.setAction("com.uniwatune.action.PLAY");
        /* Start our MediaPlayerControllerService */
        context.startService(serviceIntent);
        /* Bind our MediaPlayerControllerService too for info exchange */
        //context.bindService(serviceIntent, )
        //TODO: what happens when else??
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        }
    }
}
