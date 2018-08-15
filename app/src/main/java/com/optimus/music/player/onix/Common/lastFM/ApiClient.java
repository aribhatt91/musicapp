package com.optimus.music.player.onix.Common.lastFM;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by longclaw on 3/10/16.
 */
public class ApiClient {

    public static final String BASE_URL = "http://ws.audioscrobbler.com/2.0/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(){
        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
