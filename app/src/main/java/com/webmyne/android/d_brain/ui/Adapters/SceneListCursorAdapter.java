package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.SwitchesListActivity;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.xmlHelpers.XMLValues;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by priyasindkar on 14-09-2015.
 */
public class SceneListCursorAdapter extends CursorRecyclerViewAdapter<SceneListCursorAdapter.ViewHolder>{
    private  Context mCtx;
    static int VIEW_TYPE;

    public onLongClickListener _longClick;
    public onSingleClickListener _singleClick;

    public SceneListCursorAdapter(Context context){
        super(context );
        mCtx = context;
    }


    public SceneListCursorAdapter(Context context, Cursor cursor){
        super(context,cursor);
        mCtx = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    public class SimpleDialogViewHolder extends ViewHolder {
        public  TextView txtSceneName;
        public LinearLayout linearScene;

        public SimpleDialogViewHolder(View view) {
            super ( view );
            this.txtSceneName = (TextView) view.findViewById(R.id.txtSceneName);
            this.linearScene = (LinearLayout) view.findViewById(R.id.linearScene);
        }
    }

    public class ListViewHolder extends ViewHolder{
        public  TextView txtSceneName, txtMachineName;
        public LinearLayout linearScene;

        public ListViewHolder(View view) {
            super ( view );
            this.txtSceneName = (TextView) view.findViewById(R.id.txtSceneName);
            this.txtMachineName = (TextView) view.findViewById(R.id.txtMachineName);
            this.linearScene = (LinearLayout) view.findViewById(R.id.linearScene);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(VIEW_TYPE == 0)
            return 0;
        else
            return 1;
    }

    public void setType(int type){
        VIEW_TYPE = type;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                ViewGroup viewgroup1 = ( ViewGroup ) mInflater.inflate ( R.layout.dialog_scene_list_item, parent, false );
                SimpleDialogViewHolder listHolder = new SimpleDialogViewHolder(viewgroup1);
                return listHolder;
            case 1:
                ViewGroup viewgroup2 = ( ViewGroup ) mInflater.inflate(R.layout.fragment_scene_list_item, parent, false);
                ListViewHolder gridHolder = new ListViewHolder(viewgroup2);
                return gridHolder;
            default:
                ViewGroup viewgroup3 = ( ViewGroup ) mInflater.inflate ( R.layout.dialog_scene_list_item, parent, false );
                ListViewHolder gridHolder1 = new ListViewHolder(viewgroup3);
                return gridHolder1;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final Cursor cursor) {
        int sceneNameIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME);
        final int position = cursor.getPosition();


        switch (viewHolder.getItemViewType () ) {
            case 0:
                final SimpleDialogViewHolder listHolder = (SimpleDialogViewHolder) viewHolder;
                listHolder.txtSceneName.setText(cursor.getString(sceneNameIndex));

                listHolder.linearScene.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _singleClick.onSingleClick(position);
                    }
                });

                break;
            case 1:
                final ListViewHolder groupViewHolder = (ListViewHolder) viewHolder;
                groupViewHolder.txtSceneName.setText(cursor.getString(sceneNameIndex));
                groupViewHolder.txtMachineName.setText("Machine-1");

                groupViewHolder.linearScene.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _singleClick.onSingleClick(position);
                    }
                });
                break;
        }


    }

    public void setSingleClickListener(onSingleClickListener obj){
        this._singleClick = obj;
    }
}