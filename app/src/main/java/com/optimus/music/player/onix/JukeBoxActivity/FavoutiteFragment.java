package com.optimus.music.player.onix.JukeBoxActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.GridSpacingItemDecoration;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.SongViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.SongViewHolderBlank;

import java.util.ArrayList;


public class FavoutiteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context context;


    public FavoutiteFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FavoutiteFragment newInstance(String param1, String param2) {
        FavoutiteFragment fragment = new FavoutiteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        RecyclerView list = (RecyclerView) view.findViewById(R.id.list);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Favourites");

        context = getActivity();

        FavSongAdapter mAdapter = new FavSongAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        list.setAdapter(mAdapter);


        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("JukeBox");


    }

    private class FavSongAdapter extends RecyclerView.Adapter<SongViewHolderBlank>{
        ArrayList<Song> songList;

        public FavSongAdapter(){
            songList = JukeBoxActivity.songEntries;
        }

        @Override
        public SongViewHolderBlank onCreateViewHolder(ViewGroup parent, int viewType) {

            return new SongViewHolderBlank(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.song_item, parent, false),
                    songList);
        }

        @Override
        public void onBindViewHolder(SongViewHolderBlank holder, int i) {
            holder.update(songList.get(i), i, context);
        }



        @Override
        public int getItemCount() {
            return songList.size();
        }




    }


}
