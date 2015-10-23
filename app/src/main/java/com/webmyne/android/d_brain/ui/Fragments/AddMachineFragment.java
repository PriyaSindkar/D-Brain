package com.webmyne.android.d_brain.ui.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.MachineListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.sql.SQLException;

import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class AddMachineFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MachineListCursorAdapter adapter;
    private int totalNoOfMachines = 2;
    private TextView txtEmptyView, txtEmptyView1;
    private LinearLayout emptyView;
    private Cursor machineCursor;

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

        /*adapter.setSingleClickListener(new onSingleClickListener() {
            @Override
            public void onSingleClick(int pos) {
                //Toast.makeText(DimmerListActivity.this, "Single Click Item Pos: " + pos, Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setLongClickListener(new onLongClickListener() {

            @Override
            public void onLongClick(int pos) {
                Toast.makeText(getActivity(), "Options Will Open Here", Toast.LENGTH_SHORT).show();
            }
        });*/

        adapter.setDeleteClickListener(new onDeleteClickListener() {
            @Override
            public void onDeleteOptionClick(int pos) {
                Toast.makeText(getActivity(), "Deleted Sccessful!", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setRenameClickListener(new onRenameClickListener() {

            @Override
            public void onRenameOptionClick(int pos, String _oldName) {
                machineCursor.moveToPosition(pos);
                final String machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));

                RenameDialog renameDialog = new RenameDialog(getActivity(), _oldName);
                renameDialog.show();

                renameDialog.setRenameListener(new onRenameClickListener() {
                    @Override
                    public void onRenameOptionClick(int pos, String newName) {
                        try {
                            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
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

        
        return view;
    }

    private void init(View view) {
        ((HomeDrawerActivity) getActivity()).setTitle("Add Machine");
        ((HomeDrawerActivity) getActivity()).showAppBarButton();
        ((HomeDrawerActivity) getActivity()).setClearButtonText("Add Machine");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        emptyView = (LinearLayout) view.findViewById(R.id.emptyView);
        txtEmptyView1 = (TextView) view.findViewById(R.id.txtEmptyView1);
        txtEmptyView = (TextView) view.findViewById(R.id.txtEmptyView);


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

}
