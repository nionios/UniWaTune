package com.nionios.uniwatune.ui.transform;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nionios.uniwatune.data.singletons.AudioScanned;
import com.nionios.uniwatune.data.types.AudioFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransformViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<AudioFile>> MutableAudioFileTransferList;

    public TransformViewModel() {
        /* Get instance of singleton for getting info of scanned songs on device */
        AudioScanned audioScannedInstance = AudioScanned.getInstance();

        MutableAudioFileTransferList = new MutableLiveData<>();
        /*List to store local audio file list instance*/
        List<AudioFile> localInstanceAudioFileList = audioScannedInstance.getAudioFileList();
        Iterator<AudioFile> iter = localInstanceAudioFileList.iterator();

        // Create 2D array to set the MutableLiveData later
        ArrayList<AudioFile> SimpleAudioFileTransferList = new ArrayList<AudioFile>();

        /* Iterate through audio file list and get their names*/
        while(iter.hasNext()) {
            // Get all info of the song in the array pos
            AudioFile elem = iter.next();
            // Add AudioFile obj fetched into the transitional array
            SimpleAudioFileTransferList.add(elem);
        }
        MutableAudioFileTransferList.setValue(SimpleAudioFileTransferList);
    }

    public LiveData<ArrayList<AudioFile>> getTexts() {
        return MutableAudioFileTransferList;
    }
}