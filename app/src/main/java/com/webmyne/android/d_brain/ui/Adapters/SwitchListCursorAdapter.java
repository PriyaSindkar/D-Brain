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
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.xmlHelpers.MainXmlPullParser;
import com.webmyne.android.d_brain.ui.xmlHelpers.XMLValues;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by priyasindkar on 14-09-2015.
 */
public class SwitchListCursorAdapter extends CursorRecyclerViewAdapter<SwitchListCursorAdapter.ViewHolder>{
    private  Context mCtx;
    static int VIEW_TYPE;
    private int totalNoOfSwitches;
    private ArrayList<XMLValues> switchStatus;

    public onLongClickListener _longClick;
    public onSingleClickListener _singleClick;
    public onFavoriteClickListener _favoriteClick;
    public onAddToSceneClickListener _addToSceneClick;
    public onAddSchedulerClickListener _addSchedulerClick;
    public onRenameClickListener _renameClick;

    public SwitchListCursorAdapter(Context context){
        super(context );
        mCtx = context;
    }


    public SwitchListCursorAdapter(Context context, Cursor cursor, ArrayList<XMLValues> _switchStatus){
        super(context,cursor);
        mCtx = context;
        this.switchStatus = _switchStatus;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    public class ListViewHolder extends ViewHolder {
        public  TextView txtSwitchName;
        public ImageView imgFavoriteOption, imgAddToSceneOption, imgAddSchedulerOption, imgRenameOption;
        public LinearLayout linearSwitch;
        public SwitchButton imgSwitch;

        public ListViewHolder ( View view ) {
            super ( view );
            this.txtSwitchName = (TextView) view.findViewById(R.id.txtSwitchName);
            this.linearSwitch = (LinearLayout) view.findViewById(R.id.linearSwitch);

            this.imgFavoriteOption = (ImageView) view.findViewById(R.id.imgFavoriteOption);
            this.imgAddToSceneOption = (ImageView) view.findViewById(R.id.imgAddToSceneOption);
            this.imgAddSchedulerOption = (ImageView) view.findViewById(R.id.imgAddSchedulerOption);
            this.imgRenameOption = (ImageView) view.findViewById(R.id.imgRenameOption);

            this.imgSwitch = (SwitchButton)view.findViewById(R.id.imgSwitch);
        }
    }

    public class GridViewHolder extends ViewHolder{
        public  TextView txtSwitchName;
        public LinearLayout linearSwitch;
        public SwitchButton imgSwitch;

        public GridViewHolder ( View view ) {
            super ( view );
            this.txtSwitchName = (TextView) view.findViewById(R.id.txtSwitchName);
            this.linearSwitch = (LinearLayout) view.findViewById(R.id.linearSwitch);
            this.imgSwitch = (SwitchButton)view.findViewById(R.id.imgSwitch);
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

    public  void setSwitchStatus(ArrayList<XMLValues> _switchStatus) {
        this.switchStatus = _switchStatus;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                ViewGroup viewgroup1 = ( ViewGroup ) mInflater.inflate ( R.layout.scene_switch_full_item, parent, false );
                ListViewHolder listHolder = new ListViewHolder (viewgroup1);
                return listHolder;
            case 1:
                ViewGroup viewgroup2 = ( ViewGroup ) mInflater.inflate(R.layout.switch_grid_item, parent, false);
                GridViewHolder gridHolder = new GridViewHolder (viewgroup2);
                return gridHolder;
            default:
                ViewGroup viewgroup3 = ( ViewGroup ) mInflater.inflate ( R.layout.scene_switch_full_item, parent, false );
                GridViewHolder gridHolder1 = new GridViewHolder (viewgroup3);
                return gridHolder1;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final Cursor cursor) {
        int switchNameIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME);
        final int position = cursor.getPosition();
        final String strPosition = String.format("%02d", (position + 1));


        switch (viewHolder.getItemViewType () ) {
            case 0:
                final ListViewHolder listHolder = ( ListViewHolder ) viewHolder;
                listHolder.txtSwitchName.setText(cursor.getString(switchNameIndex));

                Log.e("XML onBind", switchStatus.toString());
                Log.e("Onbind value ", "POS: "+ position+ " "+ switchStatus.get(position).tagName + " "+ switchStatus.get(position).tagValue );


                if( switchStatus.get(position).tagValue.equals("00")) {
                    Log.e("IF value ", "POS: " + position + " ");
                    listHolder.imgSwitch.setChecked(false);
                    listHolder.linearSwitch.setBackgroundResource(R.drawable.off_switch_border);
                } else {
                    Log.e("ELSE value ", "POS: " + position + " ");
                    listHolder.imgSwitch.setChecked(true);
                    listHolder.linearSwitch.setBackgroundResource(R.drawable.on_switch_border);
                }

//                listHolder.imgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        Log.e("onCheckedChanged ","POS "+ position);
//                        if (isChecked) {
//                            Log.e("# IF", "On checked changes IF");
//                            Log.e("CURR_POS IF", "POS " + position);
//
//                            String CHANGE_STATUS_URL = AppConstants.URL_MACHINE_IP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + "01";
//                            new ChangeSwitchStatus().execute(CHANGE_STATUS_URL);
//
//
//                            listHolder.linearSwitch.setBackgroundResource(R.drawable.on_switch_border);
//                        } else {
//                            Log.e("# ELSE", "On checked changes else");
//                            Log.e("CURR_POS ELSE","POS "+ position);
//
//                            String CHANGE_STATUS_URL = AppConstants.URL_MACHINE_IP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + "00";
//                            new ChangeSwitchStatus().execute(CHANGE_STATUS_URL);
//                            listHolder.linearSwitch.setBackgroundResource(R.drawable.off_switch_border);
//                        }
//                    }
//                });



                listHolder.imgSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listHolder.imgSwitch.toggle();

                        if(listHolder.imgSwitch.isChecked()){
                            Log.e("# IF", "On checked changes IF");
                            Log.e("CURR_POS IF", "POS " + position);

                            String CHANGE_STATUS_URL = AppConstants.URL_MACHINE_IP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + "00";
                            new ChangeSwitchStatus().execute(CHANGE_STATUS_URL);


                            listHolder.linearSwitch.setBackgroundResource(R.drawable.on_switch_border);
                        }else{
                            Log.e("# ELSE", "On checked changes else");
                            Log.e("CURR_POS ELSE","POS "+ position);

                            String CHANGE_STATUS_URL = AppConstants.URL_MACHINE_IP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + "01";
                            new ChangeSwitchStatus().execute(CHANGE_STATUS_URL);
                            listHolder.linearSwitch.setBackgroundResource(R.drawable.off_switch_border);
                        }

//                        String CHANGE_STATUS_URL = AppConstants.URL_MACHINE_IP + AppConstants.URL_CHANGE_SWITCH_STATUS + strPosition + "01";
//                        new ChangeSwitchStatus().execute(CHANGE_STATUS_URL);
                    }
                });

                /*listHolder.linearSwitch.setOnClickListener(new View.OnClickListener() {
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
                });

                listHolder.imgRenameOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _renameClick.onRenameOptionClick(position);
                    }
                });*/

                break;
            case 1:
                final GridViewHolder groupViewHolder = ( GridViewHolder ) viewHolder;
                groupViewHolder.txtSwitchName.setText(cursor.getString(switchNameIndex));

                groupViewHolder.imgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            groupViewHolder.linearSwitch.setBackgroundResource(R.drawable.on_switch_border);
                        } else {
                            groupViewHolder.linearSwitch.setBackgroundResource(R.drawable.off_switch_border);
                        }

                    }
                });

                groupViewHolder.linearSwitch.setOnLongClickListener(new View.OnLongClickListener() {
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



    public class ChangeSwitchStatus extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL urlValue = new URL(params[0]);
                Log.e("# urlValue", urlValue.toString());

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

                //new GetSwitchStatus().execute();
                _singleClick.onSingleClick(0);

            }catch(Exception e){
            }
        }

    }

    public class GetSwitchStatus extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL urlValue = new URL(AppConstants.URL_MACHINE_IP + AppConstants.URL_FETCH_SWITCH_STATUS);
                Log.e("# urlValue", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();
                MainXmlPullParser pullParser = new MainXmlPullParser();

                switchStatus = pullParser.processXML(inputStream);
                Log.e("XML", switchStatus.toString());

            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e("TAG_ASYNC", "Inside onPostExecute");
            try{
            }catch(Exception e){
            }
        }

    }
}
