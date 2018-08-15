package com.optimus.music.player.onix.Common.lastFM;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class Cache {

    private static final String CACHE_EXTENSION = ".lfm";
    private static boolean initialized = false;
    static String resultpath = "FAILED";

    /**
     * This class is never instantiated
     */
    private Cache() {

    }

    private static void initializeCache(Context context) {
        try {
            //noinspection ResultOfMethodCallIgnored
            new File(context.getExternalCacheDir() + "/lastFM/").mkdirs();
            initialized = true;
        }catch (Exception e){
            initialized = false;
        }
    }

    private static String getItemPath(Context context, long id) {
        try {
            if (!initialized) {
                initializeCache(context);
            }
            return context.getExternalCacheDir() + "/lastFM/" + id + CACHE_EXTENSION;
        }catch (Exception e){
            return "";
        }
    }

    public static boolean hasItem(Context context, long id) {
        try {
            File reference = new File(getItemPath(context, id));
            return reference.exists()
                    && System.currentTimeMillis() - reference.lastModified() < 7 * 24 * 60 * 60 * 1000;
        }catch (Exception e){
            return false;
        }
    }

    protected static void cacheArtist(Context context, long artistId, LArtist artist) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter cacheWriter = new FileWriter(getItemPath(context, artistId));
            cacheWriter.write(gson.toJson(artist, LArtist.class));
            cacheWriter.close();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    protected static LArtist getCachedArtist(Context context, long artistId) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(new FileReader(getItemPath(context, artistId)), LArtist.class);
        } catch (Exception e) {
            Crashlytics.logException(e);
            return null;
        }
    }

    public static boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            return true;
        }
        return false;
    }

    public static String getAlbumArtStorageDir(){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Onix");
        if(file.exists()){
            return file.getAbsolutePath();
        }else if(!file.mkdirs())
            return null;
        return file.getAbsolutePath();
    }

    public static boolean deleteCache(Context context){
        boolean success = false;
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()){
                success = deleteDir(dir);
            }
            return success;
        }catch (Exception e){
            return false;
        }
    }

    public static boolean deleteDir(File dir){
        if(dir != null){
            if(dir.isDirectory()) {
                try {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++) {
                        boolean success = deleteDir(new File(dir, children[i]));
                        if (!success)
                            return false;
                    }
                    return dir.delete();
                } catch (Exception e) {
                    return false;
                }
            }else if(dir.isFile()){
                try {
                    return dir.delete();
                }catch (Exception e){
                    return false;
                }
            }
        }
        return false;
    }



}