package com.nionios.uniwatune.data.types;

public class AudioFile {
    String name;
    String artist;
    String album;
    String path;

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
    public String getPath() {return path;}

}
