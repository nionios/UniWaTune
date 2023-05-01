package com.nionios.uniwatune.ui.transform;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nionios.uniwatune.data.helpers.fileFinder;
import com.nionios.uniwatune.data.singletons.AudioScanned;
import com.nionios.uniwatune.data.types.AudioFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransformViewModel extends ViewModel {

    private final MutableLiveData<List<String>> mTexts;

    public TransformViewModel() {
        /* Get instance of singleton for getting info of scanned songs on device */
        AudioScanned audioScannedInstance = AudioScanned.getInstance();

        mTexts = new MutableLiveData<>();
        /*List to store local audio file list instance*/
        List<AudioFile> localInstanceAudioFileList = audioScannedInstance.getAudioFileList();
        /*List for song names to be displayed*/
        List<String> textsToDisplay = new ArrayList<String>();
        Iterator<AudioFile> iter = localInstanceAudioFileList.iterator();
        /* Iterate through audio file list and get their names*/
        while(iter.hasNext()) {
            textsToDisplay.add(iter.next().getName());
        }
        mTexts.setValue(textsToDisplay);
    }

    public LiveData<List<String>> getTexts() {
        return mTexts;
    }
}