package com.webmyne.android.d_brain.ui.Activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
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
import com.webmyne.android.d_brain.ui.Customcomponents.AddToSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.LongPressOptionsDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.SceneListDialog;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.Model.SchedulerModel;
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
    private Cursor switchListCursor, machineListCursor;
    private String[] machineIPs;
    ArrayList<XMLValues> switchStatusList, allSwitchesStatusList;
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
                            new GetSwitchStatus().execute(machineIPs);
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

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.recycler_view) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_launcher_screen, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterViewCompat.AdapterContextMenuInfo info = (AdapterViewCompat.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
*/
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
           // switchListCursor =  dbHelper.getAllSwitchComponentsForAMachine(DashboardFragment.MACHINE_IP);
            switchListCursor =  dbHelper.getAllSwitchComponents();
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
                allSwitchesStatusList = new ArrayList<>();
                for(int i=0; i<params.length; i++) {
                    String machineBaseURL = "";

                    if(params[i].startsWith("http://")) {
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

                    switchStatusList = pullParser.processXML(inputStream);
                    Log.e("XML PARSERED", switchStatusList.toString());
                    DatabaseHelper dbHelper = new DatabaseHelper(SwitchesListActivity.this);
                    try {
                        dbHelper.openDataBase();
                        Cursor cursor =  dbHelper.getAllSwitchComponentsForAMachine(params[i]);
                        int totalSwitchesofMachine = 0;
                        if(cursor != null) {
                            totalSwitchesofMachine = cursor.getCount();
                        }
                        Log.e("totalSwitchesofMachine", totalSwitchesofMachine+"");
                        for(int j=0 ;j <totalSwitchesofMachine ; j++) {
                            allSwitchesStatusList.add(switchStatusList.get(j));
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
                progressBar.setVisibility(View.GONE);
                if(isFirstTime) {
                    //init adapter
                    adapter = new SwitchListCursorAdapter(SwitchesListActivity.this, switchListCursor, allSwitchesStatusList);
                    adapter.setType(0);
                    adapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    isFirstTime = false;
                } else {
                    //set adapter again
                    adapter.setSwitchStatus(allSwitchesStatusList);
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
                        renameComponent(pos);
                    }

                    @Override
                    public void onRenameOptionClick(int pos, String oldName, String oldDetails) {

                    }
                });

                adapter.setAddToSceneClickListener(new onAddToSceneClickListener() {
                    @Override
                    public void onAddToSceneOptionClick(int pos) {
                        addComponentToScene(pos);
                    }
                });

                adapter.setFavoriteClickListener(new onFavoriteClickListener() {
                    @Override
                    public void onFavoriteOptionClick(int pos) {
                        addComponentToFavourite(pos);
                    }
                });

                adapter.setLongClickListener(new onLongClickListener() {

                    @Override
                    public void onLongClick(final int pos, View view) {

                        LongPressOptionsDialog longPressOptionsDialog = new LongPressOptionsDialog(SwitchesListActivity.this, pos);
                        longPressOptionsDialog.show();

                        longPressOptionsDialog.setRenameClickListener(new onRenameClickListener() {
                            @Override
                            public void onRenameOptionClick(int pos, String oldName) {
                                renameComponent(pos);
                            }

                            @Override
                            public void onRenameOptionClick(int pos, String oldName, String oldDetails) {

                            }
                        });

                        longPressOptionsDialog.setFavoriteClickListener(new onFavoriteClickListener() {
                            @Override
                            public void onFavoriteOptionClick(int pos) {
                                addComponentToFavourite(pos);
                            }
                        });

                        longPressOptionsDialog.setAddToSceneClickListener(new onAddToSceneClickListener() {
                            @Override
                            public void onAddToSceneOptionClick(int pos) {
                                addComponentToScene(pos);
                            }
                        });

                        longPressOptionsDialog.setAddSchedulerClickListener(new onAddSchedulerClickListener() {
                            @Override
                            public void onAddSchedulerOptionClick(int pos) {
                                addComponentToScheduler(pos);
                            }
                        });

                        /*PopupMenu popup = new PopupMenu(SwitchesListActivity.this, view);
                        popup.getMenuInflater().inflate(R.menu.menu_components, popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.action_rename:
                                        renameComponent(pos);
                                        break;

                                    case R.id.action_add_to_scene:
                                        addComponentToScene(pos);
                                        break;

                                    case R.id.action_add_to_favorite:
                                        addComponentToFavourite(pos);
                                        break;
                                } //switch end
                                return true;
                            }
                        });

                        popup.show();//showing popup menu*/
                    }
                });

                adapter.setAddSchedulerClickListener(new onAddSchedulerClickListener() {

                    @Override
                    public void onAddSchedulerOptionClick(int pos) {
                        addComponentToScheduler(pos);
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

    private void addComponentToFavourite(int pos) {
        switchListCursor.moveToPosition(pos);
        String componentId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
        String componentPrimaryId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        String componentName = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
        String componentType = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
        String componentMachineID = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID));
        String machineIP = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP));
        String machineName = "";
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(SwitchesListActivity.this);
            dbHelper.openDataBase();
            machineName = dbHelper.getMachineNameByIP(machineIP);
            int switchCount = dbHelper.getComponentTypeCountInFavourite(componentType);

            if (switchCount < 10) {
                boolean isAlreadyAFavourite = dbHelper.insertIntoFavorite(componentPrimaryId, componentId, componentName, componentType, componentMachineID,machineIP, machineName);
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

    private void renameComponent(int pos) {
        final int position = pos;
        switchListCursor.moveToPosition(position);
        final String componentId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        final String componentName = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));


        RenameDialog renameDialog = new RenameDialog(SwitchesListActivity.this, componentName);
        renameDialog.show();

        renameDialog.setRenameListener(new onRenameClickListener() {
            @Override
            public void onRenameOptionClick(int pos, String newName) {
                try {
                    DatabaseHelper dbHelper = new DatabaseHelper(SwitchesListActivity.this);
                    dbHelper.openDataBase();
                    dbHelper.renameComponent(componentId, newName);
                    switchListCursor = dbHelper.getAllSwitchComponents();
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

    private void addComponentToScene(int pos) {
        timer.cancel();
        switchListCursor.moveToPosition(pos);
        String componentId1 = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        String componentType = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
        SceneListDialog dialog = new SceneListDialog(SwitchesListActivity.this, componentId1, componentType);
        dialog.show();
    }

    private void addComponentToScheduler(int pos) {
        switchListCursor.moveToPosition(pos);
        String componentPrimaryId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        String componentId = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
        String componentType = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
        String componentName = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
        String componentMachineName = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MNAME));
        String componentMachineID = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MID));
        String componentMachineIP = switchListCursor.getString(switchListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP));

        try {
            DatabaseHelper dbHelper = new DatabaseHelper(SwitchesListActivity.this);
            dbHelper.openDataBase();
            boolean isAlreadyScheduled = dbHelper.isAlreadyScheduled(componentId, componentMachineID);
            dbHelper.close();

            if(! isAlreadyScheduled) {
                SchedulerModel schedulerModel = new SchedulerModel("", componentPrimaryId, componentId,componentName, componentType, componentMachineID,componentMachineIP, componentMachineName, false, "00");

                AddToSchedulerDialog addToSchedulerDialog = new AddToSchedulerDialog(SwitchesListActivity.this, schedulerModel);
                addToSchedulerDialog.show();
            } else {
                Toast.makeText(SwitchesListActivity.this, "This component is already scheduled.", Toast.LENGTH_SHORT).show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


}
