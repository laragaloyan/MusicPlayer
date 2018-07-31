package com.musicplayer.musicplayer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import java.io.IOException;
import java.util.ArrayList;
import android.app.Notification;
import android.app.PendingIntent;
import android.util.Log;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer player;
    private ArrayList<Song> songs;
    private int currentPosition;
    private String songTitle="";
    public static final int NOTIFY_ID=1;

    public MusicPlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentPosition = 0;
        player= new MediaPlayer();

        initMediaPlayer();
    }

    public void initMediaPlayer(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);

    }
// set list from the activity to service
    public  void setList(ArrayList<Song> theSongs ){
        songs = theSongs;
    }

    public class MusicBinder extends Binder{
        MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    private final IBinder musicBind = new MusicBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        //starts the playback
        mp.start();
        //continue playback even if the user navigates the app from notification
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);

        }

        @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()>=0){
            mp.reset();
            playNext();
        }
    }

    public void playSong(){
        player.reset();
        Song playsong =songs.get(currentPosition);
        //setting the song title
        songTitle= playsong.getSongTitle();
        long currentSong = playsong.getSongID();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currentSong);

        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
            e.printStackTrace();
        }
       player.prepareAsync();


//        try {
//            player.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public void setSong(int songIndex){
        currentPosition=songIndex;
    }

    public int getPosition(){
        return player.getCurrentPosition();
    }
    public int getDur(){
        return player.getDuration();
    }
    public boolean isSongPlaying(){
        return player.isPlaying();
    }
    public void  pauseSong(){
        player.pause();
    }
    public  void  seek(int position){
        player.seekTo(position);
    }
    public  void go(){
        player.start();
    }

    public void playPrev(){
        currentPosition--;
        if(currentPosition<0) {
            currentPosition=songs.size()-1;}
        playSong();
        }

    public void playNext(){
        currentPosition ++;
        if(currentPosition>=songs.size()) {
            currentPosition=songs.size()-1;}
        playSong();

    }
    }



