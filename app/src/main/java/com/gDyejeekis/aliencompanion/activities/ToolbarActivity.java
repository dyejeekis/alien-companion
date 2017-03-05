package com.gDyejeekis.aliencompanion.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

import com.gDyejeekis.aliencompanion.MyApplication;
import com.gDyejeekis.aliencompanion.R;

/**
 * Created by George on 2/24/2017.
 */

public abstract class ToolbarActivity extends BackNavActivity {

    public static final int TOOLBAR_ANIM_DURATION = 500;

    public Toolbar toolbar;
    public boolean toolbarVisible;

    public void hideToolbar() {
        if(toolbarVisible) {
            toolbarVisible = false;
            toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).setDuration(TOOLBAR_ANIM_DURATION);
        }
    }

    public void showToolbar() {
        if(!toolbarVisible) {
            toolbarVisible = true;
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).setDuration(TOOLBAR_ANIM_DURATION);
        }
    }

    public void  updateToolbarColor() {
        updateToolbarColor(MyApplication.colorPrimary);
    }

    public void updateToolbarColor(int color) {
        toolbar.setBackgroundColor(color);
    }

    protected void initToolbar() {
        toolbarVisible = true;
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if(MyApplication.nightThemeEnabled) {
            toolbar.setPopupTheme(R.style.OverflowStyleDark);
        }
        toolbar.setBackgroundColor(MyApplication.currentColor);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(MyApplication.colorPrimaryDark);
        }
        toolbar.setNavigationIcon(MyApplication.homeAsUpIndicator);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //@Override
    //protected void onCreate(@Nullable Bundle savedInstanceState) {
    //    super.onCreate(savedInstanceState);
    //}
}