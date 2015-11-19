package com.webmyne.android.d_brain.ui.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.TouchPanelGridAdapter;
import com.webmyne.android.d_brain.ui.Adapters.TouchPanelItemListAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.MachineUnAvailableAlertDialog;
import com.webmyne.android.d_brain.ui.Listeners.OnPaneItemClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onTouchPanelSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.Model.Machine;
import com.webmyne.android.d_brain.ui.Model.TouchPanelSwitchModel;
import com.webmyne.android.d_brain.ui.Widgets.TouchPanelBox;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;
import com.webmyne.android.d_brain.ui.dbHelpers.Functions;
import com.webmyne.android.d_brain.ui.xmlHelpers.MainXmlPullParser;
import com.webmyne.android.d_brain.ui.xmlHelpers.XMLValues;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private Cursor touchPanelListCursor, switchListCursor, dimmerListCursor, motorListCursor, currentComponentAssignmentCursor;
    //private TouchPComponentListAdapter componentAdapter;
    private ListView  panelItemsList;

    private ImageView imgBack, imgChangeMachine;
    AlertDialog levelDialog;
    private int selectedPanelPosition;
    private String selectedPanelId, selectedComponentId = "", selectedComponentPrimaryId = "";

    private TouchPanelItemListAdapter touchPanelItemListAdapter;

    private String CURRENT_MACHINE_IP, CURRENT_MACHINE_NAME;
    private int CURRENT_MACHINE_ID, DEFAULT_MACHINE, SELECTED_COMPONENT_TYPE = 0;
    private ArrayList<Machine> MACHINES;

    private RelativeLayout leftParent;
    private ProgressDialog progress_dialog;
    private ArrayList<XMLValues> touchPanelStatusList = new ArrayList<>();

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

        //intialize the left component panel
        View leftPanel = leftParent;
        gridComponent = (GridView)leftPanel.findViewById(R.id.gridComponent);
        linearSwitch = (LinearLayout)leftPanel.findViewById(R.id.linearSwitch);
        linearDimmer = (LinearLayout)leftPanel.findViewById(R.id.linearDimmer);
        linearMotor = (LinearLayout)leftPanel.findViewById(R.id.linearMotor);

        progress_dialog = new ProgressDialog(this);
        progress_dialog.setCancelable(false);

        linearSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setComponentGrid(0);
            }
        });

        linearDimmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setComponentGrid(1);
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
                for (int i = 0; i < 6; i++) {
                    defaultPayLoad.put(i + "", "0000");
                }

                int t = 0; // counter for payload

                for (int i = 0; i < linearTouchPanelItems.getChildCount(); i++) {
                    View view = linearTouchPanelItems.getChildAt(i);
                    TouchPanelBox touchPanelBox = (TouchPanelBox) view.findViewById(R.id.touchPanelBox);
                    String tpId = String.valueOf(i + 1);
                    ArrayList<String> selectedValues = touchPanelBox.getSelectedValues();

                    if (!selectedValues.isEmpty()) {
                        for (int j = 0; j < selectedValues.size(); j++) {
                            String tpPosition = selectedValues.get(j);
                            defaultPayLoad.put(t + "", String.format("%02d", Integer.parseInt(tpId)) + String.format("%02d", Integer.parseInt(tpPosition)));
                            t++;
                        }
                    }
                }

                TouchPanelSwitchModel touchPanelSwitchModel = new TouchPanelSwitchModel();
                touchPanelSwitchModel.setPos1(defaultPayLoad.get(String.valueOf(0)));
                touchPanelSwitchModel.setPos2(defaultPayLoad.get(String.valueOf(1)));
                touchPanelSwitchModel.setPos3(defaultPayLoad.get(String.valueOf(2)));
                touchPanelSwitchModel.setPos4(defaultPayLoad.get(String.valueOf(3)));
                touchPanelSwitchModel.setPos5(defaultPayLoad.get(String.valueOf(4)));
                touchPanelSwitchModel.setPos6(defaultPayLoad.get(String.valueOf(5)));


                StringBuilder payLoad = new StringBuilder();
                for (int i = 0; i < 6; i++) {
                    payLoad.append(defaultPayLoad.get(String.valueOf(i)));
                }
                touchPanelSwitchModel.setMid(String.valueOf(CURRENT_MACHINE_ID));
                touchPanelSwitchModel.setComponentName(AppConstants.TOUCHPANEL_SWITCH_PREFIX + selectedComponentId);
                touchPanelSwitchModel.setPayLoad(payLoad.toString());

                if(SELECTED_COMPONENT_TYPE == 0) { //switches
                    touchPanelSwitchModel.setComponentType(AppConstants.SWITCH_TYPE);
                } else if(SELECTED_COMPONENT_TYPE == 1){ //dimmers
                    touchPanelSwitchModel.setComponentType(AppConstants.DIMMER_TYPE);
                } else if(SELECTED_COMPONENT_TYPE == 2){ //motors
                    touchPanelSwitchModel.setComponentType(AppConstants.MOTOR_TYPE);
                }


                // payload 24-bytes with component-position prefix to send with webservice
                String urlPayLoad = selectedComponentId.substring(2,4) + payLoad.toString();

                // update in db
                DatabaseHelper dbHelper = new DatabaseHelper(TouchPanelActivity.this);
                try {
                    dbHelper.openDataBase();
                    dbHelper.insertOrUpdateIntoPanelSwitch(touchPanelSwitchModel);
                    dbHelper.close();
                } catch (Exception e) {}

                // call change webservice
                String[] params = {urlPayLoad};
                new ChangeTouchPanelStatus().execute(params);

                Toast.makeText(TouchPanelActivity.this, "Switches Assigned.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setComponentGrid(int componentType){
        SELECTED_COMPONENT_TYPE = componentType;
        linearTouchPanelItems.removeAllViews();
        // 0 - Switch
        // 1 - Dimmer
        // 2 - Motor

        if(componentType == 0) {
            // for switch
            ArrayList<ComponentModel> switches = new ArrayList<>();
            switchListCursor.moveToFirst();
            do {
                ComponentModel componentModel = new ComponentModel();
                String componentPrimaryId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
                String componentId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                String swName = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
                componentModel.setId(Integer.parseInt(componentPrimaryId));
                componentModel.setName(swName);
                componentModel.setComponentId(componentId);
                switches.add(componentModel);

            } while (switchListCursor.moveToNext());

            final TouchPanelGridAdapter gridAdapter = new TouchPanelGridAdapter(TouchPanelActivity.this, switches);
            gridComponent.setNumColumns(3);
            gridComponent.setAdapter(gridAdapter);

            gridAdapter.setOnTouchPanelSingleClickListener(new onTouchPanelSingleClickListener() {
                @Override
                public void onTouchPanelSingleClick(String componentId, String componentPrimaryId) {
                    // save selected switch component id's (for db)
                    selectedComponentId = componentId;
                    selectedComponentPrimaryId = componentPrimaryId;

                    //setting up touch panel
                    new GetTouchPanelStatus().execute();
                }
            });

            linearSwitch.setBackgroundColor(getResources().getColor(R.color.baseButtonColor));
            linearDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            linearMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));

        }else if(componentType == 1){
            // for dimmer
            ArrayList<ComponentModel> dimmers = new ArrayList<>();
            dimmerListCursor.moveToFirst();
            do {
                ComponentModel componentModel = new ComponentModel();
                String componentPrimaryId = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
                String componentId = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                String swName = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
                componentModel.setId(Integer.parseInt(componentPrimaryId));
                componentModel.setName(swName);
                componentModel.setComponentId(componentId);
                dimmers.add(componentModel);

            } while (dimmerListCursor.moveToNext());

            final TouchPanelGridAdapter gridAdapter = new TouchPanelGridAdapter(TouchPanelActivity.this, dimmers);
            gridComponent.setNumColumns(3);
            gridComponent.setAdapter(gridAdapter);

            gridAdapter.setOnTouchPanelSingleClickListener(new onTouchPanelSingleClickListener() {
                @Override
                public void onTouchPanelSingleClick(String componentId, String componentPrimaryId) {
                    // save selected switch component id's (for db)
                    selectedComponentId = componentId;
                    selectedComponentPrimaryId = componentPrimaryId;

                    //setting up touch panel
                   // new GetTouchPanelStatus().execute();

                    initTouchPanelList(new HashMap<String, String>());
                }
            });

            linearDimmer.setBackgroundColor(getResources().getColor(R.color.baseButtonColor));
            linearSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            linearMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));

        }else  if(componentType == 2){
                // for motors
        }
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


    private void initTouchPanelList(HashMap<String, String> selectionMap) {
        linearTouchPanelItems.removeAllViews();

        if (touchPanelListCursor != null) {
            touchPanelListCursor.moveToFirst();
            if (touchPanelListCursor.getCount() > 0) {
                int i=1;
                do {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.touch_panel_box, null);

                    final String touchPanelName = touchPanelListCursor.getString(touchPanelListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
                    final String touchPanelId = touchPanelListCursor.getString(touchPanelListCursor.getColumnIndexOrThrow(DBConstants.KEY_TP_ITEM_ID));

                    final TextView txtPanelHeading = (TextView) view.findViewById(R.id.txtPanelHeading);
                    txtPanelHeading.setText(touchPanelName);

                    final TouchPanelBox touchPanelBox = (TouchPanelBox) view.findViewById(R.id.touchPanelBox);
                    touchPanelBox.setPanelPrimaryId(touchPanelId);
                    touchPanelBox.setPanelPositionInMachine(String.valueOf(i));
                    touchPanelBox.setUpTouchBox(selectionMap);

                    touchPanelBox.setOnPanelItemClickListner(onPaneItemClickListener);
                    linearTouchPanelItems.addView(view);
                    i++;
                } while (touchPanelListCursor.moveToNext());
            }
        }
    }

    public OnPaneItemClickListener onPaneItemClickListener = new OnPaneItemClickListener() {
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

    int timeOutErrorCounter = 0;
    boolean isTimeOutContinue = true;

    // fetch touch panel assignment
    public class GetTouchPanelStatus extends AsyncTask<Void, Void, Void> {
        String machineId="", machineName = "", machineIp, isMachineActive = "false";
        boolean isMachineUnAvailable = false;

        Cursor machineCursor, componentCursor;
        DatabaseHelper dbHelper = new DatabaseHelper(TouchPanelActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_dialog.setMessage("Please Wait..");
            progress_dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String machineBaseURL = "";

                if (CURRENT_MACHINE_IP.startsWith("http://")) {
                    machineBaseURL = CURRENT_MACHINE_IP;
                } else {
                    machineBaseURL = "http://" + CURRENT_MACHINE_IP;
                }

                // get current machine details
                dbHelper.openDataBase();
                machineCursor = dbHelper.getMachineByID(String.valueOf(CURRENT_MACHINE_ID));

                if(SELECTED_COMPONENT_TYPE == 0) {
                    componentCursor = dbHelper.getAllSwitchComponentsForTouchPanel(String.valueOf(CURRENT_MACHINE_ID));
                } else if(SELECTED_COMPONENT_TYPE == 1) {
                    componentCursor = dbHelper.getAllDimmerComponentsForTouchPanel(String.valueOf(CURRENT_MACHINE_ID));
                } else if(SELECTED_COMPONENT_TYPE == 2) {

                }

                isMachineActive = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));

                if(isMachineActive.equals("true")) {
                    // todo fetch dimmmer list from webservice (left from webservice side)
                    URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_TOUCH_PANEL_SWITCHLIST_STATUS);
                    Log.e("# url", urlValue.toString());

                    HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                    httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                    httpUrlConnection.setRequestMethod("GET");
                    InputStream inputStream = httpUrlConnection.getInputStream();

                    MainXmlPullParser pullParser = new MainXmlPullParser();
                    touchPanelStatusList = pullParser.processXML(inputStream);
                    Log.e("touchPanelStatusList", touchPanelStatusList.toString());

                    isTimeOutContinue = false;
                } else {
                    isMachineUnAvailable = true;
                    isTimeOutContinue = false;
                }

            } catch (Exception e) {
                Log.e("# ~~~~~timeout~~~~~", timeOutErrorCounter+"");
                //isMachineUnAvailable = true;
                if(timeOutErrorCounter < 10) {
                    timeOutErrorCounter ++;
                    isTimeOutContinue = true;
                } else {
                    isTimeOutContinue = false;
                    isMachineUnAvailable = true;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress_dialog.hide();

            if(isTimeOutContinue) {
                new GetTouchPanelStatus().execute();
            } else {
                timeOutErrorCounter = 0;
                if (isMachineUnAvailable) {
                    MachineUnAvailableAlertDialog machineUnAvailableAlertDialog = new MachineUnAvailableAlertDialog(TouchPanelActivity.this);
                    machineUnAvailableAlertDialog.show();

                    linearTouchPanelItems.removeAllViews();
                } else {

                    DatabaseHelper dbHelper = new DatabaseHelper(TouchPanelActivity.this);

                    //insert switches in adapter for machine-1
                    try {
                        dbHelper.openDataBase();
                        int totalTouchPanelCountInCurrentMachine = 0;
                        if (SELECTED_COMPONENT_TYPE == 0) { //switches
                            if (switchListCursor != null) {
                                totalTouchPanelCountInCurrentMachine = switchListCursor.getCount();
                            }
                        } else if (SELECTED_COMPONENT_TYPE == 1) { //dimmers
                            if (switchListCursor != null) {
                                totalTouchPanelCountInCurrentMachine = dimmerListCursor.getCount();
                            }
                        } else if (SELECTED_COMPONENT_TYPE == 2) { //motors
                            if (switchListCursor != null) {
                                totalTouchPanelCountInCurrentMachine = motorListCursor.getCount();
                            }
                        }


                        // input in DB the touch panel status for selected component type (switch/dimmer/motor)
                        for (int i = 0; i < totalTouchPanelCountInCurrentMachine; i++) {
                            TouchPanelSwitchModel touchPanelSwitchModel = new TouchPanelSwitchModel();
                            touchPanelSwitchModel.setMid(String.valueOf(CURRENT_MACHINE_ID));
                            touchPanelSwitchModel.setComponentName(touchPanelStatusList.get(i).tagName);
                            touchPanelSwitchModel.setPayLoad(touchPanelStatusList.get(i).tagValue);

                            String payload = touchPanelStatusList.get(i).tagValue;

                            touchPanelSwitchModel.setPos1(payload.substring(0, 4));
                            touchPanelSwitchModel.setPos2(payload.substring(4, 8));
                            touchPanelSwitchModel.setPos3(payload.substring(8, 12));
                            touchPanelSwitchModel.setPos4(payload.substring(12, 16));
                            touchPanelSwitchModel.setPos5(payload.substring(16, 20));
                            touchPanelSwitchModel.setPos6(payload.substring(20, 24));

                            if (SELECTED_COMPONENT_TYPE == 0) { //switches
                                touchPanelSwitchModel.setComponentType(AppConstants.SWITCH_TYPE);
                            } else if (SELECTED_COMPONENT_TYPE == 1) { //dimmers
                                touchPanelSwitchModel.setComponentType(AppConstants.DIMMER_TYPE);
                            } else if (SELECTED_COMPONENT_TYPE == 2) { //motors
                                touchPanelSwitchModel.setComponentType(AppConstants.MOTOR_TYPE);
                            }

                            dbHelper.insertOrUpdateIntoPanelSwitch(touchPanelSwitchModel);
                        }

                        // fetch touch panel status from db for showing initial assignment
                        if (!selectedComponentId.equals(""))
                            currentComponentAssignmentCursor = dbHelper.getSwitchAssignmentForAComponent(String.valueOf(CURRENT_MACHINE_ID), AppConstants.TOUCHPANEL_SWITCH_PREFIX + selectedComponentId);
                        dbHelper.close();

                        // initial assignment
                        initSelectionForAComponent(currentComponentAssignmentCursor);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // chnage touch panel assignments
    public class ChangeTouchPanelStatus extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_dialog.setMessage("Please Wait..");
            progress_dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                String machineBaseURL = "";

                if (CURRENT_MACHINE_IP.startsWith("http://")) {
                    machineBaseURL = CURRENT_MACHINE_IP;
                } else {
                    machineBaseURL = "http://" + CURRENT_MACHINE_IP;
                }

                URL urlValue = new URL(machineBaseURL + AppConstants.URL_CHANGE_TOUCH_PANEL_SWITCHLIST_STATUS + params[0]);
                Log.e("# url", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();

            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress_dialog.hide();
        }
    }

    private void initSelectionForAComponent(Cursor cursor) {

        if(cursor != null) {
            cursor.moveToFirst();

            String payload = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_TP_SWITCH_PAYLOAD));

            String payload1 = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_TP_SWITCH_PAYLOAD1));
            String payload2 = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_TP_SWITCH_PAYLOAD2));
            String payload3 = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_TP_SWITCH_PAYLOAD3));
            String payload4 = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_TP_SWITCH_PAYLOAD4));
            String payload5 = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_TP_SWITCH_PAYLOAD5));
            String payload6 = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_TP_SWITCH_PAYLOAD6));

            HashMap<String, String> defaultPayload = new HashMap<>();

            defaultPayload.put(String.valueOf(0), payload1);
            defaultPayload.put(String.valueOf(1), payload2);
            defaultPayload.put(String.valueOf(2), payload3);
            defaultPayload.put(String.valueOf(3), payload4);
            defaultPayload.put(String.valueOf(4), payload5);
            defaultPayload.put(String.valueOf(5), payload6);

            Log.e("defaultPayload webservice", defaultPayload.toString());

            // show touch panel list with selection
            initTouchPanelList(defaultPayload);
        }

    }


    //end of main class
}
