package com.webmyne.android.d_brain.ui.Fragments;


import android.app.ProgressDialog;
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

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.CreateSceneActivity;
import com.webmyne.android.d_brain.ui.Activities.FavouriteListActivity;
import com.webmyne.android.d_brain.ui.Activities.MachineListActivity;
import com.webmyne.android.d_brain.ui.Activities.MainDimmersListActivity;
import com.webmyne.android.d_brain.ui.Activities.MainSwitchesListActivity;
import com.webmyne.android.d_brain.ui.Activities.MotorListActivity;
import com.webmyne.android.d_brain.ui.Activities.SceneActivity;
import com.webmyne.android.d_brain.ui.Activities.SchedulersListActivity;
import com.webmyne.android.d_brain.ui.Activities.SensorsListActivity;
import com.webmyne.android.d_brain.ui.Activities.TouchPanelActivity;
import com.webmyne.android.d_brain.ui.Customcomponents.MachineInactiveDialog;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Helpers.ComplexPreferences;
import com.webmyne.android.d_brain.ui.Helpers.PopupAnimationEnd;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.Model.UserSettings;
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
import java.util.Timer;
import java.util.TimerTask;

public class DashboardFragment extends Fragment implements PopupAnimationEnd, View.OnClickListener {

    private AnimationHelper animObj;
    private ImageView imgOptions, imgFavorites, imgSchedulers, bulb_image;
    private boolean isImageUp = true, isBulbOn;
    private LinearLayout layoutBottom, linearOptions, linearSceneList, linearDisabled;
    private HorizontalScrollView hScrollView;
    private FrameLayout parentMotor, parentSlider, parentSwitches, parentSensors, linearLeft;
    private TextView txtNoOfSwitchUnits, txtNoOfMotorUnits, txtNoOfSensorUnits, txtNoOfSliderUnits, txtPowerOffMessage, txtPowerOffMessageLink;
    private LinearLayout linearCreateScene, linearAddMachine, linearAddScheduler, firstBottomItem, linearTopComponentRow, linearBottomComponentRow, linearFavorites;
    private ArrayList<XMLValues> switchStatusList, dimmerStatusList;
    private Cursor switchListCursor, dimmerListCursor, motorListCursor, sensorListCursor, machineCursor, machineListCursor;
    private boolean isPowerOn = true, mainPowerOnOffFlag;
    private ArrayList<XMLValues> powerStatus;
    private FragmentActivity activity;
    private int powerSignalCount = 0;
    private String previousLed = "", led = "";
    private String[] machineIPs;
    private ProgressDialog progressDilaog;

    private ArrayList<ComponentModel> allOnSwitchesList, allOnDimmersList;

    private int topRowComponentCount = 0; // fix layout-> switches and motors
    private int bottomRowComponentCount = 0; // fix layout-> Dimmers and sensors
    private Timer timer1;
    private Handler handler1;
    private Context mContext;
    int timeOutErrorCount = 0, changeSwitchTimeOutErorrCount = 0, changeDimmerTimeOutErorrCount = 0, fetchStatusTimeOutErrorCount = 0;

