package com.nionios.uniwatune.data.singletons;

import androidx.annotation.NonNull;

import com.nionios.uniwatune.data.observables.GlobalCurrentAudioFile;
import com.nionios.uniwatune.data.types.AudioFile;

import org.jetbrains.annotations.Contract;

import java.io.LineNumberReader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class AudioQueueStorage {
    private static AudioQueueStorage AUDIO_QUEUE_INSTANCE;
    private LinkedList<AudioFile> AudioQueue = new LinkedList<>();
    private LinkedList<AudioFile> UnshuffledAudioQueue = new LinkedList<>();
    /* ShadowQueue is our silent navigator through all of the songs once on queue
     * (previous ones too!) */
    private LinkedList<AudioFile> ShadowQueue = new LinkedList<>();
    private AudioQueueStorage() {}
    // If isQueueActive is true, then the song on index 0 is currently playing
    private boolean isQueueActive;
    private boolean isQueueShuffled = false;


    public static AudioQueueStorage getInstance() {
        if(AUDIO_QUEUE_INSTANCE == null) {
            AUDIO_QUEUE_INSTANCE = new AudioQueueStorage();
        }
        return AUDIO_QUEUE_INSTANCE;
    }

    public void setAudioQueue (LinkedList<AudioFile> inputAudioFileQueue) {
        AudioQueue = inputAudioFileQueue;
        GlobalCurrentAudioFile globalCurrentAudioFile = GlobalCurrentAudioFile.getInstance();
        globalCurrentAudioFile.updateCurrentAudioFile(inputAudioFileQueue.peek());
    }

    // Function to convert an array list to a linked list
    private static <T> List<T> convertALtoLL(List<T> aL) {
        return new LinkedList<>(aL);
    }

    public void setAudioAndShadowQueue (LinkedList<AudioFile> inputAudioFileQueue) {
        AudioQueue = inputAudioFileQueue;
        // When we set a whole new queue, we set the ShadowQueue too with AudioScanned list
        AudioScanned localAudioScannedInstance = AudioScanned.getInstance();
        ShadowQueue = (LinkedList<AudioFile>) convertALtoLL(localAudioScannedInstance.getAudioFileList());
    }

    public LinkedList<AudioFile> getAudioQueue () {
        return AudioQueue;
    }
    public LinkedList<AudioFile> getShadowQueue () {
        return ShadowQueue;
    }

    // Set the unshuffled audio queue if user switches to unshuffling
    public void shuffle () {
        UnshuffledAudioQueue = AudioQueue;
        Collections.shuffle(AudioQueue);
        isQueueShuffled = true;
    }

    public void unshuffle () {
        AudioQueue = UnshuffledAudioQueue;
        isQueueShuffled = false;
    }

    public boolean isQueueShuffled () {
        return isQueueShuffled;
    }

    public void addToAudioQueue (AudioFile inputAudioFile) {
        AudioQueue.add(inputAudioFile);
    }
    public boolean getIsQueueActive () {return isQueueActive;}
    public void setIsQueueActive (boolean inputIsQueueActive) {isQueueActive=inputIsQueueActive;}
}
