package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders;

/**
 * Created by apricot on 13/12/15.
 */
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
//import com.marverenic.music.PlayerController;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.DetailScreens.AllTracksByArtist;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.DetailScreens.AlbumDetailDemo;

import com.optimus.music.player.onix.Common.Instances.Album;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.TagEditorActivity.AlbumTagEditorActivity;
import com.optimus.music.player.onix.TagEditorActivity.SongTagActivity;
import com.optimus.music.player.onix.Utility.PlaylistDialog;
//import com.marverenic.music.utils.PlaylistDialog;
//import com.marverenic.music.view.ViewUtils;

import java.util.HashMap;

public class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        Palette.PaletteAsyncListener, RequestListener<Uri, GlideDrawable>,
        PopupMenu.OnMenuItemClickListener {

    // Used to cache Palette values in memory
    private static final int FRAME_COLOR = 0;
    private static final int TITLE_COLOR = 1;
    private static final int DETAIL_COLOR = 2;

    private static int defaultFrameColor;
    private static int defaultTitleColor;
    private static int defaultDetailColor;

    private View itemView;
    private TextView albumName;
    private TextView artistName, year, songs;
    LinearLayout container;
    private ImageView artwork;
    private Album reference;

    private Context context;
    ImageView moreButton;

    private AsyncTask<Bitmap, Void, Palette> paletteTask;
    private ObjectAnimator backgroundAnimator;
    private ObjectAnimator titleAnimator;
    private ObjectAnimator detailAnimator, songAnimator, yearAnimator;
    private boolean isFlat;

    @SuppressWarnings("deprecation")
    public AlbumViewHolder(View itemView, boolean isFlat) {
        super(itemView);
        this.itemView = itemView;
        this.isFlat = isFlat;

        defaultFrameColor = itemView.getResources().getColor(R.color.grid_background_default);
        defaultTitleColor = itemView.getResources().getColor(R.color.grid_text);
        defaultDetailColor = itemView.getResources().getColor(R.color.grid_detail_text);



        container = (LinearLayout) itemView.findViewById(R.id.container);

        albumName = (TextView) itemView.findViewById(R.id.album_name);
        artistName = (TextView) itemView.findViewById(R.id.songs_num);
        moreButton = (ImageView) itemView.findViewById(R.id.instanceMore);
        artwork = (ImageView) itemView.findViewById(R.id.albumart);

        if(isFlat){
            year = (TextView) itemView.findViewById(R.id.year);
            songs = (TextView) itemView.findViewById(R.id.artist);
        }

        itemView.setOnClickListener(this);
        moreButton.setOnClickListener(this);
    }

    public void update(Album a, Context context){
        if (paletteTask != null && !paletteTask.isCancelled()){
            paletteTask.cancel(true);
        }

        this.context = context;

        reference = a;
        albumName.setText(a.albumName);
        artistName.setText(a.artistName);
        if(isFlat && year!=null && songs!=null){
            try {
                String y = "Year : " + ((a.year>0)?  a.year : "Unknown");
                year.setText(y);
                String numsongs = a.numberOfSongs + ((a.numberOfSongs>1)? " songs" : " song");
                songs.setText(numsongs);
            }catch (Exception e){

            }
        }
        final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");




        final Uri uri = Uri.parse("file://"+reference.artUri); //ContentUris.withAppendedId(sArtworkUri, a.albumId);



        if(Prefs.colourAlbum(itemView.getContext())) {


            resetPalette();

            Glide.with(itemView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.default_album_art_)
                    .override(300,300)
                    .crossFade(1000)
                    .animate(R.anim.fade_in_grid)
                    .listener(this)
                    .into(artwork);
        }
        else{
            Glide.with(itemView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.default_album_art_)
                    .override(300,300)
                    .crossFade(1000)
                    .into(artwork);
        }

    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.instanceMore:
                final PopupMenu menu = new PopupMenu(itemView.getContext(), v);
                String[] options =
                        itemView.getResources().getStringArray(R.array.queue_options_album);
                for (int i = 0; i < options.length; i++) {
                    menu.getMenu().add(Menu.NONE, i, i, options[i]);
                }
                menu.setOnMenuItemClickListener(this);
                menu.show();
                break;
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !Prefs.animOff(itemView.getContext())) {
                    Intent intent = new Intent(itemView.getContext(), AlbumDetailDemo.class);
                    intent.putExtra("album_id", reference.albumId)
                            .putExtra("album_name", reference.albumName)
                            .putExtra("artist_name", reference.artistName);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                            .makeSceneTransitionAnimation((Activity)context, artwork, "album_art");
                    v.getContext().startActivity(intent, optionsCompat.toBundle());

                }
                else {

                    v.getContext().startActivity(new Intent(context, AlbumDetailDemo.class)
                            .putExtra("album_id", reference.albumId)
                            .putExtra("album_name", reference.albumName)
                            .putExtra("artist_name", reference.artistName));
                }
                break;
        }
    }


    @Override
    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        if (isFromMemoryCache) {
            updatePalette(resource);
        } else {
            animatePalette(resource);
        }
        return false;
    }

    @Override
    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
        return false;
    }

    private void generatePalette(Drawable drawable) {
        if (Library.colorCache.get(reference.albumId) == null) {
            paletteTask = Palette.from(Util.drawableToBitmap(drawable)).generate(this);
        }
    }

    private void resetPalette() {
        if (paletteTask != null && !paletteTask.isCancelled()) paletteTask.cancel(true);

        if (backgroundAnimator != null){
            backgroundAnimator.setDuration(300);
            backgroundAnimator.cancel();
        }
        if (titleAnimator != null){
            titleAnimator.setDuration(300);
            titleAnimator.cancel();
        }
        if (detailAnimator != null){
            detailAnimator.setDuration(300);
            detailAnimator.cancel();
        }

        container.setBackgroundColor(defaultFrameColor);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                moreButton.setImageTintList(ColorStateList.valueOf(defaultTitleColor));
                moreButton.setImageTintMode(PorterDuff.Mode.SRC_ATOP);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            moreButton.setColorFilter(defaultTitleColor, PorterDuff.Mode.MULTIPLY);
        }
        albumName.setTextColor(defaultTitleColor);
        artistName.setTextColor(defaultDetailColor);
        if(isFlat && year!=null && songs!=null){
            year.setTextColor(defaultTitleColor);
            songs.setTextColor(defaultDetailColor);

        }
    }

    private void updatePalette(Drawable drawable) {
        try {
            int[] colors = Library.colorCache.get(reference.albumId);

            if (colors != null) {
                container.setBackgroundColor(colors[FRAME_COLOR]);
                albumName.setTextColor(colors[TITLE_COLOR]);
                artistName.setTextColor(colors[DETAIL_COLOR]);
                if(isFlat && year!=null && songs!=null){
                    year.setTextColor(colors[DETAIL_COLOR]);
                    songs.setTextColor(colors[DETAIL_COLOR]);

                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        moreButton.setImageTintList(ColorStateList.valueOf(colors[TITLE_COLOR]));
                        moreButton.setImageTintMode(PorterDuff.Mode.SRC_ATOP);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    moreButton.setColorFilter(colors[TITLE_COLOR], PorterDuff.Mode.MULTIPLY);
                }
            } else {
                resetPalette();
                generatePalette(drawable);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void animatePalette(Drawable drawable) {
        try {


            int[] colors = Library.colorCache.get(reference.albumId);

            if (colors != null) {
                backgroundAnimator = ObjectAnimator.ofObject(
                        container,
                        "backgroundColor",
                        new ArgbEvaluator(),
                        defaultFrameColor,
                        colors[FRAME_COLOR]);
                backgroundAnimator.setDuration(700).start();

                titleAnimator = ObjectAnimator.ofObject(
                        albumName,
                        "textColor",
                        new ArgbEvaluator(),
                        defaultTitleColor,
                        colors[TITLE_COLOR]);
                titleAnimator.setDuration(700).start();

                detailAnimator = ObjectAnimator.ofObject(
                        artistName,
                        "textColor",
                        new ArgbEvaluator(),
                        defaultDetailColor,
                        colors[DETAIL_COLOR]);
                detailAnimator.setDuration(700).start();

                if(isFlat && year!=null && songs!=null){
                    songAnimator = ObjectAnimator.ofObject(
                            songs,
                            "textColor",
                            new ArgbEvaluator(),
                            defaultDetailColor,
                            colors[DETAIL_COLOR]);
                    songAnimator.setDuration(700).start();

                    yearAnimator = ObjectAnimator.ofObject(
                            year,
                            "textColor",
                            new ArgbEvaluator(),
                            defaultDetailColor,
                            colors[DETAIL_COLOR]);
                    yearAnimator.setDuration(700).start();

                }
            } else {
                generatePalette(drawable);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onGenerated(Palette palette) {
        int frameColor = palette.getVibrantColor(Color.TRANSPARENT);
        Palette.Swatch swatch = palette.getVibrantSwatch();

        if (swatch == null || frameColor == Color.TRANSPARENT) {
            frameColor = palette.getLightVibrantColor(Color.TRANSPARENT);
            swatch = palette.getLightVibrantSwatch();
        }
        if (swatch == null || frameColor == Color.TRANSPARENT) {
            frameColor = palette.getDarkVibrantColor(Color.TRANSPARENT);
            swatch = palette.getDarkVibrantSwatch();
        }
        if (swatch == null || frameColor == Color.TRANSPARENT) {
            frameColor = palette.getLightMutedColor(Color.TRANSPARENT);
            swatch = palette.getLightMutedSwatch();
        }
        if (swatch == null || frameColor == Color.TRANSPARENT) {
            frameColor = palette.getDarkMutedColor(Color.TRANSPARENT);
            swatch = palette.getDarkMutedSwatch();
        }

        int titleColor = defaultTitleColor;
        int detailColor = defaultDetailColor;

        if (swatch != null && frameColor != Color.TRANSPARENT) {
            titleColor = swatch.getTitleTextColor();
            detailColor = swatch.getBodyTextColor();
        } else {
            frameColor = defaultFrameColor;
        }

        Library.colorCache.put(reference.albumId, new int[]{frameColor, titleColor, detailColor});
        animatePalette(null);
    }



    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 0: //Queue this album next
                PlayerController.queueNext(Library.getAlbumEntries(reference));
                return true;
            case 1: //Queue this album last
                PlayerController.queueLast(Library.getAlbumEntries(reference));
                return true;

            case 2: //Add to playlist...
                PlaylistDialog.AddToNormal.alert(
                        itemView,
                        Library.getAlbumEntries(reference),
                        itemView.getContext()
                                .getString(R.string.header_add_song_name_to_playlist, reference));
                return true;
            case 3:

                context.startActivity(new Intent(context, AlbumTagEditorActivity.class)
                        .putExtra("album_id", reference.albumId));
                return true;
            case 4:
                Library.deleteAlbumDialog(itemView.getContext(), reference, true);
                return true;
        }
        return false;
    }
}