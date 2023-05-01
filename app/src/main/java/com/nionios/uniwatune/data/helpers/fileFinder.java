package com.nionios.uniwatune.data.helpers;

import android.database.Cursor;
import android.content.Context;
import android.provider.MediaStore;
import android.net.Uri;

import com.nionios.uniwatune.data.types.AudioFile;

import java.util.ArrayList;
import java.util.List;

public class fileFinder {
    /**
     * {@code @description} Function to make and organize audioFile objects with the audio file info
     * from the device into a list.
     * */
    public List<AudioFile> getAllAudioFromDevice(final Context context) {

        final List<AudioFile> AudioFileList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        /* Projection to search with specific columns with cursor later */
        String[] projection = {
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST,
        };

        // if want from specific folder
        //Cursor c = context.getContentResolver().query(uri, projection, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%utm%"}, null);

        // Some files might not be music
        // Enable this for only songs (also add it in the cursor query)
        // String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        //FIXME: For some reason this ignores Music/

        /* Fetch all files from internal storage with the columns described in projection */
        Cursor audioCursor = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null
        );

        if (audioCursor != null) {
            while (audioCursor.moveToNext()) {
                /* Get the desired strings and make a new AudioFile obj with the info */
                String path   = audioCursor.getString(0);
                String name   = audioCursor.getString(1);
                String album  = audioCursor.getString(2);
                String artist = audioCursor.getString(3);

                AudioFile AudioFile = new AudioFile(path,name,album,artist);
                AudioFileList.add(AudioFile);
            }
            audioCursor.close();
        }
        return AudioFileList;
    }
}
