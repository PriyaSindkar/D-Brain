package com.webmyne.android.d_brain.ui.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;


public class AddMachineFragment extends Fragment {

    public static AddMachineFragment newInstance() {
        AddMachineFragment fragment = new AddMachineFragment();

        return fragment;
    }

    public AddMachineFragment() {
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
        View view = inflater.inflate(R.layout.fragment_add_machine, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        ((HomeDrawerActivity) getActivity()).setTitle("Add Machine");
    }

}
