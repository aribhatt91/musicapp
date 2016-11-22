package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;
import com.optimus.music.player.onix.Common.Instances.Album;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.DetailScreens.AlbumDetailDemo;
import com.optimus.music.player.onix.DetailScreens.AllTracksByArtist;
import com.optimus.music.player.onix.DetailScreens.PlaylistDetail;
import com.optimus.music.player.onix.JukeBoxActivity.JukeBoxDBHelper;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.Common.Instances.Playlist;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.SettingsActivity.Prefs;

import com.optimus.music.player.onix.TagEditorActivity.SongTagActivity;
import com.optimus.music.player.onix.Utility.PlaylistDialog;
import com.optimus.music.player.onix.WhatsHotActivity.VideoLibrary;


import java.util.ArrayList;

public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        PopupMenu.OnMenuItemClickListener{

    private View itemView;
    private TextView songName;
    private TextView detailText;
    private ImageView imageView;
    Context context;

    protected Song reference;
    protected int index;


    private ArrayList<Song> songList;

    private View divider;


    public SongViewHolder(View itemView, ArrayList<Song> songList) {
        super(itemView);
        //final Context context = ctx;

        this.itemView = itemView;
        this.songList = songList;

        songName = (TextView) itemView.findViewById(R.id.songTitle);
        detailText = (TextView) itemView.findViewById(R.id.songArtist);
        imageView = (ImageView) itemView.findViewById(R.id.list_image);
        divider = itemView.findViewById(R.id.divider);

        ImageView moreButton = (ImageView) itemView.findViewById(R.id.expanded_menu);
        itemView.setOnClickListener(this);
        moreButton.setOnClickListener(this);

    }



    public void update(Song s, int index, Context context) {
        reference = s;
        this.index = index;
        this.context = context;

        final Context ctx = context;
        final long id = s.albumId;


        songName.setText(s.songName);
        detailText.setText(s.artistName + " \u2022 " + s.albumName);
        final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

        //final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final Uri uri = Util.getAlbumArtUri(s.albumId); //ContentUris.withAppendedId(sArtworkUri, id);
        Glide.with(ctx)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.default_album_art_75)
                .crossFade(700)
                .into(imageView);


        divider.setAlpha(0.4f);

        if(index==songList.size()-1)
            divider.setAlpha(0.0f);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.expanded_menu:
                final PopupMenu menu = new PopupMenu(itemView.getContext(), v, Gravity.AXIS_CLIP);
                String[] options = itemView.getResources()
                        .getStringArray(
                        R.array.queue_options_song
                );

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
                        jb.updateMostPlayed(reference.songId);
                        jb.close();
                    }catch (Exception e){
                        Crashlytics.log(e.getMessage());
                    }

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
            case 3:
                itemView.getContext().startActivity(new Intent(context, AlbumDetailDemo.class)
                        .putExtra("album_id", reference.albumId)
                        .putExtra("album_name", reference.albumName)
                        .putExtra("artist_name", reference.artistName));
                return true;
            case 4:
                itemView.getContext().startActivity(new Intent(itemView.getContext(), AllTracksByArtist.class)
                        .putExtra("artist_id", reference.artistId)
                        .putExtra("name", reference.artistName));
                return true;
            case 5: // set as ringtone
                Library.setRingtone(itemView.getContext(), reference.songId);
                return true;
            case 6:
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
            case 7:
                try {
                    JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
                    jb.insertFav(reference.songId, 1);
                    jb.close();
                }catch (Exception e){
                    Crashlytics.log(e.getMessage());
                }
                return true;
            case 8:
                try {
                    JukeBoxDBHelper jukeBoxDBHelper = new JukeBoxDBHelper(context);
                    jukeBoxDBHelper.deleteFav(reference.songId, 1);
                    jukeBoxDBHelper.close();
                }catch (Exception e){
                    Crashlytics.log(e.getMessage());
                }
                return true;
            case 9:
                //Library.showMediaDetails(itemView.getContext(), reference);
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

            case 10:

                Navigate.to(context, SongTagActivity.class,
                        SongTagActivity.TAGGER_EXTRA, reference);
                return true;
            case 11:// delete
                Library.deleteSongDialog(itemView.getContext(), reference, true);
                return true;


        }
        return false;
    }

}