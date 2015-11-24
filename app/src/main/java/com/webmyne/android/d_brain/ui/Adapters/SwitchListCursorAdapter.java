package com.webmyne.android.d_brain.ui.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Customcomponents.MachineInactiveDialog;
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
public class SwitchListCursorAdapter extends CursorRecyclerViewAdapter<SwitchListCursorAdapter.ViewHolder>{
    private  Context mCtx;
    static int VIEW_TYPE;
    private int totalNoOfSwitches;
    private ArrayList<XMLValues> switchStatus;

    public onLongClickListener _longClick;
    public onSingleClickListener _singleClick;
    public onFavoriteClickListener _favoriteClick;
    public onAddToSceneClickListener _addToSceneClick;
    public onAddSchedulerClickListener _addSchedulerClick;
    public onRenameClickListener _renameClick;
    public onCheckedChangeListener _switchClick;

    private ProgressDialog progress_dialog;
    int switchtimeOutErrorCount = 3;

    public SwitchListCursorAdapter(Context context){
        super(context );
        mCtx = context;
        progress_dialog = new ProgressDialog(mCtx);
        progress_dialog.setCancelable(false);
    }


    public SwitchListCursorAdapter(Context context, Cursor cursor, ArrayList<XMLValues> _switchStatus){
        super(context,cursor);
        mCtx = context;
        this.switchStatus = _switchStatus;
        progress_dialog = new ProgressDialog(mCtx);
        progress_dialog.setCancelable(false);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    public class ListViewHolder extends ViewHolder {
        public  TextView txtSwitchName, txtMachineName;
        public ImageView imgFavoriteOption, imgAddToSceneOption, imgAddSchedulerOption, imgRenameOption;
        public LinearLayout linearSwitch, linearParent, linearOptionsMenu;
        public SwitchButton imgSwitch;

        public ListViewHolder ( View view ) {
            super ( view );
            this.txtSwitchName = (TextView) view.findViewById(R.id.txtSwitchName);
            this.txtMachineName = (TextView) view.findViewById(R.id.txtMachineName);
            this.linearSwitch = (LinearLayout) view.findViewById(R.id.linearSwitch);

            this.imgFavoriteOption = (ImageView) view.findViewById(R.id.imgFavoriteOption);
            this.imgAddToSceneOption = (ImageView) view.findViewById(R.id.imgAddToSceneOption);
            this.imgAddSchedulerOption = (ImageView) view.findViewById(R.id.imgAddSchedulerOption);
            this.imgRenameOption = (ImageView) view.findViewById(R.id.imgRenameOption);

            this.imgSwitch = (SwitchButton)view.findViewById(R.id.imgSwitch);
            this.linearParent = (LinearLayout) view.findViewById(R.id.linearParent);
            this.linearOptionsMenu = (LinearLayout)view.findViewById(R.id.linearOptionsMenu);

            this.txtMachineName.setVisibility(View.GONE);
        }
    }

    public class GridViewHolder extends ViewHolder  {
        public  TextView txtSwitchName, txtMachineName;
        public LinearLayout linearSwitch;
        public SwitchButton imgSwitch;

        public GridViewHolder ( View view ) {
            super ( view );
            this.txtSwitchName = (TextView) view.findViewById(R.id.txtSwitchName);
            this.txtMachineName = (TextView) view.findViewById(R.id.txtMachineName);
            this.linearSwitch = (LinearLayout) view.findViewById(R.id.linearSwitch);
            this.imgSwitch = (SwitchButton)view.findViewById(R.id.imgSwitch);

          /*  this.txtMachineName.setVisibility(View.GONE);*/
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

    public  void setSwitchStatus(ArrayList<XMLValues> _switchStatus) {
        this.switchStatus = _switchStatus;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                ViewGroup viewgroup1 = (ViewGroup) mInflater.inflate(R.layout.scene_switch_full_item, parent, false);
                ListViewHolder listHolder = new ListViewHolder(viewgroup1);
                return listHolder;
            case 1:
                ViewGroup viewgroup2 = (ViewGroup) mInflater.inflate(R.layout.switch_grid_item, parent, false);
                GridViewHolder gridHolder = new GridViewHolder(viewgroup2);
                return gridHolder;
            default:
                ViewGroup viewgroup3 = (ViewGroup) mInflater.inflate(R.layout.switch_grid_item, parent, false);
                GridViewHolder gridHolder1 = new GridViewHolder(viewgroup3);
                return gridHolder1;
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final Cursor cursor) {
        final int switchNameIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME);
        final String switchName = cursor.getString(switchNameIndex);
        int machineNameIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MNAME);
        final String machineName = cursor.getString(machineNameIndex);
        final int machineIPIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP);
        final int machineIdIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID);
        final String machineId = cursor.getString(machineIdIndex);

