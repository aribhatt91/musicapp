package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.optimus.music.player.onix.Common.Instances.Artist;
//import com.marverenic.music.PlayerController;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.DetailScreens.AllTracksByArtist;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.Utility.PlaylistDialog;


public class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    private Context context;
    ColorGenerator gen = ColorGenerator.MATERIAL;


    private TextView artistName, songNum;
    private ImageView roundtext, moreButton;
    private Artist reference;
    private View divider;
    public ArtistViewHolder(View itemView) {
        super(itemView);
        artistName = (TextView) itemView.findViewById(R.id.songTitle);
        songNum = (TextView) itemView.findViewById(R.id.songArtist);
        roundtext = (ImageView) itemView.findViewById(R.id.list_image);
        moreButton = (ImageView) itemView.findViewById(R.id.expanded_menu);
        divider = itemView.findViewById(R.id.divider);

        itemView.setOnClickListener(this);
        moreButton.setOnClickListener(this);

        context = itemView.getContext();
    }

    public String getFirstLetter(String name){
        if(name.startsWith("The ")){
            name.replace("The ", "");
            String al = name.trim().substring(0,1);
            return al;
        }
        else{
            String al = name.trim().substring(0,1);
            return al;
        }
    }

    public void update( Artist a){
        reference = a;
        try {
            artistName.setText(a.artistName);
            String b = a.numAlbums + (a.numAlbums > 1 ? " albums \u2022" : " album \u2022");
            String c = " " + a.numTracks + (a.numTracks > 1 ? " songs" : " song");
            b = b + c;
            songNum.setText(b);
            String name = a.artistName;
            String i = name.substring(0, 1);
            int colour = gen.getColor(name);
            TextDrawable.IBuilder ib = TextDrawable.builder().beginConfig().toUpperCase().bold().endConfig().round();
            TextDrawable td = ib.build(i, colour);
            roundtext.setImageDrawable(td);
            divider.setAlpha(0.4f);



        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.expanded_menu:
                final PopupMenu menu = new PopupMenu(context, v);
                String[] options = context.getResources().getStringArray(R.array.queue_options_artist);
                for (int i = 0; i < options.length;  i++) {
                    menu.getMenu().add(Menu.NONE, i, i, options[i]);
                }
                menu.setOnMenuItemClickListener(this);
                menu.show();
                break;
            default:
                v.getContext().startActivity(new Intent(v.getContext(), AllTracksByArtist.class).putExtra("artist_id", reference.artistId).putExtra("name", reference.artistName));

                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case 0:
                PlayerController.setQueue(Library.getArtistSongEntries(reference), 0);
                PlayerController.begin();
                if(!PlayerController.isShuffle())
                    PlayerController.toggleShuffle();
                return true;
            case 1: //Queue this artist next
                PlayerController.queueNext(Library.getArtistSongEntries(reference));
                return true;
            case 2: //Queue this artist last
                PlayerController.queueLast(Library.getArtistSongEntries(reference));
                return true;
            case 3: //Add to playlist...
                PlaylistDialog.AddToNormal.alert(
                        itemView,
                        Library.getArtistSongEntries(reference),
                        itemView.getContext()
                                .getString(R.string.header_add_song_name_to_playlist, reference));
                return true;
        }
        return false;
    }
}
