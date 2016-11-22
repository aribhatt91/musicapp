package com.optimus.music.player.onix.TabScreensFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EmptyViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.EmptyState;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.SongViewHolder;
import com.optimus.music.player.onix.SettingsActivity.Prefs;

import java.util.ArrayList;
import java.util.List;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class Songs extends Fragment {

    private SongAdapter nAdapter;
    private static final String ARG_POSITION = "position";
    private ArrayList<Song> songList;
    private Context context;
    private RecyclerView list;

    private int position;



    public static Songs newInstance(int position) {
        Songs f = new Songs();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myFragmentView = inflater.inflate(R.layout.list_gallery, container, false);
        //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        SharedPreferences prefs = Prefs.getPrefs(getActivity());
        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fabs);
        int primary = Integer.parseInt(prefs.getString(Prefs.PRIMARY_COLOR, "0"));
        final View bg = myFragmentView.findViewById(R.id.bg);
        //final View shadow = myFragmentView.findViewById(R.id.toolbar_shadow);
        context = getActivity();
        list = (RecyclerView) myFragmentView.findViewById(R.id.list);

        nAdapter = new SongAdapter();

        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) myFragmentView.findViewById(R.id.fast_scroller);
        fastScroller.setRecyclerView(list);
        if(primary==0 || primary==6 || primary==8)
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
        /*

        if(Library.getSongs().isEmpty())
            shadow.setAlpha(0.0f);
            */


        //songList = Library.getSongs(); //new ArrayList<>();

        setRecyclerViewLayoutManager(list);
        list.setHasFixedSize(true);

        list.setAdapter(nAdapter);




        return myFragmentView;
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
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);




    }

    @Override
    public void onResume() {
        super.onResume();
        Library.addSongListener(nAdapter);
        Library.addRefreshListener(nAdapter);
        nAdapter.onLibraryRefreshed();
    }

    @Override
    public void onPause() {
        super.onPause();
        Library.removeSongListener(nAdapter);
        Library.removeRefreshListener(nAdapter);
    }

    public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            Library.SongListChangeListener, Library.LibraryRefreshListener{
        private final int EMPTY = 0, SONG = 1;

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
                    return new SongViewHolder(
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.song_item, parent, false),
                            Library.getSongs());
                default:
                    return new SongViewHolder(
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.song_item, parent, false),
                            Library.getSongs());
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
            if(getItemViewType(position) == SONG)
                ((SongViewHolder)holder).update(Library.getSongs().get(i), i, context);
            else if(holder instanceof EmptyViewHolder) {
                EmptyViewHolder emptyHolder = ((EmptyViewHolder) holder);
                emptyHolder.setReason(R.string.empty);
                emptyHolder.setDetail(R.string.empty_detail);
                emptyHolder.setButton1(R.string.action_try_again);
            }
        }

        @Override
        public void onSongAdded(Song added, int index) {
            notifyItemInserted(index);
        }

        @Override
        public int getItemViewType(int position) {
            if(Library.getSongs().isEmpty())
                return EMPTY;
            else
                return SONG;
        }

        @Override
        public void onSongRemoved(Song removed, int index) {
            notifyItemRemoved(index);
        }

        @Override
        public void onSongModified(Song modified, int index) {
            notifyItemChanged(index);
        }

        @Override
        public int getItemCount() {
            return (Library.getSongs().isEmpty())? 1 : Library.getSongs().size();
        }

        @Override
        public void onLibraryRefreshed() {
            notifyDataSetChanged();
        }


    }



}

