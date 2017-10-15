package com.khairy.moham.equalizerfirst.view.main_niew;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.Equalizer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.khairy.moham.equalizerfirst.R;
import com.khairy.moham.equalizerfirst.contractor.MyMediaPlayer;
import com.khairy.moham.equalizerfirst.contractor.Utilities;
import com.khairy.moham.equalizerfirst.databinding.ActivityMainBinding;
import com.khairy.moham.equalizerfirst.presenter.main.Main_presenter;
import com.khairy.moham.equalizerfirst.presenter.main.Main_presenterImpl;
import com.khairy.moham.equalizerfirst.service.MyService;
import com.khairy.moham.equalizerfirst.view.songs_list_view.SongsListActivity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, PlayerView {
    ActivityMainBinding binding;
    MyMediaPlayer mediaPlayer;
    Handler handler = new Handler();
    ArrayList<HashMap<String, String>> myList;
    Main_presenterImpl presenter;
    private Equalizer mEqualizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        presenter = new Main_presenterImpl(this);
        mediaPlayer = MyMediaPlayer.getInstance();
        mediaPlayer.setSessionId(95);
        presenter.loadSongs();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mEqualizer = new Equalizer(AudioEffect.CONTENT_TYPE_VOICE, mediaPlayer.getSessionId());
        mEqualizer.setEnabled(false);
        setupEqualizerView();
        binding.songProgressBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);

    }

    private void setupEqualizerView() {
        LinearLayout mLinearLayout = binding.equalizerLayout;

//        equalizer heading
        TextView equalizerHeading = new TextView(this);
        equalizerHeading.setText("Equalizer");
        equalizerHeading.setTextSize(20);
        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);
        mLinearLayout.addView(equalizerHeading);

//        get number frequency bands supported by the equalizer engine
        short numberFrequencyBands = mEqualizer.getNumberOfBands();

//        get the level ranges to be used in setting the band level
//        get lower limit of the range in milliBels
        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
//        get the upper limit of the range in millibels
        final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];

//        loop through all the equalizer bands to display the band headings, lower
//        & upper levels and the seek bars
        for (short i = 0; i < numberFrequencyBands; i++) {
            final short equalizerBandIndex = i;

//            frequency header for each seekBar
            TextView frequencyHeaderTextview = new TextView(this);
            frequencyHeaderTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            frequencyHeaderTextview.setGravity(Gravity.CENTER_HORIZONTAL);
            frequencyHeaderTextview
                    .setText((mEqualizer.getCenterFreq(equalizerBandIndex) / 1000) + " Hz");
            mLinearLayout.addView(frequencyHeaderTextview);

//            set up linear layout to contain each seekBar
            LinearLayout seekBarRowLayout = new LinearLayout(this);
            seekBarRowLayout.setOrientation(LinearLayout.HORIZONTAL);

//            set up lower level textview for this seekBar
            TextView lowerEqualizerBandLevelTextview = new TextView(this);
            lowerEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            lowerEqualizerBandLevelTextview.setText((lowerEqualizerBandLevel / 100) + " dB");
//            set up upper level textview for this seekBar
            TextView upperEqualizerBandLevelTextview = new TextView(this);
            upperEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            upperEqualizerBandLevelTextview.setText((upperEqualizerBandLevel / 100) + " dB");

            //            **********  the seekBar  **************
//            set the layout parameters for the seekbar
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;

//            create a new seekBar
            SeekBar seekBar = new SeekBar(this);
//            give the seekBar an ID
            seekBar.setId(i);

            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
//            set the progress for this seekBar
            seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex));

//            change progress as its changed by moving the sliders
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    mEqualizer.setBandLevel(equalizerBandIndex,
                            (short) (progress + lowerEqualizerBandLevel));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    //not used
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    //not used
                }
            });

//            add the lower and upper band level textviews and the seekBar to the row layout
            seekBarRowLayout.addView(lowerEqualizerBandLevelTextview);
            seekBarRowLayout.addView(seekBar);
            seekBarRowLayout.addView(upperEqualizerBandLevelTextview);

            mLinearLayout.addView(seekBarRowLayout);

            //        show the spinner
            equalizeSound();
        }
    }

    private void equalizeSound() {

        ArrayList<String> equalizerPresetNames = new ArrayList<String>();
        ArrayAdapter<String> equalizerPresetSpinnerAdapter
                = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                equalizerPresetNames);
        equalizerPresetSpinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner equalizerPresetSpinner = (Spinner) findViewById(R.id.spinner);

//        get list of the device's equalizer presets
        for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
            equalizerPresetNames.add(mEqualizer.getPresetName(i));
        }

        equalizerPresetSpinner.setAdapter(equalizerPresetSpinnerAdapter);

//        handle the spinner item selections
        equalizerPresetSpinner.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                //first list item selected by default and sets the preset accordingly
                mEqualizer.usePreset((short) position);
