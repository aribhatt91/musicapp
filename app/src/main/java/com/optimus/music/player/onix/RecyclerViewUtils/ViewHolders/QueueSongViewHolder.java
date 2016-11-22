package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.DetailScreens.AlbumDetailDemo;
import com.optimus.music.player.onix.DetailScreens.AllTracksByArtist;
import com.optimus.music.player.onix.JukeBoxActivity.JukeBoxDBHelper;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.Utility.PlaylistDialog;

import java.util.ArrayList;
import java.util.List;

public class QueueSongViewHolder extends DraggableSongViewHolder {

    private OnRemovedListener removedListener;
    private View nowPlayingIndicator;
    private Song thisSong;

    public QueueSongViewHolder(View itemView, OnRemovedListener listener) {
        super(itemView, null, R.array.edit_queue_options);
        this.removedListener = listener;
        this.nowPlayingIndicator = itemView.findViewById(R.id.instancePlayingIndicator);
    }

    @Override
    public void update(Song item, int position) {
        super.update(item, position);
        thisSong = item;

        if (PlayerController.getQueuePosition() == position) {
            nowPlayingIndicator.setVisibility(View.VISIBLE);
        } else {
            nowPlayingIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 0: //Go to artist
                JukeBoxDBHelper jb = new JukeBoxDBHelper(itemView.getContext());
                jb.insertFav(thisSong.songId,1);
                jb.updateMostPlayed(reference.songId);

                jb.close();

                return true;
            case 1: // Go to album
                itemView.getContext().startActivity(new Intent(itemView.getContext(), AlbumDetailDemo.class)
                        .putExtra("album_id", thisSong.albumId)
                        .putExtra("album_name", thisSong.albumName)
                        .putExtra("artist_name", thisSong.artistName));

                return true;
            case 2:
                String name = Library.getArtistNameById(itemView.getContext(), thisSong.artistId);
                //Toast.makeText(itemView.getContext(), name + "  "+ reference.artistId, Toast.LENGTH_LONG).show();
                itemView.getContext().startActivity(new Intent(itemView.getContext(), AllTracksByArtist.class)
                        .putExtra("artist_id", thisSong.artistId)
                        .putExtra("name", name));

                return true;
            case 3:
                PlaylistDialog.AddToNormal.alert(itemView, thisSong,
                        itemView.getResources().getString(
                                R.string.header_add_song_name_to_playlist, thisSong));
                return true;
            case 4: // Remove
                ArrayList<Song> editedQueue = new ArrayList<>(PlayerController.getQueue());
                if (editedQueue != null) {
                    int queuePosition = PlayerController.getQueuePosition();
                    int itemPosition = getAdapterPosition();

                    editedQueue.remove(itemPosition);
                    removedListener.onItemRemoved(itemPosition);

                    PlayerController.editQueue(
                            editedQueue,
                            (queuePosition > itemPosition)
                                    ? queuePosition - 1
                                    : queuePosition);

                    if (queuePosition == itemPosition) {
                        PlayerController.begin();
                    }
                }
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == itemView) {
            PlayerController.changeSong(index);
        } else {
            super.onClick(v);
        }
    }
}