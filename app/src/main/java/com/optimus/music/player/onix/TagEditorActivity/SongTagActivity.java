package com.optimus.music.player.onix.TagEditorActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.Themes;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagOptionSingleton;

import java.io.File;
import java.io.IOException;

public class SongTagActivity extends AppCompatActivity {

    private Song reference;
    public static String TAGGER_EXTRA = "song";
    private EditText inputTitle, inputAlbum, inputArtist, inputYear, inputGenre, inputTrack;
    FloatingActionButton click;
    private boolean flag = false;

    private final String UNKNOWN = "unknown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Themes.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_tag);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Tags");
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

        click = (FloatingActionButton) findViewById(R.id.click);


        inputTitle = (EditText) findViewById(R.id.inputTitle);
        inputAlbum = (EditText) findViewById(R.id.inputAlbum);
        inputArtist = (EditText) findViewById(R.id.inputArtist);
        inputGenre = (EditText) findViewById(R.id.inputGenre);
        inputYear = (EditText) findViewById(R.id.inputYear);
        inputTrack = (EditText) findViewById(R.id.inputTrack);

        if(reference!=null) {


            try {
                final File test = new File(reference.location);
                if(test.exists()) {
                    TagOptionSingleton.getInstance().setAndroid(true);
                    final AudioFile f = AudioFileIO.read(test);

                    final Tag newTag = f.getTagOrCreateAndSetDefault();

                    String title = newTag.getFirst(FieldKey.TITLE);
                    String album = newTag.getFirst(FieldKey.ALBUM);
                    String artist = newTag.getFirst(FieldKey.ARTIST);
                    String year = newTag.getFirst(FieldKey.YEAR);
                    final String genre = newTag.getFirst(FieldKey.GENRE);
                    String track = newTag.getFirst(FieldKey.TRACK);


                    inputTitle.setText(title, TextView.BufferType.EDITABLE);
                    inputAlbum.setText(album, TextView.BufferType.EDITABLE);
                    inputArtist.setText(artist, TextView.BufferType.EDITABLE);
                    inputYear.setText(year, TextView.BufferType.EDITABLE);
                    inputGenre.setText(genre, TextView.BufferType.EDITABLE);
                    inputTrack.setText(track);

                    click.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String t = inputTitle.getText().toString();
                            String al = inputAlbum.getText().toString();
                            String ar = inputArtist.getText().toString();
                            String yr = inputYear.getText().toString();
                            String gn = inputGenre.getText().toString();
                            String tr = inputTrack.getText().toString();


                            try {
                                if (!t.isEmpty() && !t.equals(reference.songName)) {
                                    newTag.setField(FieldKey.TITLE, t);
                                }

                                if (!al.isEmpty() && !al.equals(reference.albumName)) {
                                    newTag.setField(FieldKey.ALBUM, al);
                                }

                                if (!gn.isEmpty() && !gn.equals(genre)) {
                                    newTag.setField(FieldKey.GENRE, gn);
                                }

                                if (!ar.isEmpty() && !ar.equals(reference.artistName)) {
                                    newTag.setField(FieldKey.ARTIST, ar);
                                }

                                if (!yr.isEmpty() && yr.length() <= 4) {
                                    newTag.setField(FieldKey.YEAR, yr);
                                }

                                if (!tr.isEmpty()) {
                                    newTag.setField(FieldKey.TRACK, tr);
                                }

                                AudioFileIO.write(f);
                                flag = true;


                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                intent.setData(Uri.fromFile(test));
                                sendBroadcast(intent);

                                if(System.currentTimeMillis()%2==0) {
                                    Util.showAd(getResources().getString(R.string.album_inter), SongTagActivity.this);
                                }

                                AlertDialog dialog = new AlertDialog.Builder(SongTagActivity.this)
                                        .setTitle("Changes saved..")
                                        .setMessage("Do you want to exit?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //finish();
                                                Library.scanAll(SongTagActivity.this);
                                                Navigate.home(SongTagActivity.this);
                                            }
                                        })
                                        .setNegativeButton("NO", null)
                                        .show();
                                Themes.themeAlertDialog(dialog);



                            }  catch (Exception e) {
                                Crashlytics.logException(e);
                                AlertDialog dialog = new AlertDialog.Builder(SongTagActivity.this)
                                        .setTitle("Changes not saved..")
                                        .setMessage("Do you want to exit?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                                //Navigate.home(SongTagActivity.this);
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
            }catch (NoClassDefFoundError e){
                Crashlytics.log(e.getMessage());
                AlertDialog dialog = new AlertDialog.Builder(SongTagActivity.this)
                        .setTitle("Changes not saved..")
                        .setMessage("Do you want to exit?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                //Navigate.home(SongTagActivity.this);
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();
                Themes.themeAlertDialog(dialog);
            }
        }




    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.song_tag_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(flag){
            Library.scanAll(this);
            Navigate.home(this);
        }else{
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(flag) {
                    Library.scanAll(this);
                    Navigate.home(this);
                }
                else {
                    finish();
                }
                return true;
            case R.id.action_edit:
                Navigate.to(SongTagActivity.this, LyricsActivity.class,LyricsActivity.TAGGER_EXTRA, reference);
                return true;
        }
        return false;
    }


}
