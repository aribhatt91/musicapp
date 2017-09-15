package com.optimus.music.player.onix.DetailScreens;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;

import android.content.res.ColorStateList;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.optimus.music.player.onix.Common.Instances.Genre;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.SongViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.SongViewHolderBlank;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.Themes;
import com.optimus.music.player.onix.Utility.HalfSquareImageView;
import com.optimus.music.player.onix.Utility.PlaylistDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class GenreDetailsDemo extends NowPlayingActivity {

    private ImageView mImageView;
    TextView album,artistname;

    private Context context;

    Bundle b;
    private RecyclerView list;
    private GenreSongsAdapter adapter;

    private ArrayList<Song> songEntries;
    private long genreID;
    private String name;
    Uri uri;
    private Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private Genre reference;
    FloatingActionButton fab;

    int frameCol = -1;
    RelativeLayout main;
    SlidingUpPanelLayout slide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Themes.setTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_square);

        try {

            Intent i = getIntent();
            b = i.getExtras();
            name = b.getString("genre_name");
            genreID = b.getLong("genreid");

            reference = new Genre(genreID, name);
        }catch (Exception e){

        }
        fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(this);

        context = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        adapter = new GenreSongsAdapter();



        Uri sArtworkUri = android.net.Uri.parse("content://media/external/audio/albumart");

        HalfSquareImageView i1, i2, i3, i4;
        i1 = (HalfSquareImageView) findViewById(R.id.albumart_one);
        i2 = (HalfSquareImageView) findViewById(R.id.albumart_two);
        i3 = (HalfSquareImageView) findViewById(R.id.albumart_three);
        i4 = (HalfSquareImageView) findViewById(R.id.albumart_four);




        if(genreID>0){

            album.setText("GENRE");
            artistname.setText(name);


            Cursor cur = getContentResolver().query(
                    MediaStore.Audio.Genres.Members.getContentUri("external", genreID),
                    Library.songProjection,
                    null, null, null);


            if (cur!=null && cur.getCount()>0) {

                Set<Long> set = new LinkedHashSet<Long>();


                for (int im = 0; im < cur.getCount(); im++) {
                    cur.moveToPosition(im);
                    long ids = cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    if(set.size()<4) {
                        set.add(ids);
                    }
                    songEntries.add(new Song(
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                            cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                            cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                            cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)),
                            cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                            cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                            cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))));
                }

                cur.close();

                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                list.setLayoutManager(layoutManager);

                    list.setAdapter(adapter);

                    long album_id = songEntries.get(0).albumId;
                    uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                    Glide.with(this).load(uri).into(mImageView);

                //colourize fab
                Bitmap art = Util.getAlbumArt(this, album_id);
                if(collapsingToolbarLayout!=null) {
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

                    album_id = it.next();
                    uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i1);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i4);

                    album_id = it.next();
                    uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                    Glide.with(this).load(uri).placeholder(R.drawable.default_album_art).into(i2);

                    album_id = it.next();
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

                int count = songEntries.size();
                String sub = count + " " + (count > 1 ? "songs" : "song");


            }
            else {
                Toast.makeText(this, "No Songs Found", Toast.LENGTH_LONG).show();
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
    }


    private void colorWindowsCollapsed(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                if(Prefs.colourSB(this)) {
                    window.setStatusBarColor(Themes.getPrimaryDark());
                }else{
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

    private void setPaddingStyle(){
        if(PlayerController.getNowPlaying()==null){
            main.setPadding(0,0,0,0);
        }else{
            if(main.getPaddingBottom()==0){
                main.setPadding(0,0,0, Util.getActionBatHeight(this));
            }
        }
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
                                Window window = GenreDetailsDemo.this.getWindow();
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                if(Prefs.colourSB(GenreDetailsDemo.this)) {
                                    window.setStatusBarColor(frameColor);
                                }else{
                                    window.setStatusBarColor(Themes.getBlack());
                                }
                                if (Prefs.colourNB(GenreDetailsDemo.this)) {
                                    window.setNavigationBarColor(frameColor);
                                }
                            }catch (Exception e){

                            }

                        }else{

                        }


                    }else{
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Prefs.colourNB(GenreDetailsDemo.this)){
                            getWindow().setNavigationBarColor(Themes.getPrimaryDark());
                        }
                    }

                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(slide.getPanelState()== SlidingUpPanelLayout.PanelState.COLLAPSED){
            colorWindowsCollapsed();
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
                    Util.showAd(getResources().getString(R.string.artist_inter), this);
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
        inflater.inflate(R.menu.menu_add,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_add:
                PlaylistDialog.AddToNormal.alert(
                        this.findViewById(android.R.id.content),
                        songEntries,
                        context.getString(R.string.header_add_song_name_to_playlist,
                                reference));
                return true;
        }
        return false;
    }


    public class GenreSongsAdapter extends RecyclerView.Adapter<SongViewHolderBlank> implements Library.LibraryRefreshListener{

        @Override
        public SongViewHolderBlank onCreateViewHolder(ViewGroup parent, int viewType) {

            return new SongViewHolderBlank(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.song_item, parent, false),
                    songEntries);
        }

        @Override
        public void onBindViewHolder(SongViewHolderBlank holder, int i) {
            holder.update(songEntries.get(i), i, context);
        }


        @Override
        public int getItemCount() {
            return songEntries.size();
        }

        @Override
        public void onLibraryRefreshed() {
            notifyDataSetChanged();
        }


    }


}