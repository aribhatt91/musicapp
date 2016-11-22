package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.section;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.optimus.music.player.onix.Common.Instances.Playlist;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.DraggableSongViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.EnhancedViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.HeterogeneousAdapter;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.PlaylistSongViewHolder;

import java.util.ArrayList;

public class PlaylistSongSection extends EditableSongSection {

    public static final int ID = 720;

    private Context mContext;
    private DraggableSongViewHolder.OnRemovedListener mRemovedListener;
    private Playlist mReference;

    public PlaylistSongSection(ArrayList<Song> data, Context context,
                               DraggableSongViewHolder.OnRemovedListener onRemovedListener,
                               Playlist reference) {
        super(ID, data);
        mContext = context;
        mRemovedListener = onRemovedListener;
        mReference = reference;
    }

    @Override
    protected void onDrop(int from, int to) {
        if (from == to) return;

        Library.editPlaylist(mContext, mReference, mData);
    }

    @Override
    public EnhancedViewHolder<Song> createViewHolder(final HeterogeneousAdapter adapter,
                                                     ViewGroup parent) {
        return new PlaylistSongViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.instance_song_drag, parent, false),
                mData,
                mReference,
                mRemovedListener);
    }
}
