package com.GatewayServerImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;

import com.BackendDataBaseInterfaces.RMIBackendDataBaseInterfaces;
import com.DevicesInterfaces.RMIDevicesInterfaces;
import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;
import com.GatewayInterface.MessageTrans;
import com.SmartCtrlIntfPkg.SmartCtrlInterfaces;


public class GwServerInterfaceImpl implements GatewayAllInterfaces{
	
	    public static HashMap<String, Integer>  devices_name_id = new HashMap<String, Integer>();               //<key, value> is <name, id>
	    public static HashMap<Integer, Integer>  Device_States = new HashMap<Integer, Integer>();
	    public static String userMode="HOME";
	    
	    public static int TasksOrTestcaseFunction = 0;				//TasksOrTestcaseFunction = 0 for task1 and task2,  TasksOrTestcaseFunction = true for TestCase;
	    public static LinkedHashMap<Double, String> GatewayEventValueHash = new LinkedHashMap<Double, String>();		//store the test input event action 
	    private static ArrayList<Double> timeEventArray = new ArrayList<Double>();
	    public static LinkedHashMap<Double, String> GatewayOutputHash = new LinkedHashMap<Double, String>();
	    public static ArrayList<Double> GatewayOutputArrayStr = new ArrayList<Double>();
	    
	    public static HashMap<Integer,String> storeIpAddressSensorsDevices = new HashMap<Integer,String>();
	    public static HashMap<Integer,String> processRecord = new HashMap<Integer,String>();
	    
	    private static boolean beginRegisterTime0Finish = false;
	    private static int FinishOneEventSymbol = 0;
	    private static boolean ReceivedMotionReport = false;
		
		public static boolean IWON=true;
	    public static boolean OK=false;
	    public static boolean ElectionPerformed=false;
	    public static boolean conditionVariable=true;
	    public static boolean Initiator=false;
	    public static boolean FlagClockSynchronizationFinished=false;
	    
	    public static boolean FlagRegisterNode = false; 
	    public static HashMap<Integer,String> storeIpAddressSensorsDevicesOfReplication = new HashMap<Integer,String>();


	    public static HashMap<String,Integer> ClockGWVlaue = new HashMap<String,Integer>();
	    
	    public static Stack<Integer> LClockGW = new Stack<Integer>();
		
		public static int offsetValuefromTimeServer=0;
	    public static ArrayList<Long> storeTimeStamps = new ArrayList<Long>();
	
	    public static HashMap<Integer,String> LRUcache = new HashMap<Integer,String>();
	    public static int LRUCacheMaxSize= 5;
	    public static LinkedList CacheHelperQueue = new LinkedList<Integer>();
	    
	    //////////////lab3
	    public static boolean GW_STATE_REP = false;           //the Gateway replica state, false means crash, true means live	    
	    private static long  receiveHeartBeatCountCurrent = 0;     //heartbeat times

	    public static HashSet<Integer> AllSensorsDevice_Sets = new HashSet<Integer>();    
	    
	    private static int FLAG_TEST_FAULT_CONTROL = 0;				//test falult tolerant control button flag

	    
	    public static String TeperatureState=""; // Variables that store the current state of all the sensors and devices.
	    public static String MotionState="";          //these are used for implementing consistency 
	    public static String HeaterState="";
	    public static String BulbState="";
	    public static String DoorState="";
	    
	    protected GwServerInterfaceImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	    
	
	    
	@Override
	public void register(int type, String name, String ip) throws RemoteException {
		if(!GwServerInterfaceImpl.FlagRegisterNode)  {// Checks if already a device or sensor is registered 
		// TODO Auto-generated method stub
			if(name.equals(Const.NAME_DATABASE)){
				devices_name_id.put(name, Const.ID_DATABASE);          //temperature sensor's id 1
                Device_States.put(Const.ID_DATABASE, 0);  //0 degree of temperature
                storeIpAddressSensorsDevices.put(Const.ID_DATABASE,ip);
                System.out.println("DataBase is Registered at Gateway");
			}
		if(Const.TYPE_SENSOR == type)
        {
			//System.out.println("In If Block"+ name);
            if(name.equals(Const.NAME_SENSOR_TEMPERATURE))
            {
                devices_name_id.put(name, Const.ID_SENSOR_TEMPERATURE);          //temperature sensor's id 1
                Device_States.put(Const.ID_SENSOR_TEMPERATURE, 0);  //0 degree of temperature
                storeIpAddressSensorsDevices.put(Const.ID_SENSOR_TEMPERATURE,ip);
                
                System.out.println("Temperature Sensor Registered at Gateway");

        /*        if(1 == TasksOrTestcaseFunction)
                {
                	java.util.Date date= new java.util.Date();
                	Timestamp tStmp = new Timestamp(date.getTime());
	             	String time = tStmp.toString();
	                System.out.println(time);

	        		String cvsSplitBy = ":";
	        		 // use comma as separator
	        		String[] timeStr = time.split(cvsSplitBy);  
	        		String second = timeStr[timeStr.length-1];
	                System.out.println("dddd"+second);
	                
	                double s = System.currentTimeMillis() / 1000000000;
	                System.out.println("tttt"+s);

	              //  double currentRealTime
	                //curent timestamp which is 0 
	                double timeOutputKey = 0;
	                
	                String value = "0,0";						//initialized value, lazy!!!!!
	               	 //output to the OutputHash file
	                GatewayOutputHash.put(timeOutputKey, value);
	                GatewayOutputArrayStr.add(timeOutputKey);
	               beginRegisterTime0Finish = true;
	                
                }  */
            }
             if(name.equals(Const.NAME_SENSOR_MOTION))
             {
                 devices_name_id.put(name, Const.ID_SENSOR_MOTION);          //motion sensor's id 2
                 Device_States.put(Const.ID_SENSOR_MOTION, Const.NO_MOTION);
                 storeIpAddressSensorsDevices.put(Const.ID_SENSOR_MOTION,ip);//no motion;
                 System.out.println("Motion Sensor is registered at Gateway");
             }
             if(name.equals(Const.NAME_SENSOR_DOOR))
             {
                 devices_name_id.put(name, Const.ID_DOOR);          //door sensor's id 2
                 Device_States.put(Const.ID_DOOR, Const.CLOSED);
                 storeIpAddressSensorsDevices.put(Const.ID_DOOR,ip);//no door;
                 System.out.println("Door Sensor is registered at Gateway");
             }
             if(name.equals(Const.NAME_SENSOR_PRESENCE))
             {
                 devices_name_id.put(name, Const.ID_SENSOR_PRESENCE);          //presence sensor's id 2
                 Device_States.put(Const.ID_SENSOR_PRESENCE, Const.OFF);
                 storeIpAddressSensorsDevices.put(Const.ID_SENSOR_PRESENCE,ip);
                 System.out.println("Presense Sensor is registered at Gateway");//off;
             }
             
             
        }
       else if(Const.TYPE_SMART_DEVICE==type)
        {
             if(name.equals(Const.NAME_DEVICE_BULB))
             {
                 devices_name_id.put(name, Const.ID_DEVICE_BULB);
                 Device_States.put(Const.ID_DEVICE_BULB, Const.OFF);  //bulb off;
                 storeIpAddressSensorsDevices.put(Const.ID_DEVICE_BULB,ip);
                 System.out.println("Smart Bulb is registered at Gateway");
             }
             if(name.equals(Const.NAME_DEVICE_OUTLET))
             {
                 devices_name_id.put(name, Const.ID_DEVICE_OUTLET);
                 Device_States.put(Const.ID_DEVICE_OUTLET, Const.OFF);  //smart outlet off;
                 storeIpAddressSensorsDevices.put(Const.ID_DEVICE_OUTLET,ip);
                 System.out.println("Samrt heater outlet is registered at Gateway");
             }
        }
		/**
		 * The variable GwServerInterfaceImpl.FlagRegisterNode is used to allow the execution flow to "else" block 
		 * 	in next invocation of this method. In Else block we will assign the next node to gateway replication for load balance.
		 * First Device or sensor for Gateway, Second for Gateway replication, Third for Gateway, fourth for gateway replication adn so on
		 */
		GwServerInterfaceImpl.FlagRegisterNode=true;
	

		}
		else {
			//System.out.println("In else block"+name);
			Thread t = new Thread(new AllocateNodesToReplica(type,name,ip));
			t.start();
			GwServerInterfaceImpl.FlagRegisterNode=false;
		}
		
	}
	


