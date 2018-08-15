package com.optimus.music.player.onix.views.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by apricot on 21/4/16.
 */
public class ScreenFrameLayout extends FrameLayout{
    public ScreenFrameLayout(Context context) {
        super(context);
    }

    public ScreenFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int newMeasureSpec = MeasureSpec.makeMeasureSpec((9*widthSize) / 16, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, newMeasureSpec);
    }
}
