package com.optimus.music.player.onix.DetailScreens;

import android.annotation.TargetApi;
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
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.crashlytics.android.Crashlytics;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.optimus.music.player.onix.Common.Instances.Album;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.JukeBoxActivity.JukeBoxDBHelper;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.AlbumSongViewHolder;

import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.Themes;
import com.optimus.music.player.onix.Utility.PlaylistDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class AlbumDetailDemo extends NowPlayingActivity{

    private ArrayList<Song> songsList;
    private Song s;
    private String albumName, detail;
    private Context context;
    private AlbumAdapter adap;
    private boolean isFav=false;

    public static final String ALBUM_EXTRA = "album";
    JukeBoxDBHelper jb;

    private static int defaultFrameColor;
    private static int defaultTitleColor;
    private static int defaultDetailColor;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton mFab;
    RelativeLayout main;
    SlidingUpPanelLayout slide;


    String[] arguments;
    long id;
    private Album reference;
    Bitmap art;
    int frameCol=-1, textCol=-1, subTextCol=-1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Themes.setTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        context = this;
        Bundle b = getIntent().getExtras();
        //reference = getIntent().getParcelableExtra(ALBUM_EXTRA);
        id = b.getLong("album_id");
        String path = ""; //= reference.artUri;
        //albumName = reference.getAlbumName();
        //detail = reference.artistName;

        jb = new JukeBoxDBHelper(this);



        if(System.currentTimeMillis()%1300==0){
            Util.showAd(getResources().getString(R.string.playlist_inter), this);
        }

        defaultFrameColor = ContextCompat.getColor(this, R.color.grid_background_default);
        defaultTitleColor = ContextCompat.getColor(this, R.color.grid_text);
        defaultDetailColor = ContextCompat.getColor(this, R.color.grid_detail_text);




        String[] args = new String[]{String.valueOf(id)};
        String select = MediaStore.Audio.Albums._ID + "=?";
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                Library.albumArtistProjection,
                select, args, null);
        if(cursor!=null && cursor.getCount()==1) {
            cursor.moveToFirst();
            albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM));
            detail = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST));
            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));
            int year = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR));
            int ns = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
            reference = new Album(id, albumName,ns, detail, year, path);
            cursor.close();
        }





        ImageView iv = (ImageView) findViewById(R.id.backdrop);
        RecyclerView rlist = (RecyclerView) findViewById(R.id.list);
        mFab = (FloatingActionButton) findViewById(R.id.fab2);
        mFab.setOnClickListener(this);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(null);
            }
        }

        songsList = new ArrayList<>();


        Glide.with(this).load("file://" + path).placeholder(R.drawable.default_album_art_500).into(iv);
        art = Util.getAlbumArt(this, id);


        if(Prefs.colourAlbum(this)){
            mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            mFab.setImageResource(R.drawable.ic_play_arrow_black_18dp);
        }

        /*if(collapsingToolbarLayout!=null)
        {
            colourElements(art, Prefs.colourAlbum(this));
        }*/
        int[] cols = Library.colorCache.get(reference);

        if(cols!=null && cols.length==3) {
            frameCol = cols[0];
            textCol = cols[1];
            subTextCol = cols[2];
            if (collapsingToolbarLayout != null) {
                colourViews();
            }
        }
        else{
            if(collapsingToolbarLayout!=null) {
                colourElements(art, Prefs.colourAlbum(this));
            }
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



        arguments = new String[]{String.valueOf(id)};
        String selection = MediaStore.Audio.Media.ALBUM_ID + "+?";

        Cursor cur = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Library.songProjection,
                selection,
                arguments,
                MediaStore.Audio.Media.TRACK);
        if(cur != null && cur.getCount()>0){
            cur.moveToFirst();

            for (int k = 0; k<cur.getCount(); k++){
                cur.moveToPosition(k);
                s = new Song(cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                        cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                        cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                        cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)),
                        cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                        cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)),
                        cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)),
                        cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)),
                        cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)),
                        cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)),
                        cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))
                );

                if(s.albumId==id)
                    songsList.add(s);
            }

            cur.close();

            adap = new AlbumAdapter();
            setPaddingStyle();

            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rlist.setLayoutManager(layoutManager);

            rlist.setAdapter(adap);

        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Prefs.colourNB(AlbumDetailDemo.this)){
                getWindow().setNavigationBarColor(Themes.getPrimaryDark());
            }
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

                if(!songsList.isEmpty()) {
                    if(PlayerController.isShuffle())
                        PlayerController.toggleShuffle();

                    PlayerController.setQueue(songsList, 0);
                    PlayerController.begin();
                    if(System.currentTimeMillis()%6==0){
                        Util.showAd(getResources().getString(R.string.playlist_inter), this);
                    }
                }

                break;
            default:
                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_album,menu);
        ArrayList<Long> ids = jb.getFavAlbums(2);
        if(ids.contains(id)){
            isFav=true;
            menu.getItem(0).setIcon(R.drawable.ic_favorite_white_24dp);
            menu.getItem(0).setTitle(getResources().getString(R.string.action_unlike));

        }
        else{
            isFav=false;
            menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_24dp);
            menu.getItem(0).setTitle(getResources().getString(R.string.action_like));


        }

        return super.onCreateOptionsMenu(menu);
    }

    private void colourElements(Bitmap img, final Boolean flag){
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
                        textCol = swatch.getTitleTextColor();
                        subTextCol = swatch.getBodyTextColor();
                        Library.colorCache.put(reference, new int[]{frameCol, textCol, subTextCol});
                        try {
                            collapsingToolbarLayout.setBackgroundColor(frameColor);
                            collapsingToolbarLayout.setContentScrimColor(frameColor);
                            collapsingToolbarLayout.setStatusBarScrimColor(frameColor);
                            if (!Prefs.colourAlbum(AlbumDetailDemo.this)) {
                                mFab.setBackgroundTintList(ColorStateList.valueOf(frameColor));
                            }
                        }catch (Exception e){

                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            try {
                                if(!Prefs.colourAlbum(AlbumDetailDemo.this)) {
                                    mFab.getDrawable().mutate().setTint(swatch.getTitleTextColor());
                                }
                                Window window = AlbumDetailDemo.this.getWindow();
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                if(Prefs.colourSB(AlbumDetailDemo.this)) {
                                    window.setStatusBarColor(frameColor);
                                }else{
                                    window.setStatusBarColor(Themes.getBlack());
                                }
                                if (Prefs.colourNB(AlbumDetailDemo.this)) {
                                    window.setNavigationBarColor(frameColor);
                                }
                            }catch (Exception e){

                            }

                        }


                    }else{
                        frameCol = defaultFrameColor;
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Prefs.colourNB(AlbumDetailDemo.this)){
                            getWindow().setNavigationBarColor(Themes.getPrimaryDark());
                        }
                    }
                }
            });
        }
    }

    private void colourViews(){
        collapsingToolbarLayout.setBackgroundColor(frameCol);
        collapsingToolbarLayout.setContentScrimColor(frameCol);
        collapsingToolbarLayout.setStatusBarScrimColor(frameCol);
        if(!Prefs.colourAlbum(this)){
            mFab.setBackgroundTintList(ColorStateList.valueOf(frameCol));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                if(!Prefs.colourAlbum(this)) {
                    mFab.getDrawable().mutate().setTint(textCol);
                }
                Window window = AlbumDetailDemo.this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                if(Prefs.colourSB(this)) {
                    window.setStatusBarColor(frameCol);
                }else{
                    window.setStatusBarColor(Themes.getBlack());
                }
                if (Prefs.colourNB(AlbumDetailDemo.this)) {
                    window.setNavigationBarColor(frameCol);
                }
            }catch (Exception e){

            }

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    supportFinishAfterTransition();
                }else {
                    finish();
                }
                return true;
            case R.id.action_like:
                if(!isFav) {
                    isFav=true;
                    jb.insertFavAlbum(id, 2);
                    item.setIcon(R.drawable.ic_favorite_white_24dp);
                    item.setTitle(getResources().getString(R.string.action_unlike));
                }
                else{
                    isFav=false;
                    jb.deleteFavAlbum(id);
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    item.setTitle(getResources().getString(R.string.action_like));

                }
                return true;
            case R.id.action_add:
                PlaylistDialog.AddToNormal.alert(
                        this.findViewById(android.R.id.content),
                        songsList,
                        context.getString(R.string.header_add_song_name_to_playlist,
                                reference));
                return true;
            case R.id.action_share:

                ArrayList<Uri> files = new ArrayList<>();
                if(songsList!=null && !songsList.isEmpty()) {
                    for(Song s : songsList){
                        files.add(Uri.parse("file://" + s.location));
                    }
                    try {
                        Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        share.setType("audio/*");

                        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                        context.startActivity(Intent.createChooser(share, "Share Album"));
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class AlbumAdapter extends RecyclerView.Adapter{
        private static final int HEADER_VIEW = 0;
        private static final int SONG_VIEW = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==HEADER_VIEW) {
                return new TitleViewHolder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.profile, parent, false));
            }

            return new AlbumSongViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.album_song_item, parent, false),
                    songsList);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

            if(holder instanceof TitleViewHolder){
                ((TitleViewHolder) holder)
                        .update(albumName,detail);
            }
            else if(holder instanceof AlbumSongViewHolder){
                int index = getTypeIndex(i);
                ((AlbumSongViewHolder) holder).update(songsList.get(index), index, context);
            }
        }

        private int getTypeIndex(int position){
            if (!songsList.isEmpty() && position <= songsList.size()){
                if (position == 0) return 0;
                else return position - 1;
            }
            return 0;
        }

        @Override
        public int getItemCount() {
            return songsList.size() +1;
        }

        @Override
        public int getItemViewType(int position) {
            if(position==0)
                return HEADER_VIEW;
            else if(position>0 && position<= songsList.size())
                return SONG_VIEW;
            return super.getItemViewType(position);
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder{
        private View item;
        private TextView album, songCount;
        private RelativeLayout card;


        public TitleViewHolder(View item){
            super(item);
            album = (TextView) item.findViewById(R.id.artist_name);
            songCount = (TextView) item.findViewById(R.id.artist_detail);
            card = (RelativeLayout) item.findViewById(R.id.card);

        }

        public void update(String string1, String string2){
            album.setText(string1);
            songCount.setText(string2);
            /*
            if(Prefs.colourAlbum(itemView.getContext())){
                generatePalette(art, true);
            }*/
            int[] cols = Library.colorCache.get(reference);

            if(cols!=null && cols.length==3) {
                frameCol = cols[0];
                textCol = cols[1];
                subTextCol = cols[2];
                if(Prefs.colourAlbum(itemView.getContext())){
                    colourHeader();
                }
            }
            else{
                if(Prefs.colourAlbum(itemView.getContext())){
                    generatePalette(art, true);
                }
            }
        }

        private void colourHeader(){
            card.setBackgroundColor(frameCol);
            album.setTextColor(textCol);
            songCount.setTextColor(subTextCol);
        }


        private void generatePalette(Bitmap img, final Boolean flag){
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

                        int titleColor = defaultTitleColor;
                        int detailColor = defaultDetailColor;

                        if (swatch != null && frameColor != Color.TRANSPARENT) {
                            titleColor = swatch.getTitleTextColor();
                            detailColor = swatch.getBodyTextColor();
                            Library.colorCache.put(reference, new int[]{frameColor, titleColor, detailColor});
                        } else {
                            frameColor = defaultFrameColor;
                            titleColor = defaultTitleColor;
                            detailColor = defaultDetailColor;
                        }


                        card.setBackgroundColor(frameColor);
                        album.setTextColor(titleColor);
                        songCount.setTextColor(detailColor);


                    }
                });
            }

        }

    }
}



