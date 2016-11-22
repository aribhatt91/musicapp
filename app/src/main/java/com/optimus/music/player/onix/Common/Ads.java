package com.optimus.music.player.onix.Common;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by apricot on 30/4/16.
 */
public class Ads {
    public static String[] Fb_native = {
            "1618833158439619_1621485054841096",
            "1618833158439619_1621739711482297",
            "1618833158439619_1621739848148950",
            "1618833158439619_1621739981482270"
    };

    public static String[] Admob_native = {
            "ca-app-pub-6990279087572974/8946777048",
            "ca-app-pub-6990279087572974/4457701846",
            "ca-app-pub-6990279087572974/5934435040"
    };

    public static void showAd(final InterstitialAd ad){
        AdRequest adRequest = new  AdRequest.Builder()
                .addTestDevice(Library.TEST_DEVICE_ID)
                .build();
        ad.loadAd(adRequest);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdLoaded() {
                ad.show();
                //showAd();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }
        });
        if(ad.isLoaded()){
            ad.show();
        }
    }
}
