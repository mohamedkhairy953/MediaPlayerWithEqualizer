package com.khairy.moham.equalizerfirst.presenter.main;

import com.khairy.moham.equalizerfirst.contractor.MyMediaPlayer;
import com.khairy.moham.equalizerfirst.contractor.SongsManager;
import com.khairy.moham.equalizerfirst.contractor.Utilities;
import com.khairy.moham.equalizerfirst.presenter.songs_list.LoadEventListener;
import com.khairy.moham.equalizerfirst.view.main_niew.PlayerView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by moham on 10/11/2017.
 */

public class Main_presenterImpl implements Main_presenter, LoadEventListener {
    private PlayerView view;
    private SongsManager manager;
    private Utilities utils;
    private MyMediaPlayer mediaPlayer;
    private ArrayList<HashMap<String, String>> list;

    public Main_presenterImpl(PlayerView view) {
        this.view = view;
        manager = new SongsManager(this);
        mediaPlayer = MyMediaPlayer.getInstance();
        utils=new Utilities();
    }

    @Override
    public void loadSongs() {
        manager.getPlayList();

    }

    @Override
    public String milliToTimer(long millis) {
        return utils.milliSecondsToTimer(millis);
    }

    @Override
    public int getProgressPercentage(long currentDuration, long totalDuration) {
        return utils.getProgressPercentage(currentDuration, totalDuration);
    }

    @Override
    public int progressToTimer(int progress, int totalDuration) {
        return utils.progressToTimer(progress, totalDuration);
    }

    @Override
    public void playMedia() {
        mediaPlayer.play(new PlaySongListner() {
            @Override
            public void onPlay() {
                view.onMediaPlay();
            }

            @Override
            public void onPause() {
                view.onMediaPause();
            }

            @Override
            public void onFail() {

            }
        });
    }

    @Override
    public void doBackward(int seekBackwardTime) {
        mediaPlayer.backward(seekBackwardTime);
    }

    @Override
    public void doForward(int seekForwardTime) {
        mediaPlayer.forward(seekForwardTime);
    }

    @Override
    public void doPlaySong(int currentSongIndex) {
        final String songTitle = list.get(currentSongIndex).get("songTitle");
        mediaPlayer.playSong(currentSongIndex, new PlaySongListner() {
            @Override
            public void onPlay() {
                view.onPlaySong(songTitle);
            }

            @Override
            public void onPause() {

            }

            @Override
            public void onFail() {
                view.failedToPlaySong();
            }
        });
    }

    @Override
    public void doPlayNext(final int currentSongIndex) {
        mediaPlayer.playNext(currentSongIndex, new PlaySongListner() {
            @Override
            public void onPlay() {
                view.onPlaySong(list.get(mediaPlayer.getCurrentSongIndex()).get("songTitle"));
            }

            @Override
            public void onPause() {

            }

            @Override
            public void onFail() {

            }
        });
    }

    @Override
    public void doPlayPrevious(final int currentSongIndex) {
        mediaPlayer.playPreviuos(currentSongIndex, new PlaySongListner() {
            @Override
            public void onPlay() {
                view.onPlaySong(list.get(mediaPlayer.getCurrentSongIndex()).get("songTitle"));
            }

            @Override
            public void onPause() {

            }

            @Override
            public void onFail() {

            }
        });
    }

    @Override
    public void doRepeat() {
        mediaPlayer.onRepeatClicked(new TrueFalseListner() {
            @Override
            public void trueResponse() {
                view.onRepeatOn();
            }

            @Override
            public void falseResponse() {
               view.onRepeatOff();
            }
        });
    }

    @Override
    public void doShuffle() {
        mediaPlayer.onShuffleCicked(new TrueFalseListner() {
            @Override
            public void trueResponse() {
                view.onShuffleOn();
            }

            @Override
            public void falseResponse() {
                view.onShuffleOff();

            }
        });
    }

    @Override
    public void LoadedSuccessfuly(ArrayList<HashMap<String, String>> list) {
        this.list=list;
        Utilities.songList=list;
        view.playFirstSong();
    }

    @Override
    public void loadFailed() {
        view.failed();
    }
}
