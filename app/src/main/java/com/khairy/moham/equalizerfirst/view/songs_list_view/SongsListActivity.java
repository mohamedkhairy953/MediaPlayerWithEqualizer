package com.khairy.moham.equalizerfirst.view.songs_list_view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.khairy.moham.equalizerfirst.R;
import com.khairy.moham.equalizerfirst.contractor.SongsListAdapter;
import com.khairy.moham.equalizerfirst.contractor.PermissionUtils;
import com.khairy.moham.equalizerfirst.presenter.songs_list.SongsListPresenterImpl;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by moham on 10/10/2017.
 */

public class SongsListActivity extends AppCompatActivity implements SongsListView {
    SongsListPresenterImpl presenter;
    @BindView(R.id.list)
    RecyclerView recyclerView;
    SongsListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);
        ButterKnife.bind(this);
        if (!PermissionUtils.isCallProvided(this))
            PermissionUtils.callPermission(this);

        presenter = new SongsListPresenterImpl(this);
        presenter.loadSongs();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new SongsListAdapter(presenter);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onSongsLoadSuccess() {
    }

    @Override
    public void onSongLoadFailure() {
        Toast.makeText(this, "Faiiiiiiiiiled", Toast.LENGTH_SHORT).show();
    }
}
