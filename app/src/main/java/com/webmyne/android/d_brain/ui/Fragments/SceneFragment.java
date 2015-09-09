package com.webmyne.android.d_brain.ui.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;

public class SceneFragment extends Fragment {


    public static SceneFragment newInstance() {
        SceneFragment fragment = new SceneFragment();

        return fragment;
    }

    public SceneFragment() {
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
        View view = inflater.inflate(R.layout.fragment_scene, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        ((HomeDrawerActivity) getActivity()).setTitle("Scenes");
    }

}
