package com.webmyne.android.d_brain.ui.Widgets;

/**
 * Created by priyasindkar on 02-10-2015.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


import android.util.Log;


import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.OnPaneItemClickListener;

/**
 * Created by dhruvil on 14-08-2015.
 */
public class TouchPanelBox extends LinearLayout {

    private TableLayout tableLayout;
    private TableRow row1;
    private TableRow row2;
    private QuadSheet sheet;
    private ArrayList<String> selectedArray;
    private OnPaneItemClickListener onPanelItemClickListner;
    private ArrayList<String> addedValues;
    private String panelPrimaryId, panelPositionInMachine;
    private Context mContext;



    public TouchPanelBox(Context context) {
        super(context);
        init(context);
    }

    public TouchPanelBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public String getPanelPrimaryId() {
        return panelPrimaryId;
    }

    public void setPanelPrimaryId(String panelPrimaryId) {
        this.panelPrimaryId = panelPrimaryId;
    }

    public String getPanelPositionInMachine() {
        return panelPositionInMachine;
    }

    public void setPanelPositionInMachine(String panelPositionInMachine) {
        this.panelPositionInMachine = panelPositionInMachine;
    }

    private void init(Context context) {
        mContext = context;
        View.inflate(context, R.layout.touch_panel_grid, this);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        row1 = (TableRow) tableLayout.getChildAt(0);
        row2 = (TableRow) tableLayout.getChildAt(1);
        sheet = new QuadSheet();
        selectedArray = new ArrayList<>();
        addedValues = new ArrayList<>();
    }

    public void setupRows(final String[] arr1, final String[] arr2, HashMap<String, String> selectionMap) {
        String strPanelPositionInMachine = String.format("%02d", Integer.parseInt(panelPositionInMachine));
        Log.e("TAG_BOX PANELID", strPanelPositionInMachine );
        int noOfComponentsRow1 = arr1.length;

        for (int i = 0; i < row1.getChildCount(); i++) {
            final int pos = i;
            final String strPos = String.format("%02d", (pos + 1));
            final View view = row1.getChildAt(i);

            TextView txtTouchPanelItemName = (TextView) view.findViewById(R.id.txtTouchPanelItemName);
            TextView txtTouchPanelItemId = (TextView) view.findViewById(R.id.txtTouchPanelItemId);
            LinearLayout viewCB = (LinearLayout) view.findViewById(R.id.viewTP);
            final String name = arr1[i];
            txtTouchPanelItemName.setText(name);
            txtTouchPanelItemId.setText(String.valueOf(pos+1));
            txtTouchPanelItemId.setPadding(8, 8, 8, 8);

            // check default assignmnets for initial selection of touch panel switches
            for(int t=0; t<6; t++) {

                if( !selectionMap.isEmpty()){
                    if (!selectionMap.get(String.valueOf(t)).equals("0000")) {
                        String tpId = selectionMap.get(String.valueOf(t)).substring(0, 2);
                        String tpPosition = selectionMap.get(String.valueOf(t)).substring(2, 4);

                        if (strPanelPositionInMachine.equals(tpId)) {
                            if (strPos.equals(tpPosition)) {
                                setSelection(name);
                            }
                        }
                    }
                } else {
                    Log.e("TAG_BOX", "selection empty");
                }
            }


            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPanelItemClickListner.onPanelItemSelection(TouchPanelBox.this,name, pos+1, panelPrimaryId);
                }
            });

        }

        for ( int i = 0; i < row2.getChildCount(); i++, noOfComponentsRow1++) {
            final int pos = noOfComponentsRow1;
            final String strPos = String.format("%02d", (pos + 1));
            final View view = row2.getChildAt(i);

            TextView txtTouchPanelItemName = (TextView) view.findViewById(R.id.txtTouchPanelItemName);
            TextView txtTouchPanelItemId = (TextView) view.findViewById(R.id.txtTouchPanelItemId);
            final String name = arr2[i];
            txtTouchPanelItemName.setText(name);
            txtTouchPanelItemId.setText(String.valueOf(pos+1));
            txtTouchPanelItemId.setPadding(8, 8, 8, 8);

            // check default assignmnets for initial selection of touch panel switches
            for(int t=0; t < 6; t++) {
                if( !selectionMap.isEmpty()) {
                    if (!selectionMap.get(String.valueOf(t)).equals("0000")) {
                        String tpId = selectionMap.get(String.valueOf(t)).substring(0, 2);
                        String tpPosition = selectionMap.get(String.valueOf(t)).substring(2, 4);

                        if (strPanelPositionInMachine.equals(tpId)) {
                            if (strPos.equals(tpPosition)) {
                                setSelection(name);
                            }
                        }
                    }
                } else {
                    Log.e("TAG_BOX", "selection empty");
                }
            }

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPanelItemClickListner.onPanelItemSelection(TouchPanelBox.this,name, pos+1, panelPrimaryId);
                }
            });
        }

    }

    public void setUpTouchBox(HashMap<String, String> selectionMap) {
        setupRows(sheet.getUl1(), sheet.getUl2(), selectionMap);
    }

    public ArrayList<String> getSelectedValues(){
        return addedValues;
    }

    public void setSelection(String panelItemName) {

        for (int i = 0; i < row1.getChildCount(); i++) {
            final int pos = i;
            final View view = row1.getChildAt(i);
            TextView txtTouchPanelItemName = (TextView) view.findViewById(R.id.txtTouchPanelItemName);
            TextView txtTouchPanelItemId = (TextView) view.findViewById(R.id.txtTouchPanelItemId);

            final LinearLayout itemLinear = (LinearLayout) view.findViewById(R.id.itemLinear);
            final String name = txtTouchPanelItemName.getText().toString();

            if (name.equals(panelItemName)) {
                if(addedValues.contains(txtTouchPanelItemId.getText().toString())){
                    itemLinear.setBackgroundResource(R.drawable.touch_panel_bg);
                    addedValues.remove(txtTouchPanelItemId.getText().toString());
                }else{
                    itemLinear.setBackgroundResource(R.drawable.touch_panel_selected);
                    addedValues.add(txtTouchPanelItemId.getText().toString());
                }
            }
        }

        for (int i = 0; i < row2.getChildCount(); i++) {
            final View view = row2.getChildAt(i);
            TextView txtTouchPanelItemName = (TextView) view.findViewById(R.id.txtTouchPanelItemName);
            TextView txtTouchPanelItemId = (TextView) view.findViewById(R.id.txtTouchPanelItemId);
            final LinearLayout itemLinear = (LinearLayout) view.findViewById(R.id.itemLinear);
            final String name = txtTouchPanelItemName.getText().toString();

            if (name.equals(panelItemName)) {
                if(addedValues.contains(txtTouchPanelItemId.getText().toString())){
                    itemLinear.setBackgroundResource(R.drawable.touch_panel_bg);
                    addedValues.remove(txtTouchPanelItemId.getText().toString());
                }else{
                    itemLinear.setBackgroundResource(R.drawable.touch_panel_selected);
                    addedValues.add(txtTouchPanelItemId.getText().toString());
                }
            }
        }
    }

    public void setOnPanelItemClickListner(OnPaneItemClickListener listner) {
        this.onPanelItemClickListner = listner;
    }
}

