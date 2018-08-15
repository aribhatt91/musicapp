package com.optimus.music.player.onix.data;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.data.legacyDB.JukeBoxDBHelper;
import com.optimus.music.player.onix.data.model.Album;
import com.optimus.music.player.onix.data.model.Artist;
import com.optimus.music.player.onix.data.model.AutoPlaylist;
import com.optimus.music.player.onix.data.model.Genre;
import com.optimus.music.player.onix.data.model.MusicFolder;
import com.optimus.music.player.onix.data.model.Playlist;
import com.optimus.music.player.onix.data.model.Song;
import com.optimus.music.player.onix.views.theme.Themes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.optimus.music.player.onix.data.Permissions.hasRWPermission;
import static com.optimus.music.player.onix.data.Permissions.hasWriteSettingsPermission;
import static com.optimus.music.player.onix.data.Permissions.previouslyRequestedRWPermission;
import static com.optimus.music.player.onix.data.Permissions.requestRWPermission;

import static com.optimus.music.player.onix.data.Projections.albumArtistProjection;
import static com.optimus.music.player.onix.data.Projections.albumProjection;
import static com.optimus.music.player.onix.data.Projections.artistProjection;
import static com.optimus.music.player.onix.data.Projections.genreProjection;
import static com.optimus.music.player.onix.data.Projections.playlistEntryProjection;
import static com.optimus.music.player.onix.data.Projections.playlistProjection;
import static com.optimus.music.player.onix.data.Projections.songProjection;

/**
 * Created by aribhatt on 10/05/18.
 */

public class Library {

    public static final String PLAY_COUNT_FILENAME = ".myplaycount";
    public static final String PLAY_COUNT_FILE_COMMENT = "This file contains play count information for Onix and should not be edited";

    private static final String AUTO_PLAYLIST_EXTENSION = ".jpl";
    public static final int SQL_MAX_VARS = 900;

    public static HashMap<Genre, Set<Long>> genMap = new HashMap<>();


    private static String[] folderpaths;
    private static String[] foldernames;
    private static Set<String> allFolders = new HashSet<>();

    private static Set<Long> incArtists = new HashSet<>();
    private static Set<Long> incAlbums = new HashSet<>();


    private static final ArrayList<Playlist> playlistLib = new ArrayList<>();
    private static final ArrayList<Song> songLib = new ArrayList<>();
    private static final ArrayList<Artist> artistLib = new ArrayList<>();
    private static final ArrayList<Album> albumLib = new ArrayList<>();
    private static final ArrayList<Genre> genreLib = new ArrayList<>();
    private static final ArrayList<AutoPlaylist> autoplaylistLib = new ArrayList<>();
    private static final ArrayList<MusicFolder> folders = new ArrayList<>();
    private static final ArrayList<Song> playingQueue = new ArrayList<>();

    private static final Map<Long, Integer> playCounts = new HashMap<>();
    private static final Map<Long, Integer> skipCounts = new HashMap<>();
    private static final Map<Long, Integer> playDates = new HashMap<>();

    public static final Map<Long, Uri> albumArtUriCache = new HashMap<>();
    public static final Map<Long, int[]> colorCache = new HashMap<>();

    public static final String TEST_DEVICE_ID = "31809332AF5B89D37488ACC6A70B5BB2";
    public static final long SEVEN = 604800;
    public static final long MONTH = 2592000;
    public static ArrayList<Song> seven = new ArrayList<>();
    public static ArrayList<Song> month = new ArrayList<>();

    public static void scanAll (final Activity activity){
        if (hasRWPermission(activity)) {
            resetAll();
            setPlaylistLib(scanPlaylists(activity));
            setSongLib(scanSongs(activity));
            setArtistLib(scanArtists(activity));
            setAlbumLib(scanAlbums(activity));
            setGenreLib(scanGenres(activity));
            //sort(activity);
            //notifyLibraryRefreshed();

            // If the user permits it, log info about the size of their library
            if (Prefs.allowAnalytics(activity)) {
                int autoPlaylistCount = 0;
                for (Playlist p : playlistLib) {
                    if (p instanceof AutoPlaylist) {
                        autoPlaylistCount++;
                    }
                }

                Answers.getInstance().logCustom(
                        new CustomEvent("Loaded library")
                                .putCustomAttribute("Playlist count", playlistLib.size())
                                .putCustomAttribute("Auto Playlist count", autoPlaylistCount)
                                .putCustomAttribute("Song count", songLib.size())
                                .putCustomAttribute("Artist count", artistLib.size())
                                .putCustomAttribute("Album count", albumLib.size())
                                .putCustomAttribute("Genre count", genreLib.size()));
            }

        }
        else if (!previouslyRequestedRWPermission(activity)) {
            requestRWPermission(activity);
        }/*
        else{
            requestRWPermission(activity);
        }*/
    }

