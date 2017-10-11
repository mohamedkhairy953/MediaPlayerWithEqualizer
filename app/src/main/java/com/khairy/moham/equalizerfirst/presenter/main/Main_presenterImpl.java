package com.khairy.moham.equalizerfirst.presenter.main;

import com.khairy.moham.equalizerfirst.contractor.SongsManager;
import com.khairy.moham.equalizerfirst.contractor.Utilities;
import com.khairy.moham.equalizerfirst.presenter.songs_list.LoadEventListener;
import com.khairy.moham.equalizerfirst.view.main_niew.PlayerView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by moham on 10/11/2017.
 */

 public class Main_presenterImpl implements Main_presenter,LoadEventListener {
    private PlayerView view;
    private SongsManager manager;
    private Utilities utils;

    public Main_presenterImpl(PlayerView view) {
        this.view = view;
        manager=new SongsManager(this);
        utils=new Utilities();
    }

    @Override
    public void loadSongs() {
        manager.getPlayList();

    }

    @Override
    public String milliToTimer(long millis) {
      return   utils.milliSecondsToTimer(millis);
    }

    @Override
    public int getProgressPercentage(long currentDuration, long totalDuration) {
        return utils.getProgressPercentage(currentDuration,totalDuration);
    }

    @Override
    public int progressToTimer(int progress, int totalDuration) {
       return utils.progressToTimer(progress,totalDuration);
    }

    @Override
    public void LoadedSuccessfuly(ArrayList<HashMap<String, String>> list) {
        view.playFirstSong(list);
    }

    @Override
    public void loadFailed() {
      view.failed();
    }
}
