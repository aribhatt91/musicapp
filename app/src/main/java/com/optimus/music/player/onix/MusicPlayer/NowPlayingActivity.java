package com.optimus.music.player.onix.MusicPlayer;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;

import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.optimus.music.player.onix.BuildConfig;
import com.optimus.music.player.onix.EqualizerActivity.EqualizerActivity;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.Common.Instances.*;

import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.Themes;
import com.optimus.music.player.onix.TagEditorActivity.LyricsActivity;
import com.optimus.music.player.onix.Utility.PlaylistDialog;
import com.optimus.music.player.onix.Utility.ViewUtils.MaterialPlayPauseButton;
import com.optimus.music.player.onix.Utility.ViewUtils.PlayPauseView;
import com.optimus.music.player.onix.Utility.ViewUtils.SimpleGestureFilter;
import com.optimus.music.player.onix.Utility.ViewUtils.SimpleGestureFilter.SimpleGestureListener;
import com.optimus.music.player.onix.Utility.ViewUtils.TimeView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.truizlop.fabreveallayout.FABRevealLayout;
import com.truizlop.fabreveallayout.OnRevealChangeListener;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;


public class NowPlayingActivity extends AppCompatActivity implements
        PlayerController.UpdateListener, View.OnClickListener,
        PlayerController.ErrorListener,
        SeekBar.OnSeekBarChangeListener{

    private MediaObserver observer = new MediaObserver(this);
    private Thread observerThread = new Thread(observer);

    private boolean userTouchingProgressBar = false; // This probably shouldn't be here...
    //private static boolean lyricsOn = false;


    private Song currentReference; // Used to reduce unnecessary view updates when an UPDATE broadcast is received
    private SeekBar seekbar;
    private ImageView artist, prev,next, play, panelButton, addButton, canvas, back, eq, rep, shuff, miniart;
    private MaterialPlayPauseButton materialPlayPauseButton;
    private SimpleGestureFilter detector;
    private int black, accent, white;
    private Toolbar toolbar;
    private FrameLayout container;//, lyricsFrame, overlay;
    private ArrayList<Song> data;
    private TimeView timeDuration, timePosition;
    private boolean prevPlaying = false;
    private FABRevealLayout fabRevealLayout;

    private TextView songTitle, artistName, albumTitle, header, lyricsText, minisong, miniartist, upnext;
    private RelativeLayout playview, pauseview;// gallery;
    QueueFragment queueFragment;
    private SlidingUpPanelLayout slidingLayout, draglayout;
    FloatingActionButton fab;

    // Miniplayer
    private static final boolean DEBUG = BuildConfig.DEBUG;
    public static boolean isFooterHidden = false;
    ProgressBar prog;
    //MaterialPlayPauseButton playPauseView;
    RelativeLayout footerView, bottom;
    LinearLayout textHolder;
    View footer, fake_toolbar, overlay, gallery;

    PlayPauseView playPauseView2;

    public static int frame;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);



    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        if(PlayerController.isPlaying())
            prevPlaying = true;

        bindNowPlayingScreen();
        bindMiniPlayer();
    }

    private void bindNowPlayingScreen(){

        prev = (ImageView) findViewById(R.id.prev);
        play = (ImageView) findViewById(R.id.play);
        next = (ImageView) findViewById(R.id.next);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        back = (ImageView) findViewById(R.id.back);
        eq = (ImageView) findViewById(R.id.equalizer);
        shuff = (ImageView) findViewById(R.id.shuff);
        rep = (ImageView) findViewById(R.id.rep);

        canvas = (ImageView) findViewById(R.id.canvas);
        fake_toolbar = findViewById(R.id.tool_controls);
        overlay = findViewById(R.id.overlay);


        playview = (RelativeLayout) findViewById(R.id.play_view);
        pauseview = (RelativeLayout) findViewById(R.id.pause_view);
        container = (FrameLayout) findViewById(R.id.player_container);
        lyricsText = (TextView) findViewById(R.id.lyrics_text);

        songTitle = (TextView) findViewById(R.id.songName);
        artistName = (TextView) findViewById(R.id.artistName);
        albumTitle = (TextView) findViewById(R.id.albumTitle);
        header = (TextView) findViewById(R.id.header);
        footer = findViewById(R.id.footer);
        bottom = (RelativeLayout) findViewById(R.id.bottom);
        addButton = (ImageView) findViewById(R.id.addButton);
        timeDuration = (TimeView) findViewById(R.id.max_time);
        timePosition = (TimeView) findViewById(R.id.curr_time);

        upnext = (TextView) findViewById(R.id.upnext);

        seekbar = (SeekBar) findViewById(R.id.seekbar);
        gallery = findViewById(R.id.gallery);
        artist = (ImageView) findViewById(R.id.background);

        //View bottomsheet = findViewById(R.id.bottomsheet);
        //bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);
        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        draglayout = (SlidingUpPanelLayout) findViewById(R.id.draglayout);
        panelButton = (ImageView) findViewById(R.id.panelButton);

        queueFragment = (QueueFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment);



        accent = Themes.getAccent();
        black = Themes.getBlack();
        white = Themes.getWhite(this);

        fabRevealLayout = (FABRevealLayout) findViewById(R.id.fab_reveal_layout);
        prev.setColorFilter(black, PorterDuff.Mode.MULTIPLY);
        next.setColorFilter(black, PorterDuff.Mode.MULTIPLY);
        play.setColorFilter(black, PorterDuff.Mode.MULTIPLY);

        addButton.setEnabled(false);
        addButton.setAlpha(0.0f);



        draglayout.setTouchEnabled(false);

        draglayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    songTitle.setAlpha(0.0f);
                    header.setAlpha(1.0f);
                    header.setText("Now Playing");
                    addButton.setAlpha(1.0f);
                    addButton.setEnabled(true);
                    scale(header, 100);
                    slidingLayout.setTouchEnabled(false);
                    upnext.setAlpha(0.0f);

                } else if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
                    songTitle.setAlpha(0.0f);
                    header.setAlpha(0.0f);
                    addButton.setAlpha(0.0f);
                    addButton.setEnabled(false);
                    slidingLayout.setTouchEnabled(false);
                    upnext.setAlpha(0.0f);


                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    songTitle.setAlpha(1.0f);
                    header.setAlpha(0.0f);
                    header.setText(null);
                    addButton.setAlpha(0.0f);
                    addButton.setEnabled(false);
                    slidingLayout.setTouchEnabled(true);
                    upnext.setAlpha(1.0f);


                }
            }


            @Override
            public void onPanelSlide(@NonNull View bottomSheet, float slideOffset) {
                overlay.setAlpha(1.0f-slideOffset);
                panelButton.setAlpha(1.0f-slideOffset);

            }
        });

        if(PlayerController.isPlaying() && !prevPlaying){
            fabRevealLayout.revealSecondaryView();
            prevPlaying = true;

        }else if (!PlayerController.isPlaying() && prevPlaying){
            prepareBackTransition(fabRevealLayout);
            fabRevealLayout.revealMainView();
            prevPlaying = false;
        }

        if(slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
            fake_toolbar.setAlpha(0.0f);
            footer.setAlpha(1.0f);
        }else{
            fake_toolbar.setAlpha(1.0f);
            footer.setAlpha(0.0f);
        }


        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                footer.setAlpha(1.0f-slideOffset);
                fake_toolbar.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    //playPauseView.setEnabled(false);
                    playPauseView2.setEnabled(false);
                    footer.setAlpha(0.0f);
                    fake_toolbar.setAlpha(1.0f);
                    rep.setEnabled(true);
                    shuff.setEnabled(true);
                    if(frame != -1){
                        colourWindows();
                    }else{
                        colourDefaultWindows();
                    }

                } else if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
                    //playPauseView.setEnabled(false);
                    playPauseView2.setEnabled(false);
                    //colourDefaultWindows();


                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    //playPauseView.setEnabled(true);
                    playPauseView2.setEnabled(true);
                    footer.setAlpha(1.0f);
                    fake_toolbar.setAlpha(0.0f);
                    //colourDefaultWindows();
                    rep.setEnabled(false);
                    shuff.setEnabled(false);
                }

            }
        });

        if(slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
            rep.setEnabled(false);
            shuff.setEnabled(false);
        }

        slidingLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        configureFABReveal();

        if (PlayerController.isPlaying())
            fabRevealLayout.revealSecondaryView();
        else
            fabRevealLayout.revealMainView();

        seekbar.setOnSeekBarChangeListener(this);

        detector = new SimpleGestureFilter(this, new SimpleGestureListener() {
            @Override
            public void onSwipe(int direction) {
                if(currentReference!=null) {
                    Animation fadeIn = AnimationUtils.loadAnimation(NowPlayingActivity.this, R.anim.fade_in);

                    switch (direction) {

                        case SimpleGestureFilter.SWIPE_LEFT:
                            // Next song
                            PlayerController.skip();
                            artist.startAnimation(fadeIn);
                            break;
                        case SimpleGestureFilter.SWIPE_RIGHT:
                            // Previous song
                            PlayerController.previous();
                            artist.startAnimation(fadeIn);

                            break;
                        case SimpleGestureFilter.SWIPE_DOWN:
                            break;
                    }
                }

            }


            @Override
            public void onDoubleTap() {
                if(currentReference!=null) {
                    String lyrics = getLyrics(currentReference.location);

                    AlertDialog dialog = new AlertDialog.Builder(NowPlayingActivity.this)
                            .setTitle("Lyrics")
                            .setMessage(lyrics)
                            .setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Navigate.to(NowPlayingActivity.this, LyricsActivity.class, LyricsActivity.TAGGER_EXTRA, currentReference);
                                }
                            })
                            .setNegativeButton("BACK", null)
                            .show();
                    Themes.themeAlertDialog(dialog);

                }

            }
        });


        gallery.setOnTouchListener(new View.OnTouchListener() {
                                       @Override
                                       public boolean onTouch(View v, MotionEvent event) {
                                           detector.onTouchEvent(event);
                                           return true;
                                       }
                                   }
        );

        play.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
        fab.setOnClickListener(this);
        addButton.setOnClickListener(this);
        panelButton.setOnClickListener(this);
        shuff.setOnClickListener(this);
        rep.setOnClickListener(this);
        eq.setOnClickListener(this);
        back.setOnClickListener(this);

    }

    private void bindMiniPlayer(){
        if (findViewById(R.id.footer) != null) {

            miniart = (ImageView)findViewById(R.id.footer_thumb);
            prog = (ProgressBar) findViewById(R.id.songProgress);
            minisong = (TextView) findViewById(R.id.songTitle);
            miniartist = (TextView) findViewById(R.id.songArtist);
            footerView = (RelativeLayout)findViewById(R.id.footer);
            textHolder = (LinearLayout) findViewById(R.id.textHolder);
            //playPauseView = (MaterialPlayPauseButton) findViewById(R.id.playpause);

            playPauseView2 = (PlayPauseView) findViewById(R.id.btn_play);

            /*

            if(playPauseView!=null) {
                playPauseView.setColor(Themes.getButtonTint(this));

                playPauseView.setAnimDuration(300);
                playPauseView.setOnClickListener(this);
                if(PlayerController.isPlaying()) {
                    playPauseView.setVisibility(View.VISIBLE);
                    playPauseView.setToPlay();
                    playPauseView.setToPause();
                }
            }
            */

            if(playPauseView2!=null){
                playPauseView2.setOnClickListener(this);
            }



            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                LayerDrawable progressDrawable = (LayerDrawable) prog.getProgressDrawable();
                progressDrawable.findDrawableByLayerId(android.R.id.background).setColorFilter(
                        0x05000000,
                        PorterDuff.Mode.SRC_ATOP);
                progressDrawable.findDrawableByLayerId(android.R.id.progress).setColorFilter(
                        0xffffffff, PorterDuff.Mode.SRC_ATOP);

                progressDrawable.findDrawableByLayerId(android.R.id.progress).setColorFilter(
                        Themes.getAccent(), PorterDuff.Mode.SRC_ATOP);
            }
            else{
                prog.setProgressTintList(ColorStateList.valueOf(Themes.getAccent()));
                prog.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
            }

            updateMiniplayer();
        }

    }

    private void configureFABReveal() {
        fabRevealLayout.setOnRevealChangeListener(new OnRevealChangeListener() {
            @Override
            public void onMainViewAppeared(FABRevealLayout fabRevealLayout, View mainView) {
                showMainViewItems();
            }

            @Override
            public void onSecondaryViewAppeared(final FABRevealLayout fabRevealLayout, View secondaryView) {

                showSecondaryViewItems();
                //prepareBackTransition(fabRevealLayout);
            }
        });
    }

    private void showMainViewItems() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            scale(albumTitle, 50);
            scale(artistName, 150);
        }else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            scale(songTitle, 100);
        }
    }

    private void showSecondaryViewItems() {

        //scale(songTitle, 100);
        scale(prev, 150);
        scale(play, 100);
        scale(next, 200);
    }

    private void prepareBackTransition(final FABRevealLayout fabRevealLayout) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fabRevealLayout.revealMainView();
            }
        }, 2000);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(footer!=null && slidingLayout!=null){
            if(slidingLayout.getPanelState()!=SlidingUpPanelLayout.PanelState.COLLAPSED){
                footer.setAlpha(0.0f);
            }
        }

        if(overlay!=null && draglayout!=null){
            if(draglayout.getPanelState()!= SlidingUpPanelLayout.PanelState.COLLAPSED){
                overlay.setAlpha(0.0f);
                if(panelButton!=null){
                    panelButton.setAlpha(0.0f);
                }
            }
        }

    }

    private void scale(View view, long delay){
        view.setScaleX(0);
        view.setScaleY(0);
        view.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(500)
                .setStartDelay(delay)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.panelButton){
            if (draglayout != null) {
                if (draglayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED)
                {
                    draglayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                } else if (draglayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    draglayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                }
            }
        }

        else if(v.getId() == R.id.footer){
            if (slidingLayout != null) {
                if (slidingLayout.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED)
                {
                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        }

        else if(v.getId() == R.id.addButton){
            if(addButton.isEnabled()){
                PlaylistDialog.AddToNormal.alert(
                        this.findViewById(android.R.id.content),
                        new ArrayList<Song>(PlayerController.getQueue()),
                        R.string.header_add_queue_to_playlist);
            }
        }

        else if(v.getId() == R.id.fab){
            if(System.currentTimeMillis()%17 == 0) {
                showAd();
            }
            if(PlayerController.isPlaying()){
                prevPlaying=false;
            }
            PlayerController.togglePlay();
        }

        else if(v.getId() == R.id.btn_play){
            PlayerController.togglePlay();
        }


        else if(v.getId()== R.id.play) {
            PlayerController.togglePlay();

        }
        else if(v.getId() == R.id.prev) {
            PlayerController.previous();
        }

        else if( v.getId() == R.id.next) {
            PlayerController.skip();

        }
        //else if( v.getId() == R.id.playpause){
            //PlayerController.togglePlay();
        //}

        else if(v.getId() == R.id.shuff){
            PlayerController.toggleShuffle();
            if (PlayerController.isShuffle()) {
                Toast toast = Toast.makeText(this, R.string.confirm_enable_shuffle, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                Toast toast = Toast.makeText(this, R.string.confirm_disable_shuffle, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            updateShuffleIcon();
            queueFragment.updateShuffle();
        }

        else if(v.getId() == R.id.equalizer){
            if(slidingLayout.getPanelState()== SlidingUpPanelLayout.PanelState.EXPANDED){

                if(Prefs.useSysEq(this)){
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
                    }

                }
                else {
                    if(System.currentTimeMillis()%26 == 0) {
                        //showAd();
                    }
                    startActivity(new Intent(getApplicationContext(), EqualizerActivity.class));
                }

            }else if(slidingLayout.getPanelState()== SlidingUpPanelLayout.PanelState.COLLAPSED){
                PlayerController.togglePlay();
            }

        }
        else if(v.getId() == R.id.rep){

            PlayerController.toggleRepeat();
            if (PlayerController.isRepeat()) {
                Toast toast = Toast.makeText(this, R.string.confirm_enable_repeat, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                if (PlayerController.isRepeatOne()) {
                    Toast toast = Toast.makeText(this, R.string.confirm_enable_repeat_one, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(this, R.string.confirm_disable_repeat, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
            updateRepeatIcon();

        }
        else if(v.getId() == R.id.back){
            if(slidingLayout.getPanelState()== SlidingUpPanelLayout.PanelState.EXPANDED){
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }else if(slidingLayout.getPanelState()== SlidingUpPanelLayout.PanelState.COLLAPSED){
               slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        }

    }

    private void showAd(){

    }

    private String getLyrics(String filename){
        String EMPTY = "No embedded lyrics found!";
        File file = new File(filename);
        if(file.exists()){
            try {
                TagOptionSingleton.getInstance().setAndroid(true);
                AudioFile f = AudioFileIO.read(file);
                Tag newTag = f.getTag();
                String lyrics = newTag.getFirst(FieldKey.LYRICS);
                if(lyrics.trim().isEmpty())
                    return EMPTY;
                return lyrics.trim();
            }catch (Exception e){
                Crashlytics.log(e.getMessage());
                return EMPTY;
            }catch (NoClassDefFoundError e){
                Crashlytics.log(e.getMessage());
                return EMPTY;
            }
        }
        return EMPTY;
    }

    @Override
    public void onBackPressed() {

        if (draglayout != null &&
                (draglayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        draglayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            draglayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else if (slidingLayout != null &&
                (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        PlayerController.seek(seekBar.getProgress());
        userTouchingProgressBar = false;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //observer.stop();
        userTouchingProgressBar = true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && !userTouchingProgressBar) {
            // For keyboards and non-touch based things
            onStartTrackingTouch(seekBar);
            onStopTrackingTouch(seekBar);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        PlayerController.unregisterUpdateListener(this);
        PlayerController.unregisterErrorListener(this);
        observer.stop();
        observerThread = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        PlayerController.registerUpdateListener(this);
        PlayerController.registerErrorListener(this);
        onUpdate();
        if(slidingLayout!=null){
            if(slidingLayout.getPanelState()!=SlidingUpPanelLayout.PanelState.COLLAPSED){
                try{
                    //playPauseView.setEnabled(false);
                    playPauseView2.setEnabled(false);
                    footer.setAlpha(0.0f);
                    fake_toolbar.setAlpha(1.0f);
                    rep.setEnabled(true);
                    shuff.setEnabled(true);
                    if(frame != -1){
                        colourWindows();
                    }else{
                        colourDefaultWindows();
                    }

                }catch (Exception e){

                }
            }
        }

        if(draglayout!=null){
            if(draglayout.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED){
                try{
                    upnext.setAlpha(0.0f);
                    header.setAlpha(1.0f);
                    header.setText("Now Playing");
                    addButton.setAlpha(1.0f);
                    addButton.setEnabled(true);
                    scale(header, 100);
                    slidingLayout.setTouchEnabled(false);
                    overlay.setAlpha(0.0f);
                    panelButton.setAlpha(0.0f);

                }catch (Exception e){

                }
            }
        }
    }

    @Override
    public void onError(String message) {
        showSnackbar(message);
    }

    protected void showSnackbar(String message) {
        View content = findViewById(R.id.list);
        if (content == null) {
            content = findViewById(android.R.id.content);
        }
        Snackbar.make(content, message, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void onUpdate() {

        updateMiniplayer();
        updateRepeatIcon();
        updateShuffleIcon();

        Song nowPlaying = PlayerController.getNowPlaying();



        if(PlayerController.isPlaying()){
            fab.setImageResource(R.drawable.ic_pause_white_18dp);
            //play.setImageResource(R.drawable.ic_pause_24dp);
            if(!prevPlaying) {
                fabRevealLayout.revealSecondaryView();
                prevPlaying = true;
            }

        }else if (!PlayerController.isPlaying() && !PlayerController.isPreparing()){
            fab.setImageResource(R.drawable.ic_play_arrow_white_18dp);
            //play.setImageResource(R.drawable.ic_play_arrow_24dp);
            if(prevPlaying) {
                prepareBackTransition(fabRevealLayout);
                fabRevealLayout.revealMainView();
                prevPlaying = false;
            }
        }
        if (nowPlaying != null) {
            setPlayingStyle();
            String title = String.valueOf(PlayerController.getQueuePosition()+1)+ "  " + nowPlaying.songName;
            songTitle.setText(nowPlaying.songName);
            artistName.setText(nowPlaying.artistName);
            albumTitle.setText(nowPlaying.albumName);

            int duration = PlayerController.getDuration();
            timeDuration.setTime(duration);
            seekbar.setMax(duration);

            if (!observer.isRunning()) {
                observerThread = new Thread(observer);
                observerThread.start();
            }

            if (currentReference==null || !currentReference.equals(nowPlaying)) {
                // The following code only needs to be executed when the song changes, which
                // doesn't happen on every single UPDATE broadcast. Because of this, we can
                // reduce the number of redundant calls by only running this if the song has
                // changed.
                currentReference = nowPlaying;

                Bitmap art;

                try {
                    art = Util.getAlbumArt(NowPlayingActivity.this, currentReference.albumId);
                    if(art!=null) {
                        artist.setImageBitmap(art);
                    }else{
                        Glide.with(NowPlayingActivity.this).load(R.drawable.default_album_art_500).into(artist);
                    }
                    colourElements(art);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            setNotPlayingDefaults();
        }
        seekbar.setEnabled(nowPlaying != null);
    }

    public void updateMiniplayer(){
        try {
            if (DEBUG) Log.i(getClass().toString(), "Called updateMiniplayer");
            final Song nowPlaying = PlayerController.getNowPlaying();

            if (nowPlaying != null && footerView != null) {

                Uri artwork = Util.getAlbumArtUri(nowPlaying.albumId);
                Glide.with(this).load(artwork).placeholder(R.drawable.default_album_art_75).crossFade(200).into(miniart);

                minisong.setText(nowPlaying.songName);
                miniartist.setText(nowPlaying.artistName);

                prog.setMax(PlayerController.getDuration());

                if (PlayerController.isPlaying()|| PlayerController.isPreparing()) {
                    //playPauseView.setToPause();
                    if(!playPauseView2.isPlay())
                        playPauseView2.play();

                    if (!observer.isRunning()) {
                        observerThread = new Thread(observer);
                        observerThread.start();
                    }
                } else {
                    prog.setProgress(PlayerController.getCurrentPosition());
                    //playPauseView.setToPlay();
                    //if(playPauseView2.isPlay()) {
                        playPauseView2.pause();
                    //}
                }
            }
        }catch (Exception e){

        }
    }

    private void updateRepeatIcon(){
        if (PlayerController.isRepeat()) {
            rep.setAlpha(1.0f);
            rep.setImageResource(R.drawable.ic_sync_white_24dp);
            rep.setContentDescription(getResources().getString(R.string.action_enable_repeat_one));

        } else {
            if (PlayerController.isRepeatOne()) {
                rep.setImageResource(R.drawable.ic_replay_white_24dp);
                rep.setContentDescription(getResources().getString(R.string.action_disable_repeat));
            } else {
                rep.setImageResource(R.drawable.ic_sync_white_24dp);
                rep.setAlpha(0.5f);
                rep.setContentDescription(getResources().getString(R.string.action_enable_repeat));
            }
        }
    }

    private void updateShuffleIcon(){
        if (PlayerController.isShuffle()) {
            shuff.setAlpha(1.0f);
            shuff.setContentDescription(getResources().getString(R.string.action_disable_shuffle));
        } else {
            shuff.setAlpha(0.5f);
            shuff.setContentDescription(getResources().getString(R.string.action_enable_shuffle));
        }
    }

    private void setNotPlayingDefaults(){
        if(PlayerController.getNowPlaying()==null) {
            if (draglayout.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)
                draglayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            if (slidingLayout.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            slidingLayout.setPanelHeight(0);
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    private void setPlayingStyle(){
        if(PlayerController.getNowPlaying()!=null && slidingLayout.getPanelHeight()==0){
            slidingLayout.setPanelHeight(Util.getActionBatHeight(this));
        }
    }

    private void colourElements(Bitmap img){
        if(img!=null){
            Palette.from(img).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int frameColor = palette.getVibrantColor(Color.TRANSPARENT);
                    Palette.Swatch swatch = palette.getVibrantSwatch();
                    Palette.Swatch bgswatch = palette.getDarkVibrantSwatch();

                    if (swatch == null || frameColor == Color.TRANSPARENT) {
                        frameColor = palette.getLightVibrantColor(Color.TRANSPARENT);
                        swatch = palette.getLightVibrantSwatch();
                    }

                    if (swatch == null || frameColor == Color.TRANSPARENT) {
                        frameColor = palette.getLightMutedColor(Color.TRANSPARENT);
                        swatch = palette.getLightMutedSwatch();
                    }


                    if (swatch != null && frameColor != Color.TRANSPARENT) {
                        try {
                            int color = swatch.getTitleTextColor();

                            if(!Prefs.animOff(NowPlayingActivity.this)) {
                                fab.setBackgroundTintList(ColorStateList.valueOf(frameColor));
                                fab.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);

                            }else{
                                albumTitle.setTextColor(color);
                                artistName.setTextColor(color);
                                artistName.setAlpha(0.6f);
                            }
                            //songTitle.setTextColor(color);
                            header.setTextColor(color);
                            //panelButton.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                            addButton.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                            playview.setBackgroundColor(frameColor);
                            bottom.setBackgroundColor(frameColor);

                        } catch (Exception e) {

                        }

                        frame = frameColor;

                        if(slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
                            colourWindows();
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            try {
                                fab.getDrawable().mutate().setTint(swatch.getTitleTextColor());

                            } catch (Exception e) {

                            }

                        }


                    } else {

                        fab.setBackgroundTintList(ColorStateList.valueOf(accent));
                        fab.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);

                        frame = -1;

                        if(slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                            colourDefaultWindows();
                        }


                        playview.setBackgroundColor(accent);
                        bottom.setBackgroundColor(accent);
                        //songTitle.setTextColor(black);
                        header.setTextColor(black);
                        //panelButton.setColorFilter(black, PorterDuff.Mode.MULTIPLY);
                        addButton.setColorFilter(black, PorterDuff.Mode.MULTIPLY);

                    }

                    if(bgswatch != null ){
                        container.setBackgroundColor(bgswatch.getRgb());
                    }else{
                        bgswatch = palette.getDarkMutedSwatch();
                        if(bgswatch!=null) {
                            container.setBackgroundColor(bgswatch.getRgb());
                        }else{
                            bgswatch = palette.getMutedSwatch();
                            if(bgswatch!=null) {
                                container.setBackgroundColor(bgswatch.getRgb());
                            }else{
                                container.setBackgroundColor(black);
                            }
                        }
                    }
                }
            });
        }else{

            fab.setBackgroundTintList(ColorStateList.valueOf(accent));
            fab.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);

            frame = -1;

            if(slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
                colourDefaultWindows();
            }

            playview.setBackgroundColor(accent);
            bottom.setBackgroundColor(accent);
            //songTitle.setTextColor(black);
            header.setTextColor(black);
            //panelButton.setColorFilter(black, PorterDuff.Mode.MULTIPLY);
            addButton.setColorFilter(black, PorterDuff.Mode.MULTIPLY);
            container.setBackgroundColor(black);

        }

    }

    private void colourWindows(){
        //Toast.makeText(this, "Window coloured with " + String.valueOf(frame), Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Window window = NowPlayingActivity.this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                if(Prefs.colourSB(this)) {
                    window.setStatusBarColor(frame);
                }else{
                    window.setStatusBarColor(Themes.getBlack());
                }
                if (Prefs.colourNB(NowPlayingActivity.this)) {
                    window.setNavigationBarColor(frame);
                } else {
                    //window.setNavigationBarColor(Themes.getBlack());
                }
            } catch (Exception e) {

            }
        }
    }

    private void colourDefaultWindows(){
        //Toast.makeText(this, "Window coloured with default : " + String.valueOf(frame), Toast.LENGTH_SHORT).show();

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
                    window.setNavigationBarColor(accent);
                }
            }catch (Exception e){

            }

        }
    }

    private static class MediaObserver implements Runnable, PlayerController.UpdateListener {

        private boolean run;
        private NowPlayingActivity parent;
        private final Runnable updater;

        MediaObserver(NowPlayingActivity parent) {
            this.parent = parent;

            updater = new Runnable() {
                @Override
                public void run() {
                    int position = PlayerController.getCurrentPosition();
                    if (!MediaObserver.this.parent.userTouchingProgressBar) {
                        MediaObserver.this.parent.seekbar.setProgress(position);

                    }
                    MediaObserver.this.parent.prog
                            .setProgress(PlayerController.getCurrentPosition());

                    MediaObserver.this.parent.timePosition.setTime(position);
                }
            };
        }

        public void stop() {
            run = false;
        }

        public boolean isRunning() {
            return run;
        }

        @Override
        public void run() {
            run = true;
            while (run) {
                parent.runOnUiThread(updater);
                try {
                    Thread.sleep(200);
                } catch (Exception ignored) {
                }
            }
        }

        @Override
        public void onUpdate() {
            final boolean wasRunning = run;
            run = PlayerController.isPlaying();
            if (!wasRunning && run) {
                parent.observerThread.run();
            }
        }
    }
}
