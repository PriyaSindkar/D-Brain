package com.webmyne.android.d_brain.ui.dbHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.webmyne.android.d_brain.ui.xmlHelpers.XMLValues;

import java.util.ArrayList;


/**
 * Created by priyasindkar on 18-09-2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConstants.CREATE_MACHINE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.TABLE_MACHINE);

        // Create tables again
        onCreate(db);
    }

   public void addMachine(ArrayList<XMLValues> machineValuesList) {
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
}
