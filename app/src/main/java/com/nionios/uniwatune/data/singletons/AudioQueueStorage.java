package com.nionios.uniwatune.data.singletons;

import com.nionios.uniwatune.data.types.AudioFile;

import java.util.LinkedList;
import java.util.Queue;

public class AudioQueueStorage {
    private static AudioQueueStorage AUDIO_QUEUE_INSTANCE;
    private Queue<AudioFile> AudioQueue = new LinkedList<>();
    private AudioQueueStorage() {}
    // If isQueueActive is true, then the song on index 0 is currently playing
    private boolean isQueueActive;

    public static AudioQueueStorage getInstance() {
        if(AUDIO_QUEUE_INSTANCE == null) {
            AUDIO_QUEUE_INSTANCE = new AudioQueueStorage();
        }
        return AUDIO_QUEUE_INSTANCE;
    }

    public void setAudioQueue (Queue<AudioFile> inputAudioFileQueue) {
        AudioQueue = inputAudioFileQueue;
    }

    public Queue<AudioFile> getAudioQueue () {
        return AudioQueue;
    }

    public void addToAudioQueue (AudioFile inputAudioFile) {
        AudioQueue.add(inputAudioFile);
    }
    public boolean getIsQueueActive () {return isQueueActive;}
    public void setIsQueueActive (boolean inputIsQueueActive) {isQueueActive=inputIsQueueActive;}
}
