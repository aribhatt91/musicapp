package com.optimus.music.player.onix.data.legacyDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class JukeBoxDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "jukebox.db";
    private static final int DB_VERSION = 1;

    public static final String FAVS = "FAVOURITE";
    public static final String FAV_ALBUM = "FAV_ALBUM";
    public static final String FAV_ARTIST = "FAV_ARTIST";
    public static final String FAV_GENRE = "FAV_GENRE";
    public static final String RECENT_SONG = "RECENT_SONG";
    public static final String MOST_PLAYED = "MOST_PLAYED";


    //JukeBox mood playlists
    public static final String PARTY = "PARTY";
    public static final String GYM = "GYM";//BEASTMODE
    public static final String LONELY = "LONELY";// TAKE A BREAK

    public static final String HAPPY = "HAPPY";
    public static final String SAD = "SAD";//FEELIN BLUE
    public static final String CHILL = "CHILL";
    public static final String LOVE = "LOVE";// I M IN LOVE
    public static final String BREAKUP = "BREAKUP";
    public static final String TRAVEL = "TRAVEL";


    public static final String ID = "ID";
    public static final String ALBUMID = "ALBUMID";
    public static final String PRIMID = "PRIMID";
    public static final String TYPE = "TYPE";
    public static final String CREATED_AT = "CREATED_AT";
    public static final String NUM = "NUM";

    //public static final String CREATE_DB =

    private static final String CREATE_FAVS =
            "CREATE TABLE " + FAVS + " ( "
            + ID + " LONG PRIMARY KEY, "
            + TYPE + " INTEGER "
            + " )";

    private static final String CREATE_MOST_PLAYED =
            "CREATE TABLE " + MOST_PLAYED + " ( "
                    + ID + " LONG PRIMARY KEY, "
                    + ALBUMID + " LONG, "
                    + NUM + " LONG "
                    + " )";

    private static final String CREATE_FAV_ALBUM =
            "CREATE TABLE " + FAV_ALBUM + " ( "
                    + ID + " LONG PRIMARY KEY, "
                    + TYPE + " INTEGER "
                    + " )";

    private static final String CREATE_FAV_ARTIST =
            "CREATE TABLE " + FAV_ARTIST + " ( "
                    + ID + " LONG PRIMARY KEY, "
                    + TYPE + " INTEGER "
                    + " )";

    private static final String CREATE_RECENT_SONG =
            "CREATE TABLE " + RECENT_SONG + " ( "
                    + ID + " LONG PRIMARY KEY, "
                    + ALBUMID + " LONG, "
                    + CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP "
                    + " )";



    public JukeBoxDBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVS);
        db.execSQL(CREATE_RECENT_SONG);

        db.execSQL(CREATE_FAV_ALBUM);
        db.execSQL(CREATE_FAV_ARTIST);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVS);
        db.execSQL("DROP TABLE IF EXISTS " + FAV_ALBUM);
        db.execSQL("DROP TABLE IF EXISTS " + FAV_ARTIST);
        db.execSQL("DROP TABLE IF EXISTS " + RECENT_SONG);

        onCreate(db);
    }

    public long insertFav(long id, int type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(TYPE, type);

        long res = db.insert(FAVS, null, cv);
        return res;
    }

    public long insertFavAlbum(long id, int type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(TYPE, type);

        long res = db.insert(FAV_ALBUM, null, cv);
        return res;
    }

    public long insertFavArtist(long id, int type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(TYPE, type);

        long res = db.insert(FAV_ARTIST, null, cv);
        return res;
    }

    public long insertRecentSong(long id, long albumid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        deleteRecentSong(id);

        cv.put(ID, id);
        cv.put(ALBUMID, albumid);
        cv.put(CREATED_AT, getDateTime());

        long res = db.insert(RECENT_SONG, null, cv);
        return res;
    }



    public ArrayList<Long> getFavs(int type){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Long> res = new ArrayList<>();
        String query = "SELECT * FROM " + FAVS + " WHERE TYPE = "+ "'"+ type + "'";
        Cursor c = db.rawQuery(query, null);
        if(c!=null && c.moveToFirst()){
            do{
                long i = c.getLong(c.getColumnIndexOrThrow(ID));
                res.add(i);

            }while (c.moveToNext());

            c.close();

        }


        return res;
    }

    public void updateMostPlayed(long songId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String query = "SELECT * FROM " + FAV_ARTIST + " WHERE ID = "+ "'"+ songId + "'";
        Cursor c = db.rawQuery(query, null);
        if(c!=null && c.getCount()==1 && c.moveToFirst()) {
            try {
                int count = c.getInt(c.getColumnIndexOrThrow(TYPE));
                count = count + 1;
                cv.put(TYPE, count);
                db.update(FAV_ARTIST, cv, "ID=" + songId, null);
                c.close();
            }catch (Exception e){

            }
        }else if(c!=null && c.getCount()==0){
            try {
                cv.put(ID, songId);
                cv.put(TYPE, 0);
                db.insert(FAV_ARTIST, null, cv);
            }catch (Exception e){

            }
        }


    }

    public ArrayList<Long> getMostPlayed(int type){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Long> res = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + FAV_ARTIST + " ORDER BY " + TYPE + " DESC";
            Cursor c = db.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                do {
                    long i = c.getLong(c.getColumnIndexOrThrow(ID));
                    res.add(i);

                    if(res.size()>type)
                        break;

                } while (c.moveToNext());

                c.close();
            }
        }catch (Exception e){

        }


        return res;
    }


    public ArrayList<Long> getFavAlbums(int type){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Long> res = new ArrayList<>();
        String query = "SELECT * FROM " + FAV_ALBUM + " WHERE TYPE = "+ "'"+ type + "'";
        Cursor c = db.rawQuery(query, null);
        if(c!=null && c.moveToFirst()){
            do{
                long i = c.getLong(c.getColumnIndexOrThrow(ID));
                res.add(i);

            }while (c.moveToNext());
            c.close();

        }


        return res;
    }






    public ArrayList<Long> getRecentSongs(){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Long> res = new ArrayList<>();
        String query = "SELECT DISTINCT " +ALBUMID+ " FROM " + RECENT_SONG + " ORDER BY "+ CREATED_AT + " DESC";
        Cursor c = db.rawQuery(query, null);
        if(c!=null && c.moveToFirst()){
            do{
                long i = c.getLong(c.getColumnIndexOrThrow(ALBUMID));
                res.add(i);

                if(res.size()>6)
                    break;

            }while (c.moveToNext());

            c.close();
        }
        return res;

    }



    public ArrayList<Long> getRecentPlayedSongs(){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Long> res = new ArrayList<>();
        String query = "SELECT DISTINCT " + ID + " FROM " + RECENT_SONG + " ORDER BY "+ CREATED_AT + " DESC";
        Cursor c = db.rawQuery(query, null);
        if(c!=null && c.moveToFirst()){
            do{
                long i = c.getLong(c.getColumnIndexOrThrow(ID));
                res.add(i);
                if(res.size()>30)
                    break;

            }while (c.moveToNext());

            c.close();
        }
        db.close();
        return res;

    }



    public void deleteFav(long id, int type){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(FAVS, ID + "=?", new String[]{String.valueOf(id)});
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteMostPlayed(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(FAV_ARTIST, ID + "=?", new String[]{String.valueOf(id)});
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteFavAlbum(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(FAV_ALBUM, ID + "=?", new String[]{String.valueOf(id)});
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteFavArtist(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(FAV_ARTIST, ID + "=?", new String[]{String.valueOf(id)});
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteRecentSong(long id){

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(RECENT_SONG, ID + "=?", new String[]{String.valueOf(id)});
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }




    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private int getCountMostPlayed(){
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT " + ID + " FROM " + FAV_ARTIST + " ORDER BY "+ TYPE + " ASC";
        Cursor c = db.rawQuery(query, null);
        if(c!=null) {
            count = c.getCount();
            c.close();
            db.close();
        }
        return count;

    }

    public void trimMostPlayed(int num){
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = getCountMostPlayed();
        if(rows>num) {
            try {
                String query = "delete from " + FAV_ARTIST +
                        " where " + ID + " in (select top " + " " + (rows - num) + " " + ID
                        + " from " + FAV_ARTIST + " order by + " + TYPE +  " ASC );";
                db.execSQL(query);
            }catch (Exception e){

            }

        }

    }

    public int getCountRecentPlayed(){
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        String query = "SELECT DISTINCT " + ID + " FROM " + RECENT_SONG + " ORDER BY "+ CREATED_AT + " DESC";
        Cursor c = db.rawQuery(query, null);
        if(c!=null) {
            count = c.getCount();
            c.close();
            db.close();
        }
        return count;

    }

    public void trimRecentlyPlayed(int num){
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = getCountRecentPlayed();
        if(rows>num) {
            try {
                String query = "delete from " + RECENT_SONG +
                        " where " + ID + " in (select top " + " " + (rows - num) + " " + ID
                        + " from " + RECENT_SONG + " order by + " + CREATED_AT +  " ASC );";
                db.execSQL(query);
            }catch (Exception e){

            }

        }

    }

}