package com.nionios.uniwatune.ui.player;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nionios.uniwatune.data.singletons.MediaPlayerController;
import com.nionios.uniwatune.data.types.AudioFile;

public class PlayerViewModel extends ViewModel {
    private final MutableLiveData<String> mutableTransferAudioFileTitle;
    private final MutableLiveData<String> mutableTransferAudioFileArtist;
    private final MutableLiveData<String> mutableTransferAudioFileAlbum;
    private final MutableLiveData<Bitmap> mutableTransferAudioFileAlbumArt;

    public PlayerViewModel() {
        MediaPlayerController localMediaPlayerControllerInstance =
                MediaPlayerController.getInstance();
        AudioFile localCurrentAudioFile  = localMediaPlayerControllerInstance.getCurrentAudioFile();
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