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

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.DimmerListActivity;
import com.webmyne.android.d_brain.ui.Customcomponents.MachineInactiveDialog;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
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
public class DimmerListCursorAdapter extends CursorRecyclerViewAdapter<DimmerListCursorAdapter.ViewHolder>{
    private  Context mCtx;
    static int VIEW_TYPE;
    private ArrayList<XMLValues> dimmerStatus;

    public onLongClickListener _longClick;
    public onSingleClickListener _singleClick;
    public onFavoriteClickListener _favoriteClick;
    public onAddToSceneClickListener _addToSceneClick;
    public onAddSchedulerClickListener _addSchedulerClick;
    public onRenameClickListener _renameClick;
    public onCheckedChangeListener _switchClick;
    private ProgressDialog progress_dialog;
    private int dimmertimeOutErrorCount = 3;

    public DimmerListCursorAdapter(Context context){
        super(context );
        mCtx = context;
        progress_dialog = new ProgressDialog(mCtx);
        progress_dialog.setCancelable(false);
    }

    public DimmerListCursorAdapter(Context context, Cursor cursor, ArrayList<XMLValues> _dimmerStatus){
        super(context,cursor);
        mCtx = context;
        this.dimmerStatus = _dimmerStatus;
        progress_dialog = new ProgressDialog(mCtx);
        progress_dialog.setCancelable(false);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    public class ListViewHolder extends ViewHolder {
        public TextView txtDimmerName, txtValue, txtMachineName;
        private SeekBar seekBar;
        private ImageView  imgFavoriteOption, imgAddToSceneOption, imgAddSchedulerOption, imgRenameOption;
        private LinearLayout linearMotor;
        private SwitchButton imgSwitch;

        public ListViewHolder(View view) {
            super(view);
            this.txtDimmerName = (TextView) view.findViewById(R.id.txtDimmerName);
            this.txtMachineName = (TextView) view.findViewById(R.id.txtMachineName);
            this.seekBar = (SeekBar) view.findViewById(R.id.seekBar);
            this.txtValue = (TextView) view.findViewById(R.id.txtValue);
            this.linearMotor = (LinearLayout) view.findViewById(R.id.linearMotor);
            txtValue.setText("0");

            this.imgSwitch = (SwitchButton) view.findViewById(R.id.imgSwitch);

            this.imgFavoriteOption = (ImageView) view.findViewById(R.id.imgFavoriteOption);
            this.imgAddToSceneOption = (ImageView) view.findViewById(R.id.imgAddToSceneOption);
            this.imgAddSchedulerOption = (ImageView) view.findViewById(R.id.imgAddSchedulerOption);
            this.imgRenameOption = (ImageView) view.findViewById(R.id.imgRenameOption);

            txtMachineName.setVisibility(View.GONE);

        }
    }

    public class GridViewHolder extends ViewHolder {
        private TextView txtDimmerName, txtValue, txtMachineName;
        private LinearLayout linearParent;
        private SeekBar seekBar;
        private SwitchButton imgSwitch;

        public GridViewHolder(View view) {
            super(view);
            this.txtDimmerName = (TextView) view.findViewById(R.id.txtDimmerName);
            txtMachineName = (TextView) view.findViewById(R.id.txtMachineName);
            this.linearParent = (LinearLayout) view.findViewById(R.id.linearParent);
            this.seekBar = (SeekBar) view.findViewById(R.id.seekBar);
            this.txtValue = (TextView) view.findViewById(R.id.txtValue);
            txtValue.setText("0");
            this.imgSwitch = (SwitchButton) view.findViewById(R.id.imgSwitch);

            txtMachineName.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(VIEW_TYPE == 0)
            return 0;
        else
            return 1;
    }

    public void setType(int type){
        VIEW_TYPE = type;
    }

    public  void setDimmerStatus(ArrayList<XMLValues> _dimmerStatus) {
        this.dimmerStatus = _dimmerStatus;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                ViewGroup viewgroup1 = ( ViewGroup ) mInflater.inflate ( R.layout.dimmer_list_item, parent, false );
                ListViewHolder listHolder = new ListViewHolder (viewgroup1);
                return listHolder;
            case 1:
                ViewGroup viewgroup2 = ( ViewGroup ) mInflater.inflate(R.layout.dimmer_grid_item, parent, false);
                GridViewHolder gridHolder = new GridViewHolder (viewgroup2);
                return gridHolder;
            default:
                ViewGroup viewgroup3 = ( ViewGroup ) mInflater.inflate ( R.layout.dimmer_grid_item, parent, false );
                GridViewHolder gridHolder1 = new GridViewHolder (viewgroup3);
                return gridHolder1;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final Cursor cursor) {
        final int dimmerNameIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME);
        final String dimmerName = cursor.getString(dimmerNameIndex);
        final int machineNameIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MNAME);
        final int machineIPIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP);

