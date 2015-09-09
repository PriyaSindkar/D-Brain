package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;
import com.webmyne.android.d_brain.ui.Model.onItemClickListener;

import java.util.ArrayList;


public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.ViewHolder> {
    private ArrayList<SceneItemsDataObject> mDataset;
    static int VIEW_TYPE;
    public Context _ctx;
    public onItemClickListener _onItemClick;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    public class SwitchViewHolder extends ViewHolder {
        public TextView txtSwitchName;
        public LinearLayout linearSwitch;

        public SwitchViewHolder ( View itemView ) {
            super ( itemView );
            txtSwitchName = (TextView) itemView.findViewById(R.id.txtSwitchName);
            linearSwitch = (LinearLayout) itemView.findViewById(R.id.linearSwitch);
        }
    }

    public class DimmerViewHolder extends ViewHolder{
        public TextView txtDimmerName;
        public LinearLayout linearParent;

        public DimmerViewHolder ( View itemView ) {
            super ( itemView );
            txtDimmerName = (TextView) itemView.findViewById(R.id.txtSwitchName);
            linearParent = (LinearLayout) itemView.findViewById(R.id.linearParent);
        }
    }

    public class MotorViewHolder extends ViewHolder{
        public TextView txtMotorName;
        public LinearLayout linearParent;

        public MotorViewHolder ( View itemView ) {
            super ( itemView );
            txtMotorName = (TextView) itemView.findViewById(R.id.txtMotorName);
            linearParent = (LinearLayout) itemView.findViewById(R.id.linearParent);
        }
    }


    @Override
    public int getItemViewType(int position) {
        /*if (mDataset.get(position) instanceof SceneItemsDataObject) {
            return 0;
        } else if (mDataset.get(position) instanceof String) {
            return 1;
        }*/

        if(mDataset.get(position).getSceneControlType() == 0) {
            return 0;
        } else if (mDataset.get(position).getSceneControlType() == 1){
            return 1;
        } else if (mDataset.get(position).getSceneControlType() == 2){
            return 2;
        }
/*
        if(VIEW_TYPE == 0) {
            return 0;
        } else if(VIEW_TYPE == 1) {
            return 1;
        } else if (VIEW_TYPE == 2) {
            return 2;
        }*/

        return -1;
    }

    public void add(int position, SceneItemsDataObject item) {

        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(SceneItemsDataObject item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setType(int type){
        VIEW_TYPE = type;
    }

    public SceneAdapter(Context ctx,ArrayList<SceneItemsDataObject> myDataset) {
        this._ctx = ctx;
        mDataset = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        ViewHolder vh;

        LayoutInflater mInflater = LayoutInflater.from (parent.getContext());
        switch (viewType) {

            case 0:
                ViewGroup viewgroup1 = ( ViewGroup ) mInflater.inflate ( R.layout.scene_switch_full_item, parent, false );
                SwitchViewHolder listHolder = new SwitchViewHolder (viewgroup1);
                return listHolder;
            case 1:
                ViewGroup viewgroup2 = ( ViewGroup ) mInflater.inflate ( R.layout.scene_dimmer_full_item, parent, false );
                DimmerViewHolder gridHolder = new DimmerViewHolder (viewgroup2);
                return gridHolder;

            case 2:
                ViewGroup viewgroup3 = ( ViewGroup ) mInflater.inflate ( R.layout.motor_list_item, parent, false );
                MotorViewHolder thirdHolder = new MotorViewHolder (viewgroup3);
                return thirdHolder;

            default:
                ViewGroup viewgroup4 = ( ViewGroup ) mInflater.inflate ( R.layout.scene_switch_full_item, parent, false );
                DimmerViewHolder gridHolder1 = new DimmerViewHolder (viewgroup4);
                return gridHolder1;
        }

    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
       /* final SceneItemsDataObject name = (SceneItemsDataObject) mDataset.get(position);*/

        switch (viewHolder.getItemViewType () ) {
            case 0:
                SwitchViewHolder switchHolder = ( SwitchViewHolder ) viewHolder;
                switchHolder.txtSwitchName.setText("Switch Name ");


                switchHolder.linearSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _onItemClick._onItemClickListener();
                    }
                });

                break;
            case 1:
                DimmerViewHolder dimmerHolder = ( DimmerViewHolder ) viewHolder;
                dimmerHolder.txtDimmerName.setText("Dimmer Name");
                dimmerHolder.linearParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _onItemClick._onItemClickListener();
                    }
                });

                break;

            case 2:
                MotorViewHolder motorViewHolder = ( MotorViewHolder ) viewHolder;
                motorViewHolder.txtMotorName.setText("Motor Name");

                motorViewHolder.linearParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _onItemClick._onItemClickListener();
                    }
                });
                break;

        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setOnItemClick(onItemClickListener obj){
        this._onItemClick = obj;
    }

}
