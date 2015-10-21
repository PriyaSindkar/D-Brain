package com.webmyne.android.d_brain.ui.BallonComponent;

import android.content.Context;

import com.webmyne.android.d_brain.ui.BallonComponent.configs.Config;
import com.webmyne.android.d_brain.ui.BallonComponent.services.ReleaseService;
import com.webmyne.android.d_brain.ui.BallonComponent.storage.PackageInfoStorage;
import com.webmyne.android.d_brain.ui.BallonComponent.utils.ServiceUtil;
import com.webmyne.android.d_brain.ui.BallonComponent.widgets.BalloonGroup;
import com.webmyne.android.d_brain.ui.BallonComponent.widgets.ReleaseViewManager;


public class BalloonPerformer {
    private static volatile BalloonPerformer mInstance;

    public static BalloonPerformer getInstance() {
        if (mInstance == null) {
            mInstance = new BalloonPerformer();
        }
        return mInstance;
    }

    private BalloonPerformer() {
        if (mConfig == null) {
            mConfig = new Config();
        }
    }

    private Config mConfig;

    private BalloonGroup.OnBalloonFlyedListener mOnBalloonFlyedListener;

    /**
     * 初始化
     */
    public BalloonPerformer init(Context context, Config config) {
        PackageInfoStorage.updateHomeList(context);
        this.mConfig = config;
        return this;
    }

    public BalloonPerformer init(Context context) {
        PackageInfoStorage.updateHomeList(context);
        return this;
    }

    public void show(Context context, BalloonGroup.OnBalloonFlyedListener onFinishListener) {
        this.mOnBalloonFlyedListener = onFinishListener;
        ReleaseService.startService(context);
    }

    public void show(Context context) {
        show(context, null);
    }

    public void gone(Context context) {
        ReleaseService.stopService(context);
    }

    public void delegateOnConfigurationChanged(Context context) {
        boolean b = ServiceUtil.isWorked(context, ReleaseService.class.getName());
        if (b) {
            // 如果服务已经启动了，悬浮窗重新运行
            ReleaseViewManager.getInstance(context.getApplicationContext()).end();
            ReleaseViewManager.getInstance(context.getApplicationContext()).start();
        }
    }

    public Config getConfig() {
        return mConfig;
    }

    public BalloonGroup.OnBalloonFlyedListener getOnBalloonFlyedListener() {
        return mOnBalloonFlyedListener;
    }
}
