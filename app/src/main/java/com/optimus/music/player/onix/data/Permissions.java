package com.optimus.music.player.onix.data;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class Permissions {
    public static final int PERMISSION_REQUEST_ID = 0x01;
    //
    //          PERMISSION METHODS
    //

    @TargetApi(23)
    public static boolean hasRWPermission(Context context){
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED
                        && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void requestRWPermission(Activity activity) {
        activity.requestPermissions(
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },
                PERMISSION_REQUEST_ID);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean previouslyRequestedRWPermission(Activity activity) {
        return
                activity.shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        || activity.shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @TargetApi(23)
    public static boolean hasWriteSettingsPermission(Context context){
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                context.checkSelfPermission(Manifest.permission.WRITE_SETTINGS)
                        == PackageManager.PERMISSION_GRANTED;
    }





    @TargetApi(Build.VERSION_CODES.M)
    public static void requestWriteSettingsPermission(Activity activity) {
        activity.requestPermissions(
                new String[]{
                        Manifest.permission.WRITE_SETTINGS
                },
                PERMISSION_REQUEST_ID);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean previouslyRequestedWriteSettingsPermission(Activity activity) {
        return
                activity.shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_SETTINGS);
    }
}
