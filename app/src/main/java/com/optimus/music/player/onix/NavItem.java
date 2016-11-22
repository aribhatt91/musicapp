package com.optimus.music.player.onix;

/**
 * Created by apricot on 9/8/15.
 */
public class NavItem {
    private String title;
    //private int colid;
    private int icon;

    public NavItem(){}

    public NavItem(String title, int icon){
        this.title = title;
       // this.colid = colid;
        this.icon = icon;
    }
    public String getTitle(){
        return this.title;
    }
    /*public int getColId(){
        return this.colid;
    }*/


    public int getIcon(){
        return this.icon;
    }

}
