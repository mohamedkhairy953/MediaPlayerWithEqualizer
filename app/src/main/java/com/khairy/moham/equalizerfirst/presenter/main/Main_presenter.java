package com.khairy.moham.equalizerfirst.presenter.main;

/**
 * Created by moham on 10/11/2017.
 */

public interface Main_presenter {
    void loadSongs();
    String milliToTimer(long millis);
    int getProgressPercentage(long currentDuration, long totalDuration);

    int progressToTimer(int progress, int totalDuration);

    void playMedia();

    void doBackward(int seekBackwardTime);
    void doForward(int seekForwardTime);

    void doPlaySong(int currentSongIndex);

    void doPlayNext(int currentSongIndex);

    void doPlayPrevious(int currentSongIndex);

    void doRepeat();

    void doShuffle();
}
