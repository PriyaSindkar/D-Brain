package com.webmyne.android.d_brain.ui.Screens;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.eftimoff.androipathview.PathView;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Customcomponents.CirclePageIndicator;
import com.webmyne.android.d_brain.ui.Customcomponents.CustomProgressBar.DashedCircularProgress;
import com.webmyne.android.d_brain.ui.Customcomponents.CustomViewPager;
import com.webmyne.android.d_brain.ui.Customcomponents.PageIndicator;
import com.webmyne.android.d_brain.ui.Fragments.UserGuideSliderFragment;


public class splash extends ActionBarActivity   {
    PathView pathView;
    private DashedCircularProgress dashedCircularProgress;
    ImageView imgBulb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        dashedCircularProgress = (DashedCircularProgress) findViewById(R.id.simple);
       // imgBulb = (ImageView)findViewById(R.id.imgBulb);
        pathView = (PathView) findViewById(R.id.pathView);
        startProgressBar();

        pathView.setFillAfter(true);
        pathView.useNaturalColors();


        start();


        dashedCircularProgress.setOnValueChangeListener(new DashedCircularProgress.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                if (value >= 998 || value == 999) {
                    int col = Color.parseColor("#edbe4c");
                   // imgBulb.setColorFilter(col, PorterDuff.Mode.SRC_ATOP);
                }
            }
        });

    }

    void start(){
        pathView.setFillAfter(true);
        pathView.getPathAnimator().
                delay(100).
                duration(1500).
                interpolator(new AccelerateDecelerateInterpolator()).
                start();

    }

    private Path makeConvexArrow(float length, float height) {
        final Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.lineTo(length / 4f, 0.0f);
        path.lineTo(length, height / 2.0f);
        path.lineTo(length / 4f, height);
        path.lineTo(0.0f, height);
        path.lineTo(length * 3f / 4f, height / 2f);
        path.lineTo(0.0f, 0.0f);
        path.close();
        return path;
    }
    private void startProgressBar(){
        dashedCircularProgress.setValue(999);

    }
 //end of main class
}
