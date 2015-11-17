package com.webmyne.android.d_brain.ui.Customcomponents;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flyco.animation.SlideEnter.SlideBottomEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.CreateSceneActivity;
import com.webmyne.android.d_brain.ui.Activities.SceneActivity;
import com.webmyne.android.d_brain.ui.Adapters.CreateSceneSwitchListAdapter;
import com.webmyne.android.d_brain.ui.Adapters.SceneListCursorAdapter;
import com.webmyne.android.d_brain.ui.Adapters.SwitchListCursorAdapter;
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSaveSceneComponentsClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

import jp.wasabeef.recyclerview.animators.LandingAnimator;

/**
 * Created by priyasindkar on 16-09-2015.
 */
public class SwitchListDialog extends BaseDialog {
    private ListView listView;
    private CreateSceneSwitchListAdapter adapter;
    private ImageView imgCancel;
    private TextView txtEmptyView, txtSave;
    private LinearLayout emptyView;
    private Cursor switchListCursor;
    private onSingleClickListener _onDismissClick;
    private onSaveSceneComponentsClickListener _onSaveClick;
    private String newComponentId;
    private String newComponentType;
    List<SceneItemsDataObject> alreadyAddedComponents;

    public SwitchListDialog(Context context) {
        super(context);
    }

    public SwitchListDialog(Context context, List<SceneItemsDataObject> _alreadyAddedComponents) {
        super(context);
        this.alreadyAddedComponents = _alreadyAddedComponents;
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
        listView = (ListView) inflate.findViewById(R.id.listView);
        imgCancel = (ImageView) inflate.findViewById(R.id.imgCancel);
        emptyView = (LinearLayout) inflate.findViewById(R.id.emptyView);
        txtEmptyView = (TextView) inflate.findViewById(R.id.txtEmptyView);
        txtSave = (TextView) inflate.findViewById(R.id.txtSave);

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
        if(switchListCursor.getCount() == 0) {
            txtEmptyView.setText(context.getString(R.string.empty_switch_list));
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            adapter = new CreateSceneSwitchListAdapter(context, R.layout.create_scene_switch_list_item,
                    switchListCursor, new String[] {DBConstants.KEY_C_NAME,
                    DBConstants.KEY_C_MNAME }, new int[] { R.id.txtSwitchName, R.id.txtMachineName});
            adapter.setSelectedComponentIds(alreadyAddedComponents);
            listView.setAdapter(adapter);
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
                _onSaveClick.onSaveClick(adapter.getSelectedComponents());
                dismiss();
            }
        });

        return true;
    }

     private void initSwitches() {
         DatabaseHelper dbHelper = new DatabaseHelper(context);
         try {
             dbHelper.openDataBase();
             switchListCursor = dbHelper.getAllSwitchComponents();
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
