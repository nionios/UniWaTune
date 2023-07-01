package com.nionios.uniwatune.data.observables;

import com.nionios.uniwatune.data.types.AudioFile;

import java.util.Observable;

public class GlobalCurrentAudioFile extends Observable {
    private static GlobalCurrentAudioFile GLOBAL_CURRENT_AUDIO_FILE_INSTANCE;
    AudioFile currentAudioFile;

    public static GlobalCurrentAudioFile getInstance() {
        if(GLOBAL_CURRENT_AUDIO_FILE_INSTANCE == null) {
            GLOBAL_CURRENT_AUDIO_FILE_INSTANCE = new GlobalCurrentAudioFile();
        }
        return GLOBAL_CURRENT_AUDIO_FILE_INSTANCE;
    }

    public void updateCurrentAudioFile (AudioFile inputAudioFile) {
        setChanged();
        notifyObservers();
    }

    public AudioFile getCurrentAudioFile () {
        return currentAudioFile;
    }
}
