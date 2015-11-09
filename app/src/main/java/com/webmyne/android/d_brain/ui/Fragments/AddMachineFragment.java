package com.webmyne.android.d_brain.ui.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.MachineListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.AddMachineDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.EditMachineDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.MachineNotActiveDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.SaveAlertDialog;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onMachineStateChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;
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

import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class AddMachineFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MachineListCursorAdapter adapter;
    private int totalNoOfMachines = 2;
    private TextView txtEmptyView, txtEmptyView1;
    private LinearLayout emptyView;
    private Cursor machineCursor;
    private ProgressBar progress_bar;
    private ProgressDialog progress_dialog;
    private String machineId, machineIp;
    private boolean isEnabled;
    int timeOutErrorCount = 3;

    public static AddMachineFragment newInstance() {
        AddMachineFragment fragment = new AddMachineFragment();

        return fragment;
    }

    public AddMachineFragment() {
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

        callRenameClick();
        callOnDeleteClick();
        callOnMachineEnabledDisabled();
        return view;
    }

    private void callRenameClick() {
        adapter.setRenameClickListener(new onRenameClickListener() {

            @Override
            public void onRenameOptionClick(int pos, String _oldName) {

            }

            @Override
            public void onRenameOptionClick(int pos, String oldName, String oldDetails) {
                machineCursor.moveToPosition(pos);
                final String machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));

                EditMachineDialog renameDialog = new EditMachineDialog(getActivity(), pos, oldName, oldDetails);
                renameDialog.show();

                renameDialog.setRenameListener(new onRenameClickListener() {
                    @Override
                    public void onRenameOptionClick(int pos, String newName) {

                    }

                    @Override
                    public void onRenameOptionClick(int pos, String newName, String newIP) {
                        progress_bar.setVisibility(View.VISIBLE);
                        try {
                            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                            dbHelper.openDataBase();

                            dbHelper.renameMachine(machineId, newName, newIP);
                            adapter.notifyDataSetChanged();
                            dbHelper.close();
                            machineCursor = dbHelper.getAllMachines();
                            adapter.changeCursor(machineCursor);
                            progress_bar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Machine Updated", Toast.LENGTH_LONG).show();

                        } catch (SQLException e) {
                            Log.e("TAG EXP", e.toString());
                        }

                    }
                });
            }
        });
    }

    private void callOnDeleteClick() {
        adapter.setDeleteClickListener(new onDeleteClickListener() {
            @Override
            public void onDeleteOptionClick(final int pos) {
                SaveAlertDialog saveAlertDialog = new SaveAlertDialog(getActivity(), "Are you sure you want to delete the machine?");
                saveAlertDialog.show();

                saveAlertDialog.setSaveListener(new onSaveClickListener() {
                    @Override
                    public void onSaveClick(boolean isSave) {
                        if (isSave) {
                            progress_bar.setVisibility(View.VISIBLE);

                            machineCursor.moveToPosition(pos);
                            final String machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));

                            try {
                                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                                dbHelper.openDataBase();
                                dbHelper.deleteMachine(machineId);
                                machineCursor = dbHelper.getAllMachines();
                                adapter = new MachineListCursorAdapter(getActivity(), machineCursor);
                                adapter.setHasStableIds(true);
                                mRecyclerView.setAdapter(adapter);
                                progress_bar.setVisibility(View.GONE);

                                callRenameClick();
                                callOnDeleteClick();
                                callOnMachineEnabledDisabled();

                                Toast.makeText(getActivity(), "Machine Deleted", Toast.LENGTH_LONG).show();
                            } catch (SQLException e) {
                                Log.e("TAG EXP", e.toString());
                            }
                        } else {
                        }
                    }
                });
            }
        });
    }

    private void callOnMachineEnabledDisabled() {
        adapter.setMachineStateChanged(new onMachineStateChangeListener() {
            @Override
            public void onMachineEnabledDisabled(int pos, boolean _isEnabled) {
                machineCursor.moveToPosition(pos);
                int machineIdIndex = machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID);
                machineId = machineCursor.getString(machineIdIndex);
                isEnabled = _isEnabled;

                int machineIpIndex = machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP);
                machineIp = machineCursor.getString(machineIpIndex);

                if (_isEnabled) {
                    //call debt
                    new GetMachineStatus().execute(machineIp);
                } else {
                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                    // enable/disable machine throughout db
                    try {
                        dbHelper.openDataBase();
                        dbHelper.enableDisableMachine(machineId, _isEnabled);
                        machineCursor = dbHelper.getAllMachines();
                        adapter = new MachineListCursorAdapter(getActivity(), machineCursor);
                        adapter.setHasStableIds(true);
                        mRecyclerView.setAdapter(adapter);
                        progress_bar.setVisibility(View.GONE);

                        callRenameClick();
                        callOnDeleteClick();
                        callOnMachineEnabledDisabled();
                        //dbHelper.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getActivity(), "Machine is Deactivated.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void refreshAdapter() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
            dbHelper.openDataBase();
            dbHelper.close();
            machineCursor = dbHelper.getAllMachines();

            adapter = new MachineListCursorAdapter(getActivity(), machineCursor);
            adapter.setType(0);
            adapter.setHasStableIds(true);
            mRecyclerView.setAdapter(adapter);

            callOnDeleteClick();
            callRenameClick();
            callOnMachineEnabledDisabled();

        } catch (SQLException e) {
            Log.e("TAG EXP", e.toString());
        }
    }

    private void init(View view) {
        ((HomeDrawerActivity) getActivity()).setTitle("Add Machine");
        ((HomeDrawerActivity) getActivity()).showAppBarButton();
        ((HomeDrawerActivity) getActivity()).setClearButtonText("Add Machine");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        emptyView = (LinearLayout) view.findViewById(R.id.emptyView);
        txtEmptyView1 = (TextView) view.findViewById(R.id.txtEmptyView1);
        txtEmptyView = (TextView) view.findViewById(R.id.txtEmptyView);

        progress_bar = (ProgressBar) view.findViewById(R.id.progress_bar);

        progress_dialog = new ProgressDialog(getActivity());
        progress_dialog.setMessage("Please wait...");
        progress_dialog.setCancelable(false);


        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        //insert switches in adapter ofr machine-1
        try {
            dbHelper.openDataBase();
            machineCursor =  dbHelper.getAllMachines();
            Log.e("machine cursor", machineCursor.getCount() + "");
            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        if(machineCursor.getCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

            txtEmptyView.setText(getResources().getString(R.string.empty_machines_list));
        } else {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(layoutManager);

        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), getActivity());
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        adapter = new MachineListCursorAdapter(getActivity(), machineCursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);
    }

    public class GetMachineStatus extends AsyncTask<String, Void, Void> {
        boolean isError = false;

        @Override
        protected void onPreExecute() {
            progress_dialog.setMessage("Connecting to machine.. " + timeOutErrorCount);
            progress_dialog.show();
            mRecyclerView.setFocusable(false);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL urlValue = new URL(params[0] + AppConstants.URL_FETCH_MACHINE_STATUS);
                Log.e("# urlValue", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);

                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();
                //  Log.e("# inputStream", inputStream.toString());
                MainXmlPullParser pullParser = new MainXmlPullParser();
                ArrayList<XMLValues> powerStatus = pullParser.processXML(inputStream);
                isError = false;

            } catch (Exception e) {
                Log.e("# EXP", e.toString());
                isError = true;
                timeOutErrorCount--;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if( !isError) {
                progress_dialog.dismiss();
                mRecyclerView.setFocusable(true);

                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                // enable/disable machine throughout db
                try {
                    dbHelper.openDataBase();
                    dbHelper.enableDisableMachine(machineId, isEnabled);

                    machineCursor = dbHelper.getAllMachines();
                    adapter = new MachineListCursorAdapter(getActivity(), machineCursor);
                    adapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(adapter);

                    callRenameClick();
                    callOnDeleteClick();
                    callOnMachineEnabledDisabled();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), "Machine is Activated.", Toast.LENGTH_SHORT).show();
            } else {
                if(timeOutErrorCount > 0) {
                    progress_dialog.setMessage("Connecting to machine.. " + timeOutErrorCount);
                    new GetMachineStatus().execute(machineIp);
                } else {
                    progress_dialog.dismiss();
                    timeOutErrorCount = 3;
                    // show alert dialog for ok and retry
                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                    // enable/disable machine throughout db
                    try {
                        dbHelper.openDataBase();
                        machineCursor = dbHelper.getAllMachines();
                        adapter = new MachineListCursorAdapter(getActivity(), machineCursor);
                        adapter.setHasStableIds(true);
                        mRecyclerView.setAdapter(adapter);

                        callRenameClick();
                        callOnDeleteClick();
                        callOnMachineEnabledDisabled();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    timeOutErrorCount = 3;
                    MachineNotActiveDialog machineNotActiveDialog = new MachineNotActiveDialog(getActivity(), "Machine cannot be activated.");
                    machineNotActiveDialog.show();

                    machineNotActiveDialog.setSaveListener(new onSaveClickListener() {
                        @Override
                        public void onSaveClick(boolean isSave) {
                            if (!isSave) {
                                //call debt
                                timeOutErrorCount = 3;
                                new GetMachineStatus().execute(machineIp);
                            } else {
                                timeOutErrorCount = 3;
                                progress_dialog.dismiss();
                            }
                        }
                    });
                }
            }
        }
    }

    /*public class GetMachineStatus extends AsyncTask<String, Void, Void> {
        boolean isError = false;

        @Override
        protected void onPreExecute() {
           progress_dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL urlValue = new URL(params[0] + AppConstants.URL_FETCH_MACHINE_STATUS);
                Log.e("# urlValue", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);

                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();
                //  Log.e("# inputStream", inputStream.toString());
                MainXmlPullParser pullParser = new MainXmlPullParser();
                ArrayList<XMLValues> powerStatus = pullParser.processXML(inputStream);

            } catch (Exception e) {
                Log.e("# EXP", e.toString());
                isError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progress_dialog.hide();
            try {
                if (isError) {

                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                    // enable/disable machine throughout db
                    try {
                        dbHelper.openDataBase();
                        machineCursor = dbHelper.getAllMachines();
                        adapter = new MachineListCursorAdapter(getActivity(), machineCursor);
                        adapter.setHasStableIds(true);
                        mRecyclerView.setAdapter(adapter);
                        progress_bar.setVisibility(View.GONE);

                        callRenameClick();
                        callOnDeleteClick();
                        callOnMachineEnabledDisabled();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    // show alert dialog for ok and retry
                    MachineNotActiveDialog machineNotActiveDialog = new MachineNotActiveDialog(getActivity(), "Machine cannot be activated.");
                    machineNotActiveDialog.show();

                    machineNotActiveDialog.setSaveListener(new onSaveClickListener() {
                        @Override
                        public void onSaveClick(boolean isSave) {
                            if( !isSave) {
                                //call debt
                                new GetMachineStatus().execute(machineIp);
                            }
                        }
                    });

                } else {
                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                    // enable/disable machine throughout db
                    try {
                        dbHelper.openDataBase();
                        dbHelper.enableDisableMachine(machineId, isEnabled);

                        machineCursor = dbHelper.getAllMachines();
                        adapter = new MachineListCursorAdapter(getActivity(), machineCursor);
                        adapter.setHasStableIds(true);
                        mRecyclerView.setAdapter(adapter);
                        progress_bar.setVisibility(View.GONE);

                        callRenameClick();
                        callOnDeleteClick();
                        callOnMachineEnabledDisabled();
                        //dbHelper.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getActivity(), "Machine is Activated.", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {

            }


        }
    }*/

}
