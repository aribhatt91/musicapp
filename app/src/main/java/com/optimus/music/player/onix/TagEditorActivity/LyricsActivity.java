package com.optimus.music.player.onix.TagEditorActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.Common.Lyrics;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.Themes;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.id3.ID3v23Tag;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

public class LyricsActivity extends AppCompatActivity {
    private Song reference;
    public static String TAGGER_EXTRA = "song";
    EditText editText;
    FloatingActionButton fab;
    private final String searchQuery = "https://www.google.com/search?q=";
    private final String EMPTY = "Failed to download lyrics!";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Themes.setTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Lyrics");
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Prefs.colourNB(this)){
            getWindow().setNavigationBarColor(Themes.getPrimary());
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !Prefs.colourSB(this)) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Themes.getBlack());
        }


        reference = getIntent().getParcelableExtra(TAGGER_EXTRA);
        editText = (EditText) findViewById(R.id.edit);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        if(reference!=null) {
            try {
                final File test = new File(reference.location);
                if(test.exists()) {
                    final AudioFile f = AudioFileIO.read(test);
                    TagOptionSingleton.getInstance().setAndroid(true);

                    //if(reference.location.endsWith(".mp3") || reference.location.endsWith(".MP3"))
                        //f.setTag(new ID3v23Tag());


                    final Tag newTag = f.getTagOrCreateAndSetDefault();
                    final String lyrics = newTag.getFirst(FieldKey.LYRICS);

                    editText.setText(lyrics, TextView.BufferType.EDITABLE);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                String edited = editText.getText().toString();
                                if (!edited.isEmpty()) {
                                    newTag.setField(FieldKey.LYRICS, edited);
                                    f.setTag(newTag);
                                }
                                AudioFileIO.write(f);

                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                intent.setData(Uri.fromFile(test));
                                sendBroadcast(intent);
                                if(System.currentTimeMillis()%5==0) {
                                    Util.showAd(getResources().getString(R.string.artist_inter), LyricsActivity.this);
                                }

                                AlertDialog dialog = new AlertDialog.Builder(LyricsActivity.this)
                                        .setTitle("Changes saved..")
                                        .setMessage("Do you want to exit?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                                //Navigate.home(LyricsActivity.this);
                                            }
                                        })
                                        .setNegativeButton("NO", null)
                                        .show();
                                Themes.themeAlertDialog(dialog);

                            } catch (Exception e) {
                                Crashlytics.logException(e);
                                AlertDialog dialog = new AlertDialog.Builder(LyricsActivity.this)
                                        .setTitle("Changes not saved..")
                                        .setMessage("Do you want to exit?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                                //Navigate.home(LyricsActivity.this);
                                            }
                                        })
                                        .setNegativeButton("NO", null)
                                        .show();
                                Themes.themeAlertDialog(dialog);
                            }
                        }
                    });
                }


            } catch (Exception e) {
                Crashlytics.logException(e);
                AlertDialog dialog = new AlertDialog.Builder(LyricsActivity.this)
                        .setTitle("Changes not saved..")
                        .setMessage("Do you want to exit?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                //Navigate.home(LyricsActivity.this);
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();
                Themes.themeAlertDialog(dialog);
            }catch (NoClassDefFoundError e){
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    AlertDialog dialog = new AlertDialog.Builder(LyricsActivity.this)
                            .setTitle("Incompatible Platform")
                            .setMessage("It seems this function is not compatible with your phone.")
                            .setNegativeButton("OK", null)
                            .show();
                    Themes.themeAlertDialog(dialog);
                }

            }
        }





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.google_search,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                    finish();
                return true;
            case R.id.action_google :
                if(reference!=null){
                    /*

                    new AsyncTask<Void, Void, Void>(){
                        String lyrics = "Not Found";
                        String artistname = Lyrics.encodeField(reference.artistName);
                        String songname = Lyrics.encodeField(reference.songName);

                        @Override
                        protected Void doInBackground(Void... params) {
                            String query = "http://lyrics.wikia.com/api.php?func=getSong&artist="
                                    + artistname
                                    + "&song="
                                    + songname
                                    + "&fmt=text";

                            try {

                                URL url = new URL(query);
                                InputStreamReader is = new InputStreamReader(url.openStream());
                                BufferedReader reader = new BufferedReader(is);
                                StringBuilder buf = new StringBuilder();
                                String s;
                                while((s = reader.readLine())!=null){
                                    buf.append(s).append('\n');
                                }
                                lyrics = buf.toString();
                            }catch (Exception e){
                                lyrics = e.toString();

                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            if(lyrics!=null){
                                editText.setText(lyrics);
                            }
                        }
                    }.execute();
                    */


                    try {
                        String temp = reference.songName.trim().replaceAll("\\s+", "+");
                        temp = temp + "+" + reference.artistName.trim().replaceAll("\\s+", "+");
                        temp = searchQuery + temp.trim();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(temp));
                        this.startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.google.com"));
                        this.startActivity(intent);
                    }


                }

                return true;


        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



}
