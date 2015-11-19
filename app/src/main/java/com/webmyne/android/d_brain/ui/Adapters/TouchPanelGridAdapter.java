package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onTouchPanelSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;

import java.util.ArrayList;

/**
 * Created by krishnakumar on 16-11-2015.
 */
public class TouchPanelGridAdapter extends BaseAdapter {
    private Context _ctx;
    private ArrayList<ComponentModel> values;
    private static int[] selectedValues;
    private int selectedValue;
    private onTouchPanelSingleClickListener _onSingleClick;


    public TouchPanelGridAdapter(Context ctx,  ArrayList<ComponentModel> data) {
        this._ctx = ctx;
        this.values = data;
        selectedValue = -1;
        selectedValues = new int[data.size()];

        for(int i=0; i<data.size(); i++){
            selectedValues[i] = 0;
        }
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView txtSwitchName;
        LinearLayout itemLinear;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(_ctx, R.layout.touch_panel_grid_item, null);
            viewHolder = new ViewHolder();

            viewHolder.txtSwitchName = (TextView) convertView.findViewById(R.id.txtTouchPanelItemName);
            viewHolder.itemLinear = (LinearLayout) convertView.findViewById(R.id.itemLinear);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtSwitchName.setText("" + values.get(position).getName());

        if(selectedValue == position) {
            viewHolder.itemLinear.setBackgroundResource(R.drawable.touch_panel_selected);
        } else {
            viewHolder.itemLinear.setBackgroundResource(R.drawable.touch_panel_bg);
        }

       convertView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               _onSingleClick.onTouchPanelSingleClick(values.get(position).getComponentId(), String.valueOf(values.get(position).getId()));
               setSelection(position);
               notifyDataSetChanged();
           }
       });

        return convertView;
    }

    public void setSelection(int pos) {
        /*for(int i=0; i<values.size(); i++){
            selectedValues[i] = 0;
        }
        selectedValues[pos] = 1;*/
        selectedValue = pos;
    }

    public void setOnTouchPanelSingleClickListener(onTouchPanelSingleClickListener _onSingleClick) {
        this._onSingleClick = _onSingleClick;
    }


}
