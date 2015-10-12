package com.webmyne.android.d_brain.ui.Fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.CreateSceneActivity;
import com.webmyne.android.d_brain.ui.Activities.DimmerListActivity;
import com.webmyne.android.d_brain.ui.Activities.MachineListActivity;
import com.webmyne.android.d_brain.ui.Activities.MotorListActivity;
import com.webmyne.android.d_brain.ui.Activities.SceneActivity;
import com.webmyne.android.d_brain.ui.Activities.SensorsListActivity;
import com.webmyne.android.d_brain.ui.Activities.SwitchesListActivity;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Helpers.PopupAnimationEnd;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
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

public class DashboardFragment extends Fragment implements PopupAnimationEnd, View.OnClickListener {

    private AnimationHelper animObj;
    private ImageView imgOptions, imgFavorites, imgSchedulers, bulb_image;
    private boolean isImageUp = true, isBulbOn = true;
    private LinearLayout layoutBottom, linearOptions, linearSceneList, linearDisabled;
    private HorizontalScrollView hScrollView;
    private FrameLayout parentMotor, parentSlider, parentSwitches, parentSensors, linearLeft ;
    private TextView txtNoOfSwitchUnits, txtNoOfMotorUnits, txtNoOfSensorUnits, txtNoOfSliderUnits;
    private LinearLayout linearCreateScene, linearAddMachine, linearAddScheduler, firstBottomItem;
    private ArrayList<String> switchesWithOnStatus;
    private ArrayList<XMLValues> dimmersWithOnStatus;
    private ArrayList<XMLValues> switchStatusList, dimmerStatusList;
    private Cursor switchListCursor, dimmerListCursor, motorListCursor, sensorListCursor, machineCursor;
    private boolean  isPowerOn = true;
    private  ArrayList<XMLValues> powerStatus;
    private FragmentActivity activity;
    private int powerSignalCount = 0;
    String previousLed = "", led = "";
    public static String URL_MACHINE_IP ="";
    public static String MACHINE_IP = "";


    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        activity = getActivity();
        init(view);



