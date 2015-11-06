package com.webmyne.android.d_brain.ui.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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


public class MachineListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MachineListCursorAdapter adapter;
    private Cursor machineCursor;
    private ImageView imgBack;
    private TextView txtAddMachine;
    private ProgressBar progress_bar;
    private ProgressDialog progress_dialog;
    private String machineId, machineIp;
    private boolean isEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_list);


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        txtAddMachine = (TextView) findViewById(R.id.txtAddMachine);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

        progress_dialog = new ProgressDialog(MachineListActivity.this);
        progress_dialog.setMessage("Please wait...");
        progress_dialog.setCancelable(false);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(layoutManager);
        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), MachineListActivity.this);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //insert switches in adapter ofr machine-1
        try {
            dbHelper.openDataBase();
            machineCursor =  dbHelper.getAllMachines();
            Log.e("machine cursor", machineCursor.getCount() + "");
            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*if(machineCursor.getCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

            txtEmptyView.setText(getResources().getString(R.string.empty_machines_list));
        } else {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
*/
        adapter = new MachineListCursorAdapter(MachineListActivity.this, machineCursor);
        adapter.setType(0);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);


        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);


        callOnRenameClick();
        callOnDeleteClick();
        callOnMachineEnabledDisabled();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtAddMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMachineDialog machineDialog = new AddMachineDialog(MachineListActivity.this);
                machineDialog.show();

                machineDialog.setClickListener(new onSingleClickListener() {
                    @Override
                    public void onSingleClick(int pos) {
                        try {
                            DatabaseHelper dbHelper = new DatabaseHelper(MachineListActivity.this);
                            dbHelper.openDataBase();
                            dbHelper.close();
                            machineCursor = dbHelper.getAllMachines();

                            adapter = new MachineListCursorAdapter(MachineListActivity.this, machineCursor);
                            adapter.setType(0);
                            adapter.setHasStableIds(true);
                            mRecyclerView.setAdapter(adapter);

                            callOnRenameClick();
                            callOnDeleteClick();
                            callOnMachineEnabledDisabled();

                        } catch (SQLException e) {
                            Log.e("TAG EXP", e.toString());
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConstants.getCurrentSsid(MachineListActivity.this);
    }

    private void callOnDeleteClick() {
        adapter.setDeleteClickListener(new onDeleteClickListener() {
            @Override
            public void onDeleteOptionClick(final int pos) {

                SaveAlertDialog saveAlertDialog = new SaveAlertDialog(MachineListActivity.this, "Are you sure you want to delete the machine?");
                saveAlertDialog.show();

                saveAlertDialog.setSaveListener(new onSaveClickListener() {
                    @Override
                    public void onSaveClick(boolean isSave) {
                        if (isSave) {
                            progress_bar.setVisibility(View.VISIBLE);
                            machineCursor.moveToPosition(pos);
                            final String machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));

                            try {
                                DatabaseHelper dbHelper = new DatabaseHelper(MachineListActivity.this);
                                dbHelper.openDataBase();
                                dbHelper.deleteMachine(machineId);
                                machineCursor = dbHelper.getAllMachines();
                                adapter = new MachineListCursorAdapter(MachineListActivity.this, machineCursor);
                                adapter.setHasStableIds(true);
                                mRecyclerView.setAdapter(adapter);
                                progress_bar.setVisibility(View.GONE);

                                callOnRenameClick();
                                callOnDeleteClick();
                                callOnMachineEnabledDisabled();

                                Toast.makeText(MachineListActivity.this, "Machine Deleted", Toast.LENGTH_LONG).show();
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

    private void callOnRenameClick() {
        adapter.setRenameClickListener(new onRenameClickListener() {

            @Override
            public void onRenameOptionClick(int pos, String _oldName) {

            }

            @Override
            public void onRenameOptionClick(int pos, String oldName, String oldDetails) {

                machineCursor.moveToPosition(pos);
                final String machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                final String machineSerialNo = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_SERIALNO));

                EditMachineDialog renameDialog = new EditMachineDialog(MachineListActivity.this, pos, oldName, oldDetails);
                renameDialog.show();

                renameDialog.setRenameListener(new onRenameClickListener() {
                    @Override
                    public void onRenameOptionClick(int pos, String newName) {
                    }

                    @Override
                    public void onRenameOptionClick(int pos, String newName, String newIP) {
                        progress_bar.setVisibility(View.VISIBLE);
                        try {
                            DatabaseHelper dbHelper = new DatabaseHelper(MachineListActivity.this);
                            dbHelper.openDataBase();

                            dbHelper.renameMachine(machineId, newName, newIP);
                            machineCursor = dbHelper.getAllMachines();
                            // dbHelper.close();
                            adapter.changeCursor(machineCursor);
                            adapter.notifyDataSetChanged();
                            progress_bar.setVisibility(View.GONE);

                            Toast.makeText(MachineListActivity.this, "Machine Updated", Toast.LENGTH_LONG).show();

                        } catch (SQLException e) {
                            Log.e("TAG EXP", e.toString());
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
                    DatabaseHelper dbHelper = new DatabaseHelper(MachineListActivity.this);
                    // enable/disable machine throughout db
                    try {
                        dbHelper.openDataBase();
                        dbHelper.enableDisableMachine(machineId, _isEnabled);
                        machineCursor = dbHelper.getAllMachines();
                        adapter = new MachineListCursorAdapter(MachineListActivity.this, machineCursor);
                        adapter.setHasStableIds(true);
                        mRecyclerView.setAdapter(adapter);
                        //progress_bar.dismiss();

                        callOnRenameClick();
                        callOnDeleteClick();
                        callOnMachineEnabledDisabled();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MachineListActivity.this, "Machine is Deactivated.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    public class GetMachineStatus extends AsyncTask<String, Void, Void> {
        boolean isError = false;

        @Override
        protected void onPreExecute() {
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

            } catch (Exception e) {
                Log.e("# EXP", e.toString());
                isError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progress_dialog.dismiss();
            mRecyclerView.setFocusable(true);
            try {
                if (isError) {
                    // show alert dialog for ok and retry
                    DatabaseHelper dbHelper = new DatabaseHelper(MachineListActivity.this);
                    // enable/disable machine throughout db
                    try {
                        dbHelper.openDataBase();
                        machineCursor = dbHelper.getAllMachines();
                        adapter = new MachineListCursorAdapter(MachineListActivity.this, machineCursor);
                        adapter.setHasStableIds(true);
                        mRecyclerView.setAdapter(adapter);

                        callOnRenameClick();
                        callOnDeleteClick();
                        callOnMachineEnabledDisabled();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    MachineNotActiveDialog machineNotActiveDialog = new MachineNotActiveDialog(MachineListActivity.this, "Machine cannot be activated.");
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
                    DatabaseHelper dbHelper = new DatabaseHelper(MachineListActivity.this);
                    // enable/disable machine throughout db
                    try {
                        dbHelper.openDataBase();
                        dbHelper.enableDisableMachine(machineId, isEnabled);

                        machineCursor = dbHelper.getAllMachines();
                        adapter = new MachineListCursorAdapter(MachineListActivity.this, machineCursor);
                        adapter.setHasStableIds(true);
                        mRecyclerView.setAdapter(adapter);

                        callOnRenameClick();
                        callOnDeleteClick();
                        callOnMachineEnabledDisabled();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MachineListActivity.this, "Machine is Activated.", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {

            }


        }
    }



}
