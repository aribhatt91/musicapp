package com.optimus.music.player.onix.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.LibraryActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.MusicPlayer.PlayerService;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;

public class AppWidgetSmall extends AppWidgetProvider
{
    Context context;
    AppWidgetManager appWidgetManager;
    int[] appWidgeIds;
    Song song;
    boolean isPlaying;


    public static String WIDGETSMALL = "APPWIDGETSMALL";

    private static AppWidgetSmall instance;

    public static synchronized AppWidgetSmall getInstance(){
        if(instance == null){
            instance = new AppWidgetSmall();
        }
        return instance;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        this.context = context;
        this.appWidgeIds = appWidgetIds;
        this.appWidgetManager = appWidgetManager;
        final int count = appWidgetIds.length;
        isPlaying = PlayerController.isPlaying();
        //Toast.makeText(context, "onUpdate called", Toast.LENGTH_SHORT).show();

        song = PlayerController.getNowPlaying();








        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.appwidget_small);


            performUpdate(context, appWidgetIds);


            initPlayIntent(context, appWidgetIds,remoteViews, appWidgetManager, widgetId);
            initPrevIntent(context, appWidgetIds,remoteViews, appWidgetManager, widgetId);
            initNextIntent(context, appWidgetIds,remoteViews, appWidgetManager, widgetId);
            initLaunchIntent(context, appWidgetIds,remoteViews, appWidgetManager, widgetId);





