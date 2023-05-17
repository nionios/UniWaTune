package com.nionios.uniwatune.data.types;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.nionios.uniwatune.data.singletons.MediaPlayerController;

public class AudioFile {
    @NonNull
    String name;
    String artist;
    Bitmap albumArt;
    String album;
    @NonNull
    String path;
    // The id is being set once the song is on the AudioScanned List.
    int ID;

    public AudioFile (
            String inputPath,
            String inputName,
            String inputArtist,
            String inputAlbum,
            Bitmap inputAlbumArt
    ) {
        name = inputName;
        artist = inputArtist;
        album = inputAlbum;
        albumArt = inputAlbumArt;
        path = inputPath;
    }

    // Overload without album art
    public AudioFile (
            String inputPath,
            String inputName,
            String inputArtist,
            String inputAlbum
    ) {
        name = inputName;
        artist = inputArtist;
        album = inputAlbum;
        path = inputPath;
    }

    // Overload without album
    public AudioFile (
            String inputPath,
            String inputName,
            String inputArtist
    ) {
        name = inputName;
        artist = inputArtist;
        album = "Unknown Album";
        path = inputPath;
    }

    // Overload constructor for unknown artist/album audio files
    public AudioFile (
            String inputPath,
            String inputName
    ) {
        name = inputName;
        artist = "Unknown Artist";
        album = "Unknown Album";
        path = inputPath;
    }

    public String getName() {return name;}
    public String getArtist() {return artist;}
    public String getAlbum() {return album;}
    public Bitmap getAlbumArt() {return albumArt;}
    public String getPath() {return path;}
    public int getID() {return ID;}
    public void setID(int inputID) {ID = inputID;}
}
