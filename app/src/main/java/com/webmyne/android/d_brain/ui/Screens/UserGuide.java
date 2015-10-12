package com.webmyne.android.d_brain.ui.Screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Customcomponents.CirclePageIndicator;
import com.webmyne.android.d_brain.ui.Customcomponents.CustomViewPager;
import com.webmyne.android.d_brain.ui.Customcomponents.PageIndicator;
import com.webmyne.android.d_brain.ui.Fragments.UserGuideSettingsFragment;
import com.webmyne.android.d_brain.ui.Fragments.UserGuideSliderFragment;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.Model.TouchPanelModel;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserGuide extends ActionBarActivity implements View.OnClickListener {
    private CustomViewPager viewPager;
    private  TabsPagerAdapter mAdapter;
    protected PageIndicator mIndicator;
    private TextView txtSkip,txtNext;
    private  ArrayList<XMLValues> powerStatus;
    private String machineName, machineIP, productCode = "";
    private Pattern pattern;
    private Matcher matcher;

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);
        pattern = Pattern.compile(IPADDRESS_PATTERN);

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
                machineName = fragment.getStrMachineName();

                if(fragment.getStrMachineName().length() == 0) {
                    Toast.makeText(this, "Must Enter Machine Name!", Toast.LENGTH_LONG).show();
                } else if(fragment.getIPAddress().length() == 0) {
                    Toast.makeText(this, "Must Enter Device IP Address!", Toast.LENGTH_LONG).show();
                } /*else if( !validate(fragment.getIPAddress())) {
                    Toast.makeText(this, "Please enter vaid IP Address", Toast.LENGTH_LONG).show();
                } */  else {
                    machineIP = fragment.getIPAddress();
                    //get machine from IP and add to db
                    new GetMachineStatus().execute();

                    /*if(Utils.validateProductCode(AppConstants.TEMP_PRODUCT_CODE)) {
                        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("hasLoggedIn", true);
                        editor.commit();

                        initDatabaseComponents();

                        Intent intent = new Intent(getBaseContext(), HomeDrawerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Invalid Product Code", Toast.LENGTH_LONG).show();
                    }*/
                }
                break;
        }
    }

    private void initDatabaseComponents(String productCode) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //insert switches in component table for the given machine
        try {
            dbHelper.openDataBase();

            // product code and machine IP are hard-coded
            //String productCode = AppConstants.TEMP_PRODUCT_CODE;
            ArrayList<ComponentModel> listOfComponents = new ArrayList<>();
            ArrayList<TouchPanelModel> listOfTouchPanels = new ArrayList<>();

            // init the component table with switches of a machine
            int totalNoOfSwitches = Integer.parseInt(productCode.substring(1,2)) * 11;
            int totalNoOfDimmers = Integer.parseInt(productCode.substring(2,3)) * 11;
            int totalNoOfMotors = Integer.parseInt(productCode.substring(3,4)) * 11;

            Log.e("totalNoOfSwitches", totalNoOfSwitches+"");

            if(totalNoOfSwitches == 0) {

            } else {
                for (int i = 0; i < totalNoOfSwitches; i++) {
                    String idSuffix = String.format("%02d", (i + 1));
                    ComponentModel switchItem = new ComponentModel(AppConstants.SWITCH_PREFIX + idSuffix, AppConstants.SWITCH_TYPE + String.valueOf(i + 1), AppConstants.SWITCH_TYPE, "", machineIP);
                    switchItem.setMachineName(machineName);
                    listOfComponents.add(switchItem);
                }
            }

            // init the component table with dimmers of a machine
            if(totalNoOfDimmers == 0) {

            } else {
                for (int i = 0; i < totalNoOfDimmers; i++) {
                    String idSuffix = String.format("%02d", (i + 1));
                    ComponentModel dimmerItem = new ComponentModel(AppConstants.DIMMER_PREFIX + idSuffix, AppConstants.DIMMER_TYPE + String.valueOf(i + 1), AppConstants.DIMMER_TYPE, "", machineIP);
                    dimmerItem.setMachineName(machineName);
                    listOfComponents.add(dimmerItem);
                }
            }

            // init the component table with dimmers of a machine
            if(totalNoOfMotors == 0) {

            } else {
                for (int i = 0; i < totalNoOfMotors; i++) {
                    String idSuffix = String.format("%02d", (i + 1));
                    ComponentModel motorItem = new ComponentModel(AppConstants.MOTOR_PREFIX + idSuffix, AppConstants.MOTOR_TYPE + String.valueOf(i + 1), AppConstants.MOTOR_TYPE, "", machineIP);
                    motorItem.setMachineName(machineName);
                    listOfComponents.add(motorItem);
                }
            }

            // init the component table with alerts of a machine
            int totalNoOfAlerts = Integer.parseInt(productCode.substring(4,5));
            if(totalNoOfAlerts == 0) {

            } else {
                for (int i = 0; i < totalNoOfAlerts; i++) {
                    String idSuffix = String.format("%02d", (i + 1));
                    ComponentModel sensorItem = new ComponentModel(AppConstants.ALERT_PREFIX + idSuffix, AppConstants.ALERT_TYPE + String.valueOf(i + 1), AppConstants.ALERT_TYPE, "", machineIP);
                    sensorItem.setMachineName(machineName);
                    sensorItem.setDetails("Alert fired on breach");
                    listOfComponents.add(sensorItem);
                }
            }

            // init the touch_panel table
            int totalNoOfPanels = (Integer.parseInt(productCode.substring(7,8)) * 10) + Integer.parseInt(productCode.substring(8,9));

            for(int i=0; i<totalNoOfPanels; i++) {
                String idSuffix = String.format("%02d", (i + 1));
                TouchPanelModel touchPanelBox = new TouchPanelModel(AppConstants.TOUCH_PANEL_TYPE+idSuffix);
                listOfTouchPanels.add(touchPanelBox);

            }

            dbHelper.insertIntoComponent(listOfComponents);
            dbHelper.insertIntoTouchPanel(listOfTouchPanels);

            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
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

    public class GetMachineStatus extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL urlValue = new URL("http://"+machineIP + AppConstants.URL_FETCH_MACHINE_STATUS);
                Log.e("# urlValue", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setConnectTimeout(1000*60);

                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();
                //  Log.e("# inputStream", inputStream.toString());
                MainXmlPullParser pullParser = new MainXmlPullParser();
                powerStatus = pullParser.processXML(inputStream);
                Log.e("XML PARSERED", powerStatus.toString());


            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Log.e("TAG_ASYNC", "Inside onPostExecute");
            DatabaseHelper dbHelper = new DatabaseHelper(UserGuide.this);
            try {
                dbHelper.openDataBase();
                dbHelper.insertIntoMachine(powerStatus, machineName, machineIP);
                dbHelper.close();

                for(int i=0; i<powerStatus.size();i++) {
                    if(powerStatus.get(i).tagName.equals("DPC")) {
                        productCode = powerStatus.get(i).tagValue;
                    }
                }

                if(Utils.validateProductCode(productCode)) {
                        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("hasLoggedIn", true);
                        editor.commit();

                        initDatabaseComponents(productCode);

                        Intent intent = new Intent(getBaseContext(), HomeDrawerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(UserGuide.this, "Invalid Product Code", Toast.LENGTH_LONG).show();
                    }
            }catch (Exception e) {}

        }
    }

    /**
     * Validate ip address with regular expression
     * @param ip ip address for validation
     * @return true valid ip address, false invalid ip address
     */
    public boolean validate(final String ip){
        Log.e("ip", ip);
        matcher = pattern.matcher(ip);
        return matcher.matches();
    }

}
