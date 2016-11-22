package com.optimus.music.player.onix.Common.Instances;

/**
 * Created by aribhatt on 11/5/2016.
 */

public class ArtistArt {
    String url;
    int[] cols;

    public ArtistArt(String url, int[] cols){
        this.url=url;
        this.cols=cols;
    }

    public void setUrl(String url){
        this.url=url;
    }

    public void setCols(int[] cols){
        this.cols = cols;
    }

    public String getUrl(){
        return url;
    }

    public int[] getCols(){
        return cols;
    }
}
