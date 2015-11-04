package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.SwitchesListActivity;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
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

    public SwitchListCursorAdapter(Context context){
        super(context );
        mCtx = context;
    }


    public SwitchListCursorAdapter(Context context, Cursor cursor, ArrayList<XMLValues> _switchStatus){
        super(context,cursor);
        mCtx = context;
        this.switchStatus = _switchStatus;
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
                ViewGroup viewgroup1 = ( ViewGroup ) mInflater.inflate ( R.layout.scene_switch_full_item, parent, false );
                ListViewHolder listHolder = new ListViewHolder (viewgroup1);
                return listHolder;
            case 1:
                ViewGroup viewgroup2 = ( ViewGroup ) mInflater.inflate(R.layout.switch_grid_item, parent, false);
                GridViewHolder gridHolder = new GridViewHolder (viewgroup2);
                return gridHolder;
            default:
                ViewGroup viewgroup3 = ( ViewGroup ) mInflater.inflate ( R.layout.scene_switch_full_item, parent, false );
                GridViewHolder gridHolder1 = new GridViewHolder (viewgroup3);
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
                    listHolder.txtMachineName.setText(cursor.getString(machineNameIndex));

                    if (switchStatus.get(position).tagValue.equals(AppConstants.OFF_VALUE)) {
                        listHolder.imgSwitch.setChecked(false);
                        //listHolder.linearSwitch.setBackgroundResource(R.drawable.off_switch_border);
                    } else {
                        listHolder.imgSwitch.setChecked(true);
                        // listHolder.linearSwitch.setBackgroundResource(R.drawable.on_switch_border);
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
                                    SwitchesListActivity.isDelay = true;
                                    new ChangeSwitchStatus().execute(CHANGE_STATUS_URL);
                                } else {
                                    //listHolder.linearSwitch.setBackgroundResource(R.drawable.off_switch_border);
                                    String CHANGE_STATUS_URL = machineIP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.ON_VALUE;
                                    SwitchesListActivity.isDelay = true;
                                    new ChangeSwitchStatus().execute(CHANGE_STATUS_URL);
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
                    groupViewHolder.txtMachineName.setText(cursor.getString(machineNameIndex));

                    if (switchStatus.get(position).tagValue.equals(AppConstants.OFF_VALUE)) {
                        groupViewHolder.imgSwitch.setChecked(false);
                        //listHolder.linearSwitch.setBackgroundResource(R.drawable.off_switch_border);
                    } else {
                        groupViewHolder.imgSwitch.setChecked(true);
                        // listHolder.linearSwitch.setBackgroundResource(R.drawable.on_switch_border);
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

                                if (groupViewHolder.imgSwitch.isChecked()) {// listHolder.linearSwitch.setBackgroundResource(R.drawable.on_switch_border);
                                    String CHANGE_STATUS_URL = machineIP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.OFF_VALUE;
                                    SwitchesListActivity.isDelay = true;
                                    new ChangeSwitchStatus().execute(CHANGE_STATUS_URL);
                                } else {
                                    //listHolder.linearSwitch.setBackgroundResource(R.drawable.off_switch_border);
                                    String CHANGE_STATUS_URL = machineIP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + AppConstants.ON_VALUE;
                                    SwitchesListActivity.isDelay = true;
                                    new ChangeSwitchStatus().execute(CHANGE_STATUS_URL);
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _switchClick.onCheckedPreChangeClick(0);
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL urlValue = new URL(params[0]);
            //    Log.e("# urlValue", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();


            } catch (Exception e) {
                Log.e("# EXP adapter", e.toString());
                isError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
             _switchClick.onCheckedChangeClick(0);

        }
    }
}
