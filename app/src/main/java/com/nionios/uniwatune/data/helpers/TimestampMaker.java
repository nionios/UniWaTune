package com.nionios.uniwatune.data.helpers;

public class TimestampMaker {

    public String convertMillisecondsToTimestamp (int timeInMilliseconds) {
        int minutes = (timeInMilliseconds / 1000) / 60;
        int seconds = (timeInMilliseconds / 1000) % 60;
        String timestamp;
        // If seconds are not 2 digits prepend a 0.
        if (seconds > 9) {
            timestamp = minutes + ":" + seconds;
        } else {
            timestamp = minutes + ":0" + seconds;
        }
        return timestamp;
    }
}
