package com.webmyne.android.d_brain.ui.Activities;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.SceneAdapter;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Helpers.PopupAnimationEnd;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;
import com.webmyne.android.d_brain.ui.Model.onItemClickListener;
import com.webmyne.android.d_brain.ui.Widgets.SceneDimmerItem;
import com.webmyne.android.d_brain.ui.Widgets.SceneMotorItem;
import com.webmyne.android.d_brain.ui.Widgets.SceneSwitchItem;

import java.util.ArrayList;


public class SceneActivity extends AppCompatActivity implements View.OnClickListener{
    Toolbar toolbar;
    TextView txtSwitch, txtDimmer, txtMotor;
    ImageView imgHScrollLeft, imgHScrollRight, imgBack;
    HorizontalScrollView hScrollView;
    LinearLayout linearControls, linearPopup;
    RelativeLayout parentRelativeLayout;

    boolean isSwitchPopupShown = false;
    boolean isDimmerPopupShown = false;
    boolean isMotorPopupShown = false;

    AnimationHelper animationHelper = new AnimationHelper();

    ArrayList<SceneItemsDataObject> mData = new ArrayList<>();
    RecyclerView mRecycler;
    SceneAdapter mAdapter;

    private int totalNoOfSwitches = 77;
    private int totalNoOfMotors = 33;
    private int totalNoOfDimmers = 77;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            //toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
            setSupportActionBar(toolbar);
        }

        init();

    }

    private void init() {
        txtDimmer = (TextView) findViewById(R.id.txtDimmer);
        txtSwitch = (TextView) findViewById(R.id.txtSwitch);
        txtMotor = (TextView) findViewById(R.id.txtMotor);

        hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);
        imgHScrollLeft = (ImageView) findViewById(R.id.imgHScrollLeft);
        imgHScrollRight = (ImageView) findViewById(R.id.imgHScrollRight);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        txtSwitch.setOnClickListener(this);
        txtMotor.setOnClickListener(this);
        txtDimmer.setOnClickListener(this);

        imgHScrollLeft.setOnClickListener(this);
        imgHScrollRight.setOnClickListener(this);
        hScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        linearControls =  (LinearLayout) findViewById(R.id.linearControls);
        linearPopup =  (LinearLayout) findViewById(R.id.linearPopup);
        parentRelativeLayout = (RelativeLayout) findViewById(R.id.parentRelativeLayout);


        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemViewCacheSize(0);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);

        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), SceneActivity.this);
        mRecycler.addItemDecoration(new VerticalSpaceItemDecoration(margin));

        mAdapter = new SceneAdapter(this,mData);
        mAdapter.setType(0);
        mRecycler.setAdapter(mAdapter);

        mRecycler.setOnClickListener(this);



        parentRelativeLayout.setOnClickListener(this);

        mAdapter.setOnItemClick(new onItemClickListener() {
            @Override
            public void _onItemClickListener() {
                txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                txtMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                linearPopup.setVisibility(View.INVISIBLE);
            }
        });

        mAdapter.setSingleClickListener(new onSingleClickListener() {
            @Override
            public void onSingleClick(int pos) {
                //Toast.makeText(DimmerActivity.this, "Single Click Item Pos: " + pos, Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setLongClickListener(new onLongClickListener() {

            @Override
            public void onLongClick(int pos) {
                Toast.makeText(SceneActivity.this, "Options Will Open Here", Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setFavoriteClickListener(new onFavoriteClickListener() {
            @Override
            public void onFavoriteOptionClick(int pos) {
                Toast.makeText(SceneActivity.this, "Added to Favorite Sccessful!", Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setAddSchedulerClickListener(new onAddSchedulerClickListener() {

            @Override
            public void onAddSchedulerOptionClick(int pos) {
                Toast.makeText(SceneActivity.this, "Added To Scheduler Sccessful!", Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setAddToSceneClickListener(new onAddToSceneClickListener() {
            @Override
            public void onAddToSceneOptionClick(int pos) {
                Toast.makeText(SceneActivity.this, "Added to Scene Sccessful!", Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setRenameClickListener(new onRenameClickListener() {

            @Override
            public void onRenameOptionClick(int pos) {
                Toast.makeText(SceneActivity.this, "Rename Sccessful!", Toast.LENGTH_SHORT).show();
            }
        });

        /*imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_scene, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtSwitch:
                showSwitchPopup();
                break;
            case R.id.txtDimmer:
                showDimmerPopup();
                break;

            case R.id.txtMotor:
                showMotorPopup();
                break;

            case R.id.imgHScrollLeft:
                hScrollView.scrollTo((int) hScrollView.getScrollX() - 20, (int) hScrollView.getScrollY());
                break;

            case R.id.imgHScrollRight:
                hScrollView.scrollTo((int) hScrollView.getScrollX() + 20, (int) hScrollView.getScrollY());
                break;

            case R.id.parentRelativeLayout:
                txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                txtMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                linearPopup.setVisibility(View.GONE);
                break;
        }

    }

    private void showSwitchPopup() {

        if( !isSwitchPopupShown) {

            txtSwitch.setBackgroundColor(getResources().getColor(R.color.baseButtonColor));
            txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            txtMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));

            isSwitchPopupShown = true;
            isDimmerPopupShown = false;
            isMotorPopupShown = false;

            linearPopup.setVisibility(View.VISIBLE);
            linearControls.removeAllViews();

            for(int i=0; i < totalNoOfSwitches; i++) {
                SceneSwitchItem sceneSwitchItem = new SceneSwitchItem(SceneActivity.this);
                sceneSwitchItem.setText("Switch "+i);
                linearControls.addView(sceneSwitchItem);

                int idx = linearControls.indexOfChild(sceneSwitchItem);
                sceneSwitchItem.setTag("SWITCH_"+Integer.toString(idx));
                sceneSwitchItem.setOnClickListener(buttonClick);
            }

            animationHelper.viewPopUpMenuFromBottomLeft(linearPopup);


        } else { // if switch popup is opened, close it

            txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));

            isSwitchPopupShown = false;
            isDimmerPopupShown = false;
            isMotorPopupShown = false;

            animationHelper.closePopUpMenuFromBottomLeft(linearPopup);
            animationHelper.setInterFaceObj(new PopupAnimationEnd() {
                @Override
                public void animationCompleted() {
                    linearPopup.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void showDimmerPopup() {
        if( !isDimmerPopupShown) {

            txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            txtMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            txtDimmer.setBackgroundColor(getResources().getColor(R.color.baseButtonColor));

            isSwitchPopupShown = false;
            isDimmerPopupShown = true;
            isMotorPopupShown = false;

            linearPopup.setVisibility(View.VISIBLE);
            linearControls.removeAllViews();

            for(int i=0; i < totalNoOfDimmers; i++) {
                SceneDimmerItem sceneSwitchItem = new SceneDimmerItem(SceneActivity.this);
                sceneSwitchItem.setText("Dimmer "+i);
                linearControls.addView(sceneSwitchItem);

                int idx = linearControls.indexOfChild(sceneSwitchItem);
                sceneSwitchItem.setTag("DIMMER_"+Integer.toString(idx));
                sceneSwitchItem.setOnClickListener(buttonClick);

            }

            animationHelper.viewPopUpMenuFromBottomLeft(linearPopup);

        } else { // if switch popup is opened, close it
            txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));

            isSwitchPopupShown = false;
            isDimmerPopupShown = false;
            isMotorPopupShown = false;

            animationHelper.closePopUpMenuFromBottomLeft(linearPopup);
            animationHelper.setInterFaceObj(new PopupAnimationEnd() {
                @Override
                public void animationCompleted() {
                    linearPopup.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void showMotorPopup() {
        if( !isMotorPopupShown) {

            txtSwitch.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            txtDimmer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            txtMotor.setBackgroundColor(getResources().getColor(R.color.baseButtonColor));

            isSwitchPopupShown = false;
            isDimmerPopupShown = false;
            isMotorPopupShown = true;

            linearPopup.setVisibility(View.VISIBLE);
            linearControls.removeAllViews();

            for(int i=0; i < totalNoOfMotors; i++) {
                SceneMotorItem sceneSwitchItem = new SceneMotorItem(SceneActivity.this);
                sceneSwitchItem.setText("Motor "+i);
                linearControls.addView(sceneSwitchItem);

                int idx = linearControls.indexOfChild(sceneSwitchItem);
                sceneSwitchItem.setTag("MOTOR_"+Integer.toString(idx));
                sceneSwitchItem.setOnClickListener(buttonClick);
            }

            animationHelper.viewPopUpMenuFromBottomLeft(linearPopup);

        } else { // if switch popup is opened, close it
            txtMotor.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            isSwitchPopupShown = false;
            isDimmerPopupShown = false;
            isMotorPopupShown = false;

            animationHelper.closePopUpMenuFromBottomLeft(linearPopup);
            animationHelper.setInterFaceObj(new PopupAnimationEnd() {
                @Override
                public void animationCompleted() {
                    linearPopup.setVisibility(View.INVISIBLE);
                }
            });
        }
    }


    View.OnClickListener buttonClick = new View.OnClickListener() {
        public void onClick(View v) {
            Log.e("CLICK","CLICK");
            if(v.isFocusable()) {
                v.setFocusable(false);

                if (v instanceof SceneSwitchItem) {
                    mAdapter.add(mData.size(), new SceneItemsDataObject(0));
                }

                if(v instanceof SceneDimmerItem) {
                    mAdapter.add(mData.size(), new SceneItemsDataObject(1));
                }

                if (v instanceof SceneMotorItem) {
                    mAdapter.add(mData.size(), new SceneItemsDataObject(2));
                }
            } else {
                Toast.makeText(SceneActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
