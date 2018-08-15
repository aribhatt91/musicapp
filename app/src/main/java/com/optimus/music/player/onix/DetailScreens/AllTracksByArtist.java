package com.optimus.music.player.onix.DetailScreens;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.optimus.music.player.onix.Common.ConnectionDetector;
import com.optimus.music.player.onix.Common.Instances.Artist;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.Common.lastFM.ImageList;
import com.optimus.music.player.onix.Common.lastFM.LArtist;
import com.optimus.music.player.onix.Common.lastFM.Query;
import com.optimus.music.player.onix.CrazyDataStore.ArtistNames;
import com.optimus.music.player.onix.CrazyDataStore.Artists;
import com.optimus.music.player.onix.DetailScreens.ArtistFragments.AlbumFragment;
import com.optimus.music.player.onix.DetailScreens.ArtistFragments.BioFragment;
import com.optimus.music.player.onix.DetailScreens.ArtistFragments.SongsFragment;
import com.optimus.music.player.onix.JukeBoxActivity.JukeBoxDBHelper;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.Themes;
import com.optimus.music.player.onix.Utility.PlaylistDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public class AllTracksByArtist extends NowPlayingActivity{

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String[] titles = {"Albums", "Songs", "Biography"};
    private ArtistPagerAdapter adapter;
    private LArtist reference;
    private Artist ref;



    Bundle b;
    private static long id;
    private static String artist;
    String[] arguments;
    int flag;
    private String url, taglist, bio;
    private ImageView backdrop;
    private ImageView back, like;
    JukeBoxDBHelper jb;
    InterstitialAd ad;
    private boolean isFav = false;
    boolean isConneted;

    boolean bolly = false;
    RelativeLayout main;
    SlidingUpPanelLayout slide;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Themes.setTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);



        Intent i = getIntent();
        try {
            b = i.getExtras();
            id = b.getLong("artist_id");
            artist = b.getString("name");
        }catch (Exception e){

        }
        arguments = new String[]{String.valueOf(id)};
        flag =-1;

        backdrop = (ImageView) findViewById(R.id.backdrop);
        slide = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        main = (RelativeLayout) findViewById(R.id.main);
        slide.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if(slide.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED){
                    //Toast.makeText(LibraryActivity.this, "Colour changed", Toast.LENGTH_SHORT).show();
                    colorWindowsCollapsed();
                }
            }
        });

        ImageView canvas = (ImageView) findViewById(R.id.canvas_large);
        try{
            canvas.setImageResource(R.drawable.default_album_art_500);

        }catch (Exception e){

        }
        catch (OutOfMemoryError e) {
            Glide.with(this).load(R.drawable.default_album_art_500).into(canvas);
        }



        for(int im=0; im< Artists.artists.length; im++){
            if(artist.toLowerCase().contains(Artists.artists[im])){
                flag = im;
                break;
            }
        }





        jb = new JukeBoxDBHelper(this);


        if(System.currentTimeMillis()%13==0 || System.currentTimeMillis()%17==0 ){
            Util.showAd(getResources().getString(R.string.artist_inter), this);
        }


        ref = new Artist(id, artist, 0, 0);
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        isConneted = cd.isConnectingToInternet();



        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //spinner.setAlpha(255);
                //spinner.start();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    if(isConneted) {

                            if (flag > -1) {
                                reference = Query.getArtist(AllTracksByArtist.this, Artists.artists[flag], id);
                            }

                            if (reference == null)
                                reference = Query.getArtist(AllTracksByArtist.this, artist, id);



                    }
                    else{
                        reference=null;
                    }
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    e.printStackTrace();
                    reference = null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(reference!=null){
                    url = reference.getImageURL(ImageList.SIZE_MEGA);
                    if(url!=null && AllTracksByArtist.this!=null) {

                        Glide.with(AllTracksByArtist.this).load(url)
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .centerCrop()
                                .animate(android.R.anim.fade_in)
                                .into(backdrop);

                    }

                }
            }
        }.execute();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(null);
            }
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tab);
        adapter = new ArtistPagerAdapter(getSupportFragmentManager(), titles);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabTextColors(0x66FFFFFF,0xFFFFFFFF);

        //setPaddingStyle();

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

/*
    private void setPaddingStyle(){
        if(PlayerController.getNowPlaying()==null){
            main.setPadding(0,0,0,0);
        }else{
            if(main.getPaddingBottom()==0){
                main.setPadding(0,0,0, Util.getActionBatHeight(this));
            }
        }
    }
*/


    @Override
    public void onUpdate() {
        super.onUpdate();
        //setPaddingStyle();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(slide.getPanelState()== SlidingUpPanelLayout.PanelState.COLLAPSED){
            colorWindowsCollapsed();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add,menu);
        //ArrayList<Long> ids = jb.getFavArtists(3);
        /*
        if(ids.contains(id)){
            isFav=true;
            menu.getItem(0).setIcon(R.drawable.ic_favorite_white_24dp);
            menu.getItem(0).setTitle(getResources().getString(R.string.action_unlike));

        }
        else{
            isFav=false;
            menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_24dp);
            menu.getItem(0).setTitle(getResources().getString(R.string.action_like));


        }*/
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            /*
            case R.id.action_like:
                if(!isFav) {
                    showAd();
                    isFav=true;
                    jb.insertFavArtist(id, 3);
                    item.setIcon(R.drawable.ic_favorite_white_24dp);
                    item.setTitle(getResources().getString(R.string.action_unlike));
                }
                else{
                    isFav=false;
                    jb.deleteFavArtist(id);
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    item.setTitle(getResources().getString(R.string.action_like));

                }
                return true;
                */
            case R.id.action_add:
                if(System.currentTimeMillis()%3==0) {
                    Util.showAd(getResources().getString(R.string.artist_inter), this);
                }
                PlaylistDialog.AddToNormal.alert(
                        this.findViewById(android.R.id.content),
                        Library.getAllSongsByArtist(this, artist),
                        this.getString(R.string.header_add_song_name_to_playlist,
                                ref));
                return true;
        }
        return false;
    }

    public class ArtistPagerAdapter extends FragmentPagerAdapter{
        private String titles[] ;


        public ArtistPagerAdapter(FragmentManager fm, String[] titles){
            super(fm);
            this.titles = titles;
        }
        @Override
        public int getCount() {
            return 3;
        }
        @Override
        public Fragment getItem(int position) {
           if(position==0) {
               return AlbumFragment.newInstance(id, artist);
           }
           else if(position==1) {
               return SongsFragment.newInstance(id, artist);
           }
           else if(position==2) {
               return BioFragment.newInstance(artist, id, flag);
           }
            return null;

        }
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }





    }


}
/*
 * final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(mViewPager);

    // Mario Velasco's code
    tabLayout.post(new Runnable()
    {
        @Override
        public void run()
        {
            int tabLayoutWidth = tabLayout.getWidth();

            DisplayMetrics metrics = new DisplayMetrics();
            ActivityMain.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int deviceWidth = metrics.widthPixels;

            if (tabLayoutWidth < deviceWidth)
            {
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            } else
            {
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            }
        }
    });

     */
