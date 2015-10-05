package com.webmyne.android.d_brain.ui.Activities;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.konifar.fab_transformation.FabTransformation;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.OnPaneItemClickListener;
import com.webmyne.android.d_brain.ui.Widgets.QuadSheet;
import com.webmyne.android.d_brain.ui.Widgets.TouchPanelBox;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.sql.SQLException;

/**
 * Created by priyasindkar on 02-10-2015.
 */
public class TouchPanelActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private TextView toolbarTitle, txtDisplayPanelName;
    private LinearLayout linearTouchPanelItems, panelSetLayout,linearAddComponents, linearAddedComponents, linearAddCancel;
    private FrameLayout rightParent;
    private int totalNoOfTouchPanels = 5;
    private RecyclerView componentsRecycler;
    private ListView listAddedComponents;
    private Cursor touchPanelListCursor;
    private View divider;
    private FloatingActionButton fab;
    private Button btnAddSwitch, btnAddDimmer, btnAddMotor, btnAdd, btnCancel;
    private String selected = "";

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

        rightParent = (FrameLayout) findViewById(R.id.rightParent);
        panelSetLayout = (LinearLayout) findViewById(R.id.panelSetLayout);
        divider = findViewById(R.id.divider);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        btnAddSwitch = (Button) findViewById(R.id.btnAddSwitch);
        btnAddDimmer = (Button) findViewById(R.id.btnAddDimmer);
        btnAddMotor = (Button) findViewById(R.id.btnAddMotor);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        linearAddCancel = (LinearLayout) findViewById(R.id.linearAddCancel);

        btnAddSwitch.setOnClickListener(this);
        btnAddDimmer.setOnClickListener(this);
        btnAddMotor.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        linearAddedComponents = (LinearLayout) findViewById(R.id.linearAddedComponents);
        listAddedComponents = (ListView) findViewById(R.id.listAddedComponents);

        initArrayOfTouchPanels();
        initTouchPanelList();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG_FAB", "fab click");
                FabTransformation.with(fab)
                        .transformTo(linearAddComponents);
            }
        });


    }

    private void initTouchPanelList() {

        linearTouchPanelItems.removeAllViews();

        if (touchPanelListCursor != null) {
            touchPanelListCursor.moveToFirst();
                if (touchPanelListCursor.getCount() > 0) {
                    do {
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.touch_panel_box, null);

                        final String touchPanelName = touchPanelListCursor.getString(touchPanelListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
                        final String touchPanelId = touchPanelListCursor.getString(touchPanelListCursor.getColumnIndexOrThrow(DBConstants.KEY_S_ID));

                        final TextView txtPanelHeading = (TextView) view.findViewById(R.id.txtPanelHeading);
                        txtPanelHeading.setText(touchPanelName);

                        final TouchPanelBox touchPanelBox = (TouchPanelBox) view.findViewById(R.id.touchPanelBox);
                        touchPanelBox.setPanelId(touchPanelId);
                        touchPanelBox.setUpTouchBox();

                        touchPanelBox.setOnPanelItemClickListner(new OnPaneItemClickListener() {
                            @Override
                            public void onPanelItemSelection(String oldName) {
                                touchPanelBox.setSelection(oldName);
                                txtDisplayPanelName.setText(touchPanelName + "\nPos: " + oldName);

                                panelSetLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                rightParent.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                rightParent.setVisibility(View.VISIBLE);
                                divider.setVisibility(View.VISIBLE);
                            }

                        });

                        linearTouchPanelItems.addView(view);

                    } while (touchPanelListCursor.moveToNext());
                }
        }

        /*for(int i=0; i<touchPanelListCursor.getCount(); i++) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.touch_panel_box, null);

            final TextView txtPanelHeading = (TextView) view.findViewById(R.id.txtPanelHeading);
            txtPanelHeading.setText("Touch Panel No. " + (i + 1));
            final TouchPanelBox touchPanelBox = (TouchPanelBox) view.findViewById(R.id.touchPanelBox);
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

        }*/
    }

    private void initArrayOfTouchPanels() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //get all touch panel boxes configured
        try {
            dbHelper.openDataBase();
            touchPanelListCursor =  dbHelper.getAllTouchPanelBoxes();
            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddSwitch:
                linearAddComponents.setVisibility(View.INVISIBLE);
                linearAddCancel.setVisibility(View.VISIBLE);
                linearAddedComponents.setVisibility(View.VISIBLE);
                break;
            case R.id.btnAddDimmer:
                break;
            case R.id.btnAddMotor:
                break;
            case R.id.btnAdd:
                /*panelSetLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
                rightParent.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);*/
               // linearAddComponents.setVisibility(View.INVISIBLE);
                linearAddedComponents.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.GONE);
                FabTransformation.with(fab)
                        .transformFrom(linearAddCancel);
                break;
            case R.id.btnCancel:
                linearAddComponents.setVisibility(View.INVISIBLE);
                linearAddedComponents.setVisibility(View.VISIBLE);
                FabTransformation.with(fab)
                        .transformFrom(linearAddCancel);
                break;
        }
    }
}
