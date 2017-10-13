/**
 * Temperature Sensor to Report the current temperature
 */
package com.tempeSensorPkg;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.Set;

import com.DevicesInterfaces.RMIDevicesInterfaces;
import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;
import com.GatewayInterface.MessageTrans;
import com.SmartCtrlIntfPkg.SmartCtrlInterfaces;


public class tempeSensorImpl implements RMIDevicesInterfaces,Runnable {
    private static int currTemp=0;  // Static Variable to store the Temperature value 
    
    static HashMap<Integer,String> processRecord = new HashMap<Integer,String>();
    private HashSet<String> broadcastSet = new HashSet<String>();
    public static String ipAddress="localhost";
    private static LinkedHashMap<Double, Integer> tempEventValueHash = new LinkedHashMap<Double, Integer>();//store the test input event action 
    private static ArrayList<Double> timeEventArray = new ArrayList<Double>();
    public static double currentTempTimeStamp = 0;
    public static boolean startNextEventAction = false;
    
    private static HashMap<Integer, Boolean> RecvMessage = new HashMap<Integer, Boolean>();           //record from which Process received message
    private static HashMap<Integer, ArrayList<Integer> > bufferVector = new HashMap<Integer, ArrayList<Integer> >();
    
    private static HashMap<String, Integer> ClockValueTemp = new HashMap<String, Integer>();
    private static Stack<Integer> LClockTemp = new Stack<Integer>();   		//lamport clock of temp
    
	 public static boolean IWON=true;
    public static boolean OK=false;
    public static boolean ElectionPerformed=false;
    public static boolean conditionVariable=true;
    public static boolean Initiator=false;
    public static int offsetValuefromTimeServer=0;
    public static ArrayList<Long> storeTimeStamps = new ArrayList<Long>();
	private static boolean FlagClockSynchronizationFinished=false;
	
	 //////////////lab3
    private static int AssignedGWOrReplicas = Const.ID_GATEWAY;          //ID_GATEWAY or ID_GATEWAY_REP; need to assign this value at load balance stage?
    
    
	@Override
	/**
	 * Function to set flag when the election algorithm is completed so that after election is completed we can 
	 *     perform further processing for accesing the input file.
	 *     the process will be buzy waiting till election completes and FlagClockSynchronizationFinished becomes true.
	 */
	public void setFlagClockSync() throws RemoteException {
		FlagClockSynchronizationFinished=true;
	}	
/**
 * query_state(int device_id)   is used to be accessed remotely by the Gateway
 */
    public int query_state(int device_id) throws RemoteException {
        if(device_id==Const.ID_SENSOR_TEMPERATURE){
            System.out.println("The Current Temperature is"+currTemp);
            return currTemp;
        }
        System.out.println("Function called with Wrong device ID");
        return -1;
    }
    /**
     * report(int cur)   is Helper Method to report the current temperature to the Gateway 
     * @param cur
     */
    public static void report(int cur){

        Registry regs = null;
        try {
            regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        GatewayAllInterfaces gtwy=null;
        
        try {
            gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
           // gtwy.report_state(Const.ID_SENSOR_TEMPERATURE, cur);
           // gtwy.report_state(Const.ID_SENSOR_TEMPERATURE, cur, VectorClockTemp);
            LClockTemp.push(LClockTemp.peek()+1);      //local event add 1
            ClockValueTemp.put(Const.EVT_TEMP_CHANGED, LClockTemp.peek());
            gtwy.report_state(Const.ID_SENSOR_TEMPERATURE, cur, LClockTemp, Const.EVT_GW_TEMP_CHANGE_SENSED);
        
        	
        } catch (Exception e) {
            System.out.println("exception");
            e.printStackTrace();
        }
    }
   /**
    * readConfigIPFile()   is Helper Method to Read the IP address from the Configuration file
    */
    public static void readConfigIPFile(){
        String filename = Const.CONFIG_IPS_FILE;
        String workingDirectory = System.getProperty("user.dir");
        File file = new File(workingDirectory, filename);
       // System.out.println("Final filepath : " + file.getAbsolutePath());
        try {
            if (file.createNewFile()) {
                System.out.println("File is created!");
            } else {
                System.out.println("Read Gateway IP Address from Configuration file!");
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
    /**
     * reg(int type, String name)   is Helper Method to allow the Temperature Sensor to register at Gateway
     *    calls the register method on Gateway Remotely
     */
    public static void reg(int type, String name){
    	try {
			String s=InetAddress.getLocalHost().toString();
			String[] ip=s.split("/"); 
			ipAddress=ip[ip.length-1];
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Registry regs = null;
        try {
            regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        GatewayAllInterfaces gtwy=null;
        try {
            gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
           // gtwy.register(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE,ipAddress);
            LClockTemp.push(LClockTemp.peek()+1);      //local event add 1
            ClockValueTemp.put(Const.EVT_MOTION_REGISTER, LClockTemp.peek());
            gtwy.register(Const.TYPE_SENSOR, Const.ID_SENSOR_TEMPERATURE, Const.NAME_SENSOR_TEMPERATURE,ipAddress,LClockTemp, Const.EVT_TEMP_REGISTER);
            
        } catch (Exception e) {
            System.out.println("exception");
            e.printStackTrace();
        }  
    }
    public static void regOver(int type, String name){
    	try {
			String s=InetAddress.getLocalHost().toString();
			String[] ip=s.split("/"); 
			ipAddress=ip[ip.length-1];
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Registry regs = null;
        try {
            regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        GatewayAllInterfaces gtwy=null;
        try {
            gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
           gtwy.register(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE,ipAddress);
     //       LClockTemp.push(LClockTemp.peek()+1);      //local event add 1
     //       ClockValueTemp.put(Const.EVT_MOTION_REGISTER, LClockTemp.peek());
     //       gtwy.register(Const.TYPE_SENSOR, Const.ID_SENSOR_TEMPERATURE, Const.NAME_SENSOR_TEMPERATURE,ipAddress,LClockTemp, Const.EVT_TEMP_REGISTER);
            
        } catch (Exception e) {
            System.out.println("exception");
            e.printStackTrace();
        }  
    }
    /**
     * CreateRegisterForGatewayLookup()   is Helper Method to create RMI registry at Temperature sensor.  
     */
    private static void CreateRegisterForGatewayLookup()
    {
        tempeSensorImpl htobj = new tempeSensorImpl();
        try{
            RMIDevicesInterfaces stub = (RMIDevicesInterfaces) UnicastRemoteObject.exportObject(htobj,0);
            Registry reg;
            try{
                reg = LocateRegistry.createRegistry(Const.TEMP_SENSOR_PORT);       //heater port 1099 here;
                System.out.println("Temp Sensor java RMI registry created.");
            }
            catch(Exception e){
                System.out.println(" Temp sensor Using existing registry");
                reg = LocateRegistry.getRegistry();
            }
            reg.rebind(Const.STR_LOOKUP_TEMP, stub);
        }catch(RemoteException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * runTasks() is Helper Method to run in User interactive Mode
     */
    private static void runTasks()
    {
			
         while(true){
         Scanner in = new Scanner(System.in);
         System.out.println("Enter Current Temprature");
         currTemp = Integer.parseInt(in.nextLine());
         
         Scanner in1 = new Scanner(System.in);
         System.out.println("Need to Report the State Enter Y or N");
         if(in1.nextLine().equals("Y")||in1.nextLine().equals("y") ){
        	 report(currTemp);
        	
         }
         }
    }
    /**
     * readTestInput() is Helper Method to read the input for test cases given by the test input file
     */
    private static void readTestInput()
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
					}
					continue;
				}
				double timestamp = Double.parseDouble(lineInformation[0]);
				timeEventArray.add(timestamp);
				int value = Integer.parseInt(lineInformation[1]);
				tempEventValueHash.put(timestamp, value);
				System.out.println("lineInformation [code= " + lineInformation[0] 
	                                 + " , name=" + lineInformation[1] + "]");
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
    /**
     * NotifyGatewayFinish() is Helper function to indicate the Gateway that it had updated temperature value.
     * So that the Gateway will be eligible to query
     */
    public static void NotifyGatewayFinish(){
        Registry regs = null;
        try {
            regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        GatewayAllInterfaces gtwy=null; 
        try {
            gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
            gtwy.FinishCurrentTimeEventToGateway(1);
        } catch (Exception e) {
            System.out.println("exception");
            e.printStackTrace();
        }
    }
/**
 * runTestCases() is Helper function to run the test cases from an input file Without user interaction. 
 */
	private static void runTestCases()
	{		
    	readConfigIPFile();
    	
    	readTestInput();
    	
    	CreateRegisterForGatewayLookup();
        if(0 == currentTempTimeStamp)
        {
            System.out.println("currentTempTimeStamp tattat" + currentTempTimeStamp);
            reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE);
        }
        
        while(true)
        {
           System.out.println("currentTempTimeStamp ttad" + currentTempTimeStamp);

        	if(startNextEventAction)
        	{
	            System.out.println("startNextEventAction dd" + startNextEventAction);

	        	//report this current time event's temperature value;
	        	currTemp = tempEventValueHash.get(currentTempTimeStamp);
        		startNextEventAction = false;
        		//tell gateway to finish current event action
        		tempeSensorImpl r= new tempeSensorImpl(); 
                Thread t = new Thread(r);
                t.start();
        		if(currentTempTimeStamp == timeEventArray.get(timeEventArray.size()-1))
        		{
        			break;
        		}
        		
        	}
	    }
	}
	/**
	 * query_state(double time, int device_id) is remotely accessed by the Gateway to get the time stamp value.
	 */
	public int query_state(double time, int device_id) throws RemoteException {
		 if(device_id==Const.ID_SENSOR_TEMPERATURE){
	            System.out.println("The Current Temperature is"+tempEventValueHash.get(time));
	            return currTemp;
	        }
	        System.out.println("Function called with Wrong device ID");
			return -1; 
	}
	
/**
 *  Method to be Remotely Accessed by the Gateway to provide the Logical clock ticking.
 *  This is done in a new thread to avoid Blocking. 
 */
	@Override
	public void NotifySensorEventAction(double time) throws RemoteException {
		NotifySensorTemp n=new NotifySensorTemp(time);
		Thread nwThread= new Thread(n);
		nwThread.start();	}
	/**
	 * Method to create a seperate thread to run the NotifyGatewayFinish() method.
	 */
	public void run() {
		NotifyGatewayFinish(); 
	}
	/**
	 * This method creates is used to be accessed by the leader to inform that it is the leader.
	 *       If the method is accessed by itself remotely then we can start the time sever from this method.
	 */
	public void electionResult(String winner) throws RemoteException {
		
		System.out.println("The election is won by "+ winner);
		System.out.println("The Leader and Time Server is"+ winner);
		
		if(winner.equals("TEMPERATURE SENSOR")){
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
		    int n = tempeSensorImpl.storeTimeStamps.size();
		    // Iterating manually is faster than using an enhanced for loop.
		    for (int i = 0; i < n; i++)
		        sum += tempeSensorImpl.storeTimeStamps.get(i);
		    // We don't want to perform an integer division, so the cast is mandatory.
		    long Average = (((long) sum) / n);
		    
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
	/**
	 * Function to maintain a record of all the process available in the distributed system 
	 *    this becomes handy when broadcasting the elction result
	 */
	public static void UpdateallProcessInformationAvailableInSystem(){
		processRecord.put(Const.ID_DATABASE, Const.DATABASE_IP);
		processRecord.put(Const.ID_DOOR, Const.DOOR_IP);
		processRecord.put(Const.ID_DEVICE_BULB, Const.CLIENT_SMART_BULB_IP);
		processRecord.put(Const.ID_DEVICE_OUTLET, Const.CLIENT_SMART_HEATER_IP);
		processRecord.put(Const.ID_GATEWAY, Const.GATEWAY_SERVER_IP);
		processRecord.put(Const.ID_SENSOR_MOTION,Const.CLIENT_SENSOR_MOTION_IP);
		processRecord.put(Const.ID_SENSOR_TEMPERATURE,Const.CLIENT_SENSOR_TMPERATURE_IP);
	}
	
	
	 
	@Override
	public MessageTrans query_state(int device_id, Stack<Integer> SendLogicClock)
			throws RemoteException {
		// TODO Auto-generated method stub
		 if(device_id==Const.ID_SENSOR_TEMPERATURE)
		 {
			   
				MessageTrans msg = new MessageTrans();
				//	VectorClockDoor = MessageTrans
				msg.QueryStateResult = currTemp;
		        msg.LogicClocks = LClockTemp;

			    msg.RecvEvent = Const.EVT_GW_TEMP_CHANGE_SENSED;

		        System.out.println("The vector clock of door after query_state is "+ LClockTemp.toString());
		        return msg;
	            
	        }
		
		return null;
	}
	/**
	 * Function to start a new thread to broadcast the result to all other machines
	 * @param s
	 */
	public static void broadcast(String s){
		Thread t = new Thread(new BroadcastResult(s));
		t.start();
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

			tempeSensorImpl.offsetValuefromTimeServer= (int) (System.currentTimeMillis()-time);
			System.out.println("The offsetvalue for time is set by Using TimeStamp from Master to :"+ tempeSensorImpl.offsetValuefromTimeServer );
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
	@Override
	public void ringAlgorithm(String s) throws RemoteException {
		
		
		if(Initiator){
			helperForRingAlgorithm(s);
		}
		if(!Initiator){
		//	if(tempeSensorImpl.count==0){
	
			String appendID = s+"delim"+Const.ID_SENSOR_TEMPERATURE;
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(Const.CLIENT_SENSOR_MOTION_IP,Const.MOTION_SENSOR_PORT);
		} catch (RemoteException e) {

		}
			RMIDevicesInterfaces stSensorObj = null;
			try {
				stSensorObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_MOTION);
			} catch (RemoteException | NotBoundException e) {
				String s4= appendID+"delim"+Const.ID_DEVICE_OUTLET;
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
	  * Main method is used to select the operation to be performed by using command line arguments    
	  * @param args
	  * @throws InterruptedException
	  */	
	public static void main(String[] args) throws RemoteException {
		
		UpdateallProcessInformationAvailableInSystem();
		
  		if((args.length==1) && args[0].equals(Const.CONFIG_IPS_FILE))
		{  
	  			 readConfigIPFile();
				  regOver(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE);
			         CreateRegisterForGatewayLookup(); 
			         
			         while(true){
			        	System.out.println("Report to Gateway or Gateway Replica enter Teperature ....");
			        	Scanner sc = new Scanner(System.in);
						String i = sc.next();
						int tmpe = Integer.parseInt(i);
						 Registry regs = null;
					        try {
					            regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);
					        } catch (RemoteException e) {
					            e.printStackTrace();
					        }
					        GatewayAllInterfaces gtwy=null;
					        
					        try {
					            gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
					            gtwy.report_state(Const.ID_SENSOR_TEMPERATURE, tmpe);
					           // gtwy.report_state(Const.ID_SENSOR_TEMPERATURE, cur, VectorClockTemp);
					          //  LClockTemp.push(LClockTemp.peek()+1);      //local event add 1
					          //  ClockValueTemp.put(Const.EVT_TEMP_CHANGED, LClockTemp.peek());
					          //  gtwy.report_state(Const.ID_SENSOR_TEMPERATURE, cur, LClockTemp, Const.EVT_GW_TEMP_CHANGE_SENSED);
					        
					        	
					        } catch (Exception e) {
					            System.out.println("exception");
					            e.printStackTrace();
					        }
						System.out.println("reported");
			         }
			        
			}
			
			if(args.length==2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals("part1"))
			{
				 readConfigIPFile();
				 LClockTemp.push(0);

		         reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE);
		         CreateRegisterForGatewayLookup();  
		         
			//	runTasks(); // Run Task1 and Task2 if only one command line Argument of configuration file
  				
				System.out.println("Do you want to perform Leader Election please enter Y or N");
					Scanner sc = new Scanner(System.in);
					String i = sc.next();
					if(i.equals("Y" )|| i.equals("y")){
						tempeSensorImpl inter = new tempeSensorImpl();
						try {
							String s =Integer.toString(Const.ID_SENSOR_TEMPERATURE)+"delim"+Integer.toString(Const.ID_SENSOR_TEMPERATURE);
							inter.ringAlgorithm(s);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}       
		         
			}
			if(args.length == 2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals("part2"))
			{
				 readConfigIPFile();
				 LClockTemp.push(0);

		         reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE);
		         CreateRegisterForGatewayLookup(); 
		         runTasks();
		         
			}
			
			if(args.length == 2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.LAB2_TEST_INPUT_FILE))
			{
				readConfigIPFile();
				 LClockTemp.push(0);

		         reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE);
		         CreateRegisterForGatewayLookup(); 
		         runTasks();
			}
			
			if(args.length==2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.TEST_INPUT_FILE))
			{
				runTestCases(); //Run Test cases from the input file if two command line Arguments are present.
			}
	    }
	
	/*
	 * (non-Javadoc) configure which gateway replica is register this motion
	 * @see com.DevicesInterfaces.RMIDevicesInterfaces#configureGateWay(java.lang.String, int)
	 */
	@Override
	public void configureGateWay(String GatewayIPaddress,int GatewayPort) throws RemoteException {

		if(GatewayPort == Const.GATEWAY_PORT)
		{
			Const.GATEWAY_PORT = Const.GATEWAY_PORT_REP;
			Const.GATEWAY_SERVER_IP = Const.GATEWAY_SERVER_IP_REP;
			Const.STR_LOOKUP_GATEWAY = Const.STR_LOOKUP_GATEWAY_REP;
			System.out.println("Gate Way Replication is Assigned to you");

		}
		else
		{
			Const.GATEWAY_PORT = Const.GATEWAY_TMP_PORT;
			Const.GATEWAY_SERVER_IP = Const.GATEWAY_TMP_IP;
			Const.STR_LOOKUP_GATEWAY = Const.STR_LOOKUP_TMP_GATEWAY;
			System.out.println("Gate Way is Assigned to you");

		}
		
	}
	
/*
 * configure which gateway replica is register this motion
 */
	public static void ConfigureGateWayorReport()
	{
		if(AssignedGWOrReplicas == Const.ID_GATEWAY)
		{
			Const.GATEWAY_PORT = Const.GATEWAY_PORT_REP;
			Const.GATEWAY_SERVER_IP = Const.GATEWAY_SERVER_IP_REP;
			Const.STR_LOOKUP_GATEWAY = Const.STR_LOOKUP_GATEWAY_REP;
		}
		else
		{
			Const.GATEWAY_PORT = Const.GATEWAY_TMP_PORT;
			Const.GATEWAY_SERVER_IP = Const.GATEWAY_TMP_IP;
			Const.STR_LOOKUP_GATEWAY = Const.STR_LOOKUP_TMP_GATEWAY;
		}
	}
	
	/*
	 * (non-Javadoc) interface for being notified by gateway server or replica which is crashed
	 * @see com.DevicesInterfaces.RMIDevicesInterfaces#NotifySensorsDevicesCrashed(int)
	 */
	
	@Override
	public void NotifySensorsDevicesCrashed(int CrashesId)
			throws RemoteException {
		// TODO Auto-generated method stub
		//record connected gateway or gateway replica
		if(CrashesId == Const.ID_GATEWAY)
		{
			System.out.println("Notified GateWay Crashed");
			AssignedGWOrReplicas = Const.ID_GATEWAY;
		}
		else
		{
			System.out.println("Notified GateWay Replica Crashed");
			AssignedGWOrReplicas = Const.ID_GATEWAY_REP;
		}
		ConfigureGateWayorReport();
	}
	 
}
