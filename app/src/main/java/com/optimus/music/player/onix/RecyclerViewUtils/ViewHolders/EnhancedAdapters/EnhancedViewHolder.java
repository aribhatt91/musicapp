package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;


public abstract class EnhancedViewHolder<Type> extends RecyclerView.ViewHolder {

    /**
     * @param itemView The view that this ViewHolder will manage
     */
    public EnhancedViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * Called when this ViewHolder has been recycled and needs to be populated with new data
     * @param item The item to show in this ViewHolder
     * @param position The index of this item in the adapter's data set
     */
    public abstract void update(Type item, int position);

}
