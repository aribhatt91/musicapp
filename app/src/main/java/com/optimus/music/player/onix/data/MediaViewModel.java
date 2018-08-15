package com.optimus.music.player.onix.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import io.reactivex.annotations.NonNull;

public class MediaViewModel extends AndroidViewModel{

    public MediaViewModel(@NonNull Application application, @NonNull Repository repository){
        super(application);
    }
}
