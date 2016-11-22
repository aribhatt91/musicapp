package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders;

import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.optimus.music.player.onix.JukeBoxActivity.JukeBoxDBHelper;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.EnhancedViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.SettingsActivity.Prefs;

import java.util.ArrayList;
import java.util.List;

public abstract class DraggableSongViewHolder extends EnhancedViewHolder<Song>
        implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private TextView songName;
    private TextView detailText;

    protected Song reference;
    protected int index;

    @ArrayRes
    private int menuRes;
    private ArrayList<Song> songList;

    public interface OnRemovedListener {
        void onItemRemoved(int index);
    }

    public DraggableSongViewHolder(View itemView, @Nullable ArrayList<Song> songList,
                                  @ArrayRes int menuRes) {
        super(itemView);
        this.songList = songList;
        this.menuRes = menuRes;

        songName = (TextView) itemView.findViewById(R.id.instanceTitle);
        detailText = (TextView) itemView.findViewById(R.id.instanceDetail);
        ImageView moreButton = (ImageView) itemView.findViewById(R.id.instanceMore);

        itemView.setOnClickListener(this);
        moreButton.setOnClickListener(this);
    }

    @Override
    public void update(Song item, int position) {
        reference = item;
        index = position;

        songName.setText(item.songName);
        detailText.setText(item.artistName + " - " + item.albumName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.instanceMore:
                final PopupMenu menu = new PopupMenu(itemView.getContext(), v);
                String[] options = itemView.getResources().getStringArray(menuRes);

                for (int i = 0; i < options.length;  i++) {
                    menu.getMenu().add(Menu.NONE, i, i, options[i]);
                }
                menu.setOnMenuItemClickListener(this);
                menu.show();
                break;
            default:
                if (songList != null) {
                    try {
                        JukeBoxDBHelper jb = new JukeBoxDBHelper(itemView.getContext());
                        jb.insertRecentSong(reference.songId, reference.albumId);
                        jb.close();
                    }catch (Exception e){
                        Crashlytics.log(e.getMessage());
                    }

                    PlayerController.setQueue(songList, songList.indexOf(reference));
                    PlayerController.begin();

                }
                break;
        }
    }
}