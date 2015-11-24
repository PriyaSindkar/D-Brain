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
    private ArrayList<String> addedValues, dimmerOnAddedValues, dimmerUpAddedValues, dimmerDownAddedValues;
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
        dimmerOnAddedValues = new ArrayList<>();
        dimmerUpAddedValues = new ArrayList<>();
        dimmerDownAddedValues = new ArrayList<>();
    }

    public void setupSwitchRows(final String[] arr1, final String[] arr2, HashMap<String, String> selectionMap) {
        String strPanelPositionInMachine = String.format("%02d", Integer.parseInt(panelPositionInMachine));
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
                                setSwitchSelection(name);
                            }
                        }
                    }
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
                                setSwitchSelection(name);
                            }
                        }
                    }
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

    public void setUpSwitchTouchBox(HashMap<String, String> selectionMap) {
        setupSwitchRows(sheet.getUl1(), sheet.getUl2(), selectionMap);
    }


    public void setupDimmerRows(final String[] arr1, final String[] arr2, HashMap<String, String> selectionMap) {
        String strPanelPositionInMachine = String.format("%02d", Integer.parseInt(panelPositionInMachine));
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
                                if(t == 0 || t == 3) { // for on/off
                                    setDimmerSelection(name, 0);
                                }

                                if(t == 1 || t == 4) { // for down
                                    setDimmerSelection(name, 2);
                                }

                                if(t == 2 || t == 5) { // for up
                                    setDimmerSelection(name, 1);
                                }
                            }
                        }
                    }
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
                                if(t == 0 || t == 3) { // for on/off
                                    setDimmerSelection(name, 0);
                                }

                                if(t == 1 || t == 4) { // for down
                                    setDimmerSelection(name, 2);
                                }

                                if(t == 2 || t == 5) { // for up
                                    setDimmerSelection(name, 1);
                                }
                            }
                        }
                    }
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

    public void setUpDimmerTouchBox(HashMap<String, String> selectionMap) {
        setupDimmerRows(sheet.getUl1(), sheet.getUl2(), selectionMap);
    }

    // selected switches
    public ArrayList<String> getSelectedValues(){
        return addedValues;
    }

    //selected Dimmer on
    public ArrayList<String> getDimmerOnAddedValues() {
        return dimmerOnAddedValues;
    }

    //selected Dimmer up
    public ArrayList<String> getDimmerUpAddedValues() {
        return dimmerUpAddedValues;
    }

    //selected Dimmer down
    public ArrayList<String> getDimmerDownAddedValues() {
        return dimmerDownAddedValues;
    }

    //for switches
    public void setSwitchSelection(String panelItemName) {
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


    public void setDimmerSelection(String panelItemName, int colorSelectionType) {

        for (int i = 0; i < row1.getChildCount(); i++) {
            final int pos = i;
            final View view = row1.getChildAt(i);
            TextView txtTouchPanelItemName = (TextView) view.findViewById(R.id.txtTouchPanelItemName);
            TextView txtTouchPanelItemId = (TextView) view.findViewById(R.id.txtTouchPanelItemId);

            final LinearLayout itemLinear = (LinearLayout) view.findViewById(R.id.itemLinear);
            final String name = txtTouchPanelItemName.getText().toString();

            if (name.equals(panelItemName)) {
                if(colorSelectionType == 0) { // for dimmer on selection
                    // add this item to selcted place.
                    if(dimmerOnAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                        itemLinear.setBackgroundResource(R.drawable.touch_panel_bg);
                        dimmerOnAddedValues.remove(txtTouchPanelItemId.getText().toString());
                    }else {

                        // remove this item from other places(if assigned)
                        if(dimmerUpAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                            dimmerUpAddedValues.remove(txtTouchPanelItemId.getText().toString());
                        }

                        if(dimmerDownAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                            dimmerDownAddedValues.remove(txtTouchPanelItemId.getText().toString());
                        }
                        itemLinear.setBackgroundResource(R.drawable.touch_panel_dimmer_onoff);
                        dimmerOnAddedValues.add(txtTouchPanelItemId.getText().toString());
                    }
                } else if (colorSelectionType == 1) { // for dimmer up selection
                    // add this item to selcted place.
                    if(dimmerUpAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                        itemLinear.setBackgroundResource(R.drawable.touch_panel_bg);
                        dimmerUpAddedValues.remove(txtTouchPanelItemId.getText().toString());
                    }else{

                        // remove this item from other places(if assigned)
                        if(dimmerOnAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                            dimmerOnAddedValues.remove(txtTouchPanelItemId.getText().toString());
                        }

                        if(dimmerDownAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                            dimmerDownAddedValues.remove(txtTouchPanelItemId.getText().toString());
                        }

                        itemLinear.setBackgroundResource(R.drawable.touch_panel_dimmer_up);
                        dimmerUpAddedValues.add(txtTouchPanelItemId.getText().toString());
                    }
                } else { // for dimmer down selection
                    // add this item to selcted place.
                    if(dimmerDownAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                        itemLinear.setBackgroundResource(R.drawable.touch_panel_bg);
                        dimmerDownAddedValues.remove(txtTouchPanelItemId.getText().toString());
                    }else{

                        // remove this item from other places(if assigned)
                        if(dimmerOnAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                            dimmerOnAddedValues.remove(txtTouchPanelItemId.getText().toString());
                        }

                        if(dimmerUpAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                            dimmerUpAddedValues.remove(txtTouchPanelItemId.getText().toString());
                        }

                        itemLinear.setBackgroundResource(R.drawable.touch_panel_dimmer_down);
                        dimmerDownAddedValues.add(txtTouchPanelItemId.getText().toString());
                    }
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
                if(colorSelectionType == 0) { // for dimmer on selection


                    // add this item to selcted place.
                    if(dimmerOnAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                        itemLinear.setBackgroundResource(R.drawable.touch_panel_bg);
                        dimmerOnAddedValues.remove(txtTouchPanelItemId.getText().toString());
                    }else {

                        // remove this item from other places(if assigned)
                        if(dimmerUpAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                            dimmerUpAddedValues.remove(txtTouchPanelItemId.getText().toString());
                        }

                        if(dimmerDownAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                            dimmerDownAddedValues.remove(txtTouchPanelItemId.getText().toString());
                        }

                        itemLinear.setBackgroundResource(R.drawable.touch_panel_dimmer_onoff);
                        dimmerOnAddedValues.add(txtTouchPanelItemId.getText().toString());
                    }
                } else if (colorSelectionType == 1) { // for dimmer up selection


                    // add this item to selcted place.
                    if(dimmerUpAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                        itemLinear.setBackgroundResource(R.drawable.touch_panel_bg);
                        dimmerUpAddedValues.remove(txtTouchPanelItemId.getText().toString());
                    }else{

                        // remove this item from other places(if assigned)
                        if(dimmerOnAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                            dimmerOnAddedValues.remove(txtTouchPanelItemId.getText().toString());
                        }

                        if(dimmerDownAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                            dimmerDownAddedValues.remove(txtTouchPanelItemId.getText().toString());
                        }


                        itemLinear.setBackgroundResource(R.drawable.touch_panel_dimmer_up);
                        dimmerUpAddedValues.add(txtTouchPanelItemId.getText().toString());
                    }
                } else { // for dimmer down selection


                    // add this item to selcted place.
                    if(dimmerDownAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                        itemLinear.setBackgroundResource(R.drawable.touch_panel_bg);
                        dimmerDownAddedValues.remove(txtTouchPanelItemId.getText().toString());
                    }else{

                        // remove this item from other places(if assigned)
                        if(dimmerOnAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                            dimmerOnAddedValues.remove(txtTouchPanelItemId.getText().toString());
                        }

                        if(dimmerUpAddedValues.contains(txtTouchPanelItemId.getText().toString())){
                            dimmerUpAddedValues.remove(txtTouchPanelItemId.getText().toString());
                        }


                        itemLinear.setBackgroundResource(R.drawable.touch_panel_dimmer_down);
                        dimmerDownAddedValues.add(txtTouchPanelItemId.getText().toString());
                    }
                }

            }
        }
    }

    public void setOnPanelItemClickListner(OnPaneItemClickListener listner) {
        this.onPanelItemClickListner = listner;
    }
}

