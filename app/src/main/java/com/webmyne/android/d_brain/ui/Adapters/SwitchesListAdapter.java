package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Widgets.MotorSwitchItem;

import java.util.ArrayList;


public class SwitchesListAdapter extends RecyclerView.Adapter<SwitchesListAdapter.ViewHolder> {
    static int VIEW_TYPE;
    public Context _ctx;
    private int totalNoOfSwitches;
    public onLongClickListener _longClick;
    public onSingleClickListener _singleClick;
    public onFavoriteClickListener _favoriteClick;
    public onAddToSceneClickListener _addToSceneClick;
    public onAddSchedulerClickListener _addSchedulerClick;
    public onRenameClickListener _renameClick;

    // 0 - for List Type
    // 1 - for Gri Type

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    public class ListViewHolder extends ViewHolder{
        public  TextView txtSwitchName;
        public ImageView imgFavoriteOption, imgAddToSceneOption, imgAddSchedulerOption, imgRenameOption;
        public LinearLayout linearSwitch;
        public SwitchButton imgSwitch;

        public ListViewHolder ( View view ) {
            super ( view );
            this.txtSwitchName = (TextView) view.findViewById(R.id.txtSwitchName);
            this.linearSwitch = (LinearLayout) view.findViewById(R.id.linearSwitch);

            this.imgFavoriteOption = (ImageView) view.findViewById(R.id.imgFavoriteOption);
            this.imgAddToSceneOption = (ImageView) view.findViewById(R.id.imgAddToSceneOption);
            this.imgAddSchedulerOption = (ImageView) view.findViewById(R.id.imgAddSchedulerOption);
            this.imgRenameOption = (ImageView) view.findViewById(R.id.imgRenameOption);

            this.imgSwitch = (SwitchButton)view.findViewById(R.id.imgSwitch);
        }
    }

    public class GridViewHolder extends ViewHolder{
        public  TextView txtSwitchName;
        public LinearLayout linearSwitch;
        public SwitchButton imgSwitch;

        public GridViewHolder ( View view ) {
            super ( view );
            this.txtSwitchName = (TextView) view.findViewById(R.id.txtSwitchName);
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

    public SwitchesListAdapter(Context ctx, int totalNoOfSwitches) {
        this._ctx = ctx;
        this.totalNoOfSwitches = totalNoOfSwitches;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        ViewHolder vh;

        LayoutInflater mInflater = LayoutInflater.from (parent.getContext());
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
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        switch (viewHolder.getItemViewType () ) {
            case 0:
               final ListViewHolder listHolder = ( ListViewHolder ) viewHolder;
                listHolder.txtSwitchName.setText("Switch " + position);

                listHolder.imgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            listHolder.linearSwitch.setBackgroundResource(R.drawable.on_switch_border);
                        } else {
                            listHolder.linearSwitch.setBackgroundResource(R.drawable.off_switch_border);
                        }

                    }
                });

                listHolder.linearSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _singleClick.onSingleClick(position);
                    }
                });

                listHolder.imgFavoriteOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _favoriteClick.onFavoriteOptionClick(position);
                    }
                });

                listHolder.imgAddToSceneOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _addToSceneClick.onAddToSceneOptionClick(position);
                    }
                });

                listHolder.imgAddSchedulerOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _addSchedulerClick.onAddSchedulerOptionClick(position);
                    }
                });

                listHolder.imgRenameOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _renameClick.onRenameOptionClick(position);
                    }
                });

                break;
            case 1:
                final GridViewHolder groupViewHolder = ( GridViewHolder ) viewHolder;
                groupViewHolder.txtSwitchName.setText("Switch "+ position);

                groupViewHolder.linearSwitch.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        _longClick.onLongClick(position);
                        return false;
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return this.totalNoOfSwitches;
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

}
