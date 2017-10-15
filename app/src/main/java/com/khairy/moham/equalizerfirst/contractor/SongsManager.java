package com.khairy.moham.equalizerfirst.contractor;


import com.khairy.moham.equalizerfirst.presenter.songs_list.LoadEventListener;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class SongsManager {
    // SDCard Path
    private final String MEDIA_PATH = "/storage/emulated/0/Music";
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private LoadEventListener listener;

    // Constructor
    public SongsManager(LoadEventListener listener) {
        this.listener = listener;

    }

    /**
     * Function to read all mp3 files from phone storage
     * and store the details in ArrayList
     */
    public void getPlayList() {
        if(Utilities.songList.size()>1){
            listener.LoadedSuccessfuly(Utilities.songList);
            return;
        }
        File home = new File(MEDIA_PATH);
        if (!home.exists()) {
            listener.loadFailed();
            return;
        }
        File[] files = home.listFiles(new FileExtensionFilter());
        if (files.length == 0) {
            listener.loadFailed();
        }
        if (files.length > 0) {
            for (File file : files) {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());

                // Adding each song to SongList
                songsList.add(song);
            }
        }
        if (songsList.isEmpty()) {
            listener.loadFailed();
        } else {
            listener.LoadedSuccessfuly(songsList);
        }
    }

    /**
     * Class to filter files which are having .mp3 extension
     */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}
