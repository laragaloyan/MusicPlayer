package com.musicplayer.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> songs;
    private LayoutInflater songInf;

    public SongAdapter(Context context,ArrayList<Song> thesongs) {
        songs = thesongs;
        songInf = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        @SuppressLint("ViewHolder") LinearLayout songLayout = (LinearLayout) songInf.inflate(R.layout.song, parent, false);
        TextView songsView = (TextView) songLayout.findViewById(R.id.song_title);
        TextView artistView = (TextView) songLayout.findViewById(R.id.song_artist);
        Song currentSong = songs.get(position);
        songsView.setText(currentSong.getSongTitle());
        artistView.setText(currentSong.getSongArtist());
        songLayout.setTag(position); //position as tag
        return songLayout;
    }
}
