package com.webmyne.android.d_brain.ui.Helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Model.SchedulerModel;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Krishna on 16-09-2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager alarmNotificationManager;
    NotificationCompat.Builder alamNotificationBuilder;
    Context _ctx;
    boolean isVibrate;
    String schedulerId;
    SchedulerModel schedulerModel;

    @Override
    public void onReceive(Context ctx, Intent arg1) {

        _ctx = ctx;
        //Toast.makeText(ctx, "Alarm received!", Toast.LENGTH_LONG).show();
        String msg = "Alarm received";

        schedulerId = arg1.getStringExtra("scheduler_id");

        sendNotification(msg);
        fireScheduler();
    }


    private void sendNotification(String msg) {

        alarmNotificationManager = (NotificationManager)_ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(_ctx, 0, new Intent(_ctx, HomeDrawerActivity.class), 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        alamNotificationBuilder = new NotificationCompat.Builder(
                _ctx).setContentTitle("Scheduler").setSmallIcon(R.drawable.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000 })
                .setSound(alarmSound)
                .setContentText(msg);

        alamNotificationBuilder.setAutoCancel(true);
        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }

    private void fireScheduler() {
        if(schedulerId != null) {
            Log.e("TAG_SCHEDULER", "Scheduler");
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(_ctx);
                dbHelper.openDataBase();
                schedulerModel = dbHelper.getSchedulerById(schedulerId);
                dbHelper.close();

            } catch (Exception e) {

            }

            if(schedulerModel.getComponentType().equals(AppConstants.SWITCH_TYPE)) {
                String name = schedulerModel.getComponentName();
                String URL = schedulerModel.getMip() + AppConstants.URL_CHANGE_SWITCH_STATUS + name.substring(2,4) + schedulerModel.getDefaultValue();
                Log.e("TAG_URL", URL);
            }

        } else {
            Log.e("TAG_SCHEDULER", "Scheduler null");
        }

    }


    public class ChangeSwitchStatus extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL urlValue = new URL(params[0]);
                Log.e("# urlValue", urlValue.toString());

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();


            } catch (Exception e) {
                Log.e("# EXP", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{

            }catch(Exception e){
            }
        }
    }

}