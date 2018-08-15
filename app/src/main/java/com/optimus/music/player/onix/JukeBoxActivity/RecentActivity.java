package com.optimus.music.player.onix.JukeBoxActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;

import com.google.android.gms.ads.InterstitialAd;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.Themes;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class RecentActivity extends NowPlayingActivity {
    public static ArrayList<Song> recentPlayed = new ArrayList<>();
    public static ArrayList<Song> mostPlayed = new ArrayList<>();
    public static ArrayList<Song> recentAdded = new ArrayList<>();

    RelativeLayout main;
    SlidingUpPanelLayout slide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Themes.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        SharedPreferences prefs = Prefs.getPrefs(this);
        final int primary = Integer.parseInt(prefs.getString(Prefs.PRIMARY_COLOR, "0"));
        //Locale loc = new Locale("", getUserCountry(this));
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);


            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Recent Activity");
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Prefs.colourNB(this)){
            getWindow().setNavigationBarColor(Themes.getPrimary());
        }


        if(System.currentTimeMillis()%13==0){
            Util.showAd(getResources().getString(R.string.artist_inter), this);
        }

        slide = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        main = (RelativeLayout) findViewById(R.id.main);
        colorWindowsCollapsed();
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



        recentAdded = Library.recentlyAddedSongs(this);
        recentPlayed = Library.recentlyPlayedSongs(this);
        mostPlayed = Library.mostPlayedSongs(this);


        Library.recentSongsBySection(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tab = (TabLayout) findViewById(R.id.tab);
        RecentActivityAdapter adapter = new RecentActivityAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tab.setupWithViewPager(viewPager);
        tab.setTabMode(TabLayout.MODE_SCROLLABLE);

        if(primary==0){
            tab.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.metalYellow));
        }
        else if(primary==6){
            tab.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.passionBackground));
        }else if(primary==7){
            tab.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.midnightAccent));
        }
        else{
            tab.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        setPaddingStyle();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    public class RecentActivityAdapter extends FragmentPagerAdapter {
            String[] titles = {"Recently Played", "Last Added", "Most Played"};

        RecentActivityAdapter(FragmentManager fm){
            super(fm);
        }
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            if(position<3){
                return RecentFragment.newInstance(position, titles[position]);
            }
            return null;
        }


    }

}
