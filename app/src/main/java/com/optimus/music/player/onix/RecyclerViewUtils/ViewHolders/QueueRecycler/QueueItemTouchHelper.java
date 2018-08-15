package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.QueueRecycler;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.MusicPlayer.PlayerController;
import com.optimus.music.player.onix.MusicPlayer.QueueFragment;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by longclaw on 7/10/16.
 */
public class QueueItemTouchHelper extends ItemTouchHelper.Callback {

    ItemTouchHelperAdapter
            queueAdapter;

    private float ALPHA_FULL = 1.0f;


    public QueueItemTouchHelper(ItemTouchHelperAdapter queueAdapter){
        this.queueAdapter = queueAdapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT);
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        /*
        ArrayList<Song> editedQueue = new ArrayList<>(PlayerController.getQueue());
        if (editedQueue != null && !editedQueue.isEmpty()) {
            Collections.swap(editedQueue, viewHolder.getAdapterPosition(), target.getAdapterPosition());
            int pos = editedQueue.indexOf(PlayerController.getNowPlaying());
            if(pos >=0){
                PlayerController.editQueue(editedQueue, pos);
            }
            return true;
        }
        */
        Log.d("MOVE", "Move called");

        if(viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }


        queueAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
        Log.d("CANDROP", "called");
        return current.getItemViewType()==target.getItemViewType();
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        queueAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
