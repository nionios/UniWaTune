package com.nionios.uniwatune.data.singletons;

import com.nionios.uniwatune.data.helpers.fileFinder;
import com.nionios.uniwatune.data.types.AudioFile;

import android.app.Activity;
import android.content.Context;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@code @description}  Singleton that stores info of the audio files scanned in device.
 * Every song has its id. The id is being set by this object.
 */
public class AudioScanned {
    private static AudioScanned INSTANCE;
    private ArrayList<AudioFile> audioFileList;
    private AudioScanned() {
        /*
        // DEBUG: Testing by creating some sample files TODO: Remove this
        AudioFile s1 = new AudioFile("SampleSongPath",
                "Sample Song Name",
                "Sample Artist",
                "Sample Album");
        AudioFile s2 = new AudioFile("SampleSongPath", "Death Classic");
        AudioFile s3 = new AudioFile("SampleSongPath", "Beware", "Death Grips");
        AudioFile s4 = new AudioFile("SampleSongPath", "This Life", "Denzel Curry");

        List<AudioFile> tempAudioList = new ArrayList<AudioFile>();
        tempAudioList.add(s1);
        tempAudioList.add(s2);
        tempAudioList.add(s3);
        tempAudioList.add(s4);

        audioFileList = tempAudioList;
        */
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