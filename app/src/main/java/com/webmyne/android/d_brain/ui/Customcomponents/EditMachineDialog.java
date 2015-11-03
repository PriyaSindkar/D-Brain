package com.webmyne.android.d_brain.ui.Customcomponents;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.animation.SlideEnter.SlideLeftEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;
import com.webmyne.android.d_brain.ui.xmlHelpers.MainXmlPullParser;
import com.webmyne.android.d_brain.ui.xmlHelpers.XMLValues;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by priyasindkar on 16-09-2015.
 */
public class EditMachineDialog extends BaseDialog {
    private EditText edtInputName, edtInputDetails;
    private TextView txtRename;
    private ImageView imgCancel;
    private onRenameClickListener _onRenameClick;
    private String oldName="", oldDetails = null, newMachineIP = "", machineSerialNo = "";
    private Context mContext;
    private Cursor machineCursor;
    private int pos;
    ArrayList<XMLValues> powerStatus = new ArrayList<>();
    private ProgressBar progress_bar;

    public EditMachineDialog(Context context) {
        super(context);
    }

    public EditMachineDialog(Context context, String _oldName) {
        super(context);
        this.oldName = _oldName;
        this.mContext = context;
    }

    public EditMachineDialog(Context context, int pos, String _oldName, String _oldDetails) {
        super(context);
        this.oldName = _oldName;
        this.oldDetails = _oldDetails;
        this.pos = pos;
        this.mContext = context;
    }

   /* public RenameDialog(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mContext = context;
    }

    public RenameDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }
*/
    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new SlideLeftEnter());

        View inflate = View.inflate(context, R.layout.dialog_edit_machine, null);

        txtRename = (TextView) inflate.findViewById(R.id.txtRename);
        edtInputDetails = (EditText) inflate.findViewById(R.id.edtInputDetails);
        edtInputName = (EditText) inflate.findViewById(R.id.edtInputName);
        imgCancel = (ImageView) inflate.findViewById(R.id.imgCancel);
        progress_bar = (ProgressBar) inflate.findViewById(R.id.progress_bar);

        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return inflate;
    }


    @Override
    public boolean setUiBeforShow() {
         edtInputName.setText(oldName);
         edtInputDetails.setText(oldDetails);

        txtRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtInputName.getText().toString().trim().length() == 0) {
                    Toast.makeText(context, "Machine Name cannot be blank", Toast.LENGTH_SHORT).show();
                } else if(edtInputDetails.getText().toString().trim().length() == 0) {
                    Toast.makeText(context, "Machine IP cannot be blank", Toast.LENGTH_SHORT).show();
                } else {
                    if(edtInputDetails.getText().toString().trim().startsWith("https://")) {
                        newMachineIP = edtInputDetails.getText().toString().trim().replace("https://", "http://");
                    } else if( !edtInputDetails.getText().toString().trim().startsWith("http://")){
                        newMachineIP = "http://"+edtInputDetails.getText().toString().trim();
                    } else {
                        newMachineIP = edtInputDetails.getText().toString().trim();
                    }

                    try {
                        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
                        dbHelper.openDataBase();

                        machineCursor =  dbHelper.getAllMachines();
                        machineCursor.moveToPosition(pos);
                        final String machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                        final String machineSerialNo = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_SERIALNO));
                        final String oldMachineIP = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP));

                        boolean isMachineIPExists = dbHelper.isMachineIPExists(newMachineIP, oldMachineIP);
                        dbHelper.close();
                        if(isMachineIPExists) {
                            Toast.makeText(mContext, "IP Address Already exists", Toast.LENGTH_LONG).show();
                        } else {

                            String[] params = new String[4];
                            params[0] = newMachineIP;
                            params[1] = machineId;
                            params[2] = edtInputName.getText().toString().trim();
                            params[3] = machineSerialNo;


                            new GetMachineStatus().execute(params);


                        }


                    } catch (SQLException e) {
                        Log.e("TAG EXP", e.toString());
                    }
                }

            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return true;
    }


    public class GetMachineStatus extends AsyncTask<String, Void, Void> {
        boolean isError = false;
        String machineId, serialNoFromDB, newMachineName, newMachineIP;

        @Override
        protected void onPreExecute() {
            //setProgressBarIndeterminateVisibility(true);
            progress_bar.setVisibility(View.VISIBLE);
            // txtNext.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL urlValue;

                newMachineIP = params[0];
                machineId = params[1];
                newMachineName = params[2];
                serialNoFromDB = params[3];

                if(newMachineIP.startsWith("https://")) {
                    newMachineIP = newMachineIP.replace("https://", "http://");
                    urlValue = new URL(newMachineIP + AppConstants.URL_FETCH_MACHINE_STATUS);
                } else if(!newMachineIP.startsWith("http://")) {
                    urlValue = new URL("http://" + newMachineIP + AppConstants.URL_FETCH_MACHINE_STATUS);
                } else {
                    urlValue = new URL( newMachineIP + AppConstants.URL_FETCH_MACHINE_STATUS);
                }
                Log.e("# urlValue", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);

                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();
                //  Log.e("# inputStream", inputStream.toString());
                MainXmlPullParser pullParser = new MainXmlPullParser();
                powerStatus = pullParser.processXML(inputStream);
                Log.e("XML PARSERED", powerStatus.toString());


            } catch (Exception e) {
                Log.e("# EXP", e.toString());
                isError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progress_bar.setVisibility(View.GONE);

            if(!isError) {
                try {
                    for (int i = 0; i < powerStatus.size(); i++) {
                        if (powerStatus.get(i).tagName.equals("DSN")) {
                            machineSerialNo = powerStatus.get(i).tagValue;
                        }
                    }

                    if (machineSerialNo.equals(serialNoFromDB)) {
                        _onRenameClick.onRenameOptionClick(0, edtInputName.getText().toString().trim(), EditMachineDialog.this.newMachineIP);
                        dismiss();
                    } else {
                        Toast.makeText(mContext, "Invalid IP Address", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Invalid IP Address", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(mContext, "Invalid IP Address", Toast.LENGTH_LONG).show();
            }
        }
    }



    public void setRenameListener(onRenameClickListener obj){
        this._onRenameClick = obj;
    }
}
