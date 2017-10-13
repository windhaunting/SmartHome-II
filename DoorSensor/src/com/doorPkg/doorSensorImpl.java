package com.doorPkg;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import com.DevicesInterfaces.RMIDevicesInterfaces;
import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;
import com.GatewayInterface.MessageTrans;

public class doorSensorImpl implements Runnable,RMIDevicesInterfaces{
	
    public static String ipAddress="localhost"; // Default ipAddress is localhost. In the program we take IpAddress from Machine and Override
    private static int doorState = Const.OPEN;

    private static int previousDoorState = Const.CLOSED;
    
	public static boolean Initiator=false;
    public static ArrayList<Long> storeTimeStamps = new ArrayList<Long>();
    public static int offsetValuefromTimeServer=0;
    static HashMap<Integer,String> processRecord = new HashMap<Integer,String>();
        
    private static HashMap<String, Integer> ClockValueDoor = new HashMap<String, Integer>();
    private static Stack<Integer> LClockDoor = new Stack<Integer>();   		//lamport clock of door
	private static boolean FlagClockSynchronizationFinished=false;
	
    private static LinkedHashMap<Double, Integer> doorEventValueHash = new LinkedHashMap<Double, Integer>();
    private static double lastTimeStamp = 0;
    
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
    
    /*******************************************************************
     * readConfigIPFile() is used to Read the Ip address of the Gateway from configuration file. 
     *****************************************************************/
    public static void readConfigIPFile(){
        String filename = Const.CONFIG_IPS_FILE;
        String workingDirectory = System.getProperty("user.dir");
        File file = new File(workingDirectory, filename);
       // System.out.println("Final filepath : " + file.getAbsolutePath());
        try {
            if (file.createNewFile()) {
                System.out.println("File is created!");
            } else {
                System.out.println("Read the IpAddress from the Configuration file");
            }
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        
        BufferedReader reader = null;
        try {
           // System.out.println("read begin：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
                
                switch(line)
                {
                    case 1:
                        Const.GATEWAY_SERVER_IP = tempString;
                       // System.out.println("line " + line + ": " + tempString);
                        break;
                    default:
                        break;
                }
                line++;
            }
            
           // System.out.println(Const.GATEWAY_SERVER_IP+"outside");
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
    
    /*********************************************************************************
     * reg(int type, String name)  is a Helper Method to register at the Gateway and send its ipAddress to the Gateway
     *********************************************************************************/
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
          //  gtwy.register(Const.TYPE_SENSOR, Const.NAME_SENSOR_MOTION,ipAddress);
          //  gtwy.register(Const.TYPE_SENSOR,Const.ID_SENSOR_DOOR, Const.NAME_SENSOR_DOOR,ipAddress, VectorClockDoor);
            LClockDoor.push(LClockDoor.peek()+1);      //local event add 1
            ClockValueDoor.put(Const.EVT_DOOR_REGISTER, LClockDoor.peek());
            gtwy.register(Const.TYPE_SENSOR,Const.ID_DOOR, Const.NAME_SENSOR_DOOR,ipAddress, LClockDoor, Const.EVT_DOOR_REGISTER);

            
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
            gtwy.register(Const.TYPE_SENSOR, Const.NAME_SENSOR_DOOR,ipAddress);
      //      gtwy.register(Const.TYPE_SENSOR,Const.ID_SENSOR_DOOR, Const.NAME_SENSOR_DOOR,ipAddress, VectorClockDoor);
       //     LClockDoor.push(LClockDoor.peek()+1);      //local event add 1
       //     ClockValueDoor.put(Const.EVT_DOOR_REGISTER, LClockDoor.peek());
       //     gtwy.register(Const.TYPE_SENSOR,Const.ID_DOOR, Const.NAME_SENSOR_DOOR,ipAddress, LClockDoor, Const.EVT_DOOR_REGISTER);

            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("exception");
            e.printStackTrace();
        }
        
    }
    
    /**
     * Creating a Registry so that RMI can be performed by Gateway
     */
     private static void CreateRegisterforGatewayLookup()
     {
         doorSensorImpl htobj = new doorSensorImpl();
         try{
             RMIDevicesInterfaces stub = (RMIDevicesInterfaces) UnicastRemoteObject.exportObject(htobj,0);
             Registry reg;
             try{
                 reg = LocateRegistry.createRegistry(Const.DOOR_PORT);       //DOOR port  here;
                 System.out.println("Door Sensor java RMI registry created.");
             }
             catch(Exception e){
                 System.out.println("Door sensor Using existing registry");
                 reg = LocateRegistry.getRegistry();
             }
             reg.rebind(Const.STR_LOOKUP_DOOR, stub);
         }catch(RemoteException e)
         {
             e.printStackTrace();
         }
         
     }
     /**
      * Helper method to execute test cases and starts a new thread to avoid blocking calls.
      * @throws InterruptedException
      */     
	   public static void runTasks() throws InterruptedException
	    {
	         doorSensorImpl mrt = new doorSensorImpl();
	         Thread t = new Thread(mrt);
	         t.start();
	         t.join();
	         
	         while(true){
	             Scanner in= new Scanner(System.in);
	             System.out.println("Please Enter 1 if you want to change door \"OPEN\"; Enter 0 if you want to change door \"CLOSE\"");
	             doorState = Integer.parseInt(in.nextLine());
	         }
	         

	    }
/**
 * 	Function used by the gateway to query the current sate of the machine   
 */
	@Override
	public int query_state(int device_id) throws RemoteException {
		// TODO Auto-generated method stub
		  if(device_id==Const.ID_DOOR){
	            System.out.println("The Current door state is"+doorState);	
	            return doorState;
	        }
	        System.out.println("Function called with Wrong device ID");
	        return -1;
	        
	}

