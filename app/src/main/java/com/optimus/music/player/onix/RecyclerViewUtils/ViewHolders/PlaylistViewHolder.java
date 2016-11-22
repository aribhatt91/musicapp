package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders;

/**
 * Created by apricot on 13/12/15.
 */
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.optimus.music.player.onix.Common.Instances.AutoPlaylist;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.DetailScreens.PlaylistDetail;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.Common.Instances.Playlist;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.Utility.HalfSquareImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener , PopupMenu.OnMenuItemClickListener{

    private Context context;

    private TextView playlistName;
    private static HalfSquareImageView a1, a2, a3, a4;

    private Playlist reference;

    public PlaylistViewHolder(View itemView) {
        super(itemView);
        playlistName = (TextView) itemView.findViewById(R.id.playlistgrid_name);

        a1 = (HalfSquareImageView) itemView.findViewById(R.id.albumart_one);
        a2 = (HalfSquareImageView) itemView.findViewById(R.id.albumart_two);
        a3 = (HalfSquareImageView) itemView.findViewById(R.id.albumart_three);
        a4 = (HalfSquareImageView) itemView.findViewById(R.id.albumart_four);



        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

        context = itemView.getContext();
    }

    public void update(Playlist p){
        reference = p;


        a1.setImageResource(R.drawable.default_album_art);
        a2.setImageResource(R.drawable.default_album_art);
        a3.setImageResource(R.drawable.default_album_art);
        a4.setImageResource(R.drawable.default_album_art);

        if (p == null){
            playlistName.setText("");
        }
        else {
            playlistName.setText(p.playlistName);

        }
        final Context ctx = itemView.getContext();
        final Set<Long> albumIds = new LinkedHashSet<Long>();
        ArrayList<Song> songs = Library.getPlaylistEntries(ctx, reference);
        for(Song s : songs){
            albumIds.add(s.albumId);
            if(albumIds.size()>=4)
                break;
        }
        loadAlbumArts(ctx, albumIds);




    }

    private static void loadAlbumArts(Context context, Set<Long> set) {
        Iterator<Long> it = set.iterator();

        final Uri sArtworkUri = android.net.Uri
                .parse("content://media/external/audio/albumart");

        if(set.size()>=4){
            long id = it.next();
            Uri uri = ContentUris.withAppendedId(sArtworkUri,id);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a1);
            id = it.next();
            uri = ContentUris.withAppendedId(sArtworkUri,id);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a2);
            id = it.next();
            uri = ContentUris.withAppendedId(sArtworkUri,id);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a3);
            id = it.next();
            uri = ContentUris.withAppendedId(sArtworkUri,id);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a4);

        }
        else if(set.size()==3){
            long id = it.next();
            Uri uri = ContentUris.withAppendedId(sArtworkUri,id);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a1);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a4);

            id = it.next();
            uri = ContentUris.withAppendedId(sArtworkUri,id);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a2);
            id = it.next();
            uri = ContentUris.withAppendedId(sArtworkUri,id);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a3);


        }

        else if(set.size()==2){
            long id = it.next();
            Uri uri = ContentUris.withAppendedId(sArtworkUri,id);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a1);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a4);

            id = it.next();
            uri = ContentUris.withAppendedId(sArtworkUri, id);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a2);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a3);

        }
        else if(set.size()==1){
            long id = it.next();
            Uri uri = ContentUris.withAppendedId(sArtworkUri,id);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a1);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a4);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a2);
            Glide.with(context).load(uri).crossFade(300).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.default_album_art).into(a3);

        }
    }

    @Override
    public boolean onLongClick(View v) {
        final PopupMenu menu = new PopupMenu(context, v);
        String[] options =  context.getResources().getStringArray(R.array.queue_options_playlist);
        for (int i = 0; i < options.length;  i++) {
            menu.getMenu().add(Menu.NONE, i, i, options[i]);
        }
        menu.setOnMenuItemClickListener(this);
        menu.show();
        return true;
    }

    @Override
    public void onClick(View v){
        /*itemView.getContext().startActivity(new Intent(itemView.getContext(),
                PlaylistDetail.class)
                .putExtra("play_id", reference.playlistId)
                .putExtra("play_name", reference.playlistName));*/
        Navigate.to(context, PlaylistDetail.class,
                PlaylistDetail.PLAYLIST_EXTRA, reference);

        /*
        switch (v.getId()){
            case R.id.instanceMore:
                final PopupMenu menu = new PopupMenu(context, v, Gravity.END);
                String[] options = (reference instanceof AutoPlaylist)
                        ? context.getResources().getStringArray(R.array.queue_options_smart_playlist)
                        : context.getResources().getStringArray(R.array.queue_options_playlist);
                for (int i = 0; i < options.length;  i++) {
                    menu.getMenu().add(Menu.NONE, i, i, options[i]);
                }
                menu.setOnMenuItemClickListener(this);
                menu.show();
                break;
            default:
                Navigate.to(context, PlaylistActivity.class, PlaylistActivity.PLAYLIST_EXTRA, reference);
                break;
        }
        */
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case 0:
                if(!Library.getPlaylistEntries(context, reference).isEmpty()) {
                    PlayerController.setQueue(Library.getPlaylistEntries(context, reference), 0);
                    PlayerController.begin();
                    if (PlayerController.isShuffle())
                        PlayerController.toggleShuffle();
                }
                return true;
            case 1:
                if(!Library.getPlaylistEntries(context, reference).isEmpty()) {
                    PlayerController.setQueue(Library.getPlaylistEntries(context, reference), 0);
                    PlayerController.begin();
                    if (!PlayerController.isShuffle())
                        PlayerController.toggleShuffle();
                }
                return true;
            case 2: //Queue this playlist next
                PlayerController.queueNext(Library.getPlaylistEntries(context, reference));
                return true;
            case 3: //Queue this playlist last
                PlayerController.queueLast(Library.getPlaylistEntries(context, reference));
                return true;
            case 4: //Delete this playlist
                Library.removePlaylist(itemView, reference);
                return true;
        }
        return false;
    }
}