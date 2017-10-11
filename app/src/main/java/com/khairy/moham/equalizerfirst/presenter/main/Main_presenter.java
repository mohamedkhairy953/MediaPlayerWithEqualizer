package com.khairy.moham.equalizerfirst.presenter.main;

/**
 * Created by moham on 10/11/2017.
 */

public interface Main_presenter {
    void loadSongs();
    String milliToTimer(long millis);
    int getProgressPercentage(long currentDuration, long totalDuration);

    int progressToTimer(int progress, int totalDuration);
}
