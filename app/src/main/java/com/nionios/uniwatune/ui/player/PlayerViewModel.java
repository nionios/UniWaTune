package com.nionios.uniwatune.ui.player;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nionios.uniwatune.R;
import com.nionios.uniwatune.data.singletons.AudioQueueStorage;
import com.nionios.uniwatune.data.types.AudioFile;

import java.util.Queue;


/* AndroidViewModel is an Application context aware ViewModel.
 * We need it for communicating with the MediaPlayerService.*/
public class PlayerViewModel extends AndroidViewModel {
    private final MutableLiveData<String> mutableTransferAudioFileTitle;
    private final MutableLiveData<String> mutableTransferAudioFileArtist;
    private final MutableLiveData<String> mutableTransferAudioFileAlbum;
    private final MutableLiveData<Bitmap> mutableTransferAudioFileAlbumArt;

    public PlayerViewModel(Application application) {
        super(application);
        // Get the currently playing song from the first position of our queue
        AudioQueueStorage localAudioQueueStorage = AudioQueueStorage.getInstance();
        Queue<AudioFile> localAudioQueueInstance = localAudioQueueStorage.getAudioQueue();
        AudioFile localCurrentAudioFile = localAudioQueueInstance.peek();

        assert localCurrentAudioFile != null;
        mutableTransferAudioFileTitle    = new MutableLiveData<>(localCurrentAudioFile.getName());
        mutableTransferAudioFileArtist   = new MutableLiveData<>(localCurrentAudioFile.getArtist());
        mutableTransferAudioFileAlbum    = new MutableLiveData<>(localCurrentAudioFile.getAlbum());
        mutableTransferAudioFileAlbumArt = new MutableLiveData<>(localCurrentAudioFile.getAlbumArt());
    }

    public LiveData<String> getMutableAudioFileTitle() {
        return mutableTransferAudioFileTitle;
    }
    public LiveData<String> getMutableAudioFileArtist() {
        return mutableTransferAudioFileArtist;
    }
    public LiveData<String> getMutableAudioFileAlbum() {
        return mutableTransferAudioFileAlbum;
    }
    public LiveData<Bitmap> getMutableAudioFileAlbumArt() {
        return mutableTransferAudioFileAlbumArt;
    }
}