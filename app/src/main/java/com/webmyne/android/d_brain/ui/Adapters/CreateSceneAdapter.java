package com.webmyne.android.d_brain.ui.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        final ViewHolderItem viewHolder;

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.create_scene_item, null);

            viewHolder = new ViewHolderItem();
            viewHolder.linearItem = (LinearLayout) convertView.findViewById(R.id.linearItem);
            viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.txtItemName);
            viewHolder.txtMachineIPAddress = (TextView) convertView.findViewById(R.id.txtMachineIPAddress);
            viewHolder.imgDelete = (ImageView) convertView.findViewById(R.id.imgDeleteOption);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        viewHolder.textViewItem.setText(mDataset.get(position).getName());
        viewHolder.txtMachineIPAddress.setText(mDataset.get(position).getMachineIP());

        viewHolder.linearItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _onSingleClick.onSingleClick(position);
            }
        });

        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _onDeleteClick.onDeleteOptionClick(position);
            }
        });

        return convertView;
    }


    static class ViewHolderItem {
        LinearLayout linearItem;
        TextView textViewItem, txtMachineIPAddress;
        ImageView imgDelete;
    }

    public void setOnDeleteClick(onDeleteClickListener obj){
        this._onDeleteClick = obj;
    }
    public void set_onSingleClick(onSingleClickListener obj){
        this._onSingleClick = obj;
    }
}