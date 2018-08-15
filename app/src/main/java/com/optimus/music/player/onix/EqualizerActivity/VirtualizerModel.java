package com.optimus.music.player.onix.EqualizerActivity;

import android.media.audiofx.Virtualizer;

import com.optimus.music.player.onix.MusicPlayer.PlayerController;

/**
 * Created by apricot on 20/6/16.
 */
public class VirtualizerModel {

    Virtualizer virtualizer;

    public VirtualizerModel(){
        virtualizer = new Virtualizer(0, PlayerController.getAudioSessionId());
    }

    public static void setStrengthString(StringBuilder stringBuilder, int strength){
        stringBuilder.append(strength/10);
        stringBuilder.append('%');
    }

    public boolean getStrengthSupported(){
        try {
            return virtualizer.getStrengthSupported();
        }catch (Exception e){
            return false;
        }
    }

    public void setStrength(short strength){
        try {
            virtualizer.setStrength(strength);
        }catch (Exception e){

        }
    }

    public short getRoundedStrength(){
        try {
            return virtualizer.getRoundedStrength();
        }catch (Exception e){
            return 0;
        }
    }

    public int setEnabled(boolean enabled){
        return virtualizer.setEnabled(enabled);

    }

    public boolean getEnabled(){
        return virtualizer.getEnabled();
    }

    public void release(){
        virtualizer.release();
    }
}
