package com.optimus.music.player.onix.TabScreensFragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.optimus.music.player.onix.Common.Instances.Artist;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.DetailScreens.AllTracksByArtist;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.ArtistViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EmptyViewHolder;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.WhatsHotActivity.RecyclerAdapter;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by apricot on 4/8/15.
 */
public class Artists extends Fragment {

    //Cursor temp;
    private ArrayList<Artist> artists;
    private ArtistsAdapter adapter;
    private RecyclerView list;



    private static final String ARG_POSITION = "position";

    private int position;

    //static final String[] ARTIST_SUMMARY_PROJECTION = { MediaStore.Audio.Artists._ID,MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS, MediaStore.Audio.Artists.NUMBER_OF_TRACKS };
    //String orderby = MediaStore.Audio.Artists.ARTIST;

    public static Artists newInstance(int position) {
        Artists f = new Artists();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.list_gallery, container, false);
        SharedPreferences prefs = Prefs.getPrefs(getActivity());
        int primary = Integer.parseInt(prefs.getString(Prefs.PRIMARY_COLOR, "0"));
        final View bg = myFragmentView.findViewById(R.id.bg);
        //final View shadow = myFragmentView.findViewById(R.id.toolbar_shadow);
        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fabs);




        //artists = new ArrayList<>();




        list = (RecyclerView) myFragmentView.findViewById(R.id.list);
        adapter = new ArtistsAdapter();

        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) myFragmentView.findViewById(R.id.fast_scroller);
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
                    //shadow.setAlpha(1.0f);

                    OnScrollUp(dy);

                } else {
                    //shadow.setAlpha(0.0f);

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

        /*temp = getActivity().getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,ARTIST_SUMMARY_PROJECTION, null, null, orderby);
        if(temp!= null && temp.moveToFirst()){
            do{
                artists.add(new Artist(
                        temp.getInt(temp.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)),
                        temp.getString(temp.getColumnIndex(MediaStore.Audio.Artists.ARTIST)),
                        temp.getInt(temp.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)),
                        temp.getInt(temp.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS))
                ));
            }while (temp.moveToNext());
        }

        if(temp!=null)
            temp.close();*/
        artists = Library.getArtists();

        /*
        if(artists.isEmpty())
            shadow.setAlpha(0.0f);
            */
       // Toast.makeText(getActivity(), " "+artists.size()+" ", Toast.LENGTH_LONG).show();
        setRecyclerViewLayoutManager(list);
        list.setAdapter(adapter);





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
    public void onResume() {
        super.onResume();
        adapter.onLibraryRefreshed();
    }






    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }







    private class ArtistsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
            implements Library.LibraryRefreshListener{
        private final int EMPTY = 0, ARTIST = 1;

        @Override
        public int getItemCount() {
            return Library.getArtists().isEmpty() ? 1 : Library.getArtists().size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==ARTIST) {
                return new ArtistViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.artist_list_item, parent, false));
            }
            else{
                return new EmptyViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.instance_empty, parent, false),
                        getActivity());
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(getItemViewType(position) == ARTIST) {
                ((ArtistViewHolder) holder).update(Library.getArtists().get(position));
            }
            else if(holder instanceof EmptyViewHolder){
                EmptyViewHolder emptyHolder = ((EmptyViewHolder) holder);
                emptyHolder.setReason(R.string.empty);
                emptyHolder.setDetail(R.string.empty_detail);
                emptyHolder.setButton1(R.string.action_try_again);
            }

        }

        @Override
        public int getItemViewType(int position) {
            if(Library.getArtists().isEmpty()){
                return EMPTY;
            }
            else{
                return ARTIST;
            }
        }

        @Override
        public void onLibraryRefreshed() {
            notifyDataSetChanged();
        }
    }
}