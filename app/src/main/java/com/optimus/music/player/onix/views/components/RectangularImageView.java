package com.optimus.music.player.onix.views.components;

/**
 * Created by apricot on 12/9/15.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class RectangularImageView extends ImageView {
    public RectangularImageView(Context context){
        super(context);
    }

    public RectangularImageView(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public RectangularImageView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width,width/2);
    }
}
