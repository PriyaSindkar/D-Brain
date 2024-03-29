package com.webmyne.android.d_brain.ui.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Customcomponents.MachineInactiveDialog;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;
import com.webmyne.android.d_brain.ui.xmlHelpers.XMLValues;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by priyasindkar on 14-09-2015.
 */
public class FavouriteListCursorAdapter extends CursorRecyclerViewAdapter<FavouriteListCursorAdapter.ViewHolder>{
    private  Context mCtx;
    static int VIEW_TYPE;
    private int totalNoOfSwitches;
    private ArrayList<XMLValues> componentStatus;
    private ArrayList<XMLValues> dimmerStatus;
    private Cursor mCursor;

    public onDeleteClickListener _onDeleteClick;
    public onCheckedChangeListener _switchClick;

    private ProgressDialog progress_dialog;
    int switchtimeOutErrorCount = 3;
    int dimmertimeOutErrorCount = 3;

    public FavouriteListCursorAdapter(Context context){
        super(context );
        mCtx = context;

        progress_dialog = new ProgressDialog(mCtx);
        progress_dialog.setCancelable(false);
    }


    public FavouriteListCursorAdapter(Context context, Cursor cursor, ArrayList<XMLValues> _componentStatus){
        super(context,cursor);
        mCtx = context;
        this.componentStatus = _componentStatus;
        this.mCursor = cursor;

        progress_dialog = new ProgressDialog(mCtx);
        progress_dialog.setCancelable(false);
    }

    public FavouriteListCursorAdapter(Context context, Cursor cursor){
        super(context,cursor);
        mCtx = context;
        this.mCursor = cursor;

        progress_dialog = new ProgressDialog(mCtx);
        progress_dialog.setCancelable(false);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    public class SwitchViewHolder extends ViewHolder {
        public TextView txtSwitchName, txtMachineName;
        public LinearLayout linearSwitch, linearItem;
        private ImageView imgDeleteOption;
        public SwitchButton imgSwitch;

        public SwitchViewHolder ( View itemView ) {
            super ( itemView );
            this.txtSwitchName = (TextView) itemView.findViewById(R.id.txtSwitchName);
            this.txtMachineName = (TextView) itemView.findViewById(R.id.txtMachineName);

            this.linearSwitch = (LinearLayout) itemView.findViewById(R.id.linearSwitch);
            this.imgDeleteOption = (ImageView) itemView.findViewById(R.id.imgDeleteOption);
            this.imgSwitch = (SwitchButton) itemView.findViewById(R.id.imgSwitch);
            this.linearItem = (LinearLayout) itemView.findViewById(R.id.linearItem);
        }
    }

    public class DimmerViewHolder extends ViewHolder{
        public TextView txtDimmerName, txtValue, txtMachineName;
        private SeekBar seekBar;
        public  ImageView  imgDeleteOption;
        private LinearLayout linearItem, linearMotor;
        private SwitchButton imgSwitch;

        public DimmerViewHolder ( View itemView ) {
            super ( itemView );
            this.txtDimmerName = (TextView) itemView.findViewById(R.id.txtDimmerName);
            this.txtMachineName = (TextView) itemView.findViewById(R.id.txtMachineName);
            this.seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
            this.txtValue = (TextView) itemView.findViewById(R.id.txtValue);
            this.linearItem = (LinearLayout) itemView.findViewById(R.id.linearItem);
            this.linearMotor = (LinearLayout) itemView.findViewById(R.id.linearMotor);
            this.imgDeleteOption = (ImageView) itemView.findViewById(R.id.imgDeleteOption);
            this.imgSwitch = (SwitchButton) itemView.findViewById(R.id.imgSwitch);
            this.imgSwitch.setVisibility(View.VISIBLE);
            txtValue.setText("0");
        }
    }

    public class MotorViewHolder extends ViewHolder{
        public TextView txtMotorName;
        public LinearLayout linearParent;
        public boolean isLeftRight = true;
        private ImageView imgRotateSwitches, leftArrow, rightArrow, imgFavoriteOption, imgAddToSceneOption, imgAddSchedulerOption, imgRenameOption;

        public MotorViewHolder ( View itemView ) {
            super ( itemView );
            txtMotorName = (TextView) itemView.findViewById(R.id.txtMotorName);
            linearParent = (LinearLayout) itemView.findViewById(R.id.linearParent);

            this.imgRotateSwitches = (ImageView) itemView.findViewById(R.id.imgRotateSwitches);
            this.rightArrow = (ImageView) itemView.findViewById(R.id.imgMotorRightArrow);
            this.leftArrow = (ImageView) itemView.findViewById(R.id.imgMotorLeftArrow);

            this.imgFavoriteOption = (ImageView) itemView.findViewById(R.id.imgFavoriteOption);
            this.imgAddToSceneOption = (ImageView) itemView.findViewById(R.id.imgAddToSceneOption);
            this.imgAddSchedulerOption = (ImageView) itemView.findViewById(R.id.imgAddSchedulerOption);
            this.imgRenameOption = (ImageView) itemView.findViewById(R.id.imgRenameOption);
        }
    }


    @Override
    public int getItemViewType(int position) {

        if(mCursor != null)
            mCursor.moveToPosition(position);

        String type = mCursor.getString(mCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));

        if(type.equals(AppConstants.SWITCH_TYPE))
            return 0;
        else if (type.equals(AppConstants.DIMMER_TYPE))
            return 1;
        else if (type.equals(AppConstants.MOTOR_TYPE))
            return 2;
        else
            return 0;
        /*if(VIEW_TYPE == 0)
            return 0;
        else
            return 1;*/
    }

