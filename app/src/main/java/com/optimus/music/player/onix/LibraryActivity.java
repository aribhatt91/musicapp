package com.optimus.music.player.onix;


import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.InterstitialAd;
import com.optimus.music.player.onix.AboutActivity.AboutActivity;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.Common.lastFM.Cache;
import com.optimus.music.player.onix.EqualizerActivity.EqualizerActivity;
import com.optimus.music.player.onix.FoldersActivity.FoldersActivity;
import com.optimus.music.player.onix.JukeBoxActivity.JukeBoxActivity;
import com.optimus.music.player.onix.MusicPlayer.MusicPlayer;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.SearchActivity.SearchActivity;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.SettingsActivity;
import com.optimus.music.player.onix.SettingsActivity.Themes;
import com.optimus.music.player.onix.TabScreensFragments.AlbumFragment;
import com.optimus.music.player.onix.TabScreensFragments.Artists;
import com.optimus.music.player.onix.TabScreensFragments.Genre;
import com.optimus.music.player.onix.TabScreensFragments.Playlist;
import com.optimus.music.player.onix.TabScreensFragments.Songs;
import com.optimus.music.player.onix.TagEditorActivity.AlbumTagEditorActivity;
import com.optimus.music.player.onix.Utility.PlaylistDialog;
import com.optimus.music.player.onix.WhatsHotActivity.WhatsHotActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


