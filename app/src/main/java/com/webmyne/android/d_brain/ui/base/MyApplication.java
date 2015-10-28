package com.webmyne.android.d_brain.ui.base;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.webmyne.android.d_brain.ui.BallonComponent.BalloonPerformer;
import com.webmyne.android.d_brain.ui.BallonComponent.configs.Config;
import com.webmyne.android.d_brain.ui.BallonComponent.widgets.BalloonGroup;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;


/**
 * Application class that called once when application is installed for the first time on device.
 * This class includes the integration of Volly [third party framework for calling webservices]
 */
public class MyApplication extends Application {
    /**
     * A singleton instance of the application class for easy access in other places
     */
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        // initialize the singleton
        sInstance = this;
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        try{
            dbHelper.createDataBase();
        }catch(Exception e){
            Log.e("# DB EXP", e.toString());
        }





    }



}
