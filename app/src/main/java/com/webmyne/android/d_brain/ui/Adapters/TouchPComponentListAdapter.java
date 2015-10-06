package com.webmyne.android.d_brain.ui.Adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.TouchPanelActivity;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;

import java.util.ArrayList;

/**
 * Created by priyasindkar on 06-10-2015.
 */

public class TouchPComponentListAdapter extends SimpleCursorAdapter {

    private Cursor cursor;
    private Context context;
    private Activity activity;
    public static int[] chkState;
    private int layout;
    private ArrayList<String> selectedComponents;

    public TouchPComponentListAdapter(Context context, int layout, Cursor c,
                               String[] from, int[] to) {
        super(context, layout, c, from, to);

        this.cursor = c;
        this.context = context;
        this.activity = (Activity) context;
        this.layout = layout;

        chkState = new int[c.getCount()];
        for(int i=0; i<c.getCount(); i++){
            chkState[i] = 0;
        }

        selectedComponents = new ArrayList<>();
    }

    public void init() {
        chkState = new int[cursor.getCount()];
        for(int i=0; i<cursor.getCount(); i++){
            chkState[i] = 0;
        }

        selectedComponents = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, layout, null);
            viewHolder = new ViewHolder();
            viewHolder.txtSwitchName = (TextView) convertView.findViewById(R.id.txtSwitchName);
            viewHolder.txtMachineName = (TextView) convertView.findViewById(R.id.txtMachineName);
            viewHolder.checkedBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final int pos = position;
        cursor.moveToPosition(pos);
        final String componentId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));

        viewHolder.txtSwitchName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME)));
        viewHolder.txtMachineName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MNAME)));


        if (chkState[position] == 0) {
            viewHolder.checkedBox.setChecked(false);
        } else {
            viewHolder.checkedBox.setChecked(true);
        }

        viewHolder.checkedBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.checkedBox.isChecked()) {
                    chkState[pos] = 1;
                    selectedComponents.add(componentId);
                } else {
                    chkState[pos] = 0;
                    selectedComponents.remove(componentId);
                }
            }
        });

        return (convertView);
    }

    public ArrayList<String> getSelectedComponents() {
        return selectedComponents;
    }

    public void setSelectedComponentIds(ArrayList<String> selectedComponents) {
        this.selectedComponents = selectedComponents;
    }

    class ViewHolder {
        TextView txtSwitchName, txtMachineName;
        CheckBox checkedBox;
    }
}
