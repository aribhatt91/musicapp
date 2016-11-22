package com.optimus.music.player.onix.DetailScreens.ArtistFragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.optimus.music.player.onix.Common.ConnectionDetector;
import com.optimus.music.player.onix.Common.Instances.Artist;
import com.optimus.music.player.onix.Common.Instances.ArtistProfile;
import com.optimus.music.player.onix.Common.lastFM.ImageList;
import com.optimus.music.player.onix.Common.lastFM.LArtist;
import com.optimus.music.player.onix.Common.lastFM.Query;
import com.optimus.music.player.onix.Common.lastFM.Tag;
import com.optimus.music.player.onix.CrazyDataStore.ArtistNames;
import com.optimus.music.player.onix.CrazyDataStore.Artists;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.SettingsActivity.Themes;
import com.optimus.music.player.onix.Utility.CircularImageView;
import com.optimus.music.player.onix.Utility.ViewUtils.MaterialProgressDrawable;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class BioFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private long mParam2;
    private int mParam3;
    //private Artist reference;
    private LArtist lfmReference;
    private List<Artist> relatedArtists;
    private String Bio, url, website;
    private boolean hasBio = false;
    private BioAdapter adapter;
    private ArtistProfile artistprofile;

    private RecyclerView list;
    private MaterialProgressDrawable spinner;
    boolean isConneted;




    public BioFragment() {
        // Required empty public constructor
    }


    public static BioFragment newInstance(String param1, long param2, int param3) {
        BioFragment fragment = new BioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putLong(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getLong(ARG_PARAM2);
            mParam3 = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bio, container, false);
        list = (RecyclerView) view.findViewById(R.id.list);

        artistprofile = new ArtistProfile(mParam3, mParam1);
        try {
            ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());
            isConneted = cd.isConnectingToInternet();
        }catch (Exception e){
            e.printStackTrace();
        }

        adapter = new BioAdapter();
        list.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);

        return view;
    }



    private class BioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BioViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.bio_header, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

    }



    public class BioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView bio, title, notFound;
        ImageView yt, tw, fb, in;
        CircularImageView profile;
        FrameLayout lfm;
        LinearLayout holder;
        private final String def = "We couldn't find any information regarding this artist.";
        public BioViewHolder(View view){
            super(view);
            bio = (TextView) view.findViewById(R.id.bio);
            title = (TextView) view.findViewById(R.id.artistTitle);
            profile = (CircularImageView) view.findViewById(R.id.profile_image);
            lfm = (FrameLayout) view.findViewById(R.id.readmore);
            holder = (LinearLayout) view.findViewById(R.id.bioViewHolder);
            notFound =(TextView) view.findViewById(R.id.notFound);
            yt = (ImageView) view.findViewById(R.id.i1);
            tw = (ImageView) view.findViewById(R.id.i2);
            fb = (ImageView) view.findViewById(R.id.i3);
            in = (ImageView) view.findViewById(R.id.i4);

            lfm.setOnClickListener(this);
            yt.setOnClickListener(this);
            tw.setOnClickListener(this);
            fb.setOnClickListener(this);
            in.setOnClickListener(this);
            setAlpha();
            setData();

        }

        public void setAlpha(){
            if(artistprofile.getYT().isEmpty()){
                yt.setAlpha(0.5f);
            }
            if(artistprofile.getTW().isEmpty()){
                tw.setAlpha(0.5f);

            }
            if(artistprofile.getFB().isEmpty()){
                fb.setAlpha(0.5f);

            }
            if(artistprofile.getIN().isEmpty()){
                in.setAlpha(0.5f);
            }
        }
        public void setData(){

            new AsyncTask<Void, Void, Void>(){
                ProgressDialog pd;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //spinner.setAlpha(255);
                    //spinner.start();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    try {

                        if(isConneted) {

                                if(mParam3>=0)
                                    lfmReference = Query.getArtist(getActivity(), Artists.artists[mParam3], mParam2);

                                if (lfmReference == null)
                                    lfmReference = Query.getArtist(getActivity(), mParam1, mParam2);


                        }

                        else{
                            lfmReference=null;
                        }
                    } catch (IOException | ParserConfigurationException | SAXException e) {
                        e.printStackTrace();
                        lfmReference = null;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    //spinner.setAlpha(0);
                    //spinner.stop();

                    if(lfmReference!=null) {
                        url = lfmReference.getImageURL(ImageList.SIZE_MEGA);
                        website = lfmReference.getUrl();
                        String summary = lfmReference.getBio().getSummary();
                        Tag[] tags = lfmReference.getTags();
                        String[] tagnames = new String[tags.length];


                        for (int i = 0; i < tags.length; i++) {
                            tagnames[i] = tags[i].getName();
                        }
                        String taglist = " ";
                        if (tags.length > 0) {
                            taglist = tagnames[0].substring(0, 1).toUpperCase() + tagnames[0].substring(1);
                            int tagcount = (tags.length < 5) ? tags.length : 5;
                            for (int i = 1; i < tagcount; i++) {
                                taglist += ", " + tagnames[i].substring(0, 1).toUpperCase() + tagnames[i].substring(1);
                            }
                        }

                        if (summary.length() > 0) {
                            summary = summary.substring(0, summary.lastIndexOf("<a href=\""));
                        }
                        Bio = taglist + ((taglist.trim().length() > 0 && summary.trim().length() > 0) ? "\n\n" : "") + summary;
                        bio.setText(Bio);
                        title.setText(mParam1);
                        notFound.setText(null);
                        if(url!=null){
                            Glide.with(getActivity())
                                    .load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                    .centerCrop()
                                    .placeholder(R.drawable.head_128)
                                    .into(profile);
                        }
                    }
                    else{
                        //Toast.makeText(getActivity(), "Not Found", Toast.LENGTH_LONG).show();

                    }
                }
            }.execute();

        }

        @Override
        public void onClick(View v) {
            if(v.equals(lfm)){
                try {
                    Intent openLFMIntent = new Intent(Intent.ACTION_VIEW);
                    if (website == null)
                        openLFMIntent.setData(Uri.parse("http://www.last.fm/home"));
                    else openLFMIntent.setData(Uri.parse(website));
                    itemView.getContext().startActivity(openLFMIntent);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(v.getContext(), "No application found", Toast.LENGTH_SHORT).show();
                }
            }
            else if(v.getId() == R.id.i1){//youtube
                if(!artistprofile.getYT().isEmpty()) {
                    try {
                        String yt = artistprofile.getYT();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(yt));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        v.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(v.getContext(), "No application found", Toast.LENGTH_SHORT).show();
                    }
                }


            }
            else if(v.getId() == R.id.i2){//twitter
                if(!artistprofile.getTW().isEmpty()) {
                    String twit = "https://www.twitter.com/" + artistprofile.getTW().trim();
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twit));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        v.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(v.getContext(), "No application found", Toast.LENGTH_SHORT).show();
                    }
                }
            }else if(v.getId() == R.id.i3){//facebook
                if(!artistprofile.getFB().isEmpty()){

                    String fb = artistprofile.fb;
                    String url = getFacebookPageURL(getActivity(), fb);

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse(url));
                        v.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(v.getContext(), "No application found", Toast.LENGTH_SHORT).show();
                    }

                }

            }else if(v.getId() == R.id.i4){//instagram
                if(!artistprofile.getIN().isEmpty()) {
                    String ins = artistprofile.ins;

                    try {
                        Uri uri = Uri.parse("https://www.instagram.com/_u/" + ins);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        v.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/" + ins));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        v.getContext().startActivity(intent);

                    }
                }

            }
        }
    }

    public static String getFacebookPageURL(Context context, String FACEBOOK_PAGE_ID) {
        String FACEBOOK_URL = "https://www.facebook.com/"+FACEBOOK_PAGE_ID.trim();
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }



}
