package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Misc;

/**
 * Created by apricot on 13/12/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.optimus.music.player.onix.LibraryActivity;

public class Navigate {

    public static void to(Context parent, Class<?  extends Activity> target) {
        to(parent, target, null, null);
    }

    public static void to(Context parent, Class<?  extends Activity> target, String extraName, Parcelable extra) {
        Intent intent = new Intent(parent, target);

        if (extraName != null && extra != null) {
            intent.putExtra(extraName, extra);
        }

        parent.startActivity(intent);
    }

    public static void up(Activity parent) {
        parent.finish();
    }

    public static void back(Activity parent) {
        parent.finish();
    }

    public static void home(Context parent) {
        parent.startActivity(new Intent(parent, LibraryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}