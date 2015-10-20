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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.SwitchesListActivity;
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
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
public class SensorListCursorAdapter extends CursorRecyclerViewAdapter<SensorListCursorAdapter.ViewHolder>{
    private  Context mCtx;
    static int VIEW_TYPE;
    private ArrayList<XMLValues> sensorStatus;

    public onLongClickListener _longClick;
    public onSingleClickListener _singleClick;
    public onFavoriteClickListener _favoriteClick;
    public onAddToSceneClickListener _addToSceneClick;
    public onAddSchedulerClickListener _addSchedulerClick;
    public onRenameClickListener _renameClick;
    public onCheckedChangeListener _switchClick;

    public SensorListCursorAdapter(Context context){
        super(context );
        mCtx = context;
    }


    public SensorListCursorAdapter(Context context, Cursor cursor, ArrayList<XMLValues> _sensorStatus){
        super(context,cursor);
        mCtx = context;
        this.sensorStatus = _sensorStatus;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    public class ListViewHolder extends ViewHolder {
        public  TextView txtSensorName, txtMachineName, txtSensorDetails;
        public ImageView imgRenameOption;
        public LinearLayout linearSensor;

        public ListViewHolder ( View view ) {
            super ( view );
            this.txtSensorName = (TextView) view.findViewById(R.id.txtSensorName);
            this.txtMachineName = (TextView) view.findViewById(R.id.txtMachineName);
            this.txtSensorDetails = (TextView) view.findViewById(R.id.txtSensorDetails);
            this.linearSensor = (LinearLayout) view.findViewById(R.id.linearSensor);

            this.imgRenameOption = (ImageView) view.findViewById(R.id.imgRenameOption);
        }
    }


   /* @Override
    public int getItemViewType(int position) {
        if(VIEW_TYPE == 0)
            return 0;
        else
            return 1;
    }

    public void setType(int type){
        VIEW_TYPE = type;
    }*/

    public  void setSensorStatus(ArrayList<XMLValues> _sensorStatus) {
        this.sensorStatus = _sensorStatus;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        ViewGroup viewgroup1 = ( ViewGroup ) mInflater.inflate ( R.layout.sensor_list_item, parent, false );
        ListViewHolder listHolder = new ListViewHolder (viewgroup1);
        return listHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final Cursor cursor) {
        int nameIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME);
        int machineNameIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_MNAME);
        int detailsNameIndex = cursor.getColumnIndexOrThrow(DBConstants.KEY_C_DETAILS);
        final int position = cursor.getPosition();
        final String strPosition = String.format("%02d", (position + 1));

        final ListViewHolder listHolder = ( ListViewHolder ) viewHolder;
        AdvancedSpannableString sp = new AdvancedSpannableString("Sensor Name: "+cursor.getString(nameIndex));
        sp.setColor(mCtx.getResources().getColor(R.color.yellow), "Sensor Name:");
        listHolder.txtSensorName.setText(sp);

        listHolder.txtSensorDetails.setText(cursor.getString(detailsNameIndex));

        sp = new AdvancedSpannableString("Machine Name: "+cursor.getString(machineNameIndex));
        sp.setColor(mCtx.getResources().getColor(R.color.yellow), "Machine Name:");
        listHolder.txtMachineName.setText(sp);

        listHolder.imgRenameOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _renameClick.onRenameOptionClick(position, listHolder.txtSensorName.getText().toString().trim(), listHolder.txtSensorDetails.getText().toString().trim());
            }
        });

    }

    public void setSingleClickListener(onSingleClickListener obj){
        this._singleClick = obj;
    }
    public void setLongClickListener(onLongClickListener obj){
        this._longClick = obj;
    }


    public void setRenameClickListener(onRenameClickListener obj){
        this._renameClick = obj;
    }

}
