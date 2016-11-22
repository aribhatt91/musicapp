package com.optimus.music.player.onix.TagEditorActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.optimus.music.player.onix.Common.ConnectionDetector;
import com.optimus.music.player.onix.Common.Instances.Album;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.Common.lastFM.Cache;
import com.optimus.music.player.onix.Common.lastFM.ImageList;
import com.optimus.music.player.onix.Common.lastFM.LAlbum;
import com.optimus.music.player.onix.Common.lastFM.Query;
import com.optimus.music.player.onix.CrazyDataStore.Artists;
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
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.images.AndroidArtwork;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class AlbumTagEditorActivity extends AppCompatActivity {

    private Album reference;
    public static String TAGGER_EXTRA = "album";
    private EditText inputAlbum, inputArtist, inputYear, inputGenre;
    ImageView click, backdrop;
    FloatingActionButton fab;

    private boolean flag = false;
    private String[] arguments;
    private ArrayList<Song> songsList = new ArrayList<>();
    private final String UNKNOWN = "unknown";
    private int PICK_IMAGE_REQUEST = 1;
    private String artUri = "";
    private Uri pathUri;
    ProgressDialog pd;
    String searchQuery = "https://www.google.com/search?tbm=isch&q=";
    boolean tempcreated = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Themes.setTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_tag_editor);

        Bundle b = getIntent().getExtras();
        long id = b.getLong("album_id");

        String[] args = new String[]{String.valueOf(id)};
        String select = MediaStore.Audio.Albums._ID + "=?";
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                Library.albumArtistProjection,
                select, args, null);
        if(cursor!=null && cursor.getCount()==1) {
            cursor.moveToFirst();
            String albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM));
            String detail = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));
            int year = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR));
            int ns = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
            reference = new Album(id, albumName,ns, detail, year, path);
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(null);
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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        backdrop = (ImageView) findViewById(R.id.backdrop);
        //reference = getIntent().getParcelableExtra(TAGGER_EXTRA);



        click = (ImageView) findViewById(R.id.click);

        inputAlbum = (EditText) findViewById(R.id.inputAlbum);
        inputArtist = (EditText) findViewById(R.id.inputArtist);
        inputYear = (EditText) findViewById(R.id.inputYear);
        inputGenre = (EditText) findViewById(R.id.inputGenre);
        TagOptionSingleton.getInstance().setAndroid(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT < 19){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                }
                else {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                }
            }
        });



        if(reference!=null){
            arguments = new String[]{String.valueOf(id)};
            String selection = MediaStore.Audio.Media.ALBUM_ID + "+?";

            Cursor cur = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Library.songProjection,
                    selection,
                    arguments,
                    MediaStore.Audio.Media.TITLE);
            if(cur != null && cur.getCount()>0) {
                cur.moveToFirst();

                for (int k = 0; k < cur.getCount(); k++) {
                    cur.moveToPosition(k);
                    Song s = new Song(cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                            cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                            cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                            cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)),
                            cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                            cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)),
                            cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)),
                            cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)),
                            cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)),
                            cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)));

                    if (s.albumId == reference.albumId)
                        songsList.add(s);
                }

                cur.close();
            }

            Glide.with(this).load(reference.artUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.default_album_art_500)
                    .skipMemoryCache(true)
                    .into(backdrop);

            String GENRE = "";
            inputAlbum.setText(reference.albumName, TextView.BufferType.EDITABLE);
            inputArtist.setText(reference.artistName, TextView.BufferType.EDITABLE);
            inputYear.setText(String.valueOf(reference.year), TextView.BufferType.EDITABLE);
            try {
                File songFile = new File(songsList.get(0).location);
                if (songFile.exists()) {
                    AudioFile audioFile = AudioFileIO.read(songFile);
                    Tag gentag = audioFile.getTagOrCreateAndSetDefault();
                    GENRE = gentag.getFirst(FieldKey.GENRE);
                }
            }catch (Exception e){
                Toast.makeText(AlbumTagEditorActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
            inputGenre.setText(GENRE, TextView.BufferType.EDITABLE);

            click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String alb = inputAlbum.getText().toString();
                    final String art = inputArtist.getText().toString();
                    final String yr = inputYear.getText().toString();
                    final String gen = inputGenre.getText().toString();
                    flag = true;

                    (new AsyncTask<Void,Void,Void>(){
                        String[] filePaths = new String[songsList.size()];
                        ArrayList<File> strings = new ArrayList<>();
                        boolean err = false;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            pd  = new ProgressDialog(AlbumTagEditorActivity.this);
                            pd.setMessage("Applying changes to " + songsList.size() + " files..");
                            pd.setTitle("Please Wait..");
                            pd.setCancelable(false);
                            pd.show();

                    }

                        @Override
                        protected Void doInBackground(Void... params) {

                            for (Song s : songsList){
                                try{
                                    final File f = new File(s.location);
                                    final AudioFile af = AudioFileIO.read(f);
                                    final Tag tag = af.getTagOrCreateAndSetDefault();



                                    if(!alb.isEmpty()){
                                        tag.setField(FieldKey.ALBUM, alb);
                                        af.commit();
                                    }else {
                                        tag.setField(FieldKey.ALBUM, UNKNOWN);

                                    }

                                    if(!yr.isEmpty() && yr.length()<=4) {
                                        tag.setField(FieldKey.YEAR, yr);
                                        af.commit();
                                    }

                                    if(!art.isEmpty()){
                                        tag.setField(FieldKey.ARTIST, art);
                                        af.commit();
                                    }else {
                                        tag.setField(FieldKey.ARTIST, UNKNOWN);
                                        af.commit();
                                    }

                                    if(!gen.isEmpty()){
                                        tag.setField(FieldKey.GENRE, gen);
                                        af.commit();
                                    }
                                    if(artUri!=null && !artUri.isEmpty()){
                                        File file = new File(artUri);
                                        //if(file.exists()) {
                                        try {


                                            AndroidArtwork cover = AndroidArtwork.createArtworkFromFile(file);
                                            //Toast.makeText(AlbumTagEditorActivity.this, "l0 yes", Toast.LENGTH_SHORT).show();

                                            tag.deleteArtworkField();
                                            tag.createField(cover);
                                            //Toast.makeText(AlbumTagEditorActivity.this, "l1 yes", Toast.LENGTH_SHORT).show();

                                            tag.addField(cover);
                                            tag.setField(cover);
                                            //Toast.makeText(AlbumTagEditorActivity.this, "l2 yes", Toast.LENGTH_SHORT).show();

                                            AudioFileIO.write(af);
                                            //Toast.makeText(AlbumTagEditorActivity.this, "l3 yes", Toast.LENGTH_SHORT).show();
                                        }catch (Exception e){
                                            err = true;
                                            Toast.makeText(AlbumTagEditorActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }

                                    }




                                    AudioFileIO.write(af);
                                    af.commit();
                                    strings.add(f);


                                }
                                catch (Exception e){
                                    Crashlytics.logException(e);
                                    e.printStackTrace();
                                    err = true;
                                    //Toast.makeText(AlbumTagEditorActivity.this, "Something went wrong!",Toast.LENGTH_LONG).show();

                                }catch (NoClassDefFoundError e){
                                    e.printStackTrace();
                                    err = true;
                                }

                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                            if(!err) {
                                if(artUri!=null && !artUri.isEmpty()){
                                    updateAlbumArtMediaStore(AlbumTagEditorActivity.this, reference.albumId, artUri);
                                    try{
                                        if(tempcreated) {
                                            File tempfile = new File(artUri);
                                            if (tempfile.delete()) {
                                               // Toast.makeText(AlbumTagEditorActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                                            }else{
                                               // Toast.makeText(AlbumTagEditorActivity.this, artUri.toString(), Toast.LENGTH_SHORT).show();

                                            }
                                            if(Cache.deleteCache(AlbumTagEditorActivity.this)){
                                               // Toast.makeText(AlbumTagEditorActivity.this, "cache deleted", Toast.LENGTH_SHORT).show();
                                            }else{
                                              //  Toast.makeText(AlbumTagEditorActivity.this, "cache not deleted", Toast.LENGTH_SHORT).show();

                                            }
                                            tempcreated = false;
                                        }
                                    }catch (Exception e){
                                        Toast.makeText(AlbumTagEditorActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        tempcreated = false;

                                    }

                                }

                                for(File f : strings){
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    intent.setData(Uri.fromFile(f));
                                    sendBroadcast(intent);
                                }


                                pd.dismiss();

                                //if(System.currentTimeMillis()%2==0) {
                                Util.showAd(getResources().getString(R.string.playlist_inter), AlbumTagEditorActivity.this);
                                //}

                                AlertDialog dialog = new AlertDialog.Builder(AlbumTagEditorActivity.this)
                                        .setTitle("Changes saved..")
                                        .setMessage("Do you want to exit?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //finish();
                                                if(System.currentTimeMillis()%8==0) {
                                                    Util.showAd(getResources().getString(R.string.playlist_inter), AlbumTagEditorActivity.this);
                                                }
                                                Navigate.home(AlbumTagEditorActivity.this);
                                            }
                                        })
                                        .setNegativeButton("NO", null)
                                        .show();
                                Themes.themeAlertDialog(dialog);

                            }else{
                                pd.dismiss();

                                AlertDialog dialog = new AlertDialog.Builder(AlbumTagEditorActivity.this)
                                        .setTitle("Changes not saved..")
                                        .setMessage("Do you want to exit?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //finish();
                                                Navigate.home(AlbumTagEditorActivity.this);
                                            }
                                        })
                                        .setNegativeButton("NO", null)
                                        .show();
                                Themes.themeAlertDialog(dialog);
                                //if(System.currentTimeMillis()%6==0)
                                    //loadInterstitial();

                            }

                        }
                    }).execute();
                }
            });

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_download, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void updateAlbumArtMediaStore(Context context, final long id, String art){
        Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), id);
        context.getContentResolver().delete(uri, null, null);
        ContentValues values = new ContentValues();
        values.put("album_id", id);
        values.put("_data", art);

        Uri newuri = context.getContentResolver()
                .insert(Uri.parse("content://media/external/audio/albumart"),
                        values);
        if(newuri!=null){
            //Toast.makeText(AlbumTagEditorActivity.this, "Mediastore UPDATED", Toast.LENGTH_SHORT).show();
            context.getContentResolver().notifyChange(uri, null);
            Library.colorCache.remove(reference);

        }else{
            //Toast.makeText(AlbumTagEditorActivity.this, "FAILED", Toast.LENGTH_LONG).show();

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {


            try {
                pathUri = data.getData();
                //artUri = Environment.getExternalStorageDirectory() + File.separator + pathUri.getPath();
                //File f = new File(uri.getPath());
                if(Cache.isExternalStorageWritable()) {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pathUri);
                    backdrop.setImageBitmap(bitmap);
                    File dir = new File(Environment.getExternalStorageDirectory() + "/onix");
                    if (!dir.exists())
                        dir.mkdirs();
                    OutputStream outputStream = null;
                    File temp = new File(Environment.getExternalStorageDirectory() + "/onix" + reference.albumId +".png");
                    outputStream = new FileOutputStream(temp);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 85, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    artUri = Environment.getExternalStorageDirectory() + "/onix" + reference.albumId +".png";
                    tempcreated = true;

                }
                /*

                String[] projection = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(pathUri, projection, null, null, null);
                if(cursor!=null) {
                    cursor.moveToFirst();

                    //Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor));

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    //artUri = cursor.getString(columnIndex); // returns null
                    if(artUri!=null) {
                        Glide.with(this).load(pathUri).into(backdrop);
                        Toast.makeText(AlbumTagEditorActivity.this, "Image Picked", Toast.LENGTH_LONG).show();

                    }else{
                       // searchDialog();

                    }
                    cursor.close();
                }*/
            } catch (Exception e) {
                Toast.makeText(this, "Failed to pick image!", Toast.LENGTH_LONG).show();
                artUri = null;
                Crashlytics.logException(e);

            }catch (OutOfMemoryError e){
                artUri = null;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(flag)
                    Navigate.home(this);
                else
                    finish();
                return true;
            case R.id.action_download:


                new AsyncTask<Void, Void, Void>(){
                    boolean isConneted;
                    ProgressDialog pd;
                    LAlbum lfmReference;
                    String result;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        pd  = new ProgressDialog(AlbumTagEditorActivity.this);
                        pd.setMessage("Downloading");
                        pd.setTitle("Please Wait..");
                        pd.setCancelable(false);
                        pd.show();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                        isConneted = cd.isConnectingToInternet();
                        if(isConneted) {
                            try {
                                if(reference!=null)
                                    lfmReference = Query.getAlbum(AlbumTagEditorActivity.this, reference.artistName, reference.albumName);


                            } catch (Exception e) {
                                e.printStackTrace();
                                lfmReference = null;
                            }

                        }

                        else{
                            lfmReference=null;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        pd.dismiss();
                        if(!isConneted){
                            AlertDialog dialog = new AlertDialog.Builder(AlbumTagEditorActivity.this)
                                    .setTitle("Ooopss!")
                                    .setMessage("Failed to connect to the Internet!")
                                    .setNegativeButton("OK", null)
                                    .show();
                            Themes.themeAlertDialog(dialog);
                        }

                        else {

                            if (lfmReference != null) {
                                saveImageFile(AlbumTagEditorActivity.this,
                                        lfmReference.getImageURL(ImageList.SIZE_MEGA));


                            } else {
                                searchDialog();

                            }
                        }
                    }
                }.execute();

                return true;
        }
        return false;
    }
    public void saveImageFile(Context context, final String url){
        String filename = url.substring(url.lastIndexOf('/')+1, url.length());
        final DownloadManager downloadManager;
        try{
            if(Cache.isExternalStorageWritable()) {
                File dir = new File(Environment.getExternalStorageDirectory() + "/onix");
                if (!dir.exists())
                    dir.mkdirs();

                if (dir.exists()){
                    downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    //request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, Cache.getAlbumArtStorageDir());
                    request.setDestinationInExternalPublicDir(dir.getAbsolutePath(), filename);
                    request.allowScanningByMediaScanner();
                    request.setVisibleInDownloadsUi(true);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                    final long reference = downloadManager.enqueue(request);

                    BroadcastReceiver receiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            try {

                                String action = intent.getAction();
                                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                                    DownloadManager.Query query = new DownloadManager.Query();
                                    query.setFilterById(reference);
                                    Cursor c = downloadManager.query(query);
                                    if (c != null && c.moveToFirst()) {
                                        int index = c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
                                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(index)) {
                                            artUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                                            if (artUri != null && !artUri.isEmpty()) {
                                                pathUri = Uri.parse(artUri);
                                                artUri = pathUri.getPath();
                                                backdrop.setImageURI(pathUri);
                                                Toast.makeText(AlbumTagEditorActivity.this, "Download Successful!", Toast.LENGTH_LONG).show();


                                            } else {
                                                searchDialog();

                                            }

                                        }
                                        c.close();

                                    }

                                }
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                                searchDialog();

                            }
                        }

                    };
                    context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                }else{
                    Toast.makeText(AlbumTagEditorActivity.this, "Failed to write to storage!", Toast.LENGTH_LONG).show();

                }
            }else{
                Toast.makeText(AlbumTagEditorActivity.this, "Permission Denied!", Toast.LENGTH_LONG).show();

            }

        }catch (Exception e) {
            searchDialog();
        }
    }








    public void searchDialog(){
        AlertDialog dialog = new AlertDialog.Builder(AlbumTagEditorActivity.this)
                .setTitle("Search google")
                .setMessage("Failed to find album art. Search google?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        searchGoogle();
                    }
                })
                .setNegativeButton("BACK", null)
                .show();
        Themes.themeAlertDialog(dialog);

    }

    public void searchGoogle(){
        if(reference!=null){
            try {
                String temp = reference.albumName.trim().replaceAll("\\s+", "+");
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
    }


}
