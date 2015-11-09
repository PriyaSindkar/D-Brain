package com.webmyne.android.d_brain.ui.dbHelpers;

/**
 * Created by priyasindkar on 18-09-2015.
 */
public class DBConstants {

    // All Static variables
    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "d_b.db";
    public static final String DATABASE_PATH = "/data/data/com.webmyne.android.d_brain/databases/";

    // Machine table name
    public static final String TABLE_MACHINE = "Machine";
    // Component table name
    public static final String TABLE_COMPONENT = "Component";
    // Scene table name
    public static final String TABLE_SCENE = "Scene";
    // Scene_Component table name
    public static final String TABLE_SCENE_COMPONENT = "SceneComponent";
    // TouchPanel table name
    public static final String TABLE_TOUCH_PANEL = "TouchPanel";

    // TouchPanel table name
    public static final String TABLE_TOUCH_PANEL_ITEM = "TouchPanelItem";

    // Favorite table name
    public static final String TABLE_FAVOURITE = "FavouriteComponent";

    // Schedulers table name
    public static final String TABLE_SCHEDULERS = "Schedulers";

    // Machine Table Columns names
    public static final String KEY_M_ID = "_id";
    public static final String KEY_M_DA = "DA";
    public static final String KEY_M_SERIALNO = "DSN";
    public static final String KEY_M_VERSION = "DV";
    public static final String KEY_M_PRODUCTCODE = "DPC";
    public static final String KEY_M_DATETIME = "DCT";
    public static final String KEY_M_IP = "MACHINE_IP";
    public static final String KEY_M_NAME = "NAME";
    public static final String KEY_M_ISACTIVE = "IS_ACTIVE";

    // Component Table Column names
    public static final String KEY_C_ID = "_id";
    public static final String KEY_C_COMPONENT_ID = "COMPONENT_ID";
    public static final String KEY_C_NAME = "NAME";
    public static final String KEY_C_DETAILS = "DETAILS";
    public static final String KEY_C_TYPE = "COMPONENT_TYPE";
    public static final String KEY_C_MIP = "MACHINE_IP";
    public static final String KEY_C_MID = "MACHINE_ID";
    public static final String KEY_C_MNAME = "MACHINE_NAME";

    // Scene Table Column names
    public static final String KEY_S_ID = "_id";
    public static final String KEY_S_NAME = "NAME";
    public static final String KEY_S_STATUS = "IS_SCENE_ON";

    // Scene-Component Table Column names
    public static final String KEY_SC_ID = "_id";
    public static final String KEY_SC_COMPONENT_ID = "COMPONENT_ID";
    public static final String KEY_SC_COMP_PRIMARY_ID = "SCENE_COMPONENT_ID";
    public static final String KEY_SC_SCENE_ID = "SCENE_ID";
    public static final String KEY_SC_TYPE = "COMPONENT_TYPE";
    public static final String KEY_SC_MIP = "MACHINE_IP";
    public static final String KEY_SC_MID = "MACHINE_ID";
    public static final String KEY_SC_DEFAULT = "DEF_VALUE";
    public static final String KEY_SC_MNAME = "MACHINE_NAME";

    // Touch_Panel_item Table Column names
    public static final String KEY_TP_ITEM_ID = "_id";
    public static final String KEY_TP_ITEM_PID = "TOUCH_PANEL_ID";
    public static final String KEY_TP_ITEM_POS = "POSITION_IN_PANEL";
    public static final String KEY_TP_ITEM_COMPONENT_ID = "COMPONENT_ID";
    public static final String KEY_TP_ITEM_COMPONENT_NAME = "COMPONENT_NAME";
    public static final String KEY_TP_ITEM_COMPONENT_TYPE = "COMPONENT_TYPE";
    public static final String KEY_TP_ITEM_DEF_VALUE = "DEF_VALUE";

    //Favourite table column names
    public static final String KEY_F_CNAME = "COMPONENT_NAME";
    public static final String KEY_F_MNAME = "MACHINE_NAME";
    public static final String KEY_F_MID = "MACHINE_ID";
    public static final String KEY_F_CID = "PRIMARY_COMP_ID";

    //Schedulers table column names
    public static final String KEY_SCH_ID = "_id";
    public static final String KEY_SCH_NAME = "NAME";
    public static final String KEY_SCH_DATETIME = "DATEANDTIME";
    public static final String KEY_SCH_IS_SCENE = "IS_SCENE";
    public static final String KEY_SCH_SCENE_NAME = "SCENE_NAME";
    public static final String KEY_SCH_SCENE_ID = "SCENE_ID";
    public static final String KEY_SCH_TYPE = "COMPONENT_TYPE";
    public static final String KEY_SCH_COMPONENT_ID = "COMPONENT_ID";
    public static final String KEY_SCH_MIP = "MACHINE_IP";
    public static final String KEY_SCH_MID = "MACHINE_ID";
    public static final String KEY_SCH_DEFAULT = "DEF_VALUE";
    public static final String KEY_SCH_MNAME = "MACHINE_NAME";
    public static final String KEY_SCH_DEF_ON_OFF = "DEF_ON_OFF_STATE";
    public static final String KEY_SCH_ALARM_ID = "ALARM_ID";



    //Machine Config
  //  public static final String MACHINE1_IP = "192.168.1.199";

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
