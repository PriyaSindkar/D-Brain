
package com.webmyne.android.d_brain.ui.BallonComponent.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.webmyne.android.d_brain.ui.BallonComponent.services.ReleaseService;
import com.webmyne.android.d_brain.ui.BallonComponent.storage.PreferenceHelper;
import com.webmyne.android.d_brain.ui.BallonComponent.utils.ServiceUtil;

public class BootBroadcastReceiver extends BroadcastReceiver {
    // 重写onReceive方法
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean b = ServiceUtil.isWorked(context,
                ReleaseService.class.getName());
        if (PreferenceHelper.isRunning(context) && !b) {
            ReleaseService.startService(context);
        }
    }

}
