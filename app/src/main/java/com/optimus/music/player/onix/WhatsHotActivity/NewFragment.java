package com.optimus.music.player.onix.WhatsHotActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.optimus.music.player.onix.Common.ConnectionDetector;
import com.optimus.music.player.onix.Common.Instances.YTVideo;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc.GridSpacingItemDecoration;

import java.util.ArrayList;


public class NewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;



    public static NewFragment newInstance(int param1, String param2) {
        NewFragment fragment = new NewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public NewFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        RecyclerView list = (RecyclerView) view.findViewById(R.id.list);
        final int numColumns = getResources().getInteger(R.integer.genre_num_cols);
        boolean isConneted;
        ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());
        isConneted = cd.isConnectingToInternet();
        ArrayList<YTVideo> videos = new ArrayList<>();
        int AD = 1;
        if(mParam1==0){
            videos = VideoLibrary.createInternationalPlaylist();
            AD=1;
        }else if(mParam1==1){
            videos = VideoLibrary.createRBPlaylist();
            AD=3;
        }else if(mParam1==2){
            videos = VideoLibrary.createDancePlaylist();
            AD = 2;
        }else if(mParam1==3){
            videos = VideoLibrary.createBollyPlaylist();
            AD=1;
        }else if(mParam1==4){
            videos = VideoLibrary.createNewSensPlaylist();
            AD=3;
        }

        if(isConneted && !videos.isEmpty()){

            final RecyclerAdapter adapter = new RecyclerAdapter(getActivity(), videos, AD);
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numColumns);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (adapter.getItemViewType(position) == RecyclerAdapter.ADVIEW) ? numColumns : 1;
                }
            });
            list.setLayoutManager(layoutManager);
            list.addItemDecoration(new GridSpacingItemDecoration(numColumns, (int) getResources().getDimension(R.dimen.jb_grid_space), true));
            list.hasFixedSize();
            list.setAdapter(adapter);

        }


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
