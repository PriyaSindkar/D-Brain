package com.webmyne.android.d_brain.ui.Customcomponents;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.animation.Attention.Flash;
import com.flyco.animation.Attention.Swing;
import com.flyco.animation.Attention.Tada;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.animation.FlipEnter.FlipTopEnter;
import com.flyco.animation.Jelly;
import com.flyco.animation.NewsPaperEnter;
import com.flyco.animation.SlideEnter.SlideRightEnter;
import com.flyco.animation.SlideEnter.SlideTopEnter;
import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.base.BaseDialog;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Customcomponents.CustomProgressBar.ProgressPainter;
import com.webmyne.android.d_brain.ui.Fragments.AddMachineFragment;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.Model.TouchPanelModel;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;
import com.webmyne.android.d_brain.ui.xmlHelpers.MainXmlPullParser;
import com.webmyne.android.d_brain.ui.xmlHelpers.XMLValues;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by priyasindkar on 16-09-2015.
 */
public class AddMachineDialog extends BaseDialog {
    private TextView txtAddMachine;
    private ImageView imgCancel;
    private EditText edtIPAddress, edtMachineName, edtMachineSerialNo;
    private String machineIP, machineSerialNo, userEnteredSerialNo, productCode, machineName;
    private ArrayList<XMLValues> powerStatus;
    private RelativeLayout relativeContent;
    private ProgressBar progressBar;
    private onSingleClickListener clickListener;
    private long machineID;
    private int totalMachinesCount = 0;

