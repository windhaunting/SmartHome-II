/**
 * This class has the information of All the constant values used through out the application.
 */
package com.GatewayInterface;

public class Const {
    
    public static final String USER_MODE_AWAY="AWAY";
    public static final String USER_MODE_HOME="HOME";
    
    public static final  int TASK1_TEMP_LOW_THRESHOLD = 1;
    public static final  int TASK1_TEMP_HIGH_THRESHOLD = 2;
    
    public static final int TYPE_SENSOR = 0;
    public static final int TYPE_SMART_DEVICE =1;
    public static final int TYPE_SMART_DATABASE=2;
    
    public static final String CONFIG_IPS_FILE = "configips.csv";
    public  static final String  NAME_SENSOR_TEMPERATURE = "TEMPERATURE";
    public  static final String  NAME_SENSOR_MOTION ="MOTION";
    public static final String NAME_DEVICE_BULB = "BULB";
    public  static final String NAME_DEVICE_OUTLET = "OUTLET";
    public  static final String  NAME_SENSOR_DOOR = "DOOR";
    public  static final String  NAME_DATABASE = "DATABASE";
    public static final String NAME_SENSOR_PRESENCE= "PRESENCE";
    public  static final String  NAME_DATABASE_REP = "DATABASE_REP";
    
    public static final int ID_SENSOR_TEMPERATURE = 1;
    public static final int ID_SENSOR_MOTION = 2;
    public static final int ID_DOOR=3;
    public static final int ID_SENSOR_PRESENCE = 4;

    public static final int ID_DEVICE_BULB = 5;
    public static final int ID_DEVICE_OUTLET = 6;
    public static final int ID_DATABASE=7;
    public static final int ID_GATEWAY=8;
    public static final int ID_GATEWAY_REP=9;
    
    public static final int NO_MOTION = 0;
    public static final int  YES_MOTION_ = 1;
    
    public static final int OPEN = 1;
    public static final int CLOSED=0;

    
    public static final int ON = 1;
    public static final int OFF = 0;
    
    public final static String STR_LOOKUP_TEMP="tempeSensorImpl";
    public final static String STR_LOOKUP_HEATER = "HeaterImpl";
    public final static String STR_LOOKUP_SMART_BULB = "bulbSmart";
    public  static String STR_LOOKUP_GATEWAY = "GwServerInterfaceImpl";
    public  static String STR_LOOKUP_TMP_GATEWAY = "GwServerInterfaceImpl";

    public final static String STR_LOOKUP_MOTION = "motionSensorImpl";
    public final static String STR_LOOKUP_USER_OPERATION = "UserOperation";
    public final static String STR_LOOKUP_DOOR = "DoorSensorImpl";
    public static String STR_LOOKUP_DATABASE = "BackendDatabaseimpl";
    public static String STR_LOOKUP_GATEWAY_REP = "GwServerInterfaceImplRep";
    
    
    public static int GATEWAY_PORT = 4850;
    public static int GATEWAY_TMP_PORT = 4850;

    public final static int GATEWAY_PORT_REP = 4876;
    public final static int SENSOR_PORT = 4908;
    public final static int HEATER_PORT = 4707;
    public final static int TEMP_SENSOR_PORT =4568;
    public final static int MOTION_SENSOR_PORT =4959;
    public final static int SMART_BULB_SENSOR_PORT =4673;
    public final static int USER_OPERATION_PORT=4554;
    public final static int DOOR_PORT = 4789;
    public final static int DATABASE_PORT = 4585;
    
    public  static String GATEWAY_SERVER_IP = "localhost";
    public  static String GATEWAY_TMP_IP = "localhost";

    public  static String GATEWAY_SERVER_IP_REP = "localhost";
    public  static String CLIENT_SENSOR_TMPERATURE_IP = "localhost";
    public  static String CLIENT_SMART_HEATER_IP = "localhost";
    public  static String CLIENT_SENSOR_MOTION_IP = "localhost";
    public  static String CLIENT_SMART_BULB_IP="localhost";
    public  static String USER_OPERATION_IP="localhost";
    public  static String DATABASE_IP="localhost";
    public  static String DOOR_IP="localhost";
    
    public final static String TEST_INPUT_FILE = "test-input.csv";
    
    public final static String LAB2_TEST_INPUT_FILE = "lab2-test-input.csv";
    
    public final static String LAB3_TEST = "lab3_test";

    public final static String GW_TEMP_ACTION = "Q(Temp)";
    public final static String GW_MOTION_ACTION = "Q(Motion)";
    
    public final static String GW_DOOR_QUERY_ACTION = "Q(Door)";
    
    
    public final static String LOGTYPE_PART1 = "RECORD EVENT FROM SENSOR";
    public final static String LOGTYPE_PART23 = "RECORD EVENT FROM MODE";

    ///all kinds of events 
    //motion
    public final static String EVT_MOTION_REGISTER = "eventMotionRegister";
    public final static String EVT_MOTION_CHANGED = "eventChangeMotion";
    public final static String EVT_RECV_QUERY_MOTION = "receivedGWQueryMotion";
    //door
    public final static String EVT_DOOR_REGISTER = "eventDoorRegister";
    public final static String EVT_GW_DOOR_CHANGED = "doorChanged";
    public final static String EVT_RECV_QUERY_DOOR = "receivedGWQueryDoor";

    //heater
    public final static String EVT_HEATER_REGISTER = "eventHeaterRegister";
    public final static String EVT_HEATER_CHANGED = "eventHeaterChanged";
    
    //bulb
    public final static String EVT_BULB_REGISTER = "eventBulbRegister";
    public final static String EVT_BULB_CHANGED = "eventBulbChanged";
   
    //temp
    public final static String EVT_TEMP_REGISTER = "eventTemperatureRegister";
    public final static String EVT_TEMP_CHANGED = "eventTemperatureChanged";
    public final static String EVT_RECV_QUERY_TEMP = "receivedGWQueryTemp";

    //gateway events logged
    public final static String EVT_GW_MOTION_NO_MOTION_SENSED = "eventNoMotion";
    public final static String EVT_GW_MOTION_MOTION_SENSED = "eventYesMotion";
    public final static String EVT_GW_DOOR_CLOSED_SENSED = "doorClosed";
    public final static String EVT_DOOR_OPEN_SENSED = "doorOpen";

    public final static String EVT_GW_HEATER_ON_SENSED = "eventHeaterOnSensed";
    public final static String EVT_GW_HEATER_OFF_SENSED = "eventHeaterOffSensed";
    public final static String EVT_GW_BULB_ON_SENSED = "eventBulbOnSensed";
    public final static String EVT_GW_BULB_OFF_SENSED = "eventBulbOffSensed";
    
    public final static String EVT_GW_TEMP_QUERY = "eventGatewayQueryTemp";
    public final static String EVT_GW_TEMP_CHANGE_SENSED = "eventTemperatureChangedSensed";

    public final static String EVT_GW_MOTION_QUERY = "eventGatewayQueryMotion";
    public final static String EVT_GW_DOOR_QUERY = "eventGatewayQueryDoor";
    public final static String EVT_GW_HEATER_CHANGE = "eventGatewayChangeHeater";
    public final static String EVT_GW_BULB_CHANGE = "eventGatewayChangeBulb";

    //for fault tolerant
    public final static String FT_LIVE_HEART_MSG = "I AM ALIVE";
    public final static byte FT_HEART_INTEVAL = 3;        		//2
    public final static int FT_TIMES_NO_HEART = 3;		
    
    
}