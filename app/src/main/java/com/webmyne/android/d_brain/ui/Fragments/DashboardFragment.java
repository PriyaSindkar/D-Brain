package com.webmyne.android.d_brain.ui.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.DimmerActivity;
import com.webmyne.android.d_brain.ui.Activities.MotorListActivity;
import com.webmyne.android.d_brain.ui.Activities.SceneActivity;
import com.webmyne.android.d_brain.ui.Activities.SwitchesListActivity;
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Helpers.PopupAnimationEnd;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;

public class DashboardFragment extends Fragment implements PopupAnimationEnd, View.OnClickListener {

    AnimationHelper animObj;
    Toolbar toolbar;
    ImageView imgOptions, imgHScrollLeft, imgHScrollRight;
    boolean isImageUp = true;
    LinearLayout layoutBottom, linearOptions, linearSceneList;
    HorizontalScrollView hScrollView;
    private FrameLayout parentMotor, parentSlider, parentSwitches;
    private TextView txtNoOfSwitchUnits, txtNoOfMotorUnits, txtNoOfSensorUnits, txtNoOfSliderUnits;
    LinearLayout sliderLayout;

    private int myTotalScenes = 5;
    private String noOfSwitchUnits="13", totalNoOfSwitchUnits="17", noOfMotorUnits="13", totalNoOfMotorUnits="17", noOfSliderUnits="13", totalNoOfSliderUnits="17", noOfSensorUnits="13", totalNoOfSensorUnits="17";

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    public DashboardFragment() {
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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        init(view);



        return view;
    }


    private void init(View row) {
        animObj = new AnimationHelper();
        imgOptions = (ImageView) row.findViewById(R.id.imgOptions);
        layoutBottom = (LinearLayout) row.findViewById(R.id.layoutBottom);
        linearOptions = (LinearLayout) row.findViewById(R.id.linearOptions);
        linearSceneList = (LinearLayout) row.findViewById(R.id.linearSceneList);
        hScrollView = (HorizontalScrollView) row.findViewById(R.id.hScrollView);
        imgHScrollLeft = (ImageView) row.findViewById(R.id.imgHScrollLeft);
        imgHScrollRight = (ImageView) row.findViewById(R.id.imgHScrollRight);

        txtNoOfSwitchUnits = (TextView) row.findViewById(R.id.txtNoOfSwitchUnits);
        txtNoOfMotorUnits = (TextView) row.findViewById(R.id.txtNoOfMotorUnits);
        txtNoOfSliderUnits = (TextView) row.findViewById(R.id.txtNoOfSliderUnits);
        txtNoOfSensorUnits = (TextView) row.findViewById(R.id.txtNoOfSensorUnits);

        imgHScrollLeft.setOnClickListener(this);
        imgHScrollRight.setOnClickListener(this);

        parentMotor = (FrameLayout) row.findViewById(R.id.parentMotor);
        parentMotor.setOnClickListener(this);
        parentSlider = (FrameLayout) row.findViewById(R.id.parentSlider);
        parentSlider.setOnClickListener(this);
        parentSwitches = (FrameLayout) row.findViewById(R.id.parentSwitches);
        parentSwitches.setOnClickListener(this);

        hScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        linearOptions.setVisibility(View.GONE);

        imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageUp) {
                    isImageUp = false;
                    // to rotate button(arrow)
                    animObj.rotateViewClockwise((ImageView) v);
                    linearOptions.setVisibility(View.VISIBLE);
                    // to animate popup
                    animObj.viewPopUpMenu(linearOptions);
                } else {
                    animObj.rotateViewAntiClockwise((ImageView) v);
                    animObj.closePopUpMenu(linearOptions);
                    animObj.setInterFaceObj(new PopupAnimationEnd() {
                        @Override
                        public void animationCompleted() {
                            linearOptions.setVisibility(View.GONE);
                            isImageUp = true;
                        }
                    });
                }
            }
        });


        AdvancedSpannableString sp = new AdvancedSpannableString(noOfSwitchUnits+"/"+totalNoOfSwitchUnits);
        sp.setSpan(new RelativeSizeSpan(1.3f), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtNoOfSwitchUnits.setText(sp);

        sp = new AdvancedSpannableString(noOfMotorUnits+"/"+totalNoOfMotorUnits);
        sp.setSpan(new RelativeSizeSpan(1.3f), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtNoOfMotorUnits.setText(sp);

        sp = new AdvancedSpannableString(noOfSliderUnits+"/"+totalNoOfSliderUnits);
        sp.setSpan(new RelativeSizeSpan(1.3f), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtNoOfSliderUnits.setText(sp);

        sp = new AdvancedSpannableString(noOfSensorUnits+"/"+totalNoOfSensorUnits);
        sp.setSpan(new RelativeSizeSpan(1.3f), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtNoOfSensorUnits.setText(sp);

        updateSceneList();
    }

    @Override
    public void onResume() {
        super.onResume();

        HomeDrawerActivity homeScreen = ((HomeDrawerActivity) getActivity());
        homeScreen.setTitle("Dashboard");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgHScrollLeft:
                hScrollView.scrollTo((int) hScrollView.getScrollX() - 20, (int) hScrollView.getScrollY());
                break;

            case R.id.imgHScrollRight:
                hScrollView.scrollTo((int) hScrollView.getScrollX() + 20, (int) hScrollView.getScrollY());
                break;
            case R.id.parentMotor:
                Intent intent = new Intent(getActivity(), MotorListActivity.class);
                startActivity(intent);
                break;

            case R.id.parentSlider:
                intent = new Intent(getActivity(), DimmerActivity.class);
                startActivity(intent);
                break;
            case R.id.parentSwitches:
                intent = new Intent(getActivity(), SwitchesListActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void animationCompleted() {

    }


    private void updateSceneList() {
        for (int i = 0; i < myTotalScenes; i++) {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dashboard_scene_slider_item, null);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), getActivity());
            layoutParams.setMargins(margin, margin, margin, margin);
            view.setLayoutParams(layoutParams);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SceneActivity.class);
                    startActivity(intent);
                }
            });

            linearSceneList.addView(view);
        }
    }


}
