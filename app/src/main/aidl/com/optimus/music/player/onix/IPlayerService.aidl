// IPlayerService.aidl
package com.optimus.music.player.onix;

// Declare any non-default types here with import statements

// Declare any non-default types here with import statements
import com.optimus.music.player.onix.Common.Instances.Song;

interface IPlayerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    void stop();
    void skip();
    void previous();
    void begin();
    void togglePlay();
    void play();
    void pause();
    //void setPrefs(boolean shuffle, int repeat);
    void setQueue(in List<Song> newQueue, int newPosition);
    void changeSong(int position);
    void editQueue(in List<Song> newQueue, int newPosition);
    void queueNext(in Song song);
    void queueNextList(in List<Song> songs);
    void queueLast(in Song song);
    void queueLastList(in List<Song> songs);
    //void seek(int position);
    
//change these 3
    void setShuffle(boolean shuffle);
    void setRepeat(int repeat);

    void seekTo(int position);


    boolean isPlaying();
    boolean isPreparing();//delete this
    Song getNowPlaying();
    List<Song> getQueue();
    int getQueuePosition();
    int getCurrentPosition();
    int getDuration();
    int getAudioSessionId();
//this as well
    int getQueueSize();


    long getSleepTimerEndTime();
    void setSleepTimerEndTime(long timestampInMillis);



}
