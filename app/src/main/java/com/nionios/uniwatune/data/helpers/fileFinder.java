package com.nionios.uniwatune.data.helpers;

import android.database.Cursor;
import android.content.Context;
import android.provider.MediaStore;
import android.net.Uri;

import com.nionios.uniwatune.data.types.AudioFile;

import java.util.ArrayList;
import java.util.List;

public class fileFinder {
    public List<AudioFile> getAllAudioFromDevice(final Context context) {

        final List<AudioFile> AudioFileList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST,
        };

        // if want from specific folder
        //Cursor c = context.getContentResolver().query(uri, projection, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%utm%"}, null);

        // if want fetch all files
        Cursor c = context.getContentResolver().query(uri,
                projection,
                null,
                null,
                null);

        if (c != null) {
            while (c.moveToNext()) {
                String path = c.getString(0);
                String name = c.getString(1);
                String album = c.getString(2);
                String artist = c.getString(3);

                AudioFile AudioFile = new AudioFile(path,name,album,artist);

                AudioFileList.add(AudioFile);
            }
            c.close();
        }
        return AudioFileList;
    }
}
