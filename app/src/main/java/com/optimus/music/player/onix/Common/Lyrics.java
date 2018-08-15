package com.optimus.music.player.onix.Common;

import android.os.AsyncTask;

import com.optimus.music.player.onix.Common.Instances.Song;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by apricot on 18/6/16.
 */
public class Lyrics {

    public static String encodeField(String in){
        return java.net.URLEncoder.encode(in.replace(' ', '_'));
    }


}
