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
import android.widget.ImageView;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Fragments.AboutUsFragment;
import com.webmyne.android.d_brain.ui.Fragments.AddMachineFragment;
import com.webmyne.android.d_brain.ui.Fragments.ContactUsFragment;
import com.webmyne.android.d_brain.ui.Fragments.HomeFragment;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Fragments.MachineConfigFragment;
import com.webmyne.android.d_brain.ui.Fragments.MainPanelFragment;
import com.webmyne.android.d_brain.ui.Fragments.NotificationFragment;
import com.webmyne.android.d_brain.ui.Fragments.SceneFragment;
import com.webmyne.android.d_brain.ui.Fragments.SchedulerFragment;
import com.webmyne.android.d_brain.ui.Fragments.SensorFragment;
import com.webmyne.android.d_brain.ui.Fragments.SettingsFragment;

public class HomeDrawerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    NavigationView view;
    Toolbar toolbar;
    private ImageView btn;
    AnimationHelper animObj;
    private boolean  isPowerOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        view = (NavigationView) findViewById(R.id.navigation_view);
        btn = (ImageView) findViewById(R.id.btn);


        if (toolbar != null) {
            toolbar.setTitle("D2 Brain");
            setSupportActionBar(toolbar);
        }
        initDrawer();
        animObj = new AnimationHelper();
        animObj.initPowerButtonAnimation(btn);
        call();

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
                ft.replace(R.id.content, DashboardFragment.newInstance(), "HOME_PAGE");
                ft.replace(R.id.content, HomeFragment.newInstance(), "HOME_PAGE");
                ft.commit();
                break;

            case R.id.drawer_main_panel:
                // Home
                ft.replace(R.id.content, MainPanelFragment.newInstance(), "MAIN_PANEL");
                ft.commit();
                break;

            case R.id.drawer_scenes:
                // Home
                ft.replace(R.id.content, SceneFragment.newInstance(), "SCENES");
                ft.commit();
                break;

            case R.id.drawer_schedulers:
                // Home
                ft.replace(R.id.content, SchedulerFragment.newInstance(), "SCHEDULER");
                ft.commit();
                break;

            case R.id.drawer_sensors:
                // Home
                ft.replace(R.id.content, SensorFragment.newInstance(), "SENSORS");
                ft.commit();
                break;

            case R.id.drawer_notification:
                // Home
                ft.replace(R.id.content, NotificationFragment.newInstance(), "NOTIFICATION");
                ft.commit();
                break;

            case R.id.drawer_add_machine:
                // Home
                ft.replace(R.id.content, AddMachineFragment.newInstance(), "Add Machine");
                ft.commit();
                break;

            case R.id.drawer_machine_config:
                // Home
                ft.replace(R.id.content, MachineConfigFragment.newInstance(), "Machine Configuration");
                ft.commit();
                break;

            case R.id.drawer_settings:
                // Home
                ft.replace(R.id.content, SettingsFragment.newInstance(), "Settings");
                ft.commit();
                break;

            case R.id.drawer_about:
                // Home
                ft.replace(R.id.content, AboutUsFragment.newInstance(), "About Us");
                ft.commit();
                break;

            case R.id.drawer_contact:
                // Home
                ft.replace(R.id.content, ContactUsFragment.newInstance(), "Contact Us");
                ft.commit();
                break;


        }
    }

    private void call() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                isPowerOn = !isPowerOn;
                if (isPowerOn) {
                    animObj.cancelPowerButtonAnimation();
                } else {
                    animObj.startPowerButtonAnimation();
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }
}
