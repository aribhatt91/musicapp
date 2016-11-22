package com.optimus.music.player.onix;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.optimus.music.player.onix.SettingsActivity.Prefs;
import com.optimus.music.player.onix.SettingsActivity.Themes;

import java.util.ArrayList;

/**
 * Created by apricot on 9/8/15.
 */
public class NavAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavItem> navDrawerItems;
    int primary;

    public NavAdapter(Context context, ArrayList<NavItem> navDrawerItems, int primary){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        this.primary = primary;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.navlistitem, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
     //   imgIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        //RelativeLayout sq = (RelativeLayout) convertView.findViewById(R.id.nav_square);
        int accent;
        try {
            if (primary == 0) {
                accent = ContextCompat.getColor(context, R.color.metalYellow);
                imgIcon.setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
            } else if (primary == 10) {
                accent = ContextCompat.getColor(context, R.color.partyAccent);
                imgIcon.setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
            } else if (primary == 6) {
                accent = ContextCompat.getColor(context, R.color.passionBackground);
                imgIcon.setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
            } else if (primary == 7) {
                accent = ContextCompat.getColor(context, R.color.midnightAccent);
                imgIcon.setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
            }
        }catch (Exception e){

        }


        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

        try {
            //Glide.with(context).load(navDrawerItems.get(position).getIcon()).into(imgIcon);

            imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
        }catch (Exception e){

        }catch (OutOfMemoryError e){

        }
        //sq.setBackgroundColor(navDrawerItems.get(position).getColId());
        txtTitle.setText(navDrawerItems.get(position).getTitle());

        return convertView;
    }
}
