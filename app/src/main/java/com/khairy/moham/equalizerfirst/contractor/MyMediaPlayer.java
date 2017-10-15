package com.khairy.moham.equalizerfirst.contractor;

import android.media.MediaPlayer;
import android.os.Handler;

import com.khairy.moham.equalizerfirst.R;
import com.khairy.moham.equalizerfirst.presenter.main.PlaySongListner;
import com.khairy.moham.equalizerfirst.presenter.main.TrueFalseListner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by moham on 10/13/2017.
 */

public class MyMediaPlayer extends MediaPlayer {
    private static final MyMediaPlayer mediaPlayer = new MyMediaPlayer();
    private boolean shuffle;
    private boolean repeat;
    private int currentSongIndex;
    public Handler handler=new Handler();
    public static MyMediaPlayer getInstance() {
        return mediaPlayer;
    }

    private MyMediaPlayer() {
    }

    public int getTotalDuration() {

        return mediaPlayer.getDuration();
    }

    public void forward(int forward_seek_time) {
        int currentPosition = getPosition();
        if (currentPosition + forward_seek_time <= getTotalDuration()) {
            mediaPlayer.seekTo(currentPosition + forward_seek_time);
        } else {
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }

    }

    public void backward(int backward_seek_time) {
        int currentPosition = getPosition();
        if (currentPosition - backward_seek_time > 0) {
            mediaPlayer.seekTo(currentPosition - backward_seek_time);
        } else {
            mediaPlayer.seekTo(0);
        }
    }

    public int getPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void setSessionId(int id) {
        mediaPlayer.setAudioSessionId(id);
    }

    public int getSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    public void play(PlaySongListner listner) {
        if (mediaPlayer.isPlaying()) {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                // Changing button image to play button
                listner.onPause();
            }
        } else {
            // Resume song
            if (mediaPlayer != null) {
                mediaPlayer.start();
                // Changing button image to pause button
                // binding.btnPlay.setImageResource(R.drawable.btn_pause);
                listner.onPlay();
            }
        }

    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void playSong(int  currentSongIndex, PlaySongListner listner) {
        String path =Utilities.songList.get(currentSongIndex).get("songPath");
         setCurrentSongIndex(currentSongIndex);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            listner.onPlay();

        } catch (Exception e) {
            listner.onFail();
        }
    }

    public void playNext(int currentSongIndex, PlaySongListner listner) {
        ArrayList<HashMap<String, String>> list = Utilities.songList;
        if (shuffle) {
            Random random = new Random();
            currentSongIndex = random.nextInt((list.size() - 1) + 1);
            playSong(currentSongIndex, listner);
        } else {
            if (currentSongIndex < (list.size() - 1)) {
                playSong(currentSongIndex +1, listner);
            } else {
                playSong(0, listner);
            }
        }

    }

    public void playPreviuos(int currentSongIndex, PlaySongListner listner) {
        ArrayList<HashMap<String, String>> list = Utilities.songList;
        if (shuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((list.size() - 1) + 1);
            playSong(currentSongIndex, listner);
        } else {
            if (currentSongIndex > 0) {
                playSong(currentSongIndex - 1, listner);
            } else {
                playSong(list.size() - 1, listner);

            }
        }

    }

    public void onRepeatClicked(TrueFalseListner listner) {
        if (mediaPlayer.repeat) {
            mediaPlayer.setRepeat(false);
            listner.falseResponse();
        } else {
            mediaPlayer.setShuffle(false);
            mediaPlayer.setRepeat(true);
            listner.trueResponse();
        }
    }

    public void onShuffleCicked(TrueFalseListner listner) {
        if (mediaPlayer.shuffle) {
            listner.falseResponse();
            mediaPlayer.setShuffle(false);
        } else {
            mediaPlayer.setShuffle(true);
            mediaPlayer.setRepeat(false);
            listner.trueResponse();
        }
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }

}
