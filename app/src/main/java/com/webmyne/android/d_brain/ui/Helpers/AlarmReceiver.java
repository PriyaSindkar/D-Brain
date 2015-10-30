package com.webmyne.android.d_brain.ui.Helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.SchedulersListActivity;
import com.webmyne.android.d_brain.ui.Model.SchedulerModel;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
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
    String schedulerId, schedulerName;
    SchedulerModel schedulerModel;

    @Override
    public void onReceive(Context ctx, Intent arg1) {

        _ctx = ctx;


        schedulerId = arg1.getStringExtra("scheduler_id");
        schedulerName = arg1.getStringExtra("scheduler_name");
        String msg = schedulerName + " Set";
        sendNotification(msg);
        fireScheduler();
    }


    private void sendNotification(String msg) {

        alarmNotificationManager = (NotificationManager)_ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent newIntent = new Intent(_ctx, SchedulersListActivity.class);
       // newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(_ctx, 0, newIntent, 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        alamNotificationBuilder = new NotificationCompat.Builder(
                _ctx).setContentTitle("Scheduler").setSmallIcon(R.drawable.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000 })
                .setSound(alarmSound)
                .setContentText(msg);

        alamNotificationBuilder.setAutoCancel(true);
        alamNotificationBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(0, alamNotificationBuilder.build());
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

            Log.e("TAG_SCH", schedulerModel.toString());

            if(schedulerModel != null) {
                if (schedulerModel.getComponentType().equals(AppConstants.SWITCH_TYPE)) {
                    String name = schedulerModel.getComponentId();
                    String mip = schedulerModel.getMip();
                    String baseMachineUrl = "";
                    if (mip.startsWith("http://")) {
                        baseMachineUrl = mip;
                    } else {
                        baseMachineUrl = "http://" + mip;
                    }
                    String URL = baseMachineUrl + AppConstants.URL_CHANGE_SWITCH_STATUS + name.substring(2, 4) + schedulerModel.getDefaultValue();

                    // call url
                    new ChangeSwitchStatus().execute(URL);

                } else if (schedulerModel.getComponentType().equals(AppConstants.DIMMER_TYPE)) {
                    String name = schedulerModel.getComponentId();
                    String mip = schedulerModel.getMip();
                    String baseMachineUrl = "";
                    if (mip.startsWith("http://")) {
                        baseMachineUrl = mip;
                    } else {
                        baseMachineUrl = "http://" + mip;
                    }

                    String URL = baseMachineUrl + AppConstants.URL_CHANGE_DIMMER_STATUS + name.substring(2, 4) + schedulerModel.getDefaultValue();

                    // call url
                    new ChangeDimmerStatus().execute(URL);

                } else if (schedulerModel.getComponentType().equals(AppConstants.SCENE_TYPE)) {

                    try {
                        DatabaseHelper dbHelper = new DatabaseHelper(_ctx);
                        dbHelper.openDataBase();
                        Cursor sceneCursor = dbHelper.getAllComponentsInAScene(schedulerModel.getComponentPrimaryId());

                        if (sceneCursor != null) {
                            if (sceneCursor.getCount() > 0) {
                                sceneCursor.moveToFirst();
                                do {
                                    String URL = "", baseMachineUrl = "";
                                    String mip = sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_MIP));
                                    String componentId = sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_COMPONENT_ID));
                                    String defaultValue = sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_DEFAULT));
                                    String componentType = sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_TYPE));

                                    if (mip.startsWith("http://")) {
                                        baseMachineUrl = mip;
                                    } else {
                                        baseMachineUrl = "http://" + mip;
                                    }

                                    if (componentType.equals(AppConstants.SWITCH_TYPE)) {
                                        URL = baseMachineUrl + AppConstants.URL_CHANGE_SWITCH_STATUS + componentId.substring(2, 4) + schedulerModel.getDefaultValue();
                                        new ChangeSwitchStatus().execute(URL);

                                    } else if (componentType.equals(AppConstants.DIMMER_TYPE)) {
                                        String strProgress = "";
                                        Log.e("def", defaultValue);
                                        if (defaultValue.equals("00")) {
                                            strProgress = String.format("%02d", Integer.parseInt(defaultValue));
                                            URL = baseMachineUrl + AppConstants.URL_CHANGE_DIMMER_STATUS + componentId.substring(2, 4) + schedulerModel.getDefaultValue() + strProgress;
                                        } else {
                                            strProgress = String.format("%02d", Integer.parseInt(defaultValue) - 1);
                                            URL = baseMachineUrl + AppConstants.URL_CHANGE_DIMMER_STATUS + componentId.substring(2, 4) + schedulerModel.getDefaultValue() + strProgress;
                                        }
                                        new ChangeDimmerStatus().execute(URL);
                                    }

                                } while (sceneCursor.moveToNext());
                            }
                        }
                        dbHelper.close();

                    } catch (Exception e) {

                    }
                }
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

    public class ChangeDimmerStatus extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL urlValue = new URL(params[0]);
                Log.e("# url change dimmer", urlValue.toString());

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
        }
    }

}