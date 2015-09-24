package com.webmyne.android.d_brain.ui.dbHelpers;

/**
 * Created by priyasindkar on 18-09-2015.
 */
public class DBConstants {

    // All Static variables
    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "d_brain.DB";
    public static final String DATABASE_PATH = "/data/data/com.webmyne.android.d_brain/databases/";

    // Machine table name
    public static final String TABLE_MACHINE = "Machine";
    // Component table name
    public static final String TABLE_COMPONENT = "Component";
    // Scene table name
    public static final String TABLE_SCENE = "Scene";
    // Scene_Component table name
    public static final String TABLE_SCENE_COMPONENT = "SceneComponent";

    // Machine Table Columns names
    public static final String KEY_M_ID = "id";
    public static final String KEY_M_DA = "DA";
    public static final String KEY_M_SERIALNO = "DSN";
    public static final String KEY_M_VERSION = "DV";
    public static final String KEY_M_PRODUCTCODE = "DPC";
    public static final String KEY_M_DATETIME = "DCT";
    public static final String KEY_M_IP = "DIP";
    public static final String KEY_M_NAME = "DNAME";

    // Component Table Column names
    public static final String KEY_C_ID = "ID";
    public static final String KEY_C_NAME = "NAME";
    public static final String KEY_C_TYPE = "COMPONENT_TYPE";
    public static final String KEY_C_MIP = "MACHINE_IP";
    public static final String KEY_C_MID = "MACHINE_ID";

    // Scene Table Column names
    public static final String KEY_S_ID = "ID";
    public static final String KEY_S_NAME = "NAME";

    // Scene-Component Table Column names
    public static final String KEY_SC_ID = "ID";
    public static final String KEY_SC_COMPONENT_ID = "COMPONENT_ID";
    public static final String KEY_SC_SCENE_ID = "SCENE_ID";
    public static final String KEY_SC_TYPE = "COMPONENT_TYPE";
    public static final String KEY_SC_MIP = "MACHINE_IP";
    public static final String KEY_SC_DEFAULT = "DEF_VALUE";

    //Machine Config
    public static final String MACHINE1_IP = "192.168.1.199";
    public static final String TEMP_PRODUCT_CODE = "0123456789";

    /*public static final String CREATE_MACHINE_TABLE = "CREATE TABLE " + TABLE_MACHINE + "("
            + DBConstants.KEY_M_ID + " INTEGER PRIMARY KEY,"
            + DBConstants.KEY_M_DA + " TEXT,"
            + DBConstants.KEY_M_SERIALNO + " TEXT,"
            + DBConstants.KEY_M_VERSION + " TEXT,"
            + DBConstants.KEY_M_PRODUCTCODE + " TEXT,"
            + DBConstants.KEY_M_DATETIME + " TEXT,"
            + DBConstants.KEY_M_IP + " TEXT,"
            + DBConstants.KEY_M_NAME + " TEXT" + ")";

    //create query for component table
    public static final String CREATE_COMPONENT_TABLE = "CREATE TABLE  " + TABLE_COMPONENT + "("
            + DBConstants.KEY_C_ID + " INTEGER PRIMARY KEY,"
            + DBConstants.KEY_C_NAME + " TEXT,"
            + DBConstants.KEY_C_TYPE + " TEXT,"
            + DBConstants.KEY_C_MIP + " TEXT,"
            + DBConstants.KEY_C_MID + " TEXT" + ")";*/


}
