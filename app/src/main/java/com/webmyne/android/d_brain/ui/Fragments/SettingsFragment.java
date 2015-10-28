package com.webmyne.android.d_brain.ui.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Helpers.ComplexPreferences;
import com.webmyne.android.d_brain.ui.Model.UserSettings;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;


public class SettingsFragment extends Fragment {
    Toolbar toolbar;
    SwitchButton imgSwitchIsStartupEnabled;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();

        return fragment;
    }

    public SettingsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        ((HomeDrawerActivity) getActivity()).setTitle("Settings");
        ((HomeDrawerActivity) getActivity()).hideAppBarButton();

        imgSwitchIsStartupEnabled = (SwitchButton) view.findViewById(R.id.imgSwitchIsStartupEnabled);

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "settings-pref", 0);
        UserSettings userSettings = complexPreferences.getObject("settings-pref", UserSettings.class);

        if(userSettings != null) {
            if(userSettings.isStartupEnabled()) {
                imgSwitchIsStartupEnabled.setChecked(true);
            }
        }

        imgSwitchIsStartupEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final UserSettings settings = new UserSettings();
                settings.setIsStartupEnabled(isChecked);

                ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "settings-pref", 0);
                complexPreferences.putObject("settings-pref", settings);
                complexPreferences.commit();
            }
        });





    }

}
