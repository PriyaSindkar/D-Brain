package com.webmyne.android.d_brain.ui.BallonComponent.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

public class IntentUtil {

    public static void startIntent(Context context, Intent intent) {
        boolean b = isIntentExist(context, intent);
        if (b) {
            context.startActivity(intent);
        }
    }


    public static boolean isIntentExist(Context context, Intent intent) {
        List<ResolveInfo> localList = context.getPackageManager()
                .queryIntentActivities(intent,
                        PackageManager.GET_INTENT_FILTERS);
        if ((localList != null) && (localList.size() > 0)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取应用市场应用详情的intent
     * 
     * @param context
     * @return
     */
    public static Intent getMarketIntent(Context context) {
        StringBuilder localStringBuilder = new StringBuilder()
                .append("market://details?id=");
        String str = context.getPackageName();
        localStringBuilder.append(str);
        Uri localUri = Uri.parse(localStringBuilder.toString());
        return new Intent(Intent.ACTION_VIEW, localUri);
    }

    /**
     * 获取文本分享的itent
     * 
     * @param context
     * @return
     */
    public static Intent getShareIntent(Context context, String subject,
            String content, String title) {
        // 启动分享发送的属性
        Intent intent = new Intent(Intent.ACTION_SEND);
        // 分享发送的数据类型
        intent.setType("text/plain");
        // 分享的主题
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        // 分享的内容
        intent.putExtra(Intent.EXTRA_TEXT, content);
        // 目标应用选择对话框的标题
        return Intent.createChooser(intent, title);
    }

}