	@Override
	public int query_state(double time, int device_id) throws RemoteException {
		// TODO Auto-generated method stub
		return -1;
	}


	@Override
	public void NotifySensorEventAction(double time) throws RemoteException {
		// TODO Auto-generated method stub
		return;
	}

    /****************************************************************
     * Creating a Seperate thread to push notifications to the Gateway
     ****************************************************************/
	@Override
	public void run() {
		// TODO Auto-generated method stub
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
	            	/*
	            	  Scanner in1= new Scanner(System.in);
	    	            System.out.println("Need to Report the State Enter Y or N");
	    	            if(in1.nextLine().equals("Y"))
	    	            {
		                    //  gtwy.report_state(Const.ID_DEVICE_OUTLET, HeaterState);
	    	            	gtwy.report_state(Const.ID_SENSOR_DOOR, doorState, VectorClockDoor);
	    	            }
	    	            
	    	            */
	            		if(previousDoorState != doorState)
    	            	{
	            			previousDoorState = doorState;
			            	System.out.println("door state changed, automatically reported");
			            	
			            	LClockDoor.push(LClockDoor.peek()+1);      //local event add 1
		            		ClockValueDoor.put(Const.EVT_GW_DOOR_CHANGED, LClockDoor.peek());

			            	if(doorState == Const.OPEN)
			            	{
				            	gtwy.report_state(Const.ID_DOOR, doorState, LClockDoor, Const.EVT_DOOR_OPEN_SENSED);
			            	}
			            	else
			            	{
				            	gtwy.report_state(Const.ID_DOOR, doorState, LClockDoor, Const.EVT_GW_DOOR_CLOSED_SENSED);
			            	}
			            		
    	            	}

			            Thread.sleep(1000);
	            }
	        } catch (Exception e) {
	            System.out.println("exception");
	            e.printStackTrace();
	        }	
	        return;
	}

	@Override
	public MessageTrans query_state(int device_id, Stack<Integer> SendLogicClock)
			throws RemoteException {
		// TODO Auto-generated method stub
		if(device_id==Const.ID_DOOR){
			   System.out.println("The Current door state is"+doorState);	
			   
				MessageTrans msg = new MessageTrans();
				//	VectorClockDoor = MessageTrans
				msg.QueryStateResult = doorState;
		        msg.LogicClocks = LClockDoor;

		        System.out.println("The logic clock of door after query_state is "+ LClockDoor.toString());
		        return msg;
	            
	        }
		
		return null;
		
	}
	/**
	 * This method creates is used to be accessed by the leader to inform that it is the leader.
	 *       If the method is accessed by itself remotely then we can start the time sever from this method.
	 */
	@Override
	public void electionResult(String winner) throws RemoteException {
		System.out.println("The election is won by "+ winner);
		System.out.println("The Leader and Time Server is"+ winner);
		
		if(winner.equals("DOOR")){
			long Average = 0;
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
		    int n = doorSensorImpl.storeTimeStamps.size();
		    // Iterating manually is faster than using an enhanced for loop.
		    for (int i = 0; i < n; i++)
		        sum += doorSensorImpl.storeTimeStamps.get(i);
		    // We don't want to perform an integer division, so the cast is mandatory.
		    try{
		    Average = (((long) sum) / n);
		    }catch(Exception e){}
		    
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
			String appendID =s+"delim"+Const.ID_DOOR;
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP,Const.GATEWAY_PORT);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			GatewayAllInterfaces stSensorObj = null;
			try {
				stSensorObj = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//System.out.println("The Host is Dead");
			}

			try {
				Initiator=!Initiator;
				stSensorObj.ringAlgorithm(appendID);
			} catch (NullPointerException | RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

			doorSensorImpl.offsetValuefromTimeServer= (int) (System.currentTimeMillis()-time);
			System.out.println("The offsetvalue for time is set by Using TimeStamp from Master to :"+ doorSensorImpl.offsetValuefromTimeServer );
	}
    
    
    private static void reportChangeForLab2Part3()
    {
    	   Registry regs = null;
           try {
               regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);
           } catch (RemoteException e) {
               e.printStackTrace();
           }
           GatewayAllInterfaces gtwy=null;
           
           try {
               gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
         
	            	System.out.println("door state changed, automatically reported");
	            	
	            	LClockDoor.push(LClockDoor.peek()+1);      //local event add 1
	            	ClockValueDoor.put(Const.EVT_GW_DOOR_CHANGED, LClockDoor.peek());

	            	if(doorState == Const.OPEN)
	            	{
		            	gtwy.report_state(Const.ID_DOOR, doorState, LClockDoor, Const.EVT_DOOR_OPEN_SENSED);
	            	}
	            	else
	            	{
		            	gtwy.report_state(Const.ID_DOOR, doorState, LClockDoor, Const.EVT_GW_DOOR_CLOSED_SENSED);
	            	}

           } catch (Exception e) {
               System.out.println("exception");
               e.printStackTrace();
           }		    
           
    }
	  public static void runDoorChangedReport()
	    {
		   //read file
		  doorEventValueHash = MessageTrans.readLab2TestInput(1);
		  
		   lastTimeStamp = 0;
		   //  motionEventValueHash 
	      //timer//
		  for (Double time : doorEventValueHash.keySet()) {
		      Integer state = doorEventValueHash.get(time);
		      //change state 
		      doorState = state;
		      //report state
		      if(previousDoorState != doorState)
         		{
     			  previousDoorState = doorState;
			      Thread thread = new Thread(){
			    	    public void run(){
			    	    	reportChangeForLab2Part3();	
			    	    }
			    	  };
			      thread.start();
         		}
		      
		      Integer interVal = (int)(time-lastTimeStamp)*1000;
		      //wait time;
		      MessageTrans.waittimeInterval(interVal);
		      
		      lastTimeStamp = time;
		      System.out.println("Key = " + time + ", Value = " + state + "，doorState = " +doorState);
   
		  }
		  
	    	
	    }
