package com.webmyne.android.d_brain.ui.Helpers;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by priyasindkar on 09-09-2015.
 */
public class Utils {

    public static int pxToDp(float px, Context _context) {
        DisplayMetrics displayMetrics = _context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}
