package com.khairy.moham.equalizerfirst.presenter.songs_list;

import com.khairy.moham.equalizerfirst.view.songs_list_view.SongsListView;
import com.khairy.moham.equalizerfirst.contractor.SongsManager;
import com.khairy.moham.equalizerfirst.view.songs_list_view.PlayListItem_View;
import com.khairy.moham.equalizerfirst.view.songs_list_view.SongsListBaseView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by moham on 10/10/2017.
 */

 public class SongsListPresenterImpl implements SongsListPresenter, LoadEventListener {
    private SongsManager songsManager;
    private SongsListView view;


    private ArrayList<HashMap<String, String>> list;

    public SongsListPresenterImpl(SongsListBaseView view) {
        if (view instanceof SongsListView) {
            this.view = (SongsListView) view;
        }
        songsManager = new SongsManager(this);
    }

    @Override
    public void loadSongs() {
        songsManager.getPlayList();
    }

    @Override
    public void LoadedSuccessfuly(ArrayList<HashMap<String, String>> list) {
        this.list = list;
        view.onSongsLoadSuccess();
    }

    @Override
    public void loadFailed() {
        view.onSongLoadFailure();
    }

    @Override
    public void onBindView(PlayListItem_View playListview, int position) {
        playListview.setSongTitle(list.get(position).get("songTitle"));
    }

    @Override
    public int getListSize() {
        return list.size();
    }

}
