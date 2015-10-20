package com.webmyne.android.d_brain.ui.Helpers;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by priyasindkar on 09-09-2015.
 */
public class Utils {

    private static final String TAG = "PRODUCT_CODE";

    public static int pxToDp(float px, Context _context) {
        DisplayMetrics displayMetrics = _context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }


    public static boolean validateProductCode(String productCode) {
        boolean flag = true;

        int controlType = Integer.parseInt(productCode.substring(0,1));
        int totalNoOfwitches = Integer.parseInt(productCode.substring(1,2));
        int totalNoOfDimmers = Integer.parseInt(productCode.substring(2,3));
        int totalNoOfMotors = Integer.parseInt(productCode.substring(3,4));
        int totalNoOfAlerts = Integer.parseInt(productCode.substring(4,5));
        String identifier = productCode.substring(5, 6);

        Log.i(TAG, "controlType : "+controlType);
        Log.i(TAG, "totalNoOfSwitches : " + totalNoOfwitches);
        Log.i(TAG, "totalNoOfDimmers : " + totalNoOfDimmers);
        Log.i(TAG, "totalNoOfMotors : " + totalNoOfMotors);
        Log.i(TAG, "totalNoOfAlerts : "+ totalNoOfAlerts);
        Log.i(TAG, "identifier : " + identifier);

        if(controlType != 0 && controlType != 3 && controlType != 5) {
            return false;
        } else {
            if(totalNoOfwitches > 7) {
                return false;
            } else {
                if(totalNoOfDimmers > 3) {
                    return false;
                } else {
                    if(totalNoOfMotors > 3) {
                        return false;
                    } else {
                        /*if( !identifier.equals("-")) {
                            return false;
                        } else {

                        }*/
                    }
                }
            }

        }

        return flag;
    }

}
