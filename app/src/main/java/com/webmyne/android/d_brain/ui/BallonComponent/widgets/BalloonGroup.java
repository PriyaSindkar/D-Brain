/*
 * FileName:	BallonGroup.java
 * Copyright:	kyson
 * Author: 		kysonX
 * Description:	<文件描述>
 * History:		2014-11-18 1.00 初始版本
 */
package com.webmyne.android.d_brain.ui.BallonComponent.widgets;


import android.content.Context;
import android.graphics.PixelFormat;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;


public class BalloonGroup {
    public interface OnBalloonFlyedListener {
        void onBalloonFlyed();
    }

    // 可配置 气球个数
    // public static final int BALLON_COUNT = 5;

    private Context mContext;

    private WindowManager mWindowManager;
    private FrameLayout mContainer;

    private OnBalloonFlyedListener mOnBalloonFlyedListener;

    public BalloonGroup(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 释放气球
     */
    public void startFly() {
        // 设置容器
        setContainer();

        if (mContainer != null && mContainer.getChildCount() <= 0
                && mContainer.getParent() != null) {
            if (mOnBalloonFlyedListener != null) {
                mOnBalloonFlyedListener.onBalloonFlyed();
            }
            mWindowManager.removeView(mContainer);
        }


    }

    /**
     * 设置容器
     */
    private void setContainer() {
        LayoutParams wmParams = new LayoutParams();

        wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.format = PixelFormat.RGBA_8888;

        wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_NOT_TOUCHABLE;
        // 设置悬浮窗口长宽数据
        wmParams.width = LayoutParams.FILL_PARENT;
        wmParams.height = LayoutParams.FILL_PARENT;

        if (mContainer == null) {
            mContainer = new FrameLayout(mContext);
        }
        // 添加容器
        if (mContainer.getParent() == null) {
            mWindowManager.addView(mContainer, wmParams);
        }
    }




    /**
     * @return 返回 mOnBalloonFlyedListener
     */
    public OnBalloonFlyedListener getOnBalloonFlyedListener() {
        return mOnBalloonFlyedListener;
    }

    /**
     * @param mOnBalloonFlyedListener
     */
    public void setOnBalloonFlyedListener(
            OnBalloonFlyedListener mOnBalloonFlyedListener) {
        this.mOnBalloonFlyedListener = mOnBalloonFlyedListener;
    }

}
