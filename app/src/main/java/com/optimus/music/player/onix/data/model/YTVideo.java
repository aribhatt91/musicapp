package com.optimus.music.player.onix.model;

/**
 * Created by apricot on 23/3/16.
 */
public class YTVideo {
    public String url;
    public String imageURI;
    public String name;
    public String artist;
    public boolean isFav;

    public YTVideo(final String url, final String imageURI, final String name, final String artist, final boolean isFav){
        this.url = url;
        this.imageURI = imageURI;
        this.name = name;
        this.artist = artist;
        this.isFav = isFav;
    }
}
