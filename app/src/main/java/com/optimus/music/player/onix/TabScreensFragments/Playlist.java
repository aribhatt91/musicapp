package com.optimus.music.player.onix.TabScreensFragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.LibraryActivity;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.GridSpacingItemDecoration;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.PlaylistViewHolder;

import java.util.ArrayList;


public class Playlist extends Fragment  {

    private PlaylistFragmentAdapter mAdapter;
    //private ArrayList<com.optimus.music.player.onix.Common.Instances.Playlist> playlists;
    private Cursor temp;
    private com.optimus.music.player.onix.Common.Instances.Playlist p;
    private RecyclerView list;


    private static final String ARG_POSITION = "position";

    private int position;

    public static Playlist newInstance(int position) {
        Playlist f = new Playlist();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);

        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.gallery_grid, container, false);
        final View bg = myFragmentView.findViewById(R.id.bg);
        final View shadow = myFragmentView.findViewById(R.id.toolbar_shadow);
        list = (RecyclerView) myFragmentView.findViewById(R.id.list);
        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fabs);

        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (fab != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (!fab.isShown() && LibraryActivity.showFab) {
                            fab.show();
                        }
                    } else {
                        if (LibraryActivity.showFab && fab.isShown()) {
                            fab.hide();
                        }
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) {

                    OnScrollUp(dy);

                } else {

                    OnScrollDown(dy);
                }

            }


            private void OnScrollDown(int dy) {
                bg.setTranslationY((bg.getTranslationY() - dy / 3));
                /*
                if(bg.getTranslationY()==0)
                    shadow.setAlpha(0.0f);
                else
                    shadow.setAlpha(1.0f);
                    */
            }

            private void OnScrollUp(int dy) {
                bg.setTranslationY((bg.getTranslationY() - dy / 3) > 0 ? 0 : (bg.getTranslationY() - dy / 3));
                /*
                if(bg.getTranslationY()==0)
                    shadow.setAlpha(0.0f);
                else
                    shadow.setAlpha(1.0f);
                    */
            }
        });



        mAdapter = new PlaylistFragmentAdapter();
        final int numColumns = getResources().getInteger(R.integer.system_ui_modes_cols);
        /*
        if((Library.getPlaylistCount()/numColumns)<3)
            shadow.setAlpha(0.0f);
            */
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numColumns);
        list.setLayoutManager(layoutManager);
        list.addItemDecoration(new GridSpacingItemDecoration(1, (int) getResources().getDimension(R.dimen.gallery_grid_space), true));

        list.setAdapter(mAdapter);

        return myFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Library.addPlaylistListener(mAdapter);
        Library.addRefreshListener(mAdapter);
        mAdapter.onLibraryRefreshed();
    }

    @Override
    public void onPause(){
        super.onPause();
        Library.removePlaylistListener(mAdapter);
        Library.removeRefreshListener(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    public class PlaylistFragmentAdapter extends RecyclerView.Adapter<PlaylistViewHolder>
            implements Library.PlaylistChangeListener ,Library.LibraryRefreshListener{

        ArrayList<com.optimus.music.player.onix.Common.Instances.Playlist> playlists;

        private final int EMPTY = 0;
        private final int PLAYLIST = 1;

        public PlaylistFragmentAdapter(){
            playlists = Library.getPlaylists();
        }


        @Override
        public int getItemCount() {
            return playlists.size();
        }

        @Override
        public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PlaylistViewHolder(
                    (LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.grid_overlay_imgview, parent, false))
            );
        }

        @Override
        public void onBindViewHolder(PlaylistViewHolder holder, int position) {
            holder.update(playlists.get(position));
        }



        @Override
        public void onLibraryRefreshed() {
            notifyDataSetChanged();
        }

        @Override
        public void onPlaylistRemoved(com.optimus.music.player.onix.Common.Instances.Playlist removed, int index) {
            //if(playlists.contains(removed))
                notifyItemRemoved(index);
            //notifyDataSetChanged();
        }

        @Override
        public void onPlaylistAdded(com.optimus.music.player.onix.Common.Instances.Playlist added, int index) {
            notifyItemInserted(index);
            //notifyDataSetChanged();
        }
    }
}
/*
    public class PlaylistFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
            implements Library.PlaylistChangeListener ,Library.LibraryRefreshListener{

        ArrayList<com.optimus.music.player.onix.Common.Instances.Playlist> playlists;

        public static final int EMPTY = 0;
        public static final int PLAYLIST = 1;



        @Override
        public int getItemCount() {
            return Library.getAlbums().isEmpty()? 1 : Library.getPlaylists().size();
        }

        @Override
        public int getItemViewType(int position) {
            if(Library.getAlbums().isEmpty())
                return EMPTY;
            else return PLAYLIST;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == EMPTY){
                return new EmptyViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.instance_empty, parent, false),
                        getActivity());
            }
            else {
                return new PlaylistViewHolder(
                        (LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.grid_overlay_imgview, parent, false))
                );
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(getItemViewType(position)==PLAYLIST) {
                ((PlaylistViewHolder)holder).update(playlists.get(position));
            }
            else if(holder instanceof EmptyViewHolder) {
                EmptyViewHolder emptyHolder = ((EmptyViewHolder) holder);
                emptyHolder.setReason(R.string.empty);
                emptyHolder.setDetail(R.string.empty_detail);
                emptyHolder.setButton1(R.string.action_try_again);
            }
        }



        @Override
        public void onLibraryRefreshed() {
            notifyDataSetChanged();
        }

        @Override
        public void onPlaylistRemoved(com.optimus.music.player.onix.Common.Instances.Playlist removed, int index) {
            //if(playlists.contains(removed))
                notifyItemRemoved(index);
            //notifyDataSetChanged();
        }

        @Override
        public void onPlaylistAdded(com.optimus.music.player.onix.Common.Instances.Playlist added, int index) {
            notifyItemInserted(index);
            //notifyDataSetChanged();
        }
    }
 */