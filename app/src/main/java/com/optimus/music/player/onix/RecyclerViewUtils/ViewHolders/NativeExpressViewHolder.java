package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.R;

/**
 * Created by apricot on 27/5/16.
 */
public class NativeExpressViewHolder extends RecyclerView.ViewHolder {

    public NativeExpressViewHolder(View itemView){
        super(itemView);
        NativeExpressAdView adView = (NativeExpressAdView) itemView.findViewById(R.id.adView);
        //AdSize size = new AdSize(AdSize.FULL_WIDTH, 150);
        //adView.setAdSize(size);
        //adView.setAdUnitId("ca-app-pub-6990279087572974/8792867440");
        try {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(Library.TEST_DEVICE_ID)
                    .build();
            adView.loadAd(adRequest);
        }catch (Exception e){

        }


    }
}
