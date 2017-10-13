package com.bulbSmart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;
import java.util.Set;

import com.DevicesInterfaces.RMIDevicesInterfaces;
import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;
import com.GatewayInterface.MessageTrans;
import com.SmartCtrlIntfPkg.SmartCtrlInterfaces;

public class bulbSmart implements SmartCtrlInterfaces, Runnable {

	public static int Bulb_state = Const.ON;
	public static String ipAddress="localhost";
	
	public static boolean Initiator=false;
    public static ArrayList<Long> storeTimeStamps = new ArrayList<Long>();
    public static int offsetValuefromTimeServer=0;
    static HashMap<Integer,String> processRecord = new HashMap<Integer,String>();

    private static HashMap<String, Integer> ClockValueBulb = new HashMap<String, Integer>();
    private static Stack<Integer> LClockBulb = new Stack<Integer>();   		//lamport clock of bulb
	private static boolean FlagClockSynchronizationFinished=false;
	
	 //////////////lab3
    private static int AssignedGWOrReplicas = Const.ID_GATEWAY;          //ID_GATEWAY or ID_GATEWAY_REP; need to assign this value at load balance stage?
    
    
	/**
	 * Function to set flag when the election algorithm is completed so that after election is completed we can 
	 *     perform further processing for accesing the input file.
	 *     the process will be buzy waiting till election completes and FlagClockSynchronizationFinished becomes true.
	 */
	@Override
	public void setFlagClockSync() throws RemoteException {
		FlagClockSynchronizationFinished=true;
	}	
	/**
	 * Function to read the file which has the ipaddress of the Gateway.
	 */ 
	public static void readConfigIPFile()
	{
		 String filename = Const.CONFIG_IPS_FILE;
			String workingDirectory = System.getProperty("user.dir");
			File file = new File(workingDirectory, filename);
			try {
				if (file.createNewFile()) {
					System.out.println("File is created!");
				} else {
					System.out.println("Read Gateway IP from Configuration File!");
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
	/**
	 * Function to be remotely invoked by the gateway to inform about state change
	 */
	@Override
	public boolean change_state(int device_id, int state)
			throws RemoteException {
		if(device_id==Const.ID_DEVICE_BULB)
		{
	       if(state == Const.OFF)
	       {
	    	   Bulb_state = Const.OFF;
	    	   System.out.println("The Bulb is OFF");
			}
			else
			{
				Bulb_state = Const.ON;
				System.out.println("The Bulb is ON");
			}
	       return true;
		}
		else
		{
			System.out.println("device ID is wrong");
			return false;
		}
		
	}
/**
 * Helper function to register at the Gateway
 * @param type
 * @param name
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			GatewayAllInterfaces gtwy=null;
			
			try {
				gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
				//gtwy.register(Const.TYPE_SENSOR, Const.NAME_DEVICE_BULB,ipAddress);
				// gtwy.register(Const.TYPE_SENSOR, Const.ID_DEVICE_BULB, Const.NAME_DEVICE_BULB,ipAddress,VectorClockBulb);
				LClockBulb.push(LClockBulb.peek()+1);      //local event add 1
				ClockValueBulb.put(Const.EVT_BULB_REGISTER, LClockBulb.peek());
		        gtwy.register(Const.TYPE_SENSOR,Const.ID_DEVICE_BULB, Const.NAME_DEVICE_BULB,ipAddress, LClockBulb, Const.EVT_BULB_REGISTER);

				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("exception");
				e.printStackTrace();
			}	
	}
	/**
	 * Function to report the current state for logical clocks
	 * @param cur
	 */
	//report state
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
				 while(true){
	    	            Scanner in1= new Scanner(System.in);
	    	            System.out.println("Need to Report the State Enter Y or N");
	    	            if(in1.nextLine().equals("Y"))
	    	            {
		                     // gtwy.report_state(Const.ID_DEVICE_BULB, Bulb_state);
	    	            	// gtwy.report_state(Const.ID_DEVICE_BULB, Bulb_state, VectorClockBulb);
	    	            	LClockBulb.push(LClockBulb.peek()+1);      //local event add 1
	    	            	ClockValueBulb.put(Const.EVT_BULB_CHANGED, LClockBulb.peek());

			            	if(Bulb_state == Const.ON)
			            	{
				            	gtwy.report_state(Const.ID_DOOR, Bulb_state, LClockBulb, Const.EVT_GW_BULB_ON_SENSED);
			            	}
			            	else
			            	{
				            	gtwy.report_state(Const.ID_DEVICE_BULB, Bulb_state, LClockBulb, Const.EVT_GW_BULB_OFF_SENSED);
			            	}
			            	
	    	            }
			            Thread.sleep(2000);
	    	        }
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			GatewayAllInterfaces gtwy=null;
			
			try {
				gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
				gtwy.register(Const.TYPE_SMART_DEVICE, Const.NAME_DEVICE_BULB,ipAddress);
				// gtwy.register(Const.TYPE_SENSOR, Const.ID_DEVICE_BULB, Const.NAME_DEVICE_BULB,ipAddress,VectorClockBulb);
				//LClockBulb.push(LClockBulb.peek()+1);      //local event add 1
				//ClockValueBulb.put(Const.EVT_BULB_REGISTER, LClockBulb.peek());
		        //gtwy.register(Const.TYPE_SENSOR,Const.ID_DEVICE_BULB, Const.NAME_DEVICE_BULB,ipAddress, LClockBulb, Const.EVT_BULB_REGISTER);

				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("exception");
				e.printStackTrace();
			}	
	}
	/**
	 * Function to create a Registry so that Gateway can perform remote actions on Database
	 */
	public static void RegisterRMIForGateWay()			//for the change state function
	{

		bulbSmart bulb = new bulbSmart();
		try{
			SmartCtrlInterfaces stub = (SmartCtrlInterfaces) UnicastRemoteObject.exportObject(bulb,0);
			Registry reg;
			
			try{
				reg = LocateRegistry.createRegistry(Const.SMART_BULB_SENSOR_PORT);       //heater port 1099 here;
				System.out.println("Smart Bulb java RMI registry created.");
			}
			catch(Exception e){
	        	System.out.println(" Smart Bulb Using existing registry");
	        	reg = LocateRegistry.getRegistry();
			}
			reg.rebind(Const.STR_LOOKUP_SMART_BULB, stub);
		}catch(RemoteException e) 
		{
	    	e.printStackTrace();
	    }
    }
/**
 * Start a new thread to report its state
 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		report(Bulb_state);
	}
/**
 * Overloaded  method to be invoked by the gateway to indicate the state to be changed.
 */	@Override
	public boolean change_state(int device_id, int state,
			Stack<Integer> LogicClock) throws RemoteException {
		// TODO Auto-generated method stub
		if(device_id==Const.ID_DEVICE_BULB)
		{
	       if(state == Const.OFF)
	       {
	    	   Bulb_state = Const.OFF;
	    	   System.out.println("The Bulb is OFF");
			}
			else
			{
				Bulb_state = Const.ON;
				System.out.println("The Bulb is ON");
			}
	       MessageTrans.LamportLogicClock(LogicClock, LClockBulb, Const.EVT_BULB_CHANGED, ClockValueBulb);
	       LClockBulb = MessageTrans.LogicClocks;
	       ClockValueBulb = MessageTrans.ClockVlaue;
	       return true;
		}
		else
		{
			System.out.println("device ID is wrong");
			return false;
		}
	}
 /**
  * This method creates is used to be accessed by the leader to inform that it is the leader.
  *       If the method is accessed by itself remotely then we can start the time sever from this method.
  */
public void electionResult(String winner) throws RemoteException {

		System.out.println("The election is won by "+ winner);
		System.out.println("The Leader and Time Server is"+ winner);
		
		if(winner.equals("SMARTBULB")){
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
		    int n = bulbSmart.storeTimeStamps.size();
		    // Iterating manually is faster than using an enhanced for loop.
		    for (int i = 0; i < n; i++)
		        sum += bulbSmart.storeTimeStamps.get(i);
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
		// TODO Auto-generated method stub

		
		if(Initiator){
			helperForRingAlgorithm(s);
		}
		if(!Initiator){
	//	if(motionSensorImpl.count==0){
			String appendID =s+"delim"+Const.ID_DEVICE_BULB;
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(Const.CLIENT_SMART_HEATER_IP,Const.HEATER_PORT);
		} catch (RemoteException e) {

		}
			SmartCtrlInterfaces stSensorObj = null;
			try {
				stSensorObj = (SmartCtrlInterfaces)regs.lookup(Const.STR_LOOKUP_HEATER);
			} catch (RemoteException | NotBoundException e) {

				String s4= appendID+"delim"+Const.ID_DEVICE_BULB;
				helperForRingAlgorithm(s4);
			}

			try {
				Initiator=!Initiator;
				stSensorObj.ringAlgorithm(appendID);
			} catch (NullPointerException | RemoteException e) {

			}
			
			//	
		}  	
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
	@Override
	public long provideTimeStamp() throws RemoteException{
		  
		return System.currentTimeMillis();
	}
	/**
	 * Function to adjust the offset of the time using the time recived remotely by the leader.
	 */
	public void setOffsetTimeVariable(Long time){

			bulbSmart.offsetValuefromTimeServer= (int) (System.currentTimeMillis()-time);
			System.out.println("The offsetvalue for time is set by Using TimeStamp from Master to :"+ bulbSmart.offsetValuefromTimeServer );
	}
	
	/**
	 * MAin method
	 * @param args
	 * @throws RemoteException
	 */
		public static void main(String[] args) throws RemoteException {
			
	  		if((args.length==1) && args[0].equals(Const.CONFIG_IPS_FILE))
			{  
	  			readConfigIPFile();  
				RegisterRMIForGateWay();
				regOver(Const.TYPE_SMART_DEVICE,Const.NAME_DEVICE_BULB);
			}
			
			if(args.length==2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals("part1"))
			{
				readConfigIPFile();  // Run Task1 and Task2 if only one command line Argument of configuration file
				
				//initialize the logic clock of bulb
				LClockBulb.push(0);
				
				RegisterRMIForGateWay();
				reg(Const.TYPE_SMART_DEVICE,Const.NAME_DEVICE_BULB);

			   System.out.println("Do you want to perform Leader Election please enter Y or N");
						Scanner sc = new Scanner(System.in);
						String i = sc.next();
						if(i.equals("Y" )|| i.equals("y")){
							bulbSmart inter = new bulbSmart();
							try {
								String s =Integer.toString(Const.ID_DEVICE_BULB)+"delim"+Integer.toString(Const.ID_DEVICE_BULB);
								inter.ringAlgorithm(s);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
			}
			
	  		if(args.length == 2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals("part2"))
	  		{
	  			readConfigIPFile();  // Run Task1 and Task2 if only one command line Argument of configuration file
				
				//initialize the logic clock of bulb
				LClockBulb.push(0);
				
				RegisterRMIForGateWay();
				reg(Const.TYPE_SMART_DEVICE,Const.NAME_DEVICE_BULB);
				bulbSmart mrt = new bulbSmart();
		        Thread t = new Thread(mrt);
		        t.start();
	  		}
	  		
			if(args.length == 2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.LAB2_TEST_INPUT_FILE))
			{
				readConfigIPFile();  // Run Task1 and Task2 if only one command line Argument of configuration file
				//initialize the logic clock of bulb
				LClockBulb.push(0);
				
				RegisterRMIForGateWay();
				reg(Const.TYPE_SMART_DEVICE,Const.NAME_DEVICE_BULB);
			}
			
		}
		
		/*
		 * (non-Javadoc)configure which gateway replica is register this motion
		 * @see com.SmartCtrlIntfPkg.SmartCtrlInterfaces#configureGateWay(java.lang.String, int)
		 */
		public void configureGateWay(String GatewayIPaddress, int GatewayPort) throws RemoteException {
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
		 * @see com.SmartCtrlIntfPkg.SmartCtrlInterfaces#NotifySensorsDevicesCrashed(int)
		 */
		@Override
		public void NotifySensorsDevicesCrashed(int CrashesId)
				throws RemoteException {
			// TODO Auto-generated method stub
			//record connected gateway or gateway replica
			if(CrashesId == Const.ID_GATEWAY)
			{
				System.out.println("Notified GateWay Crashed");
				AssignedGWOrReplicas = Const.ID_GATEWAY_REP;
			}
			else
			{
				System.out.println("Notified GateWay Replica Crashed");
				AssignedGWOrReplicas = Const.ID_GATEWAY;
			}
			
			ConfigureGateWayorReport();
		}	
		
	}





