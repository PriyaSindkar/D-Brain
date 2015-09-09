package com.webmyne.android.d_brain.ui.Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;


public class MainPanelFragment extends Fragment {


    public static MainPanelFragment newInstance() {
        MainPanelFragment fragment = new MainPanelFragment();
        return fragment;
    }

    public MainPanelFragment() {
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
        View view = inflater.inflate(R.layout.fragment_main_panel, container, false);
        init(view);

        return view;
    }

    private void init(View view) {
        ((HomeDrawerActivity) getActivity()).setTitle("Main Panel");
    }


}
