package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.section;

import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.DraggableSongViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.EnhancedViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.HeterogeneousAdapter;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.QueueSongViewHolder;

import java.util.ArrayList;
import java.util.List;

public class QueueSection extends EditableSongSection implements PlayerController.UpdateListener {

    public static final int ID = 721;

    public QueueSection(ArrayList<Song> data) {
        super(ID, data);
    }

    @Override
    public void onUpdate() {
        if(mData!=null && !mData.equals(PlayerController.getQueue())){
            mData=(ArrayList<Song>) PlayerController.getQueue();
        }
    }

    @Override
    protected void onDrop(int from, int to) {
        if (from == to) return;

        // Calculate where the current song index is moving to
        final int nowPlayingIndex = PlayerController.getQueuePosition();
        int futureNowPlayingIndex;

        if (from == nowPlayingIndex) {
            futureNowPlayingIndex = to;
        } else if (from < nowPlayingIndex && to >= nowPlayingIndex) {
            futureNowPlayingIndex = nowPlayingIndex - 1;
        } else if (from > nowPlayingIndex && to <= nowPlayingIndex) {
            futureNowPlayingIndex = nowPlayingIndex + 1;
        } else {
            futureNowPlayingIndex = nowPlayingIndex;
        }

        // Push the change to the service
        PlayerController.editQueue(mData, futureNowPlayingIndex);
    }

    @Override
    public EnhancedViewHolder<Song> createViewHolder(final HeterogeneousAdapter adapter, ViewGroup parent) {
        return new QueueSongViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.instance_song_drag_highlight, parent, false),
                new DraggableSongViewHolder.OnRemovedListener() {
                    @Override
                    public void onItemRemoved(int index) {
                        mData.remove(index);

                        // Calculate the new song index of the track that's currently playing
                        int playingIndex = PlayerController.getQueuePosition();
                        if (index < playingIndex) {
                            playingIndex--;
                        }

                        // push the change to the service
                        PlayerController.editQueue(mData, playingIndex);

                        adapter.notifyItemRemoved(index);
                        adapter.notifyItemChanged(index);

                    }
                });
    }
}
