package com.optimus.music.player.onix.SplashScreen;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.optimus.music.player.onix.LibraryActivity;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.SettingsActivity.Themes;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Themes.setTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        int id = Themes.getThemeId(this);
        View back = findViewById(R.id.background);
        ImageView head = (ImageView) findViewById(R.id.onix_head);
        ImageView logo = (ImageView) findViewById(R.id.logo);

        if(id == 0){
            try {
                int accent = ContextCompat.getColor(this, R.color.metalYellow);
                int bg = ContextCompat.getColor(this, R.color.metalDark);
                head.setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
                logo.setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
                back.setBackgroundColor(bg);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        else if(id==6){
            try {
                int accent = ContextCompat.getColor(this, R.color.passionAccent);
                int bg = ContextCompat.getColor(this, R.color.passionBackground);
                head.setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
                logo.setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
                back.setBackgroundColor(bg);
            }catch (Exception e){
                e.printStackTrace();
            }


        }else if(id==7){
            try {
                int accent = ContextCompat.getColor(this, R.color.midnightAccent);
                head.setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
                logo.setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(id==10){
            try {
                int accent = ContextCompat.getColor(this, R.color.partyAccent);
                head.setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
                logo.setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreenActivity.this, LibraryActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
