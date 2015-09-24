package com.webmyne.android.d_brain.ui.Activities;

import android.database.Cursor;
import android.os.AsyncTask;
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

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.SceneAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.SaveAlertDialog;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Helpers.PopupAnimationEnd;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;
import com.webmyne.android.d_brain.ui.Model.onItemClickListener;
import com.webmyne.android.d_brain.ui.Widgets.SceneDimmerItem;
import com.webmyne.android.d_brain.ui.Widgets.SceneMotorItem;
import com.webmyne.android.d_brain.ui.Widgets.SceneSwitchItem;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;


public class SceneActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private TextView txtSwitch, txtDimmer, txtMotor;
    private EditText edtSceneName;
    private ImageView imgHScrollLeft, imgHScrollRight, imgBack;
    private HorizontalScrollView hScrollView;
    private LinearLayout linearControls, linearPopup, linearSaveScene;
    private RelativeLayout parentRelativeLayout;
    private SwitchButton sceneMainSwitch;


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
    private Cursor switchListCursor;

    private String currentSceneId, currentSceneName;
    private boolean isSceneSaved = true;
    private boolean isSceneNamedChanged = false;

    private int totalNoOfSwitches = 77;
    private int totalNoOfMotors = 33;
    private int totalNoOfDimmers = 77;

    ArrayList<SceneSwitchItem> initSwitches = new ArrayList<>();
    ArrayList<SceneMotorItem> initMotors = new ArrayList<>();
    ArrayList<SceneDimmerItem> initDimmers = new ArrayList<>();


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

        hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);
        imgHScrollLeft = (ImageView) findViewById(R.id.imgHScrollLeft);
        imgHScrollRight = (ImageView) findViewById(R.id.imgHScrollRight);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        edtSceneName = (EditText) findViewById(R.id.edtSceneName);
        linearSaveScene = (LinearLayout) findViewById(R.id.linearSaveScene);
        sceneMainSwitch = (SwitchButton) findViewById(R.id.sceneMainSwitch);

        txtSwitch.setOnClickListener(this);
        txtMotor.setOnClickListener(this);
        txtDimmer.setOnClickListener(this);
        linearSaveScene.setOnClickListener(this);
        sceneMainSwitch.setOnClickListener(this);

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
                //Toast.makeText(DimmerActivity.this, "Single Click Item Pos: " + pos, Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setDeleteClickListener(new onDeleteClickListener() {
            @Override
            public void onDeleteOptionClick(int pos) {
                if (mData.get(pos).getSceneControlType().equals(AppConstants.SWITCH_TYPE)) {
                    initSwitches.get(pos).setFocusable(true);
                } else if (mData.get(pos).getSceneControlType().equals(AppConstants.DIMMER_TYPE)) {
                    initDimmers.get(pos).setFocusable(true);
                } else if (mData.get(pos).getSceneControlType().equals(AppConstants.MOTOR_TYPE)) {
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
                updatedMData.add(mData.get(pos));
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
                            }
                        }
                    });
                }
            }
        });

        edtSceneName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isSceneNamedChanged = true;
                isSceneSaved = false;
                currentSceneName = s.toString().trim();
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
            SaveAlertDialog saveAlertDialog = new SaveAlertDialog(SceneActivity.this);
            saveAlertDialog.show();

            saveAlertDialog.setSaveListener(new onSaveClickListener() {
                @Override
                public void onSaveClick(boolean isSave) {
                    if(isSave) {
                        if(saveScene()) {
                            finish();
                        }
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
             //   showDimmerPopup();
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
                if(sceneMainSwitch.isChecked()) {
                    new CallSceneOff().execute();
                } else {
                    new CallSceneOn().execute();
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
                            SceneItemsDataObject sceneItemsDataObject = new SceneItemsDataObject(AppConstants.SWITCH_TYPE, initSwitches.get(position).getText());
                            sceneItemsDataObject.setSceneItemId(initSwitches.get(position).getSwitchId());
                            sceneItemsDataObject.setMachineIP(DBConstants.MACHINE1_IP);
                            sceneItemsDataObject.setMachineID("");
                            sceneItemsDataObject.setDefaultValue("00");
                            //mAdapter.add(mData.size(), sceneItemsDataObject);
                            mData.add(sceneItemsDataObject);

                            // to save this new component
                            newMData.add(sceneItemsDataObject);

                            mAdapter.notifyDataSetChanged();
                            initSwitches.get(position).setFocusable(false);
                            isSceneSaved = false;
                        } else {
                            Toast.makeText(SceneActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
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
                            mAdapter.add(mData.size(), new SceneItemsDataObject(AppConstants.DIMMER_TYPE, initDimmers.get(position).getText()));
                            initDimmers.get(position).setFocusable(false);
                        } else {
                            Toast.makeText(SceneActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
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
            switchListCursor = dbHelper.getAllSwitchComponentsForAMachine(DBConstants.MACHINE1_IP);
            dbHelper.close();

            totalNoOfSwitches = switchListCursor.getCount();

            if (switchListCursor != null) {
                switchListCursor.moveToFirst();
                if (switchListCursor.getCount() > 0) {
                    do {
                        SceneSwitchItem sceneSwitchItem = new SceneSwitchItem(SceneActivity.this);
                        sceneSwitchItem.setText(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME)));
                        sceneSwitchItem.setSwitchId(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID)));

                        for(int i=0; i<mData.size(); i++) {
                            if(mData.get(i).getSceneItemId().equals(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID)))) {
                                sceneSwitchItem.setFocusable(false);
                            }
                        }

                        initSwitches.add(sceneSwitchItem);
                    } while (switchListCursor.moveToNext());
                }
            }
        } catch (SQLException e) {
            Log.e("SQLEXP", e.toString());
        }
    }

    private void initMotors() {
        for(int i=0; i < totalNoOfMotors; i++) {
            SceneMotorItem sceneSwitchItem = new SceneMotorItem(SceneActivity.this);
            sceneSwitchItem.setText("Motor " + i);
            initMotors.add(sceneSwitchItem);
        }
    }

    private void initDimmers() {
        for(int i=0; i < totalNoOfMotors; i++) {
            SceneDimmerItem sceneSwitchItem = new SceneDimmerItem(SceneActivity.this);
            sceneSwitchItem.setText("Dimmer " + i);
            initDimmers.add(sceneSwitchItem);
        }
    }

    private void showSceneSavedState() {
        // show scene saved state
        edtSceneName.setText(currentSceneName);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.openDataBase();
            Cursor switchListCursor = dbHelper.getAllSwitchComponentsInAScene(currentSceneId);
            mData.clear();
            if (switchListCursor != null) {
                switchListCursor.moveToFirst();
                if (switchListCursor.getCount() > 0) {
                    do {
                        String componentId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_COMPONENT_ID));
                        String defaultValue = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_DEFAULT));

                        SceneItemsDataObject sceneItemsDataObject = new SceneItemsDataObject();
                        sceneItemsDataObject.setMachineIP(DBConstants.MACHINE1_IP);
                        sceneItemsDataObject.setSceneControlType(AppConstants.SWITCH_TYPE);
                        sceneItemsDataObject.setSceneItemId(componentId);

                        String componentName = dbHelper.getAllComponentsById(componentId);
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
            Toast.makeText(SceneActivity.this, "Scene Saved", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Log.e("SQLEXP", e.toString());
        }
        return true;
    }

    public class CallSceneOn extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //setting default wal
                for(int i=0;i<mData.size();i++) {
                    String strPosition;
                    if(Integer.parseInt(mData.get(i).getSceneItemId()) < 10) {
                        strPosition = "0"+mData.get(i).getSceneItemId();
                    } else {
                        strPosition = mData.get(i).getSceneItemId();
                    }

                    String SET_STATUS_URL = "";
                    if(mData.get(i).getDefaultValue().equals("00")) {
                        SET_STATUS_URL = AppConstants.URL_MACHINE_IP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + "00";
                    } else {
                        SET_STATUS_URL = AppConstants.URL_MACHINE_IP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + "01";
                    }

                    URL urlValue = new URL(SET_STATUS_URL);
                    Log.e("# urlValue", urlValue.toString());

                    HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                    httpUrlConnection.setRequestMethod("GET");
                    InputStream inputStream = httpUrlConnection.getInputStream();
                }

            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
            }catch(Exception e){
            }
        }
    }

    public class CallSceneOff extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //setting default wal
                for(int i=0;i<mData.size();i++) {
                    String strPosition;
                    if(Integer.parseInt(mData.get(i).getSceneItemId()) < 10) {
                        strPosition = "0"+mData.get(i).getSceneItemId();
                    } else {
                        strPosition = mData.get(i).getSceneItemId();
                    }

                    String SET_STATUS_URL = AppConstants.URL_MACHINE_IP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + "00";

                    URL urlValue = new URL(SET_STATUS_URL);
                    Log.e("# urlValue", urlValue.toString());

                    HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                    httpUrlConnection.setRequestMethod("GET");
                    InputStream inputStream = httpUrlConnection.getInputStream();
                }

            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
            }catch(Exception e){
            }
        }
    }
}
