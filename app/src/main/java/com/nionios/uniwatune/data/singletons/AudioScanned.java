package com.nionios.uniwatune.data.singletons;

import com.nionios.uniwatune.data.types.AudioFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/** @description With this comparator we are able to sort the audio files alphabetically according
 *  to the audio file names. */
class AudioFileLexicographicComparator implements Comparator<AudioFile> {
    @Override
    public int compare(AudioFile a, AudioFile b) {
        return a.getName().compareToIgnoreCase(b.getName());
    }
}

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
        // Sort list and then give the IDs
        sortAudioFileList();
        Iterator<AudioFile> iter = audioFileList.iterator();
        int i = 0;
        while (iter.hasNext()) {
            // Set the ID of each audio file in our list.
            iter.next().setID(i);
            i += 1;
        }
    }

    /* Sort the Audio Scanned file list with our lexicographic comparator to construct the list
     * of songs in app */
    public void sortAudioFileList () {
        audioFileList.sort(new AudioFileLexicographicComparator());
    }

    // Get the audio file needed with its ID.
    public AudioFile getAudioFile (int ID) {
        return audioFileList.get(ID);
    }

    public ArrayList<AudioFile> getAudioFileList () {
        return audioFileList;
    }
}