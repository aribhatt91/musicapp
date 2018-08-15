package com.optimus.music.player.onix.Common.lastFM;

/**
 * Created by apricot on 9/11/15.
 */
import com.google.gson.annotations.SerializedName;

public class Tag {

    @SerializedName("name")
    protected String name;
    @SerializedName("url")
    protected String url;

    protected Tag() {

    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}