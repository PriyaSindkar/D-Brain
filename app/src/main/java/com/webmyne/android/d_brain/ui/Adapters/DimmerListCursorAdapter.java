package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.DimmerListActivity;
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
import com.webmyne.android.d_brain.ui.xmlHelpers.XMLValues;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public DimmerListCursorAdapter(Context context){
        super(context );
        mCtx = context;
    }


    public DimmerListCursorAdapter(Context context, Cursor cursor, ArrayList<XMLValues> _dimmerStatus){
        super(context,cursor);
        mCtx = context;
        this.dimmerStatus = _dimmerStatus;
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

        int componentIdIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID);
        String componentName = cursor.getString(componentIdIndex);

        final int isActiveIdx = cursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE);
        final String isActive = cursor.getString(isActiveIdx);

        String machineIP = cursor.getString(machineIPIndex);
        if(!machineIP.startsWith("http://")) {
            machineIP = "http://" + machineIP;
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

                            DimmerListActivity.isDelay = true;
                            new ChangeDimmerStatus().execute(CHANGE_STATUS_URL);
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

                            DimmerListActivity.isDelay = true;
                            new ChangeDimmerStatus().execute(CHANGE_STATUS_URL);
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

                            DimmerListActivity.isDelay = true;
                            new ChangeDimmerStatus().execute(CHANGE_STATUS_URL);

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

                            DimmerListActivity.isDelay = true;
                            new ChangeDimmerStatus().execute(CHANGE_STATUS_URL);
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

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL urlValue = new URL(params[0]);
                Log.e("# url change dimmer", urlValue.toString());

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
            try{
                _switchClick.onCheckedChangeClick(0);

            }catch(Exception e){
            }
        }
    }
}
