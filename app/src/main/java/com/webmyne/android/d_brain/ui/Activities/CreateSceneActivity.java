package com.webmyne.android.d_brain.ui.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.CreateSceneAdapter;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Helpers.PopupAnimationEnd;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;
import com.webmyne.android.d_brain.ui.Widgets.SceneDimmerItem;
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
    private LinearLayout linearControls, linearPopup, linearSaveScene;
    private RelativeLayout parentRelativeLayout;
    private EditText edtIPAddress;

    boolean isSwitchPopupShown = false;
    boolean isDimmerPopupShown = false;
    boolean isMotorPopupShown = false;

    private  AnimationHelper animationHelper = new AnimationHelper();

    private  ArrayList<SceneItemsDataObject> mData = new ArrayList<>();
    private GridView gridView;
    private   CreateSceneAdapter mAdapter;

    private int totalNoOfSwitches = 77;
    private int totalNoOfMotors = 33;
    private int totalNoOfDimmers = 77;

    private  ArrayList<SceneSwitchItem> initSwitches = new ArrayList<>();
    private   ArrayList<SceneMotorItem> initMotors = new ArrayList<>();
    private  ArrayList<SceneDimmerItem> initDimmers = new ArrayList<>();


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


        gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setNumColumns(3);

        mAdapter = new CreateSceneAdapter(this,mData);
        gridView.setAdapter(mAdapter);

        mAdapter.setOnDeleteClick(new onDeleteClickListener() {
            @Override
            public void onDeleteOptionClick(int pos) {
                if (mData.get(pos).getSceneControlType().equals(AppConstants.SWITCH_TYPE)) {
                    initSwitches.get(pos).setFocusable(true);
                    mData.remove(pos);
                    mAdapter.notifyDataSetChanged();
                } else if (mData.get(pos).getSceneControlType().equals(AppConstants.DIMMER_TYPE)) {
                    initDimmers.get(pos).setFocusable(true);
                    mData.remove(pos);
                    mAdapter.notifyDataSetChanged();
                } else if (mData.get(pos).getSceneControlType().equals(AppConstants.MOTOR_TYPE)) {
                    initMotors.get(pos).setFocusable(true);
                    mData.remove(pos);
                    mAdapter.notifyDataSetChanged();
                } else {

                }
            }
        });

        // hide pop-up
        mAdapter.set_onSingleClick(new onSingleClickListener() {
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

        parentRelativeLayout.setOnClickListener(this);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
               // showDimmerPopup();
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
                        Toast.makeText(CreateSceneActivity.this, "Scene Saved", Toast.LENGTH_SHORT).show();
                    } catch (SQLException e) {
                        Log.e("SQLEXP", e.toString());
                    }
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
                            sceneItemsDataObject.setSceneItemId(initSwitches.get(position).getSwitchId());
                            sceneItemsDataObject.setMachineIP(DBConstants.MACHINE1_IP);
                            sceneItemsDataObject.setMachineID("");

                            mAdapter.add(mData.size(), sceneItemsDataObject);
                            initSwitches.get(position).setFocusable(false);
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
                            sceneItemsDataObject.setMachineIP(DBConstants.MACHINE1_IP);
                            sceneItemsDataObject.setMachineID("");

                            mAdapter.add(mData.size(), sceneItemsDataObject);
                            initDimmers.get(position).setFocusable(false);
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
                            sceneItemsDataObject.setMachineIP(DBConstants.MACHINE1_IP);
                            sceneItemsDataObject.setMachineID("");

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
            Cursor switchListCursor = dbHelper.getAllSwitchComponentsForAMachine(DBConstants.MACHINE1_IP);
            dbHelper.close();

            totalNoOfSwitches = switchListCursor.getCount();

            if (switchListCursor != null) {
                switchListCursor.moveToFirst();
                if (switchListCursor.getCount() > 0) {
                    do {
                        SceneSwitchItem sceneSwitchItem = new SceneSwitchItem(CreateSceneActivity.this);
                        sceneSwitchItem.setText(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME)));
                        sceneSwitchItem.setSwitchId(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID)));

                        initSwitches.add(sceneSwitchItem);
                    } while (switchListCursor.moveToNext());
                }
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
        for(int i=0; i < totalNoOfMotors; i++) {
            SceneMotorItem sceneSwitchItem = new SceneMotorItem(CreateSceneActivity.this);
            sceneSwitchItem.setText("Motor " + i);
            initMotors.add(sceneSwitchItem);
        }
    }

    private void initDimmers() {
        for(int i=0; i < totalNoOfMotors; i++) {
            SceneDimmerItem sceneSwitchItem = new SceneDimmerItem(CreateSceneActivity.this);
            sceneSwitchItem.setText("Dimmer " + i);
            initDimmers.add(sceneSwitchItem);
        }
    }
}