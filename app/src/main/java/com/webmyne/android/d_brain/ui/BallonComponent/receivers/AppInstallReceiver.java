
package com.webmyne.android.d_brain.ui.BallonComponent.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.webmyne.android.d_brain.ui.BallonComponent.storage.PackageInfoStorage;

public class AppInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 更新安装包列表
        PackageInfoStorage.updateHomeList(context);
    }
}