	@Override
	public void report_state(int device_id, int state) throws RemoteException {
		// TODO Auto-generated method stub
      //  System.out.println("report_state ' s enter here");
		 /**
	     *makeDataConsistentHelper Method is used to maintain consistency in data at replica and gateway 
	     */
	
		GwServerInterfaceImpl tmp = new GwServerInterfaceImpl();
		tmp.makeDataConsistentHelper(state, device_id);
		
		switch(device_id)
		{
			case Const.ID_SENSOR_TEMPERATURE:
			{  
				TeperatureState=Integer.toString(state);
				
                System.out.println("temperature sensor' s current temperature is : " + state);
                String id=Const.ID_SENSOR_TEMPERATURE+""; 
                String Temperature="Temerature is"+state; 
                String t1= new Timestamp(System.currentTimeMillis()).toString();
                UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,Temperature,"RECORD EVENT FROM SENSOR","");
                Thread t = new Thread(updateLog);
                t.start();
				break;
			}
			case Const.ID_SENSOR_MOTION: 
				{
					  if(Const.ON == state)
		                {
							MotionState="Motion is present";
		                    if(userMode.equals(Const.USER_MODE_HOME)){
		                        try {
		                            TimerforMotion.main(LClockGW);
		                        } catch (InterruptedException e) {
		                            // TODO Auto-generated catch block
		                            e.printStackTrace();
		                        }
		                    }
		                    if(userMode.equals(Const.USER_MODE_AWAY)){
		                        SendNotification.main();
		                    }
		                    System.out.println("Motion Sensor Current State is : " + state);
		                    String id=Const.ID_SENSOR_MOTION+""; 
		                    String t1= new Timestamp(System.currentTimeMillis()).toString();
		                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,"Motion exists","RECORD EVENT FROM SENSOR","");
		                    Thread t = new Thread(updateLog);
		                    t.start();
		                    System.out.println("motion sensor's is motion yes");
		    				break;
		                   
		                }
		                else
		                {	
		                	MotionState="Motion is Absent";
		                	 String id=Const.ID_SENSOR_MOTION+""; 
		                	 String t1= new Timestamp(System.currentTimeMillis()).toString();
			                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,"Motion does not exists","RECORD EVENT FROM SENSOR","");
			                    Thread t = new Thread(updateLog);
			                    t.start();
		                    System.out.println("motion sensor's is motion no");
		                }
		                break;
				}
			case Const.ID_DEVICE_BULB: 
			{
				 if(Const.ON == state)
	                {
					 	BulbState="Bulb is ON";
					 	String id=Const.ID_DEVICE_BULB+""; 
					 	 String t1= new Timestamp(System.currentTimeMillis()).toString();
	                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Device",id,"Bulb is ON","RECORD EVENT FROM SENSOR","");
	                    Thread t = new Thread(updateLog);
	                    t.start();
	                    System.out.println("bulb device's state is on ");
	                }
	                else
	                {
	                	BulbState="Bulb is OFF";
	                	String id=Const.ID_DEVICE_BULB+""; 
	                	 String t1= new Timestamp(System.currentTimeMillis()).toString();
	                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Device",id,"Bulb is OFF","RECORD EVENT FROM SENSOR","");
	                    Thread t = new Thread(updateLog);
	                    t.start();
	                    System.out.println("bulb device's state is off ");
	                }          
				break;
			}
			case Const.ID_DEVICE_OUTLET:
				{
					   if(Const.ON == state)
		                {	
						 HeaterState="Heater is ON";
						String id=Const.ID_DEVICE_OUTLET+""; 
						 String t1= new Timestamp(System.currentTimeMillis()).toString();
	                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Device",id,"Heater is ON","RECORD EVENT FROM SENSOR","");
	                    Thread t = new Thread(updateLog);
	                    t.start();
		                System.out.println("smart outlet device's state is on ");
		                }
		                else
		                {
		                	HeaterState="Heater is OFF";
		                	String id=Const.ID_DEVICE_OUTLET+""; 
		                	 String t1= new Timestamp(System.currentTimeMillis()).toString();
		                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Device",id,"Heater is OFF","RECORD EVENT FROM SENSOR","");
		                    Thread t = new Thread(updateLog);
		                    t.start();
		                    System.out.println("smart outlet device's state is off ");
		                }  
				}
					break;
			case Const.ID_SENSOR_PRESENCE:
				{
					  if(Const.ON == state)
		                {	
						String id=Const.ID_SENSOR_PRESENCE+""; 
						 String t1= new Timestamp(System.currentTimeMillis()).toString();
	                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,"User Present in Home","RECORD EVENT FROM SENSOR","");
	                    Thread t = new Thread(updateLog);
	                    t.start();
		           //     System.out.println("User Present in Home ");
		                }
		                else
		                {
		                	String id=Const.ID_SENSOR_PRESENCE+""; 
		                	 String t1= new Timestamp(System.currentTimeMillis()).toString();
		                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,"User Away from Home","RECORD EVENT FROM SENSOR","");
		                    Thread t = new Thread(updateLog);
		                    t.start();
		                    System.out.println("User Away from Home ");
		                } 
					  
				}
				break;
			case Const.ID_DOOR:
			{
				  if(Const.ON == state)
	                {
					  DoorState="Door is Open";
					String id=Const.ID_DOOR+""; 
					 String t1= new Timestamp(System.currentTimeMillis()).toString();
                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,"Door is Open","RECORD EVENT FROM SENSOR","");
                    Thread t = new Thread(updateLog);
                    t.start();
	           //     System.out.println("User Present in Home ");
	                }
	                else
	                {
	                	DoorState="Door is Closed";
	                	String id=Const.ID_DOOR+""; 
	                	 String t1= new Timestamp(System.currentTimeMillis()).toString();
	                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,"Door is Closed","RECORD EVENT FROM SENSOR","");
	                    Thread t = new Thread(updateLog);
	                    t.start();
	                    System.out.println("User Away from Home ");
	                } 
			}	
			break;
			default:break;
		}
		
		Device_States.put(device_id, state);          //if state changed, update
	}
	
    @Override
    public void change_mode(String mode) throws RemoteException {
        if(mode.equals(Const.USER_MODE_AWAY)){
            userMode=Const.USER_MODE_AWAY;
        }
        if(mode.equals(Const.USER_MODE_HOME)){
            userMode=Const.USER_MODE_HOME;
        }
    }
    
    
    public static void readConfigIPFile()
	{
		 String filename = Const.CONFIG_IPS_FILE;
			String workingDirectory = System.getProperty("user.dir");
			File file = new File(workingDirectory, filename);
			try {
				if (file.createNewFile()) {
					System.out.println("File is created!");
				} else {
					System.out.println("Gate Way Started");
				}
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	 
	        BufferedReader reader = null;   
	        try {     
	            reader = new BufferedReader(new FileReader(file));   
	            String tempString = null;   
	            int line = 1;   
	            while ((tempString = reader.readLine()) != null) {      
	                switch(line)
	                {
		                case 1:
		                	Const.GATEWAY_SERVER_IP = tempString;
		                	break;
		                default:
		                		break;
	                }
	                line++;
	            }
				 System.out.print("ip" + Const.GATEWAY_SERVER_IP);
	            reader.close();   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        } finally {   
	            if (reader != null) {   
	                try {   
	                    reader.close();   
	                } catch (IOException e1) {   
	                }   
	            }   
	        }   
	}
	
	
	public static void runTasks() throws InterruptedException
	{
	
		//iterate check
		while(true)
		{
		    //query state,  the gateway as a client
			 System.out.print("Change Smart Device State, Please enter 1\n");
			 System.out.print("Query Sensor Device State, Please enter 2\n");
			 System.out.print("Enter your option: ");
			 //  open up standard input
			 
			 Scanner in = new Scanner(System.in);
			 int option = in.nextInt();
		      //  read the username from the command-line; need to use try/catch with the
		      //  readLine() method
		    System.out.println("Thanks for the option, " + option);
		    if(1 == option)															//change state;
		    {
				 System.out.print("change Smart Bulb State, Please enter 5\n");
				 System.out.print("change Smart Heater State, Please enter 6\n");
				 
				 Scanner inCommand = new Scanner(System.in);
				 int deviceId = inCommand.nextInt();
				 switch(deviceId)
				 {
					 case 5: 
					 {
						 do{
								 System.out.print("Want to Change to OFF, Please enter 0\n");
								 System.out.print("Want to Change to ON, Please enter 1\n");
								 Scanner inCommd = new Scanner(System.in);
								 int state = inCommd.nextInt();
								 if((state == Const.ON) || (state == Const.OFF))
								 {
								//	 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(true, Const.CLIENT_SMART_BULB_IP,
								//			 Const.SMART_BULB_SENSOR_PORT, deviceId, state, 0);
									 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(true,storeIpAddressSensorsDevices.get(Const.ID_DEVICE_BULB),
											 Const.SMART_BULB_SENSOR_PORT, deviceId, state, 0,  LClockGW, ClockGWVlaue);
									 mThreadChangeSt.start();
									 mThreadChangeSt.join();
									 break;
								 }
								 else
								 {
									 System.out.print("Wrong input, Please input again\n\n");
								 }
						 }while(true);
						 break;
					}
					 case 6: 
					 {
						 do{
							 System.out.print("Want to Change to OFF, Please enter 0\n");
							 System.out.print("Want to Change to ON, Please enter 1\n");
							 Scanner inCommd = new Scanner(System.in);
							 int state = inCommd.nextInt();
							 if((state == Const.ON) || (state == Const.OFF))
							 {
								 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(true, storeIpAddressSensorsDevices.get(Const.ID_DEVICE_OUTLET),
										 Const.HEATER_PORT, deviceId, state, 0,  LClockGW, ClockGWVlaue);
								 mThreadChangeSt.start();
								 mThreadChangeSt.join();
								 break;
							 }
							 else
							 {
								 System.out.print("Wrong input, Please input again\n\n");
							 }
							 }while(true);
						 break;
					 }
					 default: 
					 {
							 System.out.print("Wrong input, Please input again\n\n");
							 break;		 
					 }
				 }
		    }
		    else if(2 == option)								//query state;
		    {

				 System.out.print("Query Temperature State, Please enter 1\n");
				 System.out.print("Query Motion State, Please enter 2\n");
				 System.out.print("Query Door State, Please enter 3 \n");

				 Scanner cmd = new Scanner(System.in);
				 int deviceId = cmd.nextInt();
				 switch(deviceId)
				 {
					 case 1: 
					 {
						 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_TEMPERATURE),
								 Const.TEMP_SENSOR_PORT, deviceId, 0, 0,  LClockGW, ClockGWVlaue);
						 mThreadChangeSt.start();
						 mThreadChangeSt.join();
						 break;
					 }
					 case 2: 
					 {
						 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_MOTION),
								 Const.MOTION_SENSOR_PORT, deviceId, 0, 0,  LClockGW, ClockGWVlaue);
						 mThreadChangeSt.start();
						 mThreadChangeSt.join();
						 break;
					 }
					 case 3: 
					 {
						 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_DOOR),
								 Const.DOOR_PORT, deviceId, 0, 0,  LClockGW, ClockGWVlaue);
						 mThreadChangeSt.start();
						 mThreadChangeSt.join();
						 break;
					 }
					 default: 
					 {
						 System.out.print("Wrong input, Please input again\n\n");
						 break;		 
					 }
				 }
		    }
		    else
		    {
				 System.out.print("Wrong input, Please input again\n\n");
		    }
		}
	}
	
	public static void startThreadRecieve(){
		//read configure IPs from file
		readConfigIPFile();	
		//initialize the logic clock of gw
		LClockGW.push(0);

        //receive report_state,  the gateway as a server  
		MultiThreadReceive mltThRev = new MultiThreadReceive(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);		
		mltThRev.start();
		try {
			mltThRev.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void readTestCaseInput()
	{
		String workingDirectory = System.getProperty("user.dir");
		String TestCaseFile = Const.TEST_INPUT_FILE;
		TestCaseFile = workingDirectory.concat("/"+TestCaseFile);
		System.out.print(TestCaseFile);
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
	 
			br = new BufferedReader(new FileReader(TestCaseFile));
			int lineNumCount = 0;
			while ((line = br.readLine()) != null) {
			        // use comma as separator
				String[] lineInformation = line.split(cvsSplitBy);
				System.out.print(lineNumCount);
				++lineNumCount;
				if(2 >= lineNumCount)
				{
					if(2 == lineNumCount)
					{
						double timestamp = Double.parseDouble(lineInformation[0]);
						timeEventArray.add(timestamp);
						String eventValue = "";
						GatewayEventValueHash.put(timestamp, eventValue);
					}
					continue;
				}
				double timestamp = Double.parseDouble(lineInformation[0]);
				timeEventArray.add(timestamp);
				String eventValue = "";
				if(lineInformation.length >= 4)
				{
					eventValue = lineInformation[3]; 
					System.out.println("lineInformation [timestamp= " + lineInformation[0] 
                            + " , eventValue =" + lineInformation[3]);
				}
				GatewayEventValueHash.put(timestamp, eventValue);
				System.out.println("lineInformation [timestamp= " + lineInformation[0]);
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void runTestCases() throws InterruptedException, IOException
	{		
		TasksOrTestcaseFunction = 1;            //testCase now
		//read configure IPs from file
		readConfigIPFile();	
		//read TestInputCsvFile
		readTestCaseInput();
		
        //receive register at timestamp 0,  the gateway as a server  
		MultiThreadReceive mltThRev = new MultiThreadReceive(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);		
		mltThRev.start();
		mltThRev.join();
		
		//update the time to do next time
		while(!beginRegisterTime0Finish)   //wait to register
		{
			System.out.println("beginRegistering");

		}
			System.out.println("beginRegiste ttttt");

		int currLoop = 1;
		while(true)
		{
		//	System.out.println("beginRegiste");
			//notify sensor to register
			double currtime = timeEventArray.get(currLoop);
			NotifySensorAction(storeIpAddressSensorsDevices.get(Const.ID_SENSOR_TEMPERATURE), Const.TEMP_SENSOR_PORT, currtime, Const.ID_SENSOR_TEMPERATURE);
			NotifySensorAction(storeIpAddressSensorsDevices.get(Const.ID_SENSOR_MOTION), Const.MOTION_SENSOR_PORT, currtime, Const.ID_SENSOR_MOTION);

			while(2 != FinishOneEventSymbol)
			{
	            System.out.println("FinishOneEventSymbol" + FinishOneEventSymbol);
			}
			FinishOneEventSymbol = 0;
			//gateway begin to have action
			String gatewayAction = GatewayEventValueHash.get(currtime);
            if(!gatewayAction.equals(""))
            {
	            System.out.println(gatewayAction);
	            if(!gatewayAction.contains(";"))
	            {
	            	if(gatewayAction.equals(Const.GW_TEMP_ACTION))   //if it's Q(Temp), query temp
	            	{
	            		 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_TEMPERATURE),
								 Const.TEMP_SENSOR_PORT, Const.ID_SENSOR_TEMPERATURE, 1, currtime, LClockGW, ClockGWVlaue);
						 mThreadChangeSt.start();
						 mThreadChangeSt.join();
						 
						 String outValue = "";
						 if(GatewayOutputHash.containsKey(currtime))
				        {
				            String temp = GwServerInterfaceImpl.GatewayOutputHash.get(currtime);
				            try{
				            outValue = temp + "," +Integer.toString(Device_States.get(Const.ID_SENSOR_MOTION));
				            }catch(Exception e){
				            	
				            }
				        }
				        System.out.println(" currtime Temp here" + currtime + "outValue " + outValue);
				               	 //output to the OutputHash file
				        GatewayOutputHash.put(currtime, outValue);
	            	}
	            	else if(gatewayAction.equals(Const.GW_MOTION_ACTION))		//if it's Q(Motion), query Motion
	            	{
	            		MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_MOTION),
								 Const.MOTION_SENSOR_PORT, Const.ID_SENSOR_MOTION, 1, currtime,  LClockGW, ClockGWVlaue);
						 mThreadChangeSt.start();
						 mThreadChangeSt.join();
						 String outValue = "";
						 if(GatewayOutputHash.containsKey(currtime))
				        {
				            String motion = GwServerInterfaceImpl.GatewayOutputHash.get(currtime);
				            outValue = Integer.toString(Device_States.get(Const.ID_SENSOR_TEMPERATURE))+ "," +motion;
				        }
				        System.out.println(" currtime motion here" + currtime + "outValue " + outValue);
				               	 //output to the OutputHash file
				        GatewayOutputHash.put(currtime, outValue);
	            	}
	            }
	            else 			//  Q(Temp) and  Q(Motion) both 
	            {
		           	 MultiThreadRequest mThreadTempSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_TEMPERATURE),
							 Const.TEMP_SENSOR_PORT, Const.ID_SENSOR_TEMPERATURE, 1, currtime,  LClockGW, ClockGWVlaue);
		           	mThreadTempSt.start();
		           	mThreadTempSt.join();
					 
					 MultiThreadRequest mThreadMotionSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_MOTION),
							 Const.MOTION_SENSOR_PORT, Const.ID_SENSOR_MOTION, 1, currtime,  LClockGW, ClockGWVlaue);
					 mThreadMotionSt.start();
					 mThreadMotionSt.join();
	            	
	            }
            }
            else                         // null no action
            {
            	int v1 = Device_States.get(Const.ID_SENSOR_TEMPERATURE);
            	int v2 =  Device_States.get(Const.ID_SENSOR_MOTION);
            	String outValue = Integer.toString(v1) + "," +Integer.toString(v2);
            	GatewayOutputHash.put(currtime, outValue);
            	GatewayOutputArrayStr.add(currtime);
            }
            
            //
            long intervalTime = (long) (timeEventArray.get(currLoop)-timeEventArray.get(currLoop-1));
            TimeUnit.SECONDS.sleep(intervalTime);
            currLoop++;
			if(currLoop == timeEventArray.size())
			{
				System.out.println("Program finished to exit");
				//print
				 Iterator<Map.Entry<Double, String>> iterator = GatewayOutputHash.entrySet().iterator() ;
			        while(iterator.hasNext()){
			            Map.Entry<Double, String> entryEvent = iterator.next();
			            System.out.println("GatewayOutputHash " + entryEvent.getKey() +" : "+ entryEvent.getValue());
			        }     
			      
			        //write file;
					File fout = new File("test-output-cached.txt");
					FileOutputStream fos = new FileOutputStream(fout); 
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					BufferedWriter bwriter = new BufferedWriter(osw); 
					for (int i = 0; i < GatewayOutputArrayStr.size(); i++) {
						if(GatewayOutputHash.containsKey(GatewayOutputArrayStr.get(i)))
						{
							String	OutputString = GatewayOutputArrayStr.get(i) + ":" + GatewayOutputHash.get(GatewayOutputArrayStr.get(i));
							bwriter.write(OutputString);
							bwriter.newLine();
						}
				}
					bwriter.close(); 
					osw.close();
					fos.close();
				break;
			}
				}
	
	}
	
	
	private static void NotifySensorAction(String ClientId, int port, double time, int device_id)
	{
		Registry regs = null;
		try {
				regs = LocateRegistry.getRegistry(ClientId, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(Const.ID_SENSOR_TEMPERATURE == device_id)
		{	
			 RMIDevicesInterfaces stTempObj = null;
			try {
					stTempObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_TEMP);
			} catch (RemoteException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			try {
			 stTempObj.NotifySensorEventAction(time);

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(Const.ID_SENSOR_MOTION == device_id)
		{	
			 RMIDevicesInterfaces stTempObj = null;
			try {
					stTempObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_MOTION);
			} catch (RemoteException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			int notifyRes = 0;
			try {
                stTempObj.NotifySensorEventAction(time);
				System.out.println(device_id + " motion notify result" + notifyRes);

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void  querySensorTestCase(String ClientId, int port, double time, int device_id)
	{
		Registry regs = null;
		try {
				regs = LocateRegistry.getRegistry(ClientId, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(Const.ID_SENSOR_TEMPERATURE == device_id)
		{	
			 RMIDevicesInterfaces stTempObj = null;
			try {
					stTempObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_TEMP);
			} catch (RemoteException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			int tempDegree = 0;
			try {
				tempDegree = stTempObj.query_state(time, device_id);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(device_id + "Query result" + tempDegree);
		}
			 
	}

	@Override
	public void FinishCurrentTimeEventToGateway(int finishOne)
			throws RemoteException {
		// TODO Auto-generated method stub
		FinishOneEventSymbol += finishOne;
		System.out.println("FinishOneEventSymbol value " + FinishOneEventSymbol);
		
	}

	//for TA's test case
	@Override
	public void report_state(int device_id, int state, double time)
			throws RemoteException {
		// TODO Auto-generated method stub
		if(Const.ID_SENSOR_MOTION == device_id)
		{
			ReceivedMotionReport = true;
			Device_States.put(device_id, state);
		}
		
	}

	
	@Override
	public void register(int type, int device_id, String name, String ip,
			Stack<Integer> SendLogicClock, String event)
			throws RemoteException {
		// TODO Auto-generated method stub
		if(Const.TYPE_SENSOR == type)
        {
            if(name.equals(Const.NAME_SENSOR_TEMPERATURE))
            {
                devices_name_id.put(name, Const.ID_SENSOR_TEMPERATURE);          //temperature sensor's id 1
                Device_States.put(Const.ID_SENSOR_TEMPERATURE, 0);  //0 degree of temperature
                storeIpAddressSensorsDevices.put(Const.ID_SENSOR_TEMPERATURE,ip);
                
                System.out.println("Temperature Sensor is Registered at the Gateway");

                if(1 == TasksOrTestcaseFunction)
                {
                	//current time
                	// create a date
                	java.util.Date date= new java.util.Date();
                	Timestamp tStmp = new Timestamp(date.getTime());
	             	String time = tStmp.toString();
	                System.out.println(time);

	        		String cvsSplitBy = ":";
	        		 // use comma as separator
	        		String[] timeStr = time.split(cvsSplitBy);  
	        		String second = timeStr[timeStr.length-1];
	                System.out.println("dddd"+second);
	                
	                double s = System.currentTimeMillis() / 1000000000;
	                System.out.println("tttt"+s);

	              //  double currentRealTime
	                //curent timestamp which is 0 
	                double timeOutputKey = 0;
	                
	                String value = "0,0";						//initialized value, lazy!!!!!
	               	 //output to the OutputHash file
	                GatewayOutputHash.put(timeOutputKey, value);
	                GatewayOutputArrayStr.add(timeOutputKey);
	               beginRegisterTime0Finish = true;
	                
                }
            }
             if(name.equals(Const.NAME_SENSOR_MOTION))
             {
                 devices_name_id.put(name, Const.ID_SENSOR_MOTION);          //motion sensor's id 2
                 Device_States.put(Const.ID_SENSOR_MOTION, Const.NO_MOTION);
                 storeIpAddressSensorsDevices.put(Const.ID_SENSOR_MOTION,ip);//no motion;
             }
             
             if(name.equals(Const.NAME_SENSOR_DOOR))
             {
                 devices_name_id.put(name, Const.ID_DOOR);          //DOOR sensor's id 5
                 Device_States.put(Const.ID_DOOR, Const.CLOSED);
                 storeIpAddressSensorsDevices.put(Const.ID_DOOR,ip);//no DOOR;
             }
             
             if(name.equals(Const.NAME_SENSOR_PRESENCE))
             {
                 devices_name_id.put(name, Const.ID_SENSOR_PRESENCE);          //presence sensor's id 2
                 Device_States.put(Const.ID_SENSOR_PRESENCE, Const.OFF);
                 storeIpAddressSensorsDevices.put(Const.ID_SENSOR_PRESENCE,ip);//off;
             }
             
        }
       else if(Const.TYPE_SMART_DEVICE == type)
        {
             if(name.equals(Const.NAME_DEVICE_BULB))
             {
                 devices_name_id.put(name, Const.ID_DEVICE_BULB);
                 Device_States.put(Const.ID_DEVICE_BULB, Const.OFF);  			//bulb off;
                 storeIpAddressSensorsDevices.put(Const.ID_DEVICE_BULB,ip);
             }
             if(name.equals(Const.NAME_DEVICE_OUTLET))
             {
                 devices_name_id.put(name, Const.ID_DEVICE_OUTLET);
                 Device_States.put(Const.ID_DEVICE_OUTLET, Const.OFF);  		//smart outlet off;
                 storeIpAddressSensorsDevices.put(Const.ID_DEVICE_OUTLET,ip);
             }
        }
		
		//update logic clocks
         MessageTrans.LamportLogicClock(SendLogicClock, LClockGW, event, ClockGWVlaue);
         LClockGW = MessageTrans.LogicClocks;
         ClockGWVlaue = MessageTrans.ClockVlaue;

         System.out.println("The logic clock of registering to Gateway Front-End is "+ LClockGW.toString());
         System.out.println("The event value of Gateway Front-End is "+ ClockGWVlaue.toString());
 
		return;
		
		
	}
	/**
	 * Function to start a new thread to broadcast the result to all other machines
	 * @param s
	 */
		public static void broadcast(String s){
		Thread t = new Thread(new BroadcastResult(s));
		t.start();
	}
		
		public void electionResult(String winner) throws RemoteException {
		
		System.out.println("The election is won by "+ winner);
		System.out.println("The Leader and Time Server is"+ winner);
		
		if(winner.equals("GATEWAY")){
			 long Average=0;
			try{			 
			Thread t = new Thread(new requestTimeBerkleyAlgo());
			t.run();
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Broadcasting the Time to Slaves......");
			long sum=(long) 0;
		    int n = GwServerInterfaceImpl.storeTimeStamps.size();
		    // Iterating manually is faster than using an enhanced for loop.
		    for (int i = 0; i < n; i++)
		        sum += GwServerInterfaceImpl.storeTimeStamps.get(i);
		    // We don't want to perform an integer division, so the cast is mandatory.
		    Average = (((long) sum) / n);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		    Thread t1= new Thread(new BroadcastTimeToSlaves(Average));
		    t1.start();
		    try {
				t1.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    System.out.println("Broadcasting Done and Time offset is adjusted in Slaves");
		}
		
	}
	@Override
	public void report_state(int device_id, int state,
			Stack<Integer> SendLogicClock, String event)
			throws RemoteException {
		// TODO Auto-generated method stub
		switch(device_id)
		{
		case Const.ID_SENSOR_TEMPERATURE:
		{
            System.out.println("temperature sensor' s current temperature is : " + state);
            String id=Const.ID_SENSOR_TEMPERATURE+""; 
            String Temperature="Temerature is"+state; 
            String t1= new Timestamp(System.currentTimeMillis()).toString();
            UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,Temperature,"RECORD EVENT FROM SENSOR","");
            Thread t = new Thread(updateLog);
            t.start();
            
 
			break;
		}
		case Const.ID_SENSOR_MOTION: 
			{
				  if(Const.ON == state)
	                {
	                    if(userMode.equals(Const.USER_MODE_HOME)){
	                        try {
	                            TimerforMotion.main(LClockGW);
	                        } catch (InterruptedException e) {
	                            // TODO Auto-generated catch block
	                            e.printStackTrace();
	                        }
	                    }
	                    if(userMode.equals(Const.USER_MODE_AWAY)){
	                        SendNotification.main();
	                    }
	                    System.out.println("Motion Sensor Current State is : " + state);
	                    String id=Const.ID_SENSOR_MOTION+""; 
	                    String t1= new Timestamp(System.currentTimeMillis()).toString();
	                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,"Motion exists","RECORD EVENT FROM SENSOR","");
	                    Thread t = new Thread(updateLog);
	                    t.start();
	                    System.out.println("motion sensor's is motion yes");
	    				break;
	                   
	                }
	                else
	                {
	                	 String id=Const.ID_SENSOR_MOTION+""; 
	                	 String t1= new Timestamp(System.currentTimeMillis()).toString();
		                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,"Motion does not exists","RECORD EVENT FROM SENSOR","");
		                    Thread t = new Thread(updateLog);
		                    t.start();
	                    System.out.println("motion sensor's is motion no");
	                }
	                break;
			}
			case Const.ID_DEVICE_BULB: 
					{
						if(Const.ON == state)
						{
				 	String id=Const.ID_DEVICE_BULB+""; 
				 	String t1= new Timestamp(System.currentTimeMillis()).toString();
                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Device",id,"Bulb is ON","RECORD EVENT FROM SENSOR","");
                    Thread t = new Thread(updateLog);
                    t.start();
                    System.out.println("bulb device's state is on ");
						}
                else
                {
                	String id=Const.ID_DEVICE_BULB+""; 
                	String t1= new Timestamp(System.currentTimeMillis()).toString();
                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Device",id,"Bulb is OFF","RECORD EVENT FROM SENSOR","");
                    Thread t = new Thread(updateLog);
                    t.start();
                    System.out.println("bulb device's state is off ");
                }          
			break;
					}
		case Const.ID_DEVICE_OUTLET:
				{
				   if(Const.ON == state)
	                {	
					String id=Const.ID_DEVICE_OUTLET+""; 
					String t1= new Timestamp(System.currentTimeMillis()).toString();
                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Device",id,"Heater is ON","RECORD EVENT FROM SENSOR","");
                    Thread t = new Thread(updateLog);
                    t.start();
	                System.out.println("smart outlet device's state is on ");
	                }
	                else
	                {
	                	String id=Const.ID_DEVICE_OUTLET+""; 
	                	String t1= new Timestamp(System.currentTimeMillis()).toString();
	                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Device",id,"Heater is OFF","RECORD EVENT FROM SENSOR","");
	                    Thread t = new Thread(updateLog);
	                    t.start();
	                    System.out.println("smart outlet device's state is off ");
	                }  
				}
				break;
		case Const.ID_SENSOR_PRESENCE:
				{
				  if(Const.ON == state)
	                {	
					String id=Const.ID_SENSOR_PRESENCE+""; 
					String t1= new Timestamp(System.currentTimeMillis()).toString();
                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,"User Present in Home","RECORD EVENT FROM SENSOR","");
                    Thread t = new Thread(updateLog);
                    t.start();
	            //    System.out.println("User Present in Home ");
	                }
	                else
	                {
	                	String id=Const.ID_SENSOR_PRESENCE+""; 
	                	String t1= new Timestamp(System.currentTimeMillis()).toString();
	                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,"User Away from Home","RECORD EVENT FROM SENSOR","");
	                    Thread t = new Thread(updateLog);
	                    t.start();
	                    System.out.println("User Away from Home ");
	                } 
				  
			}
			break;
		case Const.ID_DOOR:
			{
			  if(Const.ON == state)
                {	
				String id=Const.ID_DOOR+""; 
				String t1= new Timestamp(System.currentTimeMillis()).toString();
                UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,"Door is Open","RECORD EVENT FROM SENSOR","");
                Thread t = new Thread(updateLog);
                t.start();
                try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
             //   System.out.println("User Present in Home ");
                }
                else
                {
                	String id=Const.ID_DOOR+""; 
                	String t1= new Timestamp(System.currentTimeMillis()).toString();
                    UpdateLogDatabase updateLog = new UpdateLogDatabase(t1,"Sensor",id,"Door is Closed","RECORD EVENT FROM SENSOR","");
                    Thread t = new Thread(updateLog);
                    t.start();
                    try {
						t.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                //    System.out.println("User Away from Home ");
                } 
			}	
		break;
		default:break;
			}
		
		Device_States.put(device_id, state);          //if state changed, update
			
		//Update logic clock
		System.out.println("Door's event reported= "+event);
		 MessageTrans.LamportLogicClock(SendLogicClock, LClockGW, event, ClockGWVlaue);
         LClockGW = MessageTrans.LogicClocks;
         ClockGWVlaue = MessageTrans.ClockVlaue;
         
         //type 2 log
         int logicClk = LClockGW.peek();
         String logicClockTime = Integer.toString(logicClk);
         
         String id= Integer.toString(device_id);
         
         String devType = "";
         if(device_id <= 4)
         {
        	 devType = "Sensor";		 
         }
         else
         {
             devType = "Device";		 
         }
         
         String inferredResult = "";

         if(ClockGWVlaue.containsKey(Const.EVT_GW_MOTION_MOTION_SENSED) && ClockGWVlaue.containsKey(Const.EVT_DOOR_OPEN_SENSED))
         {
	        
	         if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) < ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
	        		 Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.ON )
	         {
	        	 inferredResult = "User left home";
	         }
	         else  if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) < ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
	        		 Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.OFF )
	         {
	        	 inferredResult = "Intruder left home";
	
	         }
	         else  if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) > ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
	        		 Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.ON )
	         {
	
	        	 inferredResult = "User came home";
	         
	         }
	         else  if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) > ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
	        		 Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.OFF )
	         {
	        	 inferredResult = "Intruder entered home";
	         }
	         else
	         {
	        	 return;
	         }
	         UpdateLogDatabase updateLog = new UpdateLogDatabase(logicClockTime,devType,id,event,Const.LOGTYPE_PART23,inferredResult);
	         Thread t = new Thread(updateLog);
	         t.start();
   
	         //  System.out.println("The logic clock of reporting to Gateway Front-End is "+ LClockGW.toString());
	         System.out.println("The event value of Gateway Front-End is "+ ClockGWVlaue.toString());
         }      
       
        return; 
	}
	/**
	 * Helper method for Ring Algorithm.
	 * Used to iterate through the token and find the machine with highest id and broacast the result.
	 * @param s
	 */
		public static void helperForRingAlgorithm(String s){
		System.out.println("The string is" +s);
		String[] s1= s.split("delim");
		Set<String> mySet = new HashSet<String>(Arrays.asList(s1));
		Iterator<String> ite = mySet.iterator();
		int leaderNode =0;
		Iterator iter =mySet.iterator();
		for (Iterator<String> flavoursIter = mySet.iterator(); flavoursIter.hasNext();){
		     // System.out.println(flavoursIter.next());
		      Integer foo = Integer.parseInt(flavoursIter.next());
		     if(foo >= leaderNode)
				leaderNode =foo;	
		    }
		switch(leaderNode)
		{
	case Const.ID_DATABASE:
		System.out.println("Data Base is the Leader");
		broadcast("DATABASE");
		break;
	case Const.ID_DEVICE_BULB:
		System.out.println("Smart Bulb is the Leader");
		broadcast("SMARTBULB");
		break;
	case Const.ID_DEVICE_OUTLET:
		System.out.println("Smart Heater is the Leader");
		broadcast("SMARTHEATER");
		break;
	case Const.ID_DOOR:
		System.out.println("Door is the Leader");
		broadcast("DOOR");
		break;
	case Const.ID_GATEWAY:
		System.out.println("Gate way is the Leader");
		broadcast("GATEWAY");
		break;
	case Const.ID_SENSOR_MOTION:
		System.out.println("Motion Sensor is the Leader");
		broadcast("MOTION SENSOR");
		break;
	case Const.ID_SENSOR_TEMPERATURE:
		System.out.println("Temperature Sensor is the Leader");
		broadcast("TEMPERATURE SENSOR");
		break;
	default:
		System.out.println("Unable to find the Leader");
		break;
		}
	}
		/**
		 * Function to help perform ring algorithm.
		 *     The  Initiator variable in this method acts as a flag for ring algorithm.
		 *     The initial value of the Initiator is false. so it enters  the second if condition when this method is invoked by itself.
		 *     when the token travels as a ring and comes back it will invoke the first if statement and hence declaring the result.
		 */	
	public void ringAlgorithm(String s) throws RemoteException {
		
		if(Initiator){
			helperForRingAlgorithm(s);
			
		}
		if(!Initiator){
		//if(GwServerInterfaceImpl.count==0){
			
			String appendID = s+"delim"+Const.ID_GATEWAY;
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(Const.CLIENT_SENSOR_TMPERATURE_IP,Const.TEMP_SENSOR_PORT);
		} catch (RemoteException e) {
		
		}
			RMIDevicesInterfaces stSensorObj = null;
			try {
				stSensorObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_TEMP);
			} catch (RemoteException | NotBoundException e) {
				
				String s4= appendID+"delim"+Const.ID_GATEWAY;
				helperForRingAlgorithm(s4);
			
			}
			try {
				Initiator=!Initiator;
				stSensorObj.ringAlgorithm(appendID);
			} catch (NullPointerException | RemoteException e) {

			}
		}
		
	}
	/**
	 * Function to be remotely invoked by the leader to provide the current time to the leader
	 */
	public long provideTimeStamp() throws RemoteException{
		
		return System.currentTimeMillis();
	}
	/**
	 * Function to adjust the offset of the time using the time recived remotely by the leader.
	 */
	public void setOffsetTimeVariable(Long time){

			GwServerInterfaceImpl.offsetValuefromTimeServer= (int) (System.currentTimeMillis()-time);
			System.out.println("The offsetvalue for time is set by Using TimeStamp from Master to :"+ GwServerInterfaceImpl.offsetValuefromTimeServer );
	}
    private static void readLab2GwTestInput()
    {
    	String workingDirectory = System.getProperty("user.dir");
		String TestCaseFile = Const.LAB2_TEST_INPUT_FILE;
		TestCaseFile = workingDirectory.concat("/"+TestCaseFile);
		System.out.print(TestCaseFile);
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
	 
			br = new BufferedReader(new FileReader(TestCaseFile));
			int lineNumCount = 0;
			while ((line = br.readLine()) != null) {
			        // use comma as separator
				String[] lineInformation = line.split(cvsSplitBy);
				++lineNumCount;
				if(1 >= lineNumCount)
				{
					continue;
				}
				double timestamp = Double.parseDouble(lineInformation[0]);
				String queryVal = "";
				if(lineInformation.length >= 5)
				{
					queryVal = lineInformation[4]; 
					System.out.println("\n lineInformation [timestamp= " + lineInformation[0] 
                            + " , eventValue =" + lineInformation[4]);
				}
				GatewayEventValueHash.put(timestamp, queryVal);
				//System.out.println("lineInformation [timestamp= " + lineInformation[0]);
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
    }
    
    
	
	public static void runGatewayQuery() throws InterruptedException
	{
		//read test input
		readLab2GwTestInput();
		 //timer//
		double lastTimeStamp = 0;
		  for (Double time : GatewayEventValueHash.keySet()) {
		      String qryAction = GatewayEventValueHash.get(time);
		      
		      String split = ";";
		      //gateway begin to have action
	          if(!qryAction.equals(""))
	          {
	            	System.out.println(qryAction);
	  	            if(!qryAction.contains(";"))
	  	            {
	  	            	
	  	            	if(qryAction.equals(Const.GW_DOOR_QUERY_ACTION))   //if it's Q(Door), query door
		            	{
		            		 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_DOOR),
									 Const.DOOR_PORT, Const.ID_DOOR, 2, time, LClockGW, ClockGWVlaue);
							 mThreadChangeSt.start();
							 mThreadChangeSt.join();
	 
							 
		            	}
		            	else if(qryAction.equals(Const.GW_MOTION_ACTION))		//if it's Q(Motion), query Motion
		            	{
		            		MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_MOTION),
									 Const.MOTION_SENSOR_PORT, Const.ID_SENSOR_MOTION, 2, time,  LClockGW, ClockGWVlaue);
							 mThreadChangeSt.start();
							 mThreadChangeSt.join();

		            	}
		            }
		            else 					//  Q(Door) and  Q(Motion) both 
		            {
			           	 MultiThreadRequest mThreadTempSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_DOOR),
								 Const.DOOR_PORT, Const.ID_DOOR, 2, time,  LClockGW, ClockGWVlaue);
			           	mThreadTempSt.start();
			           	mThreadTempSt.join();
						 
						 MultiThreadRequest mThreadMotionSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_MOTION),
								 Const.MOTION_SENSOR_PORT, Const.ID_SENSOR_MOTION, 2, time,  LClockGW, ClockGWVlaue);
						 mThreadMotionSt.start();
						 mThreadMotionSt.join();
						 
						 
		            }
	  	        }
	          
	          Integer interVal = (int)(time-lastTimeStamp)*1000;
		      //wait time;
		      MessageTrans.waittimeInterval(interVal);
		      
		      lastTimeStamp = time;
		      
		  }
		
		

	}
	/**
	 * Function to set flag when the election algorithm is completed so that after election is completed we can 
	 *     perform further processing for accesing the input file.
	 *     the process will be buzy waiting till election completes and FlagClockSynchronizationFinished becomes true.
	 */
	@Override
	public void setFlagClockSync() throws RemoteException {
     System.out.println("FlagClockSynchronizationFinished changed");

		FlagClockSynchronizationFinished=true;
		
	}

