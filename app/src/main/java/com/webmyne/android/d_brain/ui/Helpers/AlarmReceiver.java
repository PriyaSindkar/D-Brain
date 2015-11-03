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
import java.sql.SQLException;
import java.util.Random;

/**
 * Created by Krishna on 16-09-2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager alarmNotificationManager;
    NotificationCompat.Builder alamNotificationBuilder;
    Context _ctx;
    boolean isVibrate;
    String schedulerId, notificationSchedulerName = "";
    SchedulerModel schedulerModel;
    Cursor schedulerCursor;

    @Override
    public void onReceive(Context ctx, Intent arg1) {
        _ctx = ctx;

        schedulerId = arg1.getStringExtra("scheduler_id");
        Log.e("onReceive", "onReceive" + schedulerId);
       // notificationSchedulerName = arg1.getStringExtra("scheduler_name");

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

        Random random = new Random();
        int randomNo = random.nextInt(9999 - 1000) + 1000;

        alarmNotificationManager.notify(randomNo, alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }

    private void fireScheduler() {
       // if(schedulerId != null) {
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(_ctx);
                dbHelper.openDataBase();
                schedulerModel = dbHelper.getSchedulerById(schedulerId);
               // schedulerCursor = dbHelper.getAllSchedulers();
                dbHelper.close();

                if(schedulerModel != null) {
                    if (schedulerModel.getComponentType().equals(AppConstants.SWITCH_TYPE)) {
                        String name = schedulerModel.getComponentId();
                        String mip = schedulerModel.getMip();
                        String mid = schedulerModel.getMid();
                        String baseMachineUrl = "";
                        if (mip.startsWith("http://")) {
                            baseMachineUrl = mip;
                        } else {
                            baseMachineUrl = "http://" + mip;
                        }
                        boolean isMachineActive =  dbHelper.isMachineActive(mip);

                        if(isMachineActive) {
                            String URL = baseMachineUrl + AppConstants.URL_CHANGE_SWITCH_STATUS + name.substring(2, 4) + schedulerModel.getDefaultValue();

                            // call url
                            notificationSchedulerName = schedulerModel.getSchedulerName();
                            String[] params = new String[2];
                            params[0] = URL;
                            params[1] = mip;
                            new ChangeSwitchStatus().execute(params);
                        }

                    } else if (schedulerModel.getComponentType().equals(AppConstants.DIMMER_TYPE)) {
                        String name = schedulerModel.getComponentId();
                        String mip = schedulerModel.getMip();
                        String baseMachineUrl = "";
                        if (mip.startsWith("http://")) {
                            baseMachineUrl = mip;
                        } else {
                            baseMachineUrl = "http://" + mip;
                        }

                        boolean isMachineActive =  dbHelper.isMachineActive(mip);

                        if(isMachineActive) {
                            String URL = baseMachineUrl + AppConstants.URL_CHANGE_DIMMER_STATUS + name.substring(2, 4) + schedulerModel.getDefaultValue();

                            // call url
                            notificationSchedulerName = schedulerModel.getSchedulerName();
                            String[] params = new String[2];
                            params[0] = URL;
                            params[1] = mip;
                            new ChangeDimmerStatus().execute(params);
                        }

                    } else if (schedulerModel.getComponentType().equals(AppConstants.SCENE_TYPE)) {
                        try {
                            notificationSchedulerName = schedulerModel.getSchedulerName();
                            dbHelper.openDataBase();
                            Cursor sceneCursor = dbHelper.getAllComponentsInAScene(schedulerModel.getComponentPrimaryId());

                            if (sceneCursor != null) {
                                if (sceneCursor.getCount() > 0) {
                                    sceneCursor.moveToFirst();
                                    do {
                                        String URL = "", baseMachineUrl = "";
                                        String mip = sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_MIP));
                                        String sceneComponentId = sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_COMPONENT_ID));
                                        String defaultValue = sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_DEFAULT));
                                        String cComponentType = sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_SC_TYPE));
                                        String sceneIsActive = sceneCursor.getString(sceneCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));

                                        if (mip.startsWith("http://")) {
                                            baseMachineUrl = mip;
                                        } else {
                                            baseMachineUrl = "http://" + mip;
                                        }

                                        if(sceneIsActive.equals("true")) {
                                            if (cComponentType.equals(AppConstants.SWITCH_TYPE)) {
                                                URL = baseMachineUrl + AppConstants.URL_CHANGE_SWITCH_STATUS + sceneComponentId.substring(2, 4) + schedulerModel.getDefaultValue();
                                                String[] params = new String[2];
                                                params[0] = URL;
                                                params[1] = mip;
                                                new ChangeSwitchStatus().execute(params);

                                            } else if (cComponentType.equals(AppConstants.DIMMER_TYPE)) {
                                                String strProgress = "";
                                                Log.e("def", defaultValue);
                                                if (defaultValue.equals("00")) {
                                                    strProgress = String.format("%02d", Integer.parseInt(defaultValue));
                                                    URL = baseMachineUrl + AppConstants.URL_CHANGE_DIMMER_STATUS + sceneComponentId.substring(2, 4) + schedulerModel.getDefaultValue() + strProgress;
                                                } else {
                                                    strProgress = String.format("%02d", Integer.parseInt(defaultValue) - 1);
                                                    URL = baseMachineUrl + AppConstants.URL_CHANGE_DIMMER_STATUS + sceneComponentId.substring(2, 4) + schedulerModel.getDefaultValue() + strProgress;
                                                }
                                                String[] params = new String[2];
                                                params[0] = URL;
                                                params[1] = mip;
                                                new ChangeDimmerStatus().execute(params);
                                            }
                                        }

                                    } while (sceneCursor.moveToNext());
                                }
                            }
                            dbHelper.close();

                            String msg = notificationSchedulerName + " Set";
                            sendNotification(msg);

                        } catch (Exception e) {

                        }
                    }
                }
            } catch (Exception e) {

            }
        }

    public class ChangeSwitchStatus extends AsyncTask<String, Void, Void> {
        boolean isError = false, isMachineActive = false;
        String machineId="", machineName = "", machineIp;
        Cursor  machineCursor;
        @Override
        protected Void doInBackground(String... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(_ctx);
            try {
                URL urlValue = new URL(params[0]);
                machineIp = params[1];
                Log.e("# urlValue", urlValue.toString());

                isMachineActive =  dbHelper.isMachineActive(machineIp);
                machineCursor = dbHelper.getMachineByIP(machineIp);
                machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();


            } catch (Exception e) {
                Log.e("# EXP", e.toString());
                isError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try{
                DatabaseHelper dbHelper = new DatabaseHelper(_ctx);
                if(isError) {
                    try {
                        dbHelper.openDataBase();
                        dbHelper.enableDisableMachine(machineId, false);
                        dbHelper.close();
                        Toast.makeText(_ctx, "Machine, " + machineName + " was deactivated.", Toast.LENGTH_LONG).show();
                    } catch (SQLException e) {
                        Log.e("TAG EXP", e.toString());
                    }
                } else {
                    String msg = notificationSchedulerName + " Set";
                    if (schedulerModel.getComponentType().equals(AppConstants.SWITCH_TYPE))
                        sendNotification(msg);
                }

            }catch(Exception e){
            }
        }
    }

    public class ChangeDimmerStatus extends AsyncTask<String, Void, Void> {
        boolean isError = false, isMachineActive = false;
        String machineId="", machineName = "", machineIp;
        Cursor  machineCursor;

        @Override
        protected Void doInBackground(String... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(_ctx);
            try {
                URL urlValue = new URL(params[0]);
                machineIp = params[1];
                Log.e("# urlValue", urlValue.toString());

                isMachineActive =  dbHelper.isMachineActive(machineIp);
                machineCursor = dbHelper.getMachineByIP(machineIp);
                machineId = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
                machineName = machineCursor.getString(machineCursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));

                HttpURLConnection httpUrlConnection = (HttpURLConnection) urlValue.openConnection();
                httpUrlConnection.setConnectTimeout(AppConstants.TIMEOUT);
                httpUrlConnection.setRequestMethod("GET");
                InputStream inputStream = httpUrlConnection.getInputStream();


            } catch (Exception e) {
                Log.e("# EXP", e.toString());
                isError = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(_ctx);
                if(isError) {
                    try {
                        dbHelper.openDataBase();
                        dbHelper.enableDisableMachine(machineId, false);
                        dbHelper.close();
                        Toast.makeText(_ctx, "Machine, " + machineName + " was deactivated.", Toast.LENGTH_LONG).show();
                    } catch (SQLException e) {
                        Log.e("TAG EXP", e.toString());
                    }
                } else {
                    String msg = notificationSchedulerName + " Set";
                    if(schedulerModel.getComponentType().equals(AppConstants.SWITCH_TYPE))
                        sendNotification(msg);
                }

            }catch (Exception e) {}
        }
    }

}