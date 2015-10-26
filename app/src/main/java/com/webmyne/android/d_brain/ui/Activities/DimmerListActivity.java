package com.webmyne.android.d_brain.ui.Activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.DimmerListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.RenameDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.SceneListDialog;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onCheckedChangeListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
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

public class DimmerListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtTitle;
    private ImageView imgBack, imgListGridToggle;
    private RecyclerView mRecyclerView;
    private DimmerListCursorAdapter adapter;
    private Cursor dimmerListCursor, machineListCursor;
    ArrayList<XMLValues> dimmerStatusList, allDimmerStatusList;
    private String[] machineIPs;

    private ProgressBar progressBar;
    private int totalNoOfDimmers = 77;
    private boolean isListView = true, isFirstTime = true;


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
                            new GetDimmerStatus().execute(machineIPs);
                            Log.e("TIMER", "Timer Start");
                        } else {
                            PauseTimer();
                            ResumeTimer();
                        }
                    }
                });

            }
        }, 0, 6000 * 1);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dimmer);

        init();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        initArrayOfDimmers();


        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //layoutManager.supportsPredictiveItemAnimations();
        mRecyclerView.setLayoutManager(layoutManager);
        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), DimmerListActivity.this);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        // fetch dimmer status periodically
           ResumeTimer();
        // call first time
        //new GetDimmerStatus().execute();

        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);

        imgListGridToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListView) {
                    isListView = false;
                    imgListGridToggle.setImageResource(R.drawable.ic_list_view);
                    android.support.v7.widget.GridLayoutManager layoutManager = new android.support.v7.widget.GridLayoutManager(DimmerListActivity.this, 2);
                    layoutManager.supportsPredictiveItemAnimations();
                    mRecyclerView.setLayoutManager(layoutManager);
                    adapter.setType(1);
                    adapter.notifyDataSetChanged();

                } else {
                    isListView = true;
                    imgListGridToggle.setImageResource(R.drawable.ic_grid_view);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(DimmerListActivity.this));
                    adapter.setType(0);
                    adapter.notifyDataSetChanged();
                }
            }
        });

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

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgListGridToggle = (ImageView) findViewById(R.id.imgListGridToggle);

        txtTitle = (TextView) findViewById(R.id.toolbarTitle);
        txtTitle.setText("Dimmers");

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    private void initArrayOfDimmers() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //insert dimmers in adapter for machine-1
        try {
            dbHelper.openDataBase();
            dimmerListCursor =  dbHelper.getAllDimmerComponents();
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

    public class GetDimmerStatus extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                allDimmerStatusList = new ArrayList<>();

                for(int i=0; i<params.length; i++) {
                    String machineBaseURL = "";

                    if(params[i].startsWith("http://")) {
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

                    dimmerStatusList = pullParser.processXML(inputStream);
                    Log.e("XML PARSERED", dimmerStatusList.toString());

                    DatabaseHelper dbHelper = new DatabaseHelper(DimmerListActivity.this);
                    try {
                        dbHelper.openDataBase();
                        Cursor cursor =  dbHelper.getAllDimmerComponentsForAMachine(params[i]);
                        int totalDimmersofMachine = 0;
                        if(cursor != null) {
                            totalDimmersofMachine = cursor.getCount();
                        }

                        for(int j=0 ;j <totalDimmersofMachine ; j++) {
                            allDimmerStatusList.add(dimmerStatusList.get(j));
                        }
                        dbHelper.close();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                progressBar.setVisibility(View.GONE);
                if(isFirstTime) {
                    //init adapter
                    adapter = new DimmerListCursorAdapter(DimmerListActivity.this, dimmerListCursor, allDimmerStatusList);
                    adapter.setType(0);
                    adapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    isFirstTime = false;
                } else {
                    //set adapter again
                    adapter.setDimmerStatus(allDimmerStatusList);
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
                        PopupMenu popup = new PopupMenu(DimmerListActivity.this, view);
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

                        popup.show();//showing popup menu
                    }
                });

            } catch (Exception e) {
            }
        }
    }

    private void renameComponent(int pos) {
        final int position = pos;
        dimmerListCursor.moveToPosition(position);
        final String componentId = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        final String componentName = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));

        RenameDialog renameDialog = new RenameDialog(DimmerListActivity.this, componentName);
        renameDialog.show();

        renameDialog.setRenameListener(new onRenameClickListener() {
            @Override
            public void onRenameOptionClick(int pos, String newName) {
                try {
                    DatabaseHelper dbHelper = new DatabaseHelper(DimmerListActivity.this);
                    dbHelper.openDataBase();
                    dbHelper.renameComponent(componentId, newName);
                    dimmerListCursor = dbHelper.getAllDimmerComponents();
                    dbHelper.close();
                    adapter.changeCursor(dimmerListCursor);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onRenameOptionClick(int pos, String oldName, String oldDetails) {

            }
        });
    }

    private void addComponentToScene(int pos) {
        timer.cancel();
        dimmerListCursor.moveToPosition(pos);
        String componentId = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        String componentType = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
        SceneListDialog dialog = new SceneListDialog(DimmerListActivity.this, componentId, componentType);
        dialog.show();
    }

    private void addComponentToFavourite(int pos) {
        dimmerListCursor.moveToPosition(pos);
        String componentId = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID));
        String componentPrimaryId = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_ID));
        String componentName = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
        String componentType = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE));
        String machineIP = dimmerListCursor.getString(dimmerListCursor.getColumnIndexOrThrow(DBConstants.KEY_C_MIP));
        String machineName = "";
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(DimmerListActivity.this);
            dbHelper.openDataBase();
            machineName = dbHelper.getMachineNameByIP(machineIP);
            int dimmerCount = dbHelper.getComponentTypeCountInFavourite(componentType);

            if(dimmerCount <10) {
                boolean isAlreadyAFavourite = dbHelper.insertIntoFavorite(componentPrimaryId, componentId, componentName, componentType, machineIP, machineName);
                dbHelper.close();

                if (isAlreadyAFavourite) {
                    Toast.makeText(DimmerListActivity.this, "Already added to Favorite!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DimmerListActivity.this, "Added to Favorite Successfully!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DimmerListActivity.this, "Cannot add more than 10 dimmers to favourites.", Toast.LENGTH_SHORT).show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