        final int machineIDIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID);
        final String machineId = cursor.getString(machineIDIndex);

        int componentIdIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID);
        String componentName = cursor.getString(componentIdIndex);

        final int isActiveIdx = cursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE);
        final String isActive = cursor.getString(isActiveIdx);

        String machineIP = cursor.getString(machineIPIndex);
        if(!machineIP.startsWith("http://")) {
            machineIP = "http://" + machineIP;
        }

        // check whether switch is favorite or not
        boolean isFavourite = false;
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(mCtx);
            dbHelper.openDataBase();
            isFavourite =  dbHelper.isAlreadyAFavourite(componentName, machineId);
            dbHelper.close();
        }catch(Exception e) {

        }

        final int position = cursor.getPosition();
       // final String strPosition = String.format("%02d", (position + 1));
        final String strPosition = componentName.substring(2,4);

        String dimmerOnOffStatus = (dimmerStatus.get(position).tagValue).substring(0, 2);
        int seekProgress  = 0;//Integer.parseInt((dimmerStatus.get(position).tagValue).substring(2,4))+1;
        if((dimmerStatus.get(position).tagValue).substring(2,4).equals("00")) {
            seekProgress = 0;
        } else {
            seekProgress  = Integer.parseInt((dimmerStatus.get(position).tagValue).substring(2,4))+1;
        }

        switch (viewHolder.getItemViewType () ) {

            case 0:
                final ListViewHolder listHolder = ( ListViewHolder ) viewHolder;

                listHolder.txtDimmerName.setText(cursor.getString(dimmerNameIndex));
                listHolder.txtMachineName.setText(cursor.getString(machineNameIndex));

                if(isFavourite) {
                    listHolder.imgFavoriteOption.setColorFilter(mCtx.getResources().getColor(R.color.yellow));
                    listHolder.imgFavoriteOption.setBackgroundResource(R.drawable.circle);
                } else {
                    listHolder.imgFavoriteOption.setColorFilter(mCtx.getResources().getColor(R.color.white));
                    listHolder.imgFavoriteOption.setBackgroundResource(R.drawable.white_border_circle);
                }

                if(dimmerOnOffStatus.equals(AppConstants.OFF_VALUE)) {
                    listHolder.imgSwitch.setChecked(false);
                } else {
                    listHolder.imgSwitch.setChecked(true);
                }

                // added on 19-10
                listHolder.txtValue.setText(String.valueOf(seekProgress));
                listHolder.seekBar.setProgress(seekProgress);

                if(isActive.equals("false")) {
                    listHolder.linearMotor.setAlpha(0.5f);
                    listHolder.imgRenameOption.setClickable(false);
                    listHolder.imgAddToSceneOption.setClickable(false);
                    listHolder.imgFavoriteOption.setClickable(false);
                    listHolder.imgAddSchedulerOption.setClickable(false);
                    listHolder.imgSwitch.setEnabled(false);
                    listHolder.imgSwitch.setClickable(false);
                    listHolder.seekBar.setEnabled(false);

                } else {
                    listHolder.linearMotor.setAlpha(1.0f);
                    listHolder.seekBar.setEnabled(true);
                    listHolder.imgSwitch.setEnabled(true);
                    listHolder.imgSwitch.setClickable(true);

                    final String finalMachineIP = machineIP;
                    listHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            listHolder.txtValue.setText("" + progress);

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
                                CHANGE_STATUS_URL = finalMachineIP + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.ON_VALUE + strProgress;
                                dimmerStatus.get(position).tagValue = AppConstants.ON_VALUE + strProgress;
                                listHolder.imgSwitch.setChecked(true);
                            } else if (seekBar.getProgress() == 0) {
                                CHANGE_STATUS_URL = finalMachineIP + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.OFF_VALUE + strProgress;
                                dimmerStatus.get(position).tagValue = AppConstants.OFF_VALUE + strProgress;
                            }
                            String[] params = new String[2];
                            params[0] = CHANGE_STATUS_URL;
                            params[1] = machineId;
                            new ChangeDimmerStatus().execute(params);
                        }
                    });

                    listHolder.imgSwitch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listHolder.imgSwitch.toggle();
                            String strProgress = "00";
                            String CHANGE_STATUS_URL = "";

                            if (listHolder.seekBar.getProgress() > 0) {
                                strProgress = String.format("%02d", (listHolder.seekBar.getProgress() - 1));
                            }

                            if (listHolder.imgSwitch.isChecked()) {
                                CHANGE_STATUS_URL = finalMachineIP + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.OFF_VALUE + strProgress;
                                //dimmerStatus.get(position).tagValue = AppConstants.ON_VALUE + strProgress;
                            } else {
                                CHANGE_STATUS_URL = finalMachineIP + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.ON_VALUE + strProgress;
                                //dimmerStatus.get(position).tagValue = AppConstants.OFF_VALUE + strProgress;
                            }
                            String[] params = new String[2];
                            params[0] = CHANGE_STATUS_URL;
                            params[1] = machineId;
                            new ChangeDimmerStatus().execute(params);
                        }
                    });

                    listHolder.imgRenameOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            _renameClick.onRenameOptionClick(position, dimmerName);
                        }
                    });

                    listHolder.imgAddToSceneOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            _addToSceneClick.onAddToSceneOptionClick(position);
                        }
                    });

                    listHolder.imgFavoriteOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            _favoriteClick.onFavoriteOptionClick(position);
                        }
                    });

                    listHolder.imgAddSchedulerOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            _addSchedulerClick.onAddSchedulerOptionClick(position);
                        }
                    });
                }

                listHolder.imgSwitch.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return event.getActionMasked() == MotionEvent.ACTION_MOVE;
                    }
                });

                break;
            case 1:
                final GridViewHolder groupViewHolder = ( GridViewHolder ) viewHolder;

                groupViewHolder.txtDimmerName.setText(cursor.getString(dimmerNameIndex));
                groupViewHolder.txtMachineName.setText(cursor.getString(machineNameIndex));

                final String finalMachineIP1 = machineIP;

                if(dimmerOnOffStatus.equals(AppConstants.OFF_VALUE)) {
                    groupViewHolder.imgSwitch.setChecked(false);
                } else {
                    groupViewHolder.imgSwitch.setChecked(true);
                }
                //added on 19-10
                groupViewHolder.txtValue.setText(String.valueOf(seekProgress));
                groupViewHolder.seekBar.setProgress(seekProgress);

                if(isActive.equals("false")) {
                    groupViewHolder.linearParent.setAlpha(0.5f);
                    groupViewHolder.linearParent.setLongClickable(false);
                    groupViewHolder.imgSwitch.setEnabled(false);
                    groupViewHolder.imgSwitch.setClickable(false);
                    groupViewHolder.seekBar.setEnabled(false);
                } else {
                    groupViewHolder.linearParent.setAlpha(1.0f);
                    groupViewHolder.imgSwitch.setEnabled(true);
                    groupViewHolder.imgSwitch.setClickable(true);
                    groupViewHolder.seekBar.setEnabled(true);

                    groupViewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            groupViewHolder.txtValue.setText("" + progress);
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
                                CHANGE_STATUS_URL = finalMachineIP1 + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.ON_VALUE + strProgress;
                                dimmerStatus.get(position).tagValue = AppConstants.ON_VALUE + strProgress;
                                groupViewHolder.imgSwitch.setChecked(true);
                            } else if (seekBar.getProgress() == 0) {
                                CHANGE_STATUS_URL = finalMachineIP1 + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.OFF_VALUE + strProgress;
                                dimmerStatus.get(position).tagValue = AppConstants.OFF_VALUE + strProgress;
                            }
                            String[] params = new String[2];
                            params[0] = CHANGE_STATUS_URL;
                            params[1] = machineId;
                            new ChangeDimmerStatus().execute(params);

                        }
                    });

                    groupViewHolder.imgSwitch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            groupViewHolder.imgSwitch.toggle();
                            String strProgress = "00";
                            String CHANGE_STATUS_URL = "";

                            if (groupViewHolder.seekBar.getProgress() > 0) {
                                strProgress = String.format("%02d", (groupViewHolder.seekBar.getProgress() - 1));
                            }

                            if (groupViewHolder.imgSwitch.isChecked()) {
                                CHANGE_STATUS_URL = finalMachineIP1 + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.OFF_VALUE + strProgress;
                                //dimmerStatus.get(position).tagValue = AppConstants.ON_VALUE + strProgress;
                            } else {
                                CHANGE_STATUS_URL = finalMachineIP1 + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.ON_VALUE + strProgress;
                                //dimmerStatus.get(position).tagValue = AppConstants.OFF_VALUE + strProgress;
                            }

                            String[] params = new String[2];
                            params[0] = CHANGE_STATUS_URL;
                            params[1] = machineId;
                            new ChangeDimmerStatus().execute(params);
                        }
                    });

                    groupViewHolder.linearParent.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            _longClick.onLongClick(position, view);
                            return false;
                        }
                    });
                }

                groupViewHolder.imgSwitch.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return event.getActionMasked() == MotionEvent.ACTION_MOVE;
                    }
                });

                break;
        }


    }

    public void setSingleClickListener(onSingleClickListener obj){
        this._singleClick = obj;
    }
    public void setLongClickListener(onLongClickListener obj){
        this._longClick = obj;
    }

    public void setFavoriteClickListener(onFavoriteClickListener obj){
        this._favoriteClick = obj;
    }

    public void setAddToSceneClickListener(onAddToSceneClickListener obj){
        this._addToSceneClick = obj;
    }

    public void setAddSchedulerClickListener(onAddSchedulerClickListener obj){
        this._addSchedulerClick = obj;
    }

    public void setRenameClickListener(onRenameClickListener obj){
        this._renameClick = obj;
    }

    public void setCheckedChangeListener(onCheckedChangeListener obj){
        this._switchClick = obj;
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
                Log.e("# ADAPTER dimmertimeOutErrorCount", dimmertimeOutErrorCount +"");
                isError = true;
                dimmertimeOutErrorCount--;
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
                if(dimmertimeOutErrorCount > 0) {
                    Log.e("ADAP", "time out > 0");
                    progress_dialog.setMessage("Sending request to machine.. " + dimmertimeOutErrorCount);
                    String[] parameters = new String[2];
                    parameters[0] = parameter;
                    parameters[1] = machineId;
                    new ChangeDimmerStatus().execute(parameters);
                } else {
                    Log.e("ADAP", "time out == 0");
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
        }
    }
}
