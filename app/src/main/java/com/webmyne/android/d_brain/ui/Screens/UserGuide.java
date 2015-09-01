package com.webmyne.android.d_brain.ui.Screens;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Customcomponents.CirclePageIndicator;
import com.webmyne.android.d_brain.ui.Customcomponents.CustomViewPager;
import com.webmyne.android.d_brain.ui.Customcomponents.PageIndicator;
import com.webmyne.android.d_brain.ui.Fragments.UserGuideSliderFragment;


public class UserGuide extends ActionBarActivity implements View.OnClickListener {
    private CustomViewPager viewPager;
    private  TabsPagerAdapter mAdapter;
    protected PageIndicator mIndicator;
    private TextView txtSignup,txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);


        init();

        if( Build.VERSION.SDK_INT >= 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(viewPager);
    }
    private void init( ){
        txtLogin = (TextView)findViewById(R.id.txtLogin);
        txtSignup = (TextView)findViewById(R.id.txtSignup);
        txtSignup.setOnClickListener(this);
        txtLogin.setOnClickListener(this);

        viewPager = (CustomViewPager) findViewById(R.id.pager);
        mIndicator = (CirclePageIndicator)findViewById(R.id.guideIndicator);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    public class TabsPagerAdapter extends FragmentStatePagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override


        public Fragment getItem(int index) {

            switch (index) {

                case 0:

                    return UserGuideSliderFragment.newInstance(R.drawable.splash, "");

                // Top Rated fragment activity

                case 1:

                    return UserGuideSliderFragment.newInstance(R.drawable.splash, "");

                // Top Rated fragment activity

                case 2:

                    // Top Rated fragment activity

                    return UserGuideSliderFragment.newInstance(R.drawable.splash, "");

            }

            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

    }


}
