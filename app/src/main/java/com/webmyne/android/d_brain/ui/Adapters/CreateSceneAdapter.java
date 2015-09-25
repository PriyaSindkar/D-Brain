package com.webmyne.android.d_brain.ui.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;

import java.util.ArrayList;


public class CreateSceneAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<SceneItemsDataObject> mDataset;
    public onDeleteClickListener _onDeleteClick;
    public onSingleClickListener  _onSingleClick;

    public CreateSceneAdapter(Activity activity, ArrayList<SceneItemsDataObject> _mDataset ) {
        this.activity = activity;
        this.mDataset = _mDataset;
    }

    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public Object getItem(int location) {
        return mDataset.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(int position, SceneItemsDataObject item) {
        mDataset.add(position, item);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderItem switchViewHolder = null;
        ViewHolderItem dimmerViewHolder = null;

        //if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            //set item view according to component type
            if(mDataset.get(position).getSceneControlType().equals(AppConstants.SWITCH_TYPE)) {
                Log.e("if", "if");
                convertView = inflater.inflate(R.layout.create_scene_switch_item, null);

                switchViewHolder = new ViewHolderItem();
                switchViewHolder.linearItem = (LinearLayout) convertView.findViewById(R.id.linearItem);
                switchViewHolder.textViewItem = (TextView) convertView.findViewById(R.id.txtSwitchName);
                switchViewHolder.txtMachineName = (TextView) convertView.findViewById(R.id.txtMachineName);
                switchViewHolder.imgDelete = (ImageView) convertView.findViewById(R.id.imgDeleteOption);
                switchViewHolder.imgSwitch = (SwitchButton) convertView.findViewById(R.id.imgSwitch);

                convertView.setTag(switchViewHolder);
            }

            else if (mDataset.get(position).getSceneControlType().equals(AppConstants.DIMMER_TYPE)) {
                Log.e("else", "else");
                convertView = inflater.inflate(R.layout.create_scene_dimmer_item, null);

                dimmerViewHolder = new ViewHolderItem();
                dimmerViewHolder.linearItem = (LinearLayout) convertView.findViewById(R.id.linearItem);
                dimmerViewHolder.textViewItem = (TextView) convertView.findViewById(R.id.txtDimmerName);
                dimmerViewHolder.txtMachineName = (TextView) convertView.findViewById(R.id.txtMachineName);
                dimmerViewHolder.imgDelete = (ImageView) convertView.findViewById(R.id.imgDeleteOption);
                dimmerViewHolder.seekBar = (SeekBar) convertView.findViewById(R.id.seekBar);

                convertView.setTag(dimmerViewHolder);
            }
        } else {
            if(mDataset.get(position).getSceneControlType().equals(AppConstants.SWITCH_TYPE)) {
                switchViewHolder = (ViewHolderItem) convertView.getTag();
            } else if (mDataset.get(position).getSceneControlType().equals(AppConstants.DIMMER_TYPE)) {
                dimmerViewHolder = (ViewHolderItem) convertView.getTag();
            }

        }



        //initialize switch status
        if(mDataset.get(position).getSceneControlType().equals(AppConstants.SWITCH_TYPE)) {

            switchViewHolder.textViewItem.setText(mDataset.get(position).getName());
            switchViewHolder.txtMachineName.setText(mDataset.get(position).getMachineIP());

            final ViewHolderItem finalViewHolder = switchViewHolder;

            //set default value to switch
            if (mDataset.get(position).getDefaultValue().equals(AppConstants.OFF_VALUE))
                switchViewHolder.imgSwitch.setChecked(false);
            else
                switchViewHolder.imgSwitch.setChecked(true);

            switchViewHolder.imgSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalViewHolder.imgSwitch.toggle();
                    if (finalViewHolder.imgSwitch.isChecked()) {
                        mDataset.get(position).setDefaultValue(AppConstants.OFF_VALUE);
                    } else {
                        mDataset.get(position).setDefaultValue(AppConstants.ON_VALUE);
                    }
                }
            });

            switchViewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _onDeleteClick.onDeleteOptionClick(position);
                }
            });


            switchViewHolder.linearItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _onSingleClick.onSingleClick(position);
                }
            });
        }

        //initialize dimmer status
        if(mDataset.get(position).getSceneControlType().equals(AppConstants.DIMMER_TYPE)) {
            dimmerViewHolder.textViewItem.setText(mDataset.get(position).getName());
            dimmerViewHolder.txtMachineName.setText(mDataset.get(position).getMachineIP());

            dimmerViewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _onDeleteClick.onDeleteOptionClick(position);
                }
            });


            dimmerViewHolder.linearItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _onSingleClick.onSingleClick(position);
                }
            });
        }



        return convertView;
    }


    static class ViewHolderItem {
        LinearLayout linearItem;
        TextView textViewItem, txtMachineName, txtDimmerValue;
        ImageView imgDelete;
        SwitchButton imgSwitch;
        SeekBar seekBar;

    }

    public void setOnDeleteClick(onDeleteClickListener obj){
        this._onDeleteClick = obj;
    }
    public void set_onSingleClick(onSingleClickListener obj){
        this._onSingleClick = obj;
    }
}