package com.khairy.moham.equalizerfirst.contractor;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khairy.moham.equalizerfirst.R;
import com.khairy.moham.equalizerfirst.presenter.songs_list.SongsListPresenter;
import com.khairy.moham.equalizerfirst.presenter.songs_list.SongsListPresenterImpl;
import com.khairy.moham.equalizerfirst.view.songs_list_view.PlayListItem_View;

/**
 * Created by moham on 10/10/2017.
 */

public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.VHolder> {
    private SongsListPresenterImpl presenter;

    public SongsListAdapter(SongsListPresenterImpl presenter) {
        this.presenter = presenter;
    }

    @Override
    public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        VHolder vHolder = new VHolder(view,presenter);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(VHolder holder, int position) {
        Log.d("fffff", position + "");
        presenter.onBindView(holder, position);

    }

    @Override
    public int getItemCount() {
        return presenter.getListSize();
    }

    public static class VHolder extends RecyclerView.ViewHolder implements PlayListItem_View,View.OnClickListener {
        TextView textView;
        SongsListPresenterImpl presenter;

        VHolder(View itemView, SongsListPresenterImpl presenter) {

            super(itemView);
            this.presenter = presenter;
            textView = itemView.findViewById(R.id.songTitle);
            textView.setOnClickListener(this);
        }

        @Override
        public void setSongTitle(String title) {
            textView.setText(title);
        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            presenter.onItemClick(position);
        }
    }
}
