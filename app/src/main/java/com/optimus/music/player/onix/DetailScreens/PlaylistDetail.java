package com.optimus.music.player.onix.DetailScreens;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;

import android.content.res.ColorStateList;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.optimus.music.player.onix.Common.Instances.Playlist;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.DraggableSongViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.DragBackgroundDecoration;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.DragDropAdapter;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.DragDropDecoration;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.HeterogeneousAdapter;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.QueueSongViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.SongViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.section.LibraryEmptyState;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.section.PlaylistSongSection;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.Themes;
import com.optimus.music.player.onix.Utility.HalfSquareImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class PlaylistDetail extends NowPlayingActivity implements

        PopupMenu.OnMenuItemClickListener,
        DraggableSongViewHolder.OnRemovedListener{

    public static final String PLAYLIST_EXTRA = "playlist";

    private ImageView mImageView;
    TextView album,artistname;

    private Context context;

    private RecyclerView list;

    private ArrayList<Song> songEntries;
    private HeterogeneousAdapter madapter;
    private Playlist reference;

    int frameCol = -1;
    RelativeLayout main;
    SlidingUpPanelLayout slide;


    Bundle b;
    String name;
    long playlistId;
    FloatingActionButton fab;
    CollapsingToolbarLayout collapsingToolbarLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Themes.setTheme(this);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_square);

        try {

            reference = getIntent().getParcelableExtra(PLAYLIST_EXTRA);
            playlistId = reference.playlistId;
            name = reference.playlistName;
        }catch (Exception e){

        }



        context = this;
        fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(null);
            }
        }





        mImageView = (ImageView)findViewById(R.id.backdrop);
        list = (RecyclerView) findViewById(R.id.list);
        album = (TextView) findViewById(R.id.albums_name);
        artistname = (TextView) findViewById(R.id.artists_name);
        songEntries = new ArrayList<Song>();
        //adapter = new PlaylistSongsAdapter();


        Uri sArtworkUri = android.net.Uri.parse("content://media/external/audio/albumart");

        HalfSquareImageView i1, i2, i3, i4;
        i1 = (HalfSquareImageView) findViewById(R.id.albumart_one);
        i2 = (HalfSquareImageView) findViewById(R.id.albumart_two);
        i3 = (HalfSquareImageView) findViewById(R.id.albumart_three);
        i4 = (HalfSquareImageView) findViewById(R.id.albumart_four);

        songEntries = new ArrayList<Song>();
        artistname = (TextView) findViewById(R.id.artists_name);
        artistname.setText(name);



        songEntries = new ArrayList<>();


        Cursor cur = getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                Library.playlistEntryProjection,
                MediaStore.Audio.Media.IS_MUSIC + " != 0",
                null, null);



        if( cur!=null && cur.getCount()>0) {
            Set<Long> set = new LinkedHashSet<Long>();
            Set<String> exFol = Prefs.getExcludedFolders(this);

            for (int im = 0; im < cur.getCount(); im++) {
                cur.moveToPosition(im);
                Song s = new Song(
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE)),
                        cur.getLong(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM)),
                        cur.getLong(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.YEAR)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.DATE_ADDED)),
                        cur.getLong(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM_ID)),
                        cur.getLong(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST_ID)));
                File f = new File(s.location);
                String path = "";
                if(f.exists()){
                    path = f.getParent();
                }
                if(!exFol.contains(path)){
                    songEntries.add(s);
                    if (set.size() < 4) {
                        set.add(s.albumId);
                    }
                }
            }


            DragDropAdapter hAdapter = new DragDropAdapter();
            hAdapter.setDragSection(new PlaylistSongSection(songEntries, this, this, reference));
            hAdapter.attach(list);
            madapter = hAdapter;

            list.addItemDecoration(new DragBackgroundDecoration(Themes.getBackground(this)));

            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            list.setLayoutManager(layoutManager);

            list.addItemDecoration(new DragDropDecoration((NinePatchDrawable) ContextCompat.getDrawable(this,
                    (Themes.getThemeId(this) == 0 || Themes.getThemeId(this) == 6 || Themes.getThemeId(this) == 7)
                            ? R.drawable.list_drag_shadow_dark
                            : R.drawable.list_drag_shadow_light
            )));

            madapter.setEmptyState(new LibraryEmptyState(this) {
                @Override
                public String getEmptyMessage() {

                    return getString(R.string.empty_playlist);
                }

                @Override
                public String getEmptyMessageDetail() {

                    return getString(R.string.empty_playlist_detail);
                }

                @Override
                public String getEmptyAction1Label() {
                    return "";
                }

                @Override
                public void onAction1() {

                    super.onAction1();

                }
            });

            //list.setAdapter(adapter);
            if (cur.getCount() > 0 && songEntries.size()>0) {


                long album_id = songEntries.get(0).albumId;
                Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                Glide.with(this).load(uri).into(mImageView);

                //colourize fab
                Bitmap art = Util.getAlbumArt(this, album_id);
                if (collapsingToolbarLayout != null) {
                    colourElements(art);
                }

                if (set.size() == 1) {
                    Iterator<Long> it = set.iterator();

                    album_id = it.next();//songEntries.get(0).albumId;
                    uri = ContentUris.withAppendedId(sArtworkUri, album_id);

                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i1);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i2);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i3);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i4);
                }
                if (set.size() == 2) {
                    Iterator<Long> it = set.iterator();

                    album_id = it.next();//songEntries.get(0).albumId;
                    uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i1);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i4);

                    album_id = it.next();//songEntries.get(1).albumId;
                    uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i2);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i3);

                }
                if (set.size() == 3) {
                    Iterator<Long> it = set.iterator();

                    album_id = it.next();//songEntries.get(0).albumId;
                    uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i1);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i4);

                    album_id = it.next(); //songEntries.get(1).albumId;
                    uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i2);

                    album_id = it.next();//songEntries.get(2).albumId;
                    uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i3);

                }
                if (set.size() >= 4) {
                    Iterator<Long> it = set.iterator();

                    album_id = it.next(); //songEntries.get(0).albumId;
                    uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i1);

                    album_id = it.next();//songEntries.get(1).albumId;
                    uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i2);

                    album_id = it.next();//songEntries.get(2).albumId;
                    uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i3);

                    album_id = it.next();//songEntries.get(3).albumId;
                    uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i4);
                }


                int n = songEntries.size();

                String sub = n + ((n > 1) ? " songs" : " song");

            }

            cur.close();
        }

        slide = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        main = (RelativeLayout) findViewById(R.id.main);
        slide.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if(slide.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED){
                    if(frameCol==-1)
                        colorWindowsCollapsed();
                    else
                        colorWindowsCollapsedElement();
                }
            }
        });
        setPaddingStyle();

    }

    private void colourElements(Bitmap img){
        if(img!=null){
            Palette.from(img).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int frameColor = palette.getVibrantColor(Color.TRANSPARENT);
                    Palette.Swatch swatch = palette.getVibrantSwatch();

                    if (swatch == null || frameColor == Color.TRANSPARENT) {
                        frameColor = palette.getLightVibrantColor(Color.TRANSPARENT);
                        swatch = palette.getLightVibrantSwatch();
                    }
                    if (swatch == null || frameColor == Color.TRANSPARENT) {
                        frameColor = palette.getDarkVibrantColor(Color.TRANSPARENT);
                        swatch = palette.getDarkVibrantSwatch();
                    }
                    if (swatch == null || frameColor == Color.TRANSPARENT) {
                        frameColor = palette.getLightMutedColor(Color.TRANSPARENT);
                        swatch = palette.getLightMutedSwatch();
                    }
                    if (swatch == null || frameColor == Color.TRANSPARENT) {
                        frameColor = palette.getDarkMutedColor(Color.TRANSPARENT);
                        swatch = palette.getDarkMutedSwatch();
                    }



                    if (swatch != null && frameColor != Color.TRANSPARENT) {
                        frameCol = frameColor;
                        try {
                            collapsingToolbarLayout.setBackgroundColor(frameColor);
                            collapsingToolbarLayout.setContentScrimColor(frameColor);
                            collapsingToolbarLayout.setStatusBarScrimColor(frameColor);
                            fab.setBackgroundTintList(ColorStateList.valueOf(frameColor));
                        }catch (Exception e){

                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            try {
                                fab.getDrawable().mutate().setTint(swatch.getTitleTextColor());
                                Window window =PlaylistDetail.this.getWindow();
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                if(Prefs.colourSB(PlaylistDetail.this)) {
                                    window.setStatusBarColor(frameColor);
                                }else {
                                    window.setStatusBarColor(Themes.getBlack());
                                }
                                if (Prefs.colourNB(PlaylistDetail.this)) {
                                    window.setNavigationBarColor(frameColor);
                                }
                            }catch (Exception e){

                            }

                        }

                    }else{
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Prefs.colourNB(PlaylistDetail.this)){
                            getWindow().setNavigationBarColor(Themes.getPrimary());
                        }
                    }

                }
            });
        }

    }






    private void colorWindowsCollapsed(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                if(Prefs.colourSB(this)) {
                    window.setStatusBarColor(Themes.getPrimaryDark());
                }else {
                    window.setStatusBarColor(Themes.getBlack());
                }
                if (Prefs.colourNB(this)) {
                    window.setNavigationBarColor(Themes.getPrimaryDark());
                }
            }catch (Exception e){

            }

        }

    }

    private void colorWindowsCollapsedElement(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                if(Prefs.colourSB(this)) {
                    window.setStatusBarColor(frameCol);
                }else{
                    window.setStatusBarColor(Themes.getBlack());
                }
                if (Prefs.colourNB(this)) {
                    window.setNavigationBarColor(frameCol);
                }
            }catch (Exception e){

            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(slide.getPanelState()== SlidingUpPanelLayout.PanelState.COLLAPSED){
            colorWindowsCollapsed();
        }
    }

    private void setPaddingStyle(){
        if(PlayerController.getNowPlaying()==null){
            main.setPadding(0,0,0,0);
        }else{
            if(main.getPaddingBottom()==0){
                main.setPadding(0,0,0, Util.getActionBatHeight(this));
            }
        }
    }


    @Override
    public void onUpdate() {
        super.onUpdate();
        setPaddingStyle();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.fab2:
                if(System.currentTimeMillis()%10==0) {
                    Util.showAd(getResources().getString(R.string.playlist_inter), this);
                }

                if(!songEntries.isEmpty()) {
                    Random r = new Random();
                    int index = r.nextInt(songEntries.size());
                    PlayerController.setQueue(songEntries, index);
                    PlayerController.begin();
                    if (!PlayerController.isShuffle())
                        PlayerController.toggleShuffle();
                }
                break;
            default:
                break;
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_playlist,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_sort:
                PopupMenu sortMenu = new PopupMenu(this, findViewById(R.id.action_sort), Gravity.END);
                sortMenu.inflate(R.menu.sort_options);
                sortMenu.setOnMenuItemClickListener(this);
                sortMenu.show();
                return true;
            case R.id.action_share:

                    ArrayList<Uri> files = new ArrayList<>();
                    if(songEntries!=null && !songEntries.isEmpty()) {
                        for(Song s : songEntries){
                            files.add(Uri.parse("file://" + s.location));
                        }
                        try {
                            Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
                            share.setType("audio/*");

                            share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                            context.startActivity(Intent.createChooser(share, "Share Playlist"));
                        }catch (Exception e){
                            Toast.makeText(context, "Oops, Something broke!", Toast.LENGTH_LONG).show();

                        }
                    }else{
                        Toast.makeText(context, "There are no songs to share!", Toast.LENGTH_LONG).show();

                    }

                return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final ArrayList<Song> unsortedData = new ArrayList<>(songEntries);
        String result = "Failed";

        switch (item.getItemId()) {
            case R.id.action_sort_random:
                if(songEntries!=null && !songEntries.isEmpty()) {
                    if (System.currentTimeMillis() % 10 == 0) {
                        Util.showAd(getResources().getString(R.string.playlist_inter), this);
                    }

                    Collections.shuffle(songEntries);
                    result = getResources().getString(R.string.message_sorted_playlist_random);
                }
                break;
            case R.id.action_sort_name:
                if(songEntries!=null && !songEntries.isEmpty()) {
                    if (System.currentTimeMillis() % 12 == 0) {
                        Util.showAd(getResources().getString(R.string.playlist_inter), this);
                    }

                    Collections.sort(songEntries);
                    result = getResources().getString(R.string.message_sorted_playlist_name);
                }
                break;
            case R.id.action_sort_artist:
                if(songEntries!=null && !songEntries.isEmpty()) {
                    Collections.sort(songEntries, Song.ARTIST_COMPARATOR);
                    result = getResources().getString(R.string.message_sorted_playlist_artist);
                }
                break;
            case R.id.action_sort_album:
                if(songEntries!=null && !songEntries.isEmpty()) {
                    Collections.sort(songEntries, Song.ALBUM_COMPARATOR);
                    result = getResources().getString(R.string.message_sorted_playlist_album);
                }
                break;
            case R.id.action_sort_play:
                if(songEntries!=null && !songEntries.isEmpty()) {
                    Collections.sort(songEntries, Song.PLAY_COUNT_COMPARATOR);
                    result = getResources().getString(R.string.message_sorted_playlist_play);
                }
                break;
            case R.id.action_sort_date_added:
                if(songEntries!=null && !songEntries.isEmpty()) {
                    if (System.currentTimeMillis() % 12 == 0) {
                        Util.showAd(getResources().getString(R.string.playlist_inter), this);
                    }

                    Collections.sort(songEntries, Song.DATE_ADDED_COMPARATOR);
                    result = getResources().getString(R.string.message_sorted_playlist_date_added);
                }
                break;
            default:
                break;
        }


        if(songEntries!=null && !songEntries.isEmpty()) {
            Library.editPlaylist(this, reference, songEntries);


            madapter.notifyDataSetChanged();

            Snackbar
                    .make(
                            findViewById(R.id.list),
                            String.format(result, reference),
                            Snackbar.LENGTH_LONG)
                    .setAction(
                            getResources().getString(R.string.action_undo),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    songEntries.clear();
                                    songEntries.addAll(unsortedData);
                                    Library.editPlaylist(
                                            PlaylistDetail.this, reference, unsortedData);
                                    madapter.notifyDataSetChanged();
                                }
                            })
                    .show();
        }
        return true;
    }

    @Override
    public void onItemRemoved(final int index) {
        final Song removed = songEntries.remove(index);

        Library.editPlaylist(PlaylistDetail.this, reference, songEntries);
        madapter.notifyItemRemoved(index);

        Snackbar
                .make(
                        findViewById(R.id.list),
                        getResources().getString(
                                R.string.message_removed_song,
                                removed.songName),
                        Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        songEntries.add(index, removed);
                        Library.editPlaylist(PlaylistDetail.this, reference, songEntries);
                        if (songEntries.size() > 1) {
                            madapter.notifyItemInserted(index);
                        } else {
                            madapter.notifyItemChanged(index);
                        }
                    }
                })
                .show();

    }



}
