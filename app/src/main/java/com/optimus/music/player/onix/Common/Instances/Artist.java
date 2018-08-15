package com.optimus.music.player.onix.Common.Instances;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Artist implements Parcelable, Comparable<Artist> {

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    public long artistId;
    public String artistName;
    public int numTracks;
    public int numAlbums;

    public Artist(){

    }

    public Artist(final long artistId, final String artistName, final int numTracks, final int numAlbums) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.numTracks = numTracks;
        this.numAlbums = numAlbums;
    }

    private Artist(Parcel in) {
        artistId = in.readInt();
        artistName = in.readString();
        numTracks = in.readInt();
        numAlbums = in.readInt();
    }

    /*
    public static List<Artist> buildArtistList(Cursor cur, Resources res) {
        List<Artist> artists = new ArrayList<>(cur.getCount());

        final int idIndex = cur.getColumnIndex(MediaStore.Audio.Artists._ID);
        final int artistIndex = cur.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
        final int tracksIndex = cur.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
        final int albumsINdex = cur.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);

        final String unknownName = res.getString(R.string.unknown_artist);

        for (int i = 0; i < cur.getCount(); i++) {
            cur.moveToPosition(i);
            Artist next = new Artist();
            next.artistId = cur.getLong(idIndex);
            next.artistName = Library.parseUnknown(cur.getString(artistIndex), unknownName);
            next.numTracks = cur.getInt(tracksIndex);
            next.numAlbums = cur.getInt(albumsINdex);


            artists.add(next);
        }

        return artists;
    }

    public long getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }
    */




    @Override
    public boolean equals(final Object obj) {
        return this == obj ||
                (obj != null && obj instanceof Artist && artistId == ((Artist) obj).artistId);
    }

    public String toString() {
        return artistName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(artistId);
        dest.writeString(artistName);
        dest.writeInt(numTracks);
        dest.writeInt(numAlbums);

    }
    @Override
    public int compareTo(@NonNull Artist another) {
        String o1c = (artistName == null)
                ? ""
                : artistName.toLowerCase(Locale.ENGLISH);
        String o2c = (another.artistName == null)
                ? ""
                : another.artistName.toLowerCase(Locale.ENGLISH);
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