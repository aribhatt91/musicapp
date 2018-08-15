package com.optimus.music.player.onix;

import android.app.Application;
import android.os.StrictMode;

import com.bumptech.glide.Glide;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by apricot on 29/12/15.
 */
public class OnixApplication extends Application{

    @Override
    public void onCreate() {
        setupStrictMode();
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        PlayerController.startService(getApplicationContext());
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.with(this).onTrimMemory(level);
    }

    private void setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
    }
}
