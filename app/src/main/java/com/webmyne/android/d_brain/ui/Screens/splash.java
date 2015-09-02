package com.webmyne.android.d_brain.ui.Screens;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.eftimoff.androipathview.PathView;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Customcomponents.CirclePageIndicator;
import com.webmyne.android.d_brain.ui.Customcomponents.CustomProgressBar.DashedCircularProgress;
import com.webmyne.android.d_brain.ui.Customcomponents.CustomViewPager;
import com.webmyne.android.d_brain.ui.Customcomponents.PageIndicator;
import com.webmyne.android.d_brain.ui.Fragments.UserGuideSliderFragment;


public class splash extends ActionBarActivity {
    PathView pathView;
    private DashedCircularProgress dashedCircularProgress;
    ImageView imgBulb;
    ImageView imgd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        init();

        startProgressBar();

        pathView.setFillAfter(true);
        pathView.useNaturalColors();


        start();


        dashedCircularProgress.setOnValueChangeListener(new DashedCircularProgress.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                if (value >= 998 || value == 999) {

                    Animation fadeIn = new AlphaAnimation(0, 1);
                    fadeIn.setDuration(1800);

                    imgBulb.setVisibility(View.VISIBLE);
                    int col = Color.parseColor("#edbe4c");
                    imgBulb.setColorFilter(col, PorterDuff.Mode.SRC_ATOP);

                    imgBulb.setAnimation(fadeIn);

                    new CountDownTimer(1000, 100) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            Intent i = new Intent(splash.this, UserGuide.class);
                            startActivity(i);
                            finish();
                        }
                    }.start();


                }
            }
        });

    }


    private void animaton360() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imgd2, "rotationY", 360f, 0f);
        animator.setDuration(2500);
        animator.start();

    }

    private void init() {
        dashedCircularProgress = (DashedCircularProgress) findViewById(R.id.simple);
        imgBulb = (ImageView) findViewById(R.id.imgBulb);
        pathView = (PathView) findViewById(R.id.pathView);
        imgd2 = (ImageView) findViewById(R.id.imgd2);


    }


    void start() {
        pathView.getPathAnimator().
                delay(100).
                duration(2500).
                listenerEnd(new PathView.AnimatorBuilder.ListenerEnd() {
                    @Override
                    public void onAnimationEnd() {
                        animaton360();
                    }
                }).
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

    private void startProgressBar() {
        dashedCircularProgress.setValue(999);

    }
    //end of main class
}
