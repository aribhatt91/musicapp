package com.optimus.music.player.onix.Utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by apricot on 17/10/15.
 */
public class CustomSquareImageView extends ImageView {
    public CustomSquareImageView(Context context){
        super(context);
    }

    public CustomSquareImageView(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public CustomSquareImageView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredHeight();
        setMeasuredDimension(width,width);
    }
}
