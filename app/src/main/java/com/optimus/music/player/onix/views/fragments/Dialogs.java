package com.optimus.music.player.onix.views.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.optimus.music.player.onix.Common.Instances.Album;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.views.theme.Themes;

import static com.optimus.music.player.onix.data.Library.deleteAlbum;
import static com.optimus.music.player.onix.data.Library.deleteTrack;

public class Dialogs {
    public static void deleteSongDialog(final Context context, final Song song, final boolean showToast){

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Delete song " + song.songName)
                .setMessage("This action can't be undone. Still want to proceed?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteTrack(context, song, showToast);
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
        Themes.themeAlertDialog(dialog);

    }
    public static void deleteAlbumDialog(final Context context, final Album album, final boolean showToast){

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Delete album " + album.albumName)
                .setMessage("This action can't be undone. Still want to proceed?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAlbum(context, album, showToast);
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
        Themes.themeAlertDialog(dialog);

    }
}
