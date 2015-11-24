package com.webmyne.android.d_brain.ui.Fragments;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.MainDimmersListActivity;
import com.webmyne.android.d_brain.ui.Activities.MainSwitchesListActivity;
import com.webmyne.android.d_brain.ui.Adapters.DimmerListCursorAdapter;
import com.webmyne.android.d_brain.ui.Adapters.SwitchListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.AddToSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.EditSchedulerDialog;
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
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class DimmerListFragment extends Fragment {

    private static final String ARG_PARAM1 = "MACHINE_ID";
    private static final String ARG_PARAM2 = "MACHINE_NAME";
    private static final String ARG_PARAM3 = "IS_LISTVIEW";
    private Activity activity;
    private int mParamMachineId;
    private String mParamMachineName;
    private boolean mParamIsListView = true, isFirstTime = true, isServiceRunning = false;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private DimmerListCursorAdapter adapter;
    private Cursor dimmerListCursor;
    private String[] machineIPs;
    ArrayList<XMLValues> allDimmerStatusList;
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
                        new GetDimmerStatus().execute();
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

    public static DimmerListFragment newInstance(int param1, String param2, boolean isListView) {
        DimmerListFragment fragment = new DimmerListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putBoolean(ARG_PARAM3, isListView);
        fragment.setArguments(args);
        return fragment;
    }

    public DimmerListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParamMachineId = getArguments().getInt(ARG_PARAM1);
            mParamMachineName = getArguments().getString(ARG_PARAM2);
            mParamIsListView = getArguments().getBoolean(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_switches_list, container, false);
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

        adapter = new DimmerListCursorAdapter(getActivity());
        ((MainDimmersListActivity)getActivity()).setListViewImage(mParamIsListView);

        if(mParamIsListView) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            adapter.setType(0);
            adapter.notifyDataSetChanged();
        } else {
            android.support.v7.widget.GridLayoutManager layoutManager = new android.support.v7.widget.GridLayoutManager(activity, 3);
            layoutManager.supportsPredictiveItemAnimations();
            mRecyclerView.setLayoutManager(layoutManager);
            adapter.setType(1);
            adapter.notifyDataSetChanged();
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
            AppConstants.getCurrentSsid(activity);
            initArrayOfDimmers();
            startTherad();
        } else {
            stopTherad();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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


    private void initArrayOfDimmers() {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        //get all dimmers in adapter for machine-1
        try {
            dbHelper.openDataBase();
            dimmerListCursor =  dbHelper.getAllDimmerComponentsForAMachineById(String.valueOf(mParamMachineId));
            dbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public class GetDimmerStatus extends AsyncTask<Void, Void, Void> {
        boolean isError = false;
        String machineId="", machineName = "", machineIp, isMachineActive = "false";
        Cursor cursor, machineCursor;
        boolean isMachineDeactivated = false;
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        @Override
        protected void onPreExecute() {
            isServiceRunning = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            allDimmerStatusList = new ArrayList<>();
            String machineBaseURL = "";

            DatabaseHelper dbHelper = new DatabaseHelper(activity);
            try {
                dbHelper.openDataBase();
                // get current machine details
                machineCursor = dbHelper.getMachineByID(String.valueOf(mParamMachineId));
                machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                machineIp = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP));
                machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));
                isMachineActive = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));
                cursor = dbHelper.getAllDimmerComponentsForAMachine(machineIp);
                dbHelper.close();


                if (machineIp.startsWith("http://")) {
                    machineBaseURL = machineIp;
                } else {
                    machineBaseURL = "http://" + machineIp;
                }

                // fetch dimmer status from machine only if the machine is active else init all the dimmer status to off
                if (isMachineActive.equals("true")) {

                    URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_DIMMER_STATUS);
                    Log.e("# urlValue", urlValue.toString());

                    HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                    httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                    httpUrlConnection.setRequestMethod("GET");
                    InputStream inputStream = httpUrlConnection.getInputStream();
                    //  Log.e("# inputStream", inputStream.toString());
                    MainXmlPullParser pullParser = new MainXmlPullParser();

                    List<XMLValues> dimmerStatusList = pullParser.processXML(inputStream);
                    Log.e("XML PARSERED", dimmerStatusList.toString());

                    int totalDimmersofMachine = 0;
                    if (cursor != null) {
                        totalDimmersofMachine = cursor.getCount();
                    }

                    for (int j = 0; j < totalDimmersofMachine; j++) {
                        allDimmerStatusList.add(dimmerStatusList.get(j));
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
                }catch (Exception e) {
                    Log.e("~~~~~~~~TIME OUT~~~", e.toString());
                    timeOutErrorCount++;
                    Log.e("# timeOutErrorCount", timeOutErrorCount + "");

                    if (timeOutErrorCount > 10) {
                        timeOutErrorCount = 0;
                        try {
                            dbHelper.openDataBase();
                            dbHelper.enableDisableMachine(machineId, false);
                            dbHelper.close();

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
                            isMachineDeactivated = true;
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        isMachineDeactivated = false;
                    }
                }
         return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isServiceRunning = false;
            try {
                if(isMachineDeactivated) {
                    stopTherad();
                    //show dialog
                    MachineInactiveDialog machineNotActiveDialog = new MachineInactiveDialog(activity, "This machine has been deactivated. Please switch it on.");
                    machineNotActiveDialog.show();
                    machineNotActiveDialog.setSaveListener(new onSaveClickListener() {
                        @Override
                        public void onSaveClick(boolean isSave) {
                            stopTherad();
                            activity.finish();
                        }
                    });

                    //startTherad();
                } else {
                    if (isFirstTime) {
                        if (allDimmerStatusList.size() > 0) {
                            progressBar.setVisibility(View.GONE);
                            //init adapter
                            adapter = new DimmerListCursorAdapter(activity, dimmerListCursor, allDimmerStatusList);
                            adapter.setHasStableIds(true);
                            mRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            isFirstTime = false;
                        }
                    } else {
                        if (allDimmerStatusList.size() > 0) {
                            progressBar.setVisibility(View.GONE);
                            //set adapter again
                            adapter.setDimmerStatus(allDimmerStatusList);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    adapter.setCheckedChangeListener(new onCheckedChangeListener() {
                        @Override
                        public void onCheckedChangeClick(int pos) {
                            if (pos == 0) {
                                stopTherad();
                                startTherad();
                            } else {
                                stopTherad();
                                Intent intent = new Intent(getActivity(), HomeDrawerActivity.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            }
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

                            boolean isFavaourite = false;
                            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                            try {
                                dbHelper.openDataBase();
                                Cursor cursor = dbHelper.getAllDimmerComponentsForAMachine(machineIp);
                                cursor.moveToPosition(pos);
                                String compName = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                                String machineId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID));
                                isFavaourite =  dbHelper.isAlreadyAFavourite(compName, machineId);
                                dbHelper.close();

                            } catch (Exception e) {

                            }

                            LongPressOptionsDialog longPressOptionsDialog = new LongPressOptionsDialog(activity, pos, isFavaourite);
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
                }
            } catch (Exception e) {
            }
        }
    }

    private void renameComponent(int pos) {

        final int position = pos;
        dimmerListCursor.moveToPosition(position);
        final String componentId = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        final String componentName = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));

        RenameDialog renameDialog = new RenameDialog(activity, componentName);
        renameDialog.show();

        renameDialog.setRenameListener(new onRenameClickListener() {
            @Override
            public void onRenameOptionClick(int pos, String newName) {
                try {
                    DatabaseHelper dbHelper = new DatabaseHelper(activity);
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
        SceneListDialog dialog = new SceneListDialog(activity, componentId, componentType);
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
            DatabaseHelper dbHelper = new DatabaseHelper(activity);
            dbHelper.openDataBase();
            machineName = dbHelper.getMachineNameByIP(machineIP);
            int dimmerCount = dbHelper.getComponentTypeCountInFavourite(componentType);

            if(dimmerCount <10) {
                boolean isAlreadyAFavourite = dbHelper.insertIntoFavorite(componentPrimaryId, componentId, componentName, componentType, componentMachineID,machineIP, machineName);
                dbHelper.close();

                if (isAlreadyAFavourite) {
                    dbHelper.openDataBase();
                    dbHelper.deleteComponentFromFavourite(componentPrimaryId);
                    dbHelper.close();
                    Toast.makeText(activity, "Removed from Favorites.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Added to Favorites Successfully!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "Cannot add more than 10 dimmers to favourites.", Toast.LENGTH_SHORT).show();
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
            DatabaseHelper dbHelper = new DatabaseHelper(activity);
            dbHelper.openDataBase();
            boolean isAlreadyScheduled = dbHelper.isAlreadyScheduled(componentPrimaryId, componentMachineID);

            if(! isAlreadyScheduled) {
                SchedulerModel schedulerModel = new SchedulerModel("", componentPrimaryId, componentId,componentName, componentType, componentMachineID,componentMachineIP, componentMachineName, false, "00");
                AddToSchedulerDialog addToSchedulerDialog = new AddToSchedulerDialog(activity, schedulerModel);
                addToSchedulerDialog.show();
            } else {
                SchedulerModel schedulerModel = null;
                schedulerModel =  dbHelper.getSchedulerByComponentId(componentPrimaryId, componentMachineID, "0");
                dbHelper.close();

                if(schedulerModel != null) {
                    EditSchedulerDialog addToSchedulerDialog = new EditSchedulerDialog(activity, schedulerModel);
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
