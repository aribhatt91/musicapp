package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.optimus.music.player.onix.Common.Instances.Genre;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.DetailScreens.GenreDetailsDemo;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.Utility.PlaylistDialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


public class GenreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    private Context context;

    private TextView genreName, songNum;
    private ImageView art1, art2, art3, art0, more, round;
    private Genre reference;
    final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    View divider;
    private boolean isGrid;

    ColorGenerator gen = ColorGenerator.MATERIAL;


    public GenreViewHolder(View itemView, boolean isGrid) {
        super(itemView);

        context = itemView.getContext();
        this.isGrid = isGrid;
        if(isGrid) {

            genreName = (TextView) itemView.findViewById(R.id.genre_name);
            songNum = (TextView) itemView.findViewById(R.id.genre_count);
            art0 = (ImageView) itemView.findViewById(R.id.albumart_zero);
            art1 = (ImageView) itemView.findViewById(R.id.albumart_one);
            art2 = (ImageView) itemView.findViewById(R.id.albumart_two);
            art3 = (ImageView) itemView.findViewById(R.id.albumart_three);
            more = (ImageView) itemView.findViewById(R.id.instanceMore);

        }else{
            genreName = (TextView) itemView.findViewById(R.id.songTitle);
            songNum = (TextView) itemView.findViewById(R.id.songArtist);
            round = (ImageView) itemView.findViewById(R.id.list_image);
            more = (ImageView) itemView.findViewById(R.id.expanded_menu);
            divider = itemView.findViewById(R.id.divider);



        }


        itemView.setOnClickListener(this);
        more.setOnClickListener(this);

    }

    public void update(Genre g){
        try {
            String cnt = "No Song";
            reference = g;
            long genreId = reference.genreId;
            if (isGrid) {
                art0.setImageResource(R.drawable.transparent);
                art1.setImageResource(R.drawable.transparent);
                art2.setImageResource(R.drawable.transparent);
                art3.setImageResource(R.drawable.transparent);

                if (genreId > 0) {
                    Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId);
                    String[] proj2 = {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ALBUM_ID};
                    String orderby = MediaStore.Audio.Media.DISPLAY_NAME;

                    Cursor tempcursor = context.getContentResolver().query(uri, proj2, null, null, orderby);

                    if (tempcursor != null) {
                        cnt = (tempcursor.getCount() == 1 ? tempcursor.getCount() + " Song" : tempcursor.getCount() + " Songs");
                    }

                    Set<Long> set = new LinkedHashSet<Long>();

                    if (tempcursor != null && tempcursor.moveToFirst()) {
                        if (Library.genMap.get(reference) != null) {
                            set.addAll(Library.genMap.get(reference));
                        } else {

                            do {
                                long id = tempcursor.getLong(tempcursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                                set.add(id);
                                if (set.size() >= 3)
                                    break;
                            } while (tempcursor.moveToNext());
                            Library.genMap.put(reference, set);

                        }

                        tempcursor.close();

                    }


                    genreName.setText(g.genreName.trim());
                    songNum.setText(cnt);

                    long id;


                    if (set.size() == 1) {
                        Iterator<Long> it = set.iterator();
                        id = it.next();
                        LoadAlbumArt(context, art0, id, 1);

                    } else if (set.size() == 2) {
                        Iterator<Long> it = set.iterator();
                        id = it.next();
                        LoadAlbumArt(context, art1, id, 2);


                        if (it.hasNext()) {

                            long id2 = it.next();
                            LoadAlbumArt(context, art3, id2, 2);

                        }
                    } else if (set.size() >= 3) {
                        Iterator<Long> it = set.iterator();
                        id = it.next();
                        LoadAlbumArt(context, art1, id, 3);


                        id = it.next();
                        LoadAlbumArt(context, art2, id, 3);

                        id = it.next();
                        LoadAlbumArt(context, art3, id, 3);

                    }
                } else {
                    art0.setImageResource(R.drawable.default_album_art_rect);
                }
            } else if (songNum != null && genreName != null && round != null && divider != null) {
                genreName.setText(g.genreName.trim());

                if (genreId > 0) {
                    try {

                        Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId);
                        String[] proj2 = {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ALBUM_ID};
                        String orderby = MediaStore.Audio.Media.DISPLAY_NAME;

                        Cursor tempcursor = context.getContentResolver().query(uri, proj2, null, null, orderby);

                        if (tempcursor != null) {
                            cnt = (tempcursor.getCount() == 1 ? tempcursor.getCount() + " Song" : tempcursor.getCount() + " Songs");
                        }
                        songNum.setText(cnt);
                        String name = g.genreName.trim();

                        String i = name.substring(0, 1);
                        int colour = gen.getColor(name);
                        TextDrawable.IBuilder ib = TextDrawable.builder().beginConfig().toUpperCase().bold().endConfig().round();
                        TextDrawable td = ib.build(i, colour);
                        round.setImageDrawable(td);
                    } catch (Exception e) {
                    }

                    divider.setAlpha(0.0f);


                }

            }
        }catch (Exception e){

        }

    }

    public void LoadAlbumArt(Context context, ImageView imageView, long album_id, int size) {
        try {


            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
            if(size==1){
                Glide.with(context).load(uri).placeholder(R.drawable.default_album_art_rect)
                        .override(300,300).into(imageView);

            }else {
                Glide.with(context).load(uri).placeholder(R.drawable.default_album_art_75)
                        .override(100,100).into(imageView);
            }


        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.instanceMore:
                final PopupMenu menu = new PopupMenu(context, v, Gravity.END);
                String[] options = context.getResources().getStringArray(R.array.queue_options_genre);
                for (int i = 0; i < options.length;  i++) {
                    menu.getMenu().add(Menu.NONE, i, i, options[i]);
                }
                menu.setOnMenuItemClickListener(this);
                menu.show();
                break;
            case R.id.expanded_menu :
                final PopupMenu men = new PopupMenu(context, v, Gravity.END);
                String[] option = context.getResources().getStringArray(R.array.queue_options_genre);
                for (int i = 0; i < option.length;  i++) {
                    men.getMenu().add(Menu.NONE, i, i, option[i]);
                }
                men.setOnMenuItemClickListener(this);
                men.show();
                break;

            default:
                context.startActivity(new Intent(context, GenreDetailsDemo.class)
                        .putExtra("genre_name", reference.genreName)
                        .putExtra("genreid", reference.genreId)) ;
        }

    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case 0:
                PlayerController.setQueue(Library.getGenreEntries(reference), 0);
                PlayerController.begin();
                if(!PlayerController.isShuffle())
                    PlayerController.toggleShuffle();
                return true;
            case 1: //Queue this genre next
                PlayerController.queueNext(Library.getGenreEntries(reference));
                return true;
            case 2: //Queue this genre last
                PlayerController.queueLast(Library.getGenreEntries(reference));
                return true;
            case 3: //Add to playlist
                PlaylistDialog.AddToNormal.alert(
                        itemView,
                        Library.getGenreEntries(reference),
                        itemView.getContext()
                                .getString(R.string.header_add_song_name_to_playlist, reference));
                return true;
        }
        return false;
    }
}
