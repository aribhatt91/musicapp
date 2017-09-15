package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.MultiselectAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.R;

import java.util.ArrayList;

/**
 * Created by longclaw on 14/10/16.
 */
public class SongMultiAdapter extends RecyclerView.Adapter<SongMultiAdapter.MusicViewHolder> implements View.OnClickListener, View.OnLongClickListener {


    public interface Callback{
        void onItemSelected(int index, boolean longClick);
        void onIconSelected(int index);
    }

    ArrayList<Song> songs;
    ArrayList<Integer> selected = new ArrayList<>();
    ArrayList<Integer> items = new ArrayList<>();
    private Callback callback;

    public SongMultiAdapter(Callback callback, ArrayList<Song> songs){
        this.callback = callback;
        this.songs = songs;
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public int getSelectedCount(){
        return selected.size();
    }

    public void add(Song item) {
        songs.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void toggleSelection(int i){

    }

    public void restoreState(){

    }

    public void saveState(){

    }

    public void clearState(){

    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder((
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.song_item, parent, false)));
    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {

        holder.itemView.setActivated(selected.contains(position));
        holder.itemView.setTag("item :" + position);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);


    }


    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    @Override
    public void onClick(View view) {

    }

    public class MusicViewHolder extends RecyclerView.ViewHolder{
        TextView text, subtext;
        ImageView art, icon;

        public MusicViewHolder(View itemView){
            super(itemView);
        }
    }
}
