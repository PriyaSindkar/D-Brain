package com.webmyne.android.d_brain.ui.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.SwitchListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.AddToSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.EditSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.LongPressOptionsDialog;
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
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class SwitchesListFragment extends Fragment {

    private static final String ARG_PARAM1 = "MACHINE_ID";
    private static final String ARG_PARAM2 = "MACHINE_NAME";
    private static final String ARG_PARAM3 = "IS_LISTVIEW";
    private Activity activity;
    private int mParamMachineId;
    private String mParamMachineName;
    private boolean mParamIsListView = true, isFirstTime = true, isServiceRunning = false;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private SwitchListCursorAdapter mAdapter;
    private Cursor switchListCursor, machineListCursor;
    private String[] machineIPs;
    ArrayList<XMLValues> allSwitchesStatusList;
    private int timeOutErrorCount = 0;

    private Timer timer1;
    private Handler handler1;

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
                        new GetSwitchStatus().execute();
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

    public static SwitchesListFragment newInstance(int param1, String param2, boolean isListView) {

        Log.e("TAG_FRAGMENT", "new instance " + param1);
        SwitchesListFragment fragment = new SwitchesListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putBoolean(ARG_PARAM3, isListView);
        fragment.setArguments(args);
        return fragment;
    }

    public SwitchesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParamMachineId = getArguments().getInt(ARG_PARAM1);
            mParamMachineName = getArguments().getString(ARG_PARAM2);
            mParamIsListView = getArguments().getBoolean(ARG_PARAM3);
            Log.e("TAG_FRAGMENT", "onCreate " + mParamMachineId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("TAG_FRAGMENT", "onCreateView " + mParamMachineId);
        View convertView= inflater.inflate(R.layout.fragment_switches_list, container, false);
        activity = getActivity();

        mRecyclerView = (RecyclerView) convertView.findViewById(R.id.recycler_view);

        final LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager1);

        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), getActivity());
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.getItemAnimator().setSupportsChangeAnimations(false);
        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(0);

        progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        mAdapter = new SwitchListCursorAdapter(getActivity());

        if(mParamIsListView) {
            Log.e("TAG_FRAGMENT", "if toggle view");
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAdapter.setType(0);
            mAdapter.notifyDataSetChanged();
        } else {
            Log.e("TAG_FRAGMENT", "else toggle view");
            android.support.v7.widget.GridLayoutManager layoutManager = new android.support.v7.widget.GridLayoutManager(getActivity(), 3);
            layoutManager.supportsPredictiveItemAnimations();
            mRecyclerView.setLayoutManager(layoutManager);
            mAdapter.setType(1);
            mAdapter.notifyDataSetChanged();
        }

        return convertView;
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        activity = (FragmentActivity) _activity;
    }

    @Override
    public void setUserVisibleHint(final boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible) {
            Log.e("TAG_FRAGMENT", "setUserVisibleHint " + mParamMachineId);
            AppConstants.getCurrentSsid(activity);
            initArrayOfSwitches();
            startTherad();
        } else {
            stopTherad();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG_FRAGMENT", "onResume " + mParamMachineId);
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


    private void initArrayOfSwitches() {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        //get all switches in mAdapter for machine-1
        try {
            dbHelper.openDataBase();
            switchListCursor =  dbHelper.getAllSwitchComponentsForAMachineById(String.valueOf(mParamMachineId));
            //switchListCursor =  dbHelper.getAllSwitchComponents();
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



    public class GetSwitchStatus extends AsyncTask<Void, Void, Void> {
        String machineId="", machineName = "", machineIp, isMachineActive = "false";
        Cursor cursor, machineCursor;
        private ProgressDialog progress_dialog1;
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        @Override
        protected void onPreExecute() {
            isServiceRunning = true;
            progress_dialog1 = new ProgressDialog(getActivity());
            progress_dialog1.setCancelable(true);
            progress_dialog1.setMessage("Please Wait..");
            //    progress_dialog1.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            allSwitchesStatusList = new ArrayList<>();
            String machineBaseURL = "";

            try {

                dbHelper.openDataBase();

                // get current machine details
                machineCursor = dbHelper.getMachineByID(String.valueOf(mParamMachineId));
                machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));
                isMachineActive = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));
                machineIp = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP));
                cursor = dbHelper.getAllSwitchComponentsForAMachine(machineIp);
                dbHelper.close();

                if(machineIp.startsWith("http://")) {
                    machineBaseURL = machineIp;
                } else {
                    machineBaseURL = "http://" + machineIp;
                }

                // fetch switch status from machine only if the machine is active else init all the switch status to off
                if (isMachineActive.equals("true")) {
                    Log.e("$#$# Switch url",""+machineBaseURL+"ACTIVE");

                    URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_SWITCH_STATUS);

                    HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();

                    httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);

                    httpUrlConnection.setRequestMethod("GET");
                    InputStream inputStream = null;
                    httpUrlConnection.setDoInput(true);
                    inputStream = httpUrlConnection.getInputStream();
                    MainXmlPullParser pullParser = new MainXmlPullParser();
                    List<XMLValues> switchStatusList = pullParser.processXML(inputStream);

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
                Log.e("~~~~~~~~TIME OUT~~~", ex1.toString());
                timeOutErrorCount++;
                Log.e("# timeOutErrorCount", timeOutErrorCount+"");

                if (timeOutErrorCount > 10) {
                    timeOutErrorCount = 0;
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
                                    allSwitchesStatusList.add(values);

                                } while (cursor.moveToNext());
                            }
                        }

                    } catch (SQLException ex) {
                        Log.e("TAG EXP TIME OUT", ex.toString());
                    }
                } else {
                    stopTherad();
                    startTherad();
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
                    //init mAdapter
                    mAdapter = new SwitchListCursorAdapter(getActivity(), switchListCursor, allSwitchesStatusList);
                    mAdapter.setType(0);
                    mAdapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    isFirstTime = false;
                } else {
                    if(allSwitchesStatusList.size()>0) {
                        //set mAdapter again
                        mAdapter.setSwitchStatus(allSwitchesStatusList);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        Log.e("size zero",""+allSwitchesStatusList.size());
                    }
                }

                mAdapter.setCheckedChangeListener(new onCheckedChangeListener() {
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

                mAdapter.setRenameClickListener(new onRenameClickListener() {

                    @Override
                    public void onRenameOptionClick(int pos, String _oldName) {
                        renameComponent(pos);
                    }

                    @Override
                    public void onRenameOptionClick(int pos, String oldName, String oldDetails) {

                    }
                });

                mAdapter.setAddToSceneClickListener(new onAddToSceneClickListener() {
                    @Override
                    public void onAddToSceneOptionClick(int pos) {
                        addComponentToScene(pos);
                    }
                });

                mAdapter.setFavoriteClickListener(new onFavoriteClickListener() {
                    @Override
                    public void onFavoriteOptionClick(int pos) {
                        addComponentToFavourite(pos);
                    }
                });

                mAdapter.setLongClickListener(new onLongClickListener() {

                    @Override
                    public void onLongClick(final int pos, View view) {

                        LongPressOptionsDialog longPressOptionsDialog = new LongPressOptionsDialog(getActivity(), pos);
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

                mAdapter.setAddSchedulerClickListener(new onAddSchedulerClickListener() {

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
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
            dbHelper.openDataBase();
            machineName = dbHelper.getMachineNameByIP(machineIP);
            int switchCount = dbHelper.getComponentTypeCountInFavourite(componentType);

            if (switchCount < 10) {
                boolean isAlreadyAFavourite = dbHelper.insertIntoFavorite(componentPrimaryId, componentId, componentName, componentType, componentMachineID,machineIP, machineName);
                dbHelper.close();

                if (isAlreadyAFavourite) {
                    Toast.makeText(getActivity(), "Already added to Favorite.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Added to Favorite Successfully.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Cannot add more than 10 switches to favourites.", Toast.LENGTH_SHORT).show();
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


        RenameDialog renameDialog = new RenameDialog(getActivity(), componentName);
        renameDialog.show();

        renameDialog.setRenameListener(new onRenameClickListener() {
            @Override
            public void onRenameOptionClick(int pos, String newName) {
                try {
                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                    dbHelper.openDataBase();
                    dbHelper.renameComponent(componentId, newName);
                    switchListCursor = dbHelper.getAllSwitchComponents();
                    dbHelper.close();
                    mAdapter.changeCursor(switchListCursor);

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
        SceneListDialog dialog = new SceneListDialog(getActivity(), componentId1, componentType);
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
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
            dbHelper.openDataBase();
            boolean isAlreadyScheduled = dbHelper.isAlreadyScheduled(componentPrimaryId, componentMachineID);
            dbHelper.close();

            if(! isAlreadyScheduled) {
                SchedulerModel schedulerModel = new SchedulerModel("", componentPrimaryId, componentId,componentName, componentType, componentMachineID,componentMachineIP, componentMachineName, false, "00");

                AddToSchedulerDialog addToSchedulerDialog = new AddToSchedulerDialog(getActivity(), schedulerModel);
                addToSchedulerDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                addToSchedulerDialog.show();
            } else {
                dbHelper = new DatabaseHelper(getActivity());
                SchedulerModel schedulerModel = null;
                try {
                    dbHelper.openDataBase();
                    schedulerModel =  dbHelper.getSchedulerByComponentId(componentPrimaryId, componentMachineID, "0");
                    dbHelper.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if(schedulerModel != null) {
                    EditSchedulerDialog addToSchedulerDialog = new EditSchedulerDialog(getActivity(), schedulerModel);
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
}
