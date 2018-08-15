package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.JukeBoxActivity.JukeBoxDBHelper;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.TagEditorActivity.SongTagActivity;
import com.optimus.music.player.onix.Utility.PlaylistDialog;
import com.optimus.music.player.onix.Utility.ViewUtils.TimeView;
import com.optimus.music.player.onix.WhatsHotActivity.VideoLibrary;

import java.util.ArrayList;

/**
 * Created by apricot on 22/5/16.
 */
public class AlbumSongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    private View itemView;
    private TextView songName, tracknum;
    private TimeView detailText;
    Context context;

    protected Song reference;
    protected int index;


    private ArrayList<Song> songList;

    private View divider;


    public AlbumSongViewHolder(View itemView, ArrayList<Song> songList) {
        super(itemView);
        //final Context context = ctx;

        this.itemView = itemView;
        this.songList = songList;

        songName = (TextView) itemView.findViewById(R.id.songTitle);
        detailText = (TimeView) itemView.findViewById(R.id.songArtist);
        tracknum = (TextView) itemView.findViewById(R.id.tracknum);
        divider = itemView.findViewById(R.id.divider);

        ImageView moreButton = (ImageView) itemView.findViewById(R.id.expanded_menu);
        itemView.setOnClickListener(this);
        moreButton.setOnClickListener(this);

    }



    public void update(Song s, int index, Context context) {
        reference = s;
        this.index = index;
        this.context = context;

        int num = s.trackNumber;
        String trackNumber = "-";
        if(num>0){
            num = num % 100;
            trackNumber = String.valueOf(num);
        }


        songName.setText(s.songName);
        detailText.setTime((int) s.songDuration);
        divider.setAlpha(0.4f);
        tracknum.setText(trackNumber);

        if(index==songList.size()-1)
            divider.setAlpha(0.0f);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.expanded_menu:
                final PopupMenu menu = new PopupMenu(itemView.getContext(), v);
                String[] options = itemView.getResources()
                        .getStringArray(
                                R.array.queue_options_song_instance
                        );

                for (int i = 0; i < options.length;  i++) {
                    menu.getMenu().add(Menu.NONE, i, i, options[i]);
                }
                menu.setOnMenuItemClickListener(this);
                menu.show();
                break;
            default:
                if (songList != null) {
                    JukeBoxDBHelper jb = new JukeBoxDBHelper(itemView.getContext());
                    jb.insertRecentSong(reference.songId, reference.albumId);
                    jb.updateMostPlayed(reference.songId);
                    //Toast.makeText(context, "Clicked", Toast.LENGTH_LONG).show();
                    PlayerController.setQueue(songList, songList.indexOf(reference));
                    PlayerController.begin();

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
                //Toast.makeText(context, "Queue", Toast.LENGTH_LONG).show();
                PlayerController.queueLast(reference);
                return true;
            case 2: //Add to playlist
                PlaylistDialog.AddToNormal.alert(itemView, reference, itemView.getContext()
                        .getString(R.string.header_add_song_name_to_playlist, reference));
                return true;
            case 3: // set as ringtone
                Library.setRingtone(itemView.getContext(), reference.songId);
                return true;
            case 4:
                try {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("audio/*");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + reference.location));
                    context.startActivity(Intent.createChooser(share, "Share File"));
                }
                catch (Exception e){
                    Toast.makeText(context, "Oops, Something broke!", Toast.LENGTH_LONG).show();
                }
                return true;

            case 5:
                JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
                jb.insertFav(reference.songId,1);
                return true;

            case 6:
                try {
                    String url = VideoLibrary.prepareSearchString(reference.songName + " " + reference.artistName);
                    Intent openLFMIntent = new Intent(Intent.ACTION_VIEW);
                    openLFMIntent.setData(Uri.parse(url));
                    itemView.getContext().startActivity(openLFMIntent);
                }
                catch (Exception e){
                    Toast.makeText(itemView.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case 7:

                Navigate.to(context, SongTagActivity.class,
                        SongTagActivity.TAGGER_EXTRA, reference);
                return true;


        }
        return false;
    }

}