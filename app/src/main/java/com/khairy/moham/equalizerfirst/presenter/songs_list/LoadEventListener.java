package com.khairy.moham.equalizerfirst.presenter.songs_list;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by moham on 10/10/2017.
 */

 public interface LoadEventListener {
    void LoadedSuccessfuly(ArrayList<HashMap<String, String>>  list);
    void loadFailed();

}
