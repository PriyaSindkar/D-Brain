package com.webmyne.android.d_brain.ui.Activities;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.DimmerListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.AddToSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.LongPressOptionsDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.MachineInactiveDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
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
import com.webmyne.android.d_brain.ui.Model.SchedulerModel;
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

public class DimmerListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtTitle;
    private ImageView imgBack, imgListGridToggle;
    private RecyclerView mRecyclerView;
    private DimmerListCursorAdapter adapter;
    private Cursor dimmerListCursor, machineListCursor;
    ArrayList<XMLValues> dimmerStatusList, allDimmerStatusList;
    private String[] machineIPs;

    private ProgressBar progressBar;
    private int totalNoOfDimmers = 77;
    private boolean isListView = true, isFirstTime = true;


    private Timer timer1;
    private Handler handler1;

    public void startTherad(){
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

    public void stopTherad(){
        try {
            timer1.cancel();
        }catch (Exception e){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initArrayOfDimmers();

        startTherad();

    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            stopTherad();
        }catch (Exception e){

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            stopTherad();
        }catch (Exception e){

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try{
            stopTherad();
        }catch (Exception e){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dimmer);

        init();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);



        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //layoutManager.supportsPredictiveItemAnimations();
        mRecyclerView.setLayoutManager(layoutManager);
        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), DimmerListActivity.this);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);



        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);

        imgListGridToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListView) {
                    isListView = false;
                    imgListGridToggle.setImageResource(R.drawable.ic_list_view);
                    android.support.v7.widget.GridLayoutManager layoutManager = new android.support.v7.widget.GridLayoutManager(DimmerListActivity.this, 2);
                    layoutManager.supportsPredictiveItemAnimations();
                    mRecyclerView.setLayoutManager(layoutManager);
                    adapter.setType(1);
                    adapter.notifyDataSetChanged();

                } else {
                    isListView = true;
                    imgListGridToggle.setImageResource(R.drawable.ic_grid_view);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(DimmerListActivity.this));
                    adapter.setType(0);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTherad();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       stopTherad();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgListGridToggle = (ImageView) findViewById(R.id.imgListGridToggle);

        txtTitle = (TextView) findViewById(R.id.toolbarTitle);
        txtTitle.setText("Dimmers");

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    private void initArrayOfDimmers() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //insert dimmers in adapter for machine-1
        try {
            dbHelper.openDataBase();
            dimmerListCursor =  dbHelper.getAllDimmerComponents();
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

    public class GetDimmerStatus extends AsyncTask<String, Void, Void> {
        boolean isError = false;
        String machineId="", machineName = "", machineIp, isMachineActive = "false";
        Cursor cursor, machineCursor;

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(String... params) {

                allDimmerStatusList = new ArrayList<>();

                for(int i=0; i<machineIPs.length; i++) {
                    String machineBaseURL = "";
                    machineIp = machineIPs[i];

                    if(machineIp.startsWith("http://")) {
                        machineBaseURL = machineIp;
                    } else {
                        machineBaseURL = "http://" + machineIp;
                    }

                    DatabaseHelper dbHelper = new DatabaseHelper(DimmerListActivity.this);
                    try {
                        dbHelper.openDataBase();
                        machineCursor = dbHelper.getMachineByIP(machineIp);
                        machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                        machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));
                        isMachineActive = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));
                        cursor = dbHelper.getAllDimmerComponentsForAMachine(machineIp);
                        dbHelper.close();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    // fetch dimmer status from machine only if the machine is active else init all the dimmer status to off
                    if (isMachineActive.equals("true")) {
                        try {
                            URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_DIMMER_STATUS);
                            Log.e("# urlValue", urlValue.toString());

                            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                            httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                            httpUrlConnection.setRequestMethod("GET");
                            InputStream inputStream = httpUrlConnection.getInputStream();
                            //  Log.e("# inputStream", inputStream.toString());
                            MainXmlPullParser pullParser = new MainXmlPullParser();

                            dimmerStatusList = pullParser.processXML(inputStream);
                            Log.e("XML PARSERED", dimmerStatusList.toString());

                            int totalDimmersofMachine = 0;
                            if (cursor != null) {
                                totalDimmersofMachine = cursor.getCount();
                            }

                            for (int j = 0; j < totalDimmersofMachine; j++) {
                                allDimmerStatusList.add(dimmerStatusList.get(j));
                            }
                        } catch (Exception e) {
                            Log.e("# EXP", e.toString());
                            isError = true;
                            try {
                                dbHelper.openDataBase();
                                dbHelper.enableDisableMachine(machineId, false);
                                dbHelper.close();

                                if (cursor != null) {
                                    cursor.moveToFirst();
                                    if(cursor.getCount() > 0) {
                                        do {
                                            String componnetId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                                            XMLValues values = new XMLValues();
                                            values.tagName = componnetId;
                                            values.tagValue = AppConstants.OFF_VALUE + AppConstants.OFF_VALUE;
                                            allDimmerStatusList.add(values);

                                        } while (cursor.moveToNext());
                                    }
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        if (cursor != null) {
                            cursor.moveToFirst();
                            if (cursor.getCount() > 0) {
                                do {
                                    String componnetId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                                    XMLValues values = new XMLValues();
                                    values.tagName = componnetId;
                                    values.tagValue = AppConstants.OFF_VALUE + AppConstants.OFF_VALUE;
                                    allDimmerStatusList.add(values);

                                } while (cursor.moveToNext());
                            }
                        }
                    }
                }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                progressBar.setVisibility(View.GONE);


                if(isFirstTime) {
                    //init adapter
                    adapter = new DimmerListCursorAdapter(DimmerListActivity.this, dimmerListCursor, allDimmerStatusList);
                    adapter.setType(0);
                    adapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    isFirstTime = false;
                } else {
                    //set adapter again
                    adapter.setDimmerStatus(allDimmerStatusList);
                    adapter.notifyDataSetChanged();
                }

                adapter.setCheckedChangeListener(new onCheckedChangeListener() {
                    @Override
                    public void onCheckedChangeClick(int pos) {
                           startTherad();
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

                adapter.setAddSchedulerClickListener(new onAddSchedulerClickListener() {
                    @Override
                    public void onAddSchedulerOptionClick(int pos) {
                        addComponentToScheduler(pos);
                    }
                });

                adapter.setLongClickListener(new onLongClickListener() {

                    @Override
                    public void onLongClick(final int pos, View view) {

                        LongPressOptionsDialog longPressOptionsDialog = new LongPressOptionsDialog(DimmerListActivity.this, pos);
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

               // new GetMachineStatus().execute();

            } catch (Exception e) {
            }
        }
    }


    public class GetMachineStatus extends AsyncTask<String, Void, Void> {
        String machineId="", machineName = "", machineIp, isMachineActive = "false";
        boolean isError = false;
        Cursor machineCursor;
        int   totalMachineCount = 0;
        @Override
        protected void onPreExecute() {
            //progressDilaog.show();
        }

        @Override
        protected Void doInBackground(String... machineIps) {

            for (int i = 0; i < machineIps.length; i++) {
                String machineBaseURL = "";
                machineIp = machineIps[i];

                if (machineIp.startsWith("http://")) {
                    machineBaseURL = machineIp;
                } else {
                    machineBaseURL = "http://" + machineIp;
                }
                DatabaseHelper dbHelper = new DatabaseHelper(DimmerListActivity.this);
                try {
                    dbHelper.openDataBase();
                    Log.e("MACHIEN_IP", machineIp);
                    machineCursor = dbHelper.getMachineByIP(machineIp);
                    machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                    machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));
                    isMachineActive = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));
                    dbHelper.close();


                    if(isMachineActive.equals("true")) {
                        Log.e("isMachineActive", isMachineActive);
                        URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_MACHINE_STATUS);
                        Log.e("# urlValue", urlValue.toString());

                        HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                        httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);

                        httpUrlConnection.setRequestMethod("GET");
                        InputStream inputStream = httpUrlConnection.getInputStream();
                        totalMachineCount++;
                    } else {
                        Log.e("isMachineActive", isMachineActive);
                    }

                } catch (Exception e) {
                    Log.e("# EXP", e.toString());
                    isError = true;
                    try {
                        dbHelper.openDataBase();
                        dbHelper.enableDisableMachine(machineId, false);
                        dbHelper.close();
                    } catch (SQLException ex) {
                        Log.e("TAG EXP", ex.toString());
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            SharedPreferences settings = getSharedPreferences("MACHINE_STATUS", 0);
            int count = settings.getInt("ACTIVE_MACHINE", machineIPs.length);

            Log.e("### Total machine count", "" + totalMachineCount);

            if(totalMachineCount == 0) {
                Toast.makeText(DimmerListActivity.this, getString(R.string.all_machines_off_text), Toast.LENGTH_LONG).show();
                stopTherad();
                finish();
            } else if (totalMachineCount < machineIPs.length) {

                if (count != totalMachineCount) {

                    settings = getSharedPreferences("MACHINE_STATUS", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("ACTIVE_MACHINE", totalMachineCount);
                    editor.commit();


                    MachineInactiveDialog machineNotActiveDialog = new MachineInactiveDialog(DimmerListActivity.this, "One of your machines deactivated.");
                    machineNotActiveDialog.show();

                    machineNotActiveDialog.setSaveListener(new onSaveClickListener() {
                        @Override
                        public void onSaveClick(boolean isSave) {
                            stopTherad();
                            finish();
                        }
                    });

                }else{

                    settings = getSharedPreferences("MACHINE_STATUS", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("ACTIVE_MACHINE", totalMachineCount);
                    editor.commit();


                    new GetDimmerStatus().execute(machineIPs);

                }


            }
            else {

                settings = getSharedPreferences("MACHINE_STATUS", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("ACTIVE_MACHINE", totalMachineCount);
                editor.commit();


                new GetDimmerStatus().execute(machineIPs);
            }

        }
    }



    private void renameComponent(int pos) {

        final int position = pos;
        dimmerListCursor.moveToPosition(position);
        final String componentId = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        final String componentName = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));

        RenameDialog renameDialog = new RenameDialog(DimmerListActivity.this, componentName);
        renameDialog.show();

        renameDialog.setRenameListener(new onRenameClickListener() {
            @Override
            public void onRenameOptionClick(int pos, String newName) {
                try {
                    DatabaseHelper dbHelper = new DatabaseHelper(DimmerListActivity.this);
                    dbHelper.openDataBase();
                    dbHelper.renameComponent(componentId, newName);
                    dimmerListCursor = dbHelper.getAllDimmerComponents();
                    dbHelper.close();
                    adapter.changeCursor(dimmerListCursor);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onRenameOptionClick(int pos, String oldName, String oldDetails) {

            }
        });
    }

    private void addComponentToScene(int pos) {
        stopTherad();
        dimmerListCursor.moveToPosition(pos);
        String componentId = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        String componentType = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
        SceneListDialog dialog = new SceneListDialog(DimmerListActivity.this, componentId, componentType);
        dialog.show();
    }

    private void addComponentToFavourite(int pos) {
        dimmerListCursor.moveToPosition(pos);
        String componentId = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
        String componentPrimaryId = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        String componentName = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
        String componentType = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
        String componentMachineID = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID));
        String machineIP = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP));
        String machineName = "";
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(DimmerListActivity.this);
            dbHelper.openDataBase();
            machineName = dbHelper.getMachineNameByIP(machineIP);
            int dimmerCount = dbHelper.getComponentTypeCountInFavourite(componentType);

            if(dimmerCount <10) {
                boolean isAlreadyAFavourite = dbHelper.insertIntoFavorite(componentPrimaryId, componentId, componentName, componentType, componentMachineID,machineIP, machineName);
                dbHelper.close();

                if (isAlreadyAFavourite) {
                    Toast.makeText(DimmerListActivity.this, "Already added to Favorite!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DimmerListActivity.this, "Added to Favorite Successfully!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DimmerListActivity.this, "Cannot add more than 10 dimmers to favourites.", Toast.LENGTH_SHORT).show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addComponentToScheduler(int pos) {
        dimmerListCursor.moveToPosition(pos);
        String componentPrimaryId = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        String componentId = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
        String componentType = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
        String componentName = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
        String componentMachineName = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MNAME));
        String componentMachineID = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID));
        String componentMachineIP = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP));

        try {
            DatabaseHelper dbHelper = new DatabaseHelper(DimmerListActivity.this);
            dbHelper.openDataBase();
            boolean isAlreadyScheduled = dbHelper.isAlreadyScheduled(componentId, componentMachineID);
            dbHelper.close();

            if(! isAlreadyScheduled) {
                SchedulerModel schedulerModel = new SchedulerModel("", componentPrimaryId, componentId,componentName, componentType, componentMachineID,componentMachineIP, componentMachineName, false, "00");

                AddToSchedulerDialog addToSchedulerDialog = new AddToSchedulerDialog(DimmerListActivity.this, schedulerModel);
                addToSchedulerDialog.show();
            } else {
                Toast.makeText(DimmerListActivity.this, "This component is already scheduled.", Toast.LENGTH_SHORT).show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
