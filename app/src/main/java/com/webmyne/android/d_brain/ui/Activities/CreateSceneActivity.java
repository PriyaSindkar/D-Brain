package com.webmyne.android.d_brain.ui.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.CreateSceneAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.CustomProgressBar.ExternalCirclePainter;
import com.webmyne.android.d_brain.ui.Customcomponents.SaveAlertDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.SwitchListDialog;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Helpers.PopupAnimationEnd;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSaveSceneComponentsClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;
import com.webmyne.android.d_brain.ui.Widgets.SceneMotorItem;
import com.webmyne.android.d_brain.ui.Widgets.SceneSwitchItem;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CreateSceneActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private TextView txtSwitch, txtDimmer, txtMotor, txtSceneTitle;
    private ImageView imgHScrollLeft, imgHScrollRight, imgBack;
    private HorizontalScrollView hScrollView;
    private LinearLayout linearControls, linearPopup, linearSaveScene, layoutBottom;
    private RelativeLayout parentRelativeLayout;
    private EditText edtIPAddress;
    private View switchDivider;

    boolean isSwitchPopupShown = false;
    boolean isDimmerPopupShown = false;
    boolean isMotorPopupShown = false;


    private  AnimationHelper animationHelper = new AnimationHelper();

    private  ArrayList<SceneItemsDataObject> mData = new ArrayList<>();
    //private GridView gridView;
    private RecyclerView mRecycler;
    private CreateSceneAdapter mAdapter;

    private boolean isSceneSaved = true;

    private int totalNoOfSwitches = 77;
    private int totalNoOfMotors = 33;
    private int totalNoOfDimmers = 77;

    private  ArrayList<SceneSwitchItem> initSwitches = new ArrayList<>();
    private  ArrayList<SceneMotorItem> initMotors = new ArrayList<>();
    private  ArrayList<SceneSwitchItem> initDimmers = new ArrayList<>();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_scene);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            //toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
            setSupportActionBar(toolbar);
        }

        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConstants.getCurrentSsid(CreateSceneActivity.this);
    }

    private void init() {
        txtDimmer = (TextView) findViewById(R.id.txtDimmer);
        txtSwitch = (TextView) findViewById(R.id.txtSwitch);
        txtMotor = (TextView) findViewById(R.id.txtMotor);

        switchDivider = findViewById(R.id.switchDivider);

        hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);
        imgHScrollLeft = (ImageView) findViewById(R.id.imgHScrollLeft);
        imgHScrollRight = (ImageView) findViewById(R.id.imgHScrollRight);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        linearSaveScene = (LinearLayout) findViewById(R.id.linearSaveScene);
        edtIPAddress = (EditText) findViewById(R.id.edtIPAddress);
        txtSceneTitle = (TextView) findViewById(R.id.txtSceneTitle);

        txtSwitch.setOnClickListener(this);
        txtMotor.setOnClickListener(this);
        txtDimmer.setOnClickListener(this);
        linearSaveScene.setOnClickListener(this);

        // disabled initially
        linearSaveScene.setAlpha(0.5f);

        imgHScrollLeft.setOnClickListener(this);
        imgHScrollRight.setOnClickListener(this);
        hScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        linearControls =  (LinearLayout) findViewById(R.id.linearControls);
        linearPopup =  (LinearLayout) findViewById(R.id.linearPopup);
        parentRelativeLayout = (RelativeLayout) findViewById(R.id.parentRelativeLayout);

        layoutBottom = (LinearLayout) findViewById(R.id.layoutBottom);

        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemViewCacheSize(0);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);

        final int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), CreateSceneActivity.this);
        mRecycler.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mAdapter = new CreateSceneAdapter(this, mData);
        mRecycler.setAdapter(mAdapter);

        mAdapter.setDeleteClickListener(new onDeleteClickListener() {
            @Override
            public void onDeleteOptionClick(int pos) {
                String componentId = mData.get(pos).getSceneComponentPrimaryId();

                if (mData.get(pos).getSceneControlType().equals(AppConstants.SWITCH_TYPE)) {
                    for(int i=0;i<initSwitches.size();i++) {
                        if( initSwitches.get(i).getComponentPrimaryId().equals(componentId)){
                            initSwitches.get(i).setFocusable(true);
                            break;
                        }
                    }
                } else if (mData.get(pos).getSceneControlType().equals(AppConstants.DIMMER_TYPE)) {
                    for(int i=0;i<initDimmers.size();i++) {
                        if( initDimmers.get(i).getComponentPrimaryId().equals(componentId)){
                            initDimmers.get(i).setFocusable(true);
                            break;
                        }
                    }

                } else if (mData.get(pos).getSceneControlType().equals(AppConstants.MOTOR_TYPE)) {
                    initMotors.get(pos).setFocusable(true);
                } else {

                }
                mData.remove(pos);
                mAdapter.notifyDataSetChanged();
            }
        });

        // hide pop-up
        mAdapter.setSingleClickListener(new onSingleClickListener() {
            @Override
            public void onSingleClick(int pos) {
                txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                txtMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                linearPopup.setVisibility(View.INVISIBLE);
            }
        });

        initMotors();

        if( totalNoOfMotors==0 && totalNoOfDimmers == 0 && totalNoOfSwitches == 0) {
            layoutBottom.setVisibility(View.GONE);
        }

        parentRelativeLayout.setOnClickListener(this);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSceneSaved) {
                    finish();
                } else {
                    SaveAlertDialog saveAlertDialog = new SaveAlertDialog(CreateSceneActivity.this);
                    saveAlertDialog.show();

                    saveAlertDialog.setSaveListener(new onSaveClickListener() {
                        @Override
                        public void onSaveClick(boolean isSave) {
                            if(isSave) {
                                saveScene();
                            } else{
                                finish();
                            }
                        }
                    });
                }
            }
        });

        edtIPAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                linearSaveScene.setAlpha(1.0f);
                isSceneSaved = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if(isSceneSaved) {
            finish();
        } else {
            SaveAlertDialog saveAlertDialog = new SaveAlertDialog(CreateSceneActivity.this);
            saveAlertDialog.show();

            saveAlertDialog.setSaveListener(new onSaveClickListener() {
                @Override
                public void onSaveClick(boolean isSave) {
                    if(isSave) {
                        saveScene();
                    } else {
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_scene, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtSwitch:
                showSwitchPopup();
                break;
            case R.id.txtDimmer:
                showDimmerPopup();
                break;

            case R.id.txtMotor:
               // showMotorPopup();
                break;

            case R.id.imgHScrollLeft:
                hScrollView.scrollTo((int) hScrollView.getScrollX() - 20, (int) hScrollView.getScrollY());
                break;

            case R.id.imgHScrollRight:
                hScrollView.scrollTo((int) hScrollView.getScrollX() + 20, (int) hScrollView.getScrollY());
                break;

            case R.id.parentRelativeLayout:
                txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                txtMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                linearPopup.setVisibility(View.GONE);
                break;

            case R.id.linearSaveScene:
                if( !isSceneSaved) {
                    saveScene();
                }
                break;
        }

    }

    private void showSwitchPopup() {
        if( !isSwitchPopupShown) {

            txtSwitch.setBackgroundColor(getResources().getColor(R.color.baseButtonColor));
            txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            txtMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));

            isSwitchPopupShown = true;
            isDimmerPopupShown = false;
            isMotorPopupShown = false;

            List<String> alreadyAddedComponentList = new ArrayList<>();

            // init the multi-select list with already added components shown as checked (in case popup is dismissed and edited again)
            for(int i=0; i<mData.size(); i++) {
                alreadyAddedComponentList.add(mData.get(i).getSceneComponentPrimaryId());
            }

            SwitchListDialog switchListDialog = new SwitchListDialog(CreateSceneActivity.this, alreadyAddedComponentList, AppConstants.SWITCH_TYPE);
            switchListDialog.show();

            switchListDialog.setOnDismissListener(new onSingleClickListener() {
                @Override
                public void onSingleClick(int pos) {
                    txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    isSwitchPopupShown = false;
                    isDimmerPopupShown = false;
                    isMotorPopupShown = false;
                }
            });

            switchListDialog.setOnSaveListener(new onSaveSceneComponentsClickListener() {
                @Override
                public void onSaveClick(List<String> _selectedComponents, List<String> _unSelectedComponents) {
                    try {
                        DatabaseHelper dbHelper = new DatabaseHelper(CreateSceneActivity.this);
                        dbHelper.openDataBase();

                        // set all the selected components in the adapter
                        for (int i = 0; i < _selectedComponents.size(); i++) {
                            Cursor cursor = dbHelper.getComponentByPrimaryId(_selectedComponents.get(i));
                            final String componentPrimaryId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
                            final String componentId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                            final String componentName = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
                            final String componentType = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
                            final String machineId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID));
                            final String machineIp = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP));
                            final String machineName = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MNAME));
                            final String isActive = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));

                            final SceneItemsDataObject sceneItemsDataObject = new SceneItemsDataObject(componentType, componentName);
                            // set component_id in scene_component table
                            sceneItemsDataObject.setSceneItemId(componentId);
                            sceneItemsDataObject.setSceneComponentPrimaryId(componentPrimaryId);
                            sceneItemsDataObject.setMachineId(machineId);
                            sceneItemsDataObject.setMachineIP(machineIp);
                            sceneItemsDataObject.setMachineName(machineName);
                            sceneItemsDataObject.setDefaultValue(AppConstants.OFF_VALUE);
                            sceneItemsDataObject.setIsActive(isActive);

                            if (!mData.contains(sceneItemsDataObject)) {
                                mData.add(sceneItemsDataObject);
                            }
                        }

                        // remove the unselected components from the adapter
                        for (int i = 0; i < _unSelectedComponents.size(); i++) {

                            Cursor cursor = dbHelper.getComponentByPrimaryId(_unSelectedComponents.get(i));
                            final String componentPrimaryId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
                            final SceneItemsDataObject sceneItemsDataObject = new SceneItemsDataObject();
                            sceneItemsDataObject.setSceneComponentPrimaryId(componentPrimaryId);

                            if (mData.contains(sceneItemsDataObject)) {
                                mData.remove(sceneItemsDataObject);
                            }
                        }

                        dbHelper.close();
                    } catch (Exception e) {
                    }

                    mAdapter.notifyDataSetChanged();
                    isSceneSaved = false;
                    linearSaveScene.setAlpha(1.0f);

                    txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    isSwitchPopupShown = false;
                    isDimmerPopupShown = false;
                    isMotorPopupShown = false;
                }
            });
        } else { // if switch popup is opened, close it
            txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            isSwitchPopupShown = false;
            isDimmerPopupShown = false;
            isMotorPopupShown = false;
        }
    }

    private void showDimmerPopup() {
        if( !isDimmerPopupShown) {
            txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            txtMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            txtDimmer.setBackgroundColor(getResources().getColor(R.color.baseButtonColor));

            isSwitchPopupShown = false;
            isDimmerPopupShown = true;
            isMotorPopupShown = false;

            List<String> alreadyAddedComponentList = new ArrayList<>();

            // init the multi-select list with already added components shown as checked (in case popup is dismissed and edited again)
            for(int i=0; i<mData.size(); i++) {
                alreadyAddedComponentList.add(mData.get(i).getSceneComponentPrimaryId());
            }

            SwitchListDialog switchListDialog = new SwitchListDialog(CreateSceneActivity.this, alreadyAddedComponentList, AppConstants.DIMMER_TYPE);
            switchListDialog.show();

            switchListDialog.setOnDismissListener(new onSingleClickListener() {
                @Override
                public void onSingleClick(int pos) {
                    txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    isSwitchPopupShown = false;
                    isDimmerPopupShown = false;
                    isMotorPopupShown = false;
                }
            });

            switchListDialog.setOnSaveListener(new onSaveSceneComponentsClickListener() {
                @Override
                public void onSaveClick(List<String> _selectedComponents, List<String> _unSelectedComponents) {
                    try {
                        DatabaseHelper dbHelper = new DatabaseHelper(CreateSceneActivity.this);
                        dbHelper.openDataBase();

                        // set all the selected components in the adapter
                        for (int i = 0; i < _selectedComponents.size(); i++) {
                            Cursor cursor = dbHelper.getComponentByPrimaryId(_selectedComponents.get(i));
                            final String componentPrimaryId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
                            final String componentId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                            final String componentName = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
                            final String componentType = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
                            final String machineId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID));
                            final String machineIp = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP));
                            final String machineName = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MNAME));
                            final String isActive = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));

                            final SceneItemsDataObject sceneItemsDataObject = new SceneItemsDataObject(componentType, componentName);
                            // set component_id in scene_component table
                            sceneItemsDataObject.setSceneItemId(componentId);
                            sceneItemsDataObject.setSceneComponentPrimaryId(componentPrimaryId);
                            sceneItemsDataObject.setMachineId(machineId);
                            sceneItemsDataObject.setMachineIP(machineIp);
                            sceneItemsDataObject.setMachineName(machineName);
                            sceneItemsDataObject.setDefaultValue(AppConstants.OFF_VALUE);
                            sceneItemsDataObject.setIsActive(isActive);

                            if (!mData.contains(sceneItemsDataObject)) {
                                mData.add(sceneItemsDataObject);
                            }
                        }

                        // remove the unselected components from the adapter
                        for (int i = 0; i < _unSelectedComponents.size(); i++) {

                            Cursor cursor = dbHelper.getComponentByPrimaryId(_unSelectedComponents.get(i));
                            final String componentPrimaryId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
                            final SceneItemsDataObject sceneItemsDataObject = new SceneItemsDataObject();
                            sceneItemsDataObject.setSceneComponentPrimaryId(componentPrimaryId);

                            if (mData.contains(sceneItemsDataObject)) {
                                mData.remove(sceneItemsDataObject);
                            }
                        }

                        dbHelper.close();
                    } catch (Exception e) {
                    }

                    mAdapter.notifyDataSetChanged();
                    isSceneSaved = false;
                    linearSaveScene.setAlpha(1.0f);

                    txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    isSwitchPopupShown = false;
                    isDimmerPopupShown = false;
                    isMotorPopupShown = false;
                }
            });
        } else { // if dimmer popup is opened, close it
            txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            isSwitchPopupShown = false;
            isDimmerPopupShown = false;
            isMotorPopupShown = false;
        }
    }

    private void showMotorPopup() {
        if( !isMotorPopupShown) {

            txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            txtMotor.setBackgroundColor(getResources().getColor(R.color.baseButtonColor));

            isSwitchPopupShown = false;
            isDimmerPopupShown = false;
            isMotorPopupShown = true;

            linearPopup.setVisibility(View.VISIBLE);
            linearControls.removeAllViews();

            for(int i=0; i < initMotors.size(); i++) {
                linearControls.addView(initMotors.get(i));

                final int position = i;
                initMotors.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (initMotors.get(position).isFocusable()) {
                            SceneItemsDataObject sceneItemsDataObject =  new SceneItemsDataObject(AppConstants.MOTOR_TYPE, initMotors.get(position).getText());
                            //sceneItemsDataObject.setMachineIP(DashboardFragment.MACHINE_IP);
                            sceneItemsDataObject.setMachineName("");

                            mAdapter.add(mData.size(), sceneItemsDataObject);
                            initMotors.get(position).setFocusable(false);
                        } else {
                            Toast.makeText(CreateSceneActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        } else { // if switch popup is opened, close it
            txtMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            isSwitchPopupShown = false;
            isDimmerPopupShown = false;
            isMotorPopupShown = false;
        }
    }

    private void initMotors() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.openDataBase();
            Cursor motorListCursor = dbHelper.getAllMotorComponents();
            dbHelper.close();

            totalNoOfMotors = motorListCursor.getCount();

            if (motorListCursor != null) {
                motorListCursor.moveToFirst();
                if (motorListCursor.getCount() > 0) {
                    do {
                        /*SceneSwitchItem sceneSwitchItem = new SceneSwitchItem(CreateSceneActivity.this);
                        sceneSwitchItem.setText(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME)));
                        //dimmer id from main component table
                        sceneSwitchItem.setSwitchId(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID)));

                        initDimmers.add(sceneSwitchItem);*/
                    } while (motorListCursor.moveToNext());
                } else {
                    txtMotor.setVisibility(View.GONE);
                }
            } else {
                txtMotor.setVisibility(View.GONE);
            }
        } catch (SQLException e) {
            Log.e("SQLEXP", e.toString());
        }
    }

    private void saveScene() {
        if(edtIPAddress.getText().toString().trim().length() == 0) {
            Toast.makeText(CreateSceneActivity.this, "Please Enter Scene Name", Toast.LENGTH_SHORT).show();
        } else if (mData == null || mData.isEmpty()) {
            SaveAlertDialog saveAlertDialog = new SaveAlertDialog(CreateSceneActivity.this, "No Component Added. Do You Want To Exit Without Creating The Scene?");
            saveAlertDialog.show();

            saveAlertDialog.setSaveListener(new onSaveClickListener() {
                @Override
                public void onSaveClick(boolean isSave) {
                    if(isSave) {
                        finish();
                    }
                }
            });
        } else {
            // save scene in DB
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            try {
                dbHelper.openDataBase();

                dbHelper.createNewScene(edtIPAddress.getText().toString().trim(), mData);
                dbHelper.close();
                txtSceneTitle.setText(edtIPAddress.getText().toString().trim());
                isSceneSaved = true;
                linearSaveScene.setAlpha(0.5f);
                Toast.makeText(CreateSceneActivity.this, "Scene Created", Toast.LENGTH_SHORT).show();
                finish();
            } catch (SQLException e) {
                Log.e("SQLEXP", e.toString());
            }
        }
    }
}
