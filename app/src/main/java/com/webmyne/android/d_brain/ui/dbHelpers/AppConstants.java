package com.webmyne.android.d_brain.ui.dbHelpers;

/**
 * Created by priyasindkar on 21-09-2015.
 */
public class AppConstants {

    public static String SIMULATOR_URL = "http://ws-srv-net.in.webmyne.com/Applications/DsquareWS";
    public static String CHANGE_STATUS_SIMULATOR_URL="http://ws-srv-net.in.webmyne.com/Applications/DsquareWS/Simulator.svc/json/Update";
    public static final String TEMP_PRODUCT_CODE = "02119-6119";

    public static String URL_MACHINE_IP = "http://192.168.1.199";
    // for simulator
   //public static String URL_MACHINE_IP = "http://ws-srv-net.in.webmyne.com/Applications/DsquareWS/Simulator.svc/json/Update";
   // public static String URL_MACHINE_IP = "http://d2brain.com";
    public static String URL_FETCH_MACHINE_STATUS = "/debt.xml";
    public static String URL_FETCH_SWITCH_STATUS = "/swcr.xml";
    public static String URL_CHANGE_SWITCH_STATUS = "/cswcr.cgi?SW=";
    public static String URL_FETCH_DIMMER_STATUS = "/dmcr.xml";
    public static String URL_CHANGE_DIMMER_STATUS = "/cdmcr.cgi?DM=";
    public static String URL_FETCH_SENSOR_STATUS = "/ascr.xml";

    //Type of component
    public static String SWITCH_TYPE="switch";
    public static String DIMMER_TYPE="dimmer";
    public static String MOTOR_TYPE="motor";
    public static String ALERT_TYPE="alert";
    public static String SWITCH_PREFIX = "SW";
    public static String DIMMER_PREFIX = "DM";
    public static String MOTOR_PREFIX = "MO";
    public static String ALERT_PREFIX = "AS";
    public static String TOUCH_PANEL_TYPE = "touch_panel";

    //component values
    public  static String OFF_VALUE = "00";
    public  static String ON_VALUE = "01";
    public  static String DIMMER_DEFAULT_VALUE = "0";
}
