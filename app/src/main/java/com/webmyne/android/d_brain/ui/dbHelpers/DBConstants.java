package com.webmyne.android.d_brain.ui.dbHelpers;

/**
 * Created by priyasindkar on 18-09-2015.
 */
public class DBConstants {

    // All Static variables
    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "machineDetails";

    // Machine table name
    public static final String TABLE_MACHINE = "machine";

    // Machine Table Columns names
    public static final String KEY_M_ID = "id";
    public static final String KEY_M_DA = "DA";
    public static final String KEY_M_SERIALNO = "DSN";
    public static final String KEY_M_VERSION = "DV";
    public static final String KEY_M_PRODUCTCODE = "DPC";
    public static final String KEY_M_DATETIME = "DCT";
    public static final String KEY_M_IP = "DIP";
    public static final String KEY_M_NAME = "DNAME";

    public static final String CREATE_MACHINE_TABLE = "CREATE TABLE " + DBConstants.TABLE_MACHINE + "("
            + DBConstants.KEY_M_ID + " INTEGER PRIMARY KEY,"
            + DBConstants.KEY_M_DA + " TEXT,"
            + DBConstants.KEY_M_SERIALNO + " TEXT,"
            + DBConstants.KEY_M_VERSION + " TEXT,"
            + DBConstants.KEY_M_PRODUCTCODE + " TEXT,"
            + DBConstants.KEY_M_DATETIME + " TEXT,"
            + DBConstants.KEY_M_IP + " TEXT,"
            + DBConstants.KEY_M_NAME + " TEXT" + ")";

    public static final String MACHINE1_IP = "192.168.1.199";
}
