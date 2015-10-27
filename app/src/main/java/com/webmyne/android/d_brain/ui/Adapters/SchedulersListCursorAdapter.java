package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.xmlHelpers.XMLValues;

import java.util.ArrayList;


/**
 * Created by priyasindkar on 14-09-2015.
 */
public class SchedulersListCursorAdapter extends CursorRecyclerViewAdapter<SchedulersListCursorAdapter.ViewHolder>{
    private  Context mCtx;
    static int VIEW_TYPE;
    private Cursor mCursor;

    public onRenameClickListener _renameClick;
    public onDeleteClickListener _deleteClick;

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
        public  TextView txtMachineName, txtMachineIPAddress, txtMachineSerialNo;
        public ImageView imgDeleteOption, imgRenameOption;
        public LinearLayout linearSwitch;

        public ListViewHolder ( View view ) {
            super ( view );
            this.txtMachineName = (TextView) view.findViewById(R.id.txtMachineName);
            this.txtMachineIPAddress = (TextView) view.findViewById(R.id.txtMachineIPAddress);
            this.txtMachineSerialNo = (TextView) view.findViewById(R.id.txtMachineSerialNo);
            this.linearSwitch = (LinearLayout) view.findViewById(R.id.linearSwitch);

            this.imgDeleteOption = (ImageView) view.findViewById(R.id.imgDeleteOption);
            this.imgRenameOption = (ImageView) view.findViewById(R.id.imgRenameOption);
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
        int machineIPIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_MIP);
        int dateTimeIdx = cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_DATETIME);

        int schedulerTypeIdx= cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_TYPE);
        String schedulerType = cursor.getString(schedulerTypeIdx);

        Log.e("schedulerType", schedulerType);

        final int position = cursor.getPosition();
        final String strPosition = String.format("%02d", (position + 1));

        final ListViewHolder listHolder = ( ListViewHolder ) viewHolder;
        AdvancedSpannableString sp = new AdvancedSpannableString("Scheduler Name: "+cursor.getString(schedulerNameIdx));
        sp.setColor(mCtx.getResources().getColor(R.color.yellow), "Scheduler Name: ");
        listHolder.txtMachineName.setText(sp);

        if(schedulerType.equals(AppConstants.SCENE_TYPE)) {
            listHolder.txtMachineIPAddress.setVisibility(View.GONE);
        } else {
            sp = new AdvancedSpannableString("Machine IP Address: " + cursor.getString(machineIPIndex));
            sp.setColor(mCtx.getResources().getColor(R.color.yellow), "Machine IP Address: ");
            listHolder.txtMachineIPAddress.setText(sp);
        }


        sp = new AdvancedSpannableString("Scheduled Date Time.: "+cursor.getString(dateTimeIdx));
        sp.setColor(mCtx.getResources().getColor(R.color.yellow), "Scheduled Date Time.: ");
        listHolder.txtMachineSerialNo.setText(sp);

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
    }

    public void setRenameClickListener(onRenameClickListener obj){
        this._renameClick = obj;
    }

    public void setDeleteClickListener(onDeleteClickListener obj){
        this._deleteClick = obj;
    }


}
