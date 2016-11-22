package com.optimus.music.player.onix.FoldersActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.optimus.music.player.onix.Common.Instances.MusicFolder;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.MusicPlayer.NowPlayingActivity;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.Themes;
import com.optimus.music.player.onix.Utility.PlaylistDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class FoldersActivity extends NowPlayingActivity {
    private ArrayList<String> dirlist;
    private Cursor cursor;
    Set<String> set;
    private RecyclerView list;
    private FrameLayout container;
    private FoldersAdapter adapter;
    private TextView all, dash, selected;
    private ArrayList<MusicFolder> folders = new ArrayList<>();
    private static final String ARG_PARAM1 = "param1";

    int primary;
    SlidingUpPanelLayout slide;
    RelativeLayout main;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Themes.setTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("My Folders");
            }
        }

        SharedPreferences prefs = Prefs.getPrefs(this);
        primary = Integer.parseInt(prefs.getString(Prefs.PRIMARY_COLOR, "0"));




        all = (TextView) findViewById(R.id.allfolders);
        dash = (TextView) findViewById(R.id.dash);
        selected = (TextView) findViewById(R.id.selectedfolder);
        dash.setAlpha(0.0f);selected.setAlpha(0.0f);
        list = (RecyclerView) findViewById(R.id.list);
        container = (FrameLayout) findViewById(R.id.fragment_container);
        slide = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        main = (RelativeLayout) findViewById(R.id.main);
        colorWindowsCollapsed();
        slide.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if(slide.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED){
                    //Toast.makeText(LibraryActivity.this, "Colour changed", Toast.LENGTH_SHORT).show();
                    colorWindowsCollapsed();
                }
            }
        });


        adapter = new FoldersAdapter();
        list.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);

        all.setEnabled(false);
        all.setClickable(false);
        setPaddingStyle();

    }

    private void colorWindowsCollapsed(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                if(Prefs.colourSB(this)) {
                    window.setStatusBarColor(Themes.getPrimaryDark());
                }else{
                    window.setStatusBarColor(Themes.getBlack());
                }
                if (Prefs.colourNB(this)) {
                    window.setNavigationBarColor(Themes.getPrimaryDark());
                }
            }catch (Exception e){

            }

        }

    }

    private void setPaddingStyle(){
        if(PlayerController.getNowPlaying()==null){
            main.setPadding(0,0,0,0);
        }else{
            if(main.getPaddingBottom()==0){
                main.setPadding(0,0,0, Util.getActionBatHeight(this));
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        setPaddingStyle();
    }



    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.setEnabled(false);
        searchItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(getFragmentManager().getBackStackEntryCount()!=0){
                    if(System.currentTimeMillis()%7 == 0){
                        Util.showAd(getResources().getString(R.string.artist_inter), this);
                    }
                    getFragmentManager().popBackStack();
                }
                else {
                    finish();
                }
                return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount()!=0){
            getFragmentManager().popBackStack();
            if(System.currentTimeMillis()%7 == 0 && System.currentTimeMillis()%4 == 0 || System.currentTimeMillis()%3 == 0 && System.currentTimeMillis()%6 == 0){
                Util.showAd(getResources().getString(R.string.artist_inter), this);
            }
        }
        else {
            super.onBackPressed();
        }
    }

    public class FoldersAdapter extends RecyclerView.Adapter<FolderViewHolder>{


        public FoldersAdapter (){
            if(Library.hasRWPermission(FoldersActivity.this)) {
                (new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                            Library.scanSongs(FoldersActivity.this);
                            folders = Library.getAllMusicFolders(FoldersActivity.this);

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if(!folders.isEmpty()){
                            notifyDataSetChanged();
                        }
                    }
                }).execute();
            }
            else if (!Library.previouslyRequestedRWPermission(FoldersActivity.this)) {
                Library.requestRWPermission(FoldersActivity.this);
            }
        }


        @Override
        public int getItemCount() {
            return folders.size();
        }

        @Override
        public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new FolderViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.folder_item, parent, false));
        }

        @Override
        public void onBindViewHolder(FolderViewHolder holder, int position) {
            holder.update(folders.get(position), position);
        }

        public void remove(MusicFolder reference){
            int index = folders.indexOf(reference);
            folders.remove(reference);
            notifyItemRemoved(index);
        }


    }

    private class FolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
            , PopupMenu.OnMenuItemClickListener{
        private TextView name, path; private ImageView icon, more;
        private String nam;
        private MusicFolder reference;
        private View divider;

        public FolderViewHolder(View itemView){
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.songTitle);
            path = (TextView) itemView.findViewById(R.id.songArtist);
            icon = (ImageView) itemView.findViewById(R.id.list_image);
            more = (ImageView) itemView.findViewById(R.id.expanded_menu);
            divider = itemView.findViewById(R.id.divider);

            more.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        public void update(MusicFolder folder, int position){

            try {

                reference = folder;
                name.setText(folder.displayName);
                path.setText(folder.content);
                //icon.setImageResource(R.drawable.folder_flat_64);
                icon.setColorFilter(Themes.getAccent(), PorterDuff.Mode.SRC_ATOP);

                if (position == folders.size() - 1) {
                    divider.setAlpha(0.0f);
                } else {
                    divider.setAlpha(0.4f);
                }
            }catch (Exception e){

            }


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.expanded_menu:
                    final PopupMenu menu = new PopupMenu(itemView.getContext(), v, Gravity.AXIS_CLIP);
                    String[] options = itemView.getResources()
                            .getStringArray(
                                    R.array.queue_options_folders
                            );

                    for (int i = 0; i < options.length;  i++) {
                        menu.getMenu().add(Menu.NONE, i, i, options[i]);
                    }
                    menu.setOnMenuItemClickListener(this);
                    menu.show();
                    break;
                default:
                    Fragment frag = new DetailsFragment();
                    Bundle args = new Bundle();
                    args.putString(ARG_PARAM1, reference.path);
                    frag.setArguments(args);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, frag)
                            .addToBackStack(null)
                            .commit();
                    break;

            }

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){

                case 0://play
                    PlayerController.setQueue(Library.getSongsByFolder(reference.path), 0);
                    PlayerController.begin();
                    return true;

                case 1://shuffle
                    PlayerController.setQueue(Library.getSongsByFolder(reference.path), 0);
                    PlayerController.begin();
                    if(!PlayerController.isShuffle())
                        PlayerController.toggleShuffle();
                    return true;

                case 2://queue next
                    PlayerController.queueNext(Library.getSongsByFolder(reference.path));
                    return true;

                case 3://queue last
                    PlayerController.queueLast(Library.getSongsByFolder(reference.path));
                    return true;

                case 4://add to playlist
                    PlaylistDialog.AddToNormal.alert(
                            itemView,
                            Library.getSongsByFolder(reference.path),
                            itemView.getContext()
                                    .getString(R.string.header_add_song_name_to_playlist, reference));
                    return true;

                case 5://delete
                    Library.deleteEntireFolder(itemView.getContext(), reference, adapter);
                    //adapter.remove(reference, folders.indexOf(reference));

                    return true;

            }
            return false;
        }
    }

}
