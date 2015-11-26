package com.webmyne.android.d_brain.ui.dbHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.webmyne.android.d_brain.ui.Adapters.CreateSceneAdapter;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;
import com.webmyne.android.d_brain.ui.Model.SchedulerModel;
import com.webmyne.android.d_brain.ui.Model.TouchPanelModel;
import com.webmyne.android.d_brain.ui.Model.TouchPanelSwitchModel;
import com.webmyne.android.d_brain.ui.xmlHelpers.XMLValues;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by priyasindkar on 18-09-2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    private DatabaseHelper myDataBase = null;
    private SQLiteDatabase mDb;

    public DatabaseHelper(Context context) {
        super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
        this.mContext = context;
    }


    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
        if (dbExist) {
            Log.e("log_tag", "database does exist");
        } else {
            Log.e("log_tag", "database does not exist");
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
        }
    }

    private void copyDataBase() throws IOException {

        InputStream myInput = mContext.getAssets().open(DBConstants.DATABASE_NAME);
        String outFileName = DBConstants.DATABASE_PATH + DBConstants.DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;

        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private boolean checkDataBase() {

        File folder = new File(DBConstants.DATABASE_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File dbFile = new File(DBConstants.DATABASE_PATH + DBConstants.DATABASE_NAME);
        return dbFile.exists();
    }


    public boolean openDataBase() throws SQLException {
        String mPath = DBConstants.DATABASE_PATH + DBConstants.DATABASE_NAME;
        //Log.e("mPath", mPath);
        mDb = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        mDb.execSQL("PRAGMA foreign_keys=ON");
        return myDataBase != null;

    }


    public DatabaseHelper open() throws SQLException {
        myDataBase = new DatabaseHelper(mContext);
        mDb = myDataBase.getWritableDatabase();
        return this;
    }


    @Override
    public synchronized void close() {
        if (mDb != null)
            mDb.close();
        super.close();
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // db.execSQL(DBConstants.CREATE_COMPONENT_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.CREATE_COMPONENT_TABLE);

        // Create tables again
        onCreate(db);*/
    }

    public long insertIntoMachine(ArrayList<XMLValues> machineValuesList, String machineName, String machineIP) {

        Log.e("TAG_DB", "Insert machine");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        for (int i = 0; i < machineValuesList.size(); i++) {
            if (machineValuesList.get(i).tagName.equals(DBConstants.KEY_M_DA)) {
                values.put(DBConstants.KEY_M_DA, machineValuesList.get(i).tagValue);
            } else if (machineValuesList.get(i).tagName.equals(DBConstants.KEY_M_SERIALNO)) {
                values.put(DBConstants.KEY_M_SERIALNO, machineValuesList.get(i).tagValue);
            } else if (machineValuesList.get(i).tagName.equals(DBConstants.KEY_M_VERSION)) {
                values.put(DBConstants.KEY_M_VERSION, machineValuesList.get(i).tagValue);
            } else if (machineValuesList.get(i).tagName.equals(DBConstants.KEY_M_PRODUCTCODE)) {
                values.put(DBConstants.KEY_M_PRODUCTCODE, machineValuesList.get(i).tagValue);
            } else if (machineValuesList.get(i).tagName.equals(DBConstants.KEY_M_DATETIME)) {
                values.put(DBConstants.KEY_M_DATETIME, machineValuesList.get(i).tagValue);
            }
            values.put(DBConstants.KEY_M_NAME, machineName);
            values.put(DBConstants.KEY_M_IP, machineIP);
            values.put(DBConstants.KEY_M_ISACTIVE, "true");
        }

        Log.e("Machine_CV", values.toString());

        long id = db.insert(DBConstants.TABLE_MACHINE, null, values);
        db.close();

        return id;
    }

    public Cursor getAllMachines() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_MACHINE, null, null, null, null, null, null, null);
            /*if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                        Log.e("MACHINEIP", cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP)));
                    } while (cursor.moveToNext());
                }
            }*/
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    // save new machine name in tables: machine, SceneComponent, Component, FavouriteComponent, Schedulers
    public void renameMachine(String machineId, String newMachineName, String newMachineIP) {
        SQLiteDatabase db = this.getWritableDatabase();

        // save old IP before updating
        String oldMachineIP = getMachineIPByID(machineId);

        //update machine
        ContentValues values = new ContentValues();
        values.put(DBConstants.KEY_M_NAME, newMachineName);
        values.put(DBConstants.KEY_M_IP, newMachineIP);
        db.update(DBConstants.TABLE_MACHINE, values, DBConstants.KEY_M_ID + "='" + machineId + "'", null);

        //update component
        values = new ContentValues();
        values.put(DBConstants.KEY_C_MNAME, newMachineName);
        values.put(DBConstants.KEY_C_MIP, newMachineIP);
        db.update(DBConstants.TABLE_COMPONENT, values, DBConstants.KEY_C_MID + "='" + machineId + "'", null);

        //update scene-component
        values = new ContentValues();
        values.put(DBConstants.KEY_SC_MNAME, newMachineName);
        values.put(DBConstants.KEY_SC_MIP, newMachineIP);
        db.update(DBConstants.TABLE_SCENE_COMPONENT, values, DBConstants.KEY_SC_MID + "='" + machineId + "'", null);

        //update FavouriteComponent
        values = new ContentValues();
        values.put(DBConstants.KEY_F_MNAME, newMachineName);
        values.put(DBConstants.KEY_C_MIP, newMachineIP);
        db.update(DBConstants.TABLE_FAVOURITE, values, DBConstants.KEY_F_MID + "='" + machineId + "'", null);

        //update Schedulers
        values = new ContentValues();
        values.put(DBConstants.KEY_SCH_MNAME, newMachineName);
        values.put(DBConstants.KEY_SCH_MIP, newMachineIP);
        db.update(DBConstants.TABLE_SCHEDULERS, values, DBConstants.KEY_SCH_MID + "='" + machineId + "'", null);

        //update TouchPanel
        values = new ContentValues();
        values.put(DBConstants.KEY_SCH_MNAME, newMachineName);
        values.put(DBConstants.KEY_SCH_MIP, newMachineIP);
        db.update(DBConstants.TABLE_TOUCH_PANEL, values, DBConstants.KEY_TP_MID + "='" + machineId + "'", null);
        db.close();
    }


    // delete machine. Also delete all entries of this machine in tables: machine,
    // SceneComponent, Component, FavouriteComponent, Schedulers, TouchPanel, TouchPanelSwitch
    public void deleteMachine(String machineId) {
        SQLiteDatabase db = this.getWritableDatabase();

        //update machine
        db.delete(DBConstants.TABLE_MACHINE, DBConstants.KEY_M_ID + "='" + machineId + "'", null);

        //update component
        db.delete(DBConstants.TABLE_COMPONENT, DBConstants.KEY_C_MID + "='" + machineId + "'", null);

        //update scene-component
        db.delete(DBConstants.TABLE_SCENE_COMPONENT, DBConstants.KEY_SC_MID + "='" + machineId + "'", null);

        //update FavouriteComponent
        db.delete(DBConstants.TABLE_FAVOURITE, DBConstants.KEY_F_MID + "='" + machineId + "'", null);

        //update Schedulers
        db.delete(DBConstants.TABLE_SCHEDULERS, DBConstants.KEY_SCH_MID + "='" + machineId + "'", null);

        //update Touch Panel
        db.delete(DBConstants.TABLE_TOUCH_PANEL, DBConstants.KEY_TP_MID + "='" + machineId + "'", null);

        //update Touch Panel Switch Item
        db.delete(DBConstants.TABLE_TOUCH_PANEL_SWITCH, DBConstants.KEY_TP_SWITCH_MID + "='" + machineId + "'", null);
    }


    // change is_active status of machine in tables: machine, SceneComponent, Component, FavouriteComponent, Schedulers
    public void enableDisableMachine(String machineId, boolean isEnabled) {
        SQLiteDatabase db = this.getWritableDatabase();

        //update machine
        ContentValues values = new ContentValues();
        if (isEnabled) {
            values.put(DBConstants.KEY_M_ISACTIVE, "true");
        } else {
            values.put(DBConstants.KEY_M_ISACTIVE, "false");
        }

        // machine
        db.update(DBConstants.TABLE_MACHINE, values, DBConstants.KEY_M_ID + "='" + machineId + "'", null);

        //update component
        db.update(DBConstants.TABLE_COMPONENT, values, DBConstants.KEY_C_MID + "='" + machineId + "'", null);

        //update scene-component
        db.update(DBConstants.TABLE_SCENE_COMPONENT, values, DBConstants.KEY_SC_MID + "='" + machineId + "'", null);

        //update FavouriteComponent
        db.update(DBConstants.TABLE_FAVOURITE, values, DBConstants.KEY_F_MID + "='" + machineId + "'", null);

        //update Schedulers
        db.update(DBConstants.TABLE_SCHEDULERS, values, DBConstants.KEY_SCH_MID + "='" + machineId + "'", null);

        db.close();
    }

    public String getMachineNameByIP(String machineIP) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String machineName = "";
        try {
            cursor = db.query(DBConstants.TABLE_MACHINE, null, DBConstants.KEY_M_IP + "=?",
                    new String[]{machineIP}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                machineName = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_M_NAME));
            }

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return machineName;
    }

    public Cursor getMachineByIP(String IP) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_MACHINE, null, DBConstants.KEY_M_IP + "=?",
                    new String[]{IP}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                // machineIP =  cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
            }

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    public Cursor getMachineByID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_MACHINE, null, DBConstants.KEY_M_ID + "=?",
                    new String[]{id}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                // machineIP =  cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_M_ID));
            }

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }


    public String getMachineIPByID(String machineID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String machineIP = "";
        try {
            cursor = db.query(DBConstants.TABLE_MACHINE, null, DBConstants.KEY_M_ID + "=?",
                    new String[]{machineID}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                machineIP = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_M_IP));
            }

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return machineIP;
    }

    // check IP other than the current machine
    public boolean isMachineIPExists(String machineIP, String oldMachineIP) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = true;
        try {
            cursor = db.query(DBConstants.TABLE_MACHINE, null, DBConstants.KEY_M_IP + "=? AND " + DBConstants.KEY_M_IP + "!=?",
                    new String[]{machineIP, oldMachineIP}, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    result = false;
                } else {
                    result = true;
                }
            } else {
                result = false;
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return result;
    }

    // check IP throughout machine table
    public boolean isMachineIPAlreadyExists(String machineIP) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = true;
        try {
            cursor = db.query(DBConstants.TABLE_MACHINE, null, DBConstants.KEY_M_IP + "=?",
                    new String[]{machineIP}, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    result = false;
                } else {
                    result = true;
                }
            } else {
                result = false;
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return result;
    }


    public boolean isMachineSerialNoExists(String machineSerialNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = true;
        try {
            cursor = db.query(DBConstants.TABLE_MACHINE, null, DBConstants.KEY_M_SERIALNO + "=?",
                    new String[]{machineSerialNo}, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    result = false;
                } else {
                    result = true;
                }
            } else {
                result = false;
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return result;
    }

    public boolean isMachineActive(String machineIP) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String isActive = "";
        boolean isMachineActive = false;
        try {
            cursor = db.query(DBConstants.TABLE_MACHINE, null, DBConstants.KEY_M_IP + "=?",
                    new String[]{machineIP}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                isActive = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_M_ISACTIVE));
            }

            if (isActive.equals("true")) {
                isMachineActive = true;
            } else {
                isMachineActive = false;
            }

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return isMachineActive;
    }


    public void insertIntoComponent(ArrayList<ComponentModel> componentModels) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        for (int i = 0; i < componentModels.size(); i++) {
            values.put(DBConstants.KEY_C_COMPONENT_ID, componentModels.get(i).getComponentId());
            values.put(DBConstants.KEY_C_NAME, componentModels.get(i).getName());
            values.put(DBConstants.KEY_C_TYPE, componentModels.get(i).getType());
            values.put(DBConstants.KEY_C_DETAILS, componentModels.get(i).getDetails());
            values.put(DBConstants.KEY_C_MID, componentModels.get(i).getMid());
            values.put(DBConstants.KEY_C_MIP, componentModels.get(i).getMip());
            values.put(DBConstants.KEY_C_MNAME, componentModels.get(i).getMachineName());
            values.put(DBConstants.KEY_M_ISACTIVE, "true");

            db.insert(DBConstants.TABLE_COMPONENT, null, values);
        }

        db.close();
    }

    public void renameComponent(String componentId, String componentName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // save new component name
        ContentValues values = new ContentValues();
        values.put(DBConstants.KEY_C_NAME, componentName);
        db.update(DBConstants.TABLE_COMPONENT, values, DBConstants.KEY_C_ID + "='" + componentId + "'", null);
        db.close();
    }

    public void renameComponent(String componentId, String componentName, String componentDetails) {
        SQLiteDatabase db = this.getWritableDatabase();

        // save new component name
        ContentValues values = new ContentValues();
        values.put(DBConstants.KEY_C_NAME, componentName);
        values.put(DBConstants.KEY_C_DETAILS, componentDetails);
        db.update(DBConstants.TABLE_COMPONENT, values, DBConstants.KEY_C_ID + "='" + componentId + "'", null);
        db.close();
    }

    public Cursor getAllSwitchComponentsForAMachine(String machineIP) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_MIP + "=? AND " + DBConstants.KEY_C_TYPE + "=?",
                    new String[]{machineIP, AppConstants.SWITCH_TYPE}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    //get switches of a machine by machine-id
    public Cursor getAllSwitchComponentsForAMachineById(String machineId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_MID + "=? AND " + DBConstants.KEY_C_TYPE + "=?",
                    new String[]{machineId, AppConstants.SWITCH_TYPE}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    //get dimmers of a machine by machine-id
    public Cursor getAllDimmerComponentsForAMachineById(String machineId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_MID + "=? AND " + DBConstants.KEY_C_TYPE + "=?",
                    new String[]{machineId, AppConstants.DIMMER_TYPE}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    //get all switches from all machines
    public Cursor getAllSwitchComponents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_TYPE + "=?",
                    new String[]{AppConstants.SWITCH_TYPE}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }


    public Cursor getAllDimmerComponentsForAMachine(String machineIP) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_MIP + "=? AND " + DBConstants.KEY_C_TYPE + "=?",
                    new String[]{machineIP, AppConstants.DIMMER_TYPE}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    //get all switches from all dimmers
    public Cursor getAllDimmerComponents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_TYPE + "=?",
                    new String[]{AppConstants.DIMMER_TYPE}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    public Cursor getAllMotorComponentsForAMachine(String machineIP) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_MIP + "=? AND " + DBConstants.KEY_C_TYPE + "=?",
                    new String[]{machineIP, AppConstants.MOTOR_TYPE}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    //get all switches from all motors
    public Cursor getAllMotorComponents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_TYPE + "=?",
                    new String[]{AppConstants.MOTOR_TYPE}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }


    public Cursor getAllSensorComponentsForAMachine(String machineIP) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_MIP + "=? AND " + DBConstants.KEY_C_TYPE + "=?",
                    new String[]{machineIP, AppConstants.ALERT_TYPE}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    //get all switches from all sensors
    public Cursor getAllSensorsComponents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_TYPE + "=?",
                    new String[]{AppConstants.ALERT_TYPE}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }


    public void createNewScene(String sceneName, ArrayList<SceneItemsDataObject> componentModels) {
        SQLiteDatabase db = this.getWritableDatabase();

        // create new scene
        ContentValues values = new ContentValues();
        values.put(DBConstants.KEY_S_NAME, sceneName);
        values.put(DBConstants.KEY_S_STATUS, "no");
        long sceneId = db.insert(DBConstants.TABLE_SCENE, null, values);

        // create scene components
        values = new ContentValues();

        for (int i = 0; i < componentModels.size(); i++) {
            values.put(DBConstants.KEY_SC_SCENE_ID, String.valueOf(sceneId));
            values.put(DBConstants.KEY_SC_COMPONENT_ID, componentModels.get(i).getSceneItemId());
            values.put(DBConstants.KEY_SC_COMP_PRIMARY_ID, componentModels.get(i).getSceneComponentPrimaryId());
            values.put(DBConstants.KEY_SC_TYPE, componentModels.get(i).getSceneControlType());
            values.put(DBConstants.KEY_SC_MIP, componentModels.get(i).getMachineIP());
            values.put(DBConstants.KEY_SC_MID, componentModels.get(i).getMachineId());
            values.put(DBConstants.KEY_SC_MNAME, componentModels.get(i).getMachineName());
            values.put(DBConstants.KEY_SC_DEFAULT, componentModels.get(i).getDefaultValue());
            values.put(DBConstants.KEY_M_ISACTIVE, "true");

            db.insert(DBConstants.TABLE_SCENE_COMPONENT, null, values);
        }

        db.close();
    }

    // to update scene  on/off statusvalues
    public void updateSceneStatus(String sceneId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        // update scene components
        ContentValues values = new ContentValues();
        values.put(DBConstants.KEY_S_STATUS, status);

        db.update(DBConstants.TABLE_SCENE, values, DBConstants.KEY_S_ID + " = '" + sceneId + "'", null);

        db.close();
    }

    // to delete  scene
    public void deleteScene(String sceneId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DBConstants.TABLE_SCENE, DBConstants.KEY_S_ID + "='" + sceneId + "'", null);
        deleteSceneComponentsBySceneId(sceneId);
        db.close();
    }


    // to get scene
    public Cursor getScenebyId(String sceneId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_SCENE, null, DBConstants.KEY_S_ID + "=?",
                    new String[]{sceneId}, null, null, null, null);

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    public String getSceneStatus(String sceneId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String sceneStatus = "no";
        try {
            // TO:DO search machine-wise

           /* cursor = db.query(DBConstants.TABLE_SCENE, null, DBConstants.KEY_C_MIP + "=?",
                    new String[]{URL_MACHINE_IP}, null, null, null, null);*/

            cursor = db.query(DBConstants.TABLE_SCENE, null, DBConstants.KEY_S_ID + "=?",
                    new String[]{sceneId}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    sceneStatus = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_S_STATUS));
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return sceneStatus;
    }


    public Cursor getAllScenes(String machineIP) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            // TO:DO search machine-wise

           /* cursor = db.query(DBConstants.TABLE_SCENE, null, DBConstants.KEY_C_MIP + "=?",
                    new String[]{machineIP}, null, null, null, null);*/

            cursor = db.query(DBConstants.TABLE_SCENE, null, null,
                    null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    // to save newly added components
    public void addNewSceneComponents(String sceneId, ArrayList<SceneItemsDataObject> componentModels) {
        SQLiteDatabase db = this.getWritableDatabase();

       /* // create new scene
        ContentValues values = new ContentValues();
        values.put(DBConstants.KEY_S_NAME, sceneName);
        long sceneId = db.insert(DBConstants.TABLE_SCENE, null, values);*/

        // save new scene components
        ContentValues values = new ContentValues();

        for (int i = 0; i < componentModels.size(); i++) {

            values.put(DBConstants.KEY_SC_SCENE_ID, sceneId);
            values.put(DBConstants.KEY_SC_COMPONENT_ID, componentModels.get(i).getSceneItemId());
            values.put(DBConstants.KEY_SC_COMP_PRIMARY_ID, componentModels.get(i).getSceneComponentPrimaryId());
            values.put(DBConstants.KEY_SC_TYPE, componentModels.get(i).getSceneControlType());
            values.put(DBConstants.KEY_SC_MID, componentModels.get(i).getMachineId());
            values.put(DBConstants.KEY_SC_MIP, componentModels.get(i).getMachineIP());
            values.put(DBConstants.KEY_SC_MNAME, componentModels.get(i).getMachineName());
            values.put(DBConstants.KEY_SC_DEFAULT, componentModels.get(i).getDefaultValue());
            values.put(DBConstants.KEY_M_ISACTIVE, componentModels.get(i).getIsActive());

            if (!isComponentAlreadyExists(componentModels.get(i).getSceneComponentPrimaryId(), sceneId))
                db.insert(DBConstants.TABLE_SCENE_COMPONENT, null, values);
            else {
                db.update(DBConstants.TABLE_SCENE_COMPONENT, values, DBConstants.KEY_SC_COMP_PRIMARY_ID + " = '" + componentModels.get(i).getSceneComponentPrimaryId() + "'", null);
            }
        }

        db.close();
    }

    // to update scene component default values
    public void updateSceneComponents(ArrayList<SceneItemsDataObject> componentModels) {
        SQLiteDatabase db = this.getWritableDatabase();

        // update scene components
        ContentValues values = new ContentValues();

        for (int i = 0; i < componentModels.size(); i++) {

            /*if(componentModels.get(i).getSceneControlType().equals(AppConstants.DIMMER_TYPE)) {
                String strDefault = "00";
                if(!componentModels.get(i).getDefaultValue().equals(AppConstants.OFF_VALUE)) {
                    if(componentModels.get(i).getDefaultValue().equals("100") ){
                        strDefault = "100";
                    } else {
                        strDefault = String.format("%02d", (Integer.parseInt(componentModels.get(i).getDefaultValue())));
                    }
                }
                values.put(DBConstants.KEY_SC_DEFAULT, strDefault);*/
            //  } else {
            values.put(DBConstants.KEY_SC_DEFAULT, componentModels.get(i).getDefaultValue());
            //  }

            db.update(DBConstants.TABLE_SCENE_COMPONENT, values, DBConstants.KEY_SC_COMP_PRIMARY_ID + " = '" + componentModels.get(i).getSceneComponentPrimaryId() + "'", null);
        }

        db.close();
    }

    // to delete scene components
    public void deleteSceneComponents(ArrayList<SceneItemsDataObject> componentModels) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < componentModels.size(); i++) {
            db.delete(DBConstants.TABLE_SCENE_COMPONENT, DBConstants.KEY_SC_COMP_PRIMARY_ID + "='" + componentModels.get(i).getSceneComponentPrimaryId() + "'", null);
        }

        db.close();
    }

    // to delete scene components
    public void deleteSceneComponentsBySceneId(String sceneId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(DBConstants.TABLE_SCENE_COMPONENT, DBConstants.KEY_SC_SCENE_ID + "='" + sceneId + "'", null);

        db.close();
    }

    public boolean isComponentAlreadyExists(String componentId, String sceneId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = true;
        try {
            // TO:DO search machine-wise

           /* cursor = db.query(DBConstants.TABLE_SCENE, null, DBConstants.KEY_C_MIP + "=?",
                    new String[]{URL_MACHINE_IP}, null, null, null, null);*/

            cursor = db.query(DBConstants.TABLE_SCENE_COMPONENT, null, DBConstants.KEY_SC_SCENE_ID + "=? AND " + DBConstants.KEY_SC_COMP_PRIMARY_ID + "=?",
                    new String[]{sceneId, componentId}, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    result = false;
                } else {
                    result = true;
                }
            } else {
                result = false;
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return result;
    }


    public void renameScene(String sceneId, String sceneName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // save new scene name
        ContentValues values = new ContentValues();
        values.put(DBConstants.KEY_S_NAME, sceneName);
        db.update(DBConstants.TABLE_SCENE, values, DBConstants.KEY_S_ID + "=" + sceneId, null);
        db.close();
    }


    public Cursor getAllSwitchComponentsInAScene(String sceneId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_SCENE_COMPONENT, null, DBConstants.KEY_SC_SCENE_ID + "=? AND " + DBConstants.KEY_SC_TYPE + "=?",
                    new String[]{sceneId, AppConstants.SWITCH_TYPE}, null, null, null, null);

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }


    public Cursor getAllComponentsInAScene(String sceneId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_SCENE_COMPONENT, null, DBConstants.KEY_SC_SCENE_ID + "=?",
                    new String[]{sceneId}, null, null, null, null);

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    public String getComponentNameById(String componentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String componentName = "";
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_COMPONENT_ID + "=?",
                    new String[]{componentId}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                componentName = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
            }

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return componentName;
    }

    public String getComponentNameByPrimaryId(String _componentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String componentName = "";
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_ID + "=?",
                    new String[]{_componentId}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                componentName = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
            }

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return componentName;
    }

    public ComponentModel getComponentById(String componentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        ComponentModel component = new ComponentModel();
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_COMPONENT_ID + "=?",
                    new String[]{componentId}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                component.setName(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME)));
                component.setType(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE)));
                component.setComponentId(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID)));
            }

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return component;
    }

    public Cursor getComponentByPrimaryId(String componentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_ID + "=?",
                    new String[]{componentId}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
            }

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    public void insertIntoTouchPanel(ArrayList<TouchPanelModel> touchPanelModels) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        for (int i = 0; i < touchPanelModels.size(); i++) {

            values.put(DBConstants.KEY_C_NAME, touchPanelModels.get(i).getName());
            values.put(DBConstants.KEY_C_MID, touchPanelModels.get(i).getMId());
            values.put(DBConstants.KEY_C_MIP, touchPanelModels.get(i).getMIp());
            values.put(DBConstants.KEY_C_MNAME, touchPanelModels.get(i).getMname());

            db.insert(DBConstants.TABLE_TOUCH_PANEL, null, values);
        }

        db.close();
    }

    public Cursor getAllTouchPanelBoxes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_TOUCH_PANEL, null, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                /*if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }*/
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    // get touch panels for a machine
    public Cursor getAllTouchPanelBoxesByMachineId(int machineId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_TOUCH_PANEL, null, DBConstants.KEY_TP_MID + "=?",
                    new String[]{String.valueOf(machineId)}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                /*if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }*/
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    public void insertIntoPanelItem(ComponentModel component, String panelId, int positionInPanel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DBConstants.KEY_TP_ITEM_POS, positionInPanel);
        values.put(DBConstants.KEY_TP_ITEM_PID, panelId);
        values.put(DBConstants.KEY_TP_ITEM_COMPONENT_ID, component.getComponentId());
        values.put(DBConstants.KEY_TP_ITEM_COMPONENT_NAME, component.getName());
        values.put(DBConstants.KEY_TP_ITEM_COMPONENT_TYPE, component.getType());
        values.put(DBConstants.KEY_TP_ITEM_DEF_VALUE, "");

        if (!isPanelItemComponentAlreadyExists(panelId, positionInPanel, component.getComponentId())) {
            db.insert(DBConstants.TABLE_TOUCH_PANEL_ITEM, null, values);
        }

        db.close();
    }

    // touch panel switch (with payload)
    public void insertOrUpdateIntoPanelSwitch(TouchPanelSwitchModel component) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DBConstants.KEY_TP_SWITCH_MID, component.getMid());
        values.put(DBConstants.KEY_TP_SWITCH_COMPONENT_NAME, component.getComponentName());
        values.put(DBConstants.KEY_TP_SWITCH_COMPONENT_TYPE, component.getComponentType());

        if (component.getPos1() == null || component.getPos1().equals("")) {
            values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD1, AppConstants.TOUCH_PANEL_DEFAULT_VALUE);
        } else {
            values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD1, component.getPos1());
        }

        if (component.getPos2() == null || component.getPos2().equals("")) {
            values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD2, AppConstants.TOUCH_PANEL_DEFAULT_VALUE);
        } else {
            values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD2, component.getPos2());
        }

        if (component.getPos3() == null || component.getPos3().equals("")) {
            values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD3, AppConstants.TOUCH_PANEL_DEFAULT_VALUE);
        } else {
            values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD3, component.getPos3());
        }

        if (component.getPos4() == null || component.getPos4().equals("")) {
            values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD4, AppConstants.TOUCH_PANEL_DEFAULT_VALUE);
        } else {
            values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD4, component.getPos4());
        }

        if (component.getPos5() == null || component.getPos5().equals("")) {
            values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD5, AppConstants.TOUCH_PANEL_DEFAULT_VALUE);
        } else {
            values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD5, component.getPos5());
        }

        if (component.getPos6() == null || component.getPos6().equals("")) {
            values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD6, AppConstants.TOUCH_PANEL_DEFAULT_VALUE);
        } else {
            values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD6, component.getPos6());
        }

        /*values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD2, component.getPos2());
        values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD3, component.getPos3());
        values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD4, component.getPos4());
        values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD5, component.getPos5());
        values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD6, component.getPos6());*/
        values.put(DBConstants.KEY_TP_SWITCH_PAYLOAD, component.getPayLoad());

        if (!isPanelSwitchComponentAlreadyExists(component.getMid(), component.getComponentName())) {
            db.insert(DBConstants.TABLE_TOUCH_PANEL_SWITCH, null, values);
        } else {
            db.update(DBConstants.TABLE_TOUCH_PANEL_SWITCH, values, DBConstants.KEY_TP_SWITCH_MID + "='" + component.getMid() + "'"
                    + " AND " + DBConstants.KEY_TP_SWITCH_COMPONENT_NAME + "='" + component.getComponentName() + "'", null);
        }
        db.close();
    }

    // get all switch components for a machine
    public Cursor getAllSwitchComponentsForTouchPanel(String machineId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_TOUCH_PANEL_SWITCH, null, DBConstants.KEY_TP_SWITCH_MID + "=? AND "
                            + DBConstants.KEY_TP_SWITCH_COMPONENT_TYPE + "=?",
                    new String[]{machineId, AppConstants.SWITCH_TYPE}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;

    }

    // get all dimmer components for a machine
    public Cursor getAllDimmerComponentsForTouchPanel(String machineId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_TOUCH_PANEL_SWITCH, null, DBConstants.KEY_TP_SWITCH_MID + "=? AND "
                            + DBConstants.KEY_TP_SWITCH_COMPONENT_TYPE + "=?",
                    new String[]{machineId, AppConstants.DIMMER_TYPE}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;

    }

    public Cursor getPanelItemComponents(String panelId, int positionInPanel) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_TOUCH_PANEL_ITEM, null, DBConstants.KEY_TP_ITEM_PID + "=? AND "
                            + DBConstants.KEY_TP_ITEM_POS + "=?",
                    new String[]{panelId, String.valueOf(positionInPanel)}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                        Log.e("PANEL_POS_COMP", cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_TP_ITEM_COMPONENT_NAME)));
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;

    }

    public boolean isPanelItemComponentAlreadyExists(String panelId, int position, String componentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = true;
        try {
            // TO:DO search machine-wise

           /* cursor = db.query(DBConstants.TABLE_SCENE, null, DBConstants.KEY_C_MIP + "=?",
                    new String[]{URL_MACHINE_IP}, null, null, null, null);*/

            cursor = db.query(DBConstants.TABLE_TOUCH_PANEL_ITEM, null, DBConstants.KEY_TP_ITEM_PID + "=? AND "
                            + DBConstants.KEY_TP_ITEM_POS + "=? AND " + DBConstants.KEY_TP_ITEM_COMPONENT_ID + "=?",
                    new String[]{panelId, String.valueOf(position), componentId}, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    result = false;
                } else {
                    result = true;
                }
            } else {
                result = false;
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return result;
    }


    // if component is already assigned to a touch panel switch
    public boolean isPanelSwitchComponentAlreadyExists(String machineId, String componentName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = true;
        try {
            // TO:DO search machine-wise

           /* cursor = db.query(DBConstants.TABLE_SCENE, null, DBConstants.KEY_C_MIP + "=?",
                    new String[]{URL_MACHINE_IP}, null, null, null, null);*/

            cursor = db.query(DBConstants.TABLE_TOUCH_PANEL_SWITCH, null, DBConstants.KEY_TP_SWITCH_MID + "=? AND "
                            + DBConstants.KEY_TP_SWITCH_COMPONENT_NAME + "=?",
                    new String[]{machineId, componentName}, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    result = false;
                } else {
                    result = true;
                }
            } else {
                result = false;
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return result;
    }

    // get touch panel switch assignments for selected component
    public Cursor getSwitchAssignmentForAComponent(String machineId, String componentName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_TOUCH_PANEL_SWITCH, null, DBConstants.KEY_TP_SWITCH_MID + "=? AND "
                            + DBConstants.KEY_TP_SWITCH_COMPONENT_NAME + "=?",
                    new String[]{machineId, componentName}, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                }
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    public void deletePanelItemComponents(String panelId, int positionInPanel, String componentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor = null;
        try {
            /*cursor = db.query(DBConstants.TABLE_TOUCH_PANEL_ITEM, null, DBConstants.KEY_TP_ITEM_PID + "=? AND "
                            + DBConstants.KEY_TP_ITEM_POS + "=? AND " +DBConstants.KEY_TP_ITEM_COMPONENT_ID + "=?" ,
                    new String[]{panelId, String.valueOf(positionInPanel), componentId }, null, null, null, null);*/

            db.delete(DBConstants.TABLE_TOUCH_PANEL_ITEM, DBConstants.KEY_TP_ITEM_PID + "=? AND "
                            + DBConstants.KEY_TP_ITEM_POS + "=? AND " + DBConstants.KEY_TP_ITEM_COMPONENT_ID + "=?",
                    new String[]{panelId, String.valueOf(positionInPanel), componentId});
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
    }

    public boolean insertIntoFavorite(String componentPrimaryId, String componentId, String componentName, String componentType, String machineID, String machineIP, String machineName) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean flag = false;

        ContentValues values = new ContentValues();
        values.put(DBConstants.KEY_F_CID, componentPrimaryId);
        values.put(DBConstants.KEY_C_COMPONENT_ID, componentId);
        values.put(DBConstants.KEY_F_CNAME, componentName);
        values.put(DBConstants.KEY_C_TYPE, componentType);
        values.put(DBConstants.KEY_C_MID, machineID);
        values.put(DBConstants.KEY_C_MIP, machineIP);
        values.put(DBConstants.KEY_F_MNAME, machineName);
        values.put(DBConstants.KEY_M_ISACTIVE, "true");

        if (!isAlreadyAFavourite(componentId, machineID)) {
            db.insert(DBConstants.TABLE_FAVOURITE, null, values);
        } else {
            flag = true;
        }

        db.close();
        return flag;
    }

    public int getComponentTypeCountInFavourite(String comopnentType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.query(DBConstants.TABLE_FAVOURITE, null, DBConstants.KEY_C_TYPE + "=?",
                    new String[]{comopnentType}, null, null, null, null);
            if (cursor != null) {
                count = cursor.getCount();
            } else {
                Log.e("CURSOR", "null");
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return count;
    }

    public boolean isAlreadyAFavourite(String componentId, String machineIP) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = true;
        try {
            cursor = db.query(DBConstants.TABLE_FAVOURITE, null, DBConstants.KEY_C_COMPONENT_ID + "=? AND " + DBConstants.KEY_C_MID + "=?",
                    new String[]{componentId, machineIP}, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    result = false;
                } else {
                    result = true;
                }
            } else {
                result = false;
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return result;
    }

    public Cursor getAllFavouriteComponents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_FAVOURITE, null, null, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        } /*finally {
            if (cursor != null) {
                cursor.close();
            }
        }*/
        return cursor;
    }

    // to delete fav component
    public void deleteComponentFromFavourite(String componentId) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DBConstants.TABLE_FAVOURITE, DBConstants.KEY_F_CID + "='" + componentId + "'", null);
    }

    public long insertIntoScheduler(SchedulerModel schedulerModel) {

        Log.e("Scheduler insert", schedulerModel.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DBConstants.KEY_SCH_NAME, schedulerModel.getSchedulerName());
        values.put(DBConstants.KEY_SCH_DATETIME, schedulerModel.getDateTime());
        values.put(DBConstants.KEY_SCH_DEFAULT, schedulerModel.getDefaultValue());
        values.put(DBConstants.KEY_SCH_DEF_ON_OFF, schedulerModel.getDefaultOnOffState());
        values.put(DBConstants.KEY_SCH_IS_SCENE, schedulerModel.isScene());
        values.put(DBConstants.KEY_SCH_SCENE_ID, schedulerModel.getComponentPrimaryId());
        values.put(DBConstants.KEY_SCH_COMPONENT_ID, schedulerModel.getComponentId());
        values.put(DBConstants.KEY_SCH_SCENE_NAME, schedulerModel.getComponentName());
        values.put(DBConstants.KEY_SCH_TYPE, schedulerModel.getComponentType());
        values.put(DBConstants.KEY_SCH_MNAME, schedulerModel.getMachineName());
        values.put(DBConstants.KEY_SCH_MID, schedulerModel.getMid());
        values.put(DBConstants.KEY_SCH_MIP, schedulerModel.getMip());
        values.put(DBConstants.KEY_M_ISACTIVE, "true");
        values.put(DBConstants.KEY_SCH_ALARM_ID, schedulerModel.getAlarmId());

        long id = db.insert(DBConstants.TABLE_SCHEDULERS, null, values);

        db.close();
        return id;
    }

    // check if the component (switch/dimmer) is scheduled or not
    public boolean isAlreadyScheduled(String componentId, String machineId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = true;
        try {
            cursor = db.query(DBConstants.TABLE_SCHEDULERS, null, DBConstants.KEY_SCH_SCENE_ID + "=? AND " + DBConstants.KEY_SCH_MID + "=?",
                    new String[]{componentId, machineId}, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    result = false;
                } else {
                    result = true;
                }
            } else {
                result = false;
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return result;
    }

    // check if a scene is already scheduled
    public boolean isAlreadyScheduled(String componentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = true;
        try {
            cursor = db.query(DBConstants.TABLE_SCHEDULERS, null, DBConstants.KEY_SCH_SCENE_ID + "=? AND " + DBConstants.KEY_SCH_IS_SCENE + "=?",
                    new String[]{componentId, "1"}, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    result = false;
                } else {
                    result = true;
                }
            } else {
                result = false;
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return result;
    }

    public Cursor getAllSchedulers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_SCHEDULERS, null, null, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    // to delete  scheduler
    public void deleteScheduler(String schedulerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DBConstants.TABLE_SCHEDULERS, DBConstants.KEY_SCH_ID + "='" + schedulerId + "'", null);
        db.close();
    }

    public void updateScheduler(SchedulerModel schedulerModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DBConstants.KEY_SCH_NAME, schedulerModel.getSchedulerName());
        values.put(DBConstants.KEY_SCH_DATETIME, schedulerModel.getDateTime());
        values.put(DBConstants.KEY_SCH_DEFAULT, schedulerModel.getDefaultValue());
        values.put(DBConstants.KEY_SCH_ALARM_ID, schedulerModel.getAlarmId());

        db.update(DBConstants.TABLE_SCHEDULERS, values, DBConstants.KEY_SCH_ID + " = " + schedulerModel.getId(), null);

        db.close();
    }

    // master on/off state
    public void updateSchedulerDefaultOnOffValue(boolean isOn, String schedulerId) {
        Log.e("TAG_DB", "update default on/off schedulers");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (isOn) {
            values.put(DBConstants.KEY_SCH_DEF_ON_OFF, AppConstants.ON_VALUE);
        } else {
            values.put(DBConstants.KEY_SCH_DEF_ON_OFF, AppConstants.OFF_VALUE);
        }

        db.update(DBConstants.TABLE_SCHEDULERS, values, DBConstants.KEY_SCH_ID + " = " + schedulerId, null);
        db.close();
    }

    public SchedulerModel getSchedulerById(String _schedulerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        SchedulerModel schedulerModel = new SchedulerModel();
        try {
            cursor = db.query(DBConstants.TABLE_SCHEDULERS, null, DBConstants.KEY_SCH_ID + "=?",
                    new String[]{_schedulerId}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                schedulerModel.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_ID))));
                schedulerModel.setSchedulerName(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_NAME)));
                schedulerModel.setComponentPrimaryId(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_SCENE_ID)));
                schedulerModel.setComponentId(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_COMPONENT_ID)));
                schedulerModel.setDefaultValue(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_DEFAULT)));
                schedulerModel.setComponentType(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_TYPE)));
                schedulerModel.setMip(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_MIP)));
                schedulerModel.setComponentName(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_SCENE_NAME)));
                schedulerModel.setDefaultOnOffState(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_DEF_ON_OFF)));
                schedulerModel.setAlarmId(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_ALARM_ID)));
            }

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return schedulerModel;
    }

    public SchedulerModel getSchedulerByComponentId(String compoentId, String machineId, String isScene) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        SchedulerModel schedulerModel = new SchedulerModel();
        try {

            // if it's not a scene, query for switches/dimmers for the scheduled machine
            if (isScene != "1") {
                cursor = db.query(DBConstants.TABLE_SCHEDULERS, null, DBConstants.KEY_SCH_SCENE_ID + "=? AND " + DBConstants.KEY_SCH_MID + "=?",
                        new String[]{compoentId, machineId}, null, null, null, null);
            } else {  // else if it's a scene, query for scenes
                cursor = db.query(DBConstants.TABLE_SCHEDULERS, null, DBConstants.KEY_SCH_SCENE_ID + "=? AND " + DBConstants.KEY_SCH_IS_SCENE + "=?",
                        new String[]{compoentId, "1"}, null, null, null, null);
            }

            if (cursor != null) {
                cursor.moveToFirst();
                schedulerModel.setSchedulerName(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_NAME)));
                schedulerModel.setComponentPrimaryId(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_SCENE_ID)));
                schedulerModel.setComponentId(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_COMPONENT_ID)));
                schedulerModel.setDefaultValue(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_DEFAULT)));
                schedulerModel.setComponentType(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_TYPE)));
                schedulerModel.setMip(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_MIP)));
                schedulerModel.setComponentName(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_SCENE_NAME)));
                schedulerModel.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_SCH_DATETIME)));

            }

        } catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return schedulerModel;
    }

}
