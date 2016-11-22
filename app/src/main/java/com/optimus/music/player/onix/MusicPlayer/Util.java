package com.optimus.music.player.onix.MusicPlayer;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.Themes;

import java.io.FileDescriptor;
import java.util.List;
import java.util.UUID;

public class Util {

    /**
     * This UUID corresponds to the UUID of an Equalizer Audio Effect. It has been copied from
     * {@link AudioEffect#EFFECT_TYPE_EQUALIZER} for backwards compatibility since this field was
     * added in API level 18.
     */
    private static final UUID EQUALIZER_UUID;

    public static final String FB_PKG = "com.facebook.katana";
    public static final String MSG_PKG = "com.facebook.orca";

    public static final String FBPAGE = "onixmusicandroid";
    public static final String TWITTERPAGE = "onixmusicplayer";
    public static final String GPLUS = "https://plus.google.com/communities/106883264473647450292";
    public static final String PLAY = "https://play.google.com/store/apps/details?id=";

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            EQUALIZER_UUID = AudioEffect.EFFECT_TYPE_EQUALIZER;
        } else {
            EQUALIZER_UUID = UUID.fromString("0bed4300-ddd6-11db-8f34-0002a5d5c51b");
        }
    }

    /**
     * This class is never instantiated
     */
    private Util() {

    }

    public static Intent getSystemEqIntent(Context c) {
        Intent systemEqualizer = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
        systemEqualizer.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, c.getPackageName());
        systemEqualizer.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, PlayerController.getAudioSessionId());

        ActivityInfo info = systemEqualizer.resolveActivityInfo(c.getPackageManager(), 0);
        if (info != null && !info.name.startsWith("com.android.musicfx")) {
            return systemEqualizer;
        } else {
            return null;
        }
    }

    /**
     * Checks whether the current device is capable of instantiating and using an
     * {@link android.media.audiofx.Equalizer}
     * @return True if an Equalizer may be used at runtime
     */
    public static boolean hasEqualizer() {
        for (AudioEffect.Descriptor effect : AudioEffect.queryEffects()) {
            if (EQUALIZER_UUID.equals(effect.type)) {
                return true;
            }
        }
        return false;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
//http://stackoverflow.com/questions/12301510/how-to-get-the-actionbar-height
    public static int getActionBatHeight(Context context){
        int h = 0;
        try{
            TypedValue tv = new TypedValue();
            if (context.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
            {
                h = TypedValue.complexToDimensionPixelSize(tv.data,context.getResources().getDisplayMetrics());
            }

        }catch (Exception e){

        }

        return h;
    }

    public static Uri getAlbumArtUri(long albumId){
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }


    public static Bitmap fetchFullArt(Song song){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(song.location);
            byte[] stream = retriever.getEmbeddedPicture();
            if (stream != null)
                return BitmapFactory.decodeByteArray(stream, 0, stream.length);
            else
                return BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.canv2);
        }
        catch (Exception e) {
            e.printStackTrace();
        }catch (OutOfMemoryError e){
            Crashlytics.logException(e);
            return null;

        }
        return null;
    }

    public static Bitmap getAlbumArt(Context context, long albumId){
        Bitmap albumArt = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try{

            Uri uri = getAlbumArtUri(albumId);
            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if(pfd != null){
                FileDescriptor fd = pfd.getFileDescriptor();
                albumArt = BitmapFactory.decodeFileDescriptor(fd, null, options);
                pfd.close();
                pfd = null;
                fd=null;
            }

        }
        catch (Exception e){
        }catch (OutOfMemoryError e){
            Crashlytics.logException(e);
        }
        return albumArt;
    }
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {
        try {

            final float densityMultiplier = context.getResources().getDisplayMetrics().density;

            int h = (int) (newHeight * densityMultiplier);
            int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

            photo = Bitmap.createScaledBitmap(photo, w, h, true);
        }catch (Exception e){

        }catch (OutOfMemoryError e){

        }

        return photo;
    }

    public void colourizeView (Context context, Bitmap bitmap, long albumId, View view){

    }

    public static int hashLong(long value) {
        return (int) (value ^ (value >>> 32));
    }

    public static int getNumberOfGridColumns(Context context){
        // Calculate the number of columns that can fit on the screen
        final short screenWidth = (short) context.getResources().getConfiguration().screenWidthDp;
        final float density = context.getResources().getDisplayMetrics().density;
        final short globalPadding = (short) (context.getResources().getDimension(R.dimen.global_padding) / density);
        final short minWidth = (short) (context.getResources().getDimension(R.dimen.grid_width) / density);
        final short gridPadding = (short) (context.getResources().getDimension(R.dimen.grid_margin) / density);

        short availableWidth = (short) (screenWidth - 2 * globalPadding);
        return (availableWidth) / (minWidth + 2 * gridPadding);
    }

    public static boolean isPackageInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        boolean res = false;
        try {
            Intent intent = packageManager.getLaunchIntentForPackage(FB_PKG);
            if (intent == null) {
                res = false;
            }else {
                List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                res = (list.size() > 0);
            }

            if(!res){
                intent = packageManager.getLaunchIntentForPackage(MSG_PKG);
                if (intent == null) {
                    res = false;
                }else {
                    List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    res = (list.size() > 0);
                }
            }
            return res;
        }catch (Exception e){
            return false;
        }
    }

    private void colorWindowsCollapsed(Activity context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Window window = context.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Themes.getPrimaryDark());
                if (Prefs.colourNB(context)) {
                    window.setNavigationBarColor(Themes.getPrimaryDark());
                }
            }catch (Exception e){

            }

        }

    }

    public static void showAd(String id, Context context){
        final InterstitialAd ad = new InterstitialAd(context);
        ad.setAdUnitId(id);
        AdRequest adRequest = new  AdRequest.Builder()
                .addTestDevice(Library.TEST_DEVICE_ID)
                .addTestDevice("FB7279A941789FFAB8C98749C0DB4D78")
                .build();
        ad.loadAd(adRequest);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdLoaded() {
                ad.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }
        });

    }


}
