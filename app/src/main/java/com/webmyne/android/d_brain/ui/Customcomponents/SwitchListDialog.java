package com.webmyne.android.d_brain.ui.Customcomponents;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.animation.SlideEnter.SlideBottomEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.CreateSceneSwitchListAdapter;
import com.webmyne.android.d_brain.ui.Listeners.onSaveSceneComponentsClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.util.List;

/**
 * Created by priyasindkar on 16-09-2015.
 */
public class SwitchListDialog extends BaseDialog {
    private GridView listView;
    private CreateSceneSwitchListAdapter adapter;
    private ImageView imgCancel;
    private TextView txtEmptyView, txtSave, txtComponentListTitle;
    private LinearLayout emptyView;
    private Cursor componentListCursor;
    private onSingleClickListener _onDismissClick;
    private onSaveSceneComponentsClickListener _onSaveClick;
    private String newComponentId;
    private String newComponentType;
    List<String> alreadyAddedComponents;

    public SwitchListDialog(Context context) {
        super(context);
    }

    public SwitchListDialog(Context context, List<String> _alreadyAddedComponents, String _componentType) {
        super(context);
        this.alreadyAddedComponents = _alreadyAddedComponents;
        this.newComponentType = _componentType;
    }

    public SwitchListDialog(Context context, String _componentId, String _componentType) {
        super(context);
        this.newComponentId = _componentId;
        this.newComponentType = _componentType;
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new SlideBottomEnter());

        // dismissAnim(this, new ZoomOutExit());
        View inflate = View.inflate(context, R.layout.dialog_create_scene_switch_list, null);
        listView = (GridView) inflate.findViewById(R.id.listView);
        imgCancel = (ImageView) inflate.findViewById(R.id.imgCancel);
        emptyView = (LinearLayout) inflate.findViewById(R.id.emptyView);
        txtEmptyView = (TextView) inflate.findViewById(R.id.txtEmptyView);
        txtSave = (TextView) inflate.findViewById(R.id.txtSave);
        txtComponentListTitle = (TextView) inflate.findViewById(R.id.txtComponentListTitle);

        listView.setNumColumns(2);
        listView.setHorizontalSpacing(context.getResources().getDimensionPixelSize(R.dimen.STD_MARGIN));
        listView.setVerticalSpacing(context.getResources().getDimensionPixelSize(R.dimen.STD_MARGIN));

        initSwitches();

        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return inflate;
    }

    @Override
    public boolean setUiBeforShow() {
        if(componentListCursor.getCount() == 0) {
            txtEmptyView.setText(context.getString(R.string.empty_switch_list));
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            adapter = new CreateSceneSwitchListAdapter(context, R.layout.create_scene_switch_list_item,
                    componentListCursor, new String[] {DBConstants.KEY_C_NAME,
                    DBConstants.KEY_C_MNAME }, new int[] { R.id.txtSwitchName, R.id.txtMachineName});
            adapter.setSelectedComponentIds(alreadyAddedComponents);
            listView.setAdapter(adapter);
        }

        if(newComponentType.equals(AppConstants.SWITCH_TYPE)) {
            txtComponentListTitle.setText("Switches");
        } else {
            txtComponentListTitle.setText("Dimmers");
        }

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _onDismissClick.onSingleClick(0);
                dismiss();
            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _onSaveClick.onSaveClick(adapter.getSelectedComponents(), adapter.getUnSelectedComponents());
                dismiss();
            }
        });

        return true;
    }

     private void initSwitches() {
         DatabaseHelper dbHelper = new DatabaseHelper(context);
         try {
             dbHelper.openDataBase();

             // fetch components from db as per the type
             if(this.newComponentType.equals(AppConstants.SWITCH_TYPE)) {
                 componentListCursor = dbHelper.getAllSwitchComponents();
             } else if(this.newComponentType.equals(AppConstants.DIMMER_TYPE)){
                 componentListCursor = dbHelper.getAllDimmerComponents();
             }
             dbHelper.close();
         } catch (Exception e) {
             Log.e("EXP SQL", e.toString());
         }
     }

    public void setOnDismissListener(onSingleClickListener obj){
        this._onDismissClick = obj;
    }

    public void setOnSaveListener(onSaveSceneComponentsClickListener obj){
        this._onSaveClick = obj;
    }
}
