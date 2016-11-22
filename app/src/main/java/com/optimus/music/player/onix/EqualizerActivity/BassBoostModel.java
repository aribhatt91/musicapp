package com.optimus.music.player.onix.EqualizerActivity;

import android.media.audiofx.BassBoost;

import com.optimus.music.player.onix.MusicPlayer.PlayerController;

/**
 * Created by apricot on 20/6/16.
 */
public class BassBoostModel {
    private BassBoost bassBoost;

    public BassBoostModel(){
        bassBoost = new BassBoost(0, PlayerController.getAudioSessionId());
    }

    public static void setStrengthString(StringBuilder stringBuilder, int strength){
        stringBuilder.append(strength/10);
        stringBuilder.append('%');
    }

    public boolean getStrengthSupported(){
        try {
            return bassBoost.getStrengthSupported();
        }catch (Exception e){
            return false;
        }
    }

    public void setStrength(short strength){
        try {
            bassBoost.setStrength(strength);
        }catch (Exception e){

        }
    }

    public short getRoundedStrength(){
        try {
            return bassBoost.getRoundedStrength();
        }catch (Exception e){
            return 0;
        }
    }

    public int setEnabled(boolean enabled){
        return bassBoost.setEnabled(enabled);

    }

    public boolean getEnabled(){
        return bassBoost.getEnabled();
    }

    public void release(){
        bassBoost.release();
    }
}
