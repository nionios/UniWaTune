package com.nionios.uniwatune.ui.player;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nionios.uniwatune.data.singletons.AudioQueueStorage;
import com.nionios.uniwatune.data.types.AudioFile;

import java.util.Queue;


/* AndroidViewModel is an Application context aware ViewModel.
 * We need it for communicating with the MediaPlayerService.*/
public class PlayerViewModel extends AndroidViewModel {
    private MutableLiveData<String> mutableTransferAudioFileTitle;
    private MutableLiveData<String> mutableTransferAudioFileArtist;
    private MutableLiveData<String> mutableTransferAudioFileAlbum;
    private MutableLiveData<Bitmap> mutableTransferAudioFileAlbumArt;

    public AudioFile getFreshUIInfo () {
        // Get the currently playing song from the first position of our queue
        AudioQueueStorage localAudioQueueStorage = AudioQueueStorage.getInstance();
        Queue<AudioFile> localAudioQueueInstance = localAudioQueueStorage.getAudioQueue();
        AudioFile localCurrentAudioFile = localAudioQueueInstance.peek();
        return localCurrentAudioFile;
    }

    public void setUIInfo () {
        AudioFile localCurrentAudioFile = getFreshUIInfo();
        assert localCurrentAudioFile != null;
        mutableTransferAudioFileTitle    = new MutableLiveData<>(localCurrentAudioFile.getName());
        mutableTransferAudioFileAlbum    = new MutableLiveData<>(localCurrentAudioFile.getAlbum());
        mutableTransferAudioFileArtist   = new MutableLiveData<>(localCurrentAudioFile.getArtist());
        mutableTransferAudioFileAlbumArt = new MutableLiveData<>(localCurrentAudioFile.getAlbumArt());
    }

    public PlayerViewModel(Application application) {
        super(application);
        setUIInfo();
    }

    public void updateUI () {
        AudioFile localCurrentAudioFile = getFreshUIInfo();
        assert localCurrentAudioFile != null;
        mutableTransferAudioFileTitle    .postValue(localCurrentAudioFile.getName());
        mutableTransferAudioFileAlbum    .postValue(localCurrentAudioFile.getAlbum());
        mutableTransferAudioFileArtist   .postValue(localCurrentAudioFile.getArtist());
        mutableTransferAudioFileAlbumArt .postValue(localCurrentAudioFile.getAlbumArt());
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