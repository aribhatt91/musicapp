package com.optimus.music.player.onix.MusicPlayer;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.optimus.music.player.onix.Common.Instances.Song;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.DragBackgroundDecoration;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.DragDropAdapter;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.EnhancedAdapters.DragDropDecoration;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.QueueRecycler.ItemTouchHelperAdapter;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.QueueRecycler.OnStartDragListener;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.QueueRecycler.QueueItemTouchHelper;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.QueueSongViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Views.QueueItemViewHolder;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.section.LibraryEmptyState;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.section.QueueSection;
import com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.section.SpacerSingleton;
import com.optimus.music.player.onix.SettingsActivity.Themes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueueFragment extends Fragment implements PlayerController.UpdateListener, OnStartDragListener {

    private ArrayList<Song> queue;
    private int lastPlayIndex;
    private RecyclerView list;
    private DragDropAdapter adapter;
    private SpacerSingleton bottomSpacer;
    private int itemHeight;
    private int dividerHeight;

    QueueAdapter queueAdapter;
    ItemTouchHelper helper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerlist, container, false);

        // Remove the list padding on landscape tablets
        /*
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE
                && config.smallestScreenWidthDp >= 600) {
            view.setPadding(0, 0, 0, 0);
        }*/
        queue = (ArrayList<Song>)PlayerController.getQueue();

        list = (RecyclerView) view.findViewById(R.id.list);

        itemHeight = (int) getResources().getDimension(R.dimen.list_height);
        dividerHeight = (int) getResources().getDisplayMetrics().density;
        bottomSpacer = new SpacerSingleton(QueueSection.ID, 0);

        queueAdapter = new QueueAdapter(this);
        list.setHasFixedSize(true);
        list.setAdapter(queueAdapter);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.Callback callback = new QueueItemTouchHelper(queueAdapter);
        helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(list);

        /*
        adapter = new DragDropAdapter();
        adapter.setDragSection(new QueueSection(queue));
        adapter.addSection(bottomSpacer);
        adapter.setEmptyState(new LibraryEmptyState(getActivity()) {
            @Override
            public String getEmptyMessage() {
                return getString(R.string.empty_queue);
            }

            @Override
            public String getEmptyMessageDetail() {
                return getString(R.string.empty_queue_detail);
            }

            @Override
            public String getEmptyAction1Label() {
                return "";
            }

            @Override
            public String getEmptyAction2Label() {
                return "";
            }
        });

        list = (RecyclerView) view.findViewById(R.id.list);

        adapter.attach(list);

        list.addItemDecoration(new DragBackgroundDecoration(Themes.getBackground(getActivity())));
        //noinspection deprecation

        list.addItemDecoration(new DragDropDecoration((NinePatchDrawable) ContextCompat.getDrawable(getActivity(),
                (Themes.getThemeId(getActivity()) == 0 || Themes.getThemeId(getActivity())==6 || Themes.getThemeId(getActivity())==7)
                        ? R.drawable.list_drag_shadow_dark
                        : R.drawable.list_drag_shadow_light
        )));
        */


        /*
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE
                || getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Add an inner shadow on phones and portrait tablets
            list.addItemDecoration(new InsetDecoration(
                    getResources().getDrawable(R.drawable.inset_shadow),
                    (int) getResources().getDimension(R.dimen.inset_shadow_height)));
        }*/

        /*
            Because of the way that CoordinatorLayout lays out children, there isn't a way to get
            the height of this list until it's about to be shown. Since this fragment is dependent
            on having an accurate height of the list (in order to pad the bottom of the list so that
            the playing song is always at the top of the list), we need to have a way to be informed
            when the list has a valid height before it's shown to the user.
            This post request will be run after the layout has been assigned a height and before
            it's shown to the user so that we can set the bottom padding correctly.
         */
        view.post(new Runnable() {
            @Override
            public void run() {
                scrollToNowPlaying();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        PlayerController.registerUpdateListener(this);
        // Assume this fragment's data has gone stale since it was last in the foreground
        onUpdate();
        //scrollToNowPlaying();
    }

    @Override
    public void onPause() {
        super.onPause();
        PlayerController.unregisterUpdateListener(this);
    }

    @Override
    public void onUpdate() {


        if(queue==null || !queue.equals(PlayerController.getQueue())){
            queue = (ArrayList<Song>) PlayerController.getQueue();
            //adapter.setDragSection(new QueueSection(queue));
            //adapter.notifyDataSetChanged();
            queueAdapter.notifyDataSetChanged();
        }


        int currentIndex = PlayerController.getQueuePosition();
        int previousIndex = lastPlayIndex;

        if (currentIndex != lastPlayIndex) {
            lastPlayIndex = currentIndex;

            updateView(previousIndex);
            updateView(currentIndex);



            if (queue!=null && shouldScrollToCurrent()) {
                scrollToNowPlaying();
            }

        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        helper.startDrag(viewHolder);
    }

    /**
     * When views are being updated and scrolled passed at the same time, the attached
     * {@link android.support.v7.widget.RecyclerView.ItemDecoration}s will not appear on the
     * changed item because of its animation.
     *
     * Because this animation implies that items are being removed from the queue, this method
     * will manually update a specific view in a RecyclerView if it's visible. If it's not visible,
     * {@link android.support.v7.widget.RecyclerView.Adapter#notifyItemChanged(int)} will be
     * called instead.
     * @param index The index of the item in the attached RecyclerView adapter to be updated
     */
    private void updateView(int index) {
        int start = list.getChildAdapterPosition(list.getChildAt(0));
        int end = list.getChildAdapterPosition(list.getChildAt(list.getChildCount() - 1));

        if (index - start >= 0 && index - start < end) {
            ViewGroup itemView = (ViewGroup) list.getChildAt(index - start);
            if (itemView != null) {
                itemView.findViewById(R.id.instancePlayingIndicator)
                        .setVisibility(index == lastPlayIndex
                                ? View.VISIBLE
                                : View.GONE);
            }
        } else {
            //adapter.notifyItemChanged(index);
            queueAdapter.notifyItemChanged(index);
        }
    }

    /**
     * @return true if the currently playing song is above or below the current item by the
     *         list's height, if the queue has been restarted, or if repeat all is enabled and
     *         the user wrapped from the front of the queue to the end of the queue
     */
    private boolean shouldScrollToCurrent() {
        int topIndex = list.getChildAdapterPosition(list.getChildAt(0));
        int bottomIndex = list.getChildAdapterPosition(list.getChildAt(list.getChildCount() - 1));

        return Math.abs(topIndex - lastPlayIndex) <= (bottomIndex - topIndex)
                || (queue.size() - bottomIndex <= 2 && lastPlayIndex == 0)
                || (bottomIndex - queue.size() <= 2 && lastPlayIndex == queue.size() - 1);
    }

    private void scrollToNowPlaying() {
        try {
            int padding = (lastPlayIndex - queue.size()) * (itemHeight + dividerHeight) - dividerHeight;
            bottomSpacer.setHeight(padding);

            queueAdapter.notifyItemChanged(queue.size());
            ((LinearLayoutManager) list.getLayoutManager())
                    .scrollToPositionWithOffset(lastPlayIndex, 0);
        }catch (Exception e){

        }
    }

    public void updateShuffle() {
        queue.clear();
        queue.addAll(PlayerController.getQueue());
        queueAdapter.notifyDataSetChanged();
        //adapter.notifyDataSetChanged();

        lastPlayIndex = PlayerController.getQueuePosition();
        scrollToNowPlaying();
    }

    public class QueueAdapter extends RecyclerView.Adapter<QueueItemViewHolder> implements ItemTouchHelperAdapter{

        private final OnStartDragListener onStartDragListener;

        public QueueAdapter(OnStartDragListener onStartDragListener){
            this.onStartDragListener = onStartDragListener;
        }

        @Override
        public int getItemCount() {
            return queue.size();
        }

        @Override
        public void onItemDismiss(int position) {
            //ArrayList<Song> editedQueue = new ArrayList<>(PlayerController.getQueue());
            if (queue != null) {
                int queuePosition = PlayerController.getQueuePosition();
                int itemPosition = position;

                queue.remove(itemPosition);
                notifyItemRemoved(position);

                //queueAdapter.remove(itemPosition);
                PlayerController.editQueue(
                        queue,
                        (queuePosition > itemPosition)
                                ? queuePosition - 1
                                : queuePosition);

                if (queuePosition == itemPosition) {
                    PlayerController.begin();
                }
            }
        }

        @Override
        public boolean onItemMove(int from, int to) {
            try {

/*

                if(from<to){
                    for(int i=from; i<to; i++){
                        Collections.swap(queue, i, i+1);
                        notifyItemMoved(i, i+1);
                        notifyItemMoved(i+1, i);

                    }
                }else{
                    for(int i=from; i>to; i--){
                        Collections.swap(queue, i, i-1);
                        notifyItemMoved(i, i-1);
                        notifyItemMoved(i-1, i);
                    }

                }
*/
                Collections.swap(queue, from, to);
                notifyItemMoved(from, to);
                int pos = queue.indexOf(PlayerController.getNowPlaying());
                if(pos>=0 && pos<queue.size())
                    PlayerController.editQueue(queue, pos);
            }catch (Exception e){

            }
            return true;
        }

        @Override
        public QueueItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new QueueItemViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.instance_song_drag_highlight, parent, false),
                    this);
        }

        @Override
        public void onBindViewHolder(final QueueItemViewHolder holder, int position) {

            holder.update(queue.get(position), position);

            holder.handle.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN){
                        onStartDragListener.onStartDrag(holder);
                    }
                    return false;
                }
            });

        }
    }
}