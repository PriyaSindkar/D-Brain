package com.webmyne.android.d_brain.ui.Activities;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.DimmerListCursorAdapter;
import com.webmyne.android.d_brain.ui.Adapters.FavouriteListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.SaveAlertDialog;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;
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
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class FavouriteListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FavouriteListCursorAdapter adapter;
    private Toolbar toolbar;
    private ImageView imgBack, imgListGridToggle, imgEmpty;
    private TextView toolbarTitle, txtEmptyView;
    private LinearLayout linearEmptyView;

    private Cursor favouriteListCursor, machineListCursor;
    ArrayList<XMLValues> switchStatusList, dimmerStatusList;
    ArrayList<XMLValues> favouriteComponentStatusList;
    private String[] machineIPs;
    private InputStream inputStream;
    private ProgressBar progressBar;
    private Timer timer1;
    private Handler handler1;

    public void startTherad(){
        handler1 = new Handler();
        timer1 = new Timer();


        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        new GetSwitchStatus().execute(machineIPs);
                    }
                });

            }
        }, 0, 2000 * 1);
    }

    public void stopTherad(){
        try {
            timer1.cancel();
        }catch (Exception e){

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        linearEmptyView = (LinearLayout) findViewById(R.id.linearEmptyView);
        imgEmpty = (ImageView) findViewById(R.id.imgEmpty);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Favourites");

        imgListGridToggle = (ImageView) findViewById(R.id.imgListGridToggle);
        imgListGridToggle.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        txtEmptyView = (TextView) findViewById(R.id.txtEmptyView);

        initArrayOfFavourties();
        // get initial status of all components from all machines
        new GetSwitchStatus().execute(machineIPs);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), FavouriteListActivity.this);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        // fetch component status periodically
       // ResumeTimer();

        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setSupportsChangeAnimations(false);
        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(0);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    stopTherad();
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConstants.getCurrentSsid(FavouriteListActivity.this);
        //startTherad();
    }

    @Override
    public void onPause() {
        super.onPause();

       // stopTherad();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

       // stopTherad();

    }

    @Override
    public void onStop() {
        super.onStop();

      //  stopTherad();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // stopTherad();

    }

    private void initArrayOfFavourties() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //get all favourite in adapter from all machines
        try {
            dbHelper.openDataBase();
            favouriteListCursor =  dbHelper.getAllFavouriteComponents();
            Log.e("favouriteListCursor", favouriteListCursor.getCount()+"");

            // get all machine IPs in an array
            machineListCursor = dbHelper.getAllMachines();

            if(machineListCursor != null) {
                if(machineListCursor.getCount() > 0) {
                    machineIPs = new String[machineListCursor.getCount()];
                    machineListCursor.moveToFirst();
                    int i = 0;
                    do {
                        String machineIP = machineListCursor.getString(machineListCursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP));
                        machineIPs[i] = machineIP;
                        i++;
                    } while(machineListCursor.moveToNext());
                }
            }
            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public class GetSwitchStatus extends AsyncTask<String, Void, Void> {
        Cursor machineCursor;
        String machineIp, machineId;
        boolean isMachineActive = true;
        DatabaseHelper dbHelper = new DatabaseHelper(FavouriteListActivity.this);

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(String... params) {
                switchStatusList = new ArrayList<>();

                for(int i=0; i<params.length; i++) {

                    String machineBaseURL = "";
                    machineIp = params[i];

                    if(machineIp.startsWith("http://")) {
                        machineBaseURL = machineIp;
                    } else {
                        machineBaseURL = "http://" + machineIp;
                    }

                    try {
                        dbHelper.openDataBase();

                        isMachineActive = dbHelper.isMachineActive(machineIp);
                        machineCursor = dbHelper.getMachineByIP(machineIp);
                        machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));


                        if (isMachineActive) {
                            URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_SWITCH_STATUS);
                            Log.e("# urlValue", urlValue.toString());

                            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                            httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                            httpUrlConnection.setRequestMethod("GET");
                            inputStream = httpUrlConnection.getInputStream();
                            //  Log.e("# inputStream", inputStream.toString());
                            MainXmlPullParser pullParser = new MainXmlPullParser();

                            ArrayList<XMLValues> tempSwitchStatusList = pullParser.processXML(inputStream);
                            Log.e("XML PARSERED", tempSwitchStatusList.toString());

                            try {
                                dbHelper.openDataBase();
                                Cursor cursor = dbHelper.getAllSwitchComponentsForAMachine(params[i]);
                                int totalSwitchesofMachine = 0;
                                if (cursor != null) {
                                    totalSwitchesofMachine = cursor.getCount();
                                }
                                for (int j = 0; j < totalSwitchesofMachine; j++) {
                                    switchStatusList.add(tempSwitchStatusList.get(j));
                                }
                                dbHelper.close();

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }  else {
                            Log.e("TAG_FAV", "INACTIVE");
                            try {
                                dbHelper.openDataBase();
                                Cursor cursor = dbHelper.getAllSwitchComponentsForAMachine(params[i]);
                                if (cursor != null) {
                                    cursor.moveToFirst();
                                    if (cursor.getCount() > 0) {
                                        do {
                                            String componnetId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                                            XMLValues values = new XMLValues();
                                            values.tagName = componnetId;
                                            values.tagValue = AppConstants.OFF_VALUE;
                                            switchStatusList.add(values);

                                        } while (cursor.moveToNext());
                                    }
                                }
                            } catch (Exception ex) {
                            }
                        }
                    } catch(Exception e){
                        Log.e("TAG_FAV", "TIMEOUT");

                        try {
                            dbHelper.openDataBase();
                            Cursor cursor = dbHelper.getAllSwitchComponentsForAMachine(params[i]);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                if (cursor.getCount() > 0) {
                                    do {
                                        String componnetId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                                        XMLValues values = new XMLValues();
                                        values.tagName = componnetId;
                                        values.tagValue = AppConstants.OFF_VALUE;
                                        switchStatusList.add(values);

                                    } while (cursor.moveToNext());
                                }
                            }
                        } catch (Exception ex) {
                        }
                    }
                }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
           // Log.e("TAG_ASYNC", "Inside onPostExecute");
            try {
                //init dimmer status
               new GetDimmerStatus().execute(machineIPs);

            } catch (Exception e) {
            }
        }
    }

    public class GetDimmerStatus extends AsyncTask<String, Void, Void> {
        Cursor machineCursor;
        String machineIp, machineId;
        boolean isMachineActive = true;
        DatabaseHelper dbHelper = new DatabaseHelper(FavouriteListActivity.this);

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(String... params) {
                dimmerStatusList =  new ArrayList<>();
                for (int i = 0; i < params.length; i++) {
                    String machineBaseURL = "";
                    machineIp = params[i];

                    if(machineIp.startsWith("http://")) {
                        machineBaseURL = machineIp;
                    } else {
                        machineBaseURL = "http://" + machineIp;
                    }

                    try {
                        dbHelper.openDataBase();

                        isMachineActive = dbHelper.isMachineActive(machineIp);
                        machineCursor = dbHelper.getMachineByIP(machineIp);
                        machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));

                        if(isMachineActive) {

                            URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_DIMMER_STATUS);
                            Log.e("# urlValue", urlValue.toString());

                            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                            httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                            httpUrlConnection.setRequestMethod("GET");
                            InputStream inputStream = httpUrlConnection.getInputStream();
                            //  Log.e("# inputStream", inputStream.toString());
                            MainXmlPullParser pullParser = new MainXmlPullParser();

                            ArrayList<XMLValues> tempDimmerStatusList = pullParser.processXML(inputStream);
                            //Log.e("XML PARSERED", dimmerStatusList.toString());


                            try {
                                dbHelper.openDataBase();
                                Cursor cursor = dbHelper.getAllDimmerComponentsForAMachine(machineIp);
                                int totalDimmersofMachine = 0;
                                if (cursor != null) {
                                    totalDimmersofMachine = cursor.getCount();
                                }
                                for (int j = 0; j < totalDimmersofMachine; j++) {
                                    dimmerStatusList.add(tempDimmerStatusList.get(j));
                                }

                                dbHelper.close();

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("TAG_FAV", "INACTIVE");
                            try {
                                dbHelper.openDataBase();
                                Cursor cursor = dbHelper.getAllDimmerComponentsForAMachine(machineIp);
                                if (cursor != null) {
                                    cursor.moveToFirst();
                                    if (cursor.getCount() > 0) {
                                        do {
                                            String componnetId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                                            XMLValues values = new XMLValues();
                                            values.tagName = componnetId;
                                            values.tagValue = AppConstants.OFF_VALUE + AppConstants.OFF_VALUE;
                                            dimmerStatusList.add(values);

                                        } while (cursor.moveToNext());
                                    }
                                }
                            } catch (Exception ex) {
                            }
                        }

                    }catch (Exception ex) {
                        Log.e("TAG_FAV", "TIMEOUT");
                        try {
                            dbHelper.openDataBase();
                            Cursor cursor = dbHelper.getAllDimmerComponentsForAMachine(params[i]);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                if (cursor.getCount() > 0) {
                                    do {
                                        String componnetId = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                                        XMLValues values = new XMLValues();
                                        values.tagName = componnetId;
                                        values.tagValue = AppConstants.OFF_VALUE + AppConstants.OFF_VALUE;
                                        dimmerStatusList.add(values);

                                    } while (cursor.moveToNext());
                                }
                            }
                        } catch(Exception e) {}
                    }
                    }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                progressBar.setVisibility(View.GONE);
                // init favourite component initial status list
                favouriteComponentStatusList = new ArrayList<>();

                if(favouriteListCursor != null) {
                    favouriteListCursor.moveToFirst();

                    if(favouriteListCursor.getCount() > 0) {
                        do{
                            String favouriteComponentId = favouriteListCursor.getString(favouriteListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                            for(int i=0; i< switchStatusList.size(); i++) {
                                if(switchStatusList.get(i).tagName.equals(favouriteComponentId)) {
                                    XMLValues values = new XMLValues();
                                    values.tagName = favouriteComponentId;
                                    values.tagValue = switchStatusList.get(i).tagValue;
                                    favouriteComponentStatusList.add(values);
                                    break;
                                }
                            }

                            for(int i=0; i< dimmerStatusList.size(); i++) {
                                if(dimmerStatusList.get(i).tagName.equals(favouriteComponentId)) {
                                    XMLValues values = new XMLValues();
                                    values.tagName = favouriteComponentId;
                                    values.tagValue = dimmerStatusList.get(i).tagValue;
                                    favouriteComponentStatusList.add(values);
                                    break;
                                }
                            }

                        } while (favouriteListCursor.moveToNext());

                    } else {
                        linearEmptyView.setVisibility(View.VISIBLE);
                        imgEmpty.setImageResource(R.drawable.ic_action_favorite);
                        txtEmptyView.setText("You Have No Favourites");
                    }
                } else {
                    linearEmptyView.setVisibility(View.VISIBLE);
                    imgEmpty.setImageResource(R.drawable.ic_action_favorite);
                    txtEmptyView.setText("You Have No Favourites");
                }

                //init adapter
                adapter = new FavouriteListCursorAdapter(FavouriteListActivity.this, favouriteListCursor,favouriteComponentStatusList);
                adapter.setHasStableIds(true);
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                deleteComponent();
                switchChangeCheck();

            } catch (Exception e) {
            }
        }
    }

    private void deleteComponent() {
        adapter.setDeleteClickListener(new onDeleteClickListener() {
            @Override
            public void onDeleteOptionClick(final int pos) {

                SaveAlertDialog saveAlertDialog = new SaveAlertDialog(FavouriteListActivity.this, "Are you sure you want to remove this component from favorites?");
                saveAlertDialog.show();

                saveAlertDialog.setSaveListener(new onSaveClickListener() {
                    @Override
                    public void onSaveClick(boolean isSave) {
                        if (isSave) {
                            deleteFromFavourite(pos);
                        } else {
                        }
                    }
                });
            }
        });
    }

    private void switchChangeCheck() {
        adapter.setCheckedChangeListener(new onCheckedChangeListener() {
            @Override
            public void onCheckedChangeClick(int pos) {
                if(pos == 1) {
                    finish();
                }
            }

            @Override
            public void onCheckedPreChangeClick(int pos) {

            }
        });
    }

    private void deleteFromFavourite(int pos) {
        favouriteListCursor.moveToPosition(pos);

        try {
            DatabaseHelper dbHelper = new DatabaseHelper(FavouriteListActivity.this);
            dbHelper.openDataBase();
            String componentId = favouriteListCursor.getString(favouriteListCursor.getColumnIndexOrThrow(DBConstants.KEY_F_CID));

            dbHelper.deleteComponentFromFavourite(componentId);

            favouriteListCursor = dbHelper.getAllFavouriteComponents();

            dbHelper.close();
            try {
                mRecyclerView.setAdapter(null);
            } catch (Exception e) {}

            /*favouriteComponentStatusList.remove(pos);
            adapter.setComponentStatus(favouriteComponentStatusList);
            adapter.changeCursor(favouriteListCursor);
            adapter.notifyDataSetChanged();*/

            adapter = new FavouriteListCursorAdapter(FavouriteListActivity.this, favouriteListCursor);
            adapter.setHasStableIds(true);
            mRecyclerView.setAdapter(adapter);
            favouriteComponentStatusList.remove(pos);
            adapter.setComponentStatus(favouriteComponentStatusList);
            adapter.notifyDataSetChanged();

            deleteComponent();
            switchChangeCheck();

            if(favouriteListCursor == null || favouriteListCursor.getCount() == 0) {
                linearEmptyView.setVisibility(View.VISIBLE);
                imgEmpty.setImageResource(R.drawable.ic_action_favorite);
                txtEmptyView.setText("You Have No Favourites");
            }
        } catch (Exception e) {
            Log.e("DB EXP", e.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


}