//                get the number of frequency bands for this equalizer engine
                short numberFrequencyBands = mEqualizer.getNumberOfBands();
//                get the lower gain setting for this equalizer band
                final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];

//                set seekBar indicators according to selected preset
                for (short i = 0; i < numberFrequencyBands; i++) {
                    short equalizerBandIndex = i;
                    SeekBar seekBar = findViewById(equalizerBandIndex);
//                    get current gain setting for this equalizer band
//                    set the progress indicator of this seekBar to indicate the current gain value
                    seekBar.setProgress(mEqualizer
                            .getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                not used
            }
        });
    }

   /* displays the SeekBar sliders for the supported equalizer frequency bands
    user can move sliders to change the frequency of the bands*/


    public void goToPlayList(View view) {
        Intent intent = new Intent(MainActivity.this, SongsListActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
           int currentSongIndex = data.getExtras().getInt("songIndex");
           mediaPlayer.setCurrentSongIndex(currentSongIndex);
            // play selected song
            presenter.doPlaySong(currentSongIndex);
        }

    }

    public void onBtnPlay(View view) {
        presenter.playMedia();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        MyMediaPlayer myMediaPlayer = MyMediaPlayer.getInstance();
        if (myMediaPlayer.isRepeat()) {
            // repeat is on play same song again
            presenter.doPlaySong(myMediaPlayer.getCurrentSongIndex());
        } else if (myMediaPlayer.isShuffle()) {
            // shuffle is on - play a random song
            Random rand = new Random();
            myMediaPlayer.setCurrentSongIndex(rand.nextInt((myList.size() - 1) + 1));
            presenter.doPlaySong(myMediaPlayer.getCurrentSongIndex());
        } else {
            // no repeat or shuffle ON - play next song
            if (myMediaPlayer.getCurrentSongIndex() < (myList.size() - 1)) {
                presenter.doPlaySong(myMediaPlayer.getCurrentSongIndex() + 1);
                myMediaPlayer.setCurrentSongIndex(myMediaPlayer.getCurrentSongIndex() + 1);
            } else {
                // play first song
                presenter.doPlaySong(0);
                myMediaPlayer.setCurrentSongIndex(0);

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
        int seekForwardTime = 5000;
        presenter.doForward(seekForwardTime);
    }

    public void onBtnNext(View view) {
        presenter.doPlayNext(mediaPlayer.getCurrentSongIndex());
    }

    public void onBtnPreviuos(View view) {
        presenter.doPlayPrevious(mediaPlayer.getCurrentSongIndex());
    }

    public void onBtnBackward(View view) {
        int seekBackwardTime = 5000;
        presenter.doBackward(seekBackwardTime);
    }

    @Override
    public void playFirstSong() {
        this.myList = Utilities.songList;
        presenter.doPlaySong(110);
    }

    @Override
    public void onMediaPlay() {
        binding.btnPlay.setImageResource(R.drawable.btn_pause);

    }

    @Override
    public void onMediaPause() {
        binding.btnPlay.setImageResource(R.drawable.img_btn_play);
    }

    @Override
    public void failed() {
        Toast.makeText(this, "Failed To Play Songs", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlaySong(String songTitle) {
        binding.songTitle.setText(songTitle);
        binding.btnPlay.setImageResource(R.drawable.btn_pause);
        binding.songProgressBar.setProgress(0);
        binding.songProgressBar.setMax(100);
        updateProgressBar();

    }

    @Override
    public void failedToPlaySong() {
        Toast.makeText(this, "Failed to play song", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRepeatOn() {
        Toast.makeText(this, "Repeat is ON", Toast.LENGTH_SHORT).show();
        binding.btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
        binding.btnShuffle.setImageResource(R.drawable.btn_shuffle);
    }

    @Override
    public void onRepeatOff() {
        Toast.makeText(this, "Repeat is OFF", Toast.LENGTH_SHORT).show();
        binding.btnRepeat.setImageResource(R.drawable.btn_repeat);
    }

    @Override
    public void onShuffleOn() {
        Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
        binding.btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
        binding.btnRepeat.setImageResource(R.drawable.btn_repeat);
    }

    @Override
    public void onShuffleOff() {
        Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
        binding.btnShuffle.setImageResource(R.drawable.btn_shuffle);
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

          /*  Intent intent = new Intent(this, MyService.class);

            intent.putExtra("path", myList.get(currentSongIndex).get("songPath"));
            intent.putExtra("current_index", currentSongIndex);
            intent.putExtra("my_list", myList);
            intent.putExtra("is_shuffle", isShuffle);
            intent.putExtra("is_repeat", isRepeat);
            intent.putExtra("seek_forward_time", seekForwardTime);
            intent.putExtra("current_position", mediaPlayer.getCurrentPosition());*/

        //  startService(intent);

    }

    public void onBtnRepeat(View view) {
        presenter.doRepeat();
    }

    public void onBtnShuffle(View view) {
        presenter.doShuffle();

    }


}