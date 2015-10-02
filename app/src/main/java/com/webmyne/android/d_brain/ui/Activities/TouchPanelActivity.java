package com.webmyne.android.d_brain.ui.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.OnPaneItemClickListener;
import com.webmyne.android.d_brain.ui.Widgets.QuadSheet;
import com.webmyne.android.d_brain.ui.Widgets.TouchPanelBox;

/**
 * Created by priyasindkar on 02-10-2015.
 */
public class TouchPanelActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle, txtDisplayPanelName;
    private LinearLayout linearTouchPanelItems, linearAddComponents, linearAddedComponents;
    private int totalNoOfTouchPanels = 5;
    private RecyclerView componentsRecycler;
    private ListView listAddedComponents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_panel_list);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Touch Panel");

        txtDisplayPanelName = (TextView) findViewById(R.id.txtDisplayPanelName);
        linearTouchPanelItems = (LinearLayout) findViewById(R.id.linearTouchPanelItems);

        linearAddComponents = (LinearLayout) findViewById(R.id.linearAddComponents);
        componentsRecycler = (RecyclerView) findViewById(R.id.panelItemsRecycler);

       /* linearAddedComponents = (LinearLayout) findViewById(R.id.linearAddedComponents);
        listAddedComponents = (ListView) findViewById(R.id.listAddedComponents);*/


        initTouchPanelList();


    }

    private void initTouchPanelList() {

        linearTouchPanelItems.removeAllViews();
        for(int i=0; i<totalNoOfTouchPanels; i++) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.touch_panel_box, null);

            final TextView txtPanelHeading = (TextView) view.findViewById(R.id.txtPanelHeading);
            txtPanelHeading.setText("Touch Panel No. " + (i + 1));
            final TouchPanelBox touchPanelBox = (TouchPanelBox) view.findViewById(R.id.touchPanelBox);
            QuadSheet data = new QuadSheet();
            touchPanelBox.setUpTouchBox();

            touchPanelBox.setOnPanelItemClickListner(new OnPaneItemClickListener() {
                @Override
                public void onPanelItemSelection(String oldName) {
                    touchPanelBox.setSelection(oldName);
                    txtDisplayPanelName.setText("Touch Panel: " + txtPanelHeading.getText().toString() + "\nPos: " + oldName);
                }

                @Override
                public void onDisplayPanelComponents(String oldName) {
                    // get touch panel pos components

                }
            });

            linearTouchPanelItems.addView(view);

        }
    }
}