/**
 * Main method is used to invoke the operation to be performed by using command line arguments    
 * @param args
 * @throws InterruptedException
 */
		 public static void main(String args[]) throws InterruptedException{
			 
	  		if((args.length==1) && args[0].equals(Const.CONFIG_IPS_FILE))
	  		{  
	  			readConfigIPFile(); 
	  			regOver(Const.TYPE_SENSOR, Const.NAME_SENSOR_DOOR); 
	    		CreateRegisterforGatewayLookup();
	    		return;
	  		}
	  		
	  		if(args.length == 2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals("part1"))
	  		{  
	  			readConfigIPFile(); 
			    // System.out.println(Const.GATEWAY_SERVER_IP+"outside");
	  			//initialize the logic clock of gw
			  	LClockDoor.push(0);
			  	 
			    reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_DOOR);
			     
	    		CreateRegisterforGatewayLookup();
	  		
	  		//	runTasks(); // One Command Line Argument means the Application can run in Manual Mode
	  			
	  			System.out.println("Do you want to perform Leader Election please enter Y or N");
				Scanner sc = new Scanner(System.in);
				String i = sc.next();
				if(i.equals("Y" )|| i.equals("y")){
					doorSensorImpl inter = new doorSensorImpl();
					try {
						String s =Integer.toString(Const.ID_DOOR)+"delim"+Integer.toString(Const.ID_DOOR);
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
			 	 //initialize the logic clock of gw
			  	 LClockDoor.push(0);
			  	
			     reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_DOOR);
			     CreateRegisterforGatewayLookup();
	  		
	  			runTasks(); 		
			}
			
			if(args.length == 2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.LAB2_TEST_INPUT_FILE))
			{
				 readConfigIPFile(); 
			     // System.out.println(Const.GATEWAY_SERVER_IP+"outside");
				 //initialize the logic clock of gw
			  	 LClockDoor.push(0);
			  	 
			    reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_DOOR);
			    CreateRegisterforGatewayLookup();	  			//initialize the vector clock of gw
			    
	  			//wait the the leader election finishes
				while(!FlagClockSynchronizationFinished)
	  			{

			    	if(	FlagClockSynchronizationFinished)
					{
				    	System.out.println("FlagClockSynchronizationFinished enter");
				
						MessageTrans.waittimeInterval(10);  //wait 10ms
						
						runDoorChangedReport(); 	
					}
	  			}
				
			}
			
	  		if(args.length == 2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.TEST_INPUT_FILE))
	  		{
	  		//	runTestCases(); // Two Command Line Arguments means the Application can run in Test case file Mode, Automatically taking from test case file 
	  		}
	  		
	      }

		 /*
		  * (non-Javadoc)configure which gateway replica is register this motion
		  * @see com.DevicesInterfaces.RMIDevicesInterfaces#configureGateWay(java.lang.String, int)
		  */
@Override
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
 * @see com.DevicesInterfaces.RMIDevicesInterfaces#NotifySensorsDevicesCrashed(int)
 */
@Override
public void NotifySensorsDevicesCrashed(int CrashesId) throws RemoteException {
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