            Intent intent = new Intent(context, AppWidgetSmall.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.notificationIcon, pendingIntent);
            //remoteViews.setImageViewBitmap(R.id.notificationIcon, PlayerController.getArtwork());

            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }


    }


    public void notifyChange( Context context, String what, Song song){
        try {
            if ((what.equals(PlayerService.ACTION_META_CHANGE) && this.song == null || !this.song.equals(song))
                    || isPlaying!=PlayerController.isPlaying()) {
                this.song = song;
                isPlaying = PlayerController.isPlaying();
                final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
                performUpdate(context, appWidgetIds);
            }
        }catch (Exception e){

        }

    }



    public void performUpdate(Context context, final int[] appWidgeIds){
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.appwidget_small);
        Bitmap icon = null;
        try {
            remoteViews.setImageViewResource(R.id.notificationIcon, R.drawable.default_album_art_75);


            if(PlayerController.getNowPlaying()!=null) {

                final String trackname = PlayerController.getNowPlaying().songName;
                final String albumName = PlayerController.getNowPlaying().albumName;
                final String artistName = PlayerController.getNowPlaying().artistName;

                Uri uri = Util.getAlbumArtUri(PlayerController.getNowPlaying().albumId);

                remoteViews.setImageViewUri(R.id.notificationIconSmall, Uri.parse(""));
                if(uri!=null)
                    remoteViews.setImageViewUri(R.id.notificationIconSmall, uri);

                remoteViews.setTextViewText(R.id.notificationContentTitle, trackname);
                remoteViews.setTextViewText(R.id.notificationContentText, albumName);


                if (PlayerController.isPlaying()) {
                    remoteViews.setImageViewResource(R.id.notificationPause, R.drawable.ic_pause_white_18dp);
                } else {
                    remoteViews.setImageViewResource(R.id.notificationPause, R.drawable.ic_play_arrow_white_18dp);
                }
            }else{
                remoteViews.setImageViewUri(R.id.notificationIconSmall, Uri.parse(""));
                remoteViews.setTextViewText(R.id.notificationContentTitle, "");
                remoteViews.setTextViewText(R.id.notificationContentText, "");
                remoteViews.setImageViewResource(R.id.notificationPause, R.drawable.ic_play_arrow_white_18dp);
            }

            linkButtons(context, appWidgeIds);
            pushUpdate(context, appWidgeIds, remoteViews);
        }catch (Exception e){

        }

    }

    private void linkButtons(Context context, final int[] appWidgeIds){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        for (int i = 0; i < appWidgeIds.length; i++) {
            int widgetId = appWidgeIds[i];

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.appwidget_small);

            initPlayIntent(context, appWidgeIds,remoteViews, appWidgetManager, widgetId);
            initPrevIntent(context, appWidgeIds,remoteViews, appWidgetManager, widgetId);
            initNextIntent(context, appWidgeIds,remoteViews, appWidgetManager, widgetId);
            initLaunchIntent(context, appWidgeIds,remoteViews, appWidgetManager, widgetId);

        }
    }

    private void pushUpdate(final Context context, final int[] appWidgetIds, final RemoteViews views){
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if(appWidgetIds!=null){
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }else{
            appWidgetManager.updateAppWidget(new ComponentName(context, getClass()), views);
        }
    }

    private void initLaunchIntent(Context context, int[] appWidgetIds, RemoteViews remoteViews,
                                  AppWidgetManager appWidgetManager, int widgetId){
        remoteViews.setOnClickPendingIntent(R.id.notificationIcon,
                PendingIntent.getActivity(context, 0,
                        new Intent(context, LibraryActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT));

        appWidgetManager.updateAppWidget(widgetId, remoteViews);

    }



    public void initPlayIntent(Context context, int[] appWidgetIds, RemoteViews remoteViews,
                               AppWidgetManager appWidgetManager, int widgetId){
        Intent playIntent = new Intent(context, AppWidgetLarge.class);
        playIntent.setAction(PlayerService.ACTION_TOGGLE_PLAY);
        playIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(context,
                0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notificationPause, pendingPlayIntent);
        //manageResources(context, remoteViews);

        appWidgetManager.updateAppWidget(widgetId, remoteViews);

    }

    private void initNextIntent(Context context, int[] appWidgetIds, RemoteViews remoteViews,
                                AppWidgetManager appWidgetManager, int widgetId){
        Intent fastForwardIntent = new Intent(context, AppWidgetLarge.class);
        fastForwardIntent.setAction(PlayerService.ACTION_NEXT);
        fastForwardIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context,
                0, fastForwardIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notificationSkipNext, pendingNextIntent);
        //manageResources(context, remoteViews);

        appWidgetManager.updateAppWidget(widgetId, remoteViews);


    }

    private void initPrevIntent(Context context, int[] appWidgetIds, RemoteViews remoteViews,
                                AppWidgetManager appWidgetManager, int widgetId){
        Intent fastBackwardIntent = new Intent(context, AppWidgetLarge.class);
        fastBackwardIntent.setAction(PlayerService.ACTION_PREV);
        fastBackwardIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(context,
                0, fastBackwardIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notificationSkipPrevious, pendingPrevIntent);
        //manageResources(context, remoteViews);

        appWidgetManager.updateAppWidget(widgetId, remoteViews);

    }






    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.appwidget_small);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds;
        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        final String action = intent.getAction();

        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            Log.d("Widget Reciever", "Widget DELETED");

            //The widget is being deleted off the desktop
            final int appWidgetId = intent.getExtras().getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                this.onDeleted(context, new int[] { appWidgetId });
            }
        }
        else {
            //Manage Play
            if (intent.getAction().equals(PlayerService.ACTION_TOGGLE_PLAY)) {
                Log.d("Widget Reciever", "Widget Play");
                PlayerController.togglePlay();

                //remoteViews.setImageViewResource(R.id.notificationPause, R.drawable.ic_shuffle_white_24dp);


                //Manage Next
            } else if (intent.getAction().equals(PlayerService.ACTION_NEXT)) {
                Log.d("Widget Reciever", "Widget Next");
                //If Main is running
                PlayerController.skip();

                //Manage Previous
            } else if (intent.getAction().equals(PlayerService.ACTION_PREV)) {
                Log.d("Widget Reciever", "Widget Previous");
                PlayerController.previous();

            } else if(intent.getAction().equals(PlayerService.ACTION_META_CHANGE)){
                Log.d("Widget Reciever", "Widget Metadata update");
                performUpdate(context, appWidgetIds);

            }
            performUpdate(context, appWidgetIds);

            //pushUpdate(context, appWidgetIds, remoteViews);
            super.onReceive(context, intent);
        }

    }




}
