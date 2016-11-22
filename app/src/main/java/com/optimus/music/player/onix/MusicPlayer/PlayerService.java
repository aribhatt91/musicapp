package com.optimus.music.player.onix.MusicPlayer;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BadParcelableException;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.IdRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RemoteViews;


import com.crashlytics.android.Crashlytics;
import com.optimus.music.player.onix.BuildConfig;
import com.optimus.music.player.onix.Common.Instances.Song;

import com.optimus.music.player.onix.IPlayerService;
import com.optimus.music.player.onix.LibraryActivity;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.MusicPlayer.ManagedMediaPlayer;
import com.optimus.music.player.onix.Widget.AppWidgetJumbo;
import com.optimus.music.player.onix.Widget.AppWidgetLarge;
import com.optimus.music.player.onix.Widget.AppWidgetMaterial;
import com.optimus.music.player.onix.Widget.AppWidgetSmall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerService extends Service implements MusicPlayer.OnPlaybackChangeListener {

    private static final String TAG = "PlayerService";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static final int NOTIFICATION_ID = 1;

    // Intent Action & Extra names
    /**
     * Toggle between play and pause
     */
    public static final String ACTION_TOGGLE_PLAY = "com.optimus.music.player.onix.action.TOGGLE_PLAY";

    public static final String ACTION_META_CHANGE = "com.optimus.music.player.onix.action.META";

    /**
     * Skip to the previous song
     */
    public static final String ACTION_PREV = "com.optimus.music.player.onix.action.PREVIOUS";
    /**
     * Skip to the next song
     */
    public static final String ACTION_NEXT = "com.optimus.music.player.onix.action.NEXT";
    /**
     * Stop playback and kill service
     */
    public static final String ACTION_STOP = "com.optimus.music.player.onix.action.STOP";

    /**
     * The service instance in use (singleton)
     */
    private static PlayerService instance;

    /**
     * Used in binding and unbinding this service to the UI process
     */
    private static IBinder binder;

    // Instance variables
    /**
     * The media player for the service instance
     */
    private MusicPlayer musicPlayer;
    private boolean finished = false; // Don't attempt to release resources more than once

    AppWidgetSmall appWidgetSmall = AppWidgetSmall.getInstance();
    AppWidgetJumbo appWidgetJumbo = AppWidgetJumbo.getInstance();
    AppWidgetLarge appWidgetLarge = AppWidgetLarge.getInstance();
    AppWidgetMaterial appWidgetMaterial = AppWidgetMaterial.getInstance();

    @Override
    public IBinder onBind(Intent intent) {
        if (binder == null) {
            binder = new Stub();
        }
        return binder;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.i(TAG, "onCreate() called");

        if (instance == null) {
            instance = this;
        } else {
            if (DEBUG) Log.w(TAG, "Attempted to create a second PlayerService");
            stopSelf();
            return;
        }

        if (musicPlayer == null) {
            musicPlayer = new MusicPlayer(this);
        }

        musicPlayer.setPlaybackChangeListener(this);
        musicPlayer.loadState();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //this is new
        if (intent != null) {
            if (musicPlayer!=null && intent.hasExtra(Intent.EXTRA_KEY_EVENT)) {
                MediaButtonReceiver.handleIntent(musicPlayer.getMediaSession(), intent);
                Log.i(TAG, intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT).toString());
            } else if (ACTION_STOP.equals(intent.getAction())) {
                stop();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.i(TAG, "Called onDestroy()");
        try {
            musicPlayer.saveState(null);
        } catch (Exception ignored) {

        }
        finish();
        super.onDestroy();
    }

    public static PlayerService getInstance() {
        return instance;
    }

    /**
     * Generate and post a notification for the current player status
     * Posts the notification by starting the service in the foreground
     */
    public void notifyNowPlaying() {
        if (DEBUG) Log.i(TAG, "notifyNowPlaying() called");

        if (musicPlayer==null || musicPlayer.getNowPlaying() == null) {
            if (DEBUG) Log.i(TAG, "Not showing notification -- nothing is playing");
            return;
        }

        // Create the compact view
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification);
        // Create the expanded view
        RemoteViews notificationViewExpanded =
                new RemoteViews(getPackageName(), R.layout.notification_expanded);

        // Set the artwork for the notification
        if (musicPlayer.getArtwork() != null) {
            notificationView.setImageViewBitmap(R.id.notificationIcon, musicPlayer.getArtwork());
            notificationViewExpanded.setImageViewBitmap(R.id.notificationIcon, musicPlayer.getArtwork());
        } else {
            notificationView.setImageViewResource(R.id.notificationIcon, R.drawable.default_album_art_75);
            notificationViewExpanded.setImageViewResource(R.id.notificationIcon, R.drawable.default_album_art_);
        }

        // If the player is playing music, set the track info and the button intents
        if (musicPlayer.getNowPlaying() != null) {
            // Update the info for the compact view
            notificationView.setTextViewText(R.id.notificationContentTitle,
                    musicPlayer.getNowPlaying().songName);
            //notificationView.setTextViewText(R.id.notificationContentText, musicPlayer.getNowPlaying().albumName);
            notificationView.setTextViewText(R.id.notificationSubText,
                    musicPlayer.getNowPlaying().artistName);

            // Update the info for the expanded view
            notificationViewExpanded.setTextViewText(R.id.notificationContentTitle,
                    musicPlayer.getNowPlaying().songName);
            notificationViewExpanded.setTextViewText(R.id.notificationContentText,
                    musicPlayer.getNowPlaying().albumName);
            notificationViewExpanded.setTextViewText(R.id.notificationSubText,
                    musicPlayer.getNowPlaying().artistName);
        }

        // Set the button intents for the compact view
        setNotificationButton(notificationView, R.id.notificationSkipPrevious, ACTION_PREV);
        setNotificationButton(notificationView, R.id.notificationSkipNext, ACTION_NEXT);
        setNotificationButton(notificationView, R.id.notificationPause, ACTION_TOGGLE_PLAY);
        setNotificationButton(notificationView, R.id.notificationStop, ACTION_STOP);

        // Set the button intents for the expanded view
        setNotificationButton(notificationViewExpanded, R.id.notificationSkipPrevious, ACTION_PREV);
        setNotificationButton(notificationViewExpanded, R.id.notificationSkipNext, ACTION_NEXT);
        setNotificationButton(notificationViewExpanded, R.id.notificationPause, ACTION_TOGGLE_PLAY);
        setNotificationButton(notificationViewExpanded, R.id.notificationStop, ACTION_STOP);

        // Update the play/pause button icon to reflect the player status
        if (!(musicPlayer.isPlaying() || musicPlayer.isPreparing())) {
            notificationView.setImageViewResource(R.id.notificationPause,
                    R.drawable.ic_play_arrow_white_24dp);
            notificationViewExpanded.setImageViewResource(R.id.notificationPause,
                    R.drawable.ic_play_arrow_white_24dp);
        } else {
            notificationView.setImageViewResource(R.id.notificationPause,
                    R.drawable.ic_pause_white_24dp);
            notificationViewExpanded.setImageViewResource(R.id.notificationPause,
                    R.drawable.ic_pause_white_24dp);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(
                        (musicPlayer.isPlaying() || musicPlayer.isPreparing())
                                ? R.drawable.small_icon
                                : R.drawable.small_icon)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setContentIntent(PendingIntent.getActivity(this, 0,
                        new Intent(this, LibraryActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT));

        Notification notification = builder.build();

        // Manually set the expanded and compact views
        notification.contentView = notificationView;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            notification.bigContentView = notificationViewExpanded;
        }

        startForeground(NOTIFICATION_ID, notification);
    }


    private void setNotificationButton(RemoteViews notificationView, @IdRes int viewId,
                                       String action) {
        notificationView.setOnClickPendingIntent(viewId,
                PendingIntent.getBroadcast(this, 1,
                        new Intent(this, Listener.class).setAction(action), 0));
    }

    public void stop() {
        if (DEBUG) Log.i(TAG, "stop() called");

        // If the UI process is still running, don't kill the process, only remove its notification
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos =
                activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++) {
            if (procInfos.get(i).processName.equals(BuildConfig.APPLICATION_ID) && musicPlayer!=null) {
                musicPlayer.pause();
                stopForeground(true);
                return;
            }
        }

        // If the UI process has already ended, kill the service and close the player
        finish();
    }

    public void finish() {
        if (DEBUG) Log.i(TAG, "finish() called");
        if (!finished) {
            try{
                musicPlayer.release();
                musicPlayer = null;
                stopForeground(true);
                instance = null;
                stopSelf();
                finished = true;
            }catch(Exception e){

            }
        }
    }

    @Override
    public void onPlaybackChange() {
        try{
            appWidgetSmall.notifyChange(getInstance(), ACTION_META_CHANGE, musicPlayer.getNowPlaying());
            appWidgetLarge.notifyChange(getInstance(), ACTION_META_CHANGE, musicPlayer.getNowPlaying());
            appWidgetJumbo.notifyChange(getInstance(), ACTION_META_CHANGE, musicPlayer.getNowPlaying());
            appWidgetMaterial.notifyChange(getInstance(), ACTION_META_CHANGE, musicPlayer.getNowPlaying());


        }catch (Exception e){

        }
        notifyNowPlaying();
    }

    public static class Listener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                if (DEBUG) Log.i(TAG, "Intent received (action = null)");
                return;
            }

            if (DEBUG) Log.i(TAG, "Intent received (action = \"" + intent.getAction() + "\")");

            if (instance == null) {
                if (DEBUG) Log.i(TAG, "Service not initialized");
                return;
            }

            if (instance.musicPlayer!=null && instance.musicPlayer.getNowPlaying() != null) {
                try {
                    instance.musicPlayer.saveState(intent.getAction());
                } catch (IOException e) {
                    Crashlytics.logException(e);
                    if (DEBUG) e.printStackTrace();
                }
            }

            switch (intent.getAction()) {
                case (ACTION_TOGGLE_PLAY):
                    try {
                        instance.musicPlayer.togglePlay();
                        instance.musicPlayer.updateUi();
                    }catch (Exception e){
                        Crashlytics.log(e.getMessage());
                    }

                    break;
                case (ACTION_PREV):
                    try {
                        instance.musicPlayer.skipPrevious();
                        instance.musicPlayer.updateUi();
                    }catch (Exception e){
                        Crashlytics.log(e.getMessage());
                    }
                    break;
                case (ACTION_NEXT):
                    try {
                        instance.musicPlayer.skip();
                        instance.musicPlayer.updateUi();
                    }catch (Exception e){
                        Crashlytics.log(e.getMessage());
                    }
                    break;
                case (ACTION_STOP):
                    try {
                        instance.stop();
                        instance.musicPlayer.updateUi();

                    }catch (Exception e){
                        Crashlytics.log(e.getMessage());
                    }
                    break;
            }
        }
    }



    /**
     * Receives media button presses from in line remotes, input devices, and other sources

    public static class RemoteControlReceiver extends BroadcastReceiver {
        static final long CLICK_DELAY = 150;
        static long lastClick =0;


        public RemoteControlReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle Media button Intents

            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if(event==null) return;

                final long currclick = event.getEventTime();
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            try {// ADDED TRY CATCH BLOCKS AFTER CRASH ISSUE #141
                                instance.musicPlayer.togglePlay();
                            }catch (Exception e){
                                Crashlytics.log(e.getMessage());
                            }
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            try {
                                instance.musicPlayer.play();
                            }catch (Exception e){
                                Crashlytics.log(e.getMessage());
                            }
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            try {
                                instance.musicPlayer.pause();
                            }catch (Exception e){
                                Crashlytics.log(e.getMessage());
                            }
                            break;
                        case KeyEvent.KEYCODE_MEDIA_NEXT:
                            try {
                                instance.musicPlayer.skip();
                            }catch (Exception e){
                                Crashlytics.log(e.getMessage());
                            }
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                            try {
                                instance.musicPlayer.skipPrevious();
                            }catch (Exception e){
                                Crashlytics.log(e.getMessage());
                            }
                            break;
                    }
                }
                else if(event.getAction() == KeyEvent.ACTION_DOWN){

                }
            }
        }
    }
        */

    public static class Stub extends IPlayerService.Stub {

        @Override
        public void stop() throws RemoteException {
            try {
                if (instance != null && instance.musicPlayer != null)
                    instance.stop();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void skip() throws RemoteException {
            try {
                if (instance != null && instance.musicPlayer != null)
                    instance.musicPlayer.skip();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void previous() throws RemoteException {
            try {
                if (instance != null && instance.musicPlayer != null)
                    instance.musicPlayer.skipPrevious();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void begin() throws RemoteException {
            try {
                instance.musicPlayer.prepare(true);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void togglePlay() throws RemoteException {
            try {
                if(instance!=null && instance.musicPlayer!=null)
                    instance.musicPlayer.togglePlay();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void play() throws RemoteException {
            try {
                if(instance!=null && instance.musicPlayer!=null)
                    instance.musicPlayer.play();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void pause() throws RemoteException {
            try {
                if (instance != null && instance.musicPlayer != null)
                    instance.musicPlayer.play();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void setShuffle(boolean shuffle) throws RemoteException {
            try {
                instance.musicPlayer.setShuffle(shuffle);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void setRepeat(int repeat) throws RemoteException {
            try {
                if (instance != null && instance.musicPlayer != null)
                    instance.musicPlayer.setRepeat(repeat);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void setQueue(List<Song> newQueue, int newPosition) throws RemoteException {
            try {
                if (instance != null && instance.musicPlayer != null)
                    instance.musicPlayer.setQueue(newQueue, newPosition);
            }catch (Exception e){
                Crashlytics.log(e.getMessage());
            }
        }

        @Override
        public void changeSong(int position) throws RemoteException {
            try {
                if (instance != null && instance.musicPlayer != null)
                    instance.musicPlayer.changeSong(position);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void editQueue(List<Song> newQueue, int newPosition) throws RemoteException {
            if(instance!=null && instance.musicPlayer!=null)
                instance.musicPlayer.editQueue(newQueue, newPosition);
        }

        @Override
        public void queueNext(Song song) throws RemoteException {
            if(instance!=null && instance.musicPlayer!=null)
                instance.musicPlayer.queueNext(song);
        }

        @Override
        public void queueNextList(List<Song> songs) throws RemoteException {
            if(instance!=null && instance.musicPlayer!=null)
                instance.musicPlayer.queueNext(songs);
        }

        @Override
        public void queueLast(Song song) throws RemoteException {
            if(instance!=null && instance.musicPlayer!=null)
                instance.musicPlayer.queueLast(song);
        }

        @Override
        public void queueLastList(List<Song> songs) throws RemoteException {
            if(instance!=null && instance.musicPlayer!=null)
                instance.musicPlayer.queueLast(songs);
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            if(instance!=null && instance.musicPlayer!=null)
                instance.musicPlayer.seekTo(position);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            if(instance!=null && instance.musicPlayer!=null)
                return instance.musicPlayer.isPlaying();
            else return false;
        }

        @Override
        public boolean isPreparing() throws RemoteException{
            return instance!=null && instance.musicPlayer!=null && instance.musicPlayer.isPreparing();
        }

        @Override
        public Song getNowPlaying() throws RemoteException {
            try {
                if(instance!=null && instance.musicPlayer!=null)
                    return instance.musicPlayer.getNowPlaying();
                else return null;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public List<Song> getQueue() throws RemoteException {
            try {
                if (instance != null && instance.musicPlayer != null)
                    return instance.musicPlayer.getQueue();
                else return null;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public int getQueuePosition() throws RemoteException {
            return instance.musicPlayer.getQueuePosition();
        }

        @Override
        public int getQueueSize() throws RemoteException {
            try {
                return instance.musicPlayer.getQueueSize();
            }catch (Exception e){
                Crashlytics.log(e.getMessage());
                return 0;
            }
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return instance.musicPlayer.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            try {
                return instance.musicPlayer.getDuration();
            }catch (Exception e){
                Crashlytics.log(e.getMessage());
                return 0;
            }
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            try {
                return instance.musicPlayer.getAudioSessionId();
            }catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        public long getSleepTimerEndTime() throws RemoteException {
            try {
                return instance.musicPlayer.getSleepTimerEndTime();
            } catch (Exception exception) {
                return 0;
            }
        }

        @Override
        public void setSleepTimerEndTime(long timestampInMillis) throws RemoteException {
            try {
                instance.musicPlayer.setSleepTimer(timestampInMillis);
            } catch (Exception exception) {
            }
        }
    }
}
