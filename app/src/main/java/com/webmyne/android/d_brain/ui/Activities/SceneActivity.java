package com.webmyne.android.d_brain.ui.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.SceneAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.AddToSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.EditSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.SaveAlertDialog;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Helpers.PopupAnimationEnd;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;
import com.webmyne.android.d_brain.ui.Model.SchedulerModel;
import com.webmyne.android.d_brain.ui.Model.onItemClickListener;
import com.webmyne.android.d_brain.ui.Widgets.SceneMotorItem;
import com.webmyne.android.d_brain.ui.Widgets.SceneSwitchItem;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;


public class SceneActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private TextView txtSwitch, txtDimmer, txtMotor, edtSceneName;
    private ImageView imgHScrollLeft, imgHScrollRight, imgBack;
    private HorizontalScrollView hScrollView;
    private LinearLayout linearControls, linearPopup, linearSaveScene;
    private RelativeLayout parentRelativeLayout;
    private SwitchButton sceneMainSwitch;
    private View switchDivider;


    boolean isSwitchPopupShown = false;
    boolean isDimmerPopupShown = false;
    boolean isMotorPopupShown = false;

    AnimationHelper animationHelper = new AnimationHelper();

    private ArrayList<SceneItemsDataObject> mData = new ArrayList<>();
    private ArrayList<SceneItemsDataObject> newMData = new ArrayList<>();
    private ArrayList<SceneItemsDataObject> updatedMData = new ArrayList<>();
    private ArrayList<SceneItemsDataObject> deletedMData = new ArrayList<>();

    RecyclerView mRecycler;
    SceneAdapter mAdapter;
    private Cursor switchListCursor,dimmerListCursor, motorListCursor;

    private String currentSceneId, currentSceneName;
    private boolean isSceneSaved = true;
    private boolean isSceneNamedChanged = false;

    private int totalNoOfSwitches = 77;
    private int totalNoOfMotors = 33;
    private int totalNoOfDimmers = 77;

    ArrayList<SceneSwitchItem> initSwitches = new ArrayList<>();
    ArrayList<SceneMotorItem> initMotors = new ArrayList<>();
    ArrayList<SceneSwitchItem> initDimmers = new ArrayList<>();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            //toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
            setSupportActionBar(toolbar);
        }

        init();

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
        edtSceneName = (TextView) findViewById(R.id.edtSceneName);
        linearSaveScene = (LinearLayout) findViewById(R.id.linearSaveScene);
        sceneMainSwitch = (SwitchButton) findViewById(R.id.sceneMainSwitch);

        txtSwitch.setOnClickListener(this);
        txtMotor.setOnClickListener(this);
        txtDimmer.setOnClickListener(this);
        linearSaveScene.setOnClickListener(this);
        sceneMainSwitch.setOnClickListener(this);

        sceneMainSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getActionMasked() == MotionEvent.ACTION_MOVE;
            }
        });

        imgHScrollLeft.setOnClickListener(this);
        imgHScrollRight.setOnClickListener(this);
        hScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        linearControls =  (LinearLayout) findViewById(R.id.linearControls);
        linearPopup =  (LinearLayout) findViewById(R.id.linearPopup);
        parentRelativeLayout = (RelativeLayout) findViewById(R.id.parentRelativeLayout);


        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemViewCacheSize(0);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);

        final int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), SceneActivity.this);
        mRecycler.addItemDecoration(new VerticalSpaceItemDecoration(margin));

        mAdapter = new SceneAdapter(this, mData);
       // mAdapter.setType(AppConstants.SWITCH_TYPE);
        mRecycler.setAdapter(mAdapter);

        currentSceneId = getIntent().getStringExtra("scene_id");
        currentSceneName = getIntent().getStringExtra("scene_name");

        showSceneSavedState();
        initSwitches();
        initMotors();
        initDimmers();

        //if component added from outside
        if(getIntent().getStringExtra("new_component_id") != null) {
            addComponentToScene(getIntent().getStringExtra("new_component_id"), getIntent().getStringExtra("new_component_type"));
        }

        mRecycler.setOnClickListener(this);

        parentRelativeLayout.setOnClickListener(this);

        mAdapter.setOnItemClick(new onItemClickListener() {
            @Override
            public void _onItemClickListener() {
                txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                txtMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                linearPopup.setVisibility(View.INVISIBLE);
            }
        });

        mAdapter.setSingleClickListener(new onSingleClickListener() {
            @Override
            public void onSingleClick(int pos) {
                //Toast.makeText(DimmerListActivity.this, "Single Click Item Pos: " + pos, Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setDeleteClickListener(new onDeleteClickListener() {
            @Override
            public void onDeleteOptionClick(int pos) {

                // remove the deleted component from update list
                if(updatedMData.contains(mData.get(pos))) {
                    updatedMData.remove(mData.get(pos));
                }

                // remove the deleted component from new list
                if( newMData.contains(mData.get(pos))) {
                    newMData.remove(mData.get(pos));
                }

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

                }  else if (mData.get(pos).getSceneControlType().equals(AppConstants.MOTOR_TYPE)) {
                    initMotors.get(pos).setFocusable(true);
                } else {

                }

                isSceneSaved = false;
                deletedMData.add(mData.get(pos));
                mData.remove(pos);
                mAdapter.notifyDataSetChanged();

            }
        });

        mAdapter.setCheckedChangeListener(new onCheckedChangeListener() {
            @Override
            public void onCheckedChangeClick(int pos) {
                isSceneSaved = false;

                if(!newMData.contains(mData.get(pos)) ) {
                    updatedMData.add(mData.get(pos));
                }
            }

            @Override
            public void onCheckedPreChangeClick(int pos) {

            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSceneSaved) {
                    finish();
                } else {
                    SaveAlertDialog saveAlertDialog = new SaveAlertDialog(SceneActivity.this);
                    saveAlertDialog.show();

                    saveAlertDialog.setSaveListener(new onSaveClickListener() {
                        @Override
                        public void onSaveClick(boolean isSave) {
                            if (isSave) {
                                if(saveScene()) {
                                    finish();
                                }
                            } else {
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConstants.getCurrentSsid(SceneActivity.this);
    }


    @Override
    public void onBackPressed() {
        if(isSceneSaved) {
            finish();
        } else {
            SaveAlertDialog saveAlertDialog = new SaveAlertDialog(SceneActivity.this);
            saveAlertDialog.show();

            saveAlertDialog.setSaveListener(new onSaveClickListener() {
                @Override
                public void onSaveClick(boolean isSave) {
                    if(isSave) {
                        if(saveScene()) {
                            finish();
                        }
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

        switch (id) {
            case R.id.action_rename:
                RenameDialog renameDialog = new RenameDialog(SceneActivity.this, currentSceneName);
                renameDialog.show();

                renameDialog.setRenameListener(new onRenameClickListener() {
                    @Override
                    public void onRenameOptionClick(int pos, String newName) {
                        isSceneNamedChanged = true;
                        isSceneSaved = false;
                        currentSceneName = newName;
                        edtSceneName.setText(newName);
                    }

                    @Override
                    public void onRenameOptionClick(int pos, String newName, String newDetails) {

                    }
                });
                break;

            case R.id.action_delete:
                SaveAlertDialog saveAlertDialog = new SaveAlertDialog(SceneActivity.this, "Are you sure you want to delete the scene?");
                saveAlertDialog.show();

                saveAlertDialog.setSaveListener(new onSaveClickListener() {
                    @Override
                    public void onSaveClick(boolean isSave) {
                        if (isSave) {
                            DatabaseHelper dbHelper = new DatabaseHelper(SceneActivity.this);
                            try {
                                dbHelper.openDataBase();
                                dbHelper.deleteScene(currentSceneId);
                                dbHelper.close();
                                Toast.makeText(SceneActivity.this, "Scene deleted", Toast.LENGTH_LONG).show();
                                finish();
                            } catch (Exception e) {
                            }
                        }
                    }
                });
                break;

            case R.id.action_add_to_scheduler:
                addComponentToScheduler();
                break;

        }
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
              //  showMotorPopup();
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

            case R.id.sceneMainSwitch:
                sceneMainSwitch.toggle();
                String sceneStatus="";
                if(sceneMainSwitch.isChecked()) {
                    sceneStatus = "no";
                    new CallSceneOff().execute();
                } else {
                    sceneStatus = "yes";
                    new CallSceneOn().execute();
                }

                DatabaseHelper dbHelper = new DatabaseHelper(this);
                try {
                    dbHelper.openDataBase();
                    dbHelper.updateSceneStatus(currentSceneId, sceneStatus);
                    dbHelper.close();
                }catch (Exception e) {

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

            linearPopup.setVisibility(View.VISIBLE);
            linearControls.removeAllViews();

            for(int i=0; i < initSwitches.size(); i++) {
                linearControls.addView(initSwitches.get(i));

                final int position = i;
                initSwitches.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (initSwitches.get(position).isFocusable()) {
                            addSwitchToScene(position);
                        } else {
                            if(initSwitches.get(position).getIsActive().equals("true")) {
                                Toast.makeText(SceneActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }

            animationHelper.viewPopUpMenuFromBottomLeft(linearPopup);


        } else { // if switch popup is opened, close it

            txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));

            isSwitchPopupShown = false;
            isDimmerPopupShown = false;
            isMotorPopupShown = false;

            animationHelper.closePopUpMenuFromBottomLeft(linearPopup);
            animationHelper.setInterFaceObj(new PopupAnimationEnd() {
                @Override
                public void animationCompleted() {
                    linearPopup.setVisibility(View.INVISIBLE);
                }
            });
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

            linearPopup.setVisibility(View.VISIBLE);
            linearControls.removeAllViews();

            for(int i=0; i < initDimmers.size(); i++) {
                linearControls.addView(initDimmers.get(i));

                final int position = i;
                initDimmers.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (initDimmers.get(position).isFocusable()) {
                            addDimmerToScene(position);
                        } else {
                            if(initDimmers.get(position).getIsActive().equals("true")) {
                                Toast.makeText(SceneActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
            }

            animationHelper.viewPopUpMenuFromBottomLeft(linearPopup);

        } else { // if switch popup is opened, close it
            txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));

            isSwitchPopupShown = false;
            isDimmerPopupShown = false;
            isMotorPopupShown = false;

            animationHelper.closePopUpMenuFromBottomLeft(linearPopup);
            animationHelper.setInterFaceObj(new PopupAnimationEnd() {
                @Override
                public void animationCompleted() {
                    linearPopup.setVisibility(View.INVISIBLE);
                }
            });
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
                            mAdapter.add(mData.size(), new SceneItemsDataObject(AppConstants.MOTOR_TYPE, initMotors.get(position).getText()));
                            initMotors.get(position).setFocusable(false);
                        } else {
                            Toast.makeText(SceneActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

            animationHelper.viewPopUpMenuFromBottomLeft(linearPopup);

        } else { // if switch popup is opened, close it
            txtMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            isSwitchPopupShown = false;
            isDimmerPopupShown = false;
            isMotorPopupShown = false;

            animationHelper.closePopUpMenuFromBottomLeft(linearPopup);
            animationHelper.setInterFaceObj(new PopupAnimationEnd() {
                @Override
                public void animationCompleted() {
                    linearPopup.setVisibility(View.INVISIBLE);
                }
            });
        }
    }


    private void initSwitches() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.openDataBase();
            switchListCursor = dbHelper.getAllSwitchComponents();
            dbHelper.close();

            totalNoOfSwitches = switchListCursor.getCount();

            if (switchListCursor != null) {
                switchListCursor.moveToFirst();
                if (switchListCursor.getCount() > 0) {
                    do {
                        SceneSwitchItem sceneSwitchItem = new SceneSwitchItem(SceneActivity.this);
                        sceneSwitchItem.setText(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME)));
                        sceneSwitchItem.setSwitchId(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_COMPONENT_ID)));
                        sceneSwitchItem.setComponentPrimaryId(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID)));
                        sceneSwitchItem.setMachineId(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID)));
                        sceneSwitchItem.setMachineIP(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP)));
                        sceneSwitchItem.setMachineName(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MNAME)));
                        String isActive = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));
                        sceneSwitchItem.setIsActive(isActive);
                        sceneSwitchItem.enableDisableComponent(isActive);

                        // check if this component is already added to the scene or not
                        for(int i=0; i<mData.size(); i++) {
                            if(mData.get(i).getSceneComponentPrimaryId().equals(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID)))) {
                                sceneSwitchItem.setFocusable(false);
                            }
                        }
                        initSwitches.add(sceneSwitchItem);
                    } while (switchListCursor.moveToNext());
                } else {
                    txtSwitch.setVisibility(View.GONE);
                    switchDivider.setVisibility(View.GONE);
                }
            } else {
                txtSwitch.setVisibility(View.GONE);
                switchDivider.setVisibility(View.GONE);
            }
        } catch (SQLException e) {
            Log.e("SQLException", e.toString());
        }
    }

    private void initMotors() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.openDataBase();
            motorListCursor = dbHelper.getAllMotorComponents();
            dbHelper.close();

            totalNoOfMotors = motorListCursor.getCount();

            if (motorListCursor != null) {
                motorListCursor.moveToFirst();
                if (motorListCursor.getCount() > 0) {
                    do {
                        /*SceneSwitchItem sceneSwitchItem = new SceneSwitchItem(SceneActivity.this);
                        sceneSwitchItem.setText(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME)));
                        sceneSwitchItem.setSwitchId(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_COMPONENT_ID)));

                        // check if this component is already added or not
                        for(int i=0; i<mData.size(); i++) {
                            if(mData.get(i).getSceneItemId().equals(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_COMPONENT_ID)))) {
                                sceneSwitchItem.setFocusable(false);
                            }
                        }

                        initDimmers.add(sceneSwitchItem);*/
                    } while (motorListCursor.moveToNext());
                } else {
                    txtMotor.setVisibility(View.GONE);
                }
            } else {
                txtMotor.setVisibility(View.GONE);
            }
        } catch (SQLException e) {
            Log.e("SQLException", e.toString());
        }
    }

    private void initDimmers() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.openDataBase();
            dimmerListCursor = dbHelper.getAllDimmerComponents();
            dbHelper.close();

            totalNoOfDimmers = dimmerListCursor.getCount();

            if (dimmerListCursor != null) {
                dimmerListCursor.moveToFirst();
                if (dimmerListCursor.getCount() > 0) {
                    do {
                        SceneSwitchItem sceneSwitchItem = new SceneSwitchItem(SceneActivity.this);
                        sceneSwitchItem.setText(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME)));
                        sceneSwitchItem.setSwitchId(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_COMPONENT_ID)));
                        sceneSwitchItem.setComponentPrimaryId(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID)));
                        sceneSwitchItem.setMachineId(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID)));
                        sceneSwitchItem.setMachineIP(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP)));
                        sceneSwitchItem.setMachineName(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MNAME)));
                        String isActive = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));
                        sceneSwitchItem.setIsActive(isActive);
                        sceneSwitchItem.enableDisableComponent(isActive);

                        // check if this component is already added or not
                        for(int i=0; i<mData.size(); i++) {
                            if(mData.get(i).getSceneComponentPrimaryId().equals(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID)))) {
                                sceneSwitchItem.setFocusable(false);
                            }
                        }

                        initDimmers.add(sceneSwitchItem);
                    } while (dimmerListCursor.moveToNext());
                } else {
                    txtDimmer.setVisibility(View.GONE);
                }
            } else {
                txtDimmer.setVisibility(View.GONE);
            }
        } catch (SQLException e) {
            Log.e("SQLException", e.toString());
        }
    }

    private void showSceneSavedState() {
        // show scene saved state
        edtSceneName.setText(currentSceneName);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.openDataBase();
            Cursor switchListCursor = dbHelper.getAllComponentsInAScene(currentSceneId);
            if(dbHelper.getSceneStatus(currentSceneId).equals("yes")) {
                sceneMainSwitch.setChecked(true);
            } else {
                sceneMainSwitch.setChecked(false);
            }
            mData.clear();
            if (switchListCursor != null) {
                switchListCursor.moveToFirst();
                if (switchListCursor.getCount() > 0) {
                    do {
                        String componentId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_COMPONENT_ID));
                        String componentPrimaryId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_COMP_PRIMARY_ID));
                        String defaultValue = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_DEFAULT));
                        String machineIP = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_MIP));
                        String machineID = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_MID));
                        String machineName = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_MNAME));
                        String isActive = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));

                        SceneItemsDataObject sceneItemsDataObject = new SceneItemsDataObject();
                        sceneItemsDataObject.setMachineIP(machineIP);
                        sceneItemsDataObject.setMachineId(machineID);
                        sceneItemsDataObject.setMachineName(machineName);
                        sceneItemsDataObject.setSceneControlType(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_TYPE)));
                        sceneItemsDataObject.setSceneItemId(componentId);
                        sceneItemsDataObject.setSceneComponentPrimaryId(componentPrimaryId);
                        sceneItemsDataObject.setIsActive(isActive);

                        String componentName = dbHelper.getComponentNameByPrimaryId(componentId);
                        //String componentName = componentCursor.getString(componentCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));

                        sceneItemsDataObject.setName(componentName);
                        sceneItemsDataObject.setDefaultValue(defaultValue);
                        mData.add(sceneItemsDataObject);

                    } while (switchListCursor.moveToNext());
                }
            }
            dbHelper.close();
            mAdapter.notifyDataSetChanged();
        } catch (SQLException e) {
            Log.e("SQLEXP", e.toString());
        }
    }

    private boolean saveScene() {
        // save scene in DB
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.openDataBase();

            // if the scene is renamed
            if(isSceneNamedChanged) {
                if(currentSceneName.length() == 0) {
                    Toast.makeText(SceneActivity.this, "Please Enter Scene Name", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    dbHelper.renameScene(currentSceneId, currentSceneName);
                }
            }

            // if scene components are updated
            if( !updatedMData.isEmpty()) {
                dbHelper.updateSceneComponents(updatedMData);
            }

            //if new components are added to the scene
            if(!newMData.isEmpty()) {
                dbHelper.addNewSceneComponents(currentSceneId, newMData);
            }

            //if scene component is deleted
            if( !deletedMData.isEmpty()) {
                dbHelper.deleteSceneComponents(deletedMData);
            }

            dbHelper.close();
            isSceneSaved = true;
            Toast.makeText(SceneActivity.this, "Scene Updated", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Log.e("SQLEXP", e.toString());
        }
        return true;
    }

    private void addComponentToScene(String componentId, String componentType) {
        if(componentType.equals(AppConstants.SWITCH_TYPE)) {
            for (int i = 0; i < initSwitches.size(); i++) {
                if (initSwitches.get(i).getComponentPrimaryId().equals(componentId)) {
                    if (initSwitches.get(i).isFocusable()) {
                        addSwitchToScene(i);
                    } else {
                        if(initSwitches.get(i).getIsActive().equals("true")) {
                            Toast.makeText(SceneActivity.this, "Component Already Added To Scene", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
            }
        } else if(componentType.equals(AppConstants.DIMMER_TYPE)) {
            for (int i = 0; i < initDimmers.size(); i++) {
                if (initDimmers.get(i).getComponentPrimaryId().equals(componentId)) {
                    if (initDimmers.get(i).isFocusable()) {
                        addDimmerToScene(i);
                    } else {
                        if(initDimmers.get(i).getIsActive().equals("true")) {
                            Toast.makeText(SceneActivity.this, "Component Already Added To Scene", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
            }
        }
    }

    private void addSwitchToScene(int position){
        SceneItemsDataObject sceneItemsDataObject = new SceneItemsDataObject(AppConstants.SWITCH_TYPE, initSwitches.get(position).getText());
        sceneItemsDataObject.setSceneItemId(initSwitches.get(position).getSwitchId());
        sceneItemsDataObject.setSceneComponentPrimaryId(initSwitches.get(position).getComponentPrimaryId());
        sceneItemsDataObject.setMachineId(initSwitches.get(position).getMachineId());
        sceneItemsDataObject.setMachineIP(initSwitches.get(position).getMachineIP());
        sceneItemsDataObject.setMachineName(initSwitches.get(position).getMachineName());

        sceneItemsDataObject.setDefaultValue(AppConstants.OFF_VALUE);
        sceneItemsDataObject.setIsActive(initSwitches.get(position).getIsActive());

        mData.add(sceneItemsDataObject);


        // remove the new component from deleted list
        if (deletedMData.contains(sceneItemsDataObject)) {
            deletedMData.remove(sceneItemsDataObject);
        }

        // remove the new component from updated list
        if (updatedMData.contains(sceneItemsDataObject)) {
            updatedMData.remove(sceneItemsDataObject);
        }

        // to save this new component
        newMData.add(sceneItemsDataObject);
        mAdapter.notifyDataSetChanged();
        initSwitches.get(position).setFocusable(false);
        isSceneSaved = false;

    }

    private void addDimmerToScene(int position){
        SceneItemsDataObject sceneItemsDataObject = new SceneItemsDataObject(AppConstants.DIMMER_TYPE, initDimmers.get(position).getText());
        sceneItemsDataObject.setSceneItemId(initDimmers.get(position).getSwitchId());
        sceneItemsDataObject.setSceneComponentPrimaryId(initDimmers.get(position).getComponentPrimaryId());
        sceneItemsDataObject.setMachineId(initDimmers.get(position).getMachineId());
        sceneItemsDataObject.setMachineIP(initDimmers.get(position).getMachineIP());
        sceneItemsDataObject.setMachineName(initDimmers.get(position).getMachineName());
        sceneItemsDataObject.setDefaultValue(AppConstants.OFF_VALUE);
        sceneItemsDataObject.setIsActive(initDimmers.get(position).getIsActive());
        //mAdapter.add(mData.size(), sceneItemsDataObject);
        mData.add(sceneItemsDataObject);

        // remove the new component from deleted list
        if(deletedMData.contains(sceneItemsDataObject)) {
            deletedMData.remove(sceneItemsDataObject);
        }

        // remove the new component from updated list
        if(updatedMData.contains(sceneItemsDataObject)) {
            updatedMData.remove(sceneItemsDataObject);
        }

        // to save this new component
        newMData.add(sceneItemsDataObject);

        mAdapter.notifyDataSetChanged();
        initDimmers.get(position).setFocusable(false);
        isSceneSaved = false;

    }

    private void addComponentToScheduler() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor sceneCursor = null;
        try {
            dbHelper.openDataBase();
            sceneCursor = dbHelper.getScenebyId(currentSceneId);

        if(sceneCursor != null) {
            sceneCursor.moveToFirst();
            String componentId1 = sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_S_ID));
            String componentName = sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_S_NAME));

            boolean isAlreadyScheduled = dbHelper.isAlreadyScheduled(componentId1);

            if(isAlreadyScheduled) {
                SchedulerModel scheduledModel = null;
                scheduledModel =  dbHelper.getSchedulerByComponentId(componentId1, "", "1");
                dbHelper.close();


                if(scheduledModel != null) {
                    EditSchedulerDialog addToSchedulerDialog = new EditSchedulerDialog(SceneActivity.this, scheduledModel);
                    addToSchedulerDialog.show();

                    addToSchedulerDialog.setOnSingleClick(new onSingleClickListener() {
                        @Override
                        public void onSingleClick(int pos) {

                        }
                    });
                }
            } else {
                SchedulerModel schedulerModel = new SchedulerModel("", componentId1, "", componentName, AppConstants.SCENE_TYPE, "", "", "", true, "00");
                AddToSchedulerDialog addToSchedulerDialog = new AddToSchedulerDialog(SceneActivity.this, schedulerModel);
                addToSchedulerDialog.show();
            }
        }

        }catch (Exception e) {}
    }

    public class CallSceneOn extends AsyncTask<Void, Void, Void> {
        boolean isError = false, isMachineActive = false;
        String machineId="", machineName = "", machineIp;
        Cursor  machineCursor;

        @Override
        protected Void doInBackground(Void... params) {

                //setting default wal
                for(int i=0;i<mData.size();i++) {
                    String strPosition;
                    strPosition = String.format("%02d",  Integer.parseInt(mData.get(i).getSceneItemId().substring(2,4)));

                    String SET_STATUS_URL = "";
                    String baseMachineUrl = "";

                    machineIp = mData.get(i).getMachineIP();

                    if(machineIp.startsWith("http://")) {
                        baseMachineUrl = machineIp;
                    } else {
                        baseMachineUrl = "http://"+ machineIp;
                    }
                    DatabaseHelper dbHelper = new DatabaseHelper(SceneActivity.this);

                    try {
                        isMachineActive =  dbHelper.isMachineActive(machineIp);
                        machineCursor = dbHelper.getMachineByIP(machineIp);
                        machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                        machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));


                        if(isMachineActive) {
                            // set defaults for switch
                            if (mData.get(i).getSceneControlType().equals(AppConstants.SWITCH_TYPE)) {
                                if (mData.get(i).getDefaultValue().equals(AppConstants.OFF_VALUE)) {
                                    SET_STATUS_URL = baseMachineUrl + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.OFF_VALUE;
                                } else {
                                    SET_STATUS_URL = baseMachineUrl + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.ON_VALUE;
                                }
                            }

                            // set defaults for dimmer
                            if (mData.get(i).getSceneControlType().equals(AppConstants.DIMMER_TYPE)) {
                                String dimmerValue = "00";
                                if (!mData.get(i).getDefaultValue().equals("00") && !mData.get(i).getDefaultValue().equals("0")) {
                                    dimmerValue = String.format("%02d", Integer.parseInt(mData.get(i).getDefaultValue()) - 1);
                                }
                                if (mData.get(i).getDefaultValue().equals(AppConstants.OFF_VALUE)) {
                                    SET_STATUS_URL = baseMachineUrl + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.OFF_VALUE + dimmerValue;
                                } else {
                                    SET_STATUS_URL = baseMachineUrl + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.ON_VALUE + dimmerValue;
                                }
                            }

                            URL urlValue = new URL(SET_STATUS_URL);
                            Log.e("# urlValue", urlValue.toString());

                            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                            httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                            httpUrlConnection.setRequestMethod("GET");
                            InputStream inputStream = httpUrlConnection.getInputStream();
                        } else {
                            try {
                                dbHelper.openDataBase();
                                dbHelper.enableDisableMachine(machineId, false);
                                dbHelper.close();
                                Toast.makeText(SceneActivity.this, "Machine, " + machineName + " was deactivated.", Toast.LENGTH_LONG).show();
                            } catch (SQLException e) {
                                Log.e("TAG EXP", e.toString());
                            }
                        }
                    } catch (Exception e) {
                        Log.e("# EXP", e.toString());
                        isError = true;
                    }
                }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
                if(isError) {
                    try {
                        DatabaseHelper dbHelper = new DatabaseHelper(SceneActivity.this);
                        dbHelper.openDataBase();
                        dbHelper.enableDisableMachine(machineId, false);
                        dbHelper.close();
                       // Toast.makeText(SceneActivity.this, "Machine, " + machineName + " was deactivated.", Toast.LENGTH_LONG).show();
                    } catch (SQLException e) {
                        Log.e("TAG EXP", e.toString());
                    }
                }
            }catch(Exception e){
            }
        }
    }

    public class CallSceneOff extends AsyncTask<Void, Void, Void> {
        boolean isError = false, isMachineActive = false;
        String machineId="", machineName = "", machineIp;
        Cursor  machineCursor;
        @Override
        protected Void doInBackground(Void... params) {

                //setting default wal
                for(int i=0;i<mData.size();i++) {
                    String strPosition;
                    strPosition = String.format("%02d",  Integer.parseInt(mData.get(i).getSceneItemId().substring(2,4)));
                    String SET_STATUS_URL = "";

                    String baseMachineUrl = "";
                    machineIp = mData.get(i).getMachineIP();

                    if(machineIp.startsWith("http://")) {
                        baseMachineUrl = machineIp;
                    } else {
                        baseMachineUrl = "http://"+ machineIp;
                    }
                    DatabaseHelper dbHelper = new DatabaseHelper(SceneActivity.this);
                    try {
                        isMachineActive =  dbHelper.isMachineActive(machineIp);
                        machineCursor = dbHelper.getMachineByIP(machineIp);
                        machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                        machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));

                        if(isMachineActive) {
                            // for switch
                            if (mData.get(i).getSceneControlType().equals(AppConstants.SWITCH_TYPE)) {
                                SET_STATUS_URL = baseMachineUrl + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.OFF_VALUE;
                            }

                            // for dimmers
                            if (mData.get(i).getSceneControlType().equals(AppConstants.DIMMER_TYPE)) {
                                String dimmerValue = "00";
                                if (!mData.get(i).getDefaultValue().equals("00") && !mData.get(i).getDefaultValue().equals("0")) {
                                    dimmerValue = String.format("%02d", Integer.parseInt(mData.get(i).getDefaultValue()) - 1);
                                }
                                SET_STATUS_URL = baseMachineUrl + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.OFF_VALUE + dimmerValue;
                            }

                            URL urlValue = new URL(SET_STATUS_URL);
                            Log.e("# urlValue", urlValue.toString());

                            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                            httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                            httpUrlConnection.setRequestMethod("GET");
                            InputStream inputStream = httpUrlConnection.getInputStream();
                        } else {
                            try {
                                dbHelper.openDataBase();
                                dbHelper.enableDisableMachine(machineId, false);
                                dbHelper.close();
                                Toast.makeText(SceneActivity.this, "Machine, " + machineName + " was deactivated.", Toast.LENGTH_LONG).show();
                            } catch (SQLException e) {
                                Log.e("TAG EXP", e.toString());
                            }
                        }
                    } catch (Exception e) {
                        Log.e("# EXP", e.toString());
                        isError = true;
                    }
                }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try{
                if(isError) {
                    try {
                        DatabaseHelper dbHelper = new DatabaseHelper(SceneActivity.this);
                        dbHelper.openDataBase();
                        dbHelper.enableDisableMachine(machineId, false);
                        dbHelper.close();
                        //Toast.makeText(SceneActivity.this, "Machine, " + machineName + " was deactivated.", Toast.LENGTH_LONG).show();
                    } catch (SQLException e) {
                        Log.e("TAG EXP", e.toString());
                    }
                }
            }catch(Exception e){
            }
        }
    }
}
