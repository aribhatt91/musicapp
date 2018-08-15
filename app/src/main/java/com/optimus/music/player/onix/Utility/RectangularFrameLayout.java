package com.optimus.music.player.onix.Utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by apricot on 17/10/15.
 */
public class RectangularFrameLayout extends FrameLayout {

    public RectangularFrameLayout(Context context) {
        super(context);
    }

    public RectangularFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RectangularFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int newMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize/2, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, newMeasureSpec);
    }



}