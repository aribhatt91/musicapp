package com.optimus.music.player.onix.Common.Instances;

import java.util.Comparator;

/**
 * Created by apricot on 28/3/16.
 */
public class MusicFolder {
    public String displayName, path, content;

    public MusicFolder(String displayName, String path, String content){
        this.displayName = displayName;
        this.path = path;
        this.content = content;
    }

    public static final Comparator<MusicFolder> DISPLAY_NAME_COMPARATOR = new Comparator<MusicFolder>() {
        @Override
        public int compare(MusicFolder lhs, MusicFolder rhs) {
            return lhs.displayName.compareTo(rhs.displayName);
        }
    };
    public static final Comparator<MusicFolder> PATH_NAME_COMPARATOR = new Comparator<MusicFolder>() {
        @Override
        public int compare(MusicFolder lhs, MusicFolder rhs) {
            return lhs.path.compareTo(rhs.path);
        }
    };
}
