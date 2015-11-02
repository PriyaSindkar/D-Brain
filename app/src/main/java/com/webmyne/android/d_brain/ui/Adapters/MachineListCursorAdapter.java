package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.webmyne.android.d_brain.ui.Activities.TouchPanelActivity;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onMachineStateChangeListener;
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
public class MachineListCursorAdapter extends CursorRecyclerViewAdapter<MachineListCursorAdapter.ViewHolder>{
    private  Context mCtx;
    static int VIEW_TYPE;
    private int totalNoOfSwitches;
    private ArrayList<XMLValues> switchStatus;
    private Cursor mCursor;

    public onRenameClickListener _renameClick;
    public onDeleteClickListener _deleteClick;
    public onMachineStateChangeListener _singleClick;

    public MachineListCursorAdapter(Context context){
        super(context );
        mCtx = context;
    }


    public MachineListCursorAdapter(Context context, Cursor cursor){
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
        public  TextView txtMachineName, txtMachineIPAddress, txtMachineSerialNo;
        public ImageView imgDeleteOption, imgRenameOption;
        public LinearLayout linearSwitch;
        private SwitchButton imgSwitch;

        public ListViewHolder ( View view ) {
            super ( view );
            this.txtMachineName = (TextView) view.findViewById(R.id.txtMachineName);
            this.txtMachineIPAddress = (TextView) view.findViewById(R.id.txtMachineIPAddress);
            this.txtMachineSerialNo = (TextView) view.findViewById(R.id.txtMachineSerialNo);
            this.linearSwitch = (LinearLayout) view.findViewById(R.id.linearSwitch);

            this.imgDeleteOption = (ImageView) view.findViewById(R.id.imgDeleteOption);
            this.imgRenameOption = (ImageView) view.findViewById(R.id.imgRenameOption);
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

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void setType(int type){
        VIEW_TYPE = type;
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
        mCursor = cursor;
        final int machineNameIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME);
        final String machineName = cursor.getString(machineNameIndex);
        final int machineIPIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP);
        final String machineIP = cursor.getString(machineIPIndex);
        int machineSerialNoIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_M_SERIALNO);
        int machineIdIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID);
        final String machineId = cursor.getString(machineIdIndex);
        int isActiveIdx = cursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE);
        final String isActive = cursor.getString(isActiveIdx);

        final int position = cursor.getPosition();
        final String strPosition = String.format("%02d", (position + 1));

        final ListViewHolder listHolder = ( ListViewHolder ) viewHolder;
        AdvancedSpannableString sp = new AdvancedSpannableString(cursor.getString(machineNameIndex));
        sp.setColor(mCtx.getResources().getColor(R.color.white), cursor.getString(machineNameIndex));
        sp.setBold(cursor.getString(machineNameIndex));
        listHolder.txtMachineName.setText(sp);

        sp = new AdvancedSpannableString(cursor.getString(machineIPIndex));
        sp.setColor(mCtx.getResources().getColor(R.color.yellow), cursor.getString(machineIPIndex));
        listHolder.txtMachineIPAddress.setText(sp);

        sp = new AdvancedSpannableString(cursor.getString(machineSerialNoIndex));
        sp.setColor(mCtx.getResources().getColor(R.color.yellow), cursor.getString(machineSerialNoIndex));
        listHolder.txtMachineSerialNo.setText(sp);


        if(isActive.equals("true")) {
            listHolder.imgSwitch.setChecked(true);
        } else {
            listHolder.imgSwitch.setChecked(false);
        }

        DatabaseHelper dbHelper = new DatabaseHelper(mCtx);
        int machineCount = 0;
        //get no. of machines count
        try {
            dbHelper.openDataBase();
            Cursor machineCursor =  dbHelper.getAllMachines();

            if(machineCursor != null) {
                machineCount = machineCursor.getCount();
            }
            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(machineCount > 1) {
            listHolder.imgDeleteOption.setVisibility(View.VISIBLE);
        } else {
            listHolder.imgDeleteOption.setVisibility(View.GONE);
        }

        listHolder.imgDeleteOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _deleteClick.onDeleteOptionClick(position);
            }
        });

        listHolder.imgRenameOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _renameClick.onRenameOptionClick(position, machineName, machineIP);
            }
        });

        listHolder.imgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _singleClick.onMachineEnabledDisabled(position, isChecked);
            }
        });
    }

    public void setRenameClickListener(onRenameClickListener obj){
        this._renameClick = obj;
    }

    public void setDeleteClickListener(onDeleteClickListener obj){
        this._deleteClick = obj;
    }

    public void setMachineStateChanged(onMachineStateChangeListener obj){
        this._singleClick = obj;
    }


}
