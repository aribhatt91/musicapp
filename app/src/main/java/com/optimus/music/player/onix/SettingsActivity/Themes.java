package com.optimus.music.player.onix.SettingsActivity;

/**
 * Created by apricot on 24/12/15.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.widget.Button;

import com.optimus.music.player.onix.LibraryActivity;
import com.optimus.music.player.onix.R;

public class Themes {

    private static int primary;
    private static int primaryDark;
    private static int accent;
    private static int primaryRes;

    private static int background;
    private static int black;
    private static int backgroundElevated;
    private static int backgroundMiniplayer;

    public static int getWhite(Context context){
        return ContextCompat.getColor(context, R.color.darkTextPrimary);
    }

    public static int getTransparent(Context context){
        return ContextCompat.getColor(context, R.color.transparent);
    }



    // Update method
    @SuppressWarnings("deprecation")
    public static void updateColors(Context context) {
        SharedPreferences prefs = Prefs.getPrefs(context);
        Resources resources = context.getResources();
        black = resources.getColor(R.color.black);

        switch (Integer.parseInt(prefs.getString(Prefs.PRIMARY_COLOR, "5"))) {
            case 0: //Black
                primary = resources.getColor(R.color.metalDark);
                primaryDark = resources.getColor(R.color.metalDark);
                accent = resources.getColor(R.color.metalYellow);
                primaryRes = R.color.metalDark;
                break;
            case 1: //fire
                primary = resources.getColor(R.color.fireLight);
                primaryDark = resources.getColor(R.color.fireDark);
                accent = resources.getColor(R.color.fireLight);
                primaryRes = R.color.fireLight;
                break;
            case 2: //earth
                primary = resources.getColor(R.color.earthLight);
                primaryDark = resources.getColor(R.color.earthDark);
                accent = resources.getColor(R.color.earthLight);
                primaryRes = R.color.earthLight;
                break;
            case 3: //marine
                primary = resources.getColor(R.color.aquaLight);
                primaryDark = resources.getColor(R.color.aquaDark);
                accent = resources.getColor(R.color.aquaLight);
                primaryRes = R.color.aquaLight;
                break;
            case 4: //ether
                primary = resources.getColor(R.color.etherLight);
                primaryDark = resources.getColor(R.color.etherDark);
                accent = resources.getColor(R.color.etherLight);
                primaryRes = R.color.etherLight;
                break;
            case 5: //life
                primary = resources.getColor(R.color.lifeLight);
                primaryDark = resources.getColor(R.color.lifeDark);
                accent = resources.getColor(R.color.lifeLight);
                primaryRes = R.color.lifeLight;
                break;
            case 6: //passion
                primary = resources.getColor(R.color.passionLight);
                primaryDark = resources.getColor(R.color.passionDark);
                accent = resources.getColor(R.color.passionAccent);
                primaryRes = R.color.passionLight;
                break;
            case 7: //midnight
                primary = resources.getColor(R.color.midnightLight);
                primaryDark = resources.getColor(R.color.midnightDark);
                accent = resources.getColor(R.color.midnightAccent);
                primaryRes = R.color.midnightLight;
                break;
            case 8: //marine
                primary = resources.getColor(R.color.marineLight);
                primaryDark = resources.getColor(R.color.marineDark);
                accent = resources.getColor(R.color.marineLight);
                primaryRes = R.color.marineLight;
                break;
            case 9: //radiance
                primary = resources.getColor(R.color.radLight);
                primaryDark = resources.getColor(R.color.radDark);
                accent = resources.getColor(R.color.radAccent);
                primaryRes = R.color.radLight;
                break;
            case 10: //radiance
                primary = resources.getColor(R.color.partyPrimary);
                primaryDark = resources.getColor(R.color.partyPrimaryDark);
                accent = resources.getColor(R.color.partyAccent);
                primaryRes = R.color.partyPrimary;
                break;
            case 11: //indigo
                primary = resources.getColor(R.color.indiLight);
                primaryDark = resources.getColor(R.color.indiDark);
                accent = resources.getColor(R.color.indiAccent);
                primaryRes = R.color.indiLight;
                break;
            default: //Blue & Unknown
                primary = resources.getColor(R.color.metalDark);
                primaryDark = resources.getColor(R.color.metalDark);
                accent = resources.getColor(R.color.metalYellow);
                primaryRes = R.color.metalDark;
                break;
        }

        switch (Integer.parseInt(prefs.getString("prefBaseTheme", "1"))) {
            case 0: // Material Light
                background = resources.getColor(R.color.lightBackground);
                backgroundElevated = resources.getColor(R.color.lightBackground);
                backgroundMiniplayer = resources.getColor(R.color.lightBackground);
                break;
            case 6: // Material Light
                background = resources.getColor(R.color.lightBackground);
                backgroundElevated = resources.getColor(R.color.lightBackground);
                backgroundMiniplayer = resources.getColor(R.color.lightBackground);
                break;
            default: // Material Dark
                background = resources.getColor(R.color.darkBackground);
                backgroundElevated = resources.getColor(R.color.darkBackground);
                backgroundMiniplayer = resources.getColor(R.color.darkBackground);
                break;
        }
    }

    public static int getThemeId(Context context){
        SharedPreferences prefs = Prefs.getPrefs(context);
        //int base = Integer.parseInt(prefs.getString(Prefs.BASE_COLOR, "1"));
        int primary = Integer.parseInt(prefs.getString(Prefs.PRIMARY_COLOR, "0"));
        return primary;
    }

    public static int getBackground(Context context){
        SharedPreferences prefs = Prefs.getPrefs(context);
        //int base = Integer.parseInt(prefs.getString(Prefs.BASE_COLOR, "1"));
        int primary = Integer.parseInt(prefs.getString(Prefs.PRIMARY_COLOR, "0"));
        if(primary==0 ){
            return ContextCompat.getColor(context, R.color.metalList);
        }
        else if(primary==6){
            return ContextCompat.getColor(context, R.color.passionForeground);
        }
        else if(primary==7){
            return ContextCompat.getColor(context, R.color.midnightForeground);

        }else if(primary==10){
            return ContextCompat.getColor(context, R.color.partyForeground);

        }
        else{
            return ContextCompat.getColor(context, R.color.darkForeground);
        }
    }

    public static int getButtonTint(Context context){
        SharedPreferences prefs = Prefs.getPrefs(context);
        //int base = Integer.parseInt(prefs.getString(Prefs.BASE_COLOR, "1"));
        int primary = Integer.parseInt(prefs.getString(Prefs.PRIMARY_COLOR, "0"));
        if(primary==0 || primary==6 || primary==7 || primary==10){
            return ContextCompat.getColor(context, R.color.darkTextPrimary);
        }
        else{
            return ContextCompat.getColor(context, R.color.lightTextPrimary);
        }
    }

    public static @StyleRes int getTheme(Context context) {
        SharedPreferences prefs = Prefs.getPrefs(context);
        //int base = Integer.parseInt(prefs.getString(Prefs.BASE_COLOR, "1"));
        int primary = Integer.parseInt(prefs.getString(Prefs.PRIMARY_COLOR, "0"));

        //if (base == 1) {
            // Light Base
            switch (primary) {
                case 0: // Black
                    return R.style.AppThemeBase;
                case 1: // orange
                    return R.style.AppThemeBaseLight_Fire;
                case 2: // green
                    return R.style.AppThemeBaseLight_Earth;
                case 3: // blue
                    return R.style.AppThemeBaseLight_Marine;
                case 4: // purple
                    return R.style.AppThemeBaseLight_Ether;
                case 5: // pink
                    return R.style.AppThemeBaseLight_Life;
                case 6:
                    return R.style.AppThemeBase_Passion;
                case 7:
                    return R.style.AppThemeBase_Midnight;
                case 8:
                    return R.style.AppThemeBaseLight_Ocean;
                case 9:
                    return R.style.AppThemeBaseLight_Rad;
                case 10:
                    return R.style.AppThemeBase_Party;
                case 11:
                    return R.style.AppThemeBaseLight_Indigo;

                default:
                    return R.style.AppThemeBase;

            }
        //} else {
            // Dark or Unknown Base
          //  return R.style.AppThemeWhite;

       // }
    }



    // Get Methods
    public static int getPrimary() {
        return primary;
    }

    public static int getPrimaryRes(){
        return primaryRes;
    }

    public static int getPrimaryDark() {
        return primaryDark;
    }

    public static int getAccent() {
        return accent;
    }

    public static int getBlack(){
        return black;
    }

    public static int getBackground() {
        return background;
    }

    public static int getBackgroundElevated(){
        return backgroundElevated;
    }

    public static int getBackgroundMiniplayer() {
        return backgroundMiniplayer;
    }

    @SuppressWarnings("deprecation")
    public static boolean isLight(Context context) {
        return background == context.getResources().getColor(R.color.lightBackground);
    }

    @SuppressWarnings("deprecation")
    public static void setTheme(Activity activity) {
        updateColors(activity);
        activity.setTheme(getTheme(activity));

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(activity.getResources().getString(R.string.app_name), getIcon(activity), primary);
            activity.setTaskDescription(taskDescription);
        } else {
            if (activity.getActionBar() != null) {
                activity.getActionBar().setBackgroundDrawable(new ColorDrawable(primary));
                if (!activity.getClass().equals(LibraryActivity.class)) {
                    activity.getActionBar().setIcon(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));
                } else {
                    activity.getActionBar().setIcon(getIconId(activity));
                }
            }
        }*/
    }

    public static Bitmap getIcon(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), getIconId(context));
    }

    public static Bitmap getLargeIcon(Context context, int density) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

            // Use a density 1 level higher than the display in case the launcher uses large icons
            if (density <= 0) {
                switch (context.getResources().getDisplayMetrics().densityDpi) {
                    case DisplayMetrics.DENSITY_LOW:
                        density = DisplayMetrics.DENSITY_MEDIUM;
                        break;
                    case DisplayMetrics.DENSITY_MEDIUM:
                        density = DisplayMetrics.DENSITY_HIGH;
                        break;
                    case DisplayMetrics.DENSITY_HIGH:
                        density = DisplayMetrics.DENSITY_XHIGH;
                        break;
                    case DisplayMetrics.DENSITY_XHIGH:
                        density = DisplayMetrics.DENSITY_XXHIGH;
                        break;
                    default:
                        density = DisplayMetrics.DENSITY_XXXHIGH;
                }
            }

            @SuppressWarnings("deprecation")
            BitmapDrawable icon = (BitmapDrawable) context.getResources().getDrawableForDensity(getIconId(context), density);
            if (icon != null) return icon.getBitmap();

        }
        return getIcon(context);
    }

    public static @DrawableRes int getIconId(Context context) {
        switch (Integer.parseInt(Prefs.getPrefs(context).getString(Prefs.PRIMARY_COLOR, "5"))) {
           /* case 0:
                return R.drawable.ic_launcher_grey;
            case 1:
                return R.drawable.ic_launcher_red;
            case 2:
                return R.drawable.ic_launcher_orange;
            case 3:
                return R.drawable.ic_launcher_yellow;
            case 4:
                return R.drawable.ic_launcher_green;
            case 6:
                return R.drawable.ic_launcher_purple;*/
            default:
                return R.mipmap.onix_launcher;
        }
    }

    public static void themeAlertDialog(AlertDialog dialog){
        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

        if (positive != null) positive.setTextColor(getAccent());
        if (negative != null) negative.setTextColor(getAccent());
        if (neutral != null) neutral.setTextColor(getAccent());
    }



    public static void updateLauncherIcon(Context context) {
        Intent launcherIntent = new Intent(context, LibraryActivity.class);

        // Uncomment to delete Jockey icons from the launcher
        // Don't forget to add permission "com.android.launcher.permission.UNINSTALL_SHORTCUT" to AndroidManifest
        /*
        Intent delIntent = new Intent();
        delIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
        delIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getResources().getString(R.string.app_name));
        delIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        context.sendBroadcast(delIntent);
        */

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getResources().getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, getLargeIcon(context, -1));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);
    }


}
