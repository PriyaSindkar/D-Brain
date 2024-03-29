package com.webmyne.android.d_brain.ui.Fragments;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.SensorListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
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


public class SensorFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SensorListCursorAdapter adapter;
    private int totalNoOfSensors = 9;
    private TextView txtEmptyView, txtEmptyView1;
    private LinearLayout emptyView;
    private Cursor sensorListCursor, machineListCursor;
    private ArrayList<XMLValues> sensorStatusList, allSensorsStatusList;
    private String[] machineIPs;
    private boolean isFirstTime = true;
    private ProgressBar progressBar;
    private Timer timer;
    private Handler handler;
    public static boolean isDelay = false;
    private ImageView imgEmpty;

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
                            new GetSensorStatus().execute();
                        } else {
                            PauseTimer();
                            ResumeTimer();
                        }
                    }
                });

            }
        }, 0, 4000 * 1);

    }

    public static SensorFragment newInstance() {
        SensorFragment fragment = new SensorFragment();

        return fragment;
    }

    public SensorFragment() {
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
        View view = inflater.inflate(R.layout.fragment_scene_list, container, false);
        init(view);

        return view;
    }

    private void init(View view) {
        ((HomeDrawerActivity) getActivity()).setTitle("Sensors");
        ((HomeDrawerActivity) getActivity()).hideAppBarButton();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyView = (LinearLayout) view.findViewById(R.id.emptyView);
        txtEmptyView1 = (TextView) view.findViewById(R.id.txtEmptyView1);
        txtEmptyView = (TextView) view.findViewById(R.id.txtEmptyView);
        imgEmpty = (ImageView) view.findViewById(R.id.imgEmpty);

        initArrayOfSensors();

        if(sensorListCursor.getCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            imgEmpty.setVisibility(View.VISIBLE);
            imgEmpty.setImageResource(R.drawable.drawer_sensors);
            txtEmptyView.setText(getResources().getString(R.string.empty_sensors_list));
        } else {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), getActivity());
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        // fetch sensor status periodically
        ResumeTimer();

        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer.cancel();
    }

    private void initArrayOfSensors() {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        //insert switches in adapter ofr machine-1
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

    public class GetSensorStatus extends AsyncTask<String, Void, Void> {
        boolean isError = false, isMachineActive = false;
        String machineId="", machineName = "", machineIp;
        Cursor cursor, machineCursor;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {

            allSensorsStatusList = new ArrayList<>();
            for(int i=0; i<params.length; i++) {
                String machineBaseURL = "";
                machineIp = machineIPs[i];

                if(machineIp.startsWith("http://")) {
                    machineBaseURL = machineIp;
                } else {
                    machineBaseURL = "http://" + machineIp;
                }

                try {
                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
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
                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
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
                        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                        dbHelper.openDataBase();
                        dbHelper.enableDisableMachine(machineId, false);
                        dbHelper.close();
                        Toast.makeText(getActivity(), "Machine, " + machineName + " was deactivated.", Toast.LENGTH_LONG).show();
                    } catch (SQLException e) {
                        Log.e("TAG EXP", e.toString());
                    }
                }

                if(isFirstTime) {
                    //init adapter
                    adapter = new SensorListCursorAdapter(getActivity(), sensorListCursor, sensorStatusList);
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

                        RenameDialog renameDialog = new RenameDialog(getActivity(), _oldName, _oldDetails);
                        renameDialog.show();

                        renameDialog.setRenameListener(new onRenameClickListener() {
                            @Override
                            public void onRenameOptionClick(int pos, String oldName) {

                            }

                            @Override
                            public void onRenameOptionClick(int pos, String newName, String newDetails) {
                                try {
                                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
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
