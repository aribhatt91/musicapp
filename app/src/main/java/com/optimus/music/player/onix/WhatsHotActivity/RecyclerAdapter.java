package com.optimus.music.player.onix.WhatsHotActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.optimus.music.player.onix.Common.Instances.YTVideo;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.NativeExpressViewHolder;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ADVIEW = 1;
    public final int YVIEW = 0;
    int index;
    int AD;

    ArrayList<YTVideo> videos;
    Context ctx;
    String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    public RecyclerAdapter(Context context, ArrayList<YTVideo> videos, int AD) {
        this.ctx = context;
        this.videos = videos;
        this.AD = AD;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==ADVIEW){
            if(AD==1){
                return new NativeExpressViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.native_one, parent, false));

            }else if(AD==2){
                return new NativeExpressViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.native_two, parent, false));

            }else{
                return new NativeExpressViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.native_three, parent, false));

            }

        }
        else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_item, parent, false);
            return new VideoInfoHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(getItemViewType(position)==ADVIEW){
        }
        else {
            if(position<6)
                index = position;
            if(position>=6)
                index = position - 1;
            if(position>=19)
                index = index - 1;
            if(position>=32)
                index = index - 1;
            if(position >= 45)
                index = index - 1;


            ((VideoInfoHolder) holder).onUpdate(videos.get(index));
        }

    }

    @Override
    public int getItemCount() {

        if(videos.size() + 2 <=19)
            return videos.size() + 1;

        if(videos.size() + 3 <=32)
            return videos.size() + 2;

        if(videos.size() + 4 <= 45)
            return videos.size() + 3;

        else
            return videos.size() + 4;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 6 || position==19 || position==32 || position==45 ){
            return ADVIEW;
        }else
            return YVIEW;
    }

    public class VideoInfoHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{


        protected ImageView star, youTubeThumbnailView, menu;
        protected TextView title, subtext;
        private YTVideo reference;

        public VideoInfoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            youTubeThumbnailView = (ImageView) itemView.findViewById(R.id.youtube_thumbnail);
            title = (TextView) itemView.findViewById(R.id.video_name);
            subtext = (TextView) itemView.findViewById(R.id.subtext);
            menu = (ImageView) itemView.findViewById(R.id.instanceMore);
            menu.setOnClickListener(this);
            //star = (ImageView) itemView.findViewById(R.id.star);
        }

        public void onUpdate(YTVideo v){
            this.reference = v;
            title.setText(v.name);
            subtext.setText(v.artist);
            youTubeThumbnailView.setImageResource(R.drawable.transparent);
            Glide.with(itemView.getContext())
                    .load(v.imageURI)
                    .crossFade(300)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(youTubeThumbnailView);


        }



        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.instanceMore:
                    final android.support.v7.widget.PopupMenu menu = new android.support.v7.widget.PopupMenu(itemView.getContext(), v, Gravity.AXIS_CLIP);
                    String[] options = itemView.getResources()
                            .getStringArray(
                                    R.array.yt_menu
                            );

                    for (int i = 0; i < options.length;  i++) {
                        menu.getMenu().add(Menu.NONE, i, i, options[i]);
                    }
                    menu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case 0:
                                    if(reference!=null) {
                                        String uri = YOUTUBE_URL + reference.url;
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                        ctx.startActivity(intent);
                                    }
                                    return true;
                            }
                            return false;
                        }
                    });
                    menu.show();
                    break;
                default:

                if (reference != null) {
                    try {
                        Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) ctx, YTApi.KEY, reference.url);
                        ctx.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        String uri = YOUTUBE_URL + reference.url;

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        ctx.startActivity(intent);
                        Toast.makeText(itemView.getContext(), "It seems you haven't got YouTube installed!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}