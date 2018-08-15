package com.optimus.music.player.onix.RecyclerViewUtils.ViewHolders.Views;

/**
 * Created by apricot on 13/12/15.
 */
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.optimus.music.player.onix.R;

/**
 * An {@link android.support.v7.widget.RecyclerView.ItemDecoration} that draws a solid color behind
 * a {@link RecyclerView} and its children
 */
public class BackgroundDecoration extends RecyclerView.ItemDecoration {

    private Drawable mBackground;
    private NinePatchDrawable mShadow;
    private int[] excludedIDs;

    /**
     * Create an ItemDecorator for use with a RecyclerView
     * @param color the color of the background
     */
    public BackgroundDecoration(int color) {
        this(color, null);
    }

    /**
     * Create an ItemDecorator for use with a RecyclerView
     * @param color the color of the background
     * @param excludedLayoutIDs an array of layoutIDs to exclude adding a background color to
     *                          null to add a background to the entire RecyclerView
     */
    public BackgroundDecoration(int color, int[] excludedLayoutIDs){
        mBackground = new ColorDrawable(color);
        excludedIDs = excludedLayoutIDs;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        if (mShadow == null){
            //noinspection deprecation
           // mShadow = (NinePatchDrawable) parent.getContext().getResources().getDrawable(R.drawable.list_shadow);
        }

        Rect shadowPadding = new Rect();
        mShadow.getPadding(shadowPadding);

        if (excludedIDs == null) {
            int top = 0;
            int bottom = c.getHeight();

            mBackground.setBounds(left, top, right, bottom);
            mBackground.draw(c);

            mShadow.setBounds(left - shadowPadding.left, top - shadowPadding.top,
                    right + shadowPadding.right, bottom + shadowPadding.bottom);
            mShadow.draw(c);
        }
        else{
            int layoutCount = parent.getChildCount();
            for (int i = 0; i < layoutCount; i++){
                View topView = parent.getChildAt(i);
                if (includeView(topView.getId())) {

                    //noinspection StatementWithEmptyBody
                    while(++i < layoutCount && includeView(parent.getChildAt(i).getId())){
                        // Find the last view in this section that will receive a background
                        // This loop is intentionally left empty
                    }

                    View bottomView = parent.getChildAt(--i);

                    RecyclerView.LayoutParams topParams = (RecyclerView.LayoutParams) topView.getLayoutParams();
                    RecyclerView.LayoutParams bottomParams = (RecyclerView.LayoutParams) bottomView.getLayoutParams();

                    final int top = topView.getTop() - topParams.topMargin;
                    final int bottom = (i == layoutCount - 1 || parent.getChildAdapterPosition(bottomView) == parent.getAdapter().getItemCount() - 1)
                            ? parent.getBottom() // If this is the last item in the adapter or last visible view, fill the parent
                            : bottomView.getBottom() + bottomParams.bottomMargin; // Otherwise, fill to the bottom of the last item in the section

                    mBackground.setBounds(left, top, right, bottom);
                    mBackground.draw(c);

                    mShadow.setBounds(left - shadowPadding.left, top - shadowPadding.top,
                            right + shadowPadding.right, bottom + shadowPadding.bottom);
                    mShadow.draw(c);
                }
            }
        }
    }

    private boolean includeView(int viewId){
        for (int i : excludedIDs){
            if (viewId == i) return false;
        }
        return true;
    }
}