package com.optimus.music.player.onix.WhatsHotActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.*;
import com.optimus.music.player.onix.Common.ConnectionDetector;
import com.optimus.music.player.onix.Common.Instances.YTVideo;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.Themes;


import java.util.ArrayList;
import java.util.Locale;

public class WhatsHotActivity extends AppCompatActivity {
   // RelativeLayout ad;
    String[] titles = { "World Top50",  "R&B/Hip-Hop", "EDM", "Bolly's Hot50", "New Sensations"};

    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Themes.setTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_hot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);


            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("What's Hot!");
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Prefs.colourNB(this)){
            getWindow().setNavigationBarColor(Themes.getPrimary());
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !Prefs.colourSB(this)) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Themes.getBlack());
        }

        adView = (AdView) findViewById(R.id.adView);
        AdRequest adrequest = new  AdRequest.Builder()
                .addTestDevice(Library.TEST_DEVICE_ID)
                .build();
        adView.loadAd(adrequest);
        //AdSettings.addTestDevice(Library.TEST_DEVICE_ID);





        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tab = (TabLayout) findViewById(R.id.tab);
        WHPagerAdapter adapter = new WHPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tab.setupWithViewPager(viewPager);
        tab.setTabMode(TabLayout.MODE_SCROLLABLE);

        if(System.currentTimeMillis()%13==0 || System.currentTimeMillis()%5==0){
            Util.showAd(getResources().getString(R.string.home_interstitial), this);
        }






        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isConneted = cd.isConnectingToInternet();


        if(isConneted){

            Answers.getInstance().logCustom(
                    new CustomEvent("Loaded What's HOT"));
        }
        else{

            AlertDialog dialog = new AlertDialog.Builder(WhatsHotActivity.this)
                    .setTitle("Ooopss!")
                    .setMessage("Failed to connect to the Internet!")
                    .setNegativeButton("OK", null)
                    .show();
            Themes.themeAlertDialog(dialog);

        }

    }




    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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



    public class WHPagerAdapter extends FragmentPagerAdapter {

        public WHPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Fragment getItem(int position) {
            return NewFragment.newInstance(position, titles[position]);
        }

    }



}
