package com.optimus.music.player.onix.TabScreensFragments;

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

import com.optimus.music.player.onix.Common.ConnectionDetector;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EmptyViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.GenreViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.GridSpacingItemDecoration;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.NativeExpressViewHolder;
import com.optimus.music.player.onix.SettingsActivity.Prefs;

import java.util.ArrayList;

/**
 * Created by apricot on 4/8/15.
 */
public class Genre extends Fragment  {

    private GenresAdapter mAdapter;

    private static final String ARG_POSITION = "position";
    private ArrayList<com.optimus.music.player.onix.Common.Instances.Genre> genres;
    private RecyclerView list;

    private int position;
    private boolean isGrid;

    public static Genre newInstance(int position) {
        Genre f = new Genre();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    static final String[] GENRE_SUMMARY_PROJECTION = { MediaStore.Audio.Genres._ID,MediaStore.Audio.Genres.NAME};
    String orderby = MediaStore.Audio.Genres.NAME;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.gallery_grid, container, false);
        final View bg = myFragmentView.findViewById(R.id.bg);
        final View shadow = myFragmentView.findViewById(R.id.toolbar_shadow);
        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fabs);

        //genres = new ArrayList<>();
        list = (RecyclerView) myFragmentView.findViewById(R.id.list);
        mAdapter = new GenresAdapter();

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

        int span, dim;

        if(Prefs.getGenreStyle(getActivity())==1){
            isGrid = true;
            span = getResources().getInteger(R.integer.genre_num_cols);
            dim = (int) getResources().getDimension(R.dimen.grid_margin);
        }else{
            isGrid = false;
            span = getResources().getInteger(R.integer.genre_list_num_cols);;
            dim = (int) getResources().getDimension(R.dimen.jb_grid_space);
        }



        final int numColumns = span;

        if((Library.getGenres().size()/numColumns)<4)
            shadow.setAlpha(0.0f);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numColumns);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (mAdapter.getItemViewType(position) == GenresAdapter.EMPTY
                || mAdapter.getItemViewType(position) == GenresAdapter.ADVIEW) ? numColumns : 1;
            }
        });
        list.setLayoutManager(layoutManager);

        list.addItemDecoration(new GridSpacingItemDecoration(numColumns, (int) getResources().getDimension(R.dimen.grid_margin), true));

        list.setAdapter(mAdapter);

        return myFragmentView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.onLibraryRefreshed();
    }


    public class GenresAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
            implements Library.LibraryRefreshListener{

        public static final int EMPTY = 0;
        public static final int GENRE = 1;
        public static final int ADVIEW = 2;
        ArrayList<com.optimus.music.player.onix.Common.Instances.Genre> genres = Library.getGenres();
        int size = Library.getGenres().size();
        boolean isFBInstalled;
        int index;

        public GenresAdapter(){
            ConnectionDetector cd = new ConnectionDetector(getActivity());

            isFBInstalled = cd.isConnectingToInternet(); //Util.isPackageInstalled(getActivity());
        }

        @Override
        public int getItemCount() {
            //return Library.getGenres().isEmpty() ? 1 : Library.getGenres().size();
            if(Library.getGenres().isEmpty()){
                return 1;
            }else{
                if(isFBInstalled) {
                    if (size + 1 <= 6)
                        return size;
                    else if (size + 2 <= 19)
                        return size + 1;
                    else if (size + 3 <= 32)
                        return size + 2;
                    else
                        return size + 3;
                }else
                    return Library.getGenres().size();
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == GENRE) {
                if(isGrid) {
                    return new GenreViewHolder(
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.genre_grid_item_new, parent, false), true);
                }else{
                    return new GenreViewHolder(
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.artist_list_item, parent, false), false);

                }
            }
            else if(viewType == ADVIEW) {
                return new NativeExpressViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.admob_native_xpress, parent, false));

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
            if(getItemViewType(position)==GENRE) {
                if(isFBInstalled) {
                    if (position < 6)
                        index = position;
                    if (position >= 6)
                        index = position - 1;
                    if (position >= 19)
                        index = index - 1;
                    if (position >= 32)
                        index = index - 1;

                    ((GenreViewHolder) holder).update(genres.get(index));
                }
                else{
                    ((GenreViewHolder) holder).update(genres.get(position));

                }
            }else if(getItemViewType(position)==ADVIEW){
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
            if(Library.getGenres().isEmpty()){
                return EMPTY;
            }
            else {
                if(isFBInstalled) {
                    if (position == 6 || position == 19 || position == 32)
                        return ADVIEW;
                    else
                        return GENRE;
                }else{
                    return GENRE;
                }
            }
        }

        @Override
        public void onLibraryRefreshed() {
            notifyDataSetChanged();
        }
    }


}