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
import com.webmyne.android.d_brain.ui.Customcomponents.SaveAlertDialog;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Helpers.PopupAnimationEnd;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;
import com.webmyne.android.d_brain.ui.Widgets.SceneMotorItem;
import com.webmyne.android.d_brain.ui.Widgets.SceneSwitchItem;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;


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
    private   ArrayList<SceneMotorItem> initMotors = new ArrayList<>();
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

       /* gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setNumColumns(1);

        mAdapter = new CreateSceneAdapter(this, mData);
        gridView.setAdapter(mAdapter);*/

        mAdapter.setDeleteClickListener(new onDeleteClickListener() {
            @Override
            public void onDeleteOptionClick(int pos) {
                String componentId = mData.get(pos).getSceneItemId();

                if (mData.get(pos).getSceneControlType().equals(AppConstants.SWITCH_TYPE)) {
                    for(int i=0;i<initSwitches.size();i++) {
                        if( initSwitches.get(i).getSwitchId().equals(componentId)){
                            initSwitches.get(i).setFocusable(true);
                            break;
                        }
                    }
                } else if (mData.get(pos).getSceneControlType().equals(AppConstants.DIMMER_TYPE)) {
                    for(int i=0;i<initDimmers.size();i++) {
                        if( initDimmers.get(i).getSwitchId().equals(componentId)){
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


        initSwitches();
        initMotors();
        initDimmers();

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

            linearPopup.setVisibility(View.VISIBLE);
            linearControls.removeAllViews();

            for(int i=0; i < initSwitches.size(); i++) {
                linearControls.addView(initSwitches.get(i));

                final int position = i;
                initSwitches.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(initSwitches.get(position).isFocusable()) {
                            SceneItemsDataObject sceneItemsDataObject =  new SceneItemsDataObject(AppConstants.SWITCH_TYPE, initSwitches.get(position).getText());
                            // set component_id in scene_component table
                            sceneItemsDataObject.setSceneItemId(initSwitches.get(position).getSwitchId());
                            sceneItemsDataObject.setMachineIP(initSwitches.get(position).getMachineIP());
                            sceneItemsDataObject.setMachineName(initSwitches.get(position).getMachineName());
                            sceneItemsDataObject.setDefaultValue(AppConstants.OFF_VALUE);

                           // mAdapter.add(mData.size(), sceneItemsDataObject);
                            mData.add(sceneItemsDataObject);
                            mAdapter.notifyDataSetChanged();
                            initSwitches.get(position).setFocusable(false);
                            isSceneSaved = false;
                        } else {
                            Toast.makeText(CreateSceneActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
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
                            SceneItemsDataObject sceneItemsDataObject =  new SceneItemsDataObject(AppConstants.DIMMER_TYPE, initDimmers.get(position).getText());
                            // set component_id in scene_component table
                            sceneItemsDataObject.setSceneItemId(initDimmers.get(position).getSwitchId());
                            sceneItemsDataObject.setMachineIP(initSwitches.get(position).getMachineIP());
                            sceneItemsDataObject.setMachineName(initSwitches.get(position).getMachineName());
                            sceneItemsDataObject.setDefaultValue(AppConstants.DIMMER_DEFAULT_VALUE);

                          //  mAdapter.add(mData.size(), sceneItemsDataObject);
                            mData.add(sceneItemsDataObject);
                            mAdapter.notifyDataSetChanged();
                            initDimmers.get(position).setFocusable(false);
                            isSceneSaved = false;
                        } else {
                            Toast.makeText(CreateSceneActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
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
                            SceneItemsDataObject sceneItemsDataObject =  new SceneItemsDataObject(AppConstants.MOTOR_TYPE, initMotors.get(position).getText());
                            sceneItemsDataObject.setMachineIP(DashboardFragment.MACHINE_IP);
                            sceneItemsDataObject.setMachineName("");

                            mAdapter.add(mData.size(), sceneItemsDataObject);
                            initMotors.get(position).setFocusable(false);
                        } else {
                            Toast.makeText(CreateSceneActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
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
            Cursor switchListCursor = dbHelper.getAllSwitchComponentsForAMachine(DashboardFragment.MACHINE_IP);
            dbHelper.close();

            totalNoOfSwitches = switchListCursor.getCount();

            if (switchListCursor != null) {
                switchListCursor.moveToFirst();
                if (switchListCursor.getCount() > 0) {
                    do {
                        SceneSwitchItem sceneSwitchItem = new SceneSwitchItem(CreateSceneActivity.this);
                        sceneSwitchItem.setText(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME)));
                        //switch id from main component table
                        sceneSwitchItem.setSwitchId(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID)));
                        sceneSwitchItem.setMachineIP(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP)));
                        sceneSwitchItem.setMachineName(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MNAME)));

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
            Log.e("SQLEXP", e.toString());
        }

       /* for(int i=0; i < totalNoOfSwitches; i++) {
            SceneSwitchItem sceneSwitchItem = new SceneSwitchItem(CreateSceneActivity.this);
            sceneSwitchItem.setText("Switch " + i);
            initSwitches.add(sceneSwitchItem);
        }*/
    }

    private void initMotors() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.openDataBase();
            Cursor motorListCursor = dbHelper.getAllMotorComponentsForAMachine(DashboardFragment.MACHINE_IP);
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

    private void initDimmers() {

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.openDataBase();
            Cursor dimmerListCursor = dbHelper.getAllDimmerComponentsForAMachine(DashboardFragment.MACHINE_IP);
            dbHelper.close();

            totalNoOfDimmers = dimmerListCursor.getCount();

            if (dimmerListCursor != null) {
                dimmerListCursor.moveToFirst();
                if (dimmerListCursor.getCount() > 0) {
                    do {
                        SceneSwitchItem sceneSwitchItem = new SceneSwitchItem(CreateSceneActivity.this);
                        sceneSwitchItem.setText(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME)));
                        //dimmer id from main component table
                        sceneSwitchItem.setSwitchId(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID)));
                        sceneSwitchItem.setMachineIP(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP)));
                        sceneSwitchItem.setMachineName(dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MNAME)));
                        initDimmers.add(sceneSwitchItem);
                    } while (dimmerListCursor.moveToNext());
                } else {
                    txtDimmer.setVisibility(View.GONE);
                }
            } else {
                txtDimmer.setVisibility(View.GONE);
            }
        } catch (SQLException e) {
            Log.e("SQLEXP", e.toString());
        }

        /*for(int i=0; i < totalNoOfMotors; i++) {
            SceneDimmerItem sceneSwitchItem = new SceneDimmerItem(CreateSceneActivity.this);
            sceneSwitchItem.setText("Dimmer " + i);
            initDimmers.add(sceneSwitchItem);
        }*/
    }


    private void saveScene() {
        if(edtIPAddress.getText().toString().trim().length() == 0) {
            Toast.makeText(CreateSceneActivity.this, "Please Enter Scene Name", Toast.LENGTH_SHORT).show();
        } else {

            // save scene in DB
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            try {
                dbHelper.openDataBase();

                dbHelper.createNewScene(edtIPAddress.getText().toString().trim(), mData);
                dbHelper.close();
                txtSceneTitle.setText(edtIPAddress.getText().toString().trim());
                isSceneSaved = true;
                Toast.makeText(CreateSceneActivity.this, "Scene Saved", Toast.LENGTH_SHORT).show();

                finish();
            } catch (SQLException e) {
                Log.e("SQLEXP", e.toString());
            }
        }
    }
}
