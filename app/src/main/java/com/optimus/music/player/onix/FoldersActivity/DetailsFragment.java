package com.optimus.music.player.onix.FoldersActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.SongFileViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.SongViewHolder;
import com.optimus.music.player.onix.SettingsActivity.Prefs;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class DetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private ArrayList<Song> songList;
    private Context context;
    private FolderSongsAdapter adapter;
    private RecyclerView list;
    private TextView all, dash, selected;



    public DetailsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        SharedPreferences prefs = Prefs.getPrefs(getActivity());
        int primary = Integer.parseInt(prefs.getString(Prefs.PRIMARY_COLOR, "0"));
        final View bg = view.findViewById(R.id.bg);
        final View shadow = getActivity().findViewById(R.id.t_shadow);


        int it;
        for(it = mParam1.length()-2;it>0;it--){
            if(mParam1.charAt(it)=='/')
                break;
        }
        String n = mParam1.substring(it + 1, mParam1.length());
        all = (TextView) getActivity().findViewById(R.id.allfolders);
        dash = (TextView) getActivity().findViewById(R.id.dash);
        selected = (TextView) getActivity().findViewById(R.id.selectedfolder);
        dash.setAlpha(1.0f);
        selected.setAlpha(1.0f);
        all.setAlpha(0.5f);
        selected.setText(n);

        context = getActivity();
        list = (RecyclerView) view.findViewById(R.id.list);
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
                if(dy<0){
                    //shadow.setAlpha(1.0f);

                    OnScrollUp(dy);

                }
                else{
                    //shadow.setAlpha(0.0f);

                    OnScrollDown(dy);
                }

            }

            private void OnScrollDown(int dy){
                bg.setTranslationY((bg.getTranslationY() - dy/3));
                if(bg.getTranslationY()==0)
                    shadow.setAlpha(0.0f);
                else
                    shadow.setAlpha(1.0f);
            }
            private void OnScrollUp(int dy){
                bg.setTranslationY((bg.getTranslationY() - dy/3)>0?0:(bg.getTranslationY() - dy/3));
                if(bg.getTranslationY()==0)
                    shadow.setAlpha(0.0f);
                else
                    shadow.setAlpha(1.0f);
            }
        });
        adapter = new FolderSongsAdapter();
        songList = Library.getSongsByFolder(mParam1);

        /*LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);*/
        setRecyclerViewLayoutManager(list);
        list.setAdapter(adapter);
        shadow.setAlpha(0.0f);


        all.setEnabled(true);
        all.setClickable(true);

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().popBackStack();
            }
        });




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
        all.setEnabled(false);
        all.setClickable(false);
        selected.setText(null);
        selected.setAlpha(0.0f);
        dash.setAlpha(0.0f);
        all.setAlpha(1.0f);

    }

    public class FolderSongsAdapter extends RecyclerView.Adapter<SongFileViewHolder> implements Library.LibraryRefreshListener{

        @Override
        public SongFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new SongFileViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.song_item, parent, false),
                    songList);
        }

        @Override
        public void onBindViewHolder(SongFileViewHolder holder, int i) {
            holder.update(songList.get(i), i, context);
        }


        @Override
        public int getItemCount() {
            return songList.size();
        }

        @Override
        public void onLibraryRefreshed() {
            notifyDataSetChanged();
        }


    }


}
