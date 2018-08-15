package com.optimus.music.player.onix.data.model.lfm;

/**
 * Created by apricot on 15/5/16.
 */
import com.google.gson.annotations.SerializedName;

public class LAlbum {
    @SerializedName("name")
    protected String name;

    @SerializedName("artist")
    protected String artist;

    @SerializedName("mbid")
    protected String mbid;

    @SerializedName("url")
    protected String url;

    @SerializedName("releasedate")
    protected String releasedate;


    @SerializedName("toptags")
    protected Tag[] toptags;


    @SerializedName("images")
    protected ImageList images;

    protected LAlbum() {
        images = new ImageList();
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getDate() {
        return releasedate;
    }

    public Tag[] getTopTags() {
        return toptags;
    }


    public String getImageURL(byte size) {
        return images.getUrl(size);
    }

    public String getUrl() {
        return url;
    }
}
