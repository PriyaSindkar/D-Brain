package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.DimmerListActivity;
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
public class DimmerListCursorAdapter extends CursorRecyclerViewAdapter<DimmerListCursorAdapter.ViewHolder>{
    private  Context mCtx;
    static int VIEW_TYPE;
    private ArrayList<XMLValues> dimmerStatus;

    public onLongClickListener _longClick;
    public onSingleClickListener _singleClick;
    public onFavoriteClickListener _favoriteClick;
    public onAddToSceneClickListener _addToSceneClick;
    public onAddSchedulerClickListener _addSchedulerClick;
    public onRenameClickListener _renameClick;
    public onCheckedChangeListener _switchClick;

    public DimmerListCursorAdapter(Context context){
        super(context );
        mCtx = context;
    }


    public DimmerListCursorAdapter(Context context, Cursor cursor, ArrayList<XMLValues> _dimmerStatus){
        super(context,cursor);
        mCtx = context;
        this.dimmerStatus = _dimmerStatus;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    public class ListViewHolder extends ViewHolder {
        public TextView txtDimmerName, txtValue;
        private SeekBar seekBar;
        private ImageView  imgFavoriteOption, imgAddToSceneOption, imgAddSchedulerOption, imgRenameOption;
        private LinearLayout linearParent;

        public ListViewHolder(View view) {
            super(view);
            this.txtDimmerName = (TextView) view.findViewById(R.id.txtDimmerName);
            this.seekBar = (SeekBar) view.findViewById(R.id.seekBar);
            this.txtValue = (TextView) view.findViewById(R.id.txtValue);
            this.linearParent = (LinearLayout) view.findViewById(R.id.linearParent);
            txtValue.setText("0");

            this.imgFavoriteOption = (ImageView) view.findViewById(R.id.imgFavoriteOption);
            this.imgAddToSceneOption = (ImageView) view.findViewById(R.id.imgAddToSceneOption);
            this.imgAddSchedulerOption = (ImageView) view.findViewById(R.id.imgAddSchedulerOption);
            this.imgRenameOption = (ImageView) view.findViewById(R.id.imgRenameOption);

        }
    }

    public class GridViewHolder extends ViewHolder {
        private TextView txtDimmerName, txtValue;
        private LinearLayout linearParent;
        private SeekBar seekBar;

        public GridViewHolder(View view) {
            super(view);
            this.txtDimmerName = (TextView) view.findViewById(R.id.txtDimmerName);
            this.linearParent = (LinearLayout) view.findViewById(R.id.linearParent);
            this.seekBar = (SeekBar) view.findViewById(R.id.seekBar);
            this.txtValue = (TextView) view.findViewById(R.id.txtValue);
            txtValue.setText("0");
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

    public  void setDimmerStatus(ArrayList<XMLValues> _dimmerStatus) {
        this.dimmerStatus = _dimmerStatus;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                ViewGroup viewgroup1 = ( ViewGroup ) mInflater.inflate ( R.layout.dimmer_list_item, parent, false );
                ListViewHolder listHolder = new ListViewHolder (viewgroup1);
                return listHolder;
            case 1:
                ViewGroup viewgroup2 = ( ViewGroup ) mInflater.inflate(R.layout.dimmer_grid_item, parent, false);
                GridViewHolder gridHolder = new GridViewHolder (viewgroup2);
                return gridHolder;
            default:
                ViewGroup viewgroup3 = ( ViewGroup ) mInflater.inflate ( R.layout.dimmer_grid_item, parent, false );
                GridViewHolder gridHolder1 = new GridViewHolder (viewgroup3);
                return gridHolder1;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final Cursor cursor) {
        final int dimmerNameIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME);
        final int position = cursor.getPosition();
        final String strPosition = String.format("%02d", (position + 1));

        String dimmerOnOffStatus = (dimmerStatus.get(position).tagValue).substring(0, 2);
        int seekProgress  = Integer.parseInt((dimmerStatus.get(position).tagValue).substring(2,4))+1;

        switch (viewHolder.getItemViewType () ) {

            case 0:
                final ListViewHolder listHolder = ( ListViewHolder ) viewHolder;
                listHolder.txtDimmerName.setText(cursor.getString(dimmerNameIndex));

                if(dimmerOnOffStatus.equals(AppConstants.OFF_VALUE)) {
                    listHolder.txtValue.setText("0");
                    listHolder.seekBar.setProgress(0);
                } else {
                    listHolder.txtValue.setText(String.valueOf(seekProgress));
                    listHolder.seekBar.setProgress(seekProgress);
                }

                listHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        listHolder.txtValue.setText("" + progress);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        String strProgress = "00";
                        String CHANGE_STATUS_URL = "";

                        if (seekBar.getProgress() > 0) {
                            strProgress = String.format("%02d", (seekBar.getProgress() - 1));
                            CHANGE_STATUS_URL = AppConstants.URL_MACHINE_IP + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.ON_VALUE + strProgress;
                            dimmerStatus.get(position).tagValue = AppConstants.ON_VALUE + strProgress;
                        } else if (seekBar.getProgress() == 0) {
                            CHANGE_STATUS_URL = AppConstants.URL_MACHINE_IP + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.OFF_VALUE + strProgress;
                            dimmerStatus.get(position).tagValue = AppConstants.OFF_VALUE + strProgress;
                        }
                        DimmerListActivity.isDelay = true;
                        new ChangeDimmerStatus().execute(CHANGE_STATUS_URL);
                    }
                });

                listHolder.imgRenameOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _renameClick.onRenameOptionClick(position, listHolder.txtDimmerName.getText().toString().trim());
                    }
                });

                listHolder.imgAddToSceneOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _addToSceneClick.onAddToSceneOptionClick(position);
                    }
                });

                /*listHolder.linearParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _singleClick.onSingleClick(position);
                    }
                });
                listHolder.imgFavoriteOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _favoriteClick.onFavoriteOptionClick(position);
                    }
                });

                listHolder.imgAddToSceneOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _addToSceneClick.onAddToSceneOptionClick(position);
                    }
                });

                listHolder.imgAddSchedulerOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _addSchedulerClick.onAddSchedulerOptionClick(position);
                    }
                });*/



                break;
            case 1:
                final GridViewHolder groupViewHolder = ( GridViewHolder ) viewHolder;
                groupViewHolder.txtDimmerName.setText(cursor.getString(dimmerNameIndex));

                if(dimmerOnOffStatus.equals(AppConstants.OFF_VALUE)) {
                    groupViewHolder.txtValue.setText("0");
                    groupViewHolder.seekBar.setProgress(0);
                } else {
                    groupViewHolder.txtValue.setText(String.valueOf(seekProgress));
                    groupViewHolder.seekBar.setProgress(seekProgress);
                }

                groupViewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        groupViewHolder.txtValue.setText("" + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        String strProgress = "00";
                        String CHANGE_STATUS_URL = "";

                        if (seekBar.getProgress() > 0) {
                            strProgress = String.format("%02d", (seekBar.getProgress() - 1));
                            CHANGE_STATUS_URL = AppConstants.URL_MACHINE_IP + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.ON_VALUE + strProgress;
                            dimmerStatus.get(position).tagValue = AppConstants.ON_VALUE + strProgress;
                        } else if (seekBar.getProgress() == 0) {
                            CHANGE_STATUS_URL = AppConstants.URL_MACHINE_IP + AppConstants.URL_CHANGE_DIMMER_STATUS + strPosition + AppConstants.OFF_VALUE + strProgress;
                            dimmerStatus.get(position).tagValue = AppConstants.OFF_VALUE + strProgress;
                        }
                        DimmerListActivity.isDelay = true;
                        new ChangeDimmerStatus().execute(CHANGE_STATUS_URL);

                    }
                });

                groupViewHolder.linearParent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        _longClick.onLongClick(position);
                        return false;
                    }
                });
                break;
        }


    }

    public void setSingleClickListener(onSingleClickListener obj){
        this._singleClick = obj;
    }
    public void setLongClickListener(onLongClickListener obj){
        this._longClick = obj;
    }

    public void setFavoriteClickListener(onFavoriteClickListener obj){
        this._favoriteClick = obj;
    }

    public void setAddToSceneClickListener(onAddToSceneClickListener obj){
        this._addToSceneClick = obj;
    }

    public void setAddSchedulerClickListener(onAddSchedulerClickListener obj){
        this._addSchedulerClick = obj;
    }

    public void setRenameClickListener(onRenameClickListener obj){
        this._renameClick = obj;
    }

    public void setCheckedChangeListener(onCheckedChangeListener obj){
        this._switchClick = obj;
    }

    public class ChangeDimmerStatus extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL urlValue = new URL(params[0]);
                Log.e("# url change dimmer", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();


            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
                _switchClick.onCheckedChangeClick(0);

            }catch(Exception e){
            }
        }
    }
}
