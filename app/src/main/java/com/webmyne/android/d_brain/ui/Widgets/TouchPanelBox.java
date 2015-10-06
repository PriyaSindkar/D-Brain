package com.webmyne.android.d_brain.ui.Widgets;

/**
 * Created by priyasindkar on 02-10-2015.
 */

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.OnPaneItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dhruvil on 14-08-2015.
 */
public class TouchPanelBox extends LinearLayout {

    private TableLayout tableLayout;
    private TableRow row1;
    private TableRow row2;
    private QuadSheet sheet;
    public HashMap<String, String> orderMap;
    private ArrayList<String> selectedArray;
    private OnPaneItemClickListener onPanelItemClickListner;
    private LinearLayout linearParentQuad;
    private ArrayList<String> addedValues;
    private String panelId;
    private Context mContext;


    public TouchPanelBox(Context context) {
        super(context);
        init(context);
    }

    public TouchPanelBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public String getPanelId() {
        return panelId;
    }

    public void setPanelId(String panelId) {
        this.panelId = panelId;
    }



    private void init(Context context) {
        mContext = context;

        View.inflate(context, R.layout.touch_panel_grid, this);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        row1 = (TableRow) tableLayout.getChildAt(0);
        row2 = (TableRow) tableLayout.getChildAt(1);
        linearParentQuad = (LinearLayout) findViewById(R.id.linearParentQuad);
        sheet = new QuadSheet();
        orderMap = new HashMap<>();
        selectedArray = new ArrayList<>();
        addedValues = new ArrayList<>();

    }

