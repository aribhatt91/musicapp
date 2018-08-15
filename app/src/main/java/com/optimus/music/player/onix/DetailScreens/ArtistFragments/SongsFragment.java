package com.optimus.music.player.onix.DetailScreens.ArtistFragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.DetailScreens.AllTracksByArtist;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.SongViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.SongViewHolderBlank;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


public class SongsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static String ARG_PARAM1;
    private static String ARG_PARAM2;

    // TODO: Rename and change types of parameters
    private long mParam1;
    private String mParam2;
    private ArrayList<Song> songsList;
    String[] arguments;
    private Context context;
    private ArtistSongsAdapter artistSongsAdapter;
    private RecyclerView list;



    public SongsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SongsFragment newInstance(long param1, String param2) {
        SongsFragment fragment = new SongsFragment();
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
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        context = getActivity();
        String selection = MediaStore.Audio.Media.ARTIST + "=?";
        arguments = new String[]{mParam2};

        list = (RecyclerView) view.findViewById(R.id.list);

        Cursor cur = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Library.songProjection,
                selection,
                arguments,
                MediaStore.Audio.Media.TITLE);

        songsList = new ArrayList<Song>();

        if(cur != null){
                cur.moveToFirst();



            for (int k = 0; k<cur.getCount(); k++){
                cur.moveToPosition(k);
                songsList.add(new Song(cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                                cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                                cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)),
                                cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                                cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                                cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)),
                                cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)),
                                cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)),
                                cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)),
                                cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)))

                );
            }

            cur.close();
        }

        if(songsList.size()>0){
            artistSongsAdapter = new ArtistSongsAdapter();
            list.setAdapter(artistSongsAdapter);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            list.setLayoutManager(layoutManager);

        }


        return view;
    }



    public class ArtistSongsAdapter extends RecyclerView.Adapter<SongViewHolderBlank> implements Library.LibraryRefreshListener{

        @Override
        public SongViewHolderBlank onCreateViewHolder(ViewGroup parent, int viewType) {

            return new SongViewHolderBlank(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.song_item, parent, false),
                    songsList);
        }

        @Override
        public void onBindViewHolder(SongViewHolderBlank holder, int i) {
                holder.update(songsList.get(i), i, context);
        }


        @Override
        public int getItemCount() {
            return songsList.size();
        }

        @Override
        public void onLibraryRefreshed() {
            notifyDataSetChanged();
        }


    }


}
