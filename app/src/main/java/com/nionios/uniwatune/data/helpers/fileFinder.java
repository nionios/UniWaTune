package com.nionios.uniwatune.data.helpers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.util.Size;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nionios.uniwatune.data.types.AudioFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class fileFinder {
    /**
     * {@code @description} Function to make and organize audioFile objects with the audio file info
     * from the device into a list.
     * */
    public ArrayList<AudioFile> getAllAudioFromDevice(final Context context) throws IOException {

        final ArrayList<AudioFile> AudioFileList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        /* TODO: Maybe search downloads too?
        Uri uri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        }

         */

        /* Projection to search with specific columns with cursor later */
        String[] projection = {
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.ArtistColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM,
        };

        // Some files might not be music
        // Enable this for only music (also add it in the cursor query). Set null for disable
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        /* Fetch all files from internal storage with the columns described in projection */
        Cursor audioCursor = context.getContentResolver().query(
                uri,
                projection,
                selection,
                null,
                null
        );

        if (audioCursor != null) {
            while (audioCursor.moveToNext()) {
                /* Get the desired strings and make a new AudioFile obj with the info */
                String path      = audioCursor.getString(1);
                String name      = audioCursor.getString(2);
                String artist    = audioCursor.getString(3);
                String album     = audioCursor.getString(4);
                /* Get the album art  with ContentResolver */
                Bitmap albumArt = null;
                ContentResolver localContentResolver = context.getContentResolver();

                Uri audioFileUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        Long.parseLong(audioCursor.getString(0))
                );

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        albumArt = localContentResolver.loadThumbnail(
                                   audioFileUri,
                                   new Size(512, 512),
                                   null);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                AudioFile NewAudioFile;
                /* Call a different constructor depending on if the info is available */
                if (!artist.equals("<unknown>")) {
                    if (!album.equals("<unknown>")) {
                        if (albumArt != null) {
                            NewAudioFile = new AudioFile(path, name, artist, album, albumArt);
                        } else {
                            NewAudioFile = new AudioFile(path, name, artist, album);
                        }
                    } else {
                        NewAudioFile = new AudioFile(path, name, artist);
                    }
                } else {
                    NewAudioFile = new AudioFile(path, name);
                }
                AudioFileList.add(NewAudioFile);
            }
            audioCursor.close();
        }
        return AudioFileList;
    }
}
