package com.webmyne.android.d_brain.ui.Fragments;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.webmyne.android.d_brain.ui.Adapters.MachineListCursorAdapter;
import com.webmyne.android.d_brain.ui.Adapters.SchedulersListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.EditSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.SaveAlertDialog;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onMachineStateChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.SchedulerModel;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.sql.SQLException;

import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class SchedulersFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SchedulersListCursorAdapter adapter;
    private TextView txtEmptyView, txtEmptyView1;
    private LinearLayout emptyView;
    private Cursor schedulersCursor;
    private ImageView imgEmpty;

    public static SchedulersFragment newInstance() {
        SchedulersFragment fragment = new SchedulersFragment();

        return fragment;
    }

    public SchedulersFragment() {
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
        ((HomeDrawerActivity) getActivity()).setTitle("Schedulers");
        ((HomeDrawerActivity) getActivity()).hideAppBarButton();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        emptyView = (LinearLayout) view.findViewById(R.id.emptyView);
        txtEmptyView1 = (TextView) view.findViewById(R.id.txtEmptyView1);
        txtEmptyView = (TextView) view.findViewById(R.id.txtEmptyView);
        imgEmpty = (ImageView) view.findViewById(R.id.imgEmpty);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(layoutManager);
        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), getActivity());
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        try {
            dbHelper.openDataBase();
            schedulersCursor =  dbHelper.getAllSchedulers();
            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(schedulersCursor.getCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            imgEmpty.setVisibility(View.VISIBLE);
            imgEmpty.setImageResource(R.drawable.drawer_schedulers);
            txtEmptyView.setText(getResources().getString(R.string.empty_scehdulers_list));
        } else {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }


        adapter = new SchedulersListCursorAdapter(getActivity(), schedulersCursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);

        callOnDeleteClick();
        callRenameOnClick();

        adapter.setOnDefaultValueChanged(new onMachineStateChangeListener() {
            @Override
            public void onMachineEnabledDisabled(int pos, boolean isEnabled) {
                schedulersCursor.moveToPosition(pos);
                String schedulerId = schedulersCursor.getString(schedulersCursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_ID));

                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                try {
                    dbHelper.openDataBase();
                    dbHelper.updateSchedulerDefaultOnOffValue(isEnabled, schedulerId);
                    schedulersCursor = dbHelper.getAllSchedulers();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

              /*  adapter = new SchedulersListCursorAdapter(SchedulersListActivity.this, schedulersCursor);
                adapter.setHasStableIds(true);
                mRecyclerView.setAdapter(adapter);*/
                adapter.changeCursor(schedulersCursor);
                adapter.notifyDataSetChanged();
                dbHelper.close();

                /*callDeleteClick();
                callOnDefaultToggle();*/
            }
        });
    }

    private void callOnDeleteClick() {
        adapter.setDeleteClickListener(new onDeleteClickListener() {
            @Override
            public void onDeleteOptionClick(final int pos) {
                SaveAlertDialog saveAlertDialog = new SaveAlertDialog(getActivity(), "Are you sure you want to delete the scheduler?");
                saveAlertDialog.show();

                saveAlertDialog.setSaveListener(new onSaveClickListener() {
                    @Override
                    public void onSaveClick(boolean isSave) {
                        if (isSave) {
                            deleteScheduler(pos);
                        }
                    }
                });
            }
        });
    }

    private void callRenameOnClick() {
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


                EditSchedulerDialog addToSchedulerDialog = new EditSchedulerDialog(getActivity(), model);
                addToSchedulerDialog.show();

                addToSchedulerDialog.setOnSingleClick(new onSingleClickListener() {
                    @Override
                    public void onSingleClick(int pos) {
                        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

                        try {
                            dbHelper.openDataBase();
                            schedulersCursor = dbHelper.getAllSchedulers();

                            if (schedulersCursor.getCount() == 0) {
                                emptyView.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                                imgEmpty.setVisibility(View.VISIBLE);
                                imgEmpty.setImageResource(R.drawable.drawer_schedulers);
                                txtEmptyView.setText(getResources().getString(R.string.empty_scehdulers_list));
                            } else {
                                emptyView.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                            }

                            /*adapter = new SchedulersListCursorAdapter(getActivity(), schedulersCursor);
                            adapter.setHasStableIds(true);
                            mRecyclerView.setAdapter(adapter);*/
                            dbHelper.close();

                            adapter.changeCursor(schedulersCursor);
                            adapter.notifyDataSetChanged();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onRenameOptionClick(int pos, String oldName, String oldDetails) {

            }
        });
    }

    private void deleteScheduler(int pos) {
        schedulersCursor.moveToPosition(pos);

        String schedulerId = schedulersCursor.getString(schedulersCursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_ID));
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        try {
            dbHelper.openDataBase();
            dbHelper.deleteScheduler(schedulerId);
            schedulersCursor = dbHelper.getAllSchedulers();

            if(schedulersCursor.getCount() == 0) {
                emptyView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                imgEmpty.setVisibility(View.VISIBLE);
                imgEmpty.setImageResource(R.drawable.drawer_schedulers);
                txtEmptyView.setText(getResources().getString(R.string.empty_scehdulers_list));
            } else {
                emptyView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }


            adapter = new SchedulersListCursorAdapter(getActivity(), schedulersCursor);
            adapter.setHasStableIds(true);
            mRecyclerView.setAdapter(adapter);
            dbHelper.close();

            callOnDeleteClick();
            callRenameOnClick();

            Toast.makeText(getActivity(), "Scheduler Deleted.", Toast.LENGTH_SHORT).show();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
