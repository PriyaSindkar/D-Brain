package com.webmyne.android.d_brain.ui.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.FavouriteListCursorAdapter;
import com.webmyne.android.d_brain.ui.Adapters.SchedulersListCursorAdapter;
import com.webmyne.android.d_brain.ui.Customcomponents.EditSchedulerDialog;
import com.webmyne.android.d_brain.ui.Customcomponents.SaveAlertDialog;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.SchedulerModel;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;
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

import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class FavoritesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FavouriteListCursorAdapter adapter;
    private TextView txtEmptyView, txtEmptyView1;
    private LinearLayout emptyView;
    private Cursor favouriteListCursor, machineListCursor;
    private ImageView imgEmpty;
    ArrayList<XMLValues> switchStatusList, dimmerStatusList;
    ArrayList<XMLValues> favouriteComponentStatusList;
    private String[] machineIPs;
    private ProgressBar progressBar;

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();

        return fragment;
    }

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scene_list, container, false);
        init(view);

        return view;
    }

    private void init(View view) {
        ((HomeDrawerActivity) getActivity()).setTitle("Favourites");
        ((HomeDrawerActivity) getActivity()).hideAppBarButton();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        emptyView = (LinearLayout) view.findViewById(R.id.emptyView);
        txtEmptyView1 = (TextView) view.findViewById(R.id.txtEmptyView1);
        txtEmptyView = (TextView) view.findViewById(R.id.txtEmptyView);
        imgEmpty = (ImageView) view.findViewById(R.id.imgEmpty);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(layoutManager);
        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), getActivity());
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);


        initArrayOfFavourties();

        // get initial status of all components from all machines
        new GetSwitchStatus().execute(machineIPs);

        if(favouriteListCursor.getCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            imgEmpty.setVisibility(View.VISIBLE);
            imgEmpty.setImageResource(R.drawable.drawer_schedulers);
            txtEmptyView.setText(getResources().getString(R.string.empty_scehdulers_list));
        } else {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);


    }

    public class GetSwitchStatus extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                switchStatusList = new ArrayList<>();

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
                    InputStream inputStream = httpUrlConnection.getInputStream();
                    //  Log.e("# inputStream", inputStream.toString());
                    MainXmlPullParser pullParser = new MainXmlPullParser();

                    ArrayList<XMLValues> tempSwitchStatusList = pullParser.processXML(inputStream);
                    Log.e("XML PARSERED", tempSwitchStatusList.toString());

                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
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
            //setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                dimmerStatusList =  new ArrayList<>();
                for (int i = 0; i < params.length; i++) {
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

                    ArrayList<XMLValues> tempDimmerStatusList = pullParser.processXML(inputStream);
                    //Log.e("XML PARSERED", dimmerStatusList.toString());

                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
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
                        emptyView.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                        imgEmpty.setVisibility(View.VISIBLE);
                        imgEmpty.setImageResource(R.drawable.drawer_schedulers);
                        txtEmptyView.setText("You Have No Favourites");
                    }
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    imgEmpty.setVisibility(View.VISIBLE);
                    imgEmpty.setImageResource(R.drawable.drawer_schedulers);
                    txtEmptyView.setText("You Have No Favourites");
                }

                //init adapter
                adapter = new FavouriteListCursorAdapter(getActivity(), favouriteListCursor,favouriteComponentStatusList);
                adapter.setHasStableIds(true);
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                deleteComponent();

            } catch (Exception e) {
            }
        }
    }

    private void deleteComponent() {
        adapter.setDeleteClickListener(new onDeleteClickListener() {
            @Override
            public void onDeleteOptionClick(final int pos) {

                SaveAlertDialog saveAlertDialog = new SaveAlertDialog(getActivity(), "Are you sure you want to remove this component from favorites?");
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

    private void deleteFromFavourite(int pos) {
        favouriteListCursor.moveToPosition(pos);

        try {
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
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

            adapter = new FavouriteListCursorAdapter(getActivity(), favouriteListCursor);
            adapter.setHasStableIds(true);
            mRecyclerView.setAdapter(adapter);
            favouriteComponentStatusList.remove(pos);
            adapter.setComponentStatus(favouriteComponentStatusList);
            adapter.notifyDataSetChanged();

            deleteComponent();

            if(favouriteListCursor == null || favouriteListCursor.getCount() == 0) {
                emptyView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                imgEmpty.setVisibility(View.VISIBLE);
                imgEmpty.setImageResource(R.drawable.drawer_schedulers);
                txtEmptyView.setText("You Have No Favourites");
            }
        } catch (Exception e) {
            Log.e("DB EXP", e.toString());
        }
    }


    private void initArrayOfFavourties() {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        //get all favourite in adapter from all machines
        try {
            dbHelper.openDataBase();
            favouriteListCursor =  dbHelper.getAllFavouriteComponents();
            Log.e("favouriteListCursor", favouriteListCursor.getCount() + "");

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


}
