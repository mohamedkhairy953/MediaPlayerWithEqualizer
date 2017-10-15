package com.khairy.moham.equalizerfirst.view.main_niew;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by moham on 10/11/2017.
 */

public interface PlayerView {
    void playFirstSong();

    void onMediaPlay();

    void onMediaPause();

    void failed();

    void onPlaySong(String songTitle);

    void failedToPlaySong();

    void onRepeatOn();

    void onRepeatOff();

    void onShuffleOn();

    void onShuffleOff();
}
