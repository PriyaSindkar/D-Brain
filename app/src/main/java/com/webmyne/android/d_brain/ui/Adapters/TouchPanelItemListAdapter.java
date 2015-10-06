package com.webmyne.android.d_brain.ui.Adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.OnPaneItemDeleteListener;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by priyasindkar on 06-10-2015.
 */

public class TouchPanelItemListAdapter extends SimpleCursorAdapter {

    private Cursor cursor;
    private Context context;
    private Activity activity;
    private int layout;
    private OnPaneItemDeleteListener onPaneItemDeleteListener;

    public TouchPanelItemListAdapter(Context context, int layout, Cursor c,
                                     String[] from, int[] to) {
        super(context, layout, c, from, to);

        this.cursor = c;
        this.context = context;
        this.activity = (Activity) context;
        this.layout = layout;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, layout, null);
            viewHolder = new ViewHolder();
            viewHolder.txtSwitchName = (TextView) convertView.findViewById(R.id.txtSwitchName);
            viewHolder.cancel = (ImageView) convertView.findViewById(R.id.cancel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final int pos = position;
        cursor.moveToPosition(pos);
        final String panelId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_TP_ITEM_PID));
        final String panelposition = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_TP_ITEM_POS));
        final String componentId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_TP_ITEM_COMPONENT_ID));

        viewHolder.txtSwitchName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_TP_ITEM_COMPONENT_NAME)));

        viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onPaneItemDeleteListener.onPanelItemDeletion(panelId, Integer.parseInt(panelposition), componentId);
                DatabaseHelper dbHelper = new DatabaseHelper(context);

                //get all touch panel item components
                try {
                    dbHelper.openDataBase();
                    dbHelper.deletePanelItemComponents(panelId, Integer.parseInt(panelposition), componentId);
                    dbHelper.close();

                    cursor = dbHelper.getPanelItemComponents(panelId, Integer.parseInt(panelposition));

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                swapCursor(cursor);
                notifyDataSetChanged();
            }
        });

        return (convertView);
    }



    class ViewHolder {
        TextView txtSwitchName;
        ImageView cancel;
    }

    public void setOnPaneItemDeleteListener(OnPaneItemDeleteListener listner) {
        this.onPaneItemDeleteListener = listner;
    }
}