    public  void setComponentStatus(ArrayList<XMLValues> _switchStatus) {
        this.componentStatus = _switchStatus;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                ViewGroup viewgroup1 = ( ViewGroup ) mInflater.inflate ( R.layout.create_scene_switch_item, parent, false );
                SwitchViewHolder switchHolder = new SwitchViewHolder (viewgroup1);
                return switchHolder;
            case 1:
                ViewGroup viewgroup2 = ( ViewGroup ) mInflater.inflate(R.layout.favourite_dimmer_item, parent, false);
                DimmerViewHolder dimmerHolder = new DimmerViewHolder (viewgroup2);
                return dimmerHolder;
            case 2:
                ViewGroup viewgroup3 = ( ViewGroup ) mInflater.inflate(R.layout.motor_list_item, parent, false);
                MotorViewHolder motorHolder = new MotorViewHolder (viewgroup3);
                return motorHolder;
            default:
                ViewGroup viewgroup4 = ( ViewGroup ) mInflater.inflate ( R.layout.create_scene_switch_item, parent, false );
                SwitchViewHolder switchHolder1 = new SwitchViewHolder (viewgroup4);
                return switchHolder1;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final Cursor cursor) {

        mCursor = cursor;
        int componentIdIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_F_CID);
        String componentId = cursor.getString(componentIdIndex);
        String componentName = "";

