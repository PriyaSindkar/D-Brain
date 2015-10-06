package com.webmyne.android.d_brain.ui.Activities;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konifar.fab_transformation.FabTransformation;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.TouchPComponentListAdapter;
import com.webmyne.android.d_brain.ui.Adapters.TouchPanelItemListAdapter;
import com.webmyne.android.d_brain.ui.Listeners.OnPaneItemClickListener;
import com.webmyne.android.d_brain.ui.Listeners.OnPaneItemDeleteListener;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.Widgets.TouchPanelBox;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by priyasindkar on 02-10-2015.
 */
public class TouchPanelActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private TextView toolbarTitle, txtDisplayPanelName, listComponentsEmptyView, panelItemsListEmptyView;
    private LinearLayout linearTouchPanelItems, panelSetLayout,linearAddComponents, linearAddCancel;
    private RelativeLayout linearPanelList, linearComponentList;
    private FrameLayout rightParent;
    private ListView listComponents, panelItemsList;
    private Cursor touchPanelListCursor, switchListCursor;
    private TouchPComponentListAdapter componentAdapter;
    private View divider;
    private FloatingActionButton fab;
    private Button btnAddSwitch, btnAddDimmer, btnAddMotor, btnAdd, btnCancel;

    private int selectedPanelPosition;
    private String selectedPanelId;

    private TouchPanelItemListAdapter touchPanelItemListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_panel_list);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Touch Panel");

        txtDisplayPanelName = (TextView) findViewById(R.id.txtDisplayPanelName);
        linearTouchPanelItems = (LinearLayout) findViewById(R.id.linearTouchPanelItems);

        linearAddComponents = (LinearLayout) findViewById(R.id.linearAddComponents);
        panelItemsList = (ListView) findViewById(R.id.panelItemsList);

        rightParent = (FrameLayout) findViewById(R.id.rightParent);
        panelSetLayout = (LinearLayout) findViewById(R.id.panelSetLayout);
        divider = findViewById(R.id.divider);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        btnAddSwitch = (Button) findViewById(R.id.btnAddSwitch);
        btnAddDimmer = (Button) findViewById(R.id.btnAddDimmer);
        btnAddMotor = (Button) findViewById(R.id.btnAddMotor);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        linearAddCancel = (LinearLayout) findViewById(R.id.linearAddCancel);

        linearPanelList = (RelativeLayout) findViewById(R.id.linearPanelList);

        btnAddSwitch.setOnClickListener(this);
        btnAddDimmer.setOnClickListener(this);
        btnAddMotor.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        linearComponentList = (RelativeLayout) findViewById(R.id.linearComponentList);
        listComponents = (ListView) findViewById(R.id.listComponents);
        listComponentsEmptyView = (TextView) findViewById(R.id.listComponentsEmptyView);
        listComponents.setEmptyView(listComponentsEmptyView);

        panelItemsListEmptyView = (TextView) findViewById(R.id.panelItemsListEmptyView);
        panelItemsList.setEmptyView(panelItemsListEmptyView);

        initArrayOfTouchPanels();
        initTouchPanelList();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FabTransformation.with(fab)
                        .transformTo(linearAddComponents);
                componentAdapter.init();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //populate the switch component list
        initArrayOfSwitches();
    }

    private void initTouchPanelList() {

        linearTouchPanelItems.removeAllViews();

        if (touchPanelListCursor != null) {
            touchPanelListCursor.moveToFirst();
                if (touchPanelListCursor.getCount() > 0) {
                    do {
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.touch_panel_box, null);

                        final String touchPanelName = touchPanelListCursor.getString(touchPanelListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
                        final String touchPanelId = touchPanelListCursor.getString(touchPanelListCursor.getColumnIndexOrThrow(DBConstants.KEY_S_ID));

                        final TextView txtPanelHeading = (TextView) view.findViewById(R.id.txtPanelHeading);
                        txtPanelHeading.setText(touchPanelName);

                        final TouchPanelBox touchPanelBox = (TouchPanelBox) view.findViewById(R.id.touchPanelBox);
                        touchPanelBox.setPanelId(touchPanelId);
                        touchPanelBox.setUpTouchBox();

                        touchPanelBox.setOnPanelItemClickListner(new OnPaneItemClickListener() {
                            @Override
                            public void onPanelItemSelection(String panelName, int positionInPanel, String panelId) {
                                selectedPanelId = panelId;
                                selectedPanelPosition = positionInPanel;


                                DatabaseHelper dbHelper = new DatabaseHelper(TouchPanelActivity.this);

                                //get all touch panel item components
                                try {
                                    dbHelper.openDataBase();
                                    Cursor savedPanelItemComponents = dbHelper.getPanelItemComponents(panelId, positionInPanel);
                                    touchPanelItemListAdapter = new TouchPanelItemListAdapter(TouchPanelActivity.this, R.layout.touch_panel_item_row,
                                            savedPanelItemComponents, new String[]{DBConstants.KEY_TP_ITEM_COMPONENT_NAME},
                                            new int[]{R.id.txtSwitchName});

                                    panelItemsList.setAdapter(touchPanelItemListAdapter);

                                    dbHelper.close();

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                touchPanelBox.setSelection(panelName);
                                txtDisplayPanelName.setText(touchPanelName + "\nPos: " + positionInPanel);

                                panelSetLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                rightParent.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                rightParent.setVisibility(View.VISIBLE);
                                divider.setVisibility(View.VISIBLE);

                                linearPanelList.setVisibility(View.VISIBLE);

                            }

                        });

                        linearTouchPanelItems.addView(view);

                    } while (touchPanelListCursor.moveToNext());
                }
        }
    }

    private void initArrayOfTouchPanels() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //get all touch panel boxes configured
        try {
            dbHelper.openDataBase();
            touchPanelListCursor =  dbHelper.getAllTouchPanelBoxes();
            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnAddSwitch:
                linearAddComponents.setVisibility(View.INVISIBLE);
                linearAddCancel.setVisibility(View.VISIBLE);
                linearComponentList.setVisibility(View.VISIBLE);
                linearPanelList.setVisibility(View.GONE);

                // populate switch components
                listComponents.setAdapter(componentAdapter);

                break;

            case R.id.btnAddDimmer:
                break;

            case R.id.btnAddMotor:
                break;

            case R.id.btnAdd:

                linearPanelList.setVisibility(View.VISIBLE);
                linearComponentList.setVisibility(View.INVISIBLE);

                fab.setVisibility(View.GONE);
                FabTransformation.with(fab)
                        .transformFrom(linearAddCancel);

                //add components to panel item
                Log.e("Selected COMPs", componentAdapter.getSelectedComponents().toString());
                addPanelItem(componentAdapter.getSelectedComponents());

                break;

            case R.id.btnCancel:
                linearPanelList.setVisibility(View.VISIBLE);
                linearComponentList.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.GONE);
                FabTransformation.with(fab)
                        .transformFrom(linearAddCancel);
                break;
        }
    }

    private void initArrayOfSwitches() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //insert switches in adapter ofr machine-1
        try {
            dbHelper.openDataBase();
            switchListCursor =  dbHelper.getAllSwitchComponentsForAMachine(DBConstants.MACHINE1_IP);
            dbHelper.close();

            componentAdapter = new TouchPComponentListAdapter(TouchPanelActivity.this, R.layout.touchp_component_list_item,
                  switchListCursor,  new String[] {DBConstants.KEY_C_NAME,
                     DBConstants.KEY_C_MNAME }, new int[] { R.id.txtSwitchName, R.id.txtMachineName});

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  add the selected components in panel item
    private void addPanelItem(ArrayList<String> selectedComonentList) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.openDataBase();

            for(int i=0; i<selectedComonentList.size(); i++) {
                ComponentModel componentModel =  dbHelper.getComponentById(selectedComonentList.get(i));
                dbHelper.insertIntoPanelItem(componentModel, selectedPanelId, selectedPanelPosition);
            }

            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /*@Override
    public void onPanelItemDeletion(String panelId, int positionInPanel, String componentId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //get all touch panel item components
        try {
            dbHelper.openDataBase();
            dbHelper.deletePanelItemComponents(panelId, positionInPanel, componentId);
            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        touchPanelItemListAdapter.notifyDataSetChanged();
    }*/

}