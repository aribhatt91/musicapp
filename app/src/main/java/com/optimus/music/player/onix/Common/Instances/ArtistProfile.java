package com.optimus.music.player.onix.Common.Instances;

import com.optimus.music.player.onix.CrazyDataStore.ArtistNames;
import com.optimus.music.player.onix.CrazyDataStore.Artists;
import com.optimus.music.player.onix.CrazyDataStore.Facebook;
import com.optimus.music.player.onix.CrazyDataStore.Instagram;
import com.optimus.music.player.onix.CrazyDataStore.Twitter;
import com.optimus.music.player.onix.CrazyDataStore.Youtube;

/**
 * Created by apricot on 24/4/16.
 */
public class ArtistProfile {
    public  String fb, tw, ins, yt;
    private String empty = "";
    public ArtistProfile(int i, String name){
        if(i>-1 && i < Artists.artists.length ){
            if(i < Facebook.facebook.length){
                String temp = Facebook.facebook[i];
                if(!temp.isEmpty() && temp.contains("https://www.facebook.com/"))
                    temp = temp.replace("https://www.facebook.com/", "");
                this.fb = temp;
            }else{
                this.fb = empty;
            }

            if(i < Twitter.twitter.length){
                String temp = Twitter.twitter[i];
                if(temp.trim().toLowerCase().contains("https://twitter.com/")){
                    this.tw = temp.replace("https://twitter.com/", "");
                }
                else {
                    this.tw = temp;
                }
            }
            else {
                this.tw = empty;
            }

            if(i < Instagram.insta.length){
                String temp = Instagram.insta[i];
                if(temp.trim().toLowerCase().contains("https://www.instagram.com/")){
                    temp = temp.replace("https://www.instagram.com/", "");
                    if(temp.contains("/")){
                        String[] subs = temp.split("/");
                        this.ins = subs[0].trim();
                    }
                }
                else {
                    this.ins = temp.trim().toLowerCase();
                }
            }
            else{
                this.ins = empty;
            }

            if( i < Youtube.youtube.length){
                this.yt = Youtube.youtube[i].trim();
            }else{
                this.yt = empty;
            }

        }
        else{
            this.fb = empty;
            this.tw = empty;
            this.ins = empty;
            this.yt = empty;
        }
    }

    public String getFB(){
        if(this.fb!=null){
            return this.fb;
        }else{
            return empty;
        }
    }

    public String getYT(){
        if(this.yt!=null){
            return this.yt;
        }else{
            return empty;
        }
    }

    public String getIN(){
        if(this.ins!=null){
            return this.ins;
        }else{
            return empty;
        }
    }

    public String getTW(){
        if(this.tw!=null){
            return this.tw;
        }else{
            return empty;
        }
    }
}
