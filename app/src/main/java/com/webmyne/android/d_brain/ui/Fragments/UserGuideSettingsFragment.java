package com.webmyne.android.d_brain.ui.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.webmyne.android.d_brain.R;


public class UserGuideSettingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int mParam1;
    private String mParam2;
    public EditText edtMachineName, edtIPAddress;
    static   String strMachineName ="", strIPAddress="";
    View convertView;

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
        convertView= inflater.inflate(R.layout.fragment_slider_settings, container, false);

        edtMachineName = (EditText) convertView.findViewById(R.id.edtMachineName);
        edtIPAddress = (EditText) convertView.findViewById(R.id.edtIPAddress);

        edtIPAddress.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                strIPAddress = s.toString().trim();
            }
        });

        edtMachineName.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                strMachineName = s.toString().trim();
            }
        });

        return convertView;
    }

    public String getIPAddress() {
        return strIPAddress;
    }

    public String getStrMachineName() {
        return strMachineName;
    }


}
