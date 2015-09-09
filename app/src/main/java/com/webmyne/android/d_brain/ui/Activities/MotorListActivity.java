package com.webmyne.android.d_brain.ui.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.MotorListAdapter;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;


public class MotorListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MotorListAdapter adapter;
    private Toolbar toolbar;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        //changeLayout = (ImageView) findViewById(R.id.changeLayout);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //layoutManager.supportsPredictiveItemAnimations();
        mRecyclerView.setLayoutManager(layoutManager);
        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), MotorListActivity.this);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        /*ArrayList<MotorSwitchItem> motorSwitchItemList = new ArrayList<>();
        for(int i=0 ; i< 33; i++) {
            motorSwitchItemList.add(new MotorSwitchItem(MotorListActivity.this));
        }*/

        adapter = new MotorListAdapter(MotorListActivity.this);
        adapter.setType(0);
        adapter.setHasStableIds(true);

        mRecyclerView.setAdapter(adapter);

        /*adapter.setRotateClickListener(new onRotateSwitchesListener() {
            @Override
            public void onRotateSwitchesClick(int pos) {

                View v = mRecyclerView.getChildAt(pos);
                Toast.makeText(MotorListActivity.this, "Option 1 is Clicked of Item " + pos, Toast.LENGTH_SHORT).show();
                // motorSwitchItem.rotateMotorButtons();
            }
        });*/

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
