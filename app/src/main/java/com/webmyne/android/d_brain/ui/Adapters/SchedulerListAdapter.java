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


public class SchedulerListAdapter extends RecyclerView.Adapter<SchedulerListAdapter.ViewHolder> {
    public Context _ctx;
    static int VIEW_TYPE;
    private int totalNoOfSchedulers;
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
        public  TextView txtSchedulerName, txtMachineName;
        public LinearLayout linearScene;
        public ImageView imgRenameOption, imgDeleteOption;

        public GridViewHolder ( View view ) {
            super ( view );
            this.txtSchedulerName = (TextView) view.findViewById(R.id.txtSceneName);
            this.txtMachineName = (TextView) view.findViewById(R.id.txtMachineName);
            this.linearScene = (LinearLayout) view.findViewById(R.id.linearScene);
            this.imgDeleteOption = (ImageView) view.findViewById(R.id.imgDeleteOption);
            this.imgRenameOption = (ImageView) view.findViewById(R.id.imgRenameOption);
        }
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public SchedulerListAdapter(Context ctx, int totalNoOfSchedulers) {
        this._ctx = ctx;
        this.totalNoOfSchedulers = totalNoOfSchedulers;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        ViewHolder vh;

        LayoutInflater mInflater = LayoutInflater.from (parent.getContext());


        ViewGroup viewgroup2 = ( ViewGroup ) mInflater.inflate(R.layout.fragment_scene_list_item, parent, false);
        GridViewHolder gridHolder = new GridViewHolder (viewgroup2);
        return gridHolder;
    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            final GridViewHolder holder = ( GridViewHolder ) viewHolder;
            holder.txtSchedulerName.setText("Scheduler Name: "+ "Scheduler " + position);
            holder.txtMachineName.setText("Machine Name: My Machine");



            holder.linearScene.setOnClickListener(new View.OnClickListener() {
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
                    _renameClick.onRenameOptionClick(position, holder.txtSchedulerName.getText().toString().trim() );
                }
            });
    }

    @Override
    public int getItemCount() {
        return this.totalNoOfSchedulers;
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
