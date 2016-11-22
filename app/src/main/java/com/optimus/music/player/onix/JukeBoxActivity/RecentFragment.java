package com.optimus.music.player.onix.JukeBoxActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.optimus.music.player.onix.Common.Instances.Album;
import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.AlbumArtistViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.AlbumViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EmptyViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.GridSpacingItemDecoration;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.SongViewHolderBlank;
import com.optimus.music.player.onix.SettingsActivity.Prefs;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;


public class RecentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<Song> result = new ArrayList<>();

    private int mParam1;
    private String mParam2;
    private Context context;


    public RecentFragment() {
        // Required empty public constructor
    }


    public static RecentFragment newInstance(int param1, String param2) {
        RecentFragment fragment = new RecentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_gallery, container, false);
        RecyclerView list = (RecyclerView) view.findViewById(R.id.list);
        context = getActivity();
        SharedPreferences prefs = Prefs.getPrefs(getActivity());
        int primary = Integer.parseInt(prefs.getString(Prefs.PRIMARY_COLOR, "0"));
        final View bg = view.findViewById(R.id.bg);
        //final View shadow = view.findViewById(R.id.toolbar_shadow);
        //Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        //toolbar.setTitle("Recent Activity");

        //AlbumsFragmentAdapter mAdapter = new AlbumsFragmentAdapter();
        //final int numColumns = getResources().getInteger(R.integer.system_ui_modes_cols);


        //GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numColumns);
        //list.setLayoutManager(layoutManager);
        //list.addItemDecoration(new GridSpacingItemDecoration(numColumns, (int) getResources().getDimension(R.dimen.grid_margin), true));
        //list.setAdapter(mAdapter);
        RecentSongAdapter adapter = new RecentSongAdapter();

        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.fast_scroller);
        fastScroller.setRecyclerView(list);
        if(primary==0)
            fastScroller.setHandleBackground(ContextCompat.getDrawable(getActivity(), R.drawable.scroll_metal));
        else if(primary==1)
            fastScroller.setHandleBackground(ContextCompat.getDrawable(getActivity(), R.drawable.scroll_fire));
        else if(primary==2)
            fastScroller.setHandleBackground(ContextCompat.getDrawable(getActivity(), R.drawable.scroll_green));
        else if(primary==3)
            fastScroller.setHandleBackground(ContextCompat.getDrawable(getActivity(), R.drawable.scroll_metal));
        else if(primary==4 || primary==10 || primary==11)
            fastScroller.setHandleBackground(ContextCompat.getDrawable(getActivity(), R.drawable.scroll_life));
        else if(primary==5)
            fastScroller.setHandleBackground(ContextCompat.getDrawable(getActivity(), R.drawable.scroll_marine));
        else if(primary==6)
            fastScroller.setHandleBackground(ContextCompat.getDrawable(getActivity(), R.drawable.scroll_metal));
        else if(primary==7)
            fastScroller.setHandleBackground(ContextCompat.getDrawable(getActivity(), R.drawable.scroll_oceangreen));
        else if(primary==8)
            fastScroller.setHandleBackground(ContextCompat.getDrawable(getActivity(), R.drawable.scroll_metal));
        else if(primary==9)
            fastScroller.setHandleBackground(ContextCompat.getDrawable(getActivity(), R.drawable.scroll_ether));



        list.addOnScrollListener(fastScroller.getOnScrollListener());
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                if (bg.getTranslationY() == 0)
                    shadow.setAlpha(0.0f);
                else
                    shadow.setAlpha(1.0f);
                    */
            }

            private void OnScrollUp(int dy) {
                bg.setTranslationY((bg.getTranslationY() - dy / 3) > 0 ? 0 : (bg.getTranslationY() - dy / 3));
                /*
                if (bg.getTranslationY() == 0)
                    shadow.setAlpha(0.0f);
                else
                    shadow.setAlpha(1.0f);
                    */
            }
        });

        //shadow.setAlpha(0.0f);


        //songList = Library.getSongs(); //new ArrayList<>();

        setRecyclerViewLayoutManager(list);
        list.setAdapter(adapter);
        list.setHasFixedSize(true);



        return view;

    }

    /**
     * Set RecyclerView's LayoutManager
     */
    public void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
    private class RecentSongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        ArrayList<Song> songList = new ArrayList<>();
        private final int EMPTY = 0, SONG=1;


        public RecentSongAdapter(){
            if(mParam1==0) {
                songList = RecentActivity.recentPlayed;
            }
            else if(mParam1==1){
                songList = RecentActivity.recentAdded;

            }else{
                songList = RecentActivity.mostPlayed;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case EMPTY:
                    return new EmptyViewHolder(
                            LayoutInflater
                                    .from(parent.getContext())
                                    .inflate(R.layout.instance_empty, parent, false),
                            getActivity());
                case SONG:
                    return new SongViewHolderBlank(
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.song_item, parent, false),
                            songList);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
            if(getItemViewType(i)==SONG) {

                ((SongViewHolderBlank) holder).update(songList.get(i), i, context);

            }else if(holder instanceof EmptyViewHolder){
                EmptyViewHolder emptyHolder = ((EmptyViewHolder) holder);
                emptyHolder.setReason(R.string.empty);
                emptyHolder.setDetail(R.string.empty_recent);
                emptyHolder.setButton1(R.string.action_try_again);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(songList.isEmpty())
                return EMPTY;
            else return SONG;
        }

        @Override
        public int getItemCount() {
            if(songList.isEmpty())
                return 1;
            else
                return songList.size();
        }




    }

    private class LastAddedAdapter extends RecyclerView.Adapter<AlbumArtistViewHolder>{
        private ArrayList<Album> albums;

        public LastAddedAdapter(){
            albums = JukeBoxActivity.albumEntries;
        }
        @Override
        public int getItemCount() {
            return albums.size();
        }

        @Override
        public AlbumArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(Prefs.colourAlbum(getActivity())){
                return new AlbumArtistViewHolder(LayoutInflater.
                        from(parent.getContext()).inflate(R.layout.cardview_item_rect, parent, false));
            }
            else{
                return new AlbumArtistViewHolder(LayoutInflater.
                        from(parent.getContext()).inflate(R.layout.cardview_item, parent, false));

            }        }

        @Override
        public void onBindViewHolder(AlbumArtistViewHolder holder, int position) {
            holder.update(albums.get(position), context);

        }


    }

}
