package com.webmyne.android.d_brain.ui.Helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;

/**
 * Created by Krishna on 16-09-2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager alarmNotificationManager;
    NotificationCompat.Builder alamNotificationBuilder;
    Context _ctx;
    boolean isVibrate;
    @Override
    public void onReceive(Context ctx, Intent arg1) {

        _ctx = ctx;
        //Toast.makeText(ctx, "Alarm received!", Toast.LENGTH_LONG).show();
        String msg = "Alram recived";

        sendNotification(msg);
    }


    private void sendNotification(String msg) {

        alarmNotificationManager = (NotificationManager)_ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(_ctx, 0, new Intent(_ctx, HomeDrawerActivity.class), 0);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            alamNotificationBuilder = new NotificationCompat.Builder(
                    _ctx).setContentTitle("JETLAG FREE").setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setSound(alarmSound)
                    .setContentText(msg);

        alamNotificationBuilder.setAutoCancel(true);
        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }

}