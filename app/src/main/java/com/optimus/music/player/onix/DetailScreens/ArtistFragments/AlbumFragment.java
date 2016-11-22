package com.optimus.music.player.onix.DetailScreens.ArtistFragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.optimus.music.player.onix.Common.Instances.Album;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.DetailScreens.AlbumDetailDemo;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.AlbumArtistViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.AlbumViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.GridSpacingItemDecoration;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Views.GridSpacingDecoration;
import com.optimus.music.player.onix.SettingsActivity.Prefs;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private long mParam1;
    private String mParam2;
    private ArrayList<Album> albums;
    private Album a;
    private Cursor cursor;
    private Context context;

    //private AlbumsFragmentAdapter mAdapter;
    private Adapter adapter;


    public AlbumFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AlbumFragment newInstance(long param1, String param2) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getLong(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        albums = new ArrayList<>();
        context = getActivity();
        RecyclerView grid = (RecyclerView) view.findViewById(R.id.list);
        adapter = new Adapter();


        Uri uri = MediaStore.Audio.Artists.Albums.getContentUri("external", mParam1);
        cursor = getActivity().getContentResolver().query(
                uri,
                Library.albumArtistProjection,
                null, null, null);
        if(cursor!=null)
            cursor.moveToFirst();
        for(int i=0; i<cursor.getCount(); i++){
            cursor.moveToPosition(i);
            a = new Album(
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.LAST_YEAR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART))
            );
            albums.add(a);
        }

        final int numColumns = getResources().getInteger(R.integer.system_ui_modes_cols);

        // Setup the layout manager
        GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //if (adapter.getItemViewType(position) == Adapter.ALBUM_VIEW) return 1;
                return numColumns;
            }
        };

        if(albums.size()>0) {

            grid.setAdapter(adapter);

            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numColumns);
            //layoutManager.setSpanSizeLookup(spanSizeLookup);
            grid.setLayoutManager(layoutManager);

            // Add item decorations
            grid.addItemDecoration(new GridSpacingItemDecoration(numColumns, (int) getResources().getDimension(R.dimen.grid_margin),  true));
        }


        cursor.close();

        return view;

    }

    private class Adapter extends RecyclerView.Adapter<AlbumArtistViewHolder>{
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

            }
        }

        @Override
        public void onBindViewHolder(AlbumArtistViewHolder holder, int position) {
            holder.update(albums.get(position), context);

        }
    }


}
