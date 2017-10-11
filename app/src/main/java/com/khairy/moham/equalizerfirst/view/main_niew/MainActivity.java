package com.khairy.moham.equalizerfirst.view.main_niew;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.khairy.moham.equalizerfirst.R;
import com.khairy.moham.equalizerfirst.databinding.ActivityMainBinding;
import com.khairy.moham.equalizerfirst.presenter.main.Main_presenter;
import com.khairy.moham.equalizerfirst.presenter.main.Main_presenterImpl;
import com.khairy.moham.equalizerfirst.view.songs_list_view.SongsListActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, PlayerView {
    ActivityMainBinding binding;
    MediaPlayer mediaPlayer;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    Handler handler = new Handler();
    ArrayList<HashMap<String, String>> myList;
    Main_presenterImpl presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        presenter = new Main_presenterImpl(this);
        mediaPlayer = new MediaPlayer();
        presenter.loadSongs();
        binding.songProgressBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);


    }

    public void goToPlayList(View view) {
        Intent intent = new Intent(MainActivity.this, SongsListActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(currentSongIndex);
        }

    }

    public void onBtnPlay(View view) {
        // check for already playing
        if (mediaPlayer.isPlaying()) {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                // Changing button image to play button
                binding.btnPlay.setImageResource(R.drawable.btn_play);
            }
        } else {
            // Resume song
            if (mediaPlayer != null) {
                mediaPlayer.start();
                // Changing button image to pause button
                binding.btnPlay.setImageResource(R.drawable.btn_pause);
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (isRepeat) {
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((myList.size() - 1) + 1);
            playSong(currentSongIndex);
        } else {
            // no repeat or shuffle ON - play next song
            if (currentSongIndex < (myList.size() - 1)) {
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            } else {
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = presenter.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();

    }

    public void onBtnForward(View view) {
        int currentPosition = mediaPlayer.getCurrentPosition();
        if (currentPosition + seekForwardTime <= mediaPlayer.getDuration()) {
            mediaPlayer.seekTo(currentPosition + seekForwardTime);
        } else {
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }

    public void onBtnNext(View view) {
        if (isShuffle) {
            Random random = new Random();
            currentSongIndex = random.nextInt((myList.size() - 1) + 1);
            playSong(currentSongIndex);
        } else {
            if (currentSongIndex < (myList.size() - 1)) {
                playSong((currentSongIndex + 1));
            } else {
                playSong(0);
            }
        }
    }

    public void onBtnPreviuos(View view) {
        if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((myList.size() - 1) + 1);
            playSong(currentSongIndex);
        } else {
            if (currentSongIndex > 0) {
                playSong((currentSongIndex - 1));
            } else {
                playSong(myList.size() - 1);
            }
        }
    }

    public void onBtnBackward(View view) {
        int currentPosition = mediaPlayer.getCurrentPosition();
        if (currentPosition - seekBackwardTime > 0) {
            mediaPlayer.seekTo(currentPosition - seekBackwardTime);
        } else {
            mediaPlayer.seekTo(0);
        }
    }

    @Override
    public void playFirstSong(ArrayList<HashMap<String, String>> songs) {
        this.myList = songs;
        playSong(0);
    }

    @Override
    public void failed() {
        Toast.makeText(this, "Failed To Play Songs", Toast.LENGTH_SHORT).show();
    }

    public void playSong(int songIndex) {
        currentSongIndex = songIndex;
        // Play song
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(myList.get(songIndex).get("songPath"));
            mediaPlayer.prepare();
            mediaPlayer.start();
            // Displaying Song title
            String songTitle = myList.get(songIndex).get("songTitle");
            binding.songTitle.setText(songTitle);

            // Changing Button Image to pause image
            binding.btnPlay.setImageResource(R.drawable.btn_pause);

            // set Progress bar values
            binding.songProgressBar.setProgress(0);
            binding.songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        handler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            binding.songTotalDurationLabel.setText("" + presenter.milliToTimer(totalDuration));
            // Displaying time completed playing
            binding.songCurrentDurationLabel.setText("" + presenter.milliToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (presenter.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            binding.songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    public void onBtnRepeat(View view) {
        if (isRepeat) {
            isRepeat = false;
            Toast.makeText(this, "Repeat is OFF", Toast.LENGTH_SHORT).show();
            binding.btnRepeat.setImageResource(R.drawable.btn_repeat);
        } else {
            isRepeat = true;
            Toast.makeText(this, "Repeat is ON", Toast.LENGTH_SHORT).show();
            binding.btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
            isShuffle = false;
            binding.btnShuffle.setImageResource(R.drawable.btn_shuffle);
        }
    }

    public void onBtnShuffle(View view) {
        if (isShuffle) {
            isShuffle = false;
            Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
            binding.btnShuffle.setImageResource(R.drawable.btn_shuffle);
        } else {
            // make repeat to true
            isShuffle = true;
            Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
            // make shuffle to false
            isRepeat = false;
            binding.btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
            binding.btnRepeat.setImageResource(R.drawable.btn_repeat);
        }
    }


}