    public void setupRows(final String[] arr1, final String[] arr2) {
        int noOfComponentsRow1 = arr1.length;

        for (int i = 0; i < row1.getChildCount(); i++) {
            final int pos = i;
            final View view = row1.getChildAt(i);

            TextView txtTouchPanelItemName = (TextView) view.findViewById(R.id.txtTouchPanelItemName);
            TextView txtTouchPanelItemId = (TextView) view.findViewById(R.id.txtTouchPanelItemId);
            LinearLayout viewCB = (LinearLayout) view.findViewById(R.id.viewTP);
            final String name = arr1[i];
            txtTouchPanelItemName.setText(name);
            txtTouchPanelItemId.setText(String.valueOf(pos+1));
            txtTouchPanelItemId.setPadding(8, 8, 8, 8);

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPanelItemClickListner.onPanelItemSelection(name, pos+1, panelId);
                }
            });

        }

        for ( int i = 0; i < row2.getChildCount(); i++, noOfComponentsRow1++) {
            final int pos = noOfComponentsRow1;
            final View view = row2.getChildAt(i);

            TextView txtTouchPanelItemName = (TextView) view.findViewById(R.id.txtTouchPanelItemName);
            TextView txtTouchPanelItemId = (TextView) view.findViewById(R.id.txtTouchPanelItemId);
            final String name = arr2[i];
            txtTouchPanelItemName.setText(name);
            txtTouchPanelItemId.setText(String.valueOf(pos+1));
            txtTouchPanelItemId.setPadding(8, 8, 8, 8);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPanelItemClickListner.onPanelItemSelection(name, pos+1, panelId);
                }
            });
        }

    }
    public void setUpTouchBox() {
        setupRows(sheet.getUl1(), sheet.getUl2());
    }

   /* public void insertAddedValue(String name) {
        addedValues.add(name);
    }*/


    public void setSelection(String panelItemName) {

        for (int i = 0; i < row1.getChildCount(); i++) {
            final int pos = i;
            final View view = row1.getChildAt(i);
            TextView txtTouchPanelItemName = (TextView) view.findViewById(R.id.txtTouchPanelItemName);
            final LinearLayout itemLinear = (LinearLayout) view.findViewById(R.id.itemLinear);
            final String name = txtTouchPanelItemName.getText().toString();

            if (name.equals(panelItemName)) {
                itemLinear.setBackgroundResource(R.drawable.touch_panel_selected);
            } else {
                itemLinear.setBackgroundResource(R.drawable.touch_panel_bg);
            }
        }

        for (int i = 0; i < row2.getChildCount(); i++) {
            final View view = row2.getChildAt(i);
            TextView txtTouchPanelItemName = (TextView) view.findViewById(R.id.txtTouchPanelItemName);
            final LinearLayout itemLinear = (LinearLayout) view.findViewById(R.id.itemLinear);
            final String name = txtTouchPanelItemName.getText().toString();

            if (name.equals(panelItemName)) {
                itemLinear.setBackgroundResource(R.drawable.touch_panel_selected);
            } else {
                itemLinear.setBackgroundResource(R.drawable.touch_panel_bg);
            }
        }
    }

    public void setOnPanelItemClickListner(OnPaneItemClickListener listner) {
        this.onPanelItemClickListner = listner;
    }

  /*  public void clearSelection() {

        for (int i = 0; i < row1.getChildCount(); i++) {

            final View view = row1.getChildAt(i);
            TextView txtDetails = (TextView) view.findViewById(R.id.txtDetails);
            final LinearLayout itemLinear = (LinearLayout) view.findViewById(R.id.itemLinear);
            final String name = txtDetails.getText().toString();

            if (!addedValues.contains(name)) {
                itemLinear.setBackgroundResource(R.drawable.crown_bg);
            } else {
                itemLinear.setBackgroundResource(R.drawable.crown_bg_selected);
            }

        }

        for (int i = 0; i < row2.getChildCount(); i++) {
            final View view = row2.getChildAt(i);
            TextView txtDetails = (TextView) view.findViewById(R.id.txtDetails);
            final LinearLayout itemLinear = (LinearLayout) view.findViewById(R.id.itemLinear);
            final String name = txtDetails.getText().toString();

            if (!addedValues.contains(name)) {
                itemLinear.setBackgroundResource(R.drawable.crown_bg);
            } else {
                itemLinear.setBackgroundResource(R.drawable.crown_bg_selected);
            }

        }

    }

    public void clearSelected(String value) {
        selectedArray.remove(value);
    }

    public void setDefault() {
        for (int i = 0; i < row1.getChildCount(); i++) {
            final View view = row1.getChildAt(i);
            final LinearLayout itemLinear = (LinearLayout) view.findViewById(R.id.itemLinear);
            TextView txtDetails = (TextView) view.findViewById(R.id.txtDetails);
            TextView txtCrown = (TextView) view.findViewById(R.id.txtCrown);
            itemLinear.setBackgroundResource(R.drawable.crown_bg);
            clearSelected(txtDetails.getText().toString());
            removeSelected(txtDetails.getText().toString());
            String string = String.format("%c%c", txtDetails.getText().toString().charAt(0), txtDetails.getText().toString().charAt(txtDetails.getText().toString().length() - 1));
            txtCrown.setText(string);
            txtCrown.setPadding(8, 8, 8, 8);
        }

        for (int i = 0; i < row2.getChildCount(); i++) {
            final View view = row2.getChildAt(i);
            final LinearLayout itemLinear = (LinearLayout) view.findViewById(R.id.itemLinear);
            TextView txtDetails = (TextView) view.findViewById(R.id.txtDetails);
            TextView txtCrown = (TextView) view.findViewById(R.id.txtCrown);
            itemLinear.setBackgroundResource(R.drawable.crown_bg);
            clearSelected(txtDetails.getText().toString());
            removeSelected(txtDetails.getText().toString());
            String string = String.format("%c%c", txtDetails.getText().toString().charAt(0), txtDetails.getText().toString().charAt(txtDetails.getText().toString().length() - 1));
            txtCrown.setText(string);
            txtCrown.setPadding(8, 8, 8, 8);
        }
    }

    public void setQuanity(final String crown, String value) {

        addedValues.add(crown);

        for (int i = 0; i < row1.getChildCount(); i++) {
            final View view = row1.getChildAt(i);
            TextView txtDetails = (TextView) view.findViewById(R.id.txtDetails);
            TextView txtCrown = (TextView) view.findViewById(R.id.txtCrown);
            final LinearLayout itemLinear = (LinearLayout) view.findViewById(R.id.itemLinear);
            final String name = txtDetails.getText().toString();
            if (name.equals(crown)) {
                itemLinear.setBackgroundResource(R.drawable.crown_bg_selected);
                txtCrown.setText(value);
                break;
            }
        }

        for (int i = 0; i < row2.getChildCount(); i++) {
            final View view = row2.getChildAt(i);
            TextView txtDetails = (TextView) view.findViewById(R.id.txtDetails);
            TextView txtCrown = (TextView) view.findViewById(R.id.txtCrown);
            final LinearLayout itemLinear = (LinearLayout) view.findViewById(R.id.itemLinear);
            final String name = txtDetails.getText().toString();
            if (name.equals(crown)) {
                itemLinear.setBackgroundResource(R.drawable.crown_bg_selected);
                txtCrown.setText(value);
                break;
            }
        }

    }

    public void removeSelected(String value) {

        if (addedValues.contains(value)) {
            addedValues.remove(value);
            for (int i = 0; i < row1.getChildCount(); i++) {

                final View view = row1.getChildAt(i);
                TextView txtDetails = (TextView) view.findViewById(R.id.txtDetails);
                TextView txtCrown = (TextView) view.findViewById(R.id.txtCrown);
                final LinearLayout itemLinear = (LinearLayout) view.findViewById(R.id.itemLinear);

                if (txtDetails.getText().toString().equalsIgnoreCase(value)) {
                    itemLinear.setBackgroundResource(R.drawable.crown_bg);
                    String string = String.format("%c%c", txtDetails.getText().toString().charAt(0), txtDetails.getText().toString().charAt(txtDetails.getText().toString().length() - 1));
                    txtCrown.setText(string);
                    txtCrown.setPadding(8, 8, 8, 8);
                }
            }

            for (int i = 0; i < row2.getChildCount(); i++) {
                final View view = row2.getChildAt(i);
                TextView txtDetails = (TextView) view.findViewById(R.id.txtDetails);
                TextView txtCrown = (TextView) view.findViewById(R.id.txtCrown);
                final LinearLayout itemLinear = (LinearLayout) view.findViewById(R.id.itemLinear);

                if (txtDetails.getText().toString().equalsIgnoreCase(value)) {
                    itemLinear.setBackgroundResource(R.drawable.crown_bg);
                    String string = String.format("%c%c", txtDetails.getText().toString().charAt(0), txtDetails.getText().toString().charAt(txtDetails.getText().toString().length() - 1));
                    txtCrown.setText(string);
                    txtCrown.setPadding(8, 8, 8, 8);
                }
            }
        }

    }

    public void setUpperLeft() {
        setupRows(R.drawable.ul, getResources().getColor(R.color.quad_blue), sheet.getUl1(), sheet.getUl2());
    }

    public void setUpperRight() {
        setupRows(R.drawable.ur, getResources().getColor(R.color.quad_orange), sheet.getUr1(), sheet.getUr2());
    }

    public void setLowerLeft() {
        setupRows(R.drawable.ll, getResources().getColor(R.color.quad_green), sheet.getLl1(), sheet.getLl2());
    }

    public void setLowerRight() {
        setupRows(R.drawable.lr, getResources().getColor(R.color.quad_violate), sheet.getLr1(), sheet.getLr2());
    }

    public interface OnCrownClickListner {

        void displayNumberpad(String value);

        void setSelection(String value);

    }*/

}

