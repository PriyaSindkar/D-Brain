package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;


public class SensorsListAdapter extends RecyclerView.Adapter<SensorsListAdapter.ViewHolder> {
    public Context _ctx;
    static int VIEW_TYPE;
    private int totalNoOfSensors;
    public onLongClickListener _longClick;
    public onSingleClickListener _singleClick;
    public onDeleteClickListener _deleteClick;
    public onRenameClickListener _renameClick;

    // 0 - for List Type
    // 1 - for Gri Type

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }



    public class GridViewHolder extends ViewHolder{
        public  TextView txtSensorName, txtMachineName, txtSensorDetails;
        public LinearLayout linearSensor;
        public ImageView imgRenameOption, imgDeleteOption;

        public GridViewHolder ( View view ) {
            super ( view );
            this.txtSensorName = (TextView) view.findViewById(R.id.txtSensorName);
            this.txtMachineName = (TextView) view.findViewById(R.id.txtMachineName);
            this.txtSensorDetails = (TextView) view.findViewById(R.id.txtSensorDetails);
            this.linearSensor = (LinearLayout) view.findViewById(R.id.linearSensor);
            this.imgDeleteOption = (ImageView) view.findViewById(R.id.imgDeleteOption);
            this.imgRenameOption = (ImageView) view.findViewById(R.id.imgRenameOption);
        }
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public SensorsListAdapter(Context ctx, int totalNoOfSensors) {
        this._ctx = ctx;
        this.totalNoOfSensors = totalNoOfSensors;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        ViewHolder vh;

        LayoutInflater mInflater = LayoutInflater.from (parent.getContext());


        ViewGroup viewgroup2 = ( ViewGroup ) mInflater.inflate(R.layout.sensor_list_item, parent, false);
        GridViewHolder gridHolder = new GridViewHolder (viewgroup2);
        return gridHolder;
    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            final GridViewHolder holder = ( GridViewHolder ) viewHolder;
            holder.txtSensorName.setText("Sensor Name: "+ "Sensor " + position);
            holder.txtMachineName.setText("Machine Name: My Machine");
            holder.txtSensorDetails.setText("Alert fired when the main gate is opened forcibly");



            holder.linearSensor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _singleClick.onSingleClick(position);
                }
            });

            holder.imgDeleteOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _deleteClick.onDeleteOptionClick(position);
                }
            });

            holder.imgRenameOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _renameClick.onRenameOptionClick(position,  holder.txtSensorName.getText().toString().trim());
                }
            });
    }

    @Override
    public int getItemCount() {
        return this.totalNoOfSensors;
    }

    public void setSingleClickListener(onSingleClickListener obj){
        this._singleClick = obj;
    }

    public void setLongClickListener(onLongClickListener obj){
        this._longClick = obj;
    }

    public void setDeleteClickListener(onDeleteClickListener obj) {
        this._deleteClick = obj;
    }

    public void setRenameClickListener(onRenameClickListener obj){
        this._renameClick = obj;
    }

}
