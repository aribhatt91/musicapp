package com.optimus.music.player.onix.model;

/**
 * Created by apricot on 27/10/15.
 */
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Album implements Parcelable, Comparable<Album> {

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public long albumId;
    public String albumName;
    public int numberOfSongs;
    public String artistName;
    public int year;
    public String artUri;
    public long artistId;

    public Album(){

    }

    public Album(final long albumId, final String albumName, final int numberOfSongs, final String artistName, final int year, final String artUri) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.numberOfSongs= numberOfSongs;
        this.artistName = artistName;
        this.year = year;
        this.artUri = artUri;
    }

    private Album(Parcel in) {
        albumId = in.readInt();
        albumName = in.readString();
        numberOfSongs = in.readInt();
        artistName = in.readString();
        year = in.readInt();
        artUri = in.readString();
    }


    public static List<Album> buildAlbumList(Cursor cur, Resources res) {
        List<Album> albums = new ArrayList<>(cur.getCount());

        final int idIndex = cur.getColumnIndex(MediaStore.Audio.Albums._ID);
        final int albumIndex = cur.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
        final int artistIndex = cur.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
        final int artistIdIndex = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
        final int yearIndex = cur.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR);
        final int artIndex = cur.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);

        final String unknownAlbum = res.getString(R.string.unknown_album);
        final String unknownArtist = res.getString(R.string.unknown_artist);

        for (int i = 0; i < cur.getCount(); i++) {
            cur.moveToPosition(i);
            Album next = new Album();
            next.albumId = cur.getLong(idIndex);
            next.albumName = cur.getString(albumIndex);
            next.artistName = cur.getString(artistIndex);
            next.artistId = cur.getLong(artistIdIndex);
            next.year = cur.getInt(yearIndex);
            next.artUri = cur.getString(artIndex);

            albums.add(next);
        }

        return albums;
    }


    public long getAlbumId() {
        return albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public long getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getYear() {
        return year;
    }

    public String getArtUri() {
        return artUri;
    }

    @Override
    public int hashCode() {
        return Util.hashLong(albumId);
    }


    @Override
    public boolean equals(final Object obj) {
        return this == obj ||
                (obj != null && obj instanceof Album && albumId == ((Album) obj).albumId);
    }

    public String toString() {
        return albumName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(albumId);
        dest.writeString(albumName);
        dest.writeInt(numberOfSongs);
        dest.writeString(artistName);
        dest.writeInt(year);
        dest.writeString(artUri);
    }

    @Override
    public int compareTo(@NonNull Album another) {
        String o1c = (albumName == null)
                ? ""
                : albumName.toLowerCase(Locale.ENGLISH);
        String o2c = (another.albumName == null)
                ? ""
                : another.albumName.toLowerCase(Locale.ENGLISH);
        if (o1c.startsWith("the ")) {
            o1c = o1c.substring(4);
        } else if (o1c.startsWith("a ")) {
            o1c = o1c.substring(2);
        }
        if (o2c.startsWith("the ")) {
            o2c = o2c.substring(4);
        } else if (o2c.startsWith("a ")) {
            o2c = o2c.substring(2);
        }
        return o1c.compareTo(o2c);
    }
}
