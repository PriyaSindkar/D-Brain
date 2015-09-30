package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;


public class MachineListAdapter extends RecyclerView.Adapter<MachineListAdapter.ViewHolder> {
    static int VIEW_TYPE;
    public Context _ctx;
    private int totalNoOfMachines;
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


    public class ListViewHolder extends ViewHolder{
        public  TextView txtMachineName, txtMachineIPAddress, txtMachineSerialNo;
        public ImageView imgDeleteOption, imgRenameOption;
        public LinearLayout linearMachine;

        public ListViewHolder ( View view ) {
            super ( view );
            this.txtMachineName = (TextView) view.findViewById(R.id.txtMachineName);
            this.txtMachineIPAddress = (TextView) view.findViewById(R.id.txtMachineIPAddress);
            this.txtMachineSerialNo = (TextView) view.findViewById(R.id.txtMachineSerialNo);
            this.linearMachine = (LinearLayout) view.findViewById(R.id.linearMachine);

            this.imgDeleteOption = (ImageView) view.findViewById(R.id.imgDeleteOption);
            this.imgRenameOption = (ImageView) view.findViewById(R.id.imgRenameOption);
        }
    }

    /*public class ListViewHolder extends ViewHolder{
        public  TextView txtSwitchName;
        public LinearLayout linearSwitch;
        public SwitchButton imgSwitch;

        public ListViewHolder ( View view ) {
            super ( view );
            this.txtSwitchName = (TextView) view.findViewById(R.id.txtSwitchName);
            this.linearSwitch = (LinearLayout) view.findViewById(R.id.linearSwitch);
            this.imgSwitch = (SwitchButton)view.findViewById(R.id.imgSwitch);
        }
    }*/


    /*@Override
    public int getItemViewType(int position) {
        if(VIEW_TYPE == 0)
            return 0;
        else
            return 1;
    }*/

  /*  public void add(int position, String item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(String item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }*/



    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setType(int type){
        VIEW_TYPE = type;
    }

    public MachineListAdapter(Context ctx, int totalNoOfMachines) {
        this._ctx = ctx;
        this.totalNoOfMachines = totalNoOfMachines;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from (parent.getContext());
        ViewGroup viewgroup1 = ( ViewGroup ) mInflater.inflate ( R.layout.machine_list_item, parent, false );
        ListViewHolder listHolder = new ListViewHolder (viewgroup1);
        return listHolder;

    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


           final ListViewHolder listHolder = ( ListViewHolder ) viewHolder;
            listHolder.txtMachineName.setText("Machine " + position);

            listHolder.linearMachine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _singleClick.onSingleClick(position);
                }
            });

            listHolder.imgDeleteOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _deleteClick.onDeleteOptionClick(position);
                }
            });


            listHolder.imgRenameOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _renameClick.onRenameOptionClick(position,listHolder.txtMachineName.getText().toString().trim() );
                }
            });
    }

    @Override
    public int getItemCount() {
        return this.totalNoOfMachines;
    }

    public void setSingleClickListener(onSingleClickListener obj){
        this._singleClick = obj;
    }

    public void setLongClickListener(onLongClickListener obj){
        this._longClick = obj;
    }

    public void setDeleteClickListener(onDeleteClickListener obj){
        this._deleteClick = obj;
    }

    public void setRenameClickListener(onRenameClickListener obj){
        this._renameClick = obj;
    }

}
