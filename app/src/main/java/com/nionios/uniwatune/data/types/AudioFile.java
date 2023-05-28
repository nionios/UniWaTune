package com.nionios.uniwatune.data.types;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

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

    public AudioFile(
            @NonNull String inputPath,
            @NonNull String inputName,
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
    public AudioFile(
            @NonNull String inputPath,
            @NonNull String inputName,
            String inputArtist,
            String inputAlbum
    ) {
        name = inputName;
        artist = inputArtist;
        album = inputAlbum;
        path = inputPath;
    }

    // Overload without album
    public AudioFile(
            @NonNull String inputPath,
            @NonNull String inputName,
            String inputArtist
    ) {
        name = inputName;
        artist = inputArtist;
        album = "Unknown Album";
        path = inputPath;
    }

    // Overload constructor for unknown artist/album audio files
    public AudioFile(
            @NonNull String inputPath,
            @NonNull String inputName
    ) {
        name = inputName;
        artist = "Unknown Artist";
        album = "Unknown Album";
        path = inputPath;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    @NonNull
    public String getPath() {
        return path;
    }

    public int getID() {
        return ID;
    }

    public void setID(int inputID) {
        ID = inputID;
    }
}
