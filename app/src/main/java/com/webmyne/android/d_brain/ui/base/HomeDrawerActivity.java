package com.webmyne.android.d_brain.ui.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.TouchPanelActivity;
import com.webmyne.android.d_brain.ui.Adapters.SwitchListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.AddMachineDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.SceneListDialog;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Fragments.AboutUsFragment;
import com.webmyne.android.d_brain.ui.Fragments.AddMachineFragment;
import com.webmyne.android.d_brain.ui.Fragments.ContactUsFragment;
import com.webmyne.android.d_brain.ui.Fragments.SchedulerFragment;
import com.webmyne.android.d_brain.ui.Fragments.SensorFragment;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Fragments.MachineConfigFragment;
import com.webmyne.android.d_brain.ui.Fragments.MainPanelFragment;
import com.webmyne.android.d_brain.ui.Fragments.NotificationFragment;
import com.webmyne.android.d_brain.ui.Fragments.SceneFragment;
import com.webmyne.android.d_brain.ui.Fragments.SettingsFragment;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;
import com.webmyne.android.d_brain.ui.xmlHelpers.MainXmlPullParser;
import com.webmyne.android.d_brain.ui.xmlHelpers.XMLValues;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

public class HomeDrawerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    NavigationView view;
    Toolbar toolbar;
    private ImageView btn;
    AnimationHelper animObj;
    private boolean  isPowerOn = true;
    private TextView toolbarTitle, txtClearButton;
    private LinearLayout linearClearButton;
    private  ArrayList<XMLValues> powerStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        view = (NavigationView) findViewById(R.id.navigation_view);
        btn = (ImageView) findViewById(R.id.btn);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        linearClearButton = (LinearLayout) findViewById(R.id.linearClearButton);
        txtClearButton = (TextView) findViewById(R.id.txtClearButton);

        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }
        initDrawer();
       /* animObj = new AnimationHelper();
        animObj.initPowerButtonAnimation(btn);*/
        btn.setVisibility(View.VISIBLE);

      //  call();

        txtClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtClearButton.getText().toString().equals("Add Machine")) {
                    AddMachineDialog machineDialog = new AddMachineDialog(HomeDrawerActivity.this);
                    machineDialog.show();
                }
            }
        });

    }

    public void setTitle(String title) {
        toolbarTitle.setText(title);
        //toolbar.setTitle(title);
       // toolbar.setNavigationIcon(R.layout.drawer_option_icon);
    }

    public void showAppBarButton() {
        linearClearButton.setVisibility(View.VISIBLE);
    }

    public void hideAppBarButton() {
        linearClearButton.setVisibility(View.GONE);
    }

    public void setClearButtonText(String text) {
        txtClearButton.setText(text);
    }

    public void setSubTitle(String subTitle) {

        toolbar.setSubtitle(subTitle);
    }

    public void initPowerButton() {
        animObj = new AnimationHelper();
        animObj.initPowerButtonAnimation(btn);
    }

    public void setPowerButtonOff() {
        btn.setAlpha(0.3f);
    }

    public void startPowerAnimation() {
        animObj.startPowerButtonAnimation();
    }

    public void cancelPowerAnimation() {
        animObj.cancelPowerButtonAnimation();
    }

    public void hideDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerToggle.setDrawerIndicatorEnabled(false);
    }

    public void showDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        drawerToggle.setDrawerIndicatorEnabled(true);
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
                ft.commit();
                break;

            case R.id.drawer_main_panel:
                // Home
                ft.replace(R.id.content, MainPanelFragment.newInstance(), "MAIN_PANEL");
                ft.commit();
                break;

            case R.id.drawer_scenes:
                ft.replace(R.id.content, SceneFragment.newInstance(), "SCENE");
                ft.commit();
                break;

            case R.id.drawer_schedulers:
                ft.replace(R.id.content, SchedulerFragment.newInstance(), "SCHEDULER");
                ft.commit();
                break;

            case R.id.drawer_sensors:
                ft.replace(R.id.content, SensorFragment.newInstance(), "SENSOR");
                ft.commit();
                break;

            case R.id.drawer_touchpanel:
                    Intent intent = new Intent(HomeDrawerActivity.this, TouchPanelActivity.class);
                    startActivity(intent);
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
                btn.setVisibility(View.GONE);
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


}
