package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.JukeBoxActivity.JukeBoxDBHelper;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.Navigate;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.TagEditorActivity.SongTagActivity;
import com.optimus.music.player.onix.Utility.PlaylistDialog;
import com.optimus.music.player.onix.WhatsHotActivity.VideoLibrary;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by apricot on 27/3/16.
 */
public class SongFileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    private View itemView;
    private TextView songName;
    private TextView detailText;
    private ImageView imageView;
    Context context;

    protected Song reference;
    protected int index;


    private ArrayList<Song> songList;

    private View divider;


    public SongFileViewHolder(View itemView, ArrayList<Song> songList) {
        super(itemView);
        //final Context context = ctx;

        this.itemView = itemView;
        this.songList = songList;

        songName = (TextView) itemView.findViewById(R.id.songTitle);
        detailText = (TextView) itemView.findViewById(R.id.songArtist);
        imageView = (ImageView) itemView.findViewById(R.id.list_image);
        divider = itemView.findViewById(R.id.divider);

        ImageView moreButton = (ImageView) itemView.findViewById(R.id.expanded_menu);
        itemView.setOnClickListener(this);
        moreButton.setOnClickListener(this);

    }



    public void update(Song s, int index, Context context) {
        reference = s;
        this.index = index;
        this.context = context;

        final Context ctx = context;

        int i = s.location.lastIndexOf('/');
        String name = s.location.substring(i + 1, s.location.length());

        String mb = Library.getFileSize(s.location);


        songName.setText(name);
        detailText.setText(mb);

        if(s.location.endsWith(".mp3") ||s.location.endsWith(".Mp3") || s.location.endsWith(".MP3")){
            imageView.setImageResource(R.drawable.file_mp3);

        }
        else if(s.location.endsWith(".wav") ||s.location.endsWith(".Wav") || s.location.endsWith(".WAV")){
            imageView.setImageResource(R.drawable.file_wav);

        }
        else if(s.location.endsWith(".ogg") ||s.location.endsWith(".Ogg") || s.location.endsWith(".OGG")){
            imageView.setImageResource(R.drawable.file_ogg);

        }
        else if(s.location.endsWith(".flac") ||s.location.endsWith(".Flac") || s.location.endsWith(".FLAC")){
            imageView.setImageResource(R.drawable.file_flac);

        }
        else{
            imageView.setImageResource(R.drawable.file_general);

        }


        divider.setAlpha(0.5f);

        if(index==songList.size()-1)
            divider.setAlpha(0.0f);





    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.expanded_menu:
                final PopupMenu menu = new PopupMenu(itemView.getContext(), v, Gravity.AXIS_CLIP);
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
                    JukeBoxDBHelper jb = new JukeBoxDBHelper(itemView.getContext());
                    jb.insertRecentSong(reference.songId, reference.albumId);
                    jb.updateMostPlayed(reference.songId);

                    //Toast.makeText(context, "Clicked", Toast.LENGTH_LONG).show();
                    PlayerController.setQueue(songList, songList.indexOf(reference));
                    PlayerController.begin();
                    jb.close();

                    if (Prefs.getPrefs(itemView.getContext()).getBoolean(Prefs.SWITCH_TO_PLAYING, false)) {
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
                jb.close();
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

}