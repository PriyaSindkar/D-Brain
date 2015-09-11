package com.webmyne.android.d_brain.ui.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.webmyne.android.d_brain.R;


public class UserGuideSettingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int mParam1;
    private String mParam2;
    private EditText edtSerialNo, edtIPAddress;

    public static UserGuideSettingsFragment newInstance(int param1, String param2) {
        UserGuideSettingsFragment fragment = new UserGuideSettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UserGuideSettingsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView= inflater.inflate(R.layout.fragment_slider_settings, container, false);

        edtSerialNo = (EditText) convertView.findViewById(R.id.edtSerialNo);
        edtIPAddress = (EditText) convertView.findViewById(R.id.edtIPAddress);

        return convertView;
    }


}
