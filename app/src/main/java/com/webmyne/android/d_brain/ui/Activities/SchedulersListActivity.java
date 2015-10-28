package com.webmyne.android.d_brain.ui.Activities;

import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.MachineListCursorAdapter;
import com.webmyne.android.d_brain.ui.Adapters.SchedulersListCursorAdapter;
import com.webmyne.android.d_brain.ui.Adapters.TouchPComponentListAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.AddMachineDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.AddToSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.EditSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.SchedulerModel;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.sql.SQLException;

import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class SchedulersListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private SchedulersListCursorAdapter adapter;
    private Cursor schedulersCursor;
    private ImageView imgBack, imgEmpty;
    private int totalNoOfMachines = 5;
    private TextView txtAddMachine, txtTitle, txtEmptyView;
    private LinearLayout linearEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        txtAddMachine = (TextView) findViewById(R.id.txtAddMachine);
        linearEmptyView = (LinearLayout) findViewById(R.id.linearEmptyView);
        txtTitle  = (TextView) findViewById(R.id.txtTitle);
        txtEmptyView = (TextView) findViewById(R.id.txtEmptyView);
        imgEmpty = (ImageView) findViewById(R.id.imgEmpty);

        txtTitle.setText("Schedulers");

        txtAddMachine.setVisibility(View.GONE);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(layoutManager);
        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), SchedulersListActivity.this);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        try {
            dbHelper.openDataBase();
            schedulersCursor =  dbHelper.getAllSchedulers();
            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(schedulersCursor.getCount() == 0) {
            linearEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            imgEmpty.setImageResource(R.drawable.drawer_schedulers);

            txtEmptyView.setText(getResources().getString(R.string.empty_scehdulers_list));
        } else {
            linearEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        adapter = new SchedulersListCursorAdapter(SchedulersListActivity.this, schedulersCursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);

        adapter.setDeleteClickListener(new onDeleteClickListener() {
            @Override
            public void onDeleteOptionClick(final int pos) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SchedulersListActivity.this);
                alertDialogBuilder.setTitle("Delete Scheduler");
                alertDialogBuilder
                        .setMessage("Are you sure you want to delete the scheduler?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteScheduler(pos);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });


        adapter.setRenameClickListener(new onRenameClickListener() {

            @Override
            public void onRenameOptionClick(int pos, String _oldName) {
                schedulersCursor.moveToPosition(pos);
                final String schedulerId = schedulersCursor.getString(schedulersCursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_ID));
                final String schedulerName = schedulersCursor.getString(schedulersCursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_NAME));
                final String componentName = schedulersCursor.getString(schedulersCursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_SCENE_NAME));
                final String componentType = schedulersCursor.getString(schedulersCursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_TYPE));
                final String defaultValue = schedulersCursor.getString(schedulersCursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_DEFAULT));
                final String dateTime = schedulersCursor.getString(schedulersCursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_DATETIME));

                SchedulerModel model = new SchedulerModel();
                model.setId(Integer.parseInt(schedulerId));
                model.setDefaultValue(defaultValue);
                model.setComponentType(componentType);
                model.setSchedulerName(schedulerName);
                model.setComponentName(componentName);
                model.setDateTime(dateTime);
                model.setDefaultValue(defaultValue);


                EditSchedulerDialog addToSchedulerDialog = new EditSchedulerDialog(SchedulersListActivity.this, model);
                addToSchedulerDialog.show();

                addToSchedulerDialog.setOnSingleClick(new onSingleClickListener() {
                    @Override
                    public void onSingleClick(int pos) {
                        DatabaseHelper dbHelper = new DatabaseHelper(SchedulersListActivity.this);

                        try {
                            dbHelper.openDataBase();
                            schedulersCursor =  dbHelper.getAllSchedulers();
                            if(schedulersCursor.getCount() == 0) {
                                linearEmptyView.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                                imgEmpty.setImageResource(R.drawable.drawer_schedulers);
                                txtEmptyView.setText(getResources().getString(R.string.empty_scehdulers_list));
                            } else {
                                linearEmptyView.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                            }

                           /* adapter = new SchedulersListCursorAdapter(SchedulersListActivity.this, schedulersCursor);
                            adapter.setHasStableIds(true);
                            mRecyclerView.setAdapter(adapter);*/
                            dbHelper.close();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        adapter.changeCursor(schedulersCursor);
                        adapter.notifyDataSetChanged();
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


    }

    private void deleteScheduler(int pos) {
        schedulersCursor.moveToPosition(pos);

        String schedulerId = schedulersCursor.getString(schedulersCursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_ID));
        DatabaseHelper dbHelper = new DatabaseHelper(SchedulersListActivity.this);

        try {
            dbHelper.openDataBase();
            dbHelper.deleteScheduler(schedulerId);
            schedulersCursor = dbHelper.getAllSchedulers();

            if(schedulersCursor.getCount() == 0) {
                linearEmptyView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                imgEmpty.setImageResource(R.drawable.drawer_schedulers);

                txtEmptyView.setText(getResources().getString(R.string.empty_scehdulers_list));
            } else {
                linearEmptyView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            adapter = new SchedulersListCursorAdapter(SchedulersListActivity.this, schedulersCursor);
            adapter.setHasStableIds(true);
            mRecyclerView.setAdapter(adapter);
            dbHelper.close();

            Toast.makeText(SchedulersListActivity.this, "Scheduler Deleted.", Toast.LENGTH_SHORT).show();

        } catch (SQLException e) {
            e.printStackTrace();
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


}
