package com.webmyne.android.d_brain.ui.dbHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.Model.SceneItemsDataObject;
import com.webmyne.android.d_brain.ui.Model.TouchPanelModel;
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
        Log.e("mPath", mPath);
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

   public void insertIntoMachine(ArrayList<XMLValues> machineValuesList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

       for(int i=0; i<machineValuesList.size(); i++) {
           if(machineValuesList.get(i).tagName.equals(DBConstants.KEY_M_DA)){
               values.put(DBConstants.KEY_M_DA, machineValuesList.get(i).tagValue);
           } else if(machineValuesList.get(i).tagName.equals(DBConstants.KEY_M_SERIALNO)){
               values.put(DBConstants.KEY_M_SERIALNO, machineValuesList.get(i).tagValue);
           } else if(machineValuesList.get(i).tagName.equals(DBConstants.KEY_M_VERSION)){
               values.put(DBConstants.KEY_M_VERSION, machineValuesList.get(i).tagValue);
           } else if(machineValuesList.get(i).tagName.equals(DBConstants.KEY_M_PRODUCTCODE)){
               values.put(DBConstants.KEY_M_PRODUCTCODE, machineValuesList.get(i).tagValue);
           } else if(machineValuesList.get(i).tagName.equals(DBConstants.KEY_M_DATETIME)){
               values.put(DBConstants.KEY_M_DATETIME, machineValuesList.get(i).tagValue);
           }
       }

       db.insert(DBConstants.TABLE_MACHINE, null, values);
       db.close();
    }

  /*   public MachineModel getMachine(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DBConstants.TABLE_MACHINE, new String[] { DBConstants.KEY_M_ID,
                        DBConstants.KEY_M_NAME, DBConstants.KEY_M_IP }, DBConstants.KEY_M_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MachineModel machine = new MachineModel(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        return machine;
    }*/


    public void insertIntoComponent(ArrayList<ComponentModel> componentModels) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        for(int i=0; i<componentModels.size(); i++) {
            values.put(DBConstants.KEY_C_COMPONENT_ID, componentModels.get(i).getComponentId());
            values.put(DBConstants.KEY_C_NAME, componentModels.get(i).getName());
            values.put(DBConstants.KEY_C_TYPE, componentModels.get(i).getType());
            values.put(DBConstants.KEY_C_MID, componentModels.get(i).getMid());
            values.put(DBConstants.KEY_C_MIP, componentModels.get(i).getMip());
            values.put(DBConstants.KEY_C_MNAME, componentModels.get(i).getMachineName());

            db.insert(DBConstants.TABLE_COMPONENT, null, values);
        }

        db.close();
    }

    public void renameComponent(String componentId, String componentName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // save new component name
        ContentValues values = new ContentValues();
        values.put(DBConstants.KEY_C_NAME, componentName);
        db.update(DBConstants.TABLE_COMPONENT, values, DBConstants.KEY_C_COMPONENT_ID + "='" + componentId+"'", null);
        db.close();
    }

    public void renameComponent(String componentId, String componentName, String componentDetails) {
        SQLiteDatabase db = this.getWritableDatabase();

        // save new component name
        ContentValues values = new ContentValues();
        values.put(DBConstants.KEY_C_NAME, componentName);
        values.put(DBConstants.KEY_C_DETAILS, componentDetails);
        db.update(DBConstants.TABLE_COMPONENT, values, DBConstants.KEY_C_COMPONENT_ID + "='" + componentId+"'", null);
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
        }catch (Exception e) {
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
        }catch (Exception e) {
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
        }catch (Exception e) {
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

        for(int i=0; i<componentModels.size(); i++) {
            values.put(DBConstants.KEY_SC_SCENE_ID, String.valueOf(sceneId));
            values.put(DBConstants.KEY_SC_COMPONENT_ID, componentModels.get(i).getSceneItemId());
            values.put(DBConstants.KEY_SC_TYPE, componentModels.get(i).getSceneControlType());
            values.put(DBConstants.KEY_SC_MIP, componentModels.get(i).getMachineIP());
            values.put(DBConstants.KEY_SC_DEFAULT, componentModels.get(i).getDefaultValue());

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

    public String getSceneStatus(String sceneId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String sceneStatus="no";
        try {
            // TO:DO search machine-wise

           /* cursor = db.query(DBConstants.TABLE_SCENE, null, DBConstants.KEY_C_MIP + "=?",
                    new String[]{machineIP}, null, null, null, null);*/

            cursor = db.query(DBConstants.TABLE_SCENE, null, DBConstants.KEY_S_ID + "=?",
                    new String[]{sceneId}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    sceneStatus = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_S_STATUS));
                }
            }
        }catch (Exception e) {
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
                    null , null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                    } while (cursor.moveToNext());
                }
            }
        }catch (Exception e) {
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

        for(int i=0; i<componentModels.size(); i++) {

            values.put(DBConstants.KEY_SC_SCENE_ID, sceneId);
            values.put(DBConstants.KEY_SC_COMPONENT_ID, componentModels.get(i).getSceneItemId());
            values.put(DBConstants.KEY_SC_TYPE, componentModels.get(i).getSceneControlType());
            values.put(DBConstants.KEY_SC_MIP, componentModels.get(i).getMachineIP());
            values.put(DBConstants.KEY_SC_DEFAULT, componentModels.get(i).getDefaultValue());

            if( !isComponentAlreadyExists(componentModels.get(i).getSceneItemId(), sceneId))
                db.insert(DBConstants.TABLE_SCENE_COMPONENT, null, values);
            else {
                db.update(DBConstants.TABLE_SCENE_COMPONENT, values, DBConstants.KEY_SC_COMPONENT_ID + " = '" + componentModels.get(i).getSceneItemId() + "'", null);
            }
        }

        db.close();
    }

    // to update scene component default values
    public void updateSceneComponents(ArrayList<SceneItemsDataObject> componentModels) {
        SQLiteDatabase db = this.getWritableDatabase();

        // update scene components
        ContentValues values = new ContentValues();

        for(int i=0; i<componentModels.size(); i++) {

            if(componentModels.get(i).getSceneControlType().equals(AppConstants.DIMMER_TYPE)) {
                String strDefault = "00";
                if(!componentModels.get(i).getDefaultValue().equals(AppConstants.OFF_VALUE)) {
                    if(componentModels.get(i).getDefaultValue().equals("100") ){
                        strDefault = "100";
                    } else {
                        strDefault = String.format("%02d", (Integer.parseInt(componentModels.get(i).getDefaultValue())));
                    }
                }
                values.put(DBConstants.KEY_SC_DEFAULT, strDefault);
            } else {
                values.put(DBConstants.KEY_SC_DEFAULT, componentModels.get(i).getDefaultValue());
            }

            db.update(DBConstants.TABLE_SCENE_COMPONENT, values, DBConstants.KEY_SC_COMPONENT_ID + " = '" + componentModels.get(i).getSceneItemId() + "'", null);
        }

        db.close();
    }

    // to update scene component default values
    public void deleteSceneComponents(ArrayList<SceneItemsDataObject> componentModels) {
        SQLiteDatabase db = this.getWritableDatabase();

        for(int i=0; i<componentModels.size(); i++) {
            db.delete(DBConstants.TABLE_SCENE_COMPONENT, DBConstants.KEY_SC_COMPONENT_ID + "='" + componentModels.get(i).getSceneItemId() + "'", null);
        }

        db.close();
    }

    public boolean isComponentAlreadyExists(String componentId, String sceneId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = true;
        try {
            // TO:DO search machine-wise

           /* cursor = db.query(DBConstants.TABLE_SCENE, null, DBConstants.KEY_C_MIP + "=?",
                    new String[]{machineIP}, null, null, null, null);*/

            cursor = db.query(DBConstants.TABLE_SCENE_COMPONENT, null, DBConstants.KEY_SC_SCENE_ID + "=? AND " + DBConstants.KEY_SC_COMPONENT_ID + "=?",
                    new String[]{sceneId, componentId}, null, null, null, null);
            if (cursor != null) {
                if(cursor.getCount() == 0) {
                    result =  false;
                } else {
                    result =  true;
                }
            } else {
                result =  false;
            }
        }catch (Exception e) {
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

        }catch (Exception e) {
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

        }catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;
    }

    public String getComponentNameById(String componentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String componentName = "";
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_COMPONENT_ID + "=?" ,
                    new String[]{componentId}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                componentName =  cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME));
            }

        }catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return componentName;
    }

    public ComponentModel getComponentById(String componentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        ComponentModel component = new ComponentModel();
        try {
            cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_COMPONENT_ID + "=?" ,
                    new String[]{componentId}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                component.setName(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_NAME)));
                component.setType(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_TYPE)));
                component.setComponentId(cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_C_COMPONENT_ID)));
            }

        }catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return component;
    }

    public void insertIntoTouchPanel(ArrayList<TouchPanelModel> touchPanelModels) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        for(int i=0; i<touchPanelModels.size(); i++) {
            values.put(DBConstants.KEY_C_NAME, touchPanelModels.get(i).getName());

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
        }catch (Exception e) {
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

        if( !isPanelItemComponentAlreadyExists(panelId, positionInPanel,component.getComponentId())) {
            Log.e("NEW",component.getComponentId() );
            db.insert(DBConstants.TABLE_TOUCH_PANEL_ITEM, null, values);
        } else {
            Log.e("EXisITS", component.getComponentId() );
        }

        db.close();
    }

    public Cursor getPanelItemComponents(String panelId, int positionInPanel) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.TABLE_TOUCH_PANEL_ITEM, null, DBConstants.KEY_TP_ITEM_PID + "=? AND "
                            + DBConstants.KEY_TP_ITEM_POS + "=?",
                    new String[]{panelId, String.valueOf(positionInPanel) }, null, null, null, null);
            /*if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                        Log.e("PANEL_POS_COMP", cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.KEY_TP_ITEM_COMPONENT_NAME)));
                    } while (cursor.moveToNext());
                }
            }*/
        }catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return cursor;

    }

    public boolean isPanelItemComponentAlreadyExists(String panelId, int position,String componentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = true;
        try {
            // TO:DO search machine-wise

           /* cursor = db.query(DBConstants.TABLE_SCENE, null, DBConstants.KEY_C_MIP + "=?",
                    new String[]{machineIP}, null, null, null, null);*/

            cursor = db.query(DBConstants.TABLE_TOUCH_PANEL_ITEM, null, DBConstants.KEY_TP_ITEM_PID + "=? AND "
                            + DBConstants.KEY_TP_ITEM_POS + "=? AND " + DBConstants.KEY_TP_ITEM_COMPONENT_ID + "=?",
                    new String[]{panelId, String.valueOf(position), componentId}, null, null, null, null);
            if (cursor != null) {
                if(cursor.getCount() == 0) {
                    result =  false;
                } else {
                    result =  true;
                }
            } else {
                result =  false;
            }
        }catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
        return result;
    }

    public void deletePanelItemComponents(String panelId, int positionInPanel, String componentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor = null;
        try {
            /*cursor = db.query(DBConstants.TABLE_TOUCH_PANEL_ITEM, null, DBConstants.KEY_TP_ITEM_PID + "=? AND "
                            + DBConstants.KEY_TP_ITEM_POS + "=? AND " +DBConstants.KEY_TP_ITEM_COMPONENT_ID + "=?" ,
                    new String[]{panelId, String.valueOf(positionInPanel), componentId }, null, null, null, null);*/

            db.delete(DBConstants.TABLE_TOUCH_PANEL_ITEM,  DBConstants.KEY_TP_ITEM_PID + "=? AND "
                            + DBConstants.KEY_TP_ITEM_POS + "=? AND " +DBConstants.KEY_TP_ITEM_COMPONENT_ID + "=?" ,
                    new String[]{panelId, String.valueOf(positionInPanel), componentId });
        }catch (Exception e) {
            Log.e("EXP ", e.toString());
        }
    }
}
