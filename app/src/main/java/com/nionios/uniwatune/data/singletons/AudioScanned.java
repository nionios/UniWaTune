package com.nionios.uniwatune.data.singletons;

import com.nionios.uniwatune.data.types.AudioFile;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * {@code @description}  Singleton that stores info of the audio files scanned in device.
 * Every song has its id. The id is being set by this object.
 */
public class AudioScanned {
    private static AudioScanned AUDIO_SCANNED_INSTANCE;
    private ArrayList<AudioFile> audioFileList;
    private AudioScanned() {}

    public static AudioScanned getInstance() {
        if(AUDIO_SCANNED_INSTANCE == null) {
            AUDIO_SCANNED_INSTANCE = new AudioScanned();
        }
        return AUDIO_SCANNED_INSTANCE;
    }

    // Add a new audio file
    public void appendAudioFile (AudioFile inputAudioFile) {
        audioFileList.add(inputAudioFile);
    }

    public void setAudioFileList (ArrayList<AudioFile> inputAudioFileList) {
        audioFileList = inputAudioFileList;
        Iterator<AudioFile> iter = audioFileList.iterator();
        int i = 0;
        while (iter.hasNext()) {
            // Set the ID of each audio file in our list.
            iter.next().setID(i);
            i += 1;
        }
    }

    // Get the audio file needed with its ID.
    public AudioFile getAudioFile (int ID) {
        return audioFileList.get(ID);
    }

    public ArrayList<AudioFile> getAudioFileList () {
        return audioFileList;
    }
}