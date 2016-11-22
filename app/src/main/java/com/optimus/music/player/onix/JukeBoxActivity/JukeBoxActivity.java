package com.optimus.music.player.onix.JukeBoxActivity;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.audiofx.AudioEffect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.crashlytics.android.Crashlytics;

import com.optimus.music.player.onix.Common.Instances.Album;

import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Instances.YTVideo;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.EqualizerActivity.EqualizerActivity;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.AlbumArtistViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.FavSongViewHolder;

import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.GridSpacingItemDecoration;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.SuggestedViewHolder;
import com.optimus.music.player.onix.SearchActivity.SearchActivity;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.Themes;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class JukeBoxActivity extends NowPlayingActivity implements TimePickerDialog.OnTimeSetListener{

    public static ArrayList<Song> songEntries = new ArrayList<>();
    public static ArrayList<Album> albumEntries = new ArrayList<>();
    public static ArrayList<Album> albumRecentlyAdded = new ArrayList<>();
    public static ArrayList<Album> albumRecentlyPlayed = new ArrayList<>();

    public static ArrayList<Song> suggested = new ArrayList<>();

    private Context context;
    //private JBAdapter adapter;
    private RecyclerView list;
    int primary;
    RelativeLayout main;
    SlidingUpPanelLayout slide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Themes.setTheme(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_juke_box);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("JukeBox");
            }
        }
        SharedPreferences prefs = Prefs.getPrefs(this);
        primary = Integer.parseInt(prefs.getString(Prefs.PRIMARY_COLOR, "0"));

        //remove this
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Prefs.colourNB(this)){
            getWindow().setNavigationBarColor(Themes.getPrimary());
        }

        main = (RelativeLayout) findViewById(R.id.main);
        slide = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

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




        long time = System.currentTimeMillis()%11;

        if(time==0 || time==3){
            Util.showAd(getResources().getString(R.string.home_interstitial), this);
        }
        slide.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if(slide.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED){
                    colorWindowsCollapsed();
                }
            }
        });

        final int numColumns = getResources().getInteger(R.integer.jb_num_cols);

        setPaddingStyle();



        context = this;
        list = (RecyclerView) findViewById(R.id.list);

        final JBAdapter adapter = new JBAdapter();

        GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position) == JBAdapter.RECENTVIEW
                        //||adapter.getItemViewType(position) == JBAdapter.SUGGVIEW
                        || adapter.getItemViewType(position) == JBAdapter.FAVVIEW) {
                    return 1;
                }
                return numColumns;
            }
        };


        GridLayoutManager gridLayoutManager = new GridLayoutManager(JukeBoxActivity.this, numColumns);
        gridLayoutManager.setSpanSizeLookup(spanSizeLookup);
        list.setLayoutManager(gridLayoutManager);
        list.addItemDecoration(new GridSpacingItemDecoration(numColumns, (int) getResources().getDimension(R.dimen.jb_grid_space), true));

        list.setAdapter(adapter);



    }



    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        setPaddingStyle();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(getFragmentManager().getBackStackEntryCount()!=0 && System.currentTimeMillis()%5==0){
                    Util.showAd(getResources().getString(R.string.artist_inter), this);
                    getFragmentManager().popBackStack();
                }
                else {
                    finish();
                }
                return true;
            case R.id.search_btn:
                //Toast.makeText(this, "Search Button is Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                return true;

            case R.id.shuffle:
                new AsyncTask<Void, Void, Void>(){
                    ArrayList<Song> res = new ArrayList<>();
                    @Override
                    protected Void doInBackground(Void... voids) {
                        res = Library.getSuggestedSongs(JukeBoxActivity.this);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if(res!=null && !res.isEmpty()) {
                            Random r = new Random();
                            int index = r.nextInt(res.size());
                            PlayerController.setQueue(res, index);
                            PlayerController.begin();
                            if (!PlayerController.isShuffle())
                                PlayerController.toggleShuffle();
                        }
                    }
                }.execute();
                return true;

            case R.id.eq:
                if(Prefs.useSysEq(JukeBoxActivity.this)){
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

        }
        return false;
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

        Toast.makeText(JukeBoxActivity.this, mins, Toast.LENGTH_LONG).show();
    }




    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount()!=0){
            if(System.currentTimeMillis()%6==0) {
                Util.showAd(getResources().getString(R.string.home_interstitial), this);
            }
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(slide.getPanelState()== SlidingUpPanelLayout.PanelState.COLLAPSED){
            colorWindowsCollapsed();
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
    public class JBAdapter extends RecyclerView.Adapter implements Library.LibraryRefreshListener{

        public static final int FAVVIEW = 2;
        public static final int RECENTVIEW = 1;
        public static final int HEADERVIEW = 0;
        public static final int SUGGVIEW = 3;

        ArrayList<Album> recent = new ArrayList<>();
        ArrayList<Song> favs= new ArrayList<>();
        ArrayList<Song> sugg = new ArrayList<>();
        String[] headers = new String[]{"Recent Activity", "Favourites"};
        String[] subheaders = new String[]{"Recently played or added", "Favourites"};



    ArrayList<YTVideo> ytVideos = new ArrayList<>();

        public JBAdapter(){
            favs.clear();
            recent.clear();
            if(Library.hasRWPermission(JukeBoxActivity.this)) {
                (new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        //Library.scanAll(JukeBoxActivity.this);
                        albumRecentlyPlayed = Library.getRecentSongs(JukeBoxActivity.this);
                        albumRecentlyAdded = Library.recentlyAddedAlbums(JukeBoxActivity.this);
                        songEntries = Library.getFavSongs(JukeBoxActivity.this);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (songEntries.size() > 0 || albumRecentlyPlayed.size() > 0 || !albumRecentlyAdded.isEmpty()) {

                            for (Song s : songEntries) {
                                favs.add(s);
                                if (favs.size() >= 6)
                                    break;
                            }

                            for (Album a : albumRecentlyPlayed) {
                                recent.add(a);
                                if (recent.size() >= 3)
                                    break;
                            }

                            for (Album a : albumRecentlyAdded) {
                                if (!recent.contains(a))
                                    recent.add(a);
                                if (recent.size() >= 6)
                                    break;
                            }

                            notifyDataSetChanged();

                        }
                    }
                }).execute();
            }else if (!Library.previouslyRequestedRWPermission(JukeBoxActivity.this)) {
                Library.requestRWPermission(JukeBoxActivity.this);
            }


        }

        @Override
        public int getItemCount() {
            return (recent.isEmpty()? 0 : 1 + recent.size())
                    //+(sugg.isEmpty()? 0 : 1 + sugg.size())
                    +(favs.isEmpty()? 0 : 1 + favs.size());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){
                case HEADERVIEW :
                    return new JBHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.jbheader, parent, false));
                case RECENTVIEW:
                    if(Prefs.colourAlbum(JukeBoxActivity.this)){
                        return new AlbumArtistViewHolder(LayoutInflater.
                                from(parent.getContext()).inflate(R.layout.cardview_item_rect, parent, false));
                    }
                    else{
                        return new AlbumArtistViewHolder(LayoutInflater.
                                from(parent.getContext()).inflate(R.layout.cardview_item, parent, false));

                    }
                    /*
                case SUGGVIEW:
                    if(Prefs.colourAlbum(JukeBoxActivity.this)){
                        return new SuggestedViewHolder(LayoutInflater
                                .from(parent.getContext()).inflate(R.layout.cardview_item_rect, parent, false), suggested);
                    }
                    else {
                        return new SuggestedViewHolder(LayoutInflater
                                .from(parent.getContext()).inflate(R.layout.cardview_item, parent, false), suggested);
                    }*/
                case FAVVIEW:
                    if(Prefs.colourAlbum(JukeBoxActivity.this)){
                        return new FavSongViewHolder(LayoutInflater
                                .from(parent.getContext()).inflate(R.layout.cardview_item_rect, parent, false), favs, this);
                    }
                    else {
                        return new FavSongViewHolder(LayoutInflater
                                .from(parent.getContext()).inflate(R.layout.cardview_item, parent, false), favs, this);
                    }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)){
                case HEADERVIEW:
                    ((JBHeaderViewHolder) holder).update(headers[getHeaderIndex(position)], null);
                    break;
                case RECENTVIEW:
                    ((AlbumArtistViewHolder) holder).update(recent.get(position-1), JukeBoxActivity.this);
                    break;
                case FAVVIEW:
                    ((FavSongViewHolder) holder).update(favs.get(getFavPosition(position)), position, JukeBoxActivity.this);
                    break;
                //case SUGGVIEW:
                    //((SuggestedViewHolder) holder).update(sugg.get(getSugPosition(position)), position, JukeBoxActivity.this);
                    //break;
            }

        }

        @Override
        public void onLibraryRefreshed() {
            notifyDataSetChanged();
        }

        public int getFavHeaderAbsPosition(){
            if(recent.isEmpty() /*&& sugg.isEmpty()*/)
                return 0;
            else{
                //if(sugg.isEmpty())
                    return 1+recent.size() ;
                //else
                    //return 1+recent.size() + 1+sugg.size();
            }
        }

        public int getFavAbsPosition(int pos){
            if(!recent.isEmpty()){
                //if(!sugg.isEmpty())
                    //return pos + 3 + recent.size() + sugg.size();
                return pos + 2 + recent.size();
            }
            else{
                //if(!sugg.isEmpty())
                    //return pos + 2 + sugg.size();
                return pos-1;
            }
        }

        public int getSugPosition(int position){
            int index = 0;
            if(recent.isEmpty()) {
                return position - 1;
            }
            else{
                if(!sugg.isEmpty()){
                    index = position - (recent.size()+2);
                }
            }
            return index;

        }


        public int getFavPosition(int position){
            int index = 0;
            if(recent.isEmpty()) {
                //if(!sugg.isEmpty()){
                    //return position - (sugg.size()+2);
                //}
                return position - 1;
            }
            else{
                if(!favs.isEmpty()){
                    index = position - (recent.size()+2);
                }
            }
            return index;
        }
        public int getSuggHeadIndex(){
            if(!recent.isEmpty())
                return 1+recent.size();
            return 0;
        }

        public int getHeaderIndex(int position){
            if(position==0 && !recent.isEmpty())
                return 0;
            //else if(position==0 && recent.isEmpty() && !sugg.isEmpty())
                //return 2;
            //else if(!recent.isEmpty() && !sugg.isEmpty() && position==recent.size()+1)
                //return 2;
            //else if(position==0 && recent.isEmpty() && sugg.isEmpty() && !favs.isEmpty())
                //return 1;

            else if(position==0 && recent.isEmpty() && !favs.isEmpty())
                return 1;
            else
                return 1;
        }

        public void removeFavItem(Song s, int pos){
            if(favs.contains(s)){
                favs.remove(s);
                if(songEntries.contains(s))
                    songEntries.remove(s);
                notifyItemRemoved(getFavAbsPosition(pos));
            }
            if(favs.isEmpty() && songEntries.isEmpty()){
                notifyItemRemoved(getFavHeaderAbsPosition());
            }
            else if(songEntries.size() > favs.size()){
                int i = favs.size();
                if(i<=5) {
                    favs.add(songEntries.get(i));
                    notifyItemInserted(i);
                }
            }
        }

        public void addItemsToFav(){
            if(favs.size()<6 && songEntries.size()>favs.size()){


            }
        }


        public void addFavItem(Song song, int pos){
            if(favs.size()<=6){
                favs.add(song);
                notifyItemInserted(getFavAbsPosition(pos));
            }
        }

        @Override
        public int getItemViewType(int position) {

            if(position==0)
                return HEADERVIEW;
            else if(!recent.isEmpty() && position==recent.size()+1)
                return HEADERVIEW;
            //else if(!recent.isEmpty() && !sugg.isEmpty() && (position == recent.size() + sugg.size() + 2))
                //return HEADERVIEW;
            else if(!recent.isEmpty() && position>0 && position<=recent.size())
                return RECENTVIEW;
            //else if(recent.isEmpty() && !sugg.isEmpty() && position>0 && position<sugg.size())
                //return SUGGVIEW;
            //else if(!recent.isEmpty() && !sugg.isEmpty() && position > getSuggHeadIndex() && position < getFavHeaderAbsPosition())
                //return SUGGVIEW;
            else
                return FAVVIEW;

        }
    }

    public class JBHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public static final int FAVHEADER = 1;
        public static final int RECENTHEADER = 0;
        private TextView headerText, subheaderText, moreView;
        String[] headers = new String[]{"Recent Activity", "Favourites"};
        private boolean flag = false;


        public JBHeaderViewHolder(View itemView) {
            super(itemView);
            headerText = (TextView) itemView.findViewById(R.id.subheader);
            moreView = (TextView) itemView.findViewById(R.id.more);
            moreView.setOnClickListener(this);

        }

        public void update(String sectionName, String desc){
            if(sectionName.equals(headers[0])) {
                flag = true;
            }
            else {
                flag = false;
            }

            headerText.setText(sectionName);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.more:
                    if(flag) {
                        //recent activity
                        Navigate.to(JukeBoxActivity.this, RecentActivity.class);
                    }
                    else{
                        Fragment frag = new FavoutiteFragment();
                        getFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, frag)
                                .addToBackStack(null)
                                .commit();

                    }

                    break;
                default:
                    break;
            }

        }
    }

}
