package com.webmyne.android.d_brain.ui.base;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Fragments.HomeFragment;

public class HomeDrawerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    NavigationView view;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        view = (NavigationView) findViewById(R.id.navigation_view);


        if (toolbar != null) {
            toolbar.setTitle("D2 Brain");
            setSupportActionBar(toolbar);
        }
        initDrawer();

    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    public void setSubTitle(String subTitle) {

        toolbar.setSubtitle(subTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.isChecked()) {
                } else {
                    setDrawerClick(menuItem.getItemId());
                }
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

        //hideMenuItem(1);
        setDrawerClick(R.id.drawer_home);


    }

    public void hideMenuItem(int position){
        view.getMenu().getItem(position).setVisible(false);
    }

    public void showMenuItem(int position){
        view.getMenu().getItem(position).setVisible(true);
    }



    private void setDrawerClick(int itemId) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        switch (itemId) {

            case R.id.drawer_home:
                // Home
                ft.replace(R.id.content, HomeFragment.newInstance(), "HOME_PAGE");
                ft.commit();
                break;


        }
    }
}
