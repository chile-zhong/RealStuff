package com.example.ivor_hu.meizhi.widget;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ivor on 16-8-12.
 */
public class SlidingTabBehavior extends CoordinatorLayout.Behavior<View> {
    private int mStatusBarHight;

    public SlidingTabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mStatusBarHight = Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        child.setTranslationY(-(dependency.getTop() - mStatusBarHight));
        return true;
    }
}
