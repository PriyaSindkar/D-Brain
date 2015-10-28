package com.webmyne.android.d_brain.ui.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.MachineListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.AddMachineDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.sql.SQLException;

import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class MachineListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MachineListCursorAdapter adapter;
    private Cursor machineCursor;
    private ImageView imgBack;
    private int totalNoOfMachines = 5;
    private TextView txtAddMachine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        txtAddMachine = (TextView) findViewById(R.id.txtAddMachine);


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


        adapter.setRenameClickListener(new onRenameClickListener() {

            @Override
            public void onRenameOptionClick(int pos, String _oldName) {
                machineCursor.moveToPosition(pos);
                final String machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));

                RenameDialog renameDialog = new RenameDialog(MachineListActivity.this, _oldName);
                renameDialog.show();

                renameDialog.setRenameListener(new onRenameClickListener() {
                    @Override
                    public void onRenameOptionClick(int pos, String newName) {
                        try {
                            DatabaseHelper dbHelper = new DatabaseHelper(MachineListActivity.this);
                            dbHelper.openDataBase();
                            dbHelper.renameMachine(machineId, newName);
                            adapter.notifyDataSetChanged();
                            dbHelper.close();
                            machineCursor = dbHelper.getAllMachines();
                            adapter.changeCursor(machineCursor);


                        } catch (SQLException e) {
                            Log.e("TAG EXP", e.toString());
                        }

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

                        } catch (SQLException e) {
                            Log.e("TAG EXP", e.toString());
                        }
                    }
                });



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


}
