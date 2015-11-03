package com.webmyne.android.d_brain.ui.Activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.SensorListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
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


public class SensorsListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private SensorListCursorAdapter adapter;
    private Toolbar toolbar;
    private ImageView imgBack, imgListGridToggle;
    private TextView toolbarTitle;
    private Cursor sensorListCursor, machineListCursor;
    private ArrayList<XMLValues> sensorStatusList, allSensorsStatusList;
    private String[] machineIPs;
    private boolean isFirstTime = true;
    private ProgressBar progressBar;
    private Timer timer;
    private Handler handler;
    public static boolean isDelay = false;

    private void PauseTimer(){
        this.timer.cancel();
    }

    public void ResumeTimer() {
        handler = new Handler();
        timer = new Timer();


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isDelay) {
                            new GetSensorStatus().execute(machineIPs);
                        } else {
                            PauseTimer();
                            ResumeTimer();
                        }
                    }
                });

            }
        }, 0, 4000 * 1);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgListGridToggle = (ImageView) findViewById(R.id.imgListGridToggle);
        imgListGridToggle.setVisibility(View.GONE);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Sensors");

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(layoutManager);
        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), SensorsListActivity.this);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        initArrayOfSensors();

        // fetch sensor status periodically
        ResumeTimer();

        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                finish();
            }
        });

    }


    private void initArrayOfSensors() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //insert sensors in adapter from all machines
        try {
            dbHelper.openDataBase();
            sensorListCursor =  dbHelper.getAllSensorsComponents();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();
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

    public class GetSensorStatus extends AsyncTask<String, Void, Void> {
        boolean isError = false, isMachineActive = false;
        String machineId="", machineName = "", machineIp;
        Cursor cursor, machineCursor;

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(String... params) {

                allSensorsStatusList = new ArrayList<>();
                for(int i=0; i<machineIPs.length; i++) {
                    String machineBaseURL = "";
                    machineIp = machineIPs[i];

                    if(machineIp.startsWith("http://")) {
                        machineBaseURL = machineIp;
                    } else {
                        machineBaseURL = "http://" + machineIp;
                    }

                    try {
                        DatabaseHelper dbHelper = new DatabaseHelper(SensorsListActivity.this);
                        try {
                            dbHelper.openDataBase();
                            isMachineActive =  dbHelper.isMachineActive(machineIp);
                            machineCursor = dbHelper.getMachineByIP(machineIp);
                            machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                            machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));
                            cursor = dbHelper.getAllSensorComponentsForAMachine(machineIp);

                            dbHelper.close();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        // fetch sensor status from machine only if the machine is active else init all the sensor status to off
                        if (isMachineActive) {
                            URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_SENSOR_STATUS);
                             Log.e("# urlValue", urlValue.toString());

                            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                            httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                            httpUrlConnection.setRequestMethod("GET");
                            InputStream inputStream = httpUrlConnection.getInputStream();
                            //  Log.e("# inputStream", inputStream.toString());
                            MainXmlPullParser pullParser = new MainXmlPullParser();

                            sensorStatusList = pullParser.processXML(inputStream);
                            allSensorsStatusList.addAll(sensorStatusList);
                            // Log.e("XML PARSERED", dimmerStatusList.toString());
                        } else {
                            if (cursor != null) {
                                cursor.moveToFirst();
                                if (cursor.getCount() > 0) {
                                    do {
                                        String componnetId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                                        XMLValues values = new XMLValues();
                                        values.tagName = componnetId;
                                        values.tagValue = AppConstants.OFF_VALUE;
                                        allSensorsStatusList.add(values);

                                    } while (cursor.moveToNext());
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("#EXP", e.toString());
                        isError = true;
                        DatabaseHelper dbHelper = new DatabaseHelper(SensorsListActivity.this);
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
                                        values.tagValue = AppConstants.OFF_VALUE;
                                        allSensorsStatusList.add(values);

                                    } while (cursor.moveToNext());
                                }
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Log.e("TAG_ASYNC", "Inside onPostExecute");
            try {
                progressBar.setVisibility(View.GONE);
                if(isError) {
                    try {
                        DatabaseHelper dbHelper = new DatabaseHelper(SensorsListActivity.this);
                        dbHelper.openDataBase();
                        dbHelper.enableDisableMachine(machineId, false);
                        dbHelper.close();
                        Toast.makeText(SensorsListActivity.this, "Machine, " + machineName + " was deactivated.", Toast.LENGTH_LONG).show();
                    } catch (SQLException e) {
                        Log.e("TAG EXP", e.toString());
                    }
                }

                if(isFirstTime) {
                    //init adapter
                    adapter = new SensorListCursorAdapter(SensorsListActivity.this, sensorListCursor, sensorStatusList);
                    adapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    isFirstTime = false;
                } else {
                    //set adapter again
                    adapter.setSensorStatus(sensorStatusList);
                    adapter.notifyDataSetChanged();
                }
                adapter.setRenameClickListener(new onRenameClickListener() {

                    @Override
                    public void onRenameOptionClick(int pos, String _oldName) {

                    }

                    @Override
                    public void onRenameOptionClick(int pos, String _oldName, String _oldDetails) {
                        final int position = pos;
                        sensorListCursor.moveToPosition(position);
                        final String componentId = sensorListCursor.getString(sensorListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));

                        RenameDialog renameDialog = new RenameDialog(SensorsListActivity.this, _oldName, _oldDetails);
                        renameDialog.show();

                        renameDialog.setRenameListener(new onRenameClickListener() {
                            @Override
                            public void onRenameOptionClick(int pos, String oldName) {

                            }

                            @Override
                            public void onRenameOptionClick(int pos, String newName, String newDetails) {
                                try {
                                    DatabaseHelper dbHelper = new DatabaseHelper(SensorsListActivity.this);
                                    dbHelper.openDataBase();
                                    dbHelper.renameComponent(componentId, newName, newDetails);
                                    sensorListCursor =  dbHelper.getAllSensorsComponents();
                                    dbHelper.close();
                                    adapter.changeCursor(sensorListCursor);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

            } catch (Exception e) {
            }
        }
    }

}
