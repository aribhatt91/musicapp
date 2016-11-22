package com.optimus.music.player.onix.JukeBoxActivity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.SettingsActivity.Themes;

public class FavouritesActivity extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Themes.setTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_square);
        fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }
}