        final int isActiveIdx = cursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE);
        final String isActive = cursor.getString(isActiveIdx);

        int componentIdIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID);
        String componentName = cursor.getString(componentIdIndex);

        final int position = cursor.getPosition();
        //final String strPosition = String.format("%02d", (position + 1));
        final String strPosition = componentName.substring(2,4);

            switch (viewHolder.getItemViewType()) {
                case 0:
                    final ListViewHolder listHolder = (ListViewHolder) viewHolder;

                    listHolder.txtSwitchName.setText(cursor.getString(switchNameIndex));
                   // listHolder.txtMachineName.setText(cursor.getString(machineNameIndex));

                    if (this.switchStatus.get(position).tagValue.equals(AppConstants.OFF_VALUE)) {
                        listHolder.imgSwitch.setChecked(false);
                    } else {
                        listHolder.imgSwitch.setChecked(true);
                    }
                    if (isActive.equals("false")) {
                        listHolder.linearParent.setAlpha(0.5f);
                        listHolder.linearOptionsMenu.setAlpha(0.5f);
                        listHolder.imgRenameOption.setClickable(false);
                        listHolder.imgAddToSceneOption.setClickable(false);
                        listHolder.imgFavoriteOption.setClickable(false);
                        listHolder.imgAddSchedulerOption.setClickable(false);
                        listHolder.imgSwitch.setEnabled(false);
                        listHolder.imgSwitch.setClickable(false);

                    } else {
                        listHolder.linearParent.setAlpha(1.0f);
                        listHolder.linearOptionsMenu.setAlpha(1.0f);
                        listHolder.imgSwitch.setEnabled(true);

                        listHolder.imgSwitch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                listHolder.imgSwitch.toggle();
                                String machineIP = cursor.getString(machineIPIndex);
                                if (!machineIP.startsWith("http://")) {
                                    machineIP = "http://" + machineIP;
                                }

                                if (listHolder.imgSwitch.isChecked()) {// listHolder.linearSwitch.setBackgroundResource(R.drawable.on_switch_border);

                                    String CHANGE_STATUS_URL = machineIP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.OFF_VALUE;
                                    String[] params = new String[2];
                                    params[0] = CHANGE_STATUS_URL;
                                    params[1] = machineId;
                                    new ChangeSwitchStatus().execute(params);
                                } else {
                                    //listHolder.linearSwitch.setBackgroundResource(R.drawable.off_switch_border);
                                    String CHANGE_STATUS_URL = machineIP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.ON_VALUE;
                                    String[] params = new String[2];
                                    params[0] = CHANGE_STATUS_URL;
                                    params[1] = machineId;
                                    new ChangeSwitchStatus().execute(params);
                                }

                            }
                        });

                        listHolder.imgRenameOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                _renameClick.onRenameOptionClick(position, switchName);
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
                    final GridViewHolder groupViewHolder = (GridViewHolder) viewHolder;
                    groupViewHolder.txtSwitchName.setText(cursor.getString(switchNameIndex));
                   // groupViewHolder.txtMachineName.setText(cursor.getString(machineNameIndex));

                    if (this.switchStatus.get(position).tagValue.equals(AppConstants.OFF_VALUE)) {
                        groupViewHolder.imgSwitch.setChecked(false);
                    } else {
                        groupViewHolder.imgSwitch.setChecked(true);
                    }

                    if (isActive.equals("false")) {
                        groupViewHolder.linearSwitch.setAlpha(0.5f);
                        groupViewHolder.linearSwitch.setLongClickable(false);
                        groupViewHolder.imgSwitch.setEnabled(false);
                        groupViewHolder.imgSwitch.setClickable(false);

                    } else {
                        groupViewHolder.linearSwitch.setAlpha(1.0f);
                        groupViewHolder.imgSwitch.setEnabled(true);
                        groupViewHolder.imgSwitch.setClickable(false);

                        groupViewHolder.imgSwitch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                groupViewHolder.imgSwitch.toggle();

                                String machineIP = cursor.getString(machineIPIndex);
                                if (!machineIP.startsWith("http://")) {
                                    machineIP = "http://" + machineIP;
                                }

                                if (groupViewHolder.imgSwitch.isChecked()) {
                                    String CHANGE_STATUS_URL = machineIP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.OFF_VALUE;
                                    String[] params = new String[2];
                                    params[0] = CHANGE_STATUS_URL;
                                    params[1] = machineId;
                                    new ChangeSwitchStatus().execute(params);
                                } else {
                                    String CHANGE_STATUS_URL = machineIP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.ON_VALUE;
                                    String[] params = new String[2];
                                    params[0] = CHANGE_STATUS_URL;
                                    params[1] = machineId;
                                    new ChangeSwitchStatus().execute(params);
                                }
                            }
                        });

                        groupViewHolder.linearSwitch.setOnLongClickListener(new View.OnLongClickListener() {
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

    public class ChangeSwitchStatus extends AsyncTask<String, Void, Void> {
        boolean isError = false;
        String parameter, machineId, isMachineActive;
        Cursor machineCursor;
        DatabaseHelper dbHelper = new DatabaseHelper(mCtx);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_dialog.setMessage("Sending request to machine.. " + switchtimeOutErrorCount);
            progress_dialog.show();
            _switchClick.onCheckedPreChangeClick(0);
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
}
