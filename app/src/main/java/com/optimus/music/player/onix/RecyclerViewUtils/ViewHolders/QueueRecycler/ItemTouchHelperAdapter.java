package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.QueueRecycler;

/**
 * Created by longclaw on 7/10/16.
 */
public interface ItemTouchHelperAdapter {

    boolean onItemMove(int from, int to);

    void onItemDismiss(int position);
}
