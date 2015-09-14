package com.webmyne.android.d_brain.ui.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.DimmerListAdapter;
import com.webmyne.android.d_brain.ui.Adapters.MotorListAdapter;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;

import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class DimmerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtTitle;
    private ImageView imgBack, imgListGridToggle;
    private RecyclerView mRecyclerView;
    private DimmerListAdapter adapter;
    private int totalNoOfDimmers = 77;
    private boolean isListView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dimmer);

        init();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //layoutManager.supportsPredictiveItemAnimations();
        mRecyclerView.setLayoutManager(layoutManager);
        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), DimmerActivity.this);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);

        adapter = new DimmerListAdapter(DimmerActivity.this, totalNoOfDimmers);
        adapter.setType(0);
        adapter.setHasStableIds(true);

        mRecyclerView.setAdapter(adapter);

        adapter.setSingleClickListener(new onSingleClickListener() {
            @Override
            public void onSingleClick(int pos) {
                //Toast.makeText(DimmerActivity.this, "Single Click Item Pos: " + pos, Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setLongClickListener(new onLongClickListener() {

            @Override
            public void onLongClick(int pos) {
                Toast.makeText(DimmerActivity.this, "Options Will Open Here", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setFavoriteClickListener(new onFavoriteClickListener() {
            @Override
            public void onFavoriteOptionClick(int pos) {
                Toast.makeText(DimmerActivity.this, "Added to Favorite Sccessful!", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setAddSchedulerClickListener(new onAddSchedulerClickListener() {

            @Override
            public void onAddSchedulerOptionClick(int pos) {
                Toast.makeText(DimmerActivity.this, "Added To Scheduler Sccessful!", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setAddToSceneClickListener(new onAddToSceneClickListener() {
            @Override
            public void onAddToSceneOptionClick(int pos) {
                Toast.makeText(DimmerActivity.this, "Added to Scene Sccessful!", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setRenameClickListener(new onRenameClickListener() {

            @Override
            public void onRenameOptionClick(int pos) {
                Toast.makeText(DimmerActivity.this, "Rename Sccessful!", Toast.LENGTH_SHORT).show();
            }
        });

        imgListGridToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListView) {
                    isListView = false;
                    imgListGridToggle.setImageResource(R.drawable.ic_list_view);
                    android.support.v7.widget.GridLayoutManager layoutManager = new android.support.v7.widget.GridLayoutManager(DimmerActivity.this, 2);
                    layoutManager.supportsPredictiveItemAnimations();
                    mRecyclerView.setLayoutManager(layoutManager);
                    adapter.setType(1);
                    adapter.notifyDataSetChanged();

                } else {
                    isListView = true;
                    imgListGridToggle.setImageResource(R.drawable.ic_grid_view);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(DimmerActivity.this));
                    adapter.setType(0);
                    adapter.notifyDataSetChanged();
                }
            }
        });
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

}
