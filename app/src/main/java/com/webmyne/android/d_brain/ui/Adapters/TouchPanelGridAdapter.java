package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;

import java.util.ArrayList;

/**
 * Created by krishnakumar on 16-11-2015.
 */
public class TouchPanelGridAdapter extends BaseAdapter {
    private Context _ctx;
    private ArrayList<String> values;


    public TouchPanelGridAdapter(Context ctx,  ArrayList<String> data) {
        this._ctx = ctx;
        this.values = data;
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
        return 0;
    }

    class ViewHolder {
        TextView txtSwitchName;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(_ctx, R.layout.touch_panel_grid_item, null);
            viewHolder = new ViewHolder();

            viewHolder.txtSwitchName = (TextView) convertView.findViewById(R.id.txtTouchPanelItemName);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtSwitchName.setText(""+values.get(position));

        return convertView;
    }
}
