package com.optimus.music.player.onix.views.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.data.model.Song;

import java.util.ArrayList;

public class SongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {


    public interface SongCallback{
        void onItemSelected(int index, boolean longClick);
        void onIconSelected(int index);
    }

    ArrayList<Song> songs = new ArrayList<>();
    ArrayList<Integer> selected = new ArrayList<>();
    ArrayList<Integer> items = new ArrayList<>();
    ArrayList<Object> list = new ArrayList<>();
    private SongCallback callback;

    public SongListAdapter(SongCallback callback, ArrayList<Song> songs){
        this.callback = callback;
        this.songs = songs;
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder((
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.song_item, parent, false)));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setActivated(selected.contains(position));
        holder.itemView.setTag("song:" + position);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);
    }

    private void setItemDetails(View view){
        try{
            String tag = (String) view.getTag();
            if(tag != null){
                int index = Integer.parseInt(tag.split(":")[1]);

            }

        }catch (Exception e) {

        }
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

    public void saveState(Bundle out){
        out.putSerializable("entirelist", list);
        out.putSerializable("selectedlist", selected);
    }

    public void clearState(){
        selected.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    @Override
    public void onClick(View view) {

    }

    public class MusicViewHolder extends RecyclerView.ViewHolder{
        TextView title, detail;
        ImageView art, more;
        View divider;

        public MusicViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.songTitle);
            detail = (TextView) itemView.findViewById(R.id.songArtist);
            art = (ImageView) itemView.findViewById(R.id.list_image);
            divider = itemView.findViewById(R.id.divider);
            more = (ImageView) itemView.findViewById(R.id.expanded_menu);
        }
    }
    public class AlbumViewHolder extends RecyclerView.ViewHolder{
        TextView title, detail;
        ImageView art, more;
        View divider;

        public AlbumViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.songTitle);
            detail = (TextView) itemView.findViewById(R.id.songArtist);
            art = (ImageView) itemView.findViewById(R.id.list_image);
            divider = itemView.findViewById(R.id.divider);
            more = (ImageView) itemView.findViewById(R.id.expanded_menu);
        }
    }
    public class AdViewHolder extends RecyclerView.ViewHolder{
        TextView title, detail;
        ImageView art, more;
        View divider;

        public AdViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.songTitle);
            detail = (TextView) itemView.findViewById(R.id.songArtist);
            art = (ImageView) itemView.findViewById(R.id.list_image);
            divider = itemView.findViewById(R.id.divider);
            more = (ImageView) itemView.findViewById(R.id.expanded_menu);
        }
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder{
        TextView title, detail;
        ImageView art, more;
        View divider;

        public FolderViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.songTitle);
            detail = (TextView) itemView.findViewById(R.id.songArtist);
            art = (ImageView) itemView.findViewById(R.id.list_image);
            divider = itemView.findViewById(R.id.divider);
            more = (ImageView) itemView.findViewById(R.id.expanded_menu);
        }
    }
}