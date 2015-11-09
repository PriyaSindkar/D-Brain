package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onMachineStateChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;


/**
 * Created by priyasindkar on 14-09-2015.
 */
public class SchedulersListCursorAdapter extends CursorRecyclerViewAdapter<SchedulersListCursorAdapter.ViewHolder>{
    private  Context mCtx;
    static int VIEW_TYPE;
    private Cursor mCursor;

    public onRenameClickListener _renameClick;
    public onDeleteClickListener _deleteClick;
    public onMachineStateChangeListener _checkChangeClick;

    public SchedulersListCursorAdapter(Context context){
        super(context );
        mCtx = context;
    }


    public SchedulersListCursorAdapter(Context context, Cursor cursor){
        super(context,cursor);
        mCtx = context;
        mCursor = cursor;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    public class ListViewHolder extends ViewHolder {
        public  TextView txtMachineName, txtMachineIPAddress, txtMachineSerialNo, txtComponentName;
        public ImageView imgDeleteOption, imgRenameOption;
        public LinearLayout linearSwitch, linearParent;
        private SwitchButton imgSwitch;

        public ListViewHolder ( View view ) {
            super ( view );
            this.txtMachineName = (TextView) view.findViewById(R.id.txtMachineName);
            this.txtMachineIPAddress = (TextView) view.findViewById(R.id.txtMachineIPAddress);
            this.txtMachineSerialNo = (TextView) view.findViewById(R.id.txtMachineSerialNo);
            this.txtComponentName = (TextView) view.findViewById(R.id.txtComponentName);
            this.linearSwitch = (LinearLayout) view.findViewById(R.id.linearSwitch);

            this.imgDeleteOption = (ImageView) view.findViewById(R.id.imgDeleteOption);
            this.imgRenameOption = (ImageView) view.findViewById(R.id.imgRenameOption);
            this.imgSwitch = (SwitchButton) view.findViewById(R.id.imgSwitch);
            this.linearParent = (LinearLayout) view.findViewById(R.id.linearParent);

            this.txtComponentName.setVisibility(View.VISIBLE);
            this.txtMachineIPAddress.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
            ViewGroup viewgroup1 = ( ViewGroup ) mInflater.inflate ( R.layout.machine_list_item, parent, false );
            ListViewHolder listHolder = new ListViewHolder (viewgroup1);
            return listHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final Cursor cursor) {
        final int schedulerNameIdx = cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_NAME);
        final String schedulerName = cursor.getString(schedulerNameIdx);

        final int componentNameIdx = cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_SCENE_NAME);
        final String componentName = cursor.getString(componentNameIdx);

        int machineIPIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_MIP);
        int dateTimeIdx = cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_DATETIME);

        int schedulerTypeIdx= cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_TYPE);
        String schedulerType = cursor.getString(schedulerTypeIdx);

        int isActiveiIdx= cursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE);
        String isActive = cursor.getString(isActiveiIdx);

        int defOnOffStateIdx = cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_DEF_ON_OFF);
        String defOnOffState = cursor.getString(defOnOffStateIdx);

        final int position = cursor.getPosition();
        final String strPosition = String.format("%02d", (position + 1));

        final ListViewHolder listHolder = ( ListViewHolder ) viewHolder;
        AdvancedSpannableString sp = new AdvancedSpannableString(cursor.getString(schedulerNameIdx));
        sp.setColor(mCtx.getResources().getColor(R.color.white), cursor.getString(schedulerNameIdx));
        sp.setBold(cursor.getString(schedulerNameIdx));
        listHolder.txtMachineName.setText(sp);

        listHolder.txtComponentName.setText(componentName);

        if(defOnOffState.equals(AppConstants.OFF_VALUE)) {
            listHolder.imgSwitch.setChecked(false);
        } else {
            listHolder.imgSwitch.setChecked(true);
        }
/*
        if(schedulerType.equals(AppConstants.SCENE_TYPE)) {
            listHolder.txtMachineIPAddress.setVisibility(View.GONE);
        } else {
            sp = new AdvancedSpannableString(cursor.getString(machineIPIndex));
            sp.setColor(mCtx.getResources().getColor(R.color.yellow), cursor.getString(machineIPIndex));
            listHolder.txtMachineIPAddress.setText(sp);
        }*/

        sp = new AdvancedSpannableString("Scheduled at: "+cursor.getString(dateTimeIdx));
        sp.setColor(mCtx.getResources().getColor(R.color.yellow), "Scheduled at: ");
        listHolder.txtMachineSerialNo.setText(sp);

        listHolder.imgSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getActionMasked() == MotionEvent.ACTION_MOVE;
            }
        });

        if(isActive.equals("false")) {
            listHolder.linearParent.setAlpha(0.5f);
            listHolder.imgRenameOption.setClickable(false);
            listHolder.imgDeleteOption.setClickable(false);
        } else {
            listHolder.linearParent.setAlpha(1.0f);
            listHolder.imgRenameOption.setClickable(true);
            listHolder.imgDeleteOption.setClickable(true);

            listHolder.imgDeleteOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _deleteClick.onDeleteOptionClick(position);
                }
            });

            listHolder.imgRenameOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _renameClick.onRenameOptionClick(position, schedulerName);
                }
            });

            listHolder.imgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    _checkChangeClick.onMachineEnabledDisabled(position, isChecked);
                }
            });
        }
    }

    public void setRenameClickListener(onRenameClickListener obj){
        this._renameClick = obj;
    }

    public void setDeleteClickListener(onDeleteClickListener obj){
        this._deleteClick = obj;
    }

    public void setOnDefaultValueChanged(onMachineStateChangeListener obj){
        this._checkChangeClick = obj;
    }


}
