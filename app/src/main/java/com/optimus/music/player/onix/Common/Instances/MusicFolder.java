package com.optimus.music.player.onix.Common.Instances;

import java.util.Comparator;

/**
 * Created by apricot on 28/3/16.
 */
public class MusicFolder implements Comparable<MusicFolder> {
    public String displayName, path, content;
    int count;

    public MusicFolder(String displayName, String path, String content){
        this.displayName = displayName;
        this.path = path;
        this.content = content;
        //this.count = Integer.parseInt(content.split(" ")[0]);
    }

    public MusicFolder(String displayName, String path, String content, int count){
        this.displayName = displayName;
        this.path = path;
        this.content = content;
        this.count = count;
    }

    public int getCount(){
        return count;
    }

    public static final Comparator<MusicFolder> DISPLAY_NAME_COMPARATOR = new Comparator<MusicFolder>() {
        @Override
        public int compare(MusicFolder lhs, MusicFolder rhs) {
            try {
                return (lhs.displayName.toLowerCase()).compareTo(rhs.displayName.toLowerCase());
            }catch (Exception e){
                return 0;
            }
        }
    };
    public static final Comparator<MusicFolder> PATH_NAME_COMPARATOR = new Comparator<MusicFolder>() {
        @Override
        public int compare(MusicFolder lhs, MusicFolder rhs) {
            try {
                return (lhs.path.toLowerCase()).compareTo(rhs.path.toLowerCase());
            }catch (Exception e){
                return 0;
            }
        }
    };

    public static final Comparator<MusicFolder> COUNT_COMPARATOR_ASC = new Comparator<MusicFolder>() {
        @Override
        public int compare(MusicFolder lhs, MusicFolder rhs) {
            try {
                return (lhs.count)-(rhs.count);
            }catch (Exception e){
                return 0;
            }
        }
    };

    public static final Comparator<MusicFolder> COUNT_COMPARATOR_DESC = new Comparator<MusicFolder>() {
        @Override
        public int compare(MusicFolder lhs, MusicFolder rhs) {
            try {
                return (rhs.count)-(lhs.count);
            }catch (Exception e){
                return 0;
            }
        }
    };

    @Override
    public int compareTo(MusicFolder o) {
        try {
            return (this.displayName.toLowerCase()).compareTo(o.displayName.toLowerCase());
        }catch (Exception e){
            return 0;
        }
    }
}
