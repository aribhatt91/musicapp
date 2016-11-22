package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.section;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.EnhancedViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.HeterogeneousAdapter;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.Utility.PlaylistDialog;

import java.util.ArrayList;
import java.util.List;

public class SongSection extends HeterogeneousAdapter.ListSection<Song> {

    public static final int ID = 9149;

    public SongSection(@NonNull ArrayList<Song> data) {
        super(ID, data);
    }

    @Override
    public EnhancedViewHolder<Song> createViewHolder(HeterogeneousAdapter adapter,
                                                                  ViewGroup parent) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.song_item, parent, false),
                getData());
    }

    public static class ViewHolder extends EnhancedViewHolder<Song>
            implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private View itemView,divider;
        private TextView songName;
        private TextView detailText;
        private ImageView art;

        protected Song reference;
        protected int index;

        private ArrayList<Song> songList;

        public ViewHolder(View itemView, ArrayList<Song> songList) {
            super(itemView);
            this.itemView = itemView;
            this.songList = songList;

            songName = (TextView) itemView.findViewById(R.id.songTitle);
            detailText = (TextView) itemView.findViewById(R.id.songArtist);
            art = (ImageView) itemView.findViewById(R.id.list_image);
            divider = itemView.findViewById(R.id.divider);

            ImageView moreButton = (ImageView) itemView.findViewById(R.id.expanded_menu);

            itemView.setOnClickListener(this);
            moreButton.setOnClickListener(this);
        }

        @Override
        public void update(Song s, int sectionPosition) {
            reference = s;
            index = sectionPosition;

            songName.setText(s.songName);
            detailText.setText(s.artistName + " - " + s.albumName);
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, s.albumId);
            Glide.with(itemView.getContext()).load(uri).placeholder(R.drawable.default_album_art_75).into(art);
            if(index== songList.size()-1)
                divider.setAlpha(0.0f);
            else
                divider.setAlpha(0.5f);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.instanceMore:
                    final PopupMenu menu = new PopupMenu(itemView.getContext(), v, Gravity.END);
                    String[] options = itemView.getResources()
                            .getStringArray(R.array.queue_options_song_instance);

                    for (int i = 0; i < options.length;  i++) {
                        menu.getMenu().add(Menu.NONE, i, i, options[i]);
                    }
                    menu.setOnMenuItemClickListener(this);
                    menu.show();
                    break;
                default:
                    if (songList != null) {
                        PlayerController.setQueue(songList, index);
                        PlayerController.begin();

                        if (Prefs.getPrefs(itemView.getContext())
                                .getBoolean(Prefs.SWITCH_TO_PLAYING, true)) {
                            Navigate.to(itemView.getContext(), NowPlayingActivity.class);
                        }
                    }
                    break;
            }
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

                case 2: //Add to playlist...
                    PlaylistDialog.AddToNormal.alert(itemView, reference, itemView.getContext()
                            .getString(R.string.header_add_song_name_to_playlist, reference));
                    return true;
                case 3:
                    Library.setRingtone(itemView.getContext(),reference.songId);
                    return true;
            }
            return false;
        }
    }
}