        int isActiveIdx = cursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE);
        String isActive = cursor.getString(isActiveIdx);

        try {
            DatabaseHelper dbHelper = new DatabaseHelper(mCtx);
            dbHelper.openDataBase();
            componentName = dbHelper.getComponentNameByPrimaryId(componentId);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        int machineNameIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_F_MNAME);
        final int machineIPIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP);
        final int machineIDIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID);
        final String machineId = cursor.getString(machineIDIndex);

        String machineIP = cursor.getString(machineIPIndex);
        if(!machineIP.startsWith("http://")) {
            machineIP = "http://" + machineIP;
        }

        final int position = cursor.getPosition();

        switch (viewHolder.getItemViewType () ) {
            case 0:

                final SwitchViewHolder switchHolder = ( SwitchViewHolder ) viewHolder;
                switchHolder.txtSwitchName.setText(componentName);
                switchHolder.txtMachineName.setText(cursor.getString(machineNameIndex));

                final String switchStrPosition = componentStatus.get(position).tagName.substring(2,4);

                if( componentStatus.get(position).tagValue.equals(AppConstants.OFF_VALUE)) {
                    switchHolder.imgSwitch.setChecked(false);
                } else {
                    switchHolder.imgSwitch.setChecked(true);
                }

                final String finalMachineIP = machineIP;

                if(isActive.equals("false")) {
                    switchHolder.linearItem.setAlpha(0.5f);
                    switchHolder.linearSwitch.setAlpha(0.5f);
                    switchHolder.imgDeleteOption.setClickable(false);
                    switchHolder.imgSwitch.setEnabled(false);
                    switchHolder.imgSwitch.setClickable(false);
                } else {
                    switchHolder.linearItem.setAlpha(1.0f);
                    switchHolder.linearSwitch.setAlpha(1.0f);
                    switchHolder.imgDeleteOption.setClickable(true);
                    switchHolder.imgSwitch.setEnabled(true);
                    switchHolder.imgSwitch.setClickable(true);

                    switchHolder.imgSwitch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switchHolder.imgSwitch.toggle();

                            if(switchHolder.imgSwitch.isChecked()){
                                String CHANGE_STATUS_URL = finalMachineIP + AppConstants.URL_CHANGE_SWITCH_STATUS + switchStrPosition + AppConstants.OFF_VALUE;
                                String[] params = new String[2];
                                params[0] = CHANGE_STATUS_URL;
                                params[1] = machineId;
                                new ChangeSwitchStatus().execute(params);
                            }else{
                                String CHANGE_STATUS_URL = finalMachineIP + AppConstants.URL_CHANGE_SWITCH_STATUS + switchStrPosition + AppConstants.ON_VALUE;
                                String[] params = new String[2];
                                params[0] = CHANGE_STATUS_URL;
                                params[1] = machineId;
                                new ChangeSwitchStatus().execute(params);
                            }
                        }
                    });

                    switchHolder.imgDeleteOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _onDeleteClick.onDeleteOptionClick(position);
                        }
                    });
                }



                switchHolder.imgSwitch.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return event.getActionMasked() == MotionEvent.ACTION_MOVE;
                    }
                });



                break;
            case 1:
                final DimmerViewHolder dimmerHolder = ( DimmerViewHolder ) viewHolder;
                dimmerHolder.txtDimmerName.setText(componentName);
                dimmerHolder.txtMachineName.setText(cursor.getString(machineNameIndex));

                final String dimmerStrPosition = componentStatus.get(position).tagName.substring(2,4);

                String dimmerOnOffStatus = (componentStatus.get(position).tagValue).substring(0, 2);
                int seekProgress  = 0;//Integer.parseInt((dimmerStatus.get(position).tagValue).substring(2,4))+1;
                if((componentStatus.get(position).tagValue).substring(2,4).equals("00")) {
                    seekProgress = 0;
                } else {
                    seekProgress  = Integer.parseInt((componentStatus.get(position).tagValue).substring(2,4))+1;
                }

                if(dimmerOnOffStatus.equals(AppConstants.OFF_VALUE)) {
                    dimmerHolder.imgSwitch.setChecked(false);
                } else {
                    dimmerHolder.imgSwitch.setChecked(true);
                }

                final String finalMachineIP1 = machineIP;

                // added on 19-10
                dimmerHolder.txtValue.setText(String.valueOf(seekProgress));
                dimmerHolder.seekBar.setProgress(seekProgress);


                if(isActive.equals("false")) {
                    dimmerHolder.linearItem.setAlpha(0.5f);
                    dimmerHolder.linearMotor.setAlpha(0.5f);
                    dimmerHolder.imgDeleteOption.setClickable(false);
                    dimmerHolder.imgSwitch.setEnabled(false);
                    dimmerHolder.imgSwitch.setClickable(false);
                    dimmerHolder.seekBar.setEnabled(false);
                } else {

                    dimmerHolder.linearItem.setAlpha(1.0f);
                    dimmerHolder.linearMotor.setAlpha(1.0f);
                    dimmerHolder.imgDeleteOption.setClickable(true);
                    dimmerHolder.imgSwitch.setEnabled(true);
                    dimmerHolder.imgSwitch.setClickable(true);
                    dimmerHolder.seekBar.setEnabled(true);


                    dimmerHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            dimmerHolder.txtValue.setText("" + progress);

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            String strProgress = "00";
                            String CHANGE_STATUS_URL = "";

                            if (seekBar.getProgress() > 0) {
                                strProgress = String.format("%02d", (seekBar.getProgress() - 1));
                                CHANGE_STATUS_URL = finalMachineIP1 + AppConstants.URL_CHANGE_DIMMER_STATUS + dimmerStrPosition + AppConstants.ON_VALUE + strProgress;
                                componentStatus.get(position).tagValue = AppConstants.ON_VALUE + strProgress;
                                dimmerHolder.imgSwitch.setChecked(true);
                            } else if (seekBar.getProgress() == 0) {
                                CHANGE_STATUS_URL = finalMachineIP1 + AppConstants.URL_CHANGE_DIMMER_STATUS + dimmerStrPosition + AppConstants.OFF_VALUE + strProgress;
                                componentStatus.get(position).tagValue = AppConstants.OFF_VALUE + strProgress;
                                dimmerHolder.imgSwitch.setChecked(false);
                            }
                            String[] params = new String[2];
                            params[0] = CHANGE_STATUS_URL;
                            params[1] = machineId;
                            new ChangeDimmerStatus().execute(params);
                        }
                    });

                    dimmerHolder.imgSwitch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dimmerHolder.imgSwitch.toggle();
                            String strProgress = "00";
                            String CHANGE_STATUS_URL = "";

                            if (dimmerHolder.seekBar.getProgress() > 0) {
                                strProgress = String.format("%02d", (dimmerHolder.seekBar.getProgress() - 1));
                            }

                            if (dimmerHolder.imgSwitch.isChecked()) {
                                CHANGE_STATUS_URL = finalMachineIP1 + AppConstants.URL_CHANGE_DIMMER_STATUS + dimmerStrPosition + AppConstants.OFF_VALUE + strProgress;
                                //dimmerStatus.get(position).tagValue = AppConstants.ON_VALUE + strProgress;
                            } else {
                                CHANGE_STATUS_URL = finalMachineIP1 + AppConstants.URL_CHANGE_DIMMER_STATUS + dimmerStrPosition + AppConstants.ON_VALUE + strProgress;
                                //dimmerStatus.get(position).tagValue = AppConstants.OFF_VALUE + strProgress;
                            }

                            String[] params = new String[2];
                            params[0] = CHANGE_STATUS_URL;
                            params[1] = machineId;
                            new ChangeDimmerStatus().execute(params);
                        }
                    });

                    dimmerHolder.imgDeleteOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("position", position + "");
                            _onDeleteClick.onDeleteOptionClick(position);
                        }
                    });
                }

                dimmerHolder.imgSwitch.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return event.getActionMasked() == MotionEvent.ACTION_MOVE;
                    }
                });

                break;

            case 2:
                final MotorViewHolder motorViewHolder = ( MotorViewHolder ) viewHolder;
                motorViewHolder.txtMotorName.setText("");

                /*motorViewHolder.imgRotateSwitches.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("TAG", "item clicked " + position);

                        AnimationHelper animationHelper = new AnimationHelper();

                        if (motorViewHolder.isLeftRight) {
                            animationHelper.rotateViewClockwiseLeftToUp(motorViewHolder.leftArrow);
                            animationHelper.rotateViewClockwiseLeftToUp(motorViewHolder.rightArrow);
                        } else {
                            animationHelper.rotateViewAntiClockwiseLeftToUp(motorViewHolder.leftArrow);
                            animationHelper.rotateViewAntiClockwiseLeftToUp(motorViewHolder.rightArrow);
                        }
                        motorViewHolder.isLeftRight = !motorViewHolder.isLeftRight;
                    }
                });

                motorViewHolder.linearParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _onItemClick._onItemClickListener();
                    }
                });*/

                break;

        }


    }

    public void setDeleteClickListener(onDeleteClickListener obj){
        this._onDeleteClick = obj;
    }

    public void setCheckedChangeListener(onCheckedChangeListener obj){
        this._switchClick = obj;
    }

    public class ChangeSwitchStatus extends AsyncTask<String, Void, Void> {
        boolean isError = false;
        String parameter, machineId, isMachineActive;
        Cursor machineCursor;
        DatabaseHelper dbHelper = new DatabaseHelper(mCtx);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _switchClick.onCheckedPreChangeClick(0);
            progress_dialog.setMessage("Sending request to machine.. " + switchtimeOutErrorCount);
            progress_dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                parameter = params[0];
                machineId = params[1];

                /*dbHelper.openDataBase();
                // get current machine details
                machineCursor = dbHelper.getMachineByID(String.valueOf(machineId));
                isMachineActive = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));
                dbHelper.close();

                if(isMachineActive.equals("true")) {*/

                URL urlValue = new URL(params[0]);
                Log.e("# SWCR CHANGE", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();
                isError = false;
               /* } else {
                    progress_dialog.dismiss();
                    isError = true;
                    switchtimeOutErrorCount = 0;
                }*/
            } catch (Exception e) {
                Log.e("# ADAPTER switchtimeOutErrorCount", switchtimeOutErrorCount +"");
                isError = true;
                switchtimeOutErrorCount--;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if( !isError) {
                _switchClick.onCheckedChangeClick(0);
                progress_dialog.dismiss();
            } else {
                Log.e("ADAP", ""+isError);
                if(switchtimeOutErrorCount > 0) {
                    Log.e("ADAP", "time out > 0");
                    progress_dialog.setMessage("Sending request to machine.. " + switchtimeOutErrorCount);
                    String[] parameters = new String[2];
                    parameters[0] = parameter;
                    parameters[1] = machineId;
                    new ChangeSwitchStatus().execute(parameters);
                } else {
                    Log.e("ADAP", "time out == 0");
                    progress_dialog.dismiss();
                    switchtimeOutErrorCount = 3;

                    try {
                        dbHelper.openDataBase();
                        dbHelper.enableDisableMachine(machineId, false);
                        dbHelper.close();
                    } catch (SQLException ex) {
                        Log.e("TAG EXP TIME OUT", ex.toString());
                    }

                    MachineInactiveDialog machineNotActiveDialog = new MachineInactiveDialog(mCtx, "Your machine was deactivated.");
                    machineNotActiveDialog.show();
                    machineNotActiveDialog.setSaveListener(new onSaveClickListener() {
                        @Override
                        public void onSaveClick(boolean isSave) {
                            switchtimeOutErrorCount = 3;
                            _switchClick.onCheckedChangeClick(1);
                            progress_dialog.dismiss();
                        }
                    });
                }
            }
        }
    }

    public class ChangeDimmerStatus extends AsyncTask<String, Void, Void> {
        boolean isError = false;
        String parameter, machineId, isMachineActive;
        Cursor machineCursor;
        DatabaseHelper dbHelper = new DatabaseHelper(mCtx);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try{
                progress_dialog.setMessage("Sending request to machine.. " + dimmertimeOutErrorCount);
                progress_dialog.show();
                _switchClick.onCheckedPreChangeClick(0);
            }catch(Exception e){
            }
        }

        @Override
        protected Void doInBackground(String... params) {

            try {

                parameter = params[0];
                machineId = params[1];

                URL urlValue = new URL(params[0]);
                Log.e("# url change dimmer", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();


            } catch (Exception e) {
                Log.e("# EXP", e.toString());
                isError = true;
                dimmertimeOutErrorCount--;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
                if( !isError) {
                    _switchClick.onCheckedChangeClick(0);
                    progress_dialog.dismiss();
                } else {
                    if(dimmertimeOutErrorCount > 0) {
                        progress_dialog.setMessage("Sending request to machine.. " + dimmertimeOutErrorCount);
                        String[] parameters = new String[2];
                        parameters[0] = parameter;
                        parameters[1] = machineId;
                        new ChangeDimmerStatus().execute(parameters);
                    } else {
                        progress_dialog.dismiss();
                        dimmertimeOutErrorCount = 3;

                        try {
                            dbHelper.openDataBase();
                            dbHelper.enableDisableMachine(machineId, false);
                            dbHelper.close();
                        } catch (SQLException ex) {
                            Log.e("TAG EXP TIME OUT", ex.toString());
                        }

                        MachineInactiveDialog machineNotActiveDialog = new MachineInactiveDialog(mCtx, "Your machine was deactivated.");
                        machineNotActiveDialog.show();
                        machineNotActiveDialog.setSaveListener(new onSaveClickListener() {
                            @Override
                            public void onSaveClick(boolean isSave) {
                                dimmertimeOutErrorCount = 3;
                                _switchClick.onCheckedChangeClick(1);
                                progress_dialog.dismiss();
                            }
                        });
                    }
                }

            }catch(Exception e){
            }
        }
    }
}