    public AddMachineDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new SlideTopEnter());

        // dismissAnim(this, new ZoomOutExit());
        View inflate = View.inflate(context, R.layout.dialog_add_machine, null);
        //txtAddMachine = ViewFindUtils.find(inflate, R.id.txtAddMachine);
        txtAddMachine = (TextView) inflate.findViewById(R.id.txtAddMachine);
        imgCancel = (ImageView) inflate.findViewById(R.id.imgCancel);

        edtIPAddress = (EditText) inflate.findViewById(R.id.edtIPAddress);
        edtMachineName = (EditText) inflate.findViewById(R.id.edtMachineName);
        edtMachineSerialNo = (EditText) inflate.findViewById(R.id.edtMachineSerialNo);

        progressBar = (ProgressBar) inflate.findViewById(R.id.progress_bar);
        relativeContent = (RelativeLayout) inflate.findViewById(R.id.relativeContent);

        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return inflate;
    }



    @Override
    public boolean setUiBeforShow() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        txtAddMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "ADD_MACHINE");
                if(edtIPAddress.getText().toString().trim().length() == 0) {
                    Toast.makeText(context, "Must Enter Device IP Address.", Toast.LENGTH_LONG).show();
                } else if(edtMachineName.getText().toString().trim().length() == 0) {
                    Toast.makeText(context, "Must Enter Machine Name.", Toast.LENGTH_LONG).show();
                } else if(edtMachineSerialNo.getText().toString().trim().length() == 0) {
                    Toast.makeText(context, "Must Enter Machine Serial No.", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        DatabaseHelper dbHelper = new DatabaseHelper(context);
                        dbHelper.openDataBase();
                        Cursor machinesCursor = dbHelper.getAllMachines();

                        if (machinesCursor != null) {
                            totalMachinesCount = machinesCursor.getCount();
                        } else {
                            totalMachinesCount = 0;
                        }
                        dbHelper.close();
                    } catch (SQLException e) {
                       Log.e("SQL EXP", e.toString());
                    }

                    if (totalMachinesCount >= 3) {
                        Toast.makeText(context, "Cannot enter more than 3 machines.", Toast.LENGTH_LONG).show();
                    } else {
                        machineIP = edtIPAddress.getText().toString().trim();
                        userEnteredSerialNo = edtMachineSerialNo.getText().toString().trim();
                        machineName = edtMachineName.getText().toString().trim();
                        new GetMachineStatus().execute();
                    }
                }
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return true;
    }

    public class GetMachineStatus extends AsyncTask<Void, Void, Void> {
        boolean isError = false;
        @Override
        protected void onPreExecute() {
             //setProgressBarIndeterminateVisibility(true);
            relativeContent.setFocusable(false);
            txtAddMachine.setClickable(false);
            imgCancel.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL urlValue;
                if(machineIP.startsWith("https://")) {
                    machineIP = machineIP.replace("https://", "http://");
                    urlValue = new URL(machineIP + AppConstants.URL_FETCH_MACHINE_STATUS);
                } else if(!machineIP.startsWith("http://")) {
                    urlValue = new URL("http://" + machineIP + AppConstants.URL_FETCH_MACHINE_STATUS);
                    machineIP = "http://" + machineIP;
                } else {
                    urlValue = new URL( machineIP + AppConstants.URL_FETCH_MACHINE_STATUS);
                }
                Log.e("# urlValue", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);

                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();
                //  Log.e("# inputStream", inputStream.toString());
                MainXmlPullParser pullParser = new MainXmlPullParser();
                powerStatus = new ArrayList<>();
                powerStatus = pullParser.processXML(inputStream);
                Log.e("XML PARSERED", powerStatus.toString());


            } catch (Exception e) {
                Log.e("# EXP", e.toString());
                isError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Log.e("TAG_ASYNC", "Inside onPostExecute");
            relativeContent.setEnabled(true);
            txtAddMachine.setClickable(true);
            imgCancel.setClickable(true);
            progressBar.setVisibility(View.GONE);

            if(!isError) {
                try {
                    for (int i = 0; i < powerStatus.size(); i++) {
                        if (powerStatus.get(i).tagName.equals("DPC")) {
                            productCode = powerStatus.get(i).tagValue;
                        } else if (powerStatus.get(i).tagName.equals("DSN")) {
                            machineSerialNo = powerStatus.get(i).tagValue;
                        }
                    }

                    /*if (machineIP.startsWith("https://")) {
                        machineIP = machineIP.replace("https://", "http://");
                    } else if (!machineIP.startsWith("http://")) {
                        machineIP = "http://" + machineIP;
                    } else {

                    }*/

                    DatabaseHelper dbHelper = new DatabaseHelper(context);
                    dbHelper.openDataBase();
                    Log.e("machineIP", machineIP);
                    if(dbHelper.isMachineIPAlreadyExists(machineIP)) {
                        Toast.makeText(context, "Machine IP Address Already Exists.", Toast.LENGTH_LONG).show();
                    } else {

                        if (dbHelper.isMachineSerialNoExists(userEnteredSerialNo)) {
                            Toast.makeText(context, "Machine Serial No. Already Exists.", Toast.LENGTH_LONG).show();
                        } else {
                            if (userEnteredSerialNo.equals(machineSerialNo)) {
                                if (Utils.validateProductCode(productCode)) {
                                    machineID = dbHelper.insertIntoMachine(powerStatus, machineName, machineIP);
                                    dbHelper.close();

                                    if(machineID != -1) {
                                        initDatabaseComponents(productCode);
                                        Toast.makeText(context, "Machine Added", Toast.LENGTH_SHORT).show();
                                        clickListener.onSingleClick(0);
                                        dismiss();
                                    } else {
                                        Toast.makeText(context, "Error Occurred While Adding Machine. Please Check IP Address and Serial No.", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Invalid Product Code.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(context, "Invalid Serial No.", Toast.LENGTH_LONG).show();
                            }
                        } // end of check serialno. duplicate
                    } // end of check IP duplicate
                } catch (Exception e) {
                    Log.e("EXP123", e.toString());
                    Toast.makeText(context, "Error Occurred While Adding Machine. Please Check IP Address and Serial No.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Error Occurred While Adding Machine. Please Check IP Address and Serial No.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initDatabaseComponents(String productCode) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        //insert switches in component table for the given machine
        try {
            dbHelper.openDataBase();

            // product code and machine IP are hard-coded
            //String productCode = AppConstants.TEMP_PRODUCT_CODE;
            ArrayList<ComponentModel> listOfComponents = new ArrayList<>();
            ArrayList<TouchPanelModel> listOfTouchPanels = new ArrayList<>();

            // init the component table with switches of a machine
            int totalNoOfSwitches = Integer.parseInt(productCode.substring(1,2)) * 11;
            int totalNoOfDimmers = Integer.parseInt(productCode.substring(2,3)) * 11;
            int totalNoOfMotors = Integer.parseInt(productCode.substring(3,4)) * 11;

            if(totalNoOfSwitches == 0) {

            } else {
                for (int i = 0; i < totalNoOfSwitches; i++) {
                    String idSuffix = String.format("%02d", (i + 1));
                    ComponentModel switchItem = new ComponentModel(AppConstants.SWITCH_PREFIX + idSuffix, AppConstants.SWITCH_TYPE + String.valueOf(i + 1), AppConstants.SWITCH_TYPE, String.valueOf(machineID), machineIP);
                    switchItem.setMachineName(machineName);
                    listOfComponents.add(switchItem);
                }
            }

            // init the component table with dimmers of a machine
            if(totalNoOfDimmers == 0) {

            } else {
                for (int i = 0; i < totalNoOfDimmers; i++) {
                    String idSuffix = String.format("%02d", (i + 1));
                    ComponentModel dimmerItem = new ComponentModel(AppConstants.DIMMER_PREFIX + idSuffix, AppConstants.DIMMER_TYPE + String.valueOf(i + 1), AppConstants.DIMMER_TYPE, String.valueOf(machineID), machineIP);
                    dimmerItem.setMachineName(machineName);
                    listOfComponents.add(dimmerItem);
                }
            }

            // init the component table with dimmers of a machine
            if(totalNoOfMotors == 0) {

            } else {
                for (int i = 0; i < totalNoOfMotors; i++) {
                    String idSuffix = String.format("%02d", (i + 1));
                    ComponentModel motorItem = new ComponentModel(AppConstants.MOTOR_PREFIX + idSuffix, AppConstants.MOTOR_TYPE + String.valueOf(i + 1), AppConstants.MOTOR_TYPE, String.valueOf(machineID), machineIP);
                    motorItem.setMachineName(machineName);
                    listOfComponents.add(motorItem);
                }
            }

            // init the component table with alerts of a machine
            int totalNoOfAlerts = Integer.parseInt(productCode.substring(4,5));
            if(totalNoOfAlerts == 0) {

            } else {
                for (int i = 0; i < totalNoOfAlerts; i++) {
                    String idSuffix = String.format("%02d", (i + 1));
                    ComponentModel sensorItem = new ComponentModel(AppConstants.ALERT_PREFIX + idSuffix, AppConstants.ALERT_TYPE + String.valueOf(i + 1), AppConstants.ALERT_TYPE, String.valueOf(machineID), machineIP);
                    sensorItem.setMachineName(machineName);
                    sensorItem.setDetails("Alert fired on breach");
                    listOfComponents.add(sensorItem);
                }
            }

            //int totalNoOfPanels = (Integer.parseInt(productCode.substring(7,8)) * 10) + Integer.parseInt(productCode.substring(8,9));

            int totalNoOfPanels = (Integer.parseInt(productCode.substring(6,7)) * 10);

            for(int i=0; i<totalNoOfPanels; i++) {
                String idSuffix = String.format("%02d", (i + 1));
                TouchPanelModel touchPanelBox = new TouchPanelModel(AppConstants.TOUCH_PANEL_TYPE+idSuffix);
                touchPanelBox.setMIp(machineIP);
                touchPanelBox.setmMId((int)machineID);
                touchPanelBox.setMname(machineName);
                listOfTouchPanels.add(touchPanelBox);

            }

            dbHelper.insertIntoComponent(listOfComponents);
            dbHelper.insertIntoTouchPanel(listOfTouchPanels);

            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setClickListener(onSingleClickListener obj){
        this.clickListener = obj;
    }
}