    public static ArrayList<Song> scanSongs(Context context){
        incArtists.clear();
        incAlbums.clear();
        allFolders.clear();
        ArrayList<Song> songs = new ArrayList<>();
        Song s;
        String orderby;
        Set<String> exFolders = Prefs.getExcludedFolders(context);
        String msg = "";

        for (String str : exFolders) {
            msg += "\n" + str;
        }

        //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();


        switch (Prefs.getSongSortOrder(context)){
            case 0:
                orderby = MediaStore.Audio.Media.TITLE;
                break;
            case 1:
                orderby = MediaStore.Audio.Media.TITLE + " DESC";
                break;
            case 2:
                orderby = MediaStore.Audio.Media.ALBUM;
                break;
            case 3:
                orderby = MediaStore.Audio.Media.ALBUM + " DESC";
                break;
            case 4:
                orderby = MediaStore.Audio.Media.DATE_ADDED + " DESC";
                break;
            case 5:
                orderby = MediaStore.Audio.Media.ARTIST;
                break;
            case 6:
                orderby = MediaStore.Audio.Media.ARTIST + " DESC";
                break;
            case 7:
                orderby = MediaStore.Audio.Media.DATA;
                break;
            default:
                orderby = MediaStore.Audio.Media.TITLE;
                break;
        }
        Cursor cur;

        if(Prefs.showAll(context)){
            cur = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    songProjection,
                    null,
                    null,
                    orderby
            );
        }
        else {
            cur = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    songProjection,
                    MediaStore.Audio.Media.IS_MUSIC + "!= 0",
                    null,
                    orderby
            );
        }


        boolean small = Prefs.hideSmall(context);

        if (cur != null && cur.getCount()>0) {

            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                s = new Song(
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.TRACK))

                );

                File music = new File(s.location);
                String path = "";
                if(music.exists()){
                    path = music.getParent();
                    allFolders.add(path);
                }



                if(!exFolders.contains(path)){

                    if(small){
                        if(s.songDuration >= 30000){
                            songs.add(s);
                            incAlbums.add(s.albumId);
                            incArtists.add(s.artistId);
                        }
                    }else {
                        songs.add(s);
                        incAlbums.add(s.albumId);
                        incArtists.add(s.artistId);
                    }

                }


            }
            cur.close();
        }

        return songs;

    }

    public static ArrayList<Album> scanAlbums(Context context){
        ArrayList<Album> albums = new ArrayList<>();
        String orderby;
        switch(Prefs.getAlbumSortOrder(context)){
            case 0:
                orderby = MediaStore.Audio.Albums.ALBUM;
                break;
            case 1:
                orderby = MediaStore.Audio.Albums.LAST_YEAR+" DESC";
                break;
            case 2:
                orderby = MediaStore.Audio.Albums.ARTIST;
                break;
            default:
                orderby = MediaStore.Audio.Albums.ALBUM;
        }
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                albumArtistProjection,
                null,
                null,
                orderby
        );

        if (cursor != null && cursor.getCount()>0) {

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                Album a = new Album(
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.LAST_YEAR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART))
                );
                if(incAlbums.contains(a.albumId)){
                    albums.add(a);
                }
            }

            cursor.close();
        }

        return albums;
    }

    public static ArrayList<Artist> scanArtists(Context context){
        ArrayList<Artist> artists = new ArrayList<>();
        Artist a;
        Cursor cur = context.getContentResolver().query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                artistProjection,
                null,
                null,
                MediaStore.Audio.Artists.ARTIST
        );

        if (cur != null && cur.getCount()>0) {

            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                a = new Artist(
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Artists._ID)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Artists.ARTIST)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS))

                );
                if(incArtists.contains(a.artistId)) {
                    artists.add(a);
                }
            }
            cur.close();
        }
        return artists;

    }
    /**
     * Scans the MediaStore for playlists
     * @param context {@link Context} to use to open a {@link Cursor}
     * @return An {@link ArrayList} with the {@link Playlist}s in the MediaStore
     */
    public static ArrayList<Playlist> scanPlaylists (Context context){
        ArrayList<Playlist> playlists = new ArrayList<>();

        Cursor cur = context.getContentResolver().query(
                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                playlistProjection,
                null,
                null,
                MediaStore.Audio.Playlists.NAME + " ASC");

        if (cur != null && cur.getCount()>0) {


            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                playlists.add(new Playlist(
                                cur.getLong(cur.getColumnIndex(MediaStore.Audio.Playlists._ID)),
                                cur.getString(cur.getColumnIndex(MediaStore.Audio.Playlists.NAME))
                        )
                );
            }
            cur.close();
        }
        /*

        for (Playlist p : scanAutoPlaylists(context)) {
            if (playlists.remove(p)) {
                playlists.add(p);
            } else {
                // If AutoPlaylists have been deleted outside of Jockey, delete its configuration
                //noinspection ResultOfMethodCallIgnored
                new File(context.getExternalFilesDir(null) + "/" + p.playlistName + AUTO_PLAYLIST_EXTENSION)
                        .delete();
            }
        }
        */
        return playlists;
    }
    /**
     * Scans the MediaStore for genres
     * @param context {@link Context} to use to open a {@link Cursor}
     * @return An {@link ArrayList} with the {@link Genre}s in the MediaStore
     */
    public static ArrayList<Genre> scanGenres (Context context){
        ArrayList<Genre> genres = new ArrayList<>();

        Cursor cur = context.getContentResolver().query(
                MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                genreProjection,
                null,
                null,
                MediaStore.Audio.Genres.NAME + " ASC");

        if (cur == null) {
            return genres;
        }

        for (int i = 0; i < cur.getCount(); i++) {
            cur.moveToPosition(i);
            int thisGenreId = cur.getInt(cur.getColumnIndex(MediaStore.Audio.Genres._ID));

         /*   if (cur.getString(cur.getColumnIndex(MediaStore.Audio.Genres.NAME)).equalsIgnoreCase("Unknown")){
                genres.add(new Genre(-1, "Unknown"));
            }
            else {*/

            // Associate all songs in this genre by setting the genreID field of each song in the genre

            Cursor genreCur = context.getContentResolver().query(
                    MediaStore.Audio.Genres.Members.getContentUri("external", thisGenreId),
                    new String[]{MediaStore.Audio.Media._ID},
                    MediaStore.Audio.Media.IS_MUSIC + " != 0 ", null, null);


            if (genreCur != null) {





                genreCur.moveToFirst();

                final int ID_INDEX = genreCur.getColumnIndex(MediaStore.Audio.Media._ID);
                int cnt = 0;
                for (int j = 0; j < genreCur.getCount(); j++) {
                    genreCur.moveToPosition(j);
                    final Song s = findSongById(genreCur.getInt(ID_INDEX));
                    if (s != null) {
                        s.genreId = thisGenreId;
                        cnt++;
                    }
                }
                if (cnt>0 && genreCur.getCount() > 0 && thisGenreId > 0) {
                    genres.add(new Genre(thisGenreId, cur.getString(cur.getColumnIndex(MediaStore.Audio.Genres.NAME))));
                }
                genreCur.close();
            }
            //}
        }
        cur.close();

        return genres;
    }


    public static int getPlaylistCount(){
        return playlistLib.size();
    }




    //
    //          LIBRARY STORAGE METHODS
    //

    /**
     * Remove all library entries from memory
     */
    public static void resetAll(){
        playlistLib.clear();
        songLib.clear();
        artistLib.clear();
        albumLib.clear();
        genreLib.clear();
    }

    /**
     * Replace the playlist library in memory with another one
     * @param newLib The new playlist library
     */
    public static void setPlaylistLib(ArrayList<Playlist> newLib){
        playlistLib.clear();
        playlistLib.addAll(newLib);
    }

    /**
     * Replace the song library in memory with another one
     * @param newLib The new song library
     */
    public static void setSongLib(ArrayList<Song> newLib){
        songLib.clear();
        songLib.addAll(newLib);
    }

    /**
     * Replace the album library in memory with another one
     * @param newLib The new album library
     */
    public static void setAlbumLib(ArrayList<Album> newLib){
        albumLib.clear();
        albumLib.addAll(newLib);
    }

    /**
     * Replace the artist library in memory with another one
     * @param newLib The new artist library
     */
    public static void setArtistLib(ArrayList<Artist> newLib){
        artistLib.clear();
        artistLib.addAll(newLib);
    }

    /**
     * Replace the genre library in memory with another one
     * @param newLib The new genre library
     */
    public static void setGenreLib(ArrayList<Genre> newLib){
        genreLib.clear();
        genreLib.addAll(newLib);
    }

    /**
     * @return true if the library is populated with any entries
     */
    public static boolean isEmpty (){
        return songLib.isEmpty() && albumLib.isEmpty() && artistLib.isEmpty() && playlistLib.isEmpty() && genreLib.isEmpty();
    }

    /**
     * @return An {@link ArrayList} of {@link Playlist}s in the MediaStore
     */
    public static ArrayList<Playlist> getPlaylists(){
        return playlistLib;
    }

    /**
     * @return An {@link ArrayList} of {@link Song}s in the MediaStore
     */
    public static ArrayList<Song> getSongs(){
        return songLib;
    }

    /**
     * @return An {@link ArrayList} of {@link Album}s in the MediaStore
     */
    public static ArrayList<Album> getAlbums(){
        return albumLib;
    }

    /**
     * @return An {@link ArrayList} of {@link Artist}s in the MediaStore
     */
    public static ArrayList<Artist> getArtists(){
        return artistLib;
    }

    /**
     * @return An {@link ArrayList} of {@link Genre}s in the MediaStore
     */
    public static ArrayList<Genre> getGenres(){
        return genreLib;
    }

    /**
     * Finds a {@link Song} in the library based on its Id
     * @param songId the MediaStore Id of the {@link Song}
     * @return A {@link Song} with a matching Id
     */
    public static Song findSongById (int songId){
        for (Song s : songLib){
            if (s.songId == songId){
                return s;
            }
        }
        return null;
    }

    public static ArrayList<Song> buildSongListFromIds (long[] songIDs, Context context){
        ArrayList<Song> contents = new ArrayList<>();
        int lower, upper;


        if(songIDs.length > SQL_MAX_VARS){
            lower = 0; upper = SQL_MAX_VARS;
            while(lower<upper && upper<=songIDs.length && upper-lower<=SQL_MAX_VARS){
                contents.addAll(buildSongListFromIds(songIDs, context, lower, upper));
                lower = upper;
                upper = (upper+SQL_MAX_VARS > songIDs.length) ? songIDs.length : upper+SQL_MAX_VARS;
            }
        }
        else{
            contents.addAll(buildSongListFromIds(songIDs, context, 0, songIDs.length));
        }

        ArrayList<Song> songs = new ArrayList<>();
        for (long i : songIDs) {
            for (Song s : contents) {
                if (s.songId == i) {
                    songs.add(s);
                    break;
                }
            }
        }

        return songs;
    }

    /**
     * Build an {@link ArrayList} of {@link Song}s from a list of id's. Doesn't require the library to be loaded
     * @param songIDs The list of song ids to convert to {@link Song}s
     * @param context The {@link Context} used to open a {@link Cursor}
     * @return An {@link ArrayList} of {@link Song}s with ids matching those of the songIDs parameter
     */
    private static ArrayList<Song> buildSongListFromIds (long[] songIDs, Context context,
                                                         int lowerBound, int upperBound){
        ArrayList<Song> contents = new ArrayList<>();
        int len = upperBound - lowerBound;

        if (songIDs.length == 0){
            return contents;
        }

        String query = MediaStore.Audio.Media._ID + " IN(?";
        //String[] ids = new String[songIDs.length];
        String[] ids = new String[len];
        ids[0] = Long.toString(songIDs[lowerBound]);
        //ids[0] = Long.toString(songIDs[0]);

        //for (int i = 1; i < songIDs.length; i++){
        for (int i = 1; i < len; i++){
            query += ",?";
            //ids[i] = Long.toString(songIDs[i]);
            ids[i] = Long.toString(songIDs[i + lowerBound]);

        }
        query += ")";

        Cursor cur = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                songProjection,
                query,
                ids,
                MediaStore.Audio.Media.TITLE + " ASC");

        if (cur == null) {
            return contents;
        }

        for (int i = 0; i < cur.getCount(); i++) {
            cur.moveToPosition(i);
            Song s = new Song(
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                    cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                    cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)),
                    cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                    cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                    cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)));

            s.trackNumber = cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.TRACK));
            contents.add(s);
        }
        cur.close();

        return contents;
    }

    /**
     * Finds a {@link Artist} in the library based on its Id
     * @param artistId the MediaStore Id of the {@link Artist}
     * @return A {@link Artist} with a matching Id
     */
    public static Artist findArtistById (int artistId){
        for (Artist a : artistLib){
            if (a.artistId == artistId){
                return a;
            }
        }
        return null;
    }

    /**
     * Finds a {@link Album} in a library based on its Id
     * @param albumId the MediaStore Id of the {@link Album}
     * @return A {@link Album} with a matching Id
     */
    public static Album findAlbumById (int albumId){
        // Returns the first Artist object in the library with a matching id
        for (Album a : albumLib){
            if (a.albumId == albumId){
                return a;
            }
        }
        return null;
    }

    /**
     * Finds a {@link Genre} in a library based on its Id
     * @param genreId the MediaStore Id of the {@link Genre}
     * @return A {@link Genre} with a matching Id
     */
    public static Genre findGenreById (int genreId){
        // Returns the first Genre object in the library with a matching id
        for (Genre g : genreLib){
            if (g.genreId == genreId){
                return g;
            }
        }
        return null;
    }

    public static Artist findArtistByName (String artistName){
        final String trimmedQuery = artistName.trim();
        for (Artist a : artistLib){
            if (a.artistName.equalsIgnoreCase(trimmedQuery))
                return a;
        }
        return null;
    }

    /**
     * Get a list of albums by a certain artist
     * @return An {@link ArrayList} of {@link Album}s by the artist
     */


    public String getTotalAlbums(Context context){
        int a=0;
        Cursor cur = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                albumProjection,
                null,
                null,
                MediaStore.Audio.Albums.ALBUM
        );
        if(cur!=null)
            a = cur.getCount();
        String album = a +" "+ (a>1?"Albums":"Album");
        return album;
    }

    /**
     * Get a list of songs by a certain artist
     * @param artist The {@link Artist} to get the entries of
     * @return An {@link ArrayList} of {@link Song}s by the artist
     */
    public static ArrayList<Song> getArtistSongEntries(Artist artist) {
        ArrayList<Song> songEntries = new ArrayList<>();

        for (Song s : songLib) {
            if (s.artistId == artist.artistId) {
                songEntries.add(s);
            }
        }

        return songEntries;
    }

    //
    //          PLAY COUNT READING & ACCESSING METHODS
    //

    /**
     * Reload the play counts as modified by
     * @param context Used to open a {@link Properties} from disk
     */
    public static void loadPlayCounts(Context context) {
        playCounts.clear();
        skipCounts.clear();
        playDates.clear();
        try {
            Properties countProperties = openPlayCountFile(context);
            Enumeration iterator = countProperties.propertyNames();

            while (iterator.hasMoreElements()) {
                String key = (String) iterator.nextElement();
                String value = countProperties.getProperty(key, "0,0");

                final String[] originalValues = value.split(",");

                int playCount = Integer.parseInt(originalValues[0]);
                int skipCount = Integer.parseInt(originalValues[1]);
                int playDate = 0;

                if (originalValues.length > 2) {
                    playDate = Integer.parseInt(originalValues[2]);
                }

                playCounts.put(Long.parseLong(key), playCount);
                skipCounts.put(Long.parseLong(key), skipCount);
                playDates.put(Long.parseLong(key), playDate);
            }
        } catch (IOException e) {
            Crashlytics.logException(e);
        }
    }

    public static Properties openPlayCountFile(Context context) throws IOException {
        File file = new File(context.getExternalFilesDir(null) + "/" + Library.PLAY_COUNT_FILENAME);

        if (file.exists() || file.createNewFile()) {
            InputStream is = new FileInputStream(file);
            Properties playCountHashtable;

            playCountHashtable = new Properties();
            playCountHashtable.load(is);

            is.close();
            return playCountHashtable;
        } else {
            return new Properties();
        }
    }

    /**
     * Returns the number of skips a song has. Note that you may need to call
     * {@link Library#loadPlayCounts(Context)} in case the data has gone stale
     * @param songId The {@link Song#songId} as written in the MediaStore
     * @return The number of times a song has been skipped
     */
    public static int getSkipCount(long songId) {
        if (skipCounts.containsKey(songId)) {
            return skipCounts.get(songId);
        }
        return 0;
    }

    /**
     * Returns the number of plays a song has. Note that you may need to call
     * {@link Library#loadPlayCounts(Context)} in case the data has gone stale
     * @param songId The {@link Song#songId} as written in the MediaStore
     * @return The number of times a song has been plays
     */
    public static int getPlayCount(long songId) {
        if (playCounts.containsKey(songId)) {
            return playCounts.get(songId);
        }
        return 0;
    }

    /**
     * * Returns the last time a song was played with Jockey. Note that you may need to call
     * {@link Library#loadPlayCounts(Context)} in case the data has gone stale
     * @param songId The {@link Song#songId} as written in the MediaStore
     * @return The last time a song was played given in seconds as a UTC timestamp
     *         (since midnight of January 1, 1970 UTC)
     */
    public static int getPlayDate(long songId) {
        if (playDates.containsKey(songId)) {
            return playDates.get(songId);
        }
        return 0;
    }






    /**
     * Get a list of songs on a certain album
     * @param album The {@link Album} to get the entries of
     * @return An {@link ArrayList} of {@link Song}s contained in the album
     */
    public static ArrayList<Song> getAlbumEntries(Album album) {
        ArrayList<Song> songEntries = new ArrayList<>();

        for (Song s : songLib) {
            if (s.albumId == album.albumId) {
                songEntries.add(s);
            }
        }

        Collections.sort(songEntries, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return (o1.trackNumber)-(o2.trackNumber);
            }
        });

        return songEntries;
    }

    public static ArrayList<Song> getAllSongsByArtist(Context context, String artistName){
        ArrayList<Song> songs = new ArrayList<>();
        String selection = MediaStore.Audio.Media.ARTIST + "=?";
        String[] arguments = new String[]{artistName};


        Cursor cur = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                songProjection,
                selection,
                arguments,
                MediaStore.Audio.Media.TITLE);


        if(cur != null) {
            cur.moveToFirst();
            Set<String> exFolders = Prefs.getExcludedFolders(context);


            for (int k = 0; k < cur.getCount(); k++) {
                cur.moveToPosition(k);
                Song s = new Song(cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                        cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                        cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)),
                        cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                        cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                        cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)),
                        cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)),
                        cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)),
                        cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)),
                        cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)));

                File f = new File(s.location);
                String path = "";
                if(f.exists()){
                    path = f.getParent();
                }
                if(!exFolders.contains(path)){
                    songs.add(s);
                }
            }
        }

        return songs;
    }

    /**
     * Get a list of songs in a certain genre
     * @param genre The {@link Genre} to get the entries of
     * @return An {@link ArrayList} of {@link Song}s contained in the genre
     */
    public static ArrayList<Song> getGenreEntries(Genre genre) {
        ArrayList<Song> songEntries = new ArrayList<>();

        for (Song s : songLib) {
            if (s.genreId == genre.genreId) {
                songEntries.add(s);
            }
        }

        return songEntries;
    }


    //
    //          CONTENTS QUERY METHODS
    //

    /**
     * Get a list of songs in a certain playlist
     * @param context A {@link Context} to open a {@link Cursor}
     * @param playlist The {@link Playlist} to get the entries of
     * @return An {@link ArrayList} of {@link Song}s contained in the playlist
     */
    public static ArrayList<Song> getPlaylistEntries (Context context, Playlist playlist){
        if (playlist instanceof AutoPlaylist){
            ArrayList<Song> entries = ((AutoPlaylist) playlist).generatePlaylist(context);
            editPlaylist(context, playlist, entries);
            return entries;
        }

        ArrayList<Song> songEntries = new ArrayList<>();

        Cursor cur = context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.playlistId),
                playlistEntryProjection,
                MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null);

        if (cur == null) {
            return songEntries;
        }

        Set<String> exFolders = Prefs.getExcludedFolders(context);

        for (int i = 0; i < cur.getCount(); i++) {
            cur.moveToPosition(i);
            Song s = new Song(
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE)),
                    cur.getInt(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST)),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM)),
                    cur.getInt(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION)),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)),
                    cur.getInt(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.YEAR)),
                    cur.getInt(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.DATE_ADDED)),
                    cur.getInt(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM_ID)),
                    cur.getInt(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST_ID)));
            File f = new File(s.location);
            String path = "";
            if(f.exists()){
                path = f.getParent();
            }
            if(!exFolders.contains(path)){
                songEntries.add(s);
            }
        }
        cur.close();

        return songEntries;
    }



    //
    //          PLAYLIST WRITING METHODS
    //

    /**
     * Add a new playlist to the MediaStore
     * @param view A {@link View} to put a in. Will also be used to get a {@link Context}.
     * @param playlistName The name of the new playlist
     * @param songList An {@link ArrayList} of {@link Song}s to populate the new playlist
     * @return The Playlist that was added to the library
     */
    public static Playlist createPlaylist(final View view, final String playlistName, @Nullable final ArrayList<Song> songList){
        final Context context = view.getContext();
        String trimmedName = playlistName.trim();

        setPlaylistLib(scanPlaylists(context));

        String error = verifyPlaylistName(context, trimmedName);
        if (error != null){
            Toast.makeText(context, error,
                    Toast.LENGTH_LONG).show();

            return null;
        }

        // Add the playlist to the MediaStore
        final Playlist created = addPlaylist(context, trimmedName, songList);
        Toast.makeText(context, String.format(
                context.getResources().getString(R.string.message_created_playlist), playlistName),
                Toast.LENGTH_LONG).show();

        /*Snackbar
                .make(
                        view,
                        String.format(context.getResources().getString(R.string.message_created_playlist), playlistName),
                        Snackbar.LENGTH_LONG)
                .setAction(
                        R.string.action_undo,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deletePlaylist(context, created);
                            }
                        })
                .show();*/

        return created;
    }

    /**
     * Test a playlist name to make sure it is valid when making a new playlist. Invalid playlist names
     * are instance_empty or already exist in the MediaStore
     * @param context A {@link Context} used to get localized Strings
     * @param playlistName The playlist name that needs to be validated
     * @return null if there is no error, or a {@link String} describing the error that can be
     *         presented to the user
     */
    public static String verifyPlaylistName (final Context context, final String playlistName){
        String trimmedName = playlistName.trim();
        if (trimmedName.length() == 0){
            return context.getResources().getString(R.string.error_hint_empty_playlist);
        }

        for (Playlist p : playlistLib){
            if (p.playlistName.equalsIgnoreCase(trimmedName)){
                return context.getResources().getString(R.string.error_hint_duplicate_playlist);
            }
        }
        return null;
    }

    /**
     * Removes a playlist from the MediaStore
     * @param view A {@link View} to show a {@link } and to get a {@link Context} used to edit the MediaStore
     * @param playlist A {@link Playlist} which will be removed from the MediaStore
     */
    public static void removePlaylist(final View view, final Playlist playlist){
        final Context context = view.getContext();
        final ArrayList<Song> entries = getPlaylistEntries(context, playlist);

        deletePlaylist(context, playlist);

        /*
        //MOVE THIS TO VIEWS/ACTIVITY SECTION
        Snackbar
                .make(
                        view,
                        String.format(context.getString(R.string.message_removed_playlist), playlist),
                        Snackbar.LENGTH_LONG)
                .setAction(
                        context.getString(R.string.action_undo),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (playlist instanceof AutoPlaylist) {
                                    createAutoPlaylist(context, (AutoPlaylist) playlist);
                                } else {
                                    addPlaylist(context, playlist.playlistName, entries);
                                }
                            }
                        })
                .show();
                */
    }

    /**
     * Replace the entries of a playlist in the MediaStore with a new {@link ArrayList} of {@link Song}s
     * @param context A {@link Context} to open a {@link Cursor}
     * @param playlist The {@link Playlist} to edit in the MediaStore
     * @param newSongList An {@link ArrayList} of {@link Song}s to overwrite the list contained in the MediaStore
     */
    public static void editPlaylist(final Context context, final Playlist playlist, final ArrayList<Song> newSongList){
        // Clear the playlist...
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.playlistId);
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(uri, null, null);

        // Then add all of the songs to it
        ContentValues[] values = new ContentValues[newSongList.size()];
        for (int i = 0; i < newSongList.size(); i++) {
            values[i] = new ContentValues();
            values[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, i + 1);
            values[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, newSongList.get(i).songId);
        }
        resolver.bulkInsert(uri, values);
        resolver.notifyChange(Uri.parse("content://media"), null);
    }

    /**
     * Rename a playlist in the MediaStore
     * @param context A {@link Context} to open a {@link ContentResolver}
     * @param playlistID The id of the {@link Playlist} to be renamed
     * @param name The new name of the playlist
     */
    public static void renamePlaylist(final Context context, final long playlistID, final String name) {
        if (verifyPlaylistName(context, name) == null) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, name);

            ContentResolver resolver = context.getContentResolver();
            if(resolver==null)
                return;
            resolver.update(
                    MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    values,
                    MediaStore.Audio.Playlists._ID + "=?",
                    new String[]{Long.toString(playlistID)});
        }
    }

    /**
     * Append a song to the end of a playlist. Alerts the user about duplicates
     * @param context A {@link Context} to open a {@link Cursor}
     * @param playlist The {@link Playlist} to edit in the MediaStore
     * @param song The {@link Song} to be added to the playlist in the MediaStore
     */
    public static void addPlaylistEntry(final Context context, final Playlist playlist, final Song song){
        // Public method to add a song to a playlist
        // Checks the playlist for duplicate entries

        //TODO Move this to Views/activity
        if (getPlaylistEntries(context, playlist).contains(song)){
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(context.getResources().getQuantityString(R.plurals.alert_confirm_duplicates, 1))
                    .setMessage(context.getString(R.string.playlist_confirm_duplicate, playlist, song))
                    .setPositiveButton(R.string.action_add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addSongToEndOfPlaylist(context, playlist, song);
                        }
                    })
                    .setNegativeButton(R.string.action_cancel, null)
                    .show();
            Themes.themeAlertDialog(dialog);
        }
        else{
            addSongToEndOfPlaylist(context, playlist, song);
        }
    }

    /**
     * Append a list of songs to the end of a playlist. Alerts the user about duplicates
     * @param view A {@link View} to put a in. Will
     *             also be used to get a {@link Context}.
     * @param playlist The {@link Playlist} to edit in the MediaStore
     * @param songs The {@link ArrayList} of {@link Song}s to be added to the playlist in the MediaStore
     */
    public static void addPlaylistEntries(final View view, final Playlist playlist, final ArrayList<Song> songs){
        // Public method to add songs to a playlist
        // Checks the playlist for duplicate entries

        final Context context = view.getContext();

        int duplicateCount = 0;
        final ArrayList<Song> currentEntries = getPlaylistEntries(context, playlist);
        final ArrayList<Song> newEntries = new ArrayList<>();

        for (Song s : songs){
            if (currentEntries.contains(s))duplicateCount++;
            else newEntries.add(s);
        }

        if (duplicateCount > 0){
            AlertDialog.Builder alert = new AlertDialog.Builder(context).setTitle(context.getResources().getQuantityString(R.plurals.alert_confirm_duplicates, duplicateCount));

            if (duplicateCount == songs.size()) {
                alert
                        .setMessage(context.getString(R.string.playlist_confirm_all_duplicates, duplicateCount))
                        .setPositiveButton(context.getResources().getQuantityText(R.plurals.playlist_positive_add_duplicates, duplicateCount), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addSongsToEndOfPlaylist(context, playlist, songs);
                                Toast.makeText(context,
                                        context.getString(R.string.confirm_add_songs, songs.size(), playlist.playlistName),
                                        Toast.LENGTH_LONG).show();

                            }
                        })
                        .setNeutralButton(context.getString(R.string.action_cancel), null);
            }
            else{
                alert
                        .setMessage(context.getResources().getQuantityString(R.plurals.playlist_confirm_some_duplicates, duplicateCount, duplicateCount))
                        .setPositiveButton(R.string.action_add_new, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addSongsToEndOfPlaylist(context, playlist, newEntries);

                                Snackbar.make(
                                        view,
                                        context.getString(R.string.confirm_add_songs, newEntries.size(), playlist.playlistName),
                                        Snackbar.LENGTH_LONG)
                                        .setAction(R.string.action_undo, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Library.editPlaylist(
                                                        context,
                                                        playlist,
                                                        currentEntries);
                                            }
                                        }).show();
                            }
                        })
                        .setNegativeButton(R.string.action_add_all, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addSongsToEndOfPlaylist(context, playlist, songs);
                                Snackbar.make(
                                        view,
                                        context.getString(R.string.confirm_add_songs, songs.size(), playlist.playlistName),
                                        Snackbar.LENGTH_LONG)
                                        .setAction(R.string.action_undo, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Library.editPlaylist(
                                                        context,
                                                        playlist,
                                                        currentEntries);
                                            }
                                        }).show();
                            }
                        })
                        .setNeutralButton(R.string.action_cancel, null);
            }

            Themes.themeAlertDialog(alert.show());
        }
        else{
            addSongsToEndOfPlaylist(context, playlist, songs);
            //TODO
            //Toast.makeText(context, context.getString(R.string.confirm_add_songs, newEntries.size(), playlist.playlistName), Toast.LENGTH_LONG).show();

        }
    }

    //
    //          MEDIA_STORE EDIT METHODS
    //
    // These methods only perform actions to the MediaStore. They do not validate inputs, and they
    // do not display confirmation messages to the user.
    //

    /**
     * Add a new playlist to the MediaStore and to the application's current library instance. Use
     * this when making regular playlists.
     * Outside of this class, use {@link Library#createPlaylist(View, String, ArrayList)} instead
     * <b>This method DOES NOT validate inputs or display a confirmation message to the user</b>.
     * @param context A {@link Context} used to edit the MediaStore
     * @param playlistName The name of the new playlist
     * @param songList An {@link ArrayList} of {@link Song}s to populate the new playlist
     * @return The Playlist that was added to the library
     */
    private static Playlist addPlaylist(final Context context, final String playlistName,
                                        @Nullable final ArrayList<Song> songList) {
        final Playlist added = makePlaylist(context, playlistName, songList);
        playlistLib.add(added);
        Collections.sort(playlistLib);
        //notifyPlaylistAdded(added);
        return added;
    }

    /**
     * Internal logic for adding a playlist to the MediaStore only.
     * @param context A {@link Context} used to edit the MediaStore
     * @param playlistName The name of the new playlist
     * @param songList An {@link ArrayList} of {@link Song}s to populate the new playlist
     * @return The Playlist that was added to the library
     * @see Library#addPlaylist(Context, String, ArrayList) for playlist creation
     * @see Library#createAutoPlaylist(Context, AutoPlaylist) for AutoPlaylist creation
     */
    private static Playlist makePlaylist(final Context context, final String playlistName, @Nullable final ArrayList<Song> songList){
        String trimmedName = playlistName.trim();

        // Add the playlist to the MediaStore
        ContentValues mInserts = new ContentValues();
        mInserts.put(MediaStore.Audio.Playlists.NAME, trimmedName);
        mInserts.put(MediaStore.Audio.Playlists.DATE_ADDED, System.currentTimeMillis());
        mInserts.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());

        Uri newPlaylistUri = context.getContentResolver().insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, mInserts);

        if (newPlaylistUri == null) {
            return null;
        }

        // Get the id of the new playlist
        Cursor cursor = context.getContentResolver().query(
                newPlaylistUri,
                new String[] {MediaStore.Audio.Playlists._ID},
                null, null, null);

        if (cursor == null) {
            return new Playlist();
        }

        cursor.moveToFirst();
        final Playlist playlist = new Playlist(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)), playlistName);
        cursor.close();

        // If we have a list of songs, associate it with the playlist
        if(songList != null) {
            ContentValues[] values = new ContentValues[songList.size()];

            for (int i = 0; i < songList.size(); i++) {
                values[i] = new ContentValues();
                values[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, i);
                values[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songList.get(i).songId);
            }

            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.playlistId);
            ContentResolver resolver = context.getContentResolver();

            resolver.bulkInsert(uri, values);
            resolver.notifyChange(Uri.parse("content://media"), null);
        }

        return playlist;
    }

    /**
     * Removes a playlist from the MediaStore
     * @param context A {@link Context} to update the MediaStore
     * @param playlist A {@link Playlist} whose matching playlist will be removed from the MediaStore
     */
    public static void deletePlaylist(final Context context, final Playlist playlist){
        // Remove the playlist from the MediaStore
        int index = playlistLib.indexOf(playlist);

        context.getContentResolver().delete(
                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                MediaStore.Audio.Playlists._ID + "=?",
                new String[]{playlist.playlistId + ""});

        // If the playlist is an AutoPlaylist, make sure to delete its configuration
        if (playlist instanceof AutoPlaylist) {
            //noinspection ResultOfMethodCallIgnored
            new File(context.getExternalFilesDir(null) + "/"  + playlist.playlistName + AUTO_PLAYLIST_EXTENSION).delete();
        }

        // Update the playlist library & resort it
        //TODO - Refactor this logic - add observable
        playlistLib.clear();
        setPlaylistLib(scanPlaylists(context));
        Collections.sort(playlistLib);
        //notifyPlaylistRemoved (playlist,index);
    }

    /**
     * Append a song to the end of a playlist. Doesn't check for duplicates
     * @param context A {@link Context} to open a {@link Cursor}
     * @param playlist The {@link Playlist} to edit in the MediaStore
     * @param song The {@link Song} to be added to the playlist in the MediaStore
     */
    private static void addSongToEndOfPlaylist (final Context context, final Playlist playlist, final Song song){
        // Private method to add a song to a playlist
        // This method does the actual operation to the MediaStore
        Cursor cur = context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.playlistId),
                null, null, null,
                MediaStore.Audio.Playlists.Members.TRACK + " ASC");

        if (cur == null) {
            return;
        }

        long count = 0;
        if (cur.moveToLast()) {
            count = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.TRACK));
        }
        cur.close();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, count + 1);
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, song.songId);

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.playlistId);
        ContentResolver resolver = context.getContentResolver();
        resolver.insert(uri, values);
        resolver.notifyChange(Uri.parse("content://media"), null);
    }

    /**
     * Append a list of songs to the end of a playlist. Doesn't check for duplicates
     * @param context A {@link Context} to open a {@link Cursor}
     * @param playlist The {@link Playlist} to edit in the MediaStore
     * @param songs The {@link ArrayList} of {@link Song}s to be added to the playlist in the MediaStore
     */
    private static void addSongsToEndOfPlaylist(final Context context, final Playlist playlist, final ArrayList<Song> songs){
        // Private method to add a song to a playlist
        // This method does the actual operation to the MediaStore
        Cursor cur = context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.playlistId),
                null, null, null,
                MediaStore.Audio.Playlists.Members.TRACK + " ASC");

        if (cur == null) {
            return;
        }

        long count = 0;
        if (cur.moveToLast()) count = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Playlists.Members.TRACK));
        cur.close();

        ContentValues[] values = new ContentValues[songs.size()];
        for (int i = 0; i < songs.size(); i++) {
            values[i] = new ContentValues();
            values[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, count + 1);
            values[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songs.get(i).songId);
        }

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.playlistId);
        ContentResolver resolver = context.getContentResolver();
        resolver.bulkInsert(uri, values);
        resolver.notifyChange(Uri.parse("content://media"), null);
    }

    //
    //          AUTO PLAYLIST EDIT METHODS
    //

    /**
     * Add an {@link AutoPlaylist} to the library.
     * @param playlist the AutoPlaylist to be added to the library. The configuration of this
     *                 playlist will be saved so that it can be loaded when the library is next
     *                 rescanned, and a "stale" copy with current entries will be written in the
     *                 MediaStore so that other applications may access this playlist
     */
    public static void createAutoPlaylist(Context context, AutoPlaylist playlist) {
        try {
            // Add the playlist to the MediaStore
            Playlist p = makePlaylist(context, playlist.playlistName, playlist.generatePlaylist(context));

            // Assign the auto playlist's ID to match the one in the MediaStore
            playlist.playlistId = p.playlistId;

            // Save the playlist configuration with GSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter playlistWriter = new FileWriter(context.getExternalFilesDir(null) + "/" + playlist.playlistName + AUTO_PLAYLIST_EXTENSION);
            playlistWriter.write(gson.toJson(playlist, AutoPlaylist.class));
            playlistWriter.close();

            // Add the playlist to the library and resort the playlist library
            playlistLib.add(playlist);
            Collections.sort(playlistLib);
            //notifyPlaylistAdded(playlist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void editAutoPlaylist(Context context, AutoPlaylist playlist) {
        try {
            // Save the playlist configuration with GSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter playlistWriter = new FileWriter(context.getExternalFilesDir(null) + "/"  + playlist.playlistName + AUTO_PLAYLIST_EXTENSION);
            playlistWriter.write(gson.toJson(playlist, AutoPlaylist.class));
            playlistWriter.close();

            // Edit the contents of this playlist in the MediaStore
            editPlaylist(context, playlist, playlist.generatePlaylist(context));

            // Remove the old index of this playlist, but keep the Object for reference.
            // Since playlists are compared by Id's, this will remove the old index
            AutoPlaylist oldReference =
                    (AutoPlaylist) playlistLib.remove(playlistLib.indexOf(playlist));

            // If the user renamed the playlist, update it now
            if (!oldReference.playlistName.equals(playlist.playlistName)) {
                renamePlaylist(context, playlist.playlistId, playlist.playlistName);
                // Delete the old config file so that it doesn't reappear on restart
                //noinspection ResultOfMethodCallIgnored
                new File(context.getExternalFilesDir(null) + "/" +
                        oldReference.playlistName + AUTO_PLAYLIST_EXTENSION).delete();
            }

            // Add the playlist again. This makes sure that if the values have been cloned before
            // being changed that their values will be updated without having to rescan the
            // entire library
            playlistLib.add(playlist);

            Collections.sort(playlistLib);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    //
    //          Media file open method
    //

    /**
     * Get a list of songs to play for a certain input file. If a song is passed as the file, then
     * the list will include other songs in the same directory. If a playlist is passed as the file,
     * then the playlist will be opened as a regular playlist.
     *
     * @param activity An {@link Activity} to use when building the list
     * @param file The {@link File} which the list will be built around
     * @param type The MIME type of the file being opened
     * @param queue An {@link ArrayList} which will be populated with the {@link Song}s
     * @return The position that this list should be started from
     * @throws IOException
     */
    public static int getSongListFromFile(Activity activity, File file, String type, final ArrayList<Song> queue) throws IOException{
        // A somewhat convoluted method for getting a list of songs from a path

        // Songs are put into the queue array list
        // The integer returned is the position in this queue that corresponds to the requested song

        if (isEmpty()){
            // We depend on the library being scanned, so make sure it's scanned before we go any further
            scanAll(activity);
        }

        // PLAYLISTS
        if (type.equals("audio/x-mpegurl") || type.equals("audio/x-scpls") || type.equals("application/vnd.ms-wpl")){
            // If a playlist was opened, try to find and play its entry from the MediaStore
            Cursor cur = activity.getContentResolver().query(
                    MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Audio.Playlists.DATA + "=?",
                    new String[] {file.getPath()},
                    MediaStore.Audio.Playlists.NAME + " ASC");

            if (cur == null) {
                return 0;
            }

            // If the media store contains this playlist, play it like a regular playlist
            if (cur.getCount() > 0){
                cur.moveToFirst();
                queue.addAll(getPlaylistEntries(activity, new Playlist(
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Playlists._ID)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Playlists.NAME)))));
            }
            //TODO Attempt to manually read common playlist writing schemes
            /*else{
                // If the MediaStore doesn't contain this playlist, attempt to read it manually
                Scanner sc = new Scanner(file);
                ArrayList<String> lines = new ArrayList<>();
                while (sc.hasNextLine()) {
                    lines.add(sc.nextLine());
                }

                if (lines.size() > 0){
                    // Do stuff
                }

            }*/
            cur.close();
            // Return 0 to start at the beginning of the playlist
            return 0;
        }
        // ALL OTHER TYPES OF MEDIA
        else {
            // If the file isn't a playlist, use a content resolver to find the song and play it
            // Find all songs in the directory
            Cursor cur = activity.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Audio.Media.DATA + " like ?",
                    new String[] {"%" + file.getParent() + "/%"},
                    MediaStore.Audio.Media.DATA + " ASC");

            if (cur == null) {
                return 0;
            }

            // Create song objects to match those in the music library
            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                queue.add(new Song(
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                        (cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.ARTIST))),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))));
            }
            cur.close();

            // Find the position of the song that should be played
            for(int i = 0; i < queue.size(); i++){
                if (queue.get(i).location.equals(file.getPath())) return i;
            }
        }

        return 0;
    }
    //TODO - Refactor the scan song logic - make it observed
    public static void deleteTrack(Context context, Song song, boolean showToast){

        final long songId = song.songId;
        int index = songLib.indexOf(song);
        final String selection = MediaStore.Audio.Media._ID + "=?";
        try {
            JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
            jb.deleteFav(song.songId, 1);
            jb.deleteRecentSong(song.songId);
            jb.deleteMostPlayed(song.songId);
            jb.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        final String[] argument = new String[]{String.valueOf(songId)};
        final Cursor c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                songProjection, selection,
                argument, null);

        if(c!=null) {

            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    selection, argument);

            //Step 3 remove selected track from device
            c.moveToFirst();
            while (!c.isAfterLast()) {
                final String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                final String ame = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));


                try {
                    final File f = new File(name);
                    if (!f.delete()) {
                        Log.e("Library", "failed to delete file " + name);
                        if(showToast)
                            Toast.makeText(context, "Failed to delete " + ame , Toast.LENGTH_LONG).show();

                    }
                    else{
                        if(showToast)
                            Toast.makeText(context, ame + " deleted", Toast.LENGTH_LONG).show();
                    }
                    c.moveToNext();
                } catch (final SecurityException e) {
                    c.moveToNext();
                }
            }

            c.close();
        }


        context.getContentResolver().notifyChange(Uri.parse("content://media"), null);

        deleteTrackFromPlayQueue(song, context);


        songLib.clear();
        setSongLib(scanSongs(context));
        //Collections.sort(songLib);
        //notifySongRemoved(song, index);
        //notifyLibraryRefreshed();



    }

    public static void deleteTrackIndividually(Context context, Song song){

        final long songId = song.songId;
        int index = songLib.indexOf(song);
        final String selection = MediaStore.Audio.Media._ID + "=?";

        //TODO
        deleteSongFromJukebox(song, context);

        final String[] argument = new String[]{String.valueOf(songId)};
        final Cursor c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                songProjection, selection,
                argument, null);

        if(c!=null) {

            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    selection, argument);

            //Step 3 remove selected track from device
            c.moveToFirst();
            while (!c.isAfterLast()) {
                final String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                final File f = new File(name);
                try {
                    if (!f.delete()) {
                        Log.e("Library", "failed to delete file " + name);
                    }
                    c.moveToNext();
                } catch (final SecurityException e) {
                    c.moveToNext();
                }
            }

            c.close();
        }


        context.getContentResolver().notifyChange(Uri.parse("content://media"), null);

        //TODO Put it elsewhere
        deleteTrackFromPlayQueue(song, context);

        songLib.clear();
        setSongLib(scanSongs(context));
        //Collections.sort(songLib);
        //notifySongRemoved(song, index);
        //notifyLibraryRefreshed();

    }

    public static void deleteSongFromJukebox(Song song, Context context){
        try {
            JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
            jb.deleteFav(song.songId, 1);
            jb.deleteRecentSong(song.songId);
            jb.deleteMostPlayed(song.songId);
            jb.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //TODO - refactor this function -- remove Player controller
    public static void deleteTrackFromPlayQueue(Song song, Context context){
        if (PlayerController.getQueue().contains(song)){
            List<Song> queue = PlayerController.getQueue();
            int pos = PlayerController.getQueuePosition();
            int i = PlayerController.getQueue().indexOf(song);
            queue.remove(i);
            //if(!queue.isEmpty()) {
            PlayerController.editQueue(
                    queue,
                    (pos > i)
                            ? pos - 1
                            : pos);

            if (pos == i) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PlayerController.begin();
                    }
                });
            }
            //}else{
            //   PlayerController.setQueue(queue, -1);
            //}
        }

    }

    public static void deleteSongList(Context context, ArrayList<Song> songList){
        for (Song s: songList) {
            deleteTrackIndividually(context, s);
        }

    }


    public static void deleteAlbum(final Context context, final Album album, final boolean showToast){


        final int index = albumLib.indexOf(album);
        final ArrayList<Song> songList = getAlbumEntries(album);
        final String[] args = new String[]{String.valueOf(album.albumId)};
        final String select = MediaStore.Audio.Albums._ID+"=?";

        final Cursor c = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                albumArtistProjection,
                select,
                args,
                null);

        (new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                deleteSongList(context, songList);
                if(c!=null){
                    try {
                        context.getContentResolver().delete(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                                select,
                                args);

                        JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
                        jb.deleteFavAlbum(album.albumId);
                        jb.close();
                    }
                    catch (Exception e){

                        //if(showToast)
                        //Toast.makeText(context, "Failed to delete "+ album.albumName, Toast.LENGTH_LONG).show();
                    }
                    c.close();

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(showToast) {
                    Toast.makeText(context, "Album " + album.albumName + " deleted", Toast.LENGTH_LONG).show();
                }
                albumLib.clear();
                setAlbumLib(scanAlbums(context));
                Collections.sort(albumLib);
                //notifyAlbumRemoved(album, index);
                super.onPostExecute(aVoid);
            }
        }).execute();
    }


    //TODO - Modify this function - separate UI and data
    public static void setRingtone(final Context context, final long id){
        if(Permissions.hasWriteSettingsPermission(context)) {
            final ContentResolver resolver = context.getContentResolver();
            final Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
            try {
                final ContentValues values = new ContentValues(2);
                values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, "1");
                values.put(MediaStore.Audio.AudioColumns.IS_ALARM, "1");
                resolver.update(uri, values, null, null);

            } catch (final UnsupportedOperationException ignored) {
                Toast.makeText(context, "Failed to set ringtone", Toast.LENGTH_LONG).show();

                return;

            }
            final String selection = MediaStore.Audio.Media._ID + "=?";
            final String[] argument = new String[]{String.valueOf(id)};
            final Cursor c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    songProjection, selection,
                    argument, null);
            try {
                if (c != null && c.getCount() == 1) {
                    c.moveToFirst();
                    String s = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    Settings.System.putString(resolver, Settings.System.RINGTONE, uri.toString());
                    Toast.makeText(context, s + " set as ringtone", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Failed to set ringtone", Toast.LENGTH_LONG).show();

                }
            } finally {
                if (c != null) {
                    c.close();
                }

            }
        }/*else if(!previouslyRequestedWriteSettingsPermission((Activity)context)){
            requestWriteSettingsPermission((Activity)context);
        }*/
        else{
            //TODO - remove this part
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setMessage("You need to grant permission for this!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + context.getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try{
                                context.startActivity(intent);
                            }catch (Exception e){
                                Crashlytics.logException(e);
                            }
                        }
                    })
                    .show();
            Themes.themeAlertDialog(alertDialog);

        }
    }


    public static int getIndexofAlbum(long albumId){
        int i=-1;

        for(Album a : albumLib){
            if(a.albumId == albumId) {
                i = albumLib.indexOf(a);
                break;
            }

        }
        return i;

    }

    public static int getArtistIndex(long artistId){
        int i=-1;
        for(Artist a: artistLib){
            if(a.artistId == artistId){
                i = artistLib.indexOf(a);
                break;
            }

        }
        return i;
    }

    public static int getGenreIndex(long genreId){
        int i =-1;
        for(Genre g : genreLib){
            if(g.genreId == genreId){
                i = genreLib.indexOf(g);
                break;
            }
        }
        return i;
    }

    public static int getSongIndex(long songId){
        int i =-1;
        for(Song s : songLib){
            if(s.songId == songId){
                i = songLib.indexOf(s);
                break;
            }
        }
        return i;
    }


    public static Song getSongWithId(long songId){
        for(Song s : songLib){
            if(s.songId == songId){
                return s;
            }
        }
        return null;

    }

    public static int getIndexOfSong(Song s){
        if(s!=null && songLib.contains(s))
            return songLib.indexOf(s);
        return -1;
    }

    public static Song getSongAtIndex(int i){
        if(i>0 && i<songLib.size())
            return songLib.get(i);
        return null;
    }

    public static Album getAlbumAtIndex(int i){
        if(i>0 && i<albumLib.size())
            return albumLib.get(i);
        return null;
    }



    public static ArrayList<Song> recentlyAddedSongs(Context context){
        ArrayList<Song> recent = new ArrayList<Song>();
        Cursor cur = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                songProjection,
                null,
                null,
                MediaStore.Audio.Media.DATE_ADDED + " DESC"
        );

        Set<String> exFolders = Prefs.getExcludedFolders(context);

        if (cur != null) {


            cur.moveToFirst();
            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                Song s = new Song(
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
                );

                File music = new File(s.location);
                String path = "";
                if(music.exists()){
                    path = music.getParent();
                }

                if(!exFolders.contains(path)){
                    recent.add(s);
                }

            }
            cur.close();
        }

        return recent;
    }

    public static void recentSongsBySection(Context context){
        seven.clear();
        month.clear();
        Cursor cur = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                songProjection,
                null,
                null,
                MediaStore.Audio.Media.DATE_ADDED + " DESC"
        );

        if (cur != null) {


            cur.moveToFirst();
            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                Song s = new Song(
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
                );
                if(System.currentTimeMillis()/1000 - s.dateAdded <= SEVEN){
                    seven.add(s);
                }else if(System.currentTimeMillis()/1000 - s.dateAdded <= MONTH){
                    month.add(s);
                }else{
                    break;
                }

            }
            cur.close();
        }
        //Toast.makeText(context, seven.size() + "   " + month.size(), Toast.LENGTH_LONG).show();

    }





    public static ArrayList<Album> recentlyAddedAlbums(Context context){
        ArrayList<Album> recent = new ArrayList<Album>();
        ArrayList<Long> idlist =new ArrayList<>();
        Cursor cur = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                songProjection,
                null,
                null,
                MediaStore.Audio.Media.DATE_ADDED + " DESC"
        );

        if (cur != null) {

            Set<String> exFolders = Prefs.getExcludedFolders(context);


            cur.moveToFirst();

            for(int i=0; i<cur.getCount(); i++){
                cur.moveToPosition(i);
                Song s = new Song(
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
                );
                File music = new File(s.location);
                String path = "";

                if(music.exists()){
                    path = music.getParent();
                }
                if(!exFolders.contains(path)) {
                    if (idlist.size() < 6) {
                        if (!idlist.contains(s.albumId)) {
                            idlist.add(s.albumId);
                        }
                    } else {
                        break;
                    }
                }

            }
            cur.close();
        }

        if(!idlist.isEmpty()) {

            String query = MediaStore.Audio.Albums._ID + " IN(?";
            String[] ids = new String[idlist.size()];
            ids[0] = Long.toString(idlist.get(0));

            for (int i = 1; i < idlist.size(); i++) {
                query += ",?";
                ids[i] = Long.toString(idlist.get(i));

            }
            query += ")";

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    albumArtistProjection,
                    query,
                    ids,
                    null);

            if (cursor == null) {
                return recent;
            }

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                recent.add(new Album(
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.LAST_YEAR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART))
                ));
            }
            cursor.close();
        }



        return recent;
    }




    public static ArrayList<Song> getFavSongs(final Context context) {
        ArrayList<Song> favs = new ArrayList<>();
        JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
        ArrayList<Long> id = jb.getFavs(1);

        if(!id.isEmpty()) {

            String query = MediaStore.Audio.Media._ID + " IN(?";
            String[] ids = new String[id.size()];
            ids[0] = Long.toString(id.get(0));

            for (int i = 1; i < id.size(); i++) {
                query += ",?";
                ids[i] = Long.toString(id.get(i));

            }
            query += ")";

            Cursor cur = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    songProjection,
                    query,
                    ids,
                    MediaStore.Audio.Media.TITLE + " ASC");

            if (cur == null) {
                return favs;
            }

            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                Song s = new Song(
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
                );

                favs.add(s);
            }
            cur.close();
        }

        return favs;
    }

    public static ArrayList<Album> getRecentSongs(final Context context) {
        ArrayList<Album> recent = new ArrayList<>();
        JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
        ArrayList<Long> id = jb.getRecentSongs();

        String query = MediaStore.Audio.Albums._ID + " IN(?";

        if(!id.isEmpty()) {
            String[] ids = new String[id.size()];
            ids[0] = Long.toString(id.get(0));

            for (int i = 1; i < id.size(); i++) {
                query += ",?";
                ids[i] = Long.toString(id.get(i));

            }
            query += ")";

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    albumArtistProjection,
                    query,
                    ids,
                    null);

            if (cursor != null && cursor.moveToFirst()) {


                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    Album a = new Album(
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.LAST_YEAR)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART))
                    );
                    if(incAlbums.contains(a.albumId)){
                        recent.add(a);
                    }
                }
                cursor.close();
            }

            ArrayList<Album> albums = new ArrayList<>();
            for (long i : id) {
                for (Album a : recent) {
                    if (a.albumId == i) {
                        albums.add(a);
                        break;
                    }
                }
            }

            return albums;
        }
        else
            return recent;
    }

    public static void trimDatabases(final Context context){
        JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
        jb.trimRecentlyPlayed(40);
        jb.trimMostPlayed(40);

    }

    public static ArrayList<Song> mostPlayedSongs(final  Context context){
        ArrayList<Song> most = new ArrayList<>();
        JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
        ArrayList<Long> id = jb.getMostPlayed(30);

        String query = MediaStore.Audio.Media._ID + " IN(?";

        if(!id.isEmpty()) {
            String[] ids = new String[id.size()];
            ids[0] = Long.toString(id.get(0));

            for (int i = 1; i < id.size(); i++) {
                query += ",?";
                ids[i] = Long.toString(id.get(i));

            }
            query += ")";

            Cursor cur = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    songProjection,
                    query,
                    ids,
                    null);

            if (cur != null && cur.moveToFirst()) {
                Set<String> exFolders = Prefs.getExcludedFolders(context);


                for (int i = 0; i < cur.getCount(); i++) {
                    cur.moveToPosition(i);
                    Song s = new Song(
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                            cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                            cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                            cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)),
                            cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                            cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                            cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
                    );

                    File f = new File(s.location);
                    String path = "";
                    if(f.exists()){
                        path = f.getParent();
                    }
                    if(!exFolders.contains(path)){
                        most.add(s);
                    }
                }
                cur.close();

                ArrayList<Song> songs = new ArrayList<>();
                for (long i : id) {
                    for (Song a : most) {
                        if (a.songId == i) {
                            songs.add(a);
                            break;
                        }
                    }
                }
                return songs;


            }

        }

        return most;
    }
    public static ArrayList<Song> recentlyPlayedSongs(final Context context) {
        ArrayList<Song> recent = new ArrayList<>();
        JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
        ArrayList<Long> id = jb.getRecentPlayedSongs();

        String query = MediaStore.Audio.Media._ID + " IN(?";

        if(!id.isEmpty()) {
            String[] ids = new String[id.size()];
            ids[0] = Long.toString(id.get(0));

            for (int i = 1; i < id.size(); i++) {
                query += ",?";
                ids[i] = Long.toString(id.get(i));

            }
            query += ")";

            Cursor cur = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    songProjection,
                    query,
                    ids,
                    null);

            if (cur != null && cur.moveToFirst()) {

                Set<String> exFolders = Prefs.getExcludedFolders(context);


                for (int i = 0; i < cur.getCount(); i++) {
                    cur.moveToPosition(i);
                    Song s = new Song(
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                            cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                            cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                            cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)),
                            cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                            cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                            cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
                    );
                    File f = new File(s.location);
                    String path = "";
                    if(f.exists()){
                        path = f.getParent();
                    }
                    if(!exFolders.contains(path)){
                        recent.add(s);
                    }
                }
                cur.close();

                ArrayList<Song> songs = new ArrayList<>();
                for (long i : id) {
                    for (Song a : recent) {
                        if (a.songId == i) {
                            songs.add(a);
                            break;
                        }
                    }
                }
                return songs;


            }

        }
        return recent;
    }

    public boolean isFavAlbum(final Context context, final long albumId){
        JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
        ArrayList<Long> id = jb.getFavAlbums(2);
        if(!id.isEmpty() && id.contains(albumId)){
            return true;
        }
        return false;
    }


    public static ArrayList<Album> getFavAlbums(final Context context) {
        ArrayList<Album> recent = new ArrayList<>();
        JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
        ArrayList<Long> id = jb.getFavAlbums(2);

        String query = MediaStore.Audio.Albums._ID + " IN(?";

        if(!id.isEmpty()) {
            String[] ids = new String[id.size()];
            ids[0] = Long.toString(id.get(0));

            for (int i = 1; i < id.size(); i++) {
                query += ",?";
                ids[i] = Long.toString(id.get(i));

            }
            query += ")";

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    albumArtistProjection,
                    query,
                    ids,
                    null);

            if (cursor != null) {


                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    recent.add(new Album(
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.LAST_YEAR)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART))
                    ));
                }
                cursor.close();
            }



        }
        return recent;
    }

    public static ArrayList<Song> getFavAlbumSongs(Context context){
        ArrayList<Song> songs = new ArrayList<>();
        JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
        ArrayList<Long> id = jb.getFavAlbums(2);

        if(!id.isEmpty()) {

            String query = MediaStore.Audio.Media.ALBUM_ID + " IN(?";
            String[] ids = new String[id.size()];
            ids[0] = Long.toString(id.get(0));

            for (int i = 1; i < id.size(); i++) {
                query += ",?";
                ids[i] = Long.toString(id.get(i));

            }
            query += ")";

            Cursor cur = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    songProjection,
                    query,
                    ids,
                    MediaStore.Audio.Media.TITLE + " ASC");

            if (cur == null) {
                return new ArrayList<>();
            }

            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                Song s = new Song(
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
                );

                songs.add(s);
            }
            cur.close();
        }

        return songs;

    }


    /*


    public static ArrayList<Song> getFavArtistSongs(Context context){
        ArrayList<Song> songs = new ArrayList<>();
        JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
        ArrayList<Long> id = jb.getFavArtists(3);

        if(!id.isEmpty()) {

            String query = MediaStore.Audio.Media.ARTIST_ID + " IN(?";
            String[] ids = new String[id.size()];
            ids[0] = Long.toString(id.get(0));

            for (int i = 1; i < id.size(); i++) {
                query += ",?";
                ids[i] = Long.toString(id.get(i));

            }
            query += ")";

            Cursor cur = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    songProjection,
                    query,
                    ids,
                    MediaStore.Audio.Media.TITLE + " ASC");

            if (cur == null) {
                return new ArrayList<>();
            }

            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                Song s = new Song(
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                        cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
                );

                songs.add(s);
            }
            cur.close();
        }

        return songs;

    }

    */


    public static ArrayList<Song> getSuggestedSongs(Context context){
        ArrayList<Song> suggested = new ArrayList<>();
        ArrayList<Song> albums = getFavAlbumSongs(context);
        //ArrayList<Song> artists = getFavArtistSongs(context);
        ArrayList<Song> favs = getFavSongs(context);
        ArrayList<Song> most = mostPlayedSongs(context);


        try {

            for (Song s : albums) {
                if (!suggested.contains(s))
                    suggested.add(s);
            }

            for (Song s : most) {
                if (!suggested.contains(s))
                    suggested.add(s);
            }

            for (Song s : favs) {
                if (!suggested.contains(s))
                    suggested.add(s);
            }

            //long seed = System.nanoTime();
            Collections.shuffle(suggested);
        }
        catch (Exception e){

            e.printStackTrace();
            //return new ArrayList<>();
        }




        return suggested;
    }





    public static String getGenreWithId(long id, Context context){

        Cursor cur = context.getContentResolver().query(
                MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                genreProjection,
                null,
                null,
                MediaStore.Audio.Genres.NAME + " ASC");

        if (cur == null) {
            return null;
        }

        for (int i = 0; i < cur.getCount(); i++) {
            cur.moveToPosition(i);
            if(id == cur.getInt(cur.getColumnIndex(MediaStore.Audio.Genres._ID))){
                String name = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME));
                cur.close();
                return (name==null || name.isEmpty())?"Unknown":name;
            }
        }
        cur.close();
        return "";

    }

    public static void deleteFolderItems(final Context context, final ArrayList<Song> songlist,
                                         final MusicFolder folder /*, final FoldersActivity.FoldersAdapter adapter*/){
        (new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                deleteSongList(context, songlist);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //adapter.remove(folder);
                folders.remove(folder);
            }
        }).execute();
    }


    public static void deleteEntireFolder(final Context context,
                                          final MusicFolder folder /*, final FoldersActivity.FoldersAdapter adapter*/){

        final ArrayList<Song> songsInFolder = getSongsByFolder(folder.path);

        //TODO - remove this part

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Delete folder " + getFolderName(folder.path))
                .setMessage("This action can't be undone. Still want to proceed?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteFolderItems(context, songsInFolder, folder);
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
        Themes.themeAlertDialog(dialog);


    }

    public static void parseAllMusicFolders(){
        if(allFolders!=null && allFolders.size()>0) {
            foldernames = new String[allFolders.size()];
            folderpaths = new String[allFolders.size()];
            int cnt = 0;
            for (String str : allFolders) {
                String name = getFolderName(str);
                try {
                    folderpaths[cnt] = str;
                    foldernames[cnt] = name;
                    cnt++;
                } catch (Exception e) {

                }
            }
        }
    }

    public static void scanMusicFolders(Context context){
        folders.clear();
        ArrayList<String> dirlist = new ArrayList<>();
        Set<String> set = new LinkedHashSet<String>();


        for (Song s: songLib) {
            File music = new File(s.location);
            if(music.exists()){
                String path = music.getParent();
                dirlist.add(path);
                set.add(path);
            }

        }

        for(String str : set){
            String name = getFolderName(str);
            int numSongs = Collections.frequency(dirlist, str);
            folders.add(
                    new MusicFolder(name, str, (numSongs+ " " +((numSongs>1)?"songs":"song")), numSongs)
            );
        }
        int sort = Prefs.getFolderSortOrder(context);
        if(sort==0) {
            Collections.sort(folders, MusicFolder.DISPLAY_NAME_COMPARATOR);
        }else if(sort==1){
            Collections.sort(folders, MusicFolder.COUNT_COMPARATOR_ASC);
        }else if(sort==2){
            Collections.sort(folders, MusicFolder.COUNT_COMPARATOR_DESC);
        }else{
            Collections.sort(folders, MusicFolder.PATH_NAME_COMPARATOR);
        }

    }

    public static ArrayList<MusicFolder> getAllMusicFolders(Context context){
        return folders;
    }

    public static String[] getFolderPaths(){
        return folderpaths;
    }

    public static String[] getFoldernames(){
        return foldernames;
    }

    public static ArrayList<Song> getSongsByFolder(String path){
        ArrayList<Song> songs = new ArrayList<>();

        for(Song s: songLib){
            File m = new File(s.location);
            if(((String)m.getParent()).equals(path)){
                songs.add(s);
            }
        }
        Collections.sort(songs);

        return songs;
    }

    public static String getFolderName (String path){
        File f = new File(path);
        if(f.exists() && f.isDirectory()){
            return f.getName();
        }

        return null;
    }

    public static String getMetaData (String path){
        String metaData = "";
        MediaMetadataRetriever md = new MediaMetadataRetriever();
        md.setDataSource(path);
        try {
            String bitrate = md.extractMetadata((MediaMetadataRetriever.METADATA_KEY_BITRATE)/1000);
            Long bit = Long.parseLong(bitrate)/1000;
            String artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String albumartist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
            String album = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String composer = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
            String sampling = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE);
            String duration = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Double time = Double.parseDouble(duration) / (1000.0*60*0);
            String length = getFileSize(path);
            String track = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
            String title = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

            metaData =
                    "\n\n\u2022 STitle :         " + title + "\n\n"
                            +"\u2022 File Path :     " + path + "\n\n"
                            +"\u2022 Album :         " + album + "\n\n"
                            +"\u2022 Artist :        " + artist + "\n\n"
                            +"• Composer :      " + composer + "\n\n"
                            +"• Track :         " + track + "\n\n"
                            +"• Duration :      " + String.valueOf(time) + " s\n\n"
                            +"• File Size :     " + length + "\n\n"
                            +"• Bitrate :       " + String.valueOf(bit) +" kb/s \n\n"
                            +"• Sampling rate : " + sampling +" HZ \n\n";


        }
        catch (Exception e){
            e.printStackTrace();
        }

        return metaData;
    }

    public static String getFileSize(String path){
        File f = new File(path);
        double siz = f.length()/(1024.0*1024.0);
        siz = (double) Math.round(siz*100.0)/100.0;
        String mb = siz + " mb";

        return mb;

    }

    /*public static void showMediaDetails(Context context,Song song){
        String metadata = getMetaData(song.location);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Details")
                .setMessage(metadata)
                .setNegativeButton("OK", null)
                .show();
        Themes.themeAlertDialog(dialog);

    }*/

    public static String getArtistNameById(Context context, long artistId){
        String selection = MediaStore.Audio.Artists._ID + " =?";
        String name="";
        String[] args = new String[]{String.valueOf(artistId)};
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                artistProjection,
                selection,args,
                null);
        if(cursor!=null && cursor.getCount()==1){
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST));
            cursor.close();
        }
        return name;
    }
    /*
    New Playing queue implementation
    */

    public static void setPlayingQueue(ArrayList<Song> queue){
        playingQueue.clear();
        playingQueue.addAll(queue);
    }

    public static ArrayList<Song> getPlayingQueue(){
        return playingQueue;
    }

    public static void appendPlayingQueue(ArrayList<Song> songs){
        playingQueue.addAll(songs);
    }

    public static void appendOnePlayingQueue(Song song){
        playingQueue.add(song);
    }

    public static void clearPlayingQueue(){
        playingQueue.clear();
    }

    public static void removeOneFromPlayingQueue(Song song){
        playingQueue.remove(song);
    }

}
