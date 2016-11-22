package com.optimus.music.player.onix.EqualizerActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;

import android.media.audiofx.Virtualizer;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import android.widget.LinearLayout;

import com.optimus.music.player.onix.SettingsActivity.Themes;


public class EqualizerActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    private Equalizer equalizer;
    private int bandCount, presets;
    //private ViewPager viewPager;
    private LinearLayout eqcontainer;
    private SwitchCompat equalizerToggle;
    private Spinner presetSpinner;
    private EqualizerFrame[] sliders;
    private LayoutInflater inflater;
    //private BassBoost bassBoost;
    //private Virtualizer virtual;
    private int VIRT_STRENGTH , BASS_STRENGTH;
    View rect;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Themes.setTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eq);
        //presetSpinnerPrefix = (TextView) findViewById(R.id.equalizerPresetPrefix);
        presetSpinner = (Spinner) findViewById(R.id.equalizerPresetSpinner);

        SharedPreferences prefs = Prefs.getPrefs(this);

        VIRT_STRENGTH = prefs.getInt(Prefs.VIRTUALIZER_STRENGTH, 0);
        BASS_STRENGTH = prefs.getInt(Prefs.BASS_STRENGTH, 0);






        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Window window = getWindow();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if(!Prefs.colourSB(this)) {
                window.setStatusBarColor(Themes.getBlack());
            }
            if (Prefs.colourNB(this)) {
                window.setNavigationBarColor(Themes.getPrimary());
            }
        }



        rect = findViewById(R.id.equalizerPresetFrame);

        if(rect!=null){
            //rect.setBackgroundColor(Themes.getPrimary());
            //rect.setBackgroundTintList(ColorStateList.valueOf(Themes.getPrimary()));
        }



            if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            equalizerToggle = new SwitchCompat(this);
            equalizerToggle.setOnCheckedChangeListener(this);

            //equalizerToggle.setCompoundDrawableTintList(ColorStateList.valueOf(0x01060012));

            Toolbar.LayoutParams params = new Toolbar.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    Gravity.END);
            int padding = (int) (16 * getResources().getDisplayMetrics().density);
            params.setMargins(padding, 0, padding, 0);

            toolbar.addView(equalizerToggle, params);
        }
        int audioSession = PlayerController.getAudioSessionId();

        inflater = LayoutInflater.from(this);

        LinearLayout equalizerPanel = (LinearLayout) findViewById(R.id.eqpanel);





        if (audioSession > 0) {

            equalizer = new Equalizer(0, PlayerController.getAudioSessionId());
            bandCount = equalizer.getNumberOfBands();
            presets = equalizer.getNumberOfPresets();



            //bassBoost.setEnabled(true);
            //virtual.setEnabled(true);

            /*

            if(bassBoost.getStrengthSupported()){
                short basst = (short) BASS_STRENGTH;
                try {
                    bassBoost.setStrength(basst);
                    bass.setProgress(bassBoost.getRoundedStrength() / 10);
                    bassstr.setText(String.valueOf(bassBoost.getRoundedStrength() / 10));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if(virtual.getStrengthSupported()){
                short virt = (short) VIRT_STRENGTH;
                try {
                    virtual.setStrength(virt);
                    virtualizer.setProgress(virtual.getRoundedStrength() / 10);
                    virstr.setText(String.valueOf(virtual.getRoundedStrength() / 10));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            */

            //bassBoost.getStrengthSupported();


            sliders = new EqualizerFrame[bandCount];

            PresetAdapter presetAdapter = new PresetAdapter(this, equalizer, sliders);
            presetSpinner.setAdapter(presetAdapter);
            presetSpinner.setSelection(prefs.getInt(Prefs.EQ_PRESET_ID, -1) + 1);
            presetSpinner.setOnItemSelectedListener(presetAdapter);

            for (short i = 0; i < bandCount; i++) {
                inflater.inflate(R.layout.eqbar, equalizerPanel, true);
                sliders[i] = new EqualizerFrame(equalizerPanel.getChildAt(i), equalizer,
                        i, presetSpinner);
            }

            setEqualizerEnabled(prefs.getBoolean(Prefs.EQ_ENABLED, false));
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Equalizer");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    private void setEqualizerEnabled(boolean enabled) {
        if (equalizerToggle.isChecked() != enabled) {
            equalizerToggle.setChecked(enabled);
        }
        try {
            equalizer.setEnabled(enabled);
        }catch (Exception e){

        }
        //presetSpinnerPrefix.setEnabled(enabled);
        presetSpinner.setEnabled(enabled);
        for (EqualizerFrame f : sliders) {
            f.update(enabled);
        }

        // Bind or unbind from the system equalizer as needed
        if (!enabled) {
            final Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
            intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, PlayerController.getAudioSessionId());
            intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, this.getPackageName());
            this.sendBroadcast(intent);
        } else {
            final Intent intent = new Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
            intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, PlayerController.getAudioSessionId());
            intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, this.getPackageName());
            this.sendBroadcast(intent);
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setEqualizerEnabled(isChecked);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.removeView(equalizerToggle);
        }*/

        if (equalizer != null /*&& bassBoost!=null && virtual!=null*/) {
            try {
                Prefs.getPrefs(this).edit()
                        .putString(Prefs.EQ_SETTINGS, equalizer.getProperties().toString())
                        .putBoolean(Prefs.EQ_ENABLED, equalizerToggle.isChecked())
                        .putInt(Prefs.EQ_PRESET_ID, (int) presetSpinner.getSelectedItemId())
                        //.putInt(Prefs.VIRTUALIZER_STRENGTH, VIRT_STRENGTH)
                        //.putInt(Prefs.BASS_STRENGTH, BASS_STRENGTH)
                        .apply();

                equalizer.release();
                //bassBoost.release();
                //virtual.release();
            }catch (Exception e){

            }
        }

    }

    private static class PresetAdapter extends BaseAdapter
            implements AdapterView.OnItemSelectedListener {

        private Context context;
        private Equalizer equalizer;
        private String[] presets;
        private EqualizerFrame[] sliders;

        PresetAdapter(Context context, Equalizer equalizer, EqualizerFrame[] sliders) {
            this.context = context;
            this.equalizer = equalizer;
            this.sliders = sliders;

            presets = new String[equalizer.getNumberOfPresets() + 1];
            presets[0] = "Custom"; // TODO String resource

            for (short i = 0; i < presets.length - 1; i++) {
                presets[i + 1] = equalizer.getPresetName(i);
            }
        }

        @Override
        public int getCount() {
            return presets.length;
        }

        @Override
        public Object getItem(int position) {
            return presets[position];
        }

        @Override
        public long getItemId(int position) {
            return position - 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater
                        .from(context)
                        .inflate(android.R.layout.simple_spinner_item, parent, false);
            }

            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
            textView.setText(presets[position]);

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater
                        .from(context)
                        .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            }

            return getView(position, convertView, parent);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (id != -1) {
                try {
                    equalizer.usePreset((short) id);

                }catch (Exception e){
                    Crashlytics.log(e.getMessage());
                }

                for (short i = 0; i < sliders.length; i++) {
                    try {
                        sliders[i].update(equalizer.getBandLevel(i));
                    }catch (Exception e){
                        Crashlytics.log(e.getMessage());
                    }
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    public static class EqualizerFrame implements SeekBar.OnSeekBarChangeListener {

        final Equalizer equalizer;
        final short bandNumber;
        final SeekBar bandSlider;
        final TextView bandLabel, current;
        final Spinner presetSpinner;

        final short minLevel;
        final short maxLevel;

        public EqualizerFrame(View root, Equalizer eq, short bandNumber, Spinner presetSpinner) {
            this.equalizer = eq;
            this.bandNumber = bandNumber;
            this.presetSpinner = presetSpinner;

            bandSlider = (SeekBar) root.findViewById(R.id.pbar);
            bandLabel = (TextView) root.findViewById(R.id.pname);
            current = (TextView) root.findViewById(R.id.currlevel);



            int frequency = eq.getCenterFreq(bandNumber) / 1000;

            if (frequency > 1000) {
                bandLabel.setText(frequency / 1000 + " Khz");
            } else {
                bandLabel.setText(Integer.toString(frequency) + " hz");
            }

            short[] range = eq.getBandLevelRange();
            minLevel = range[0];
            maxLevel = range[1];

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                try {
                    LayerDrawable progressDrawable = (LayerDrawable) bandSlider.getProgressDrawable();
                    progressDrawable.findDrawableByLayerId(android.R.id.background).setColorFilter(
                            0x05000000,
                            PorterDuff.Mode.SRC_ATOP);
                    progressDrawable.findDrawableByLayerId(android.R.id.progress).setColorFilter(
                            0xffffffff, PorterDuff.Mode.SRC_ATOP);

                    progressDrawable.findDrawableByLayerId(android.R.id.progress).setColorFilter(
                            Themes.getAccent(), PorterDuff.Mode.SRC_ATOP);
                    Drawable thumb = (Drawable) bandSlider.getThumb();
                    thumb.setColorFilter(0xffffffff, PorterDuff.Mode.SRC_ATOP);
                    thumb.setColorFilter(Themes.getAccent(), PorterDuff.Mode.SRC_ATOP);
                }catch (Exception e){
                    Crashlytics.log(e.getMessage());

                }
            }
            else{
                try {
                    bandSlider.setProgressTintList(ColorStateList.valueOf(Themes.getAccent()));
                    bandSlider.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
                    bandSlider.setThumbTintList(ColorStateList.valueOf(Themes.getAccent()));
                    bandSlider.setThumbTintMode(PorterDuff.Mode.SRC_ATOP);
                }catch (Exception e){
                    Crashlytics.log(e.getMessage());
                }

            }

            bandSlider.setMax(Math.abs(minLevel) + maxLevel);
            bandSlider.setProgress(eq.getBandLevel(bandNumber) + Math.abs(range[0]));
            bandSlider.setOnSeekBarChangeListener(this);
            int level = (bandSlider.getProgress() - Math.abs(minLevel))/100;
            String curr = level + "db";
            current.setText(curr);
        }

        public void update(boolean enabled) {
            bandSlider.setEnabled(enabled);
            bandLabel.setEnabled(enabled);
            current.setEnabled(enabled);
        }

        public void update(int level) {
            bandSlider.setProgress(level + Math.abs(minLevel));
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            try {
                equalizer.setBandLevel(bandNumber, (short) (progress - Math.abs(minLevel)));
                int l = (progress - Math.abs(minLevel))/100;
                String curr = l + "db";
                current.setText(curr);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Disable any preset
            presetSpinner.setSelection(0);
        }
    }

}