public class LibraryActivity extends NowPlayingActivity
        implements TimePickerDialog.OnTimeSetListener
        {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private ListView mDrawerList;
    ViewPager pager;
    private String[] titles; //= new String[]{ "  Genre  ", "  Playlists  ", "  Albums  ", "  Songs  ","  Artists  "};
    private Toolbar toolbar;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private FloatingActionButton fab;
    RelativeLayout main;

    //private int [] navColors;

    private ArrayList<NavItem> navDrawerItems;
    private NavAdapter adapter;
    int themeId;
    private PagerAdapter pagerAdapter;
    public static boolean showFab;

    SlidingUpPanelLayout slide;



    TabLayout slidingTabLayout;

    int primary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themes.setTheme(this);
        themeId = Themes.getTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        //onNewIntent(getIntent());

        SharedPreferences prefs = Prefs.getPrefs(this);
        primary = Integer.parseInt(prefs.getString(Prefs.PRIMARY_COLOR, "0"));



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons_shadow);
        slide = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        titles = getResources().getStringArray(R.array.tab_items);


        navDrawerItems = new ArrayList<NavItem>();

        navDrawerItems.add(new NavItem(navMenuTitles[0], R.drawable.small_icon));
        navDrawerItems.add(new NavItem(navMenuTitles[1], R.drawable.jukebox));
        navDrawerItems.add(new NavItem(navMenuTitles[2], R.drawable.yo));
        navDrawerItems.add(new NavItem(navMenuTitles[3], R.drawable.folder_shadow));
        navDrawerItems.add(new NavItem(navMenuTitles[4], R.drawable.settings_shadow));
        navDrawerItems.add(new NavItem(navMenuTitles[5], R.drawable.pencil_shadow));
        navDrawerItems.add(new NavItem(navMenuTitles[6], R.drawable.info_shadow));



        navMenuIcons.recycle();

        View header=getLayoutInflater().inflate(R.layout.navheader, null);

        main = (RelativeLayout) findViewById(R.id.main);



        mDrawerList.addHeaderView(header, this, false);
        ImageView gr = (ImageView) findViewById(R.id.gridd);
        ImageView headlogo = (ImageView) findViewById(R.id.head_logo);




        adapter = new NavAdapter(getApplicationContext(), navDrawerItems, primary);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setDivider(null);



        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        */



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_ab_drawer);
            toolbar.setTitleTextColor(0xFFFFFFFF);
        }


        if(gr!=null && headlogo != null) {
            try {
                gr.setImageResource(R.drawable.equalizer_grid_white);
                headlogo.setImageResource(R.drawable.head_logo);
            }catch (OutOfMemoryError e){
                Glide.with(this).load(R.drawable.equalizer_grid_white).centerCrop().into(gr);
                Glide.with(this).load(R.drawable.head_logo).into(headlogo);

            }
            if(primary==0 || primary==7 ){
                gr.setAlpha(0.1f);
            }else if(primary==10){
                gr.setAlpha(0.2f);
            }
            else if(primary==1 || primary==2||  primary==3|| primary==4 || primary==5
                    || primary==6 || primary==8 || primary==11){
                gr.setAlpha(0.4f);
            }
            else{
                gr.setAlpha(0.8f);
            }
        }

        try {
            if(toolbar!=null) {

                if (primary == 0) {
                    toolbar.setPopupTheme(R.style.AppThemeBase);
                } else if (primary == 6) {
                    toolbar.setPopupTheme(R.style.AppThemeBase_Passion);
                } else if (primary == 7) {
                    toolbar.setPopupTheme(R.style.AppThemeBase_Midnight);
                } else if (primary == 10){
                    toolbar.setPopupTheme(R.style.AppThemeBase_Party);
                }
                else {
                    toolbar.setPopupTheme(R.style.AppThemeBaseLight);
                }
            }
        }catch (Exception e){

        }





        fab = (FloatingActionButton) findViewById(R.id.fabs);
        //fab.setBackgroundTintList(ColorStateList.valueOf(0xff9f0e46));
        pager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), titles);

        slidingTabLayout = (TabLayout)findViewById(R.id.tab);
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(pagerAdapter);
        pagerAdapter.setFAB(fab);
        int page = Integer.parseInt(prefs.getString(Prefs.DEFAULT_PAGE, "2"));
        if(page>=0 && page<5) {
            pager.setCurrentItem(page);
        }else{
            if(page==5){
                int p = Prefs.getLastPage(this);
                if(p>=0 && p<5)
                    pager.setCurrentItem(p);
            }
        }

        if(slide.getPanelState()== SlidingUpPanelLayout.PanelState.COLLAPSED)
            colorWindowsCollapsed();

        slide.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if(previousState == SlidingUpPanelLayout.PanelState.EXPANDED){
                    //Toast.makeText(LibraryActivity.this, "Colour changed", Toast.LENGTH_SHORT).show();
                    colorWindowsCollapsed();
                }
            }
        });

        //displayMessage();




        //Library.scanAll(this);

        fab.setOnClickListener(this);


        slidingTabLayout.setupWithViewPager(pager);
        slidingTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(drawerToggle);


        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {

                    case 1:
                        if(PlayerController.getNowPlaying()!= null ) {
                            //Navigate.to(LibraryActivity.this, NowPlayingActivity.class);
                            if(slide.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
                                slide.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                            }else {
                                try {
                                    ((SlidingUpPanelLayout) findViewById(R.id.draglayout)).setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                    slide.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                }catch(Exception e){

                                }
                            }
                        }

                        else
                            Toast.makeText(LibraryActivity.this, "Nothing is Playing", Toast.LENGTH_SHORT).show();
                        try {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        }catch (Exception e){

                            e.printStackTrace();

                        }


                        //mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), JukeBoxActivity.class));
                        try {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        }catch (Exception e){
                            e.printStackTrace();

                        }

                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(), WhatsHotActivity.class));
                        try {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        }catch (Exception e){
                            e.printStackTrace();

                        }
                        //mDrawerLayout.closeDrawer(Gravity.LEFT);

                        break;
                    case 4:
                        try {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        }catch (Exception e){
                            e.printStackTrace();

                        }
                        startActivity(new Intent(getApplicationContext(), FoldersActivity.class));

                        //mDrawerLayout.closeDrawer(Gravity.LEFT);

                        break;
                    case 5:
                        try {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        }catch (Exception e){
                            e.printStackTrace();

                        }
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));

                        //mDrawerLayout.closeDrawer(Gravity.LEFT);

                        break;
                    case 6:
                        final String pkg = getPackageName();
                        try {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        }catch (Exception e){
                            e.printStackTrace();

                        }

                        try{
                            View view1 = LibraryActivity.this.findViewById(android.R.id.content).getRootView();
                            PlaylistDialog.FeedbackDialog.showDialog(view1);
                            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+pkg)));
                        }catch (Exception e){
                            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+pkg)));
                        }


                        break;
                    case 7:
                        try {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Navigate.to(LibraryActivity.this, AboutActivity.class);

                        break;
                }

            }
        }
        );

    }

    private void colorWindowsCollapsed(){
        //Toast.makeText(this, "Collapse colour", Toast.LENGTH_SHORT).show();
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


    private void displayMessage(){
        if(Prefs.disPlayMessage(this)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Hey Users!")
                    .setMessage(getResources().getString(R.string.new_mgs))
                    .setNegativeButton("Ok", null)
                    .show();
            Themes.themeAlertDialog(dialog);
        }

        Prefs.setMsgVar(this, false);
    }


    @Override
    public void onNewIntent(Intent intent) {
        // Handle incoming requests to play media from other applications
        if (intent==null || intent.getData() == null) return;

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );

        // If this intent is a music intent, process it
        if (intent.getType().contains("audio") || intent.getType().contains("application/ogg")
                || intent.getType().contains("application/x-ogg") ||
                intent.getType().contains("application/itunes")){

            // The queue to be passed to the player service
            ArrayList<Song> queue = new ArrayList<>();
            int position = 0;

            // Have the LibraryScanner class get a song list for this file
            try{
                position = Library.getSongListFromFile(this,
                        new File(intent.getData().getPath()), intent.getType(), queue);
            }
            catch (Exception e){
                e.printStackTrace();
                queue = new ArrayList<>();
            }

            if (queue.isEmpty()){
                // No music was found
                Toast toast = Toast.makeText(getApplicationContext(), "No Music Was Found", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
            else {
                if (PlayerController.isServiceStarted()) {
                    Toast.makeText(getApplicationContext(), "Player Service Started", Toast.LENGTH_SHORT).show();
                    PlayerController.setQueue(queue, position);
                    PlayerController.begin();
                } else {

                    // If the service hasn't been bound yet, then we need to wait for the service to
                    // start before we can pass data to it. This code will bind a short-lived
                    // BroadcastReceiver to wait for the initial UPDATE broadcast to be sent before
                    // sending data. Once it has fulfilled its purpose it will unbind itself to
                    // avoid a lot of problems later on.

                    //Toast.makeText(getApplicationContext(), "No Player Service", Toast.LENGTH_SHORT).show();


                    final ArrayList<Song> pendingQueue = queue;
                    final int pendingPosition = position;

                    final BroadcastReceiver binderWaiter = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            PlayerController.setQueue(pendingQueue, pendingPosition);
                            PlayerController.begin();
                            LibraryActivity.this.unregisterReceiver(this);
                        }
                    };
                    registerReceiver(binderWaiter, new IntentFilter(MusicPlayer.UPDATE_BROADCAST));
                }
            }
        }
        //setIntent(null);
    }







    private void setPaddingStyle(){
        if(PlayerController.getNowPlaying()==null){
            main.setPadding(0,0,0,0);
        }else{
            if(main.getPaddingBottom()==0){
                main.setPadding(0,0,0,Util.getActionBatHeight(this));
            }
        }
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String secondString = second < 10 ? "0"+second : ""+second;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int res = 0;

        if(hour<=hourOfDay){
            if(hour==hourOfDay){
                if(min>minute){
                    res = 24*(60-minute);
                }else{
                    res = minute-min;
                }
            }else if(hour<hourOfDay){
                if(min>minute){
                    res = (hourOfDay-hour-1)*60 + (60-min+minute);
                }else{
                    res = (hourOfDay-hour)*60 + (minute-min);
                }
            }

        }
        else if(hour>hourOfDay){
            res = 24*60 - (hour*60+min) + (hourOfDay*60 + minute);
        }
        //String time = "You picked the following time: "+hourString+"h"+minuteString+"m"+secondString+"s";
        long duration = res*60*1000 + System.currentTimeMillis();
        String mins = "Sleep timer set for: "+res+" mins";

        PlayerController.setSleepTimerEndTime(duration);

        Toast.makeText(LibraryActivity.this, mins, Toast.LENGTH_LONG).show();
    }




    @Override
    public void onBackPressed() {
        //if(getFragmentManager().getBackStackEntryCount()!=0){
            //getFragmentManager().popBackStack();
        //}
        //else {
            super.onBackPressed();
        //}

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.fabs:

                if(!Library.getSongs().isEmpty()) {
                    Random r = new Random();
                    int index = r.nextInt(Library.getSongs().size());
                    PlayerController.setQueue(Library.getSongs(), index);
                    PlayerController.begin();
                    if (!PlayerController.isShuffle())
                        PlayerController.toggleShuffle();
                }
                //loadInterstitial();
                Util.showAd(getResources().getString(R.string.home_interstitial), this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //onNewIntent(getIntent());

        try {
            if(Library.getSongs().isEmpty()){
                Library.scanAll(this);
            }
            showFab = Prefs.showFab(this);
            if(!showFab)
                fab.hide();
            else
                fab.show();

            if(slide.getPanelState()== SlidingUpPanelLayout.PanelState.COLLAPSED){
                colorWindowsCollapsed();
            }

        /*
            SharedPreferences prefs = Prefs.getPrefs(this);
            int page = Integer.parseInt(prefs.getString(Prefs.DEFAULT_PAGE, "2"));
            if (page < 5) {
                pager.setCurrentItem(page);
            } else {
                if (page == 5) {
                    int p = Prefs.getLastPage(this);
                    if (p >= 0 && p < 5)
                        pager.setCurrentItem(p);
                }
            }*/
            if (themeId != Themes.getTheme(this)) {
                // If the theme was changed since this Activity was last started, recreate it
                recreate();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onUpdate() {
        super.onUpdate();
        setPaddingStyle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                try {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }catch (Exception e){
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;

            case R.id.search_btn:
                //Toast.makeText(this, "Search Button is Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                return true;

            case R.id.rescan:
                Cache.deleteCache(this);
                Library.genMap.clear();
                Library.colorCache.clear();
                Library.scanAll(this);
                return true;

            case R.id.eq:
                if(Prefs.useSysEq(LibraryActivity.this)){
                    //Util.getSystemEqIntent(getApplicationContext());
                    try {
                        final Intent eqintent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                        eqintent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, PlayerController.getAudioSessionId());
                        startActivityForResult(eqintent, 0);
                    }catch (Exception e){
                        Crashlytics.log(e.getMessage());
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle("Oops!!")
                                .setMessage("It seems System Equalizer is not responding! Open Onix equalizer instead?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(getApplicationContext(), EqualizerActivity.class));
                                    }
                                })
                                .setNegativeButton(R.string.action_cancel, null)
                                .show();
                        Themes.themeAlertDialog(dialog);
                        //Toast.makeText(this, "System doesn't have an equalizer or its not responding!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {

                    startActivity(new Intent(getApplicationContext(), EqualizerActivity.class));
                }
                return true;
            case R.id.timer:
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );

                if(primary==0 || primary == 6){
                    tpd.setThemeDark(true);
                }

                tpd.vibrate(false);
                tpd.dismissOnPause(true);
                tpd.enableSeconds(false);
                tpd.enableMinutes(true);
                tpd.setTitle("Sleep Timer");
                tpd.setTimeInterval(1, 1, 10);

                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d("TimePicker", "Dialog was cancelled");
                    }
                });
                tpd.show(getFragmentManager(), "Timepickerdialog");
                return true;

            case R.id.queue:

                PlayerController.editQueue(new ArrayList<Song>(), 0);
                PlayerController.begin();
                return true;

            case R.id.rate:
                try{
                    View view1 = LibraryActivity.this.findViewById(android.R.id.content).getRootView();
                    PlaylistDialog.FeedbackDialog.showDialog(view1);
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+pkg)));
                }catch (Exception e){
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+pkg)));
                }
                return true;

        }

        return super.onOptionsItemSelected(item);
    }






    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_icons,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }




    public class PagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener{

        final int PAGE_COUNT =5;
        private String titles[] ;
        private FloatingActionButton fab;

        public PagerAdapter(FragmentManager fm, String[] titles2) {
            super(fm);
            titles=titles2;
        }

        public void setFAB(FloatingActionButton fab){
            this.fab = fab;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 4:
                    return Artists.newInstance(position);
                case 2:
                    return AlbumFragment.newInstance(position);
                case 3:
                    return Songs.newInstance(position);
                case 1:
                    return Playlist.newInstance(position);
                case 0:
                    return Genre.newInstance(position);


            }
            return null;
        }

        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public void onPageSelected(int position) {
            Prefs.setCurrPage(LibraryActivity.this, position);

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

    }



}