@Override
	public void takeControlOfDevices(int type, String name, String ip_Address)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	public void makeDataConsistentHelper(int state,int device_id) throws RemoteException {


    Registry regs = null;
    try {
        regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP_REP, Const.GATEWAY_PORT_REP);
    } catch (RemoteException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    GatewayAllInterfaces gtwy=null;
    
    try {
        gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY_REP);
        gtwy.makeDataConsistent(state,device_id);  
    } catch (Exception e) {
        // TODO Auto-generated catch block
        System.out.println("exception");
        e.printStackTrace();
    }	
	
	
}	
public void makeDataConsistent(int state,int device_id) throws RemoteException {

	switch(device_id){
	case Const.ID_SENSOR_TEMPERATURE:
		TeperatureState=Integer.toString(state);
		System.out.println("Updated Tempearature State for consistency to...."+TeperatureState);
		break;
	case Const.ID_SENSOR_MOTION:
		if(state==1){
		MotionState="Motion is present";
		System.out.println("Updated Motion State for consistency to...."+MotionState);
		}
		if(state==0){
		MotionState="Motion is Absent";	
		System.out.println("Updated Motion State for consistency to...."+MotionState);
		}
		break;
	case Const.ID_DEVICE_OUTLET:
		if(state==1){
		HeaterState="Heater is ON";
		System.out.println("Updated Heater State for consistency to...."+HeaterState);
		}
		if(state==0){
		HeaterState="Heater is OFF";	
		System.out.println("Updated Heater State for consistency to...."+HeaterState);
		}
		break;
	case Const.ID_DEVICE_BULB:
		if(state==0){
		BulbState="Bulb is OFF";
		System.out.println("Updated Bulb State for consistency to...."+BulbState);
		}
		if(state==1){
		BulbState="Bulb is ON";
		System.out.println("Updated Bulb State for consistency to...."+BulbState);
		}
		break;
	case Const.ID_DOOR:
		if(state==0){
		DoorState="Door is closed";
		System.out.println("Updated Door State for consistency to...."+DoorState);
		}
		if(state==1){
		DoorState="Door is Open";
		System.out.println("Updated Door State for consistency to...."+DoorState);
		}
		break;
		default:
		break;
	}	
	
}	
	

/*
 * do fault tolerant
 */
public static void FaultTolerantTask()
{
	System.out.println("Fault Tolerant Task starts");
	//add all sensors and devices into this collection
	for(int i = Const.ID_SENSOR_TEMPERATURE; i < Const.ID_DEVICE_OUTLET; i++)
	{
		if(i == Const.ID_SENSOR_PRESENCE)         //don't need to include presence here, used in lab2 only
		{
			continue;
		}
		AllSensorsDevice_Sets.add(i);
	}
	
	ExecutorService executorService = Executors.newSingleThreadExecutor();

	FutureTask<Boolean> FTDetect = new FutureTask<Boolean>(
			new Callable<Boolean>(){
		        public Boolean call() throws Exception {
		        	Thread.sleep(5000);		//fault tolerant execuation after 5s 
		        	GwServerInterfaceImpl.DetectGateWayReplicaAlive();        //detect whether replica crashes
		        	return true;
		        }
			});
	
	FutureTask<Boolean> FTHeartBeat = new FutureTask<Boolean>(
			new Callable<Boolean>(){
		        public Boolean call() throws Exception {
		        	Thread.sleep(5000);		//fault tolerant execuation after 5s 
		        	PeriodicalSendHeartBeatToGwReplica();		//send heartbeat message to gateway replica for gw_replica to detect gw 
		        	return true;
		        }
			});
	
	 ExecutorService executor = Executors.newFixedThreadPool(2);
	 executor.execute(FTDetect);
	 executor.execute(FTHeartBeat);

	 if(FTHeartBeat.isDone() && FTDetect.isDone())
	 {
		 System.out.println("Done");
		 executor.shutdown();  //shutdown services
	 }
	 
}
/*
 *  periodically lookup gateway replica rmi and send message to gateway replica
 */
public static void PeriodicalSendHeartBeatToGwReplica()
{
	System.out.println("Enter PeriodicalSendHeartBeatToGwReplica");
	
	Runnable ControlRunnable = new Runnable() {
	    public void run() {
	    	 System.out.println("Enter 1 to cause this gateway fault/crash(not sending hearbeat); Enter 0 to recover");
	    	  Scanner in1 = new Scanner(System.in);
	          if(in1.nextLine().equals("1"))
	          {
	        	  FLAG_TEST_FAULT_CONTROL = 1;
	          }
	          else
	          {
	        	  FLAG_TEST_FAULT_CONTROL = 0;
	          }
	    }
	};
	
	Runnable SendRunnable = new Runnable() {
	    public void run() {
	    	if(0 == FLAG_TEST_FAULT_CONTROL)
	    	{
	    		SendHeartBeatMsgToGwReplica();
	    	}	
	    }
	};
	
	
	ScheduledExecutorService HeartExec = Executors.newScheduledThreadPool(2);
	HeartExec.scheduleAtFixedRate(SendRunnable, 0, Const.FT_HEART_INTEVAL, TimeUnit.SECONDS);
	HeartExec.scheduleAtFixedRate(ControlRunnable, 0, Const.FT_HEART_INTEVAL*3, TimeUnit.SECONDS);

}

/*
 * lookup gateway replica rmi and send message to gateway replica
 */
public static void SendHeartBeatMsgToGwReplica()
{
//	System.out.println(" GATEWAY_SERVER_IP_REP ttttt");
	Registry regs = null;
	try {
	    regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP_REP, Const.GATEWAY_PORT_REP);
	} catch (RemoteException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	GatewayAllInterfaces gtwy=null;
	
	try {
	    gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY_REP);
	   // gtwy.register(type, name,ipAddress);
	    gtwy.HeartMessage(Const.FT_LIVE_HEART_MSG);

	
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    System.out.println("exception");
	    e.printStackTrace();
	}

}

/*
* detect Gateway is live or not
* every Const.HEART_INTEVAL timer to check the state of the gateway 
* 
*/
public static void DetectGateWayReplicaAlive()
{
	
	Runnable DetectRunnable = new Runnable() {
	    public void run() {
	    	
	    	//think gateway crash if HEART_INTEVAL*TIMES_NO_HEART times not received updated heartbeat
	    	if(receiveHeartBeatCountCurrent >= Const.FT_TIMES_NO_HEART)
	    	{
	    		//if crash, how to do?
	    		receiveHeartBeatCountCurrent = 0;
	    		GW_STATE_REP = false;
	    		//replica crashes, gateway take over the sensor and device's responsibility
	    		System.out.println(" Detect GateWay Replica is crashed, needs to take over gateway replica");

	    		BroadCastSensorsDevicesFaultTask();
	    	}
	    	receiveHeartBeatCountCurrent++;
	    //	System.out.println("Enter receiveHeartBeatCountCurrent = "+receiveHeartBeatCountCurrent);

	   
	    }
	};
	
	ScheduledExecutorService HeartExec = Executors.newScheduledThreadPool(1);
	
	HeartExec.scheduleAtFixedRate(DetectRunnable, 0, Const.FT_HEART_INTEVAL, TimeUnit.SECONDS);
	
}




@Override
public void HeartMessage(String message) throws RemoteException {
	// TODO Auto-generated method stub
	//deal with message received from gateway 
	if(message.equals(Const.FT_LIVE_HEART_MSG))
	{			
		System.out.println("receive gateway replica's HeartBeat Message");
		
		GW_STATE_REP = true;
		//receiveHeartBeatCountOld = receiveHeartBeatCountCurrent;
		receiveHeartBeatCountCurrent = 0;
	}
}

/*
 * broadcast task every sensor and device that is communicating with the crashed gw replica
 */
private static void BroadCastSensorsDevicesFaultTask()
{
	Thread trd = new Thread(){
		int port;
		String ipAddress;
		String Lookup;
		String catageory="GATEWAY";
	    public void run(){
	    	
	    Iterator<Integer> iterator = AllSensorsDevice_Sets.iterator(); 
	    // check values
	    while (iterator.hasNext()){
	    	   int id = iterator.next();
	   	    if(!storeIpAddressSensorsDevices.containsKey(id))
	   	    {
	   	    	System.out.println("id = " +id);
	   	    	System.out.println("ipAddress = " +GwServerInterfaceImpl.processRecord.get(id));

	    	 	switch(id)
	    		{
		    		case Const.ID_DEVICE_BULB:
		    			port=Const.SMART_BULB_SENSOR_PORT;
		    			Lookup=Const.STR_LOOKUP_SMART_BULB;
		    			catageory="DEVICE";
		    			ipAddress = GwServerInterfaceImpl.processRecord.get(id);
		    			
		    			break;
		    		case Const.ID_SENSOR_TEMPERATURE:
		    			port=Const.TEMP_SENSOR_PORT;
		    			Lookup=Const.STR_LOOKUP_TEMP;
		    			catageory="SENSOR";
		    			ipAddress = GwServerInterfaceImpl.processRecord.get(id);
		    			
		    			break;
		    			
		    		case Const.ID_DEVICE_OUTLET:
		    			port=Const.HEATER_PORT;
		    			Lookup=Const.STR_LOOKUP_HEATER;
		    			catageory="DEVICE";
		    			ipAddress = GwServerInterfaceImpl.processRecord.get(id);
		    			break;
		    		case Const.ID_SENSOR_MOTION:
		    			port=Const.MOTION_SENSOR_PORT;
		    			Lookup=Const.STR_LOOKUP_MOTION;
		    			catageory="SENSOR";
		    			break;
		    		case Const.ID_DOOR:
		    			System.out.println("came into door");
		    			port=Const.DOOR_PORT;
		    			Lookup=Const.STR_LOOKUP_DOOR;
		    			catageory="SENSOR";
		    			ipAddress = GwServerInterfaceImpl.processRecord.get(id);
		    			break;
		    		default:
		    			break;
	    		}
	    	 	//notify corresponding's sensors and devices gateway replica crashed
    			NotifySensorsDevicesFT(Const.ID_GATEWAY_REP,ipAddress,port,Lookup,catageory);
	   	    }
    	   }
	    }
	  };
	  trd.start();
}

/*
 * Notify sensors and devices
 */
public static void NotifySensorsDevicesFT(int CrashId, String ipAddress, int port,String Lookup, String catageory)
{
	
	if(catageory.equals("SENSOR"))
	{
		Registry regs = null;
		try {
			regs = LocateRegistry.getRegistry(ipAddress, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 RMIDevicesInterfaces stSensorObj = null;
		try {
			stSensorObj = (RMIDevicesInterfaces)regs.lookup(Lookup);
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println("The Host is Dead");
		}
	
		try {
			///////////////
			stSensorObj.NotifySensorsDevicesCrashed(CrashId);
			
		} catch (NullPointerException | RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}	
	}
	else if(catageory.equals("DEVICE"))
	{
		Registry regs = null;
		try {
			regs = LocateRegistry.getRegistry(ipAddress, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SmartCtrlInterfaces stDeviceObj = null;
		try {
			stDeviceObj = (SmartCtrlInterfaces)regs.lookup(Lookup);
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//System.out.println("The Host is Dead");
		}
	
		try {
			///////////////
			stDeviceObj.NotifySensorsDevicesCrashed(CrashId);
		} catch (NullPointerException | RemoteException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
		}	
	}
  }
}

