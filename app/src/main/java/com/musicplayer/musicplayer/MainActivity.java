package com.musicplayer.musicplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController.MediaPlayerControl;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MediaPlayerControl {

   private  ArrayList<Song> songList;
   private  ListView songView;
   private MusicPlayerService musicService;
   boolean musicBound = false;
   private Intent playIntent;
   private MusicController controller;
   private boolean isPaused=false;
   private boolean isPlaybackPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songView = (ListView)findViewById(R.id.song_list);
        songList = new ArrayList<Song>();

        SongAdapter songAdapter = new SongAdapter(this, songList);
        songView.setAdapter(songAdapter);

        getSongList();

        setController();
        }

        private ServiceConnection musicConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name,IBinder service) {
                MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder) service;
                musicService = binder.getService(); //getting the service
                musicService.setList(songList);
                musicBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;

            }
        };


    @Override
    protected void onStart() {
        super.onStart();

        if (playIntent==null){
            playIntent = new Intent(this,MusicPlayerService.class);
            bindService(playIntent, musicConnection, BIND_AUTO_CREATE);
        }
    }

    public void getSongList(){
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor =  musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to the list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
               songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }

   public void songPicked(View view){
        musicService.setSong(Integer.parseInt(view.getTag().toString()));
       musicService.playSong();
       if(isPlaybackPaused){
           setController();
           isPlaybackPaused=false;}
           controller.show(0);
    }


    @Override
    public void start() {
         musicService.go();
    }

    @Override
    public void pause() {
        isPlaybackPaused=true;
        musicService.pauseSong();
    }

    @Override
    public int getDuration() {
        if(musicService!=null && musicBound && musicService.isSongPlaying()){
            return musicService.getDur();
        }
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicService!=null && musicBound && musicService.isSongPlaying()){
            return musicService.getPosition();
        }
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
          musicService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        return musicService != null && musicBound && musicService.isSongPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
    private void setController(){
        controller= new MusicController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        },new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    private void playNext(){
        musicService.playNext();
        //to prevent our mediaplayer behave unpredictably
        if(isPlaybackPaused){
            setController();
            isPlaybackPaused=false;
        }
        controller.show(0);
    }
    private void playPrev(){
        musicService.playPrev();
        if(isPlaybackPaused){
            setController();
            isPlaybackPaused=false;}
        controller.show(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused= true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isPaused){
            setController();
            isPaused=false;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(isPaused){
            setController();
            isPaused=false;
        }
    }


    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }
}
