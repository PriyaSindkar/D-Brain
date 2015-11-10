package com.webmyne.android.d_brain.ui.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.SwitchListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.AddToSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.EditSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.LongPressOptionsDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.MachineInactiveDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.MachineNotActiveDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.SaveAlertDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.SceneListDialog;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.Model.SchedulerModel;
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

import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class SwitchesListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private SwitchListCursorAdapter adapter;
    private Toolbar toolbar;
    private ImageView imgBack, imgListGridToggle;
    private TextView toolbarTitle;

    private boolean isListView = true, isFirstTime = true;
    private Cursor switchListCursor, machineListCursor;
    private String[] machineIPs;
    ArrayList<XMLValues> switchStatusList, allSwitchesStatusList;
    private InputStream inputStream;
    private ProgressBar progressBar;
    private Timer timer1;
    private Handler handler,handler1;
    private int timeOutErrorCount = 0;
    private boolean isServiceRunning = false;

    public void startTherad(){
        Log.e("$#$ THREAD", "START");
        handler1 = new Handler();
        timer1 = new Timer();

        timer1.scheduleAtFixedRate(new TimerTask() {


            @Override
            public void run() {
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        new GetSwitchStatus().execute(machineIPs);
                    }
                });
            }
        }, 0, 3000 * 1);


    }

    public void stopTherad(){
        Log.e("$#$ THREAD", "STOP");
        try {
            timer1.cancel();
            timer1.purge();
        }catch (Exception e){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgListGridToggle = (ImageView) findViewById(R.id.imgListGridToggle);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Switches");


        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), SwitchesListActivity.this);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setSupportsChangeAnimations(false);
        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(0);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTherad();
                finish();
            }
        });


        imgListGridToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListView) {
                    isListView = false;
                    imgListGridToggle.setImageResource(R.drawable.ic_list_view);
                    android.support.v7.widget.GridLayoutManager layoutManager = new android.support.v7.widget.GridLayoutManager(SwitchesListActivity.this, 3);
                    layoutManager.supportsPredictiveItemAnimations();
                    mRecyclerView.setLayoutManager(layoutManager);
                    adapter.setType(1);
                    adapter.notifyDataSetChanged();

                } else {
                    isListView = true;
                    imgListGridToggle.setImageResource(R.drawable.ic_grid_view);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(SwitchesListActivity.this));
                    adapter.setType(0);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConstants.getCurrentSsid(SwitchesListActivity.this);
        initArrayOfSwitches();
        startTherad();

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
    public void onBackPressed() {
        super.onBackPressed();
        stopTherad();
    }

    private void initArrayOfSwitches() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //insert switches in adapter ofr machine-1
        try {
            dbHelper.openDataBase();
           // switchListCursor =  dbHelper.getAllSwitchComponentsForAMachine(DashboardFragment.MACHINE_IP);
            switchListCursor =  dbHelper.getAllSwitchComponents();
            machineListCursor = dbHelper.getAllMachines();

            if(machineListCursor != null) {
                if(machineListCursor.getCount() > 0) {
                    machineIPs = new String[machineListCursor.getCount()];
                    machineListCursor.moveToFirst();
                    int i = 0;
                    do {
                        String machineIP = machineListCursor.getString(machineListCursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP));
                        machineIPs[i] = machineIP;
                        i++;
                    } while(machineListCursor.moveToNext());
                }
            }
            dbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public class GetSwitchStatus extends AsyncTask<String, Void, Void> {
        boolean  isMachineActive = false;
        String machineId="", machineName = "", machineIp;
        Cursor cursor, machineCursor;
        private ProgressDialog progress_dialog1;

        @Override
            protected void onPreExecute() {

                isServiceRunning = true;

                progress_dialog1 = new ProgressDialog(SwitchesListActivity.this);
                progress_dialog1.setCancelable(true);
                progress_dialog1.setMessage("Please Wait..");
               //    progress_dialog1.show();
                setProgressBarIndeterminateVisibility(true);
            }

        @Override
        protected Void doInBackground(String... machineIps) {
                allSwitchesStatusList = new ArrayList<>();

                for(int i=0; i<machineIps.length; i++) {

                    String machineBaseURL = "";
                    machineIp = machineIps[i];
                    if(machineIp.startsWith("http://")) {
                        machineBaseURL = machineIp;
                    } else {
                        machineBaseURL = "http://" + machineIp;
                    }

                    try {
                        DatabaseHelper dbHelper = new DatabaseHelper(SwitchesListActivity.this);
                        dbHelper.openDataBase();

                        isMachineActive =  dbHelper.isMachineActive(machineIp);
                        machineCursor = dbHelper.getMachineByIP(machineIp);
                        machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                        machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));

                        cursor = dbHelper.getAllSwitchComponentsForAMachine(machineIp);

                        dbHelper.close();


                    // fetch switch status from machine only if the machine is active else init all the switch status to off
                    if (isMachineActive) {
                        Log.e("#### Switch url",""+machineBaseURL+"ACTIVE");

                        URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_SWITCH_STATUS);

                        HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();

                        httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);

                        httpUrlConnection.setRequestMethod("GET");
                        InputStream inputStream = null;
                        httpUrlConnection.setDoInput(true);
                        inputStream = httpUrlConnection.getInputStream();
                        MainXmlPullParser pullParser = new MainXmlPullParser();
                        switchStatusList = pullParser.processXML(inputStream);

                        int totalSwitchesofMachine = 0;
                        if (cursor != null) {
                            totalSwitchesofMachine = cursor.getCount();
                        }

                        for (int j = 0; j < totalSwitchesofMachine; j++) {
                            allSwitchesStatusList.add(switchStatusList.get(j));
                        }
                    } else {
                        Log.e("#### Switch url",""+machineBaseURL+"DISABLE");

                        if (cursor != null) {
                            cursor.moveToFirst();
                            if (cursor.getCount() > 0) {
                                do {
                                    String componnetId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                                    XMLValues values = new XMLValues();
                                    values.tagName = componnetId;
                                    values.tagValue = AppConstants.OFF_VALUE;
                                    allSwitchesStatusList.add(values);

                                } while (cursor.moveToNext());
                            }
                        }
                    }
                    } catch (Exception ex1) {
                        Log.e("#EXP switch", ex1.toString());
                        if (cursor != null) {
                            cursor.moveToFirst();
                            if(cursor.getCount() > 0) {
                                do {
                                    String componnetId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                                    XMLValues values = new XMLValues();
                                    values.tagName = componnetId;
                                    values.tagValue = AppConstants.OFF_VALUE;
                                    allSwitchesStatusList.add(values);

                                } while (cursor.moveToNext());
                            }
                        }
                    }
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progress_dialog1.dismiss();
            isServiceRunning = false;

            try {
                progressBar.setVisibility(View.GONE);
                if(isFirstTime) {
                    //init adapter
                    adapter = new SwitchListCursorAdapter(SwitchesListActivity.this, switchListCursor, allSwitchesStatusList);
                    adapter.setType(0);
                    adapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    isFirstTime = false;
                } else {
                    if(allSwitchesStatusList.size()>0) {
                        //set adapter again
                        adapter.setSwitchStatus(allSwitchesStatusList);
                        adapter.notifyDataSetChanged();
                    }else{
                        Log.e("size zero",""+allSwitchesStatusList.size());
                    }
                }

                adapter.setCheckedChangeListener(new onCheckedChangeListener() {
                    @Override
                    public void onCheckedChangeClick(int pos) {
                          //  if(isServiceRunning){
                                stopTherad();
                             //   startTherad();
                           // }else {
                                startTherad();
                          //  }
                    }

                    @Override
                    public void onCheckedPreChangeClick(int pos) {
                        stopTherad();
                    }
                });

                adapter.setRenameClickListener(new onRenameClickListener() {

                    @Override
                    public void onRenameOptionClick(int pos, String _oldName) {
                        renameComponent(pos);
                    }

                    @Override
                    public void onRenameOptionClick(int pos, String oldName, String oldDetails) {

                    }
                });

                adapter.setAddToSceneClickListener(new onAddToSceneClickListener() {
                    @Override
                    public void onAddToSceneOptionClick(int pos) {
                        addComponentToScene(pos);
                    }
                });

                adapter.setFavoriteClickListener(new onFavoriteClickListener() {
                    @Override
                    public void onFavoriteOptionClick(int pos) {
                        addComponentToFavourite(pos);
                    }
                });

                adapter.setLongClickListener(new onLongClickListener() {

                    @Override
                    public void onLongClick(final int pos, View view) {

                        LongPressOptionsDialog longPressOptionsDialog = new LongPressOptionsDialog(SwitchesListActivity.this, pos);
                        longPressOptionsDialog.show();

                        longPressOptionsDialog.setRenameClickListener(new onRenameClickListener() {
                            @Override
                            public void onRenameOptionClick(int pos, String oldName) {
                                renameComponent(pos);
                            }

                            @Override
                            public void onRenameOptionClick(int pos, String oldName, String oldDetails) {

                            }
                        });

                        longPressOptionsDialog.setFavoriteClickListener(new onFavoriteClickListener() {
                            @Override
                            public void onFavoriteOptionClick(int pos) {
                                addComponentToFavourite(pos);
                            }
                        });

                        longPressOptionsDialog.setAddToSceneClickListener(new onAddToSceneClickListener() {
                            @Override
                            public void onAddToSceneOptionClick(int pos) {
                                addComponentToScene(pos);
                            }
                        });

                        longPressOptionsDialog.setAddSchedulerClickListener(new onAddSchedulerClickListener() {
                            @Override
                            public void onAddSchedulerOptionClick(int pos) {
                                addComponentToScheduler(pos);
                            }
                        });
                    }
                });

                adapter.setAddSchedulerClickListener(new onAddSchedulerClickListener() {

                    @Override
                    public void onAddSchedulerOptionClick(int pos) {
                        addComponentToScheduler(pos);
                    }
                });


            } catch (Exception e) {
                Log.e("EXc post", e.toString());

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void addComponentToFavourite(int pos) {
        switchListCursor.moveToPosition(pos);
        String componentId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
        String componentPrimaryId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        String componentName = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
        String componentType = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
        String componentMachineID = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID));
        String machineIP = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP));
        String machineName = "";
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(SwitchesListActivity.this);
            dbHelper.openDataBase();
            machineName = dbHelper.getMachineNameByIP(machineIP);
            int switchCount = dbHelper.getComponentTypeCountInFavourite(componentType);

            if (switchCount < 10) {
                boolean isAlreadyAFavourite = dbHelper.insertIntoFavorite(componentPrimaryId, componentId, componentName, componentType, componentMachineID,machineIP, machineName);
                dbHelper.close();

                if (isAlreadyAFavourite) {Toast.makeText(SwitchesListActivity.this, "Already added to Favorite.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SwitchesListActivity.this, "Added to Favorite Successfully.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SwitchesListActivity.this, "Cannot add more than 10 switches to favourites.", Toast.LENGTH_SHORT).show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void renameComponent(int pos) {
        final int position = pos;
        switchListCursor.moveToPosition(position);
        final String componentId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        final String componentName = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));


        RenameDialog renameDialog = new RenameDialog(SwitchesListActivity.this, componentName);
        renameDialog.show();

        renameDialog.setRenameListener(new onRenameClickListener() {
            @Override
            public void onRenameOptionClick(int pos, String newName) {
                try {
                    DatabaseHelper dbHelper = new DatabaseHelper(SwitchesListActivity.this);
                    dbHelper.openDataBase();
                    dbHelper.renameComponent(componentId, newName);
                    switchListCursor = dbHelper.getAllSwitchComponents();
                    dbHelper.close();
                    adapter.changeCursor(switchListCursor);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRenameOptionClick(int pos, String newName, String newDetails) {

            }
        });
    }

    private void addComponentToScene(int pos) {


           stopTherad();

        switchListCursor.moveToPosition(pos);
        String componentId1 = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        String componentType = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
        SceneListDialog dialog = new SceneListDialog(SwitchesListActivity.this, componentId1, componentType);
        dialog.show();
    }

    private void addComponentToScheduler(int pos) {
        switchListCursor.moveToPosition(pos);
        String componentPrimaryId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        String componentId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
        String componentType = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
        String componentName = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
        String componentMachineName = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MNAME));
        String componentMachineID = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID));
        String componentMachineIP = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP));

        try {
            DatabaseHelper dbHelper = new DatabaseHelper(SwitchesListActivity.this);
            dbHelper.openDataBase();
            boolean isAlreadyScheduled = dbHelper.isAlreadyScheduled(componentPrimaryId, componentMachineID);
            dbHelper.close();

            if(! isAlreadyScheduled) {
                SchedulerModel schedulerModel = new SchedulerModel("", componentPrimaryId, componentId,componentName, componentType, componentMachineID,componentMachineIP, componentMachineName, false, "00");

                AddToSchedulerDialog addToSchedulerDialog = new AddToSchedulerDialog(SwitchesListActivity.this, schedulerModel);
                addToSchedulerDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                addToSchedulerDialog.show();
            } else {
                dbHelper = new DatabaseHelper(this);
                SchedulerModel schedulerModel = null;
                try {
                    dbHelper.openDataBase();
                    schedulerModel =  dbHelper.getSchedulerByComponentId(componentPrimaryId, componentMachineID, "0");
                    dbHelper.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if(schedulerModel != null) {
                    EditSchedulerDialog addToSchedulerDialog = new EditSchedulerDialog(SwitchesListActivity.this, schedulerModel);
                    addToSchedulerDialog.show();

                    addToSchedulerDialog.setOnSingleClick(new onSingleClickListener() {
                        @Override
                        public void onSingleClick(int pos) {

                        }
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public class GetMachineStatus extends AsyncTask<String, Void, Void> {
        String machineId="", machineName = "", machineIp, isMachineActive = "false";
        Cursor machineCursor;
        int totalMachineCount = machineIPs.length;
        ProgressDialog progress_dialog;

        @Override
        protected void onPreExecute() {
            progress_dialog = new ProgressDialog(SwitchesListActivity.this);
            progress_dialog.setCancelable(true);
            progress_dialog.setMessage("Please Wait..");
            progress_dialog.show();
        }

        @Override
        protected Void doInBackground(String... MachineIp) {

            for (int i = 0; i < MachineIp.length; i++) {
                String machineBaseURL = "";
                machineIp = MachineIp[i];
                int position = i; // save to continue calling debt of the same machine until errorCount > 10

                if (machineIp.startsWith("http://")) {
                    machineBaseURL = machineIp;
                } else {
                    machineBaseURL = "http://" + machineIp;
                }

                DatabaseHelper dbHelper = new DatabaseHelper(SwitchesListActivity.this);
                try {
                    dbHelper.openDataBase();
                    machineCursor = dbHelper.getMachineByIP(machineIp);
                    machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                    machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));
                    isMachineActive = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));
                    dbHelper.close();

                    if(isMachineActive.equals("true")) {
                        URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_MACHINE_STATUS);
                        Log.e("# machine url", urlValue.toString());

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
                    Log.e("~~~~~~~~TIME OUT~~~", e.toString());
                    timeOutErrorCount++;
                    Log.e("# timeOutErrorCount", timeOutErrorCount+"");

                    if (timeOutErrorCount > 10) {
                        timeOutErrorCount = 0;
                        totalMachineCount--;
                        try {
                            dbHelper.openDataBase();
                            dbHelper.enableDisableMachine(machineId, false);
                            dbHelper.close();
                        } catch (SQLException ex) {
                            Log.e("TAG EXP TIME OUT", ex.toString());
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

            progress_dialog.dismiss();

            SharedPreferences settings = getSharedPreferences("MACHINE_STATUS", 0);
            int count = settings.getInt("ACTIVE_MACHINE", machineIPs.length);

            Log.e("### Total machine count", "" + totalMachineCount);
            Log.e("### PREFS count", "" + count);

            if(totalMachineCount == 0) {
                Toast.makeText(SwitchesListActivity.this, getString(R.string.all_machines_off_text), Toast.LENGTH_LONG).show();
                stopTherad();
                finish();
            } else if (totalMachineCount < machineIPs.length) {
                if (count != totalMachineCount) {
                    settings = getSharedPreferences("MACHINE_STATUS", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("ACTIVE_MACHINE", totalMachineCount);
                    editor.commit();

                    MachineInactiveDialog machineNotActiveDialog = new MachineInactiveDialog(SwitchesListActivity.this, "One of your machines deactivated.");
                    machineNotActiveDialog.show();
                    machineNotActiveDialog.setSaveListener(new onSaveClickListener() {
                        @Override
                        public void onSaveClick(boolean isSave) {
                        stopTherad();
                        finish();
                        }
                    });
                    //Toast.makeText(SwitchesListActivity.this, "One of your machines deactivated.", Toast.LENGTH_LONG).show();
                }else{
                    settings = getSharedPreferences("MACHINE_STATUS", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("ACTIVE_MACHINE", totalMachineCount);
                    editor.commit();
                    new GetSwitchStatus().execute(machineIPs);
                }
            }
            else {
                settings = getSharedPreferences("MACHINE_STATUS", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("ACTIVE_MACHINE", totalMachineCount);
                editor.commit();

                new GetSwitchStatus().execute(machineIPs);

            }
        }
    }


}
