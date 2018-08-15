package com.optimus.music.player.onix.views.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by apricot on 17/10/15.
 */
public class CustomRectangularFrameLayout extends FrameLayout {

    public CustomRectangularFrameLayout(Context context) {
        super(context);
    }

    public CustomRectangularFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRectangularFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int newMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize / 2, MeasureSpec.EXACTLY);
        final int widthMeasure = MeasureSpec.makeMeasureSpec(3*widthSize /4, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasure, newMeasureSpec);
    }



}