package com.webmyne.android.d_brain.ui.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.animation.SlideEnter.SlideRightEnter;
import com.konifar.fab_transformation.FabTransformation;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.TouchPComponentListAdapter;
import com.webmyne.android.d_brain.ui.Adapters.TouchPanelItemListAdapter;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Listeners.OnPaneItemClickListener;
import com.webmyne.android.d_brain.ui.Listeners.OnPaneItemDeleteListener;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.Model.Machine;
import com.webmyne.android.d_brain.ui.Widgets.TouchPanelBox;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;
import com.webmyne.android.d_brain.ui.dbHelpers.Functions;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by priyasindkar on 02-10-2015.
 */
public class TouchPanelActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private TextView toolbarTitle, txtDisplayPanelName, listComponentsEmptyView, panelItemsListEmptyView, txtComponentListHeading;
    private LinearLayout linearTouchPanelItems, panelSetLayout;

    private Cursor touchPanelListCursor, switchListCursor, dimmerListCursor, motorListCursor;
    private TouchPComponentListAdapter componentAdapter;


    private ImageView imgBack, imgChangeMachine;
    AlertDialog levelDialog;
    private int selectedPanelPosition;
    private String selectedPanelId;

    private TouchPanelItemListAdapter touchPanelItemListAdapter;

    private String CURRENT_MACHINE_IP,CURRENT_MACHINE_NAME;
    private int CURRENT_MACHINE_ID,DEFAULT_MACHINE;
    private ArrayList<Machine> MACHINES;

    private RelativeLayout leftParent;

    GridView gridComponent;
   LinearLayout linearSwitch,linearDimmer,linearMotor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_panel_list);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgChangeMachine = (ImageView) findViewById(R.id.imgChangeMachine);
        imgBack.setOnClickListener(this);
        imgChangeMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMachinePopup();
            }
        });

        init();

    }

    private void init(){
        txtDisplayPanelName = (TextView) findViewById(R.id.txtDisplayPanelName);
        linearTouchPanelItems = (LinearLayout) findViewById(R.id.linearTouchPanelItems);
        panelSetLayout = (LinearLayout) findViewById(R.id.panelSetLayout);
        leftParent = (RelativeLayout)findViewById(R.id.leftParent);
        txtComponentListHeading = (TextView) findViewById(R.id.txtComponentListHeading);


        //intialize the left component panel
        View leftPanel = leftParent;
        gridComponent = (GridView)leftPanel.findViewById(R.id.gridComponent);
        linearSwitch  = (LinearLayout)leftPanel.findViewById(R.id.linearSwitch);
        linearDimmer  = (LinearLayout)leftPanel.findViewById(R.id.linearDimmer);
        linearMotor  = (LinearLayout)leftPanel.findViewById(R.id.linearMotor);

    }

    private void setComponentGrid(){



    }

    private void initData(){

        final ProgressDialog progressDialog = ProgressDialog.show(TouchPanelActivity.this, "", "Setting up touch panel configuration...");
        progressDialog.setCancelable(false);

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();

                setComponentGrid();
            }

            @Override
            protected Void doInBackground(Void... params) {

                //populate the each component list
                initArrayOfTouchPanels();

                if(Functions.isSwitchAvialabel(MACHINES.get(DEFAULT_MACHINE).getMachineProductCode())) {
                    initArrayOfSwitches(DEFAULT_MACHINE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearSwitch.setVisibility(View.VISIBLE);
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearSwitch.setVisibility(View.GONE);
                        }
                    });
                }


                if(Functions.isDimmerAvialabel(MACHINES.get(DEFAULT_MACHINE).getMachineProductCode())) {
                    initArrayOfDimmers();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearDimmer.setVisibility(View.VISIBLE);
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearDimmer.setVisibility(View.GONE);
                        }
                    });
                }

                if(Functions.isMotorAvialabel(MACHINES.get(DEFAULT_MACHINE).getMachineProductCode())) {
                    initArrayOfMotors();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearMotor.setVisibility(View.VISIBLE);
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearMotor.setVisibility(View.GONE);
                        }
                    });
                }





                return null;
            }
        }.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConstants.getCurrentSsid(TouchPanelActivity.this);
        getMachine();

        initData();

    }


    private void getMachine(){
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //get all touch panel boxes configured
        try {
            dbHelper.openDataBase();
            Cursor machineCursor =  dbHelper.getAllMachines();

            MACHINES = new ArrayList<>();
            machineCursor.moveToFirst();

            do {
                Machine machineItem = new Machine();

                String CURRENT_MACHINE_NAME = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));
                int CURRENT_MACHINE_ID = machineCursor.getInt(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                String CURRENT_MACHINE_IP = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP));
                String deviceProductCode = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_PRODUCTCODE));

                machineItem.setMachineId(CURRENT_MACHINE_ID);
                machineItem.setMachineIp(CURRENT_MACHINE_IP);
                machineItem.setMachineName(CURRENT_MACHINE_NAME);
                machineItem.setMachineProductCode(deviceProductCode);

                MACHINES.add(machineItem);

            }while (machineCursor.moveToNext());

            if(machineCursor.getCount()>1){
                imgChangeMachine.setVisibility(View.VISIBLE);
            }else{
                imgChangeMachine.setVisibility(View.GONE);
            }

           // Set Default machine
            setDefaultMachine(0);

            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setDefaultMachine(int counter){

        DEFAULT_MACHINE = counter;

        CURRENT_MACHINE_ID = MACHINES.get(counter).getMachineId();
        CURRENT_MACHINE_IP = MACHINES.get(counter).getMachineIp();
        CURRENT_MACHINE_NAME = MACHINES.get(counter).getMachineName();

        toolbarTitle.setText(CURRENT_MACHINE_NAME);

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

               case R.id.imgBack:
                finish();
                break;
        }
    }

    private void initArrayOfSwitches(int defaultMachine) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //insert switches in adapter ofr machine-1
        try {
            dbHelper.openDataBase();

            String defaultMachineIp = MACHINES.get(defaultMachine).getMachineIp();
            switchListCursor =  dbHelper.getAllSwitchComponentsForAMachine(defaultMachineIp);

            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initArrayOfDimmers() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //insert switches in adapter ofr machine-1
        try {
            dbHelper.openDataBase();
            dimmerListCursor =  dbHelper.getAllDimmerComponents();
            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initArrayOfMotors() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //insert switches in adapter ofr machine-1
        try {
            dbHelper.openDataBase();
            motorListCursor =  dbHelper.getAllMotorComponents();
            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void showMachinePopup(){
        final PopupMenu popup = new PopupMenu(TouchPanelActivity.this, imgChangeMachine,R.style.PopupMenu);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_changemachine, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.chnangeMachine:

                        String [] machineItem = new String[MACHINES.size()];
                        for(int i=0;i<MACHINES.size();i++) {
                            machineItem[i] = MACHINES.get(i).getMachineName();
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(TouchPanelActivity.this);
                        builder.setTitle("Select Machine");
                        builder.setSingleChoiceItems(machineItem, DEFAULT_MACHINE, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int pos) {
                                levelDialog.dismiss();
                                setDefaultMachine(pos);
                                initData();
                            }
                        });
                        levelDialog = builder.create();
                        levelDialog.show();
                        break;

                }

                popup.dismiss();
                return true;
            }
        });

        popup.show();//showing popup menu
    }

//end of main class
}
