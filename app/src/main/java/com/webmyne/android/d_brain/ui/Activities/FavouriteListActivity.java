package com.webmyne.android.d_brain.ui.Activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.DimmerListCursorAdapter;
import com.webmyne.android.d_brain.ui.Adapters.FavouriteListCursorAdapter;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
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
    private ImageView imgBack, imgListGridToggle;
    private TextView toolbarTitle, txtEmptyView;

    private Cursor favouriteListCursor, machineListCursor;
    ArrayList<XMLValues> switchStatusList, dimmerStatusList;
    ArrayList<XMLValues> favouriteComponentStatusList;
    private String[] machineIPs;
    private InputStream inputStream;
    private ProgressBar progressBar;
    private Timer timer;
    private Handler handler;
    public static boolean isDelay = false;

    private void PauseTimer(){
        this.timer.cancel();
        Log.e("TIMER", "Timer Paused");
    }

    public void ResumeTimer() {
        handler = new Handler();
        timer = new Timer();


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isDelay) {
                            //new GetSwitchStatus().execute();
                            Log.e("TIMER switch", "Timer Start");
                        } else {
                            PauseTimer();
                            ResumeTimer();
                        }
                    }
                });

            }
        }, 0, 4000 * 1);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgBack = (ImageView) findViewById(R.id.imgBack);

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
        new GetSwitchStatus().execute(machineIPs);

       /* adapter = new FavouriteListCursorAdapter(FavouriteListActivity.this, favouriteListCursor, switchStatusList);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);*/

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), FavouriteListActivity.this);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        // fetch switch status periodically
        ResumeTimer();




        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setSupportsChangeAnimations(false);
        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(0);

       /*   adapter.setLongClickListener(new onLongClickListener() {

            @Override
            public void onLongClick(int pos) {
                Toast.makeText(SwitchesListActivity.this, "Options Will Open Here", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setAddSchedulerClickListener(new onAddSchedulerClickListener() {

            @Override
            public void onAddSchedulerOptionClick(int pos) {
                Toast.makeText(SwitchesListActivity.this, "Added To Scheduler Sccessful!", Toast.LENGTH_SHORT).show();
            }
        });
        */

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();
    }

    private void initArrayOfFavourties() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //insert all favourite in adapter ofr machine-1
        try {
            dbHelper.openDataBase();
            favouriteListCursor =  dbHelper.getAllFavouriteComponents();
            Log.e("favouriteListCursor", favouriteListCursor.getCount()+"");

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

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                switchStatusList = new ArrayList<>();

                for(int i=0; i<params.length; i++) {

                    String machineBaseURL = "";

                    if(params[i].contains("http://")) {
                        machineBaseURL = params[i];
                    } else {
                        machineBaseURL = "http://" + params[i];
                    }

                    URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_SWITCH_STATUS);
                     Log.e("# urlValue", urlValue.toString());

                    HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                    httpUrlConnection.setRequestMethod("GET");
                    inputStream = httpUrlConnection.getInputStream();
                    //  Log.e("# inputStream", inputStream.toString());
                    MainXmlPullParser pullParser = new MainXmlPullParser();

                    ArrayList<XMLValues> tempSwitchStatusList = pullParser.processXML(inputStream);
                    Log.e("XML PARSERED", tempSwitchStatusList.toString());

                    DatabaseHelper dbHelper = new DatabaseHelper(FavouriteListActivity.this);
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
                }

            } catch (Exception e) {
                Log.e("# EXP123", e.toString());
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

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                dimmerStatusList =  new ArrayList<>();
                for (int i = 0; i < params.length; i++) {
                    String machineBaseURL = "";

                    if(params[i].contains("http://")) {
                        machineBaseURL = params[i];
                    } else {
                        machineBaseURL = "http://" + params[i];
                    }

                    URL urlValue = new URL(machineBaseURL + AppConstants.URL_FETCH_DIMMER_STATUS);
                    Log.e("# urlValue", urlValue.toString());

                    HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                    httpUrlConnection.setRequestMethod("GET");
                    InputStream inputStream = httpUrlConnection.getInputStream();
                    //  Log.e("# inputStream", inputStream.toString());
                    MainXmlPullParser pullParser = new MainXmlPullParser();

                    ArrayList<XMLValues> tempDimmerStatusList = pullParser.processXML(inputStream);
                    //Log.e("XML PARSERED", dimmerStatusList.toString());

                    DatabaseHelper dbHelper = new DatabaseHelper(FavouriteListActivity.this);
                    try {
                        dbHelper.openDataBase();
                        Cursor cursor = dbHelper.getAllDimmerComponentsForAMachine(params[i]);
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

                }

                }catch(Exception e){
                    Log.e("# EXP456", e.toString());
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
                        txtEmptyView.setVisibility(View.VISIBLE);
                        txtEmptyView.setText("You Have No Favourites");
                    }
                } else {
                    txtEmptyView.setVisibility(View.VISIBLE);
                    txtEmptyView.setText("You Have No Favourites");
                }

                //init adapter
                adapter = new FavouriteListCursorAdapter(FavouriteListActivity.this, favouriteListCursor,favouriteComponentStatusList);
                adapter.setHasStableIds(true);
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                /*adapter.setDeleteClickListener(new onDeleteClickListener() {
                    @Override
                    public void onDeleteOptionClick(int pos) {
                        favouriteListCursor.moveToPosition(pos);

                        try {
                            DatabaseHelper dbHelper = new DatabaseHelper(FavouriteListActivity.this);
                            dbHelper.openDataBase();
                            String componentId = favouriteListCursor.getString(favouriteListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));

                            dbHelper.deleteComponentFromFavourite(componentId);
                            favouriteListCursor = dbHelper.getAllFavouriteComponents();
                            favouriteComponentStatusList.remove(pos);
                            adapter.setComponentStatus(favouriteComponentStatusList);
                            adapter.changeCursor(favouriteListCursor);

                            dbHelper.close();
                        } catch(Exception e) {
                            Log.e("DB EXP", e.toString());
                        }
                    }
                });*/

            } catch (Exception e) {
            }
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
