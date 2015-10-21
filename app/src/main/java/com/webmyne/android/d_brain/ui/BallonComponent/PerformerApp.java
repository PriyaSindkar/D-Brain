package com.webmyne.android.d_brain.ui.BallonComponent;

import android.app.Application;
import android.content.res.Configuration;

public class PerformerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        BalloonPerformer.getInstance().delegateOnConfigurationChanged(this);
    }
}
