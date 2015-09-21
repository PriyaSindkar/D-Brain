package com.webmyne.android.d_brain.ui.dbHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.webmyne.android.d_brain.ui.Model.ComponentModel;
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
            values.put(DBConstants.KEY_C_NAME, componentModels.get(i).getName());
            values.put(DBConstants.KEY_C_TYPE, componentModels.get(i).getType());
            values.put(DBConstants.KEY_C_MID, componentModels.get(i).getMid());
            values.put(DBConstants.KEY_C_MIP, componentModels.get(i).getMip());

            db.insert(DBConstants.TABLE_COMPONENT, null, values);
        }

        db.close();
    }

    public Cursor getAllSwitchComponentsForAMachine(String machineIP) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DBConstants.TABLE_COMPONENT, null, DBConstants.KEY_C_MIP + "=?",
                new String[] { machineIP }, null, null, null, null);
            if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                } while (cursor.moveToNext());
            }
        }

        return cursor;

    }
}
