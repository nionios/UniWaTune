package com.nionios.uniwatune.data.singletons;

import com.nionios.uniwatune.data.helpers.fileFinder;
import com.nionios.uniwatune.data.types.AudioFile;

import android.app.Activity;
import android.content.Context;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code @description}  Singleton that stores info of the audio files scanned in device.
 */
public class AudioScanned {
    private static AudioScanned INSTANCE;
    private List<AudioFile> audioFileList;
    private AudioScanned() {
    }

    public static AudioScanned getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AudioScanned();
        }
        return INSTANCE;
    }

    // Add a new audio file
    public void appendAudioFile (AudioFile inputAudioFile) {
        audioFileList.add(inputAudioFile);
    }

    public void setAudioFileList (List<AudioFile> inputAudioFileList) {
        audioFileList = inputAudioFileList;
    }
    public List<AudioFile> getAudioFileList () {
        return audioFileList;
    }
}