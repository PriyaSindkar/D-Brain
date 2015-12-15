package com.webmyne.android.d_brain.ui.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.CreateSceneActivity;
import com.webmyne.android.d_brain.ui.Activities.SceneActivity;
import com.webmyne.android.d_brain.ui.Adapters.SceneListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.AddToSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSceneOnOffClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;
import com.webmyne.android.d_brain.ui.Model.SchedulerModel;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;
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

import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class SceneFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private SceneListCursorAdapter adapter;
    private int totalNoOfScenes = 0;
    private TextView txtEmptyView, txtEmptyView1;
    private LinearLayout emptyView;
    private ImageView imgEmpty;
    private Cursor sceneListCursor;
    private ArrayList<SceneItemsDataObject> mData = new ArrayList<>();

    private int timeOutErrorCount = 3;

    public static SceneFragment newInstance() {
        SceneFragment fragment = new SceneFragment();

        return fragment;
    }

    public SceneFragment() {
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

    @Override
    public void onResume() {
        super.onResume();
        updateSceneList();
    }

    private void init(View view) {
        ((HomeDrawerActivity) getActivity()).setTitle("Scenes");
        ((HomeDrawerActivity) getActivity()).showAppBarButton();
        ((HomeDrawerActivity) getActivity()).setClearButtonText("Create New Scene");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyView = (LinearLayout) view.findViewById(R.id.emptyView);
        txtEmptyView1 = (TextView) view.findViewById(R.id.txtEmptyView1);
        txtEmptyView = (TextView) view.findViewById(R.id.txtEmptyView);
        imgEmpty = (ImageView) view.findViewById(R.id.imgEmpty);

        txtEmptyView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateSceneActivity.class);
                getActivity().startActivity(intent);
            }
        });


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), getActivity());
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);

    }

    private void updateSceneList() {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        try {
            dbHelper.openDataBase();
            sceneListCursor = dbHelper.getAllScenes("");
            dbHelper.close();
        } catch (SQLException e) {
            Log.e("SQLEXP", e.toString());
        }

        if(sceneListCursor.getCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            imgEmpty.setVisibility(View.VISIBLE);

            imgEmpty.setImageResource(R.drawable.drawer_scenes);
            AdvancedSpannableString sp = new AdvancedSpannableString("Click Here");
            sp.setUnderLine("Click Here");
            sp.setColor(getResources().getColor(R.color.yellow), "Click Here");
            txtEmptyView1.setText(sp);
        } else {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            adapter = new SceneListCursorAdapter(getActivity(), sceneListCursor);
            adapter.setType(1);
            adapter.setHasStableIds(true);
            mRecyclerView.setAdapter(adapter);

            adapter.setSingleClickListener(new onSingleClickListener() {
                @Override
                public void onSingleClick(int pos) {
                    sceneListCursor.moveToPosition(pos);

                    String sceneId = sceneListCursor.getString(sceneListCursor.getColumnIndexOrThrow(DBConstants.KEY_S_ID));
                    String sceneName = sceneListCursor.getString(sceneListCursor.getColumnIndexOrThrow(DBConstants.KEY_S_NAME));

                    // Redirect To the respective saved scene
                    Intent intent = new Intent(getActivity(), SceneActivity.class);
                    intent.putExtra("scene_id", sceneId);
                    intent.putExtra("scene_name", sceneName);
                    getActivity().startActivity(intent);
                }
            });


            adapter.setAddToScheduler(new onAddSchedulerClickListener() {
                @Override
                public void onAddSchedulerOptionClick(int pos) {
                    addComponentToScheduler(pos);
                }
            });

            adapter.setSceneOnOffListener(new onSceneOnOffClickListener() {
                @Override
                public void onSingleClick(int pos, boolean isOn, String sceneId) {
                    String sceneStatus = "";
                    if (isOn) {
                        sceneStatus = "yes";
                    } else {
                        sceneStatus = "no";
                    }

                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                    try {
                        dbHelper.openDataBase();
                        dbHelper.updateSceneStatus(sceneId, sceneStatus);
                        dbHelper.close();
                    } catch (Exception e) {

                    }
                    setSceneOnOff(sceneId, isOn);
                }
            });

            adapter.setRenameClickListener(new onRenameClickListener() {
                @Override
                public void onRenameOptionClick(int pos, String oldName) {
                    RenameDialog renameDialog = new RenameDialog(getActivity(), oldName);
                    renameDialog.show();
                    sceneListCursor.moveToPosition(pos);
                    final String sceneId = sceneListCursor.getString(sceneListCursor.getColumnIndexOrThrow(DBConstants.KEY_S_ID));

                    renameDialog.setRenameListener(new onRenameClickListener() {
                        @Override
                        public void onRenameOptionClick(int pos, String newName) {
                            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                            try {
                                dbHelper.openDataBase();
                                dbHelper.renameScene(sceneId, newName);
                                sceneListCursor = dbHelper.getAllScenes("");
                                adapter.changeCursor(sceneListCursor);
                                adapter.notifyDataSetChanged();

                            }catch ( Exception e) {}
                        }

                        @Override
                        public void onRenameOptionClick(int pos, String newName, String newDetails) {

                        }
                    });
                }

                @Override
                public void onRenameOptionClick(int pos, String oldName, String oldDetails) {

                }
            });
        }
    }

    private void addComponentToScheduler(int pos) {
        sceneListCursor.moveToPosition(pos);
        String componentId1 = sceneListCursor.getString(sceneListCursor.getColumnIndexOrThrow(DBConstants.KEY_S_ID));
        String componentName = sceneListCursor.getString(sceneListCursor.getColumnIndexOrThrow(DBConstants.KEY_S_NAME));

        SchedulerModel schedulerModel = new SchedulerModel("", componentId1,"", componentName, AppConstants.SCENE_TYPE, "","", "", true, "00");

        AddToSchedulerDialog addToSchedulerDialog = new AddToSchedulerDialog(getActivity(), schedulerModel);
        addToSchedulerDialog.show();
    }


    private void setSceneOnOff(String currentSceneId, boolean isOn) {
        // show scene saved state
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        try {
            dbHelper.openDataBase();
            Cursor switchListCursor = dbHelper.getAllComponentsInAScene(currentSceneId);
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

                        SceneItemsDataObject sceneItemsDataObject = new SceneItemsDataObject();
                        sceneItemsDataObject.setMachineId(machineID);
                        sceneItemsDataObject.setMachineIP(machineIP);
                        sceneItemsDataObject.setMachineName(machineName);
                        sceneItemsDataObject.setSceneControlType(switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_TYPE)));
                        sceneItemsDataObject.setSceneItemId(componentId);
                        sceneItemsDataObject.setSceneComponentPrimaryId(componentPrimaryId);

                        String componentName = dbHelper.getComponentNameByPrimaryId(componentId);
                        //String componentName = componentCursor.getString(componentCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));

                        sceneItemsDataObject.setName(componentName);
                        sceneItemsDataObject.setDefaultValue(defaultValue);
                        mData.add(sceneItemsDataObject);

                    } while (switchListCursor.moveToNext());
                }
            }
            dbHelper.close();

        } catch (SQLException e) {
            Log.e("SQLEXP", e.toString());
        }


        if(mData != null) {
            if(mData.size() > 0 && isOn) {
                new CallSceneOn().execute();
            }

            if(mData.size() > 0 && !isOn) {
                new CallSceneOff().execute();
            }
        }
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
                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

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
                            Toast.makeText(getActivity(), "Machine, " + machineName + " was deactivated.", Toast.LENGTH_LONG).show();
                        } catch (SQLException e) {
                            Log.e("TAG EXP", e.toString());
                        }
                    }
                } catch (Exception e) {
                    Log.e("# EXP", e.toString());
                    isError = true;
                    timeOutErrorCount--;
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
                if(isError) {
                    if(timeOutErrorCount > 0) {
                        new CallSceneOn().execute();
                    } else {
                        timeOutErrorCount = 3;
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
                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
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
                            Toast.makeText(getActivity(), "Machine, " + machineName + " was deactivated.", Toast.LENGTH_LONG).show();
                        } catch (SQLException e) {
                            Log.e("TAG EXP", e.toString());
                        }
                    }
                } catch (Exception e) {
                    Log.e("# EXP", e.toString());
                    isError = true;
                    timeOutErrorCount--;
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try{
                if(isError) {
                    if(timeOutErrorCount > 0) {
                        new CallSceneOff().execute();
                    } else {
                        timeOutErrorCount = 3;
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
                }
            }catch(Exception e){
            }
        }
    }

}
