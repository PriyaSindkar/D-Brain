package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;


public class SceneListAdapter extends RecyclerView.Adapter<SceneListAdapter.ViewHolder> {
    public Context _ctx;
    static int VIEW_TYPE;
    private int totalNoOfScenes;
    public onLongClickListener _longClick;
    public onSingleClickListener _singleClick;
    public onDeleteClickListener _deleteClick;
    public onRenameClickListener _renameClick;

    // 0 - for List Type
    // 1 - for Gri Type

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(VIEW_TYPE == 0)
            return 0;
        else
            return 1;
    }

    public class ListViewHolder extends ViewHolder{
        public  TextView txtSceneName;
        public LinearLayout linearScene;

        public ListViewHolder ( View view ) {
            super ( view );
            this.txtSceneName = (TextView) view.findViewById(R.id.txtSceneName);
            this.linearScene = (LinearLayout) view.findViewById(R.id.linearScene);
        }
    }

    public class GridViewHolder extends ViewHolder{
        public  TextView txtSceneName, txtMachineName;
        public LinearLayout linearScene;
        public ImageView imgRenameOption, imgDeleteOption;
        private SwitchButton imgSwitch;

        public GridViewHolder ( View view ) {
            super ( view );
            this.txtSceneName = (TextView) view.findViewById(R.id.txtSceneName);
            this.txtMachineName = (TextView) view.findViewById(R.id.txtMachineName);
            this.linearScene = (LinearLayout) view.findViewById(R.id.linearScene);
            this.imgDeleteOption = (ImageView) view.findViewById(R.id.imgDeleteOption);
            this.imgRenameOption = (ImageView) view.findViewById(R.id.imgRenameOption);
            this.imgSwitch = (SwitchButton) view.findViewById(R.id.imgSwitch);
        }
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setType(int type){
        VIEW_TYPE = type;
    }

    public SceneListAdapter(Context ctx, int totalNoOfScenes) {
        this._ctx = ctx;
        this.totalNoOfScenes = totalNoOfScenes;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        ViewHolder vh;

        LayoutInflater mInflater = LayoutInflater.from (parent.getContext());

        switch (viewType) {

            case 0:
                ViewGroup viewgroup1 = ( ViewGroup ) mInflater.inflate ( R.layout.dialog_scene_list_item, parent, false );
                ListViewHolder listHolder = new ListViewHolder (viewgroup1);
                return listHolder;
            case 1:
                ViewGroup viewgroup2 = ( ViewGroup ) mInflater.inflate(R.layout.fragment_scene_list_item, parent, false);
                GridViewHolder gridHolder = new GridViewHolder (viewgroup2);
                return gridHolder;
            default:
                ViewGroup viewgroup3 = ( ViewGroup ) mInflater.inflate ( R.layout.fragment_scene_list_item, parent, false );
                GridViewHolder gridHolder1 = new GridViewHolder (viewgroup3);
                return gridHolder1;
        }
    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        switch (viewHolder.getItemViewType () ) {
            case 0:
                final ListViewHolder listHolder = (ListViewHolder) viewHolder;
                listHolder.txtSceneName.setText("Scene " + position);

                listHolder.linearScene.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _singleClick.onSingleClick(position);
                    }
                });

                break;

            case 1:

                final GridViewHolder holder = ( GridViewHolder ) viewHolder;
                holder.txtSceneName.setText("Scene Name: "+ "Scene " + position);

                holder.linearScene.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _singleClick.onSingleClick(position);
                    }
                });

                holder.imgDeleteOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _deleteClick.onDeleteOptionClick(position);
                    }
                });

                holder.imgRenameOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _renameClick.onRenameOptionClick(position, holder.txtSceneName.getText().toString().trim());
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return this.totalNoOfScenes;
    }

    public void setSingleClickListener(onSingleClickListener obj){
        this._singleClick = obj;
    }

    public void setLongClickListener(onLongClickListener obj){
        this._longClick = obj;
    }

    public void setDeleteClickListener(onDeleteClickListener obj) {
        this._deleteClick = obj;
    }

    public void setRenameClickListener(onRenameClickListener obj){
        this._renameClick = obj;
    }

}