        return view;
    }


    private void init(View row) {
        animObj = new AnimationHelper();
        linearDisabled = (LinearLayout) row.findViewById(R.id.linearDisabled);

        linearDisabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        linearLeft = (FrameLayout)row.findViewById(R.id.linearLeft);
        firstBottomItem = (LinearLayout)row.findViewById(R.id.firstBottomItem);

        imgOptions = (ImageView) row.findViewById(R.id.imgOptions);
        layoutBottom = (LinearLayout) row.findViewById(R.id.layoutBottom);
        linearOptions = (LinearLayout) row.findViewById(R.id.linearOptions);
        linearSceneList = (LinearLayout) row.findViewById(R.id.linearSceneList);
        hScrollView = (HorizontalScrollView) row.findViewById(R.id.hScrollView);
        imgFavorites = (ImageView) row.findViewById(R.id.imgFavorites);
        imgSchedulers = (ImageView) row.findViewById(R.id.imgSchedulers);
        bulb_image = (ImageView) row.findViewById(R.id.bulb_image);
        bulb_image.setOnClickListener(this);

        txtNoOfSwitchUnits = (TextView) row.findViewById(R.id.txtNoOfSwitchUnits);
        txtNoOfMotorUnits = (TextView) row.findViewById(R.id.txtNoOfMotorUnits);
        txtNoOfSliderUnits = (TextView) row.findViewById(R.id.txtNoOfSliderUnits);
        txtNoOfSensorUnits = (TextView) row.findViewById(R.id.txtNoOfSensorUnits);

        linearCreateScene = (LinearLayout) row.findViewById(R.id.linearCreateScene);
        linearCreateScene.setOnClickListener(this);

        linearAddMachine = (LinearLayout) row.findViewById(R.id.linearAddMachine);
        linearAddMachine.setOnClickListener(this);
        linearAddScheduler = (LinearLayout) row.findViewById(R.id.linearAddScheduler);
        linearAddScheduler.setOnClickListener(this);

        imgFavorites.setOnClickListener(this);
        imgSchedulers.setOnClickListener(this);

        parentMotor = (FrameLayout) row.findViewById(R.id.parentMotor);
        parentMotor.setOnClickListener(this);
        parentSlider = (FrameLayout) row.findViewById(R.id.parentSlider);
        parentSlider.setOnClickListener(this);
        parentSwitches = (FrameLayout) row.findViewById(R.id.parentSwitches);
        parentSwitches.setOnClickListener(this);
        parentSensors = (FrameLayout) row.findViewById(R.id.parentSensors);
        parentSensors.setOnClickListener(this);

       /* parentSwitches.setClickable(false);
        parentMotor.setClickable(false);
        parentSlider.setClickable(false);
        parentSensors.setClickable(false);*/

        hScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        linearOptions.setVisibility(View.INVISIBLE);

        imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageUp) {
                    isImageUp = false;
                    // to rotate button(arrow)
                    animObj.rotateViewClockwise((ImageView) v);
                    linearOptions.setVisibility(View.VISIBLE);
                    // to animate popup
                    animObj.viewPopUpMenu(linearOptions);
                } else {
                    animObj.rotateViewAntiClockwise((ImageView) v);
                    animObj.closePopUpMenu(linearOptions);
                    animObj.setInterFaceObj(new PopupAnimationEnd() {
                        @Override
                        public void animationCompleted() {
                            linearOptions.setVisibility(View.GONE);
                            isImageUp = true;
                        }
                    });
                }
            }
        });

        ViewTreeObserver vto = firstBottomItem.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            int width = firstBottomItem.getWidth();
            linearLeft.getLayoutParams().width = width;
            linearLeft.requestLayout();
            }
        });

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        try {
            dbHelper.openDataBase();
            machineCursor = dbHelper.getMachine();

            if(machineCursor != null) {
                machineCursor.moveToFirst();
                URL_MACHINE_IP = "http://" + machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP));
                MACHINE_IP = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP));
            }
            /*dbHelper.close();
            Log.e("Machine IP", URL_MACHINE_IP);
        } catch (Exception e) {
            Log.e("EXP_MACHINE", e.toString());
        }

        //check total components in adapter ofr machine-1
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        try {
            dbHelper.openDataBase();*/
            switchListCursor =  dbHelper.getAllSwitchComponentsForAMachine(MACHINE_IP);
            dimmerListCursor =  dbHelper.getAllDimmerComponentsForAMachine(MACHINE_IP);
            motorListCursor =  dbHelper.getAllMotorComponentsForAMachine(MACHINE_IP);
            sensorListCursor =  dbHelper.getAllSensorComponentsForAMachine(MACHINE_IP);
            dbHelper.close();

            // get no of switches from db, if 0 no, switches not shown
            if(switchListCursor != null) {
                Log.e("no of switches", switchListCursor.getCount()+"");
                if (switchListCursor.getCount() == 0) {
                    parentSwitches.setVisibility(View.GONE);
                } else {
                    txtNoOfSwitchUnits.setText(String.valueOf(switchListCursor.getCount()));
                }
            } else {
                Log.e("no of switches", "null");
                parentSwitches.setVisibility(View.GONE);
            }

            // get no of dimmers from db, if 0 no, dimmeres not shown
            if(dimmerListCursor != null) {
                if (dimmerListCursor.getCount() == 0) {
                    parentSlider.setVisibility(View.GONE);
                } else {
                    txtNoOfSliderUnits.setText(String.valueOf(dimmerListCursor.getCount()));
                }
            } else {
                parentSlider.setVisibility(View.GONE);
            }

            // get no of motors from db, if 0 no, motors not shown
            if(motorListCursor != null) {
                if (motorListCursor.getCount() == 0) {
                    parentMotor.setVisibility(View.GONE);
                } else {
                    txtNoOfMotorUnits.setText(String.valueOf(motorListCursor.getCount()));
                }
            } else {
                parentMotor.setVisibility(View.GONE);
            }

            // get no of sensors/alerts from db, if 0 no, sensors/alerts not shown
            if(sensorListCursor != null) {
                if (sensorListCursor.getCount() == 0) {
                    parentSensors.setVisibility(View.GONE);
                } else {
                    txtNoOfSensorUnits.setText(String.valueOf(sensorListCursor.getCount()));
                }
            } else {
                parentSensors.setVisibility(View.GONE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ((HomeDrawerActivity) getActivity()).initPowerButton();
        // call();
    }

    @Override
    public void onResume() {
        super.onResume();

        HomeDrawerActivity homeScreen = ((HomeDrawerActivity) getActivity());
        homeScreen.setTitle("Dashboard");
        homeScreen.hideAppBarButton();

        /*DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        try {
            dbHelper.openDataBase();
            machineCursor = dbHelper.getMachine();

            if(machineCursor != null) {
                machineCursor.moveToFirst();
                URL_MACHINE_IP = "http://" + machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP));
                MACHINE_IP = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP));
            }
            dbHelper.close();
            Log.e("Machine IP", URL_MACHINE_IP);
        } catch (Exception e) {
            Log.e("EXP_MACHINE", e.toString());
        }*/

        updateSceneList();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.imgHScrollLeft:
                hScrollView.scrollTo((int) hScrollView.getScrollX() - 20, (int) hScrollView.getScrollY());
                break;

            case R.id.imgHScrollRight:
                hScrollView.scrollTo((int) hScrollView.getScrollX() + 20, (int) hScrollView.getScrollY());
                break;*/
            case R.id.parentMotor:
                Intent intent = new Intent(getActivity(), MotorListActivity.class);
                startActivity(intent);
                break;

            case R.id.parentSlider:
                intent = new Intent(getActivity(), DimmerListActivity.class);
                startActivity(intent);
                break;
            case R.id.parentSwitches:
                intent = new Intent(getActivity(), SwitchesListActivity.class);
                startActivity(intent);
                break;

            case R.id.parentSensors:
                intent = new Intent(getActivity(), SensorsListActivity.class);
                startActivity(intent);
                break;

            case R.id.bulb_image:
                if(isBulbOn) {
                  //  linearMainBody.setAlpha(0.3f);
                    Toast.makeText(getActivity(), "Power is off. Please switch on the main switch.", Toast.LENGTH_LONG).show();

                    showOffScreen() ;
                    ((HomeDrawerActivity) getActivity()).hideDrawer();

                    bulb_image.setColorFilter(getResources().getColor(R.color.white));
                    bulb_image.setBackgroundResource(R.drawable.white_border_circle);

                    // if switches exist, fetch their status
                    if(switchListCursor != null && switchListCursor.getCount() > 0) {
                        new GetSwitchStatus().execute();
                    } else {
                        Log.e("TAG_DASHBOARD", "No switches");
                    }

                    // if dimmers exist, fetch their status
                    if(dimmerListCursor != null && dimmerListCursor.getCount() > 0) {
                        new GetDimmerStatus().execute();
                    } else {
                        Log.e("TAG_DASHBOARD", "No dimmers");
                    }

                } else {
                    bulb_image.setColorFilter(getResources().getColor(R.color.yellowBorder));
                    bulb_image.setBackgroundResource(R.drawable.circle);

                    showOnScreen();
                    ((HomeDrawerActivity) getActivity()).showDrawer();

                    // switch on all the previously on switches(prior to main power off)
                    if(switchesWithOnStatus != null && !switchesWithOnStatus.isEmpty()) {
                        for (int i = 0; i < switchesWithOnStatus.size(); i++) {
                            String strPosition = switchesWithOnStatus.get(i).substring(2, 4);
                            String CHANGE_STATUS_URL = DashboardFragment.URL_MACHINE_IP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.ON_VALUE;
                            new ChangeSwitchStatus().execute(CHANGE_STATUS_URL);
                        }
                    }

                    // switch on all the previously on dimmers with previously saved value(prior to main power off)
                    if(dimmersWithOnStatus != null && !dimmersWithOnStatus.isEmpty()) {
                        for (int i = 0; i < dimmersWithOnStatus.size(); i++) {
                            XMLValues dimmer = dimmersWithOnStatus.get(i);
                            String strPosition = dimmer.tagName.substring(2, 4);
                            String strProgress = dimmer.tagValue.substring(2, 4);
                            String CHANGE_STATUS_URL = DashboardFragment.URL_MACHINE_IP + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.ON_VALUE + strProgress;
                            new ChangeDimmerStatus().execute(CHANGE_STATUS_URL);
                        }
                    }
                }
                isBulbOn = !isBulbOn;
                break;

            case R.id.linearCreateScene:
                //close the popup
                animObj.rotateViewAntiClockwise(imgOptions);
                animObj.closePopUpMenu(linearOptions);
                animObj.setInterFaceObj(new PopupAnimationEnd() {
                    @Override
                    public void animationCompleted() {
                        linearOptions.setVisibility(View.GONE);
                        isImageUp = true;
                    }
                });

                intent = new Intent(getActivity(), CreateSceneActivity.class);
                startActivity(intent);
                break;
            case R.id.linearAddMachine:
                //close the popup
                animObj.rotateViewAntiClockwise(imgOptions);
                animObj.closePopUpMenu(linearOptions);
                animObj.setInterFaceObj(new PopupAnimationEnd() {
                    @Override
                    public void animationCompleted() {
                        linearOptions.setVisibility(View.GONE);
                        isImageUp = true;
                    }
                });

                intent = new Intent(getActivity(), MachineListActivity.class);
                startActivity(intent);
                break;
            case R.id.linearAddScheduler:
                // close pop up
                animObj.rotateViewAntiClockwise(imgOptions);
                animObj.closePopUpMenu(linearOptions);
                animObj.setInterFaceObj(new PopupAnimationEnd() {
                    @Override
                    public void animationCompleted() {
                        linearOptions.setVisibility(View.GONE);
                        isImageUp = true;
                    }
                });
                break;
        }
    }

    @Override
    public void animationCompleted() {

    }


    private void updateSceneList() {
        linearSceneList.removeAllViews();
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        try {
            dbHelper.openDataBase();

            final Cursor sceneCursor = dbHelper.getAllScenes(DashboardFragment.MACHINE_IP);
            dbHelper.close();

            if (sceneCursor != null) {

                sceneCursor.moveToFirst();
                if (sceneCursor.getCount() > 0) {
                    do {
                        final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.dashboard_scene_slider_item, null);
                        TextView txtSceneName = (TextView) view.findViewById(R.id.txtSceneName);
                        final String sceneId = ""+sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_S_ID));
                        final String sceneName = sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_S_NAME));

                        txtSceneName.setText(sceneName);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), getActivity());
                        layoutParams.setMargins(margin, margin, margin, margin);
                        view.setLayoutParams(layoutParams);

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), SceneActivity.class);
                                intent.putExtra("scene_id", sceneId);
                                intent.putExtra("scene_name", sceneName);
                                startActivity(intent);
                            }
                        });

                        linearSceneList.addView(view);
                    } while (sceneCursor.moveToNext());
                } else {

                    TextView emptyMessage = new TextView(getActivity());
                    emptyMessage.setText("No Scenes Created");
                    emptyMessage.setTextColor(getResources().getColor(R.color.white));
                    linearSceneList.addView(emptyMessage);

                }
            }

        } catch (SQLException e) {
            Log.e("SQLEXP", e.toString());
        }

    }

    // fetch current status of all switches before OFF on main power.
    // Store the list of on switches(to maintain previous state for turning on the main power) and turn-off the on switches
    public class GetSwitchStatus extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL urlValue = new URL(DashboardFragment.URL_MACHINE_IP + AppConstants.URL_FETCH_SWITCH_STATUS);
                // Log.e("# urlValue", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();
                //  Log.e("# inputStream", inputStream.toString());
                MainXmlPullParser pullParser = new MainXmlPullParser();

                switchStatusList = pullParser.processXML(inputStream);
                // Log.e("XML PARSERED", dimmerStatusList.toString());


            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Log.e("TAG_ASYNC", "Inside onPostExecute");
            // store list of all on switches
            switchesWithOnStatus = new ArrayList<>();
            for(int i =0; i<switchStatusList.size();i++) {
                if(switchStatusList.get(i).tagValue.equals("01")) {
                    switchesWithOnStatus.add(switchStatusList.get(i).tagName);

                }
            }
            Log.e("ON SWITCHES", switchesWithOnStatus.toString());

            // turn off all the on switches
            for(int i=0; i< switchesWithOnStatus.size();i++) {
                String strPosition = switchesWithOnStatus.get(i).substring(2,4);
                String CHANGE_STATUS_URL = DashboardFragment.URL_MACHINE_IP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.OFF_VALUE;
                new ChangeSwitchStatus().execute(CHANGE_STATUS_URL);
            }
        }
    }

    // fetch current status of all dimmers before OFF on main power.
    // Store the list of on dimmers with their values(to maintain previous state for turning on the main power)
    // and turn-off the on dimmers
    public class GetDimmerStatus extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL urlValue = new URL(DashboardFragment.URL_MACHINE_IP + AppConstants.URL_FETCH_DIMMER_STATUS);
                // Log.e("# urlValue", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();
                //  Log.e("# inputStream", inputStream.toString());
                MainXmlPullParser pullParser = new MainXmlPullParser();

                dimmerStatusList = pullParser.processXML(inputStream);
               //  Log.e("XML PARSERED", dimmerStatusList.toString());


            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Log.e("TAG_ASYNC", "Inside onPostExecute");
            // store dimmer status
            dimmersWithOnStatus = new ArrayList<>();
            for(int i =0; i<dimmerStatusList.size();i++) {

                if(dimmerStatusList.get(i).tagValue.substring(0,2).equals("01")) {
                    dimmersWithOnStatus.add(dimmerStatusList.get(i));
                }
            }
            Log.e("ON DIMMERS", dimmersWithOnStatus.toString());

            // switch off on dimmers
            for(int i=0; i< dimmersWithOnStatus.size();i++) {
                XMLValues dimmer = dimmersWithOnStatus.get(i);
                String strPosition =  dimmer.tagName.substring(2, 4);
                String strProgress = dimmer.tagValue.substring(2, 4);
                String CHANGE_STATUS_URL = DashboardFragment.URL_MACHINE_IP + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.OFF_VALUE + strProgress;
                new ChangeDimmerStatus().execute(CHANGE_STATUS_URL);
            }
        }
    }

    public class ChangeSwitchStatus extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL urlValue = new URL(params[0]);
                Log.e("# urlValue", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();


            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            }
    }

    public class ChangeDimmerStatus extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL urlValue = new URL(params[0]);
                Log.e("# url change dimmer", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();


            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void call() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                new GetMachineStatus().execute();
                // isPowerOn = !isPowerOn;
                /*if (isPowerOn) {
                    animObj.cancelPowerButtonAnimation();
                } else {
                    animObj.startPowerButtonAnimation();
                }*/
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    public class GetMachineStatus extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
           // setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL urlValue = new URL(DashboardFragment.URL_MACHINE_IP + AppConstants.URL_FETCH_MACHINE_STATUS);
                 Log.e("# urlValue", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setConnectTimeout(1000*60);

                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();
                //  Log.e("# inputStream", inputStream.toString());
                MainXmlPullParser pullParser = new MainXmlPullParser();
                powerStatus = pullParser.processXML(inputStream);
                 Log.e("XML PARSERED", powerStatus.toString());
                for (int i = 0; i < powerStatus.size(); i++) {
                    if (powerStatus.get(i).tagName.equals("led0")) {
                        led = powerStatus.get(i).tagValue;
                        break;
                    }
                }

            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Log.e("TAG_ASYNC", "Inside onPostExecute");


            try {

                Log.e("Power_status", led);
                Log.e("Previous_status", previousLed);


                if (led.equals(previousLed)) {
                    powerSignalCount++;

                    if(powerSignalCount > 10) {
                       // Toast.makeText(activity, "Machine is disconnected", Toast.LENGTH_LONG).show();
                        ((HomeDrawerActivity) activity).setPowerButtonOff();
                        ((HomeDrawerActivity) activity).cancelPowerAnimation();
                        showOffScreen();
                        ((HomeDrawerActivity) activity).hideDrawer();
                        bulb_image.setClickable(false);
                    } else {
                        ((HomeDrawerActivity) activity).startPowerAnimation();
                        showOnScreen();
                        bulb_image.setClickable(true);
                        parentSwitches.setClickable(true);
                        parentMotor.setClickable(true);
                        parentSlider.setClickable(true);
                        parentSensors.setClickable(true);
                    }
                } else {
                    powerSignalCount = 0;
                    ((HomeDrawerActivity) activity).startPowerAnimation();
                    showOnScreen();
                    bulb_image.setClickable(true);
                    parentSwitches.setClickable(true);
                    parentMotor.setClickable(true);
                    parentSlider.setClickable(true);
                    parentSensors.setClickable(true);
                }
                previousLed = led;

            } catch(Exception e) {
               // Toast.makeText(activity, "Machine is disconnected", Toast.LENGTH_LONG).show();
                ((HomeDrawerActivity) activity).setPowerButtonOff();
                ((HomeDrawerActivity) activity).cancelPowerAnimation();
                showOffScreen();
                ((HomeDrawerActivity) activity).hideDrawer();
                bulb_image.setClickable(false);
            }











            /*try {
                for (int i = 0; i < powerStatus.size(); i++) {
                    if (powerStatus.get(i).tagName.equals("led0")) {
                         Log.e("Power_status", powerStatus.get(i).tagValue);
                        if (powerStatus.get(i).tagValue.equals("0")) {
                            if (isPowerOn) {
                                Toast.makeText(activity, "Machine is disconnected", Toast.LENGTH_LONG).show();
                            }
                            isPowerOn = false;
                            ((HomeDrawerActivity) activity).setPowerButtonOff();
                            ((HomeDrawerActivity) activity).cancelPowerAnimation();
                                showOffScreen() ;
                            ((HomeDrawerActivity) activity).hideDrawer();
                            bulb_image.setClickable(false);

                        } else {
                            Log.e("Power_status", powerStatus.get(i).tagValue);
                            isPowerOn = true;
                            //btn.setAlpha(1f);
                            ((HomeDrawerActivity) activity).startPowerAnimation();
                            bulb_image.setClickable(true);
                            parentSwitches.setClickable(true);
                            parentMotor.setClickable(true);
                            parentSlider.setClickable(true);
                            parentSensors.setClickable(true);
                        }
                        break;
                    }
                }

            }catch (Exception e){
                Toast.makeText(activity, "Machine is disconnected", Toast.LENGTH_LONG).show();
                isPowerOn = false;
                ((HomeDrawerActivity) activity).setPowerButtonOff();
                ((HomeDrawerActivity) activity).cancelPowerAnimation();
                showOffScreen();
                ((HomeDrawerActivity) activity).hideDrawer();
                bulb_image.setClickable(false);
            }*/


        }
    }

    private void showOffScreen() {
        linearDisabled.setAlpha(0.8f);
        linearDisabled.setVisibility(View.VISIBLE);

        imgOptions.setClickable(false);
        imgFavorites.setClickable(false);
        imgSchedulers.setClickable(false);
    }

    private void showOnScreen() {
        linearDisabled.setVisibility(View.GONE);
        imgOptions.setClickable(true);
        imgFavorites.setClickable(true);
        imgSchedulers.setClickable(true);
    }
}
