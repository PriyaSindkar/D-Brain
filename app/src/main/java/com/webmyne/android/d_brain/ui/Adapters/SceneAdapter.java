package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
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
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;
import com.webmyne.android.d_brain.ui.Model.onItemClickListener;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.util.ArrayList;


public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.ViewHolder> {
    private ArrayList<SceneItemsDataObject> mDataset;
    static String VIEW_TYPE;
    public Context _ctx;
    public onItemClickListener _onItemClick;
    public onLongClickListener _longClick;
    public onSingleClickListener _singleClick;
    public onDeleteClickListener _onDeleteClick;

    public onCheckedChangeListener _switchClick;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    public class SwitchViewHolder extends ViewHolder {
        public TextView txtSwitchName, txtMachineName;
        public LinearLayout linearSwitch;
        private ImageView imgDeleteOption;
        public SwitchButton imgSwitch;

        public SwitchViewHolder ( View itemView ) {
            super ( itemView );
            this.txtSwitchName = (TextView) itemView.findViewById(R.id.txtSwitchName);
            this.txtMachineName = (TextView) itemView.findViewById(R.id.txtMachineName);
            this.linearSwitch = (LinearLayout) itemView.findViewById(R.id.linearSwitch);
            this.imgDeleteOption = (ImageView) itemView.findViewById(R.id.imgDeleteOption);
            this.imgSwitch = (SwitchButton) itemView.findViewById(R.id.imgSwitch);
        }
    }

    public class DimmerViewHolder extends ViewHolder{
        public TextView txtDimmerName, txtValue, txtMachineName;
        private SeekBar seekBar;
        public  ImageView  imgDeleteOption;
        private LinearLayout linearItem;

        public DimmerViewHolder ( View itemView ) {
            super ( itemView );
            this.txtDimmerName = (TextView) itemView.findViewById(R.id.txtDimmerName);
            this.txtMachineName = (TextView) itemView.findViewById(R.id.txtMachineName);
            this.seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
            this.txtValue = (TextView) itemView.findViewById(R.id.txtValue);
            this.linearItem = (LinearLayout) itemView.findViewById(R.id.linearItem);
            this.imgDeleteOption = (ImageView) itemView.findViewById(R.id.imgDeleteOption);
            txtValue.setText("0");


        }
    }

    public class MotorViewHolder extends ViewHolder{
        public TextView txtMotorName;
        public LinearLayout linearParent;
        public boolean isLeftRight = true;
        private ImageView imgRotateSwitches, leftArrow, rightArrow, imgFavoriteOption, imgAddToSceneOption, imgAddSchedulerOption, imgRenameOption;

        public MotorViewHolder ( View itemView ) {
            super ( itemView );
            txtMotorName = (TextView) itemView.findViewById(R.id.txtMotorName);
            linearParent = (LinearLayout) itemView.findViewById(R.id.linearParent);

            this.imgRotateSwitches = (ImageView) itemView.findViewById(R.id.imgRotateSwitches);
            this.rightArrow = (ImageView) itemView.findViewById(R.id.imgMotorRightArrow);
            this.leftArrow = (ImageView) itemView.findViewById(R.id.imgMotorLeftArrow);

            this.imgFavoriteOption = (ImageView) itemView.findViewById(R.id.imgFavoriteOption);
            this.imgAddToSceneOption = (ImageView) itemView.findViewById(R.id.imgAddToSceneOption);
            this.imgAddSchedulerOption = (ImageView) itemView.findViewById(R.id.imgAddSchedulerOption);
            this.imgRenameOption = (ImageView) itemView.findViewById(R.id.imgRenameOption);
        }
    }


    @Override
    public int getItemViewType(int position) {
        /*if (mDataset.get(position) instanceof SceneItemsDataObject) {
            return 0;
        } else if (mDataset.get(position) instanceof String) {
            return 1;
        }*/

        if(mDataset.get(position).getSceneControlType().equals(AppConstants.SWITCH_TYPE)) {
            return 0;
        } else if (mDataset.get(position).getSceneControlType().equals(AppConstants.DIMMER_TYPE)){
            return 1;
        } else if (mDataset.get(position).getSceneControlType().equals(AppConstants.MOTOR_TYPE)){
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

    public void setType(String type){
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
                ViewGroup viewgroup1 = ( ViewGroup ) mInflater.inflate ( R.layout.create_scene_switch_item, parent, false );
                SwitchViewHolder listHolder = new SwitchViewHolder (viewgroup1);
                return listHolder;
            case 1:
                ViewGroup viewgroup2 = ( ViewGroup ) mInflater.inflate ( R.layout.create_scene_dimmer_item, parent, false );
                DimmerViewHolder gridHolder = new DimmerViewHolder (viewgroup2);
                return gridHolder;

            case 2:
                ViewGroup viewgroup3 = ( ViewGroup ) mInflater.inflate ( R.layout.motor_list_item, parent, false );
                MotorViewHolder thirdHolder = new MotorViewHolder (viewgroup3);
                return thirdHolder;

            default:
                ViewGroup viewgroup4 = ( ViewGroup ) mInflater.inflate ( R.layout.create_scene_dimmer_item, parent, false );
                DimmerViewHolder gridHolder1 = new DimmerViewHolder (viewgroup4);
                return gridHolder1;
        }

    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
       /* final SceneItemsDataObject name = (SceneItemsDataObject) mDataset.get(position);*/
        String componentPrimaryKey = mDataset.get(position).getSceneComponentPrimaryId();
        DatabaseHelper dbHelper = new DatabaseHelper(_ctx);
        String componentName = "";
        try {
            dbHelper.openDataBase();
            componentName = dbHelper.getComponentNameByPrimaryId(componentPrimaryKey);

        } catch(Exception e) {

        }


        switch (viewHolder.getItemViewType () ) {
            case 0:
                final SwitchViewHolder switchHolder = ( SwitchViewHolder ) viewHolder;

                AdvancedSpannableString sp = new AdvancedSpannableString("Component Name: "+ componentName);
                sp.setColor(_ctx.getResources().getColor(R.color.yellow), "Component Name:");
                switchHolder.txtSwitchName.setText(sp);

                sp = new AdvancedSpannableString("Machine Name: "+mDataset.get(position).getMachineName());
                sp.setColor(_ctx.getResources().getColor(R.color.yellow), "Machine Name:");
                switchHolder.txtMachineName.setText(sp);

                if( mDataset.get(position).getDefaultValue().equals(AppConstants.OFF_VALUE))
                    switchHolder.imgSwitch.setChecked(false);
                else
                    switchHolder.imgSwitch.setChecked(true);

                switchHolder.imgSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchHolder.imgSwitch.toggle();
                        if (switchHolder.imgSwitch.isChecked()) {
                            mDataset.get(position).setDefaultValue(AppConstants.OFF_VALUE);
                        } else {
                            mDataset.get(position).setDefaultValue(AppConstants.ON_VALUE);
                        }
                        _switchClick.onCheckedChangeClick(position);
                    }
                });

                /*switchHolder.imgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            switchHolder.linearSwitch.setBackgroundResource(R.drawable.on_switch_border);
                        } else {
                            switchHolder.linearSwitch.setBackgroundResource(R.drawable.off_switch_border);
                        }

                    }
                });*/


                switchHolder.linearSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _onItemClick._onItemClickListener();
                }
            });

                switchHolder.imgDeleteOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _onDeleteClick.onDeleteOptionClick(position);
                    }
                });



                break;
            case 1:
                final DimmerViewHolder dimmerHolder = ( DimmerViewHolder ) viewHolder;
                sp = new AdvancedSpannableString("Component Name: "+componentName);
                sp.setColor(_ctx.getResources().getColor(R.color.yellow), "Component Name:");
                dimmerHolder.txtDimmerName.setText(sp);

                sp = new AdvancedSpannableString("Machine Name: "+mDataset.get(position).getMachineName());
                sp.setColor(_ctx.getResources().getColor(R.color.yellow), "Machine Name:");
                dimmerHolder.txtMachineName.setText(sp);

                dimmerHolder.txtValue.setText(mDataset.get(position).getDefaultValue());
                dimmerHolder.seekBar.setProgress(Integer.parseInt(mDataset.get(position).getDefaultValue()));
                dimmerHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        dimmerHolder.txtValue.setText("" + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        String strProgress = "";
                        if (seekBar.getProgress() > 0 && seekBar.getProgress() < 100) {
                            strProgress = String.format("%02d", (seekBar.getProgress()));
                        } else if (seekBar.getProgress() == 0) {
                            strProgress = "00";
                        } else if(seekBar.getProgress() == 100) {
                            strProgress = "100";
                        }

                        mDataset.get(position).setDefaultValue(strProgress);

                        _switchClick.onCheckedChangeClick(position);
                    }
                });

                dimmerHolder.imgDeleteOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _onDeleteClick.onDeleteOptionClick(position);
                    }
                });

                dimmerHolder.linearItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _onItemClick._onItemClickListener();
                    }
                });

                /*dimmerHolder.imgFavoriteOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _favoriteClick.onFavoriteOptionClick(position);
                    }
                });

                dimmerHolder.imgAddToSceneOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _addToSceneClick.onAddToSceneOptionClick(position);
                    }
                });

                dimmerHolder.imgAddSchedulerOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _addSchedulerClick.onAddSchedulerOptionClick(position);
                    }
                });

                dimmerHolder.imgRenameOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _renameClick.onRenameOptionClick(position);
                    }
                });*/

                break;

            case 2:
                final MotorViewHolder motorViewHolder = ( MotorViewHolder ) viewHolder;
                motorViewHolder.txtMotorName.setText(mDataset.get(position).getName());

                motorViewHolder.imgRotateSwitches.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("TAG", "item clicked " + position);

                        AnimationHelper animationHelper = new AnimationHelper();

                        if (motorViewHolder.isLeftRight) {
                            animationHelper.rotateViewClockwiseLeftToUp(motorViewHolder.leftArrow);
                            animationHelper.rotateViewClockwiseLeftToUp(motorViewHolder.rightArrow);
                        } else {
                            animationHelper.rotateViewAntiClockwiseLeftToUp(motorViewHolder.leftArrow);
                            animationHelper.rotateViewAntiClockwiseLeftToUp(motorViewHolder.rightArrow);
                        }
                        motorViewHolder.isLeftRight = !motorViewHolder.isLeftRight;
                    }
                });

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

    public void setSingleClickListener(onSingleClickListener obj){
        this._singleClick = obj;
    }

    public void setDeleteClickListener(onDeleteClickListener obj){
        this._onDeleteClick = obj;
    }

    public void setCheckedChangeListener(onCheckedChangeListener obj){
        this._switchClick = obj;
    }

}
