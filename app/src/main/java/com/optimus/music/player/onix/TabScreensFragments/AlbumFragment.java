package com.optimus.music.player.onix.TabScreensFragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.optimus.music.player.onix.Common.Instances.Album;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.DetailScreens.AlbumDetailDemo;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.AlbumViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EmptyViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.GridSpacingItemDecoration;
import com.optimus.music.player.onix.SettingsActivity.Prefs;

import java.util.ArrayList;

import io.fabric.sdk.android.InitializationCallback;

public class AlbumFragment extends Fragment {

    private AlbumsFragmentAdapter mAdapter;
    //private Cursor cursor;
    private Context context;
    private ArrayList<Album> albums;
    private String orderby = MediaStore.Audio.Albums.ALBUM;
    private int position;
    private RecyclerView list;
    private static final String ARG_PARAM1 = "position";
    boolean isFlat;
    int span;

    int dimension;


    public AlbumFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AlbumFragment newInstance(int position) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.gallery_grid, container, false);
        final View bg = view.findViewById(R.id.bg);
        final View shadow = view.findViewById(R.id.toolbar_shadow);
        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fabs);
        list = (RecyclerView) view.findViewById(R.id.list);
        context = getActivity();

        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (fab != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (!fab.isShown()) {
                            fab.show();
                        }
                    } else {
                        if (fab.isShown()) {
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
                if(bg.getTranslationY()==0)
                    shadow.setAlpha(0.0f);
                else
                    shadow.setAlpha(1.0f);
            }

            private void OnScrollUp(int dy) {
                bg.setTranslationY((bg.getTranslationY() - dy / 3) > 0 ? 0 : (bg.getTranslationY() - dy / 3));
                if(bg.getTranslationY()==0)
                    shadow.setAlpha(0.0f);
                else
                    shadow.setAlpha(1.0f);
            }
        });



        if(Prefs.getAlbumsize(getActivity())==0){
            span = getResources().getInteger(R.integer.system_ui_modes_cols);
            dimension = (int) getResources().getDimension(R.dimen.grid_margin);
            isFlat = false;
        }else if(Prefs.getAlbumsize(getActivity())==1){
            span = 1;
            dimension = (int) getResources().getDimension(R.dimen.jb_grid_space);
            isFlat = true;

        }else if(Prefs.getAlbumsize(getActivity())==2){
            span = 2;
            dimension = (int) getResources().getDimension(R.dimen.grid_margin);
            isFlat = false;
        }else if(Prefs.getAlbumsize(getActivity())==3){
            span = 3;
            dimension = (int) getResources().getDimension(R.dimen.jb_grid_space);
            isFlat = false;
        }else{
            span = getResources().getInteger(R.integer.system_ui_modes_cols);
            dimension = (int) getResources().getDimension(R.dimen.grid_margin);
            isFlat = false;
        }


        albums = Library.getAlbums();
        mAdapter = new AlbumsFragmentAdapter();
        final int numColumns = span;
        if((albums.size()/numColumns)<3)
            shadow.setAlpha(0.0f);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numColumns);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (mAdapter.getItemViewType(position) == AlbumsFragmentAdapter.EMPTY)? numColumns : 1;
            }
        });

        list.setLayoutManager(layoutManager);
        list.addItemDecoration(new GridSpacingItemDecoration(numColumns, dimension, true));
        list.setAdapter(mAdapter);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        Library.addAlbumListener(mAdapter);
        Library.addRefreshListener(mAdapter);
        mAdapter.onLibraryRefreshed();
    }

    @Override
    public void onPause() {
        super.onPause();
        Library.removeAlbumListener(mAdapter);
        Library.removeRefreshListener(mAdapter);
    }




    public class AlbumsFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            Library.LibraryRefreshListener, Library.AlbumListChangeListener {

        public static final int EMPTY = 0, ALBUM = 1;
        @Override
        public int getItemCount() {
            return (Library.getAlbums().isEmpty())? 1 : Library.getAlbums().size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch(viewType) {
                case ALBUM:
                    if(Prefs.colourAlbum(getActivity())){
                        if(span==1){
                            return new AlbumViewHolder(LayoutInflater.
                                    from(parent.getContext()).inflate(R.layout.album_grid_flat, parent, false), isFlat);
                        }else {
                            return new AlbumViewHolder(LayoutInflater.
                                    from(parent.getContext()).inflate(R.layout.cardview_item_rect, parent, false), isFlat);
                        }
                    }
                    else{
                        if(span==1){
                            return new AlbumViewHolder(LayoutInflater.
                                    from(parent.getContext()).inflate(R.layout.album_grid_flat_card, parent, false), isFlat);

                        }else {
                            return new AlbumViewHolder(LayoutInflater.
                                    from(parent.getContext()).inflate(R.layout.cardview_item, parent, false), isFlat);
                        }

                    }
                case EMPTY:
                    return new EmptyViewHolder(
                            LayoutInflater
                                    .from(parent.getContext())
                                    .inflate(R.layout.instance_empty, parent, false),
                            getActivity());
                default:
                    return new EmptyViewHolder(
                            LayoutInflater
                                    .from(parent.getContext())
                                    .inflate(R.layout.instance_empty, parent, false),
                            getActivity());
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(getItemViewType(position)==ALBUM) {
                ((AlbumViewHolder)holder).update(Library.getAlbums().get(position), context);
            }
            else if(holder instanceof EmptyViewHolder) {
                EmptyViewHolder emptyHolder = ((EmptyViewHolder) holder);
                emptyHolder.setReason(R.string.empty);
                emptyHolder.setDetail(R.string.empty_detail);
                emptyHolder.setButton1(R.string.action_try_again);
            }

        }

        @Override
        public int getItemViewType(int position) {
            if(!Library.getAlbums().isEmpty())
                return ALBUM;
            else
                return EMPTY;
        }

        @Override
        public void onAlbumAdded(Album added, int index) {
            notifyItemInserted(index);
        }

        @Override
        public void onAlbumRemoved(Album removed, int index) {
            notifyItemRemoved(position);
        }

        @Override
        public void onLibraryRefreshed() {
            notifyDataSetChanged();
        }
    }
}
