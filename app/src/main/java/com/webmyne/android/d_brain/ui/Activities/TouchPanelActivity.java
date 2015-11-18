package com.webmyne.android.d_brain.ui.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
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
import com.webmyne.android.d_brain.ui.Adapters.TouchPanelGridAdapter;
import com.webmyne.android.d_brain.ui.Adapters.TouchPanelItemListAdapter;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Listeners.OnPaneItemClickListener;
import com.webmyne.android.d_brain.ui.Listeners.OnPaneItemDeleteListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.Model.Machine;
import com.webmyne.android.d_brain.ui.Widgets.TouchPanelBox;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;
import com.webmyne.android.d_brain.ui.dbHelpers.Functions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by priyasindkar on 02-10-2015.
 */
public class TouchPanelActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private TextView toolbarTitle, txtDisplayPanelName, listComponentsEmptyView, panelItemsListEmptyView, txtComponentListHeading;
    private LinearLayout linearTouchPanelItems, linearSaveTouchPanel;
    private RelativeLayout panelSetLayout;

    private Cursor touchPanelListCursor, switchListCursor, dimmerListCursor, motorListCursor;
    //private TouchPComponentListAdapter componentAdapter;
    private ListView  panelItemsList;

    private ImageView imgBack, imgChangeMachine;
    AlertDialog levelDialog;
    private int selectedPanelPosition;
    private String selectedPanelId, selectedComponentId;

    private TouchPanelItemListAdapter touchPanelItemListAdapter;

    private String CURRENT_MACHINE_IP, CURRENT_MACHINE_NAME;
    private int CURRENT_MACHINE_ID, DEFAULT_MACHINE;
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
        panelSetLayout = (RelativeLayout) findViewById(R.id.panelSetLayout);
        leftParent = (RelativeLayout)findViewById(R.id.leftParent);
        txtComponentListHeading = (TextView) findViewById(R.id.txtComponentListHeading);

        linearSaveTouchPanel = (LinearLayout) findViewById(R.id.linearSaveTouchPanel);

        panelItemsList = (ListView) findViewById(R.id.panelItemsList);
      //  panelItemsListEmptyView = (TextView) findViewById(R.id.panelItemsListEmptyView);
      //  panelItemsList.setEmptyView(panelItemsListEmptyView);

        //intialize the left component panel
        View leftPanel = leftParent;
        gridComponent = (GridView)leftPanel.findViewById(R.id.gridComponent);
        linearSwitch = (LinearLayout)leftPanel.findViewById(R.id.linearSwitch);
        linearDimmer = (LinearLayout)leftPanel.findViewById(R.id.linearDimmer);
        linearMotor = (LinearLayout)leftPanel.findViewById(R.id.linearMotor);

        linearSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setComponentGrid(0);
            }
        });

        linearDimmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setComponentGrid(1);
            }
        });

        linearMotor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // setComponentGrid(2);
            }
        });

        linearSaveTouchPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> defaultPayLoad = new HashMap<String, String>();
                //ArrayList<String> defaultPayLoad = new ArrayList<String>();
                for(int i=0; i<6; i++) {
                    defaultPayLoad.put(i+"", "0000");
                }

                Log.e("defaultPayLoad: ", "" + defaultPayLoad.toString());
                Log.e("Machine Selected: ", ""+CURRENT_MACHINE_ID);
                Log.e("Component Selected: ", selectedComponentId);

                for(int i=0;i<linearTouchPanelItems.getChildCount();i++){
                    View view = linearTouchPanelItems.getChildAt(i);
                    TouchPanelBox touchPanelBox = (TouchPanelBox) view.findViewById(R.id.touchPanelBox);

                    ArrayList<String> selectedValues = touchPanelBox.getSelectedValues();

                   // Log.e("Panel Selected: ", selectedValues.toString());

                    //for(int t=0; t<6;t++) {
                        if( !selectedValues.isEmpty()) {
                            Log.e("Panel No: ", touchPanelBox.getPanelId());
                            for (int j = 0; j < selectedValues.size(); j++) {
                                String tpId = touchPanelBox.getPanelId();
                                String tpPosition = selectedValues.get(j);
                                defaultPayLoad.put(j+"", String.format("%02d", Integer.parseInt(tpId)) + String.format("%02d", Integer.parseInt(tpPosition)));
                                //Log.e("Panel POs Selected: ", selectedValues.get(j));
                            }
                        }
                   // }
                }
                Log.e("finalPayLoad: ", "" + defaultPayLoad.toString());
                Toast.makeText(TouchPanelActivity.this, "SAved", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setComponentGrid(int componentType){
        // 0 - Switch
        // 1 - Dimmer
        // 2 - Motor

        if(componentType == 0) {
            // for switch

            linearTouchPanelItems.removeAllViews();
            ArrayList<ComponentModel> switches = new ArrayList<>();
            switchListCursor.moveToFirst();
            do {
                ComponentModel componentModel = new ComponentModel();
                String componentPrimaryId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
                String swName = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
                componentModel.setId(Integer.parseInt(componentPrimaryId));
                componentModel.setName(swName);
                switches.add(componentModel);

            } while (switchListCursor.moveToNext());

            final TouchPanelGridAdapter gridAdapter = new TouchPanelGridAdapter(TouchPanelActivity.this, switches);
            gridComponent.setNumColumns(3);
            gridComponent.setAdapter(gridAdapter);
            //gridComponent.setOnItemClickListener(gridItemClickListener);

            gridAdapter.setOnSingleClickListener(new onSingleClickListener() {
                @Override
                public void onSingleClick(int pos) {
                    //setting up touch panel
                    initTouchPanelList();

                    selectedComponentId = String.valueOf(pos);
                    Toast.makeText(TouchPanelActivity.this,"Clicked "+ selectedComponentId, Toast.LENGTH_SHORT).show();
                }
            });


            int orgColor = Color.parseColor("#18516e");
            int SelectedColor = Color.parseColor("#009987");

            linearSwitch.setBackgroundColor(SelectedColor);
            linearDimmer.setBackgroundColor(orgColor);
            linearMotor.setBackgroundColor(orgColor);

        }else if(componentType == 1){
            // for dimmer
            ArrayList<String> dimmerNames = new ArrayList<>();
            dimmerListCursor.moveToFirst();
            do {
                String swName = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
                dimmerNames.add(swName);

            } while (dimmerListCursor.moveToNext());

            /*TouchPanelGridAdapter gridAdapter = new TouchPanelGridAdapter(TouchPanelActivity.this, dimmerNames);
            gridComponent.setNumColumns(3);
            gridComponent.setAdapter(gridAdapter);*/
          //  gridComponent.setOnItemClickListener(gridItemClickListener);

            int orgColor = Color.parseColor("#18516e");
            int SelectedColor = Color.parseColor("#009987");
            linearDimmer.setBackgroundColor(SelectedColor);
            linearSwitch.setBackgroundColor(orgColor);
            linearMotor.setBackgroundColor(orgColor);

        }else  if(componentType == 2){
                // for motors
        }


    }


   /* AdapterView.OnItemClickListener gridItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(TouchPanelActivity.this,"Clicked "+position,Toast.LENGTH_SHORT).show();
            ((LinearLayout)view).findViewById(R.id.itemLinear).setBackgroundResource(R.drawable.touch_panel_selected);
        }
    };*/



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

                // Setting the default tab like switch or dimmer or motor
                String dpc = MACHINES.get(DEFAULT_MACHINE).getMachineProductCode();

                if(Functions.isSwitchAvialabel(dpc))
                    setComponentGrid(0);
                else if(Functions.isSwitchAvialabel(dpc))
                    setComponentGrid(1);
                else if(Functions.isMotorAvialabel(dpc))
                    setComponentGrid(2);

                /*//setting up touch panel
                initTouchPanelList();*/
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
                    initArrayOfDimmers(DEFAULT_MACHINE);
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

              /*  if(Functions.isMotorAvialabel(MACHINES.get(DEFAULT_MACHINE).getMachineProductCode())) {
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
                }*/





                return null;
            }
        }.execute();
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
                    final String touchPanelId = touchPanelListCursor.getString(touchPanelListCursor.getColumnIndexOrThrow(DBConstants.KEY_TP_ITEM_ID));

                    final TextView txtPanelHeading = (TextView) view.findViewById(R.id.txtPanelHeading);
                    txtPanelHeading.setText(touchPanelName);

                    final TouchPanelBox touchPanelBox = (TouchPanelBox) view.findViewById(R.id.touchPanelBox);
                    Log.e("TAG_NEW_PANEL", touchPanelId);
                    touchPanelBox.setPanelId(touchPanelId);
                    touchPanelBox.setUpTouchBox();
                    touchPanelBox.setOnPanelItemClickListner(onPaneItemClickListener);
                    linearTouchPanelItems.addView(view);

                } while (touchPanelListCursor.moveToNext());
            }
        }
    }

    public  OnPaneItemClickListener onPaneItemClickListener = new OnPaneItemClickListener() {
        @Override
        public void onPanelItemSelection(TouchPanelBox touchPanelBox, String oldName, int positionInPanel, String panelId) {

            if(touchPanelBox.getSelectedValues().contains(String.valueOf(positionInPanel))){
                touchPanelBox.setSelection(oldName);
            }else{
                if(checkCount()){
                    selectedPanelId = panelId;
                    selectedPanelPosition = positionInPanel;
                    touchPanelBox.setSelection(oldName);
                }else{
                  //  todo toast for greater then 6
                }
            }




        }


    };

    private boolean checkCount() {
        int count = 0;

        for(int i=0;i<linearTouchPanelItems.getChildCount();i++){
            View view = linearTouchPanelItems.getChildAt(i);
            TouchPanelBox touchPanelBox = (TouchPanelBox) view.findViewById(R.id.touchPanelBox);
            int temp = touchPanelBox.getSelectedValues().size();
            count = count + temp;
        }

        return  (count>5)?false:true;

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

        //get all touch panel boxes configured for a particular machine
        try {
            dbHelper.openDataBase();
            touchPanelListCursor =  dbHelper.getAllTouchPanelBoxesByMachineId(CURRENT_MACHINE_ID);
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

    private void initArrayOfDimmers(int defaultMachine) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //insert switches in adapter ofr machine-1
        try {
            dbHelper.openDataBase();
            String defaultMachineIp = MACHINES.get(defaultMachine).getMachineIp();
            dimmerListCursor =  dbHelper.getAllDimmerComponentsForAMachine(defaultMachineIp);
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
                                linearTouchPanelItems.removeAllViews();
                               /* //setting up touch panel
                                initTouchPanelList();*/
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
