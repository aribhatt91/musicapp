package com.optimus.music.player.onix.views.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.audiofx.Equalizer;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class Prefs {

    public static final String VIRTUALIZER_STRENGTH = "virstren";
    public static final String BASS_STRENGTH = "bassstren";
    public static final String COLOURNAVBAR = "colourNavBar";
    public static final String SYSEQ = "sysEq";
    public static final String COLOURGRID = "colourAlbum";

    public static final String TINTSTATUS = "tintStat";

    public static final String SHOW_ALL = "showAll";

    public static final String HIDE_SMALL = "hideSmall";

    public static final String TAP = "tap";

    public static final String TRIM = "trimMem";
    public static final String CLEAR = "clearCache";

    public static final String ALBUMSIZE = "albumSize";
    public static final String DISP_GENRE = "displayGenre";
    public static final String SORTFOLDERS = "sortFolder";


    // Preference keys
    public static final String SHOW_FAB = "prefShowFab";

    public static final String ANIM = "animOff";

    public static final String SORTSONGS = "sortSongs";

    public static final String HEADPLAY = "headPlay";

    public static final String SORTALBUMS = "sortAlbums";

    public static final String LAST_PAGE = "lastPage";

    public static final String DISP_MSG = "dispMsg";

    public static final String EX_FOL = "excludeFolders";

    public static final String DBL_TAP = "doubleTap";
    /**
     * Whether or not to preform first start actions. Default value is true
     */
    public static final String SHOW_FIRST_START = "prefShowFirstStart";
    /**
     * Whether or not to allow usage logging to Crashlytics. Crash logging may not be disabled
     * because of its importance in developing Jockey.
     */
    public static final String ALLOW_LOGGING = "prefAllowLogging";
    /**
     * Which page to show by default when opening Jockey. Must be a numeric value that corresponds
     * to a value given by
     *
     */
    public static final String DEFAULT_PAGE = "prefDefaultPage";
    /**
     * What accent color Jockey should use. 0 for black, 1 for red, 2 for orange, 3 for yellow,
     * 4 for green, 5 (default) for blue, and 6 for purple
     */
    public static final String PRIMARY_COLOR = "prefColorPrimary";
    /**
     * Whether Jockey should use a light or dark theme. 1 for light, 0 for dark
     */
    public static final String BASE_COLOR = "prefBaseTheme";
    /**
     * A temporary preference used for adding color-coordinated shortcuts to the launcher
     */
    public static final String ADD_SHORTCUT = "prefAddShortcut";
    /**
     * Whether or not Jockey can use mobile data to retrieve information from Last.Fm. This flag
     * doesn't affect Crashlytics currently
     */
    public static final String USE_MOBILE_NET = "prefUseMobileData";
    /**
     * Whether or not to navigate to the Now Playing Activity when the user picks a new song
     */
    public static final String SWITCH_TO_PLAYING = "prefSwitchToNowPlaying";
    /**
     * An equalizer preset defined by the system that the user has selected as indexed by
     * {@link Equalizer#getPresetName(short)} and {@link Equalizer#usePreset(short)}.
     * -1 is saved to specify a custom equalizer configuration
     */
    public static final String EQ_PRESET_ID = "equalizerPresetId";
    /**
     * Whether or not to enable the equalizer
     */
    public static final String EQ_ENABLED = "prefUseEqualizer";
    /**
     * All {@link Equalizer} settings written and parsed by
     * {@link Equalizer.Settings}
     */
    public static final String EQ_SETTINGS = "prefEqualizerSettings";

    public static final String SLEEPTIMER = "sleepTimer";



    /**
     * Shorthand to get the default {@link SharedPreferences}. Equivalent to calling
     * {@link PreferenceManager#getDefaultSharedPreferences(Context)}
     * @param context A {@link Context} used to open the preferences
     * @return The default {@link SharedPreferences}
     */
    public static SharedPreferences getPrefs(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static int getSongSortOrder (Context context){
        return (Integer.parseInt(Prefs.getPrefs(context).getString(SORTSONGS, "0")));
    }

    public static int getFolderSortOrder (Context context){
        return (Integer.parseInt(Prefs.getPrefs(context).getString(SORTFOLDERS, "0")));
    }

    public static int getAlbumSortOrder (Context context){
        return (Integer.parseInt(Prefs.getPrefs(context).getString(SORTALBUMS, "0")));
    }

    public static boolean showFab(Context context){
        return (Prefs.getPrefs(context).getBoolean(Prefs.SHOW_FAB, true));
    }

    public static boolean animOff(Context context){
        return (Prefs.getPrefs(context).getBoolean(Prefs.ANIM, false));
    }

    public static boolean colourNB(Context context){
        return (Prefs.getPrefs(context).getBoolean(Prefs.COLOURNAVBAR, true));
    }

    public static boolean colourSB(Context context){
        return (Prefs.getPrefs(context).getBoolean(Prefs.TINTSTATUS, true));
    }
    public static boolean colourAlbum(Context context){
        return (Prefs.getPrefs(context).getBoolean(Prefs.COLOURGRID, false));
    }

    public static boolean useSysEq(Context context){
        return (Prefs.getPrefs(context).getBoolean(Prefs.SYSEQ, true));
    }

    public static boolean disPlayMessage(Context context){
        return (Prefs.getPrefs(context).getBoolean(Prefs.DISP_MSG, true));
    }

    public static void setMsgVar(Context context, boolean val){
        try{
            SharedPreferences prefs = Prefs.getPrefs(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Prefs.DISP_MSG, val);
            editor.apply();
        }catch (Exception e){

        }
    }



    public static boolean headsetPlay(Context context){
        return false;
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //return (prefs.getBoolean(Prefs.HEADPLAY, false));
    }


    public static void setCurrPage(Context context, int pos){
        if(pos>=0 && pos<=5){
            try {
                SharedPreferences prefs = Prefs.getPrefs(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(Prefs.LAST_PAGE, pos);
                editor.apply();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static int getLastPage(Context context){
        return Prefs.getPrefs(context).getInt(Prefs.LAST_PAGE, 2);
    }

    public static int getAlbumsize(Context context){
        return Integer.parseInt(Prefs.getPrefs(context).getString(ALBUMSIZE, "0"));
    }

    public static int getGenreStyle(Context context){
        return Integer.parseInt(Prefs.getPrefs(context).getString(DISP_GENRE, "1"));
    }

    public static Set<String> getExcludedFolders(Context context){
        return Prefs.getPrefs(context).getStringSet(Prefs.EX_FOL, new HashSet<String>());
    }

    public static int getDoubleTap (Context context){
        return (Integer.parseInt(Prefs.getPrefs(context).getString(DBL_TAP, "0")));
    }

    public static void setSleeptimer(SharedPreferences preferences, long time){
        try{
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(SLEEPTIMER, time);
            editor.apply();

        }catch (Exception e){
            e.printStackTrace();

        }

    }

    /**
     * Verify that network use is allowed right now. Takes into account the user's current network
     * type and whether or not they have disabled data over mobile networks.
     * @param context A {@link Context} used to query the current network configuration
     * @return Whether Jockey is permitted to use the network right now
     */


    public static boolean allowNetwork(Context context){
        ConnectivityManager network =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return network.getActiveNetworkInfo() != null && network.getActiveNetworkInfo().isAvailable()
                && !network.getActiveNetworkInfo().isRoaming()
                && (Prefs.getPrefs(context).getBoolean(Prefs.USE_MOBILE_NET, true)
                || network.getActiveNetworkInfo().getType() != ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Verify that the user has allowed additional logging to occur. This doesn't affect
     * Crashlytics crashes and caught exception logging
     * @param context A {@link Context} used to verify this preference
     * @return Whether {@link Prefs#ALLOW_LOGGING} is true in the default {@link SharedPreferences}
     */
    public static boolean allowAnalytics(Context context){
        return getPrefs(context).getBoolean(ALLOW_LOGGING, false);
    }

    public static boolean hideSmall(Context context){
        return getPrefs(context).getBoolean(HIDE_SMALL, false);
    }

    public static boolean showAll(Context context){
        return getPrefs(context).getBoolean(SHOW_ALL, false);
    }

    public static int tap(Context context){
        return getPrefs(context).getInt(TAP, 0);
    }


}