    public void startTherad() {
        handler1 = new Handler();
        timer1 = new Timer();


        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        new GetMachineStatus().execute(machineIPs);
                    }
                });

            }
        }, 0, 2000 * 1);
    }

    public void stopTherad() {
        try {
            timer1.cancel();
        } catch (Exception e) {

        }
    }

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
        txtPowerOffMessage = (TextView) row.findViewById(R.id.txtPowerOffMessage);
        txtPowerOffMessageLink = (TextView) row.findViewById(R.id.txtPowerOffMessageLink);

        linearDisabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        progressDilaog = new ProgressDialog(getActivity());
        progressDilaog.setMessage("Checking Machine Status...");
        progressDilaog.setCancelable(false);


        linearLeft = (FrameLayout) row.findViewById(R.id.linearLeft);
        firstBottomItem = (LinearLayout) row.findViewById(R.id.firstBottomItem);

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
        linearAddScheduler = (LinearLayout) row.findViewById(R.id.linearShowTouchPanel);
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

        linearBottomComponentRow = (LinearLayout) row.findViewById(R.id.linearBottomComponentRow);
        linearTopComponentRow = (LinearLayout) row.findViewById(R.id.linearTopComponentRow);

        linearFavorites = (LinearLayout) row.findViewById(R.id.linearFavorites);
        linearFavorites.setOnClickListener(this);

        bulb_image.setClickable(true);

       /* parentSwitches.setClickable(false);
        parentMotor.setClickable(false);
        parentSlider.setClickable(false);
        parentSensors.setClickable(false);*/

        hScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        linearOptions.setVisibility(View.INVISIBLE);

        imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
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
                } catch (Exception e) {
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

        ((HomeDrawerActivity) getActivity()).initPowerButton();
        // call();
    }

    @Override
    public void onResume() {
        super.onResume();

        initComponents();

        HomeDrawerActivity homeScreen = ((HomeDrawerActivity) getActivity());
        homeScreen.setTitle("Main Panel");
        homeScreen.hideAppBarButton();

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "settings-pref", 0);
        UserSettings userSettings = complexPreferences.getObject("settings-pref", UserSettings.class);

        if (userSettings != null) {
            if (!userSettings.isMainPowerOn()) {
                showOffScreen();
                ((HomeDrawerActivity) getActivity()).hideDrawer();

                bulb_image.setColorFilter(getResources().getColor(R.color.white));
                bulb_image.setBackgroundResource(R.drawable.white_border_circle);
                isBulbOn = false;
            } else {
                bulb_image.setColorFilter(getResources().getColor(R.color.yellowBorder));
                bulb_image.setBackgroundResource(R.drawable.circle);

                showOnScreen();
                ((HomeDrawerActivity) getActivity()).showDrawer();
                isBulbOn = true;
            }
        } else {
            bulb_image.setColorFilter(getResources().getColor(R.color.yellowBorder));
            bulb_image.setBackgroundResource(R.drawable.circle);

            showOnScreen();
            ((HomeDrawerActivity) getActivity()).showDrawer();
        }

        // to check periodically if all the machines are working or not
        startTherad();

        updateSceneList();

    }


    @Override
    public void onPause() {
        super.onPause();

        stopTherad();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopTherad();

    }

    @Override
    public void onStop() {
        super.onStop();

        stopTherad();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parentMotor:
                Intent intent = new Intent(getActivity(), MotorListActivity.class);
                startActivity(intent);
                break;

            case R.id.parentSlider:
                stopTherad();
                intent = new Intent(getActivity(), MainDimmersListActivity.class);
                startActivity(intent);
                break;
            case R.id.parentSwitches:
                stopTherad();
                intent = new Intent(getActivity(), MainSwitchesListActivity.class);
                startActivity(intent);
                break;

            case R.id.parentSensors:
                intent = new Intent(getActivity(), SensorsListActivity.class);
                startActivity(intent);
                break;

            case R.id.bulb_image:
                txtPowerOffMessageLink.setVisibility(View.GONE);
                if (isBulbOn) {
                    mainPowerOnOffFlag = false;
                    bulb_image.setColorFilter(getResources().getColor(R.color.white));
                    bulb_image.setBackgroundResource(R.drawable.white_border_circle);

                    // if switches exist, fetch their status
                    if (switchListCursor != null && switchListCursor.getCount() > 0) {
                        new GetSwitchStatus().execute(machineIPs);
                    }

                    // if dimmers exist, fetch their status
                    if (dimmerListCursor != null && dimmerListCursor.getCount() > 0) {
                        new GetDimmerStatus().execute(machineIPs);
                    }

                    txtPowerOffMessage.setText(getString(R.string.power_off_text));
                    showOffScreen();
                    ((HomeDrawerActivity) getActivity()).hideDrawer();

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "settings-pref", 0);
                    UserSettings userSettings = complexPreferences.getObject("settings-pref", UserSettings.class);

                    if (userSettings == null) {
                        userSettings = new UserSettings();
                    }
                    userSettings.setIsMainPowerOn(false);
                    complexPreferences.putObject("settings-pref", userSettings);
                    complexPreferences.commit();

                } else {
                    mainPowerOnOffFlag = true;
                    bulb_image.setColorFilter(getResources().getColor(R.color.yellowBorder));
                    bulb_image.setBackgroundResource(R.drawable.circle);

                    showOnScreen();
                    ((HomeDrawerActivity) getActivity()).showDrawer();

                    // switch on all the previously saved on switches(set prior to main power off)
                    if (allOnSwitchesList != null && !allOnSwitchesList.isEmpty()) {
                        for (int i = 0; i < allOnSwitchesList.size(); i++) {
                            String strPosition = allOnSwitchesList.get(i).getName().substring(2, 4);
                            String CHANGE_STATUS_URL = allOnSwitchesList.get(i).getMip() + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.ON_VALUE;
                            String[] params = new String[3];
                            params[0] = CHANGE_STATUS_URL;
                            params[1] = allOnSwitchesList.get(i).getMid();
                            params[2] = allOnSwitchesList.get(i).getMachineName();
                            new ChangeSwitchStatus().execute(params);
                        }
                    }

                    // switch on all the previously saved on dimmers with previously saved value(set prior to main power off)
                    if (allOnDimmersList != null && !allOnDimmersList.isEmpty()) {
                        for (int i = 0; i < allOnDimmersList.size(); i++) {
                            ComponentModel dimmer = allOnDimmersList.get(i);
                            String strPosition = dimmer.getName().substring(2, 4);
                            String strProgress = dimmer.getDefaultValue().substring(2, 4);
                            String CHANGE_STATUS_URL = dimmer.getMip() + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.ON_VALUE + strProgress;
                            String[] params = new String[3];
                            params[0] = CHANGE_STATUS_URL;
                            params[1] = dimmer.getMid();
                            params[2] = dimmer.getMachineName();
                            new ChangeDimmerStatus().execute(params);
                        }
                    }

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "settings-pref", 0);
                    UserSettings userSettings = complexPreferences.getObject("settings-pref", UserSettings.class);

                    if (userSettings == null) {
                        userSettings = new UserSettings();
                    }
                    userSettings.setIsMainPowerOn(true);
                    complexPreferences.putObject("settings-pref", userSettings);
                    complexPreferences.commit();
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
            case R.id.linearShowTouchPanel:
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

                intent = new Intent(getActivity(), TouchPanelActivity.class);
                startActivity(intent);
                break;

            case R.id.imgFavorites:
                intent = new Intent(getActivity(), FavouriteListActivity.class);
                startActivity(intent);
                break;

            case R.id.imgSchedulers:
                intent = new Intent(getActivity(), SchedulersListActivity.class);
                startActivity(intent);
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

            final Cursor sceneCursor = dbHelper.getAllScenes("");
            dbHelper.close();

            if (sceneCursor != null) {

                sceneCursor.moveToFirst();
                if (sceneCursor.getCount() > 0) {
                    do {
                        final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.dashboard_scene_slider_item, null);
                        TextView txtSceneName = (TextView) view.findViewById(R.id.txtSceneName);
                        final String sceneId = "" + sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_S_ID));
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


    private void initComponents() {
        DatabaseHelper dbHelper = new DatabaseHelper(activity);
        try {
            dbHelper.openDataBase();

            switchListCursor = dbHelper.getAllSwitchComponents();
            dimmerListCursor = dbHelper.getAllDimmerComponents();
            motorListCursor = dbHelper.getAllMotorComponents();
            sensorListCursor = dbHelper.getAllSensorsComponents();
            machineListCursor = dbHelper.getAllMachines();

            if (machineListCursor != null) {
                if (machineListCursor.getCount() > 0) {
                    machineIPs = new String[machineListCursor.getCount()];
                    machineListCursor.moveToFirst();
                    int i = 0;
                    do {
                        String machineIP = machineListCursor.getString(machineListCursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP));
                        machineIPs[i] = machineIP;
                        i++;
                    } while (machineListCursor.moveToNext());
                }
            }
            dbHelper.close();

            // get no of switches from db, if 0 no, switches not shown
            if (switchListCursor != null) {
                if (switchListCursor.getCount() == 0) {
                    parentSwitches.setVisibility(View.GONE);
                } else {
                    topRowComponentCount++;
                    parentSwitches.setVisibility(View.VISIBLE);
                    txtNoOfSwitchUnits.setText(String.valueOf(switchListCursor.getCount()));
                }
            } else {
                parentSwitches.setVisibility(View.GONE);
            }

            // get no of dimmers from db, if 0 no, dimmeres not shown
            if (dimmerListCursor != null) {
                if (dimmerListCursor.getCount() == 0) {
                    parentSlider.setVisibility(View.GONE);
                } else {
                    parentSlider.setVisibility(View.VISIBLE);
                    bottomRowComponentCount++;
                    txtNoOfSliderUnits.setText(String.valueOf(dimmerListCursor.getCount()));
                }
            } else {
                parentSlider.setVisibility(View.GONE);
            }

            // get no of motors from db, if 0 no, motors not shown
            if (motorListCursor != null) {
                if (motorListCursor.getCount() == 0) {
                    parentMotor.setVisibility(View.GONE);
                    // set right margin of left side block to 0
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                    lp.rightMargin = 0;
                    parentSwitches.setLayoutParams(lp);
                } else {
                    parentMotor.setVisibility(View.VISIBLE);
                    topRowComponentCount++;
                    txtNoOfMotorUnits.setText(String.valueOf(motorListCursor.getCount()));

                    // set right margin of left side block to original size
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                    lp.rightMargin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), activity);
                    parentSwitches.setLayoutParams(lp);
                }
            } else {
                parentMotor.setVisibility(View.GONE);
            }

            // get no of sensors/alerts from db, if 0 no, sensors/alerts not shown
            if (sensorListCursor != null) {
                if (sensorListCursor.getCount() == 0) {
                    parentSensors.setVisibility(View.GONE);
                    // set right margin of left side block to 0
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                    lp.rightMargin = 0;
                    parentSlider.setLayoutParams(lp);
                } else {
                    bottomRowComponentCount++;
                    parentSensors.setVisibility(View.VISIBLE);
                    txtNoOfSensorUnits.setText(String.valueOf(sensorListCursor.getCount()));
                }
            } else {
                parentSensors.setVisibility(View.GONE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (topRowComponentCount < 2 && topRowComponentCount > 0) {
            parentSwitches.setBackgroundColor(getResources().getColor(R.color.baseButtonColorTint));
            parentMotor.setBackgroundColor(getResources().getColor(R.color.baseButtonColorTint));

            parentSlider.setBackgroundColor(getResources().getColor(R.color.primaryColorTint));
            parentSensors.setBackgroundColor(getResources().getColor(R.color.primaryColorTint));
        } else if (topRowComponentCount == 0) {
            //check if any top row is empty, if yes, shift bottom component above
            if (bottomRowComponentCount == 2) {
                linearBottomComponentRow.removeView(parentSlider);
                linearTopComponentRow.addView(parentSlider);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                lp.rightMargin = 0;
                parentSlider.setLayoutParams(lp);
            } else {
                linearTopComponentRow.setVisibility(View.GONE);
            }
        }
        if (bottomRowComponentCount < 2 && bottomRowComponentCount > 0) {
            parentSwitches.setBackgroundColor(getResources().getColor(R.color.baseButtonColorTint));
            parentMotor.setBackgroundColor(getResources().getColor(R.color.baseButtonColorTint));

            parentSlider.setBackgroundColor(getResources().getColor(R.color.primaryColorTint));
            parentSensors.setBackgroundColor(getResources().getColor(R.color.primaryColorTint));
        } else if (bottomRowComponentCount == 0) {
            //check if any bottom row is empty, if yes, shift bottom component above
            if (topRowComponentCount == 2) {
                linearTopComponentRow.removeView(parentMotor);
                linearBottomComponentRow.addView(parentMotor);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                lp.rightMargin = 0;
                parentSwitches.setLayoutParams(lp);
            } else {
                linearBottomComponentRow.setVisibility(View.GONE);
            }
        }
    }

    // fetch current status of all switches before OFF on main power.
    // Store the list of on switches(to maintain previous state for turning on the main power) and turn-off the on switches


    public class GetSwitchStatus extends AsyncTask<String, Void, Void> {
        boolean isDeactivated = false, isMachineActive = false;
        String machineId = "", machineName = "", machineIp;
        Cursor cursor, machineCursor;
        ProgressDialog progressDialog1;
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        int position = 0;

        @Override
        protected void onPreExecute() {
            progressDialog1 = new ProgressDialog(activity);
            progressDialog1.setCancelable(false);
            progressDialog1.setMessage("Please wait ..");
            progressDialog1.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            allOnSwitchesList = new ArrayList<>();
            for (int i = 0; i < machineIPs.length; i++) {
                String machineBaseURL = "";
                machineIp = machineIPs[i];
                position = i; // save to continue calling debt of the same machine until errorCount > 3

                if (machineIp.startsWith("http://")) {
                    machineBaseURL = machineIp;
                } else {
                    machineBaseURL = "http://" + machineIp;
                }

                try {
                    dbHelper.openDataBase();
                    isMachineActive = dbHelper.isMachineActive(machineIp);
                    machineCursor = dbHelper.getMachineByIP(machineIp);
                    machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                    machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));
                    dbHelper.close();

                    // fetch switch status from machine only if the machine is active else init all the switch status to off
                    if (isMachineActive) {
                        URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_SWITCH_STATUS);
                        Log.e("# url", urlValue.toString());

                        HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                        httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                        httpUrlConnection.setRequestMethod("GET");
                        InputStream inputStream = httpUrlConnection.getInputStream();
                        //  Log.e("# inputStream", inputStream.toString());
                        MainXmlPullParser pullParser = new MainXmlPullParser();

                        switchStatusList = new ArrayList<>();
                        switchStatusList = pullParser.processXML(inputStream);

                        for (int k = 0; k < switchStatusList.size(); k++) {
                            if (switchStatusList.get(k).tagValue.equals("01")) {
                                ComponentModel componentModel = new ComponentModel();
                                componentModel.setName(switchStatusList.get(k).tagName);
                                componentModel.setDefaultValue(switchStatusList.get(k).tagValue);
                                componentModel.setMip(machineBaseURL);
                                componentModel.setMid(machineId);
                                componentModel.setMachineName(machineName);
                                allOnSwitchesList.add(componentModel);
                            }
                        }
                        isDeactivated = false;
                    }

                } catch (Exception e) {
                    Log.e("#TIMEOUT GetSwitchStatus", e.toString());
                    fetchStatusTimeOutErrorCount++;

                    if (fetchStatusTimeOutErrorCount > 3) {
                        isDeactivated = true;
                        fetchStatusTimeOutErrorCount = 0;
                        try {
                            dbHelper.openDataBase();
                            dbHelper.enableDisableMachine(machineId, false);
                            dbHelper.close();
                            //Toast.makeText(getActivity(), "Machine : " + machineName + " was deactivated.", Toast.LENGTH_LONG).show();
                        } catch (SQLException ex) {
                            Log.e("TAG EXP", ex.toString());
                        }
                    } else {
                        i = position;
                        continue;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog1.dismiss();

            if (isDeactivated) {

            } else {
                // turn off all the on switches
                for (int i = 0; i < allOnSwitchesList.size(); i++) {
                    String strPosition = allOnSwitchesList.get(i).getName().substring(2, 4);
                    String CHANGE_STATUS_URL = allOnSwitchesList.get(i).getMip() + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.OFF_VALUE;
                    String[] params = new String[3];
                    params[0] = CHANGE_STATUS_URL;
                    params[1] = allOnSwitchesList.get(i).getMid();
                    params[2] = allOnSwitchesList.get(i).getMachineName();
                    new ChangeSwitchStatus().execute(params);
                }
                changeSwitchTimeOutErorrCount = 0;
            }
        }
    }

    // fetch current status of all dimmers before OFF on main power.
    // Store the list of on dimmers with their values(to maintain previous state for turning on the main power)
    // and turn-off the on dimmers

    public class GetDimmerStatus extends AsyncTask<String, Void, Void> {
        boolean isDeactivated = false;
        String machineId = "", machineName = "", machineIp, isMachineActive = "false";
        Cursor cursor, machineCursor;
        ProgressDialog progressDialog1;
        DatabaseHelper dbHelper = new DatabaseHelper(activity);
        int position = 0;

        @Override
        protected void onPreExecute() {
            fetchStatusTimeOutErrorCount = 0;
            progressDialog1 = new ProgressDialog(activity);
            progressDialog1.setCancelable(false);
            progressDialog1.setMessage("Please wait ..");
            progressDialog1.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            allOnDimmersList = new ArrayList<>();
            for (int i = 0; i < machineIPs.length; i++) {
                String machineBaseURL = "";
                machineIp = machineIPs[i];
                position = i; // save to continue calling debt of the same machine until errorCount > 3

                if (machineIp.startsWith("http://")) {
                    machineBaseURL = machineIp;
                } else {
                    machineBaseURL = "http://" + machineIp;
                }

                try {
                    dbHelper.openDataBase();
                    machineCursor = dbHelper.getMachineByIP(machineIp);
                    machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                    machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));
                    isMachineActive = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));
                    dbHelper.close();

                    // fetch dimmer status from machine only if the machine is active else init all the dimmer status to off
                    if (isMachineActive.equals("true")) {
                        URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_DIMMER_STATUS);
                        Log.e("# url", urlValue.toString());

                        HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                        httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                        httpUrlConnection.setRequestMethod("GET");
                        InputStream inputStream = httpUrlConnection.getInputStream();
                        //Log.e("# inputStream dimmer" , inputStream.toString());
                        MainXmlPullParser pullParser = new MainXmlPullParser();

                        dimmerStatusList = new ArrayList<>();
                        dimmerStatusList = pullParser.processXML(inputStream);

                        for (int k = 0; k < dimmerStatusList.size(); k++) {
                            if (dimmerStatusList.get(k).tagValue.substring(0, 2).equals("01")) {
                                ComponentModel componentModel = new ComponentModel();
                                componentModel.setName(dimmerStatusList.get(k).tagName);
                                componentModel.setDefaultValue(dimmerStatusList.get(k).tagValue);
                                componentModel.setMip(machineBaseURL);
                                componentModel.setMid(machineId);
                                componentModel.setMachineName(machineName);
                                allOnDimmersList.add(componentModel);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("#TIMEOUT GetDimmerStatus", e.toString());
                    fetchStatusTimeOutErrorCount++;

                    if (fetchStatusTimeOutErrorCount > 3) {
                        isDeactivated = true;
                        fetchStatusTimeOutErrorCount = 0;
                        try {
                            dbHelper.openDataBase();
                            dbHelper.enableDisableMachine(machineId, false);
                            dbHelper.close();
                            //Toast.makeText(getActivity(), "Machine : " + machineName + " was deactivated.", Toast.LENGTH_LONG).show();
                        } catch (SQLException ex) {
                            Log.e("TAG EXP", ex.toString());
                        }
                    } else {
                        i = position;
                        continue;
                    }
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog1.dismiss();

            if (isDeactivated) {

            } else {
                // switch off on dimmers
                for (int i = 0; i < allOnDimmersList.size(); i++) {
                    ComponentModel dimmer = allOnDimmersList.get(i);
                    String strPosition = dimmer.getName().substring(2, 4);
                    String strProgress = dimmer.getDefaultValue().substring(2, 4);
                    String CHANGE_STATUS_URL = dimmer.getMip() + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.OFF_VALUE + strProgress;
                    String[] params = new String[3];
                    params[0] = CHANGE_STATUS_URL;
                    params[1] = dimmer.getMid();
                    params[2] = dimmer.getMachineName();
                    new ChangeDimmerStatus().execute(params);
                }
            }
        }
    }

    public class ChangeSwitchStatus extends AsyncTask<String, Void, Void> {
        boolean isMachineActived = true;
        String mid = "", mname = "", url = "";
        DatabaseHelper dbHelper = new DatabaseHelper(activity);
        ProgressDialog progressDialog1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog1 = new ProgressDialog(activity);
            progressDialog1.setCancelable(false);
            if( !mainPowerOnOffFlag) {
                progressDialog1.setMessage("Please wait while the machine is shutting down..");
            } else {
                progressDialog1.setMessage("Please wait while the machine is restarting..");
            }
            progressDialog1.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                url = params[0];
                URL urlValue = new URL(params[0]);
                mid = params[1];
                mname = params[2];
                Log.e("# url change switch", urlValue.toString());

                dbHelper.openDataBase();
                machineCursor = dbHelper.getMachineByID(mid);
                String isActive = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));
                dbHelper.close();

                if (isActive.equals("true")) {
                    HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                    httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                    httpUrlConnection.setRequestMethod("GET");
                    InputStream inputStream = httpUrlConnection.getInputStream();
                    isMachineActived = true;
                } else {
                    isMachineActived = true;
                }
            } catch (Exception e) {
                Log.e("#TIMEOUT ChangeSwitchStatus", changeSwitchTimeOutErorrCount+"");
                changeSwitchTimeOutErorrCount++;
                isMachineActived = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog1.dismiss();
            // machine became inactive.
            if (changeSwitchTimeOutErorrCount > 3) {
                isMachineActived = true;
                changeSwitchTimeOutErorrCount = 0;
                try {
                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                    dbHelper.openDataBase();
                    dbHelper.enableDisableMachine(mid, false);
                    dbHelper.close();
                    // Toast.makeText(getActivity(), "Machine, " + mname + " was deactivated.", Toast.LENGTH_LONG).show();
                } catch (SQLException e) {
                    Log.e("TAG EXP", e.toString());
                }
            } else {
                if (isMachineActived) {
                    changeSwitchTimeOutErorrCount = 0;
                } else {
                    String[] params = new String[3];
                    params[0] = url;
                    params[1] = mid;
                    params[2] = mname;
                    new ChangeSwitchStatus().execute(params);
                }
            }
        }
    }

    public class ChangeDimmerStatus extends AsyncTask<String, Void, Void> {
        boolean isMachineActived = true;
        String mid = "", mname = "", url = "";
        DatabaseHelper dbHelper = new DatabaseHelper(activity);
        ProgressDialog progressDialog1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog1 = new ProgressDialog(activity);
            progressDialog1.setCancelable(false);
            if( !mainPowerOnOffFlag) {
                progressDialog1.setMessage("Please wait while the machine is shutting down..");
            } else {
                progressDialog1.setMessage("Please wait while the machine is restarting..");
            }
           progressDialog1.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL urlValue = new URL(params[0]);
                url = params[0];
                mid = params[1];
                mname = params[2];
                Log.e("# url change dimmer", urlValue.toString());

                dbHelper.openDataBase();
                machineCursor = dbHelper.getMachineByID(mid);
                String isActive = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));
                dbHelper.close();

                if (isActive.equals("true")) {
                    HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                    httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                    httpUrlConnection.setRequestMethod("GET");
                    InputStream inputStream = httpUrlConnection.getInputStream();
                    isMachineActived = true;
                } else {
                    isMachineActived = true;
                }

            } catch (Exception e) {
                Log.e("#TIMEOUT ChangeDimmerStatus", e.toString());
                changeDimmerTimeOutErorrCount++;
                isMachineActived = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog1.dismiss();
            if (changeDimmerTimeOutErorrCount > 3) {
                isMachineActived = true;
                changeDimmerTimeOutErorrCount = 0;
                try {
                    dbHelper.openDataBase();
                    dbHelper.enableDisableMachine(mid, false);
                    dbHelper.close();
                    //Toast.makeText(getActivity(), "Machine : " + mname + " was deactivated.", Toast.LENGTH_LONG).show();
                } catch (SQLException e) {
                    Log.e("TAG EXP", e.toString());
                }
            } else {

                if (isMachineActived) {
                    changeDimmerTimeOutErorrCount = 0;
                } else {
                    String[] params = new String[3];
                    params[0] = url;
                    params[1] = mid;
                    params[2] = mname;
                    new ChangeDimmerStatus().execute(params);
                }
            }

        }
    }

    private void call() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                //new GetMachineStatus().execute();
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

    // to display the green LED on toolbar
   /* public class GetMachineStatus extends AsyncTask<Void, Void, Void> {

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
                httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);

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
        }
    }*/

    private void showOffScreen() {
        linearDisabled.setAlpha(0.8f);
        linearDisabled.setVisibility(View.VISIBLE);

        imgOptions.setClickable(false);
        imgFavorites.setClickable(false);
        imgSchedulers.setClickable(false);
    }

    private void showOnScreen() {
        linearDisabled.setVisibility(View.GONE);
        bulb_image.setClickable(false);
        imgOptions.setClickable(true);
        imgFavorites.setClickable(true);
        imgSchedulers.setClickable(true);
    }


    public class GetMachineStatus extends AsyncTask<String, Void, Void> {
        String machineId = "", machineName = "", machineIp, isMachineActive = "false";
        int totalMachineCount = machineIPs.length;

        @Override
        protected void onPreExecute() {
            //progressDilaog.show();
        }

        @Override
        protected Void doInBackground(String... machineIP) {
            for (int i = 0; i < machineIP.length; i++) {
                String machineBaseURL = "";
                machineIp = machineIP[i];
                int position = i; // save to continue calling debt of the same machine until errorCount > 10
                Log.e("loop ", i + " " + machineIp);

                if (machineIp.startsWith("http://")) {
                    machineBaseURL = machineIp;
                } else {
                    machineBaseURL = "http://" + machineIp;
                }

                DatabaseHelper dbHelper = new DatabaseHelper(activity);
                try {
                    dbHelper.openDataBase();
                    machineCursor = dbHelper.getMachineByIP(machineIp);
                    machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                    machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));
                    isMachineActive = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));
                    dbHelper.close();

                    if (isMachineActive.equals("true")) {
                        URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_MACHINE_STATUS);
                        Log.e("# urlValue", urlValue.toString());
                        Log.e("# totalMachineCount", totalMachineCount+"");

                        HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                        httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);

                        httpUrlConnection.setRequestMethod("GET");
                        InputStream inputStream = httpUrlConnection.getInputStream();
                        //totalMachineCount++;
                    } else {
                        Log.e("TAG_MACHINE", "Already Inactive " + machineIp);
                        //timeOutErrorCount = 0;
                        totalMachineCount--;
                    }
                } catch (Exception e) {
                    Log.e("# EXP123", e.toString());
                    timeOutErrorCount++;
                    Log.e("# timeOutErrorCount", timeOutErrorCount + "");

                    if (timeOutErrorCount > 10) {
                        timeOutErrorCount = 0;
                        totalMachineCount--;
                        Log.e("TAG IF", "time out");
                        try {
                            DatabaseHelper dbHelper1 = new DatabaseHelper(activity);
                            dbHelper1.openDataBase();
                            dbHelper1.enableDisableMachine(machineId, false);
                            dbHelper1.close();

                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    //show dialog
                                    MachineInactiveDialog machineNotActiveDialog = new MachineInactiveDialog(activity, "One of the machines was deactivated.");
                                    machineNotActiveDialog.show();
                                    machineNotActiveDialog.setSaveListener(new onSaveClickListener() {
                                        @Override
                                        public void onSaveClick(boolean isSave) {
                                            //stopTherad();
                                            //activity.finish();
                                        }
                                    });
                                }
                            });
                        } catch (Exception ex) {
                            Log.e("TAG EXP", ex.toString());
                        }
                    } else {
                        Log.e("TAG ELSE", "time out");
                        i = position;
                        continue;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e("TAG onPost", "Inside onPost");
            if (totalMachineCount == 0) {
                //Toast.makeText(getActivity(), getString(R.string.all_machines_off_text), Toast.LENGTH_LONG).show();
                txtPowerOffMessageLink.setVisibility(View.VISIBLE);
                txtPowerOffMessage.setText(getString(R.string.all_machines_off_text));

                txtPowerOffMessageLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MachineListActivity.class);
                        startActivity(intent);
                    }
                });

                showOffScreen();
                ((HomeDrawerActivity) getActivity()).hideDrawer();

                bulb_image.setColorFilter(getResources().getColor(R.color.white));
                bulb_image.setBackgroundResource(R.drawable.white_border_circle);
                bulb_image.setClickable(false);
            } else {
                bulb_image.setClickable(true);
            }
        }
    }
}
