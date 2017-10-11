package com.khairy.moham.equalizerfirst.presenter.songs_list;

import com.khairy.moham.equalizerfirst.view.songs_list_view.PlayListItem_View;

/**
 * Created by moham on 10/10/2017.
 */

public interface SongsListPresenter {
    void loadSongs();
    void onBindView(PlayListItem_View playListView, int position);

    int getListSize();
}
