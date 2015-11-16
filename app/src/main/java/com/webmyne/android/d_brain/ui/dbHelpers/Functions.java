package com.webmyne.android.d_brain.ui.dbHelpers;

import android.util.Log;

/**
 * Created by krishnakumar on 16-11-2015.
 */
public class Functions {


    public static boolean isSwitchAvialabel(String dpc){
        boolean value = false;
        String trimString = dpc.substring(1,2);
        int totalSwitch = Integer.valueOf(trimString);

        if(totalSwitch>0){
            value = true;
        }

        return value;
    }

    public static boolean isDimmerAvialabel(String dpc){
        boolean value = false;
        String trimString = dpc.substring(2,3);
        int totalDimmer = Integer.valueOf(trimString);

        if(totalDimmer>0){
            value = true;
        }

        return value;
    }



    public static boolean isMotorAvialabel(String dpc){
        boolean value = false;
        String trimString = dpc.substring(3,4);
        int totalMotor = Integer.valueOf(trimString);

        if(totalMotor>0){
            value = true;
        }

        return value;
    }
}
