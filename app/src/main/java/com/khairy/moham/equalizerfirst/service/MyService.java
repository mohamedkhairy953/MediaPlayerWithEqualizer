package com.khairy.moham.equalizerfirst.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.khairy.moham.equalizerfirst.R;
import com.khairy.moham.equalizerfirst.contractor.SongsManager;
import com.khairy.moham.equalizerfirst.contractor.Utilities;
import com.khairy.moham.equalizerfirst.presenter.songs_list.LoadEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by moham on 10/11/2017.
 */

public class MyService extends Service implements MediaPlayer.OnCompletionListener {
    MediaPlayer mediaPlayer;
    boolean isRepeat;
    int currentSongIndex;
    boolean isShuffle;
    ArrayList<HashMap<String, String>> myList = Utilities.songList;
    private int current_position = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String path = intent.getStringExtra("path");
        current_position = intent.getIntExtra("current_position", 0);
        isShuffle = intent.getBooleanExtra("is_shuffle", false);
        isRepeat = intent.getBooleanExtra("is_repeat", false);
        currentSongIndex = intent.getIntExtra("current_index", 0);
        new SongsManager(new LoadEventListener() {
            @Override
            public void LoadedSuccessfuly(ArrayList<HashMap<String, String>> list) {
                myList=list;
                playSong(current_position, currentSongIndex);

            }

            @Override
            public void loadFailed() {

            }
        });
        return START_REDELIVER_INTENT;
    }

    public void playSong(int position, int current_index) {

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(myList.get(current_index).get("songTitle"));
            mediaPlayer.prepare();
            mediaPlayer.seekTo(position);
            mediaPlayer.start();
            // Displaying Song title
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (isRepeat) {
            // repeat is on play same song again
            playSong(current_position, currentSongIndex);
        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((myList.size() - 1) + 1);
            playSong(current_position, currentSongIndex);
        } else {
            // no repeat or shuffle ON - play next song
            if (currentSongIndex < (myList.size() - 1)) {
                playSong(current_position, currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            } else {
                // play first song
                playSong(current_position, 0);
                currentSongIndex = 0;
            }
        }
    }
}
