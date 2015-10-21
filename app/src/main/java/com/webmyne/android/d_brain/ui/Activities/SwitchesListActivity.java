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
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.SwitchListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.SceneListDialog;
import com.webmyne.android.d_brain.ui.Fragments.DashboardFragment;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
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
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class SwitchesListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private SwitchListCursorAdapter adapter;
    private Toolbar toolbar;
    private ImageView imgBack, imgListGridToggle;
    private TextView toolbarTitle;

    private boolean isListView = true, isFirstTime = true;
    private Cursor switchListCursor;
    ArrayList<XMLValues> switchStatusList;
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
                            new GetSwitchStatus().execute();
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
        imgListGridToggle = (ImageView) findViewById(R.id.imgListGridToggle);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Switches");


        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        initArrayOfSwitches();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), SwitchesListActivity.this);
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
                Log.e("back", "back");
                timer.cancel();
                finish();
            }
        });


        imgListGridToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListView) {
                    isListView = false;
                    imgListGridToggle.setImageResource(R.drawable.ic_list_view);
                    android.support.v7.widget.GridLayoutManager layoutManager = new android.support.v7.widget.GridLayoutManager(SwitchesListActivity.this, 3);
                    layoutManager.supportsPredictiveItemAnimations();
                    mRecyclerView.setLayoutManager(layoutManager);
                    adapter.setType(1);
                    adapter.notifyDataSetChanged();

                } else {
                    isListView = true;
                    imgListGridToggle.setImageResource(R.drawable.ic_grid_view);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(SwitchesListActivity.this));
                    adapter.setType(0);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();
    }

    private void initArrayOfSwitches() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //insert switches in adapter ofr machine-1
        try {
            dbHelper.openDataBase();
            switchListCursor =  dbHelper.getAllSwitchComponentsForAMachine(DashboardFragment.MACHINE_IP);
            Log.e("switchListCursor", switchListCursor.getCount()+"");
            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public class GetSwitchStatus extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL urlValue = new URL(DashboardFragment.URL_MACHINE_IP + AppConstants.URL_FETCH_SWITCH_STATUS);
               // Log.e("# urlValue", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                inputStream = httpUrlConnection.getInputStream();
              //  Log.e("# inputStream", inputStream.toString());
                MainXmlPullParser pullParser = new MainXmlPullParser();

                switchStatusList = pullParser.processXML(inputStream);
                Log.e("XML PARSERED", switchStatusList.toString());


            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
           // Log.e("TAG_ASYNC", "Inside onPostExecute");
            try {
                progressBar.setVisibility(View.GONE);
                if(isFirstTime) {
                    //init adapter
                    adapter = new SwitchListCursorAdapter(SwitchesListActivity.this, switchListCursor, switchStatusList);
                    adapter.setType(0);
                    adapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    isFirstTime = false;
                } else {
                    //set adapter again
                    adapter.setSwitchStatus(switchStatusList);
                    adapter.notifyDataSetChanged();
                }

                adapter.setCheckedChangeListener(new onCheckedChangeListener() {
                    @Override
                    public void onCheckedChangeClick(int pos) {
                        isDelay  = false;
                    }
                });

                adapter.setRenameClickListener(new onRenameClickListener() {

                    @Override
                    public void onRenameOptionClick(int pos, String _oldName) {
                        final int position = pos;
                        switchListCursor.moveToPosition(position);
                        final String componentId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));

                        RenameDialog renameDialog = new RenameDialog(SwitchesListActivity.this, _oldName);
                        renameDialog.show();

                        renameDialog.setRenameListener(new onRenameClickListener() {
                            @Override
                            public void onRenameOptionClick(int pos, String newName) {
                                try {
                                    DatabaseHelper dbHelper = new DatabaseHelper(SwitchesListActivity.this);
                                    dbHelper.openDataBase();
                                    dbHelper.renameComponent(componentId, newName);
                                    switchListCursor = dbHelper.getAllSwitchComponentsForAMachine(DashboardFragment.MACHINE_IP);
                                    dbHelper.close();
                                    adapter.changeCursor(switchListCursor);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onRenameOptionClick(int pos, String newName, String newDetails) {

                            }
                        });
                    }

                    @Override
                    public void onRenameOptionClick(int pos, String oldName, String oldDetails) {

                    }
                });

                adapter.setAddToSceneClickListener(new onAddToSceneClickListener() {
                    @Override
                    public void onAddToSceneOptionClick(int pos) {
                        timer.cancel();
                        switchListCursor.moveToPosition(pos);
                        String componentId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                        String componentType = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
                        SceneListDialog dialog = new SceneListDialog(SwitchesListActivity.this, componentId, componentType);
                        dialog.show();

                        //Toast.makeText(SwitchesListActivity.this, "Added to Scene Sccessful!", Toast.LENGTH_SHORT).show();
                    }
                });

                adapter.setFavoriteClickListener(new onFavoriteClickListener() {
                    @Override
                    public void onFavoriteOptionClick(int pos) {
                        switchListCursor.moveToPosition(pos);
                        String componentId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
                        String componentName = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
                        String componentType = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
                        String machineIP = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP));
                        String machineName = "";
                        try {
                            DatabaseHelper dbHelper = new DatabaseHelper(SwitchesListActivity.this);
                            dbHelper.openDataBase();
                            machineName = dbHelper.getMachineNameByIP(machineIP);
                            int switchCount = dbHelper.getComponentTypeCountInFavourite(componentType);

                            Log.e("switchCount", switchCount+"");

                            if(switchCount <10) {
                                boolean isAlreadyAFavourite = dbHelper.insertIntoFavorite(componentId, componentName, componentType, machineIP, machineName);
                                dbHelper.close();

                                if (isAlreadyAFavourite) {
                                    Toast.makeText(SwitchesListActivity.this, "Already added to Favorite.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SwitchesListActivity.this, "Added to Favorite Successfully.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SwitchesListActivity.this, "Cannot add more than 10 switches to favourites.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });

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
