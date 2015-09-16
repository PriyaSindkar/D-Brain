package com.webmyne.android.d_brain.ui.Screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Customcomponents.CirclePageIndicator;
import com.webmyne.android.d_brain.ui.Customcomponents.CustomViewPager;
import com.webmyne.android.d_brain.ui.Customcomponents.PageIndicator;
import com.webmyne.android.d_brain.ui.Fragments.UserGuideSettingsFragment;
import com.webmyne.android.d_brain.ui.Fragments.UserGuideSliderFragment;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;


public class UserGuide extends ActionBarActivity implements View.OnClickListener {
    private CustomViewPager viewPager;
    private  TabsPagerAdapter mAdapter;
    protected PageIndicator mIndicator;
    private TextView txtSkip,txtNext;

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

        txtSkip = (TextView)findViewById(R.id.txtSkip);
        txtNext = (TextView)findViewById(R.id.txtNext);


        txtSkip.setOnClickListener(this);
        txtNext.setOnClickListener(this);

        viewPager = (CustomViewPager) findViewById(R.id.pager);
        mIndicator = (CirclePageIndicator)findViewById(R.id.guideIndicator);

        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 6) {
                    txtNext.setVisibility(View.VISIBLE);
                    txtSkip.setVisibility(View.GONE);
                } else {
                    txtNext.setVisibility(View.GONE);
                    txtSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtSkip:
                viewPager.setCurrentItem(6);
                break;
            case R.id.txtNext:

                UserGuideSettingsFragment fragment = (UserGuideSettingsFragment) mAdapter.getItem(6);

                if(fragment.getStrMachineName().length() == 0) {
                    Toast.makeText(this, "Must Enter Machine Name!", Toast.LENGTH_LONG).show();
                } else if(fragment.getIPAddress().length() == 0) {
                    Toast.makeText(this, "Must Enter Device IP Address!", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getBaseContext(), HomeDrawerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("hasLoggedIn", true);
                    editor.commit();
                }
                break;
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

                case 1:

                    return UserGuideSliderFragment.newInstance(R.drawable.splash, "");

                case 2:

                    return UserGuideSliderFragment.newInstance(R.drawable.splash, "");
                case 3:

                    return UserGuideSliderFragment.newInstance(R.drawable.splash, "");
                case 4:

                    return UserGuideSliderFragment.newInstance(R.drawable.splash, "");
                case 5:

                    return UserGuideSliderFragment.newInstance(R.drawable.splash, "");
                case 6:

                    return UserGuideSettingsFragment.newInstance(R.drawable.splash, "");

            }

            return null;
        }

        @Override
        public int getCount() {
            return 7;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
