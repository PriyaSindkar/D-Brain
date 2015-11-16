package com.webmyne.android.d_brain.ui.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Fragments.SwitchesListFragment;
import com.webmyne.android.d_brain.ui.Model.Machine;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by priyasindkar on 16-11-2015.
 */
public class MainSwitchesListActivity extends AppCompatActivity {
    private ViewPager pager;
    private SmartTabLayout tabHost;
    private TabsPagerAdapter pagerAdapter;
    private ImageView imgListGridToggle, imgBack;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    public static boolean isListView = true;
    private int currentTabItem = 0;
    private Cursor machineListCursor;
    private List<Machine> machineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_switches_list);
        init();
    }


    private void init() {
        tabHost = (SmartTabLayout) this.findViewById(R.id.tabs);
        pager = (ViewPager) this.findViewById(R.id.pager);
        imgListGridToggle = (ImageView) findViewById(R.id.imgListGridToggle);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        tabHost.setDistributeEvenly(true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Switches");

        //set pager-adapter
        initMachineList();
        // init view pager
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        pagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), isListView, machineList);
        pager.setAdapter(pagerAdapter);
        tabHost.setViewPager(pager);

        pager.setCurrentItem(0);

        tabHost.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("TAG_ACT", "onPageSelected "+ position);
                currentTabItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        imgListGridToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG_ACT", "toggle view");
                isListView = !isListView;
                pagerAdapter.setIsListView(isListView);
                pagerAdapter.notifyDataSetChanged();
                pager.setCurrentItem(currentTabItem);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initMachineList() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //get list of all machines added
        try {
            dbHelper.openDataBase();
            machineListCursor = dbHelper.getAllMachines();
            machineList = new ArrayList<>();

            if(machineListCursor != null) {
                if(machineListCursor.getCount() > 0) {
                    machineListCursor.moveToFirst();
                    do {
                        String machineIP = machineListCursor.getString(machineListCursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP));
                        String machineID = machineListCursor.getString(machineListCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                        String machineName = machineListCursor.getString(machineListCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));

                        Machine machine = new Machine( Integer.parseInt(machineID), machineIP, machineName);
                        machineList.add(machine);
                    } while(machineListCursor.moveToNext());
                }
            }
            dbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class TabsPagerAdapter extends FragmentStatePagerAdapter {
    boolean isListView = true;
    private List<Machine> machines = new ArrayList<>();

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public TabsPagerAdapter(FragmentManager fm, boolean _isListView, List<Machine> _machines) {
        super(fm);
        this.isListView = _isListView;
        this.machines = _machines;
    }

    public void setIsListView(boolean isListView) {
        this.isListView = isListView;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int index) {
        return SwitchesListFragment.newInstance(machines.get(index).getMachineId(), machines.get(index).getMachineName(), isListView);

       /* switch (index) {
            case 0:
                return SwitchesListFragment.newInstance(0, "Machine 1", isListView);
            case 1:
                return SwitchesListFragment.newInstance(0, "Machine 2", isListView);
            case 2:
                return SwitchesListFragment.newInstance(0, "Machine 3", isListView);
        }
        return null;*/
    }



    @Override
    public CharSequence getPageTitle(int position) {
        //return "TAB "+ (position);
        return machines.get(position).getMachineName();
    }

    @Override
    public int getCount() {
        return machines.size();
    }
}
