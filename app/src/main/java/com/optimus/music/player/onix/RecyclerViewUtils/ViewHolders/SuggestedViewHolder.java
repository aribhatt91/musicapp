package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
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
import android.support.v7.graphics.Palette;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.JukeBoxActivity.JukeBoxActivity;
import com.optimus.music.player.onix.JukeBoxActivity.JukeBoxDBHelper;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.TagEditorActivity.SongTagActivity;
import com.optimus.music.player.onix.Utility.PlaylistDialog;
import com.optimus.music.player.onix.WhatsHotActivity.VideoLibrary;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by apricot on 13/5/16.
 */
public class SuggestedViewHolder extends RecyclerView.ViewHolder
        implements
        Palette.PaletteAsyncListener, RequestListener<Uri, GlideDrawable>,

        View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    // Used to cache Palette values in memory
    private static HashMap<Song, int[]> colorCache = new HashMap<>();
    private static final int FRAME_COLOR = 0;
    private static final int TITLE_COLOR = 1;
    private static final int DETAIL_COLOR = 2;

    private static int defaultFrameColor;
    private static int defaultTitleColor;
    private static int defaultDetailColor;

    private View itemView;
    private TextView songName;
    private TextView detailText;
    private ImageView imageView;
    private LinearLayout container;
    Context context;
    private AsyncTask<Bitmap, Void, Palette> paletteTask;
    private ObjectAnimator backgroundAnimator;
    private ObjectAnimator titleAnimator;
    private ObjectAnimator detailAnimator;

    protected Song reference;
    protected int index;


    private ArrayList<Song> songList;

    ImageView moreButton;


    public SuggestedViewHolder(View itemView, ArrayList<Song> songList) {
        super(itemView);


        defaultFrameColor = itemView.getResources().getColor(R.color.grid_background_default);
        defaultTitleColor = itemView.getResources().getColor(R.color.grid_text);
        defaultDetailColor = itemView.getResources().getColor(R.color.grid_detail_text);

        this.itemView = itemView;
        this.songList = songList;

        songName = (TextView) itemView.findViewById(R.id.album_name);
        detailText = (TextView) itemView.findViewById(R.id.songs_num);
        imageView = (ImageView) itemView.findViewById(R.id.albumart);
        container = (LinearLayout) itemView.findViewById(R.id.container);
        moreButton = (ImageView) itemView.findViewById(R.id.instanceMore);
        itemView.setOnClickListener(this);
        moreButton.setOnClickListener(this);

    }



    public void update(Song s, int index, Context context) {
        reference = s;
        this.index = index;
        this.context = context;


        songName.setText(s.songName);
        detailText.setText(s.artistName + " \u2022 " + s.albumName);
        final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

        final Uri uri = ContentUris.withAppendedId(sArtworkUri, s.albumId);
        if(Prefs.colourAlbum(itemView.getContext())) {

            resetPalette();

            Glide.with(itemView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.default_album_art_)
                    .crossFade(1000)
                    .animate(R.anim.fade_in_grid)
                    .listener(this)
                    .into(imageView);
        }
        else {

            Glide.with(context)
                    .load(uri)
                    .placeholder(R.drawable.default_album_art_)
                    .crossFade(300)
                    .into(imageView);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.instanceMore:
                final PopupMenu menu = new PopupMenu(itemView.getContext(), v);
                String[] options = itemView.getResources()
                        .getStringArray(
                                R.array.queue_options_song_instance
                        );

                for (int i = 0; i < options.length;  i++) {
                    menu.getMenu().add(Menu.NONE, i, i, options[i]);
                }
                menu.setOnMenuItemClickListener(this);
                menu.show();


                break;
            default:
                if (songList != null) {
                    //Toast.makeText(context, "Clicked", Toast.LENGTH_LONG).show();
                    PlayerController.setQueue(songList, songList.indexOf(reference));
                    PlayerController.begin();

                    if (Prefs.getPrefs(itemView.getContext()).getBoolean(Prefs.SWITCH_TO_PLAYING, true)) {
                        if (PlayerController.getQueue()!=null && !PlayerController.getQueue().isEmpty()) {
                            Navigate.to(itemView.getContext(), NowPlayingActivity.class);
                        } else {
                            Toast.makeText(itemView.getContext(), "Couldn't play this song!", Toast.LENGTH_LONG).show();
                        }
                    }


                }
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 0: //Queue this song next
                PlayerController.queueNext(reference);
                return true;
            case 1: //Queue this song last
                //Toast.makeText(context, "Queue", Toast.LENGTH_LONG).show();
                PlayerController.queueLast(reference);
                return true;
            case 2: //Add to playlist
                PlaylistDialog.AddToNormal.alert(itemView, reference, itemView.getContext()
                        .getString(R.string.header_add_song_name_to_playlist, reference));
                return true;
            case 3: // set as ringtone
                Library.setRingtone(itemView.getContext(), reference.songId);
                return true;
            case 4:
                try {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("audio/*");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + reference.location));
                    context.startActivity(Intent.createChooser(share, "Share File"));
                }
                catch (Exception e){
                    Toast.makeText(context, "Oops, Something broke!", Toast.LENGTH_LONG).show();
                }
                return true;

            case 5:
                JukeBoxDBHelper jb = new JukeBoxDBHelper(context);
                jb.insertFav(reference.songId,1);
                return true;

            case 6:
                try {
                    String url = VideoLibrary.prepareSearchString(reference.songName + " " + reference.artistName);
                    Intent openLFMIntent = new Intent(Intent.ACTION_VIEW);
                    openLFMIntent.setData(Uri.parse(url));
                    itemView.getContext().startActivity(openLFMIntent);
                }
                catch (Exception e){
                    Toast.makeText(itemView.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case 7:

                Navigate.to(context, SongTagActivity.class,
                        SongTagActivity.TAGGER_EXTRA, reference);
                return true;


        }
        return false;
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
        if (colorCache.get(reference) == null) {
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
        songName.setTextColor(defaultTitleColor);
        detailText.setTextColor(defaultDetailColor);
    }

    private void updatePalette(Drawable drawable) {
        try {
            int[] colors = colorCache.get(reference);

            if (colors != null) {
                container.setBackgroundColor(colors[FRAME_COLOR]);
                songName.setTextColor(colors[TITLE_COLOR]);
                detailText.setTextColor(colors[DETAIL_COLOR]);
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

            int[] colors = colorCache.get(reference);

            if (colors != null) {
                backgroundAnimator = ObjectAnimator.ofObject(
                        container,
                        "backgroundColor",
                        new ArgbEvaluator(),
                        defaultFrameColor,
                        colors[FRAME_COLOR]);
                backgroundAnimator.setDuration(300).start();

                titleAnimator = ObjectAnimator.ofObject(
                        songName,
                        "textColor",
                        new ArgbEvaluator(),
                        defaultTitleColor,
                        colors[TITLE_COLOR]);
                titleAnimator.setDuration(300).start();

                detailAnimator = ObjectAnimator.ofObject(
                        songName,
                        "textColor",
                        new ArgbEvaluator(),
                        defaultDetailColor,
                        colors[DETAIL_COLOR]);
                detailAnimator.setDuration(300).start();
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

        colorCache.put(reference, new int[]{frameColor, titleColor, detailColor});
        animatePalette(null);
    }




}
