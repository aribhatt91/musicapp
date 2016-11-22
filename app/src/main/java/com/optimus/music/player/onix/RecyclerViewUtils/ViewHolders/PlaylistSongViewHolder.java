package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders;

import android.view.MenuItem;
import android.view.View;

import com.optimus.music.player.onix.Common.Instances.AutoPlaylist;
import com.optimus.music.player.onix.Common.Instances.Playlist;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.DraggableSongViewHolder;
import com.optimus.music.player.onix.Utility.PlaylistDialog;

import java.util.ArrayList;

/**
 * Created by apricot on 11/3/16.
 */
public class PlaylistSongViewHolder extends DraggableSongViewHolder {

    private DraggableSongViewHolder.OnRemovedListener removedListener;
    private boolean isReferenceAuto;

    public PlaylistSongViewHolder(View itemView, ArrayList<Song> playlistEntries, Playlist reference,
                                  OnRemovedListener listener) {
        super(itemView, playlistEntries,
                        R.array.edit_playlist_options);
        this.removedListener = listener;
        this.isReferenceAuto = reference instanceof AutoPlaylist;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 0: //Queue this song next
                PlayerController.queueNext(reference);
                return true;
            case 1: //Queue this song last
                PlayerController.queueLast(reference);
                return true;
            case 2: //Go to artist

                return true;
            case 3: // Go to album

                return true;
            case 4:
                removedListener.onItemRemoved(getAdapterPosition());
                return true;
        }
        return false;
    }

}