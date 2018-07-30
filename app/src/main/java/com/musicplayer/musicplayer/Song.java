package com.musicplayer.musicplayer;

public class Song {
    public long id;
    public String title;
    public String artist;

    public Song(long songID,String songTitle,String songArtist) {
        this.id = songID;
        this.title = songTitle;
        this.artist = songArtist;
    }

    public long getSongID() {
        return id;
    }

    public String getSongTitle() {
        return title;
    }

    public String getSongArtist() {
        return artist;
    }
}
