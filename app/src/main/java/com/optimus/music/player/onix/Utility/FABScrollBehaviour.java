package com.optimus.music.player.onix.Utility;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;

/**
 * Created by apricot on 8/2/16.
 */
public class FABScrollBehaviour extends FloatingActionButton.Behavior {
    public FABScrollBehaviour(){

    }
    public FABScrollBehaviour(Context context, AttributeSet attributeSet) {
        super();
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            return super.onDependentViewChanged(parent, child, dependency);
        } else if (dependency instanceof AppBarLayout) {
            this.updateFabVisibility(parent, (AppBarLayout) dependency, child);
        }
        else if (dependency instanceof RecyclerView) {
            //this.updateFabVisibility(parent, (AppBarLayout) dependency, child);
        }

        return false;
    }

    private boolean updateFabVisibility(CoordinatorLayout parent, AppBarLayout appBarLayout, FloatingActionButton child) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (lp.getAnchorId() != appBarLayout.getId()) {
            return  false;
        }
        else {

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            int point = child.getTop() - params.topMargin;
            try {
                Method method = AppBarLayout.class.getDeclaredMethod("getMinimumHeightForVisibleOverlappingContent");
                method.setAccessible(true);
                if (point <= (int) method.invoke(appBarLayout)) {
                    child.hide();
                } else {
                    child.show();
                }
                return true;
            } catch (Exception e) {
                return true;
            }
        }
    }
}