/**
 * Reports the Gate way the presence of any motion in the region . It is both push and pull based.
 */
package com.motionSensorPkg;

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
import com.SmartCtrlIntfPkg.SmartCtrlInterfaces;





public class motionSensorImpl extends Object implements Runnable,RMIDevicesInterfaces {
    
	 static HashMap<Integer,String> processRecord = new HashMap<Integer,String>();
    private static int motionState = Const.OFF;
    public static String ipAddress="localhost"; // Default ipAddress is localhost. In the program we take IpAddress from Machine and Override
  
    private static LinkedHashMap<Double, Integer> motionEventValueHash = new LinkedHashMap<Double, Integer>();
    private static ArrayList<Double> timeEventArray = new ArrayList<Double>();
    private static double lastTimeStamp = 0;
    
    public static double currentMotionTimeStamp = 0;
    public static boolean startNextEventAction = false;
    public static boolean IWON = true;
    public static boolean ElectionPerformed=false;
    public static boolean conditionVariable=true;
    public static boolean Initiator=false;
    public static ArrayList<Long> storeTimeStamps = new ArrayList<Long>();
    public static int offsetValuefromTimeServer=0;

   private static HashMap<String, Integer> ClockValueMotion = new HashMap<String, Integer>();
    private static Stack<Integer> LClockMotion = new Stack<Integer>();   		//lamport clock of Motion

    //////////////lab3
    private static int AssignedGWOrReplicas = Const.ID_GATEWAY;          //ID_GATEWAY or ID_GATEWAY_REP; need to assign this value at load balance stage?
    
    
private static boolean FlagClockSynchronizationFinished=false;
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
     * readConfigIPFile() is used to Read the Ip address of the Gateway from configuration file. 
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
    /**
     * reg(int type, String name)  is a Helper Method to register at the Gateway and send its ipAddress to the Gateway
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
          //  gtwy.register(Const.TYPE_SENSOR, Const.NAME_SENSOR_MOTION,ipAddress);
            LClockMotion.push(LClockMotion.peek()+1);      //local event add 1
            ClockValueMotion.put(Const.EVT_MOTION_REGISTER, LClockMotion.peek());
            gtwy.register(Const.TYPE_SENSOR,Const.ID_SENSOR_MOTION, Const.NAME_SENSOR_MOTION,ipAddress, LClockMotion, Const.EVT_MOTION_REGISTER);

            
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
            gtwy.register(Const.TYPE_SENSOR, Const.NAME_SENSOR_MOTION,ipAddress);
          //  LClockMotion.push(LClockMotion.peek()+1);      //local event add 1
           // ClockValueMotion.put(Const.EVT_MOTION_REGISTER, LClockMotion.peek());
           // gtwy.register(Const.TYPE_SENSOR,Const.ID_SENSOR_MOTION, Const.NAME_SENSOR_MOTION,ipAddress, LClockMotion, Const.EVT_MOTION_REGISTER);

            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("exception");
            e.printStackTrace();
        }
        
    }
    /**
     * Creating a Seperate thread to push notifications to the Gateway
     */
    @Override
    public void run() {
        Registry regs = null;
        try {
            regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        GatewayAllInterfaces gtwy=null;
        
        try {
            gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
            while(true)
            {
	               if(motionState == Const.YES_MOTION_)
	               {
                     //  gtwy.report_state(Const.ID_SENSOR_MOTION, motionState);
	            	 //  gtwy.report_state(Const.ID_SENSOR_MOTION, motionState, VectorClockMotion);
	            	 LClockMotion.push(LClockMotion.peek()+1);      //local event add 1
	               	 ClockValueMotion.put(Const.EVT_MOTION_CHANGED, LClockMotion.peek());
	            	 gtwy.report_state(Const.ID_SENSOR_MOTION, motionState, LClockMotion, Const.EVT_GW_MOTION_MOTION_SENSED);
	               }
	               Thread.sleep(5000);  //5ms to test
            }
        } catch (Exception e) {
            System.out.println("exception");
            e.printStackTrace();
        }		    
    }
    
   /**
    * Gate way can remotely query the query_state(int device_id) method to get the current Temperature
    */
    public int query_state(int device_id) throws RemoteException {
        if(device_id==Const.ID_SENSOR_MOTION){
            System.out.println("The Current state is"+motionState);	
            return motionState;
        }
        System.out.println("Function called with Wrong device ID");
        return -1;
    }
   /**
    * Creating a Registry so that RMI can be performed by Gateway
    */
    private static void CreateRegisterforGatewayLookup()
    {
        motionSensorImpl htobj = new motionSensorImpl();
        try{
            RMIDevicesInterfaces stub = (RMIDevicesInterfaces) UnicastRemoteObject.exportObject(htobj,0);
            Registry reg;
            try{
                reg = LocateRegistry.createRegistry(Const.MOTION_SENSOR_PORT);       //heater port 1099 here;
                System.out.println("Motion Sensor java RMI registry created.");
            }
            catch(Exception e){
                System.out.println("Motion sensor Using existing registry");
                reg = LocateRegistry.getRegistry();
            }
            reg.rebind(Const.STR_LOOKUP_MOTION, stub);
        }catch(RemoteException e)
        {
            e.printStackTrace();
        }
        
    }
    /**
     * runTasks()  is Helper method run Task2 specified in the assignment. The implementation of smart bulb through Gateway
     */
    public static void runTasks()
    {
         motionSensorImpl mrt = new motionSensorImpl();
         Thread t = new Thread(mrt);
         t.start();
         
      //   CreateRegisterforGatewayLookup();
         
         while(true){
             Scanner in= new Scanner(System.in);
             System.out.println("Please Enter 1 if you change Motion to Motion state Enter 0 if No motion");
             motionState = Integer.parseInt(in.nextLine());
         }
    }
    /**
     * readTestInput() is the Helper fucntion to get the input from the test file to check for test cases. 
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
				int value = Integer.parseInt(lineInformation[2]);
				motionEventValueHash.put(timestamp, value);
				System.out.println("lineInformation [code= " + lineInformation[0] 
	                                 + " , name=" + lineInformation[2] + "]");
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
     * Report the Gateway that reading of the input test case file is done 
     * @param time
     * @param reportStateorFinish
     */
    public static void reportStateOrFinishforTestCase(double time, boolean reportStateorFinish) {
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
            if(!reportStateorFinish)   //false is for report state
            {
               gtwy.report_state(Const.ID_SENSOR_MOTION, motionState, time);
            }
            else
            {
            	gtwy.FinishCurrentTimeEventToGateway(1);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("exception");
            e.printStackTrace();
        }		   
    }
    /**
     * Helper function to inform to run Test cases given in an input file.
     */
    private static void runTestCases()
	{	
    	//read IP configures
    	readConfigIPFile();
    	//read TestInputCsvFile
    	readTestInput();
    	
    	CreateRegisterforGatewayLookup();
    	
    	if(0 == currentMotionTimeStamp)
        {
              System.out.println("currentMotionTimeStamp qqq" + currentMotionTimeStamp);
              reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_MOTION);
        }
    	
        while(true)
        {
            System.out.println("currentMotionTimeStamp adfqee" + currentMotionTimeStamp);
        	if(startNextEventAction)
        	{
                System.out.println("startNextEventAction aa" + startNextEventAction);
	           int currTimeStampMotion = motionEventValueHash.get(currentMotionTimeStamp);
	           if(currTimeStampMotion != motionState)
	           {
	        	   reportStateOrFinishforTestCase(currentMotionTimeStamp, false);   //report state;
	           }
       
            	reportStateOrFinishforTestCase(currentMotionTimeStamp, true);   //report finish;
        		startNextEventAction = false;
        		if(currentMotionTimeStamp == timeEventArray.get(timeEventArray.size()-1))
        		{
        			break;
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

	@Override
	/**
	 * The Gateway can access remotely query_state(double time, int device_id) method to check if there exists motion or not.
	 */
	public int query_state(double time, int device_id) throws RemoteException {
		// TODO Auto-generated method stub
	    if(device_id==Const.ID_SENSOR_MOTION){
            System.out.println("The Current time motion is"+motionEventValueHash.get(time));	
            return motionState;
        }
        System.out.println("Function called with Wrong device ID");
        return -1;
	}

	@Override
	/**
	 * NotifySensorEventAction(double time) is called my the Gateway remotely so that motion sensor can proceed further
	 *  using Logical clock.
	 *  This is implemented using a new Thread to avoid Blocking call.
	 */
	public void NotifySensorEventAction(double time) throws RemoteException {
		NotifySensor n=new NotifySensor(time);
		Thread nwThread= new Thread(n);
		nwThread.start();
	}
	/**
	 * This method creates is used to be accessed by the leader to inform that it is the leader.
	 *       If the method is accessed by itself remotely then we can start the time sever from this method.
	 */	
	public void electionResult(String winner) throws RemoteException {
		System.out.println("The election is won by "+ winner);
		System.out.println("The Leader and Time Server is"+ winner);
		
		if(winner.equals("MOTION SENSOR")){
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
		    int n = motionSensorImpl.storeTimeStamps.size();
		    // Iterating manually is faster than using an enhanced for loop.
		    for (int i = 0; i < n; i++)
		        sum += motionSensorImpl.storeTimeStamps.get(i);
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
	 * Function to start a new thread to broadcast the result to all other machines
	 * @param s
	 */
	public static void broadcast(String s){
		Thread t = new Thread(new BroadcastResult(s));
		t.start();
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
	//	if(motionSensorImpl.count==0){
			String appendID =s+"delim"+Const.ID_SENSOR_MOTION;
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(Const.CLIENT_SMART_BULB_IP,Const.SMART_BULB_SENSOR_PORT);
		} catch (RemoteException e) {

		}
			SmartCtrlInterfaces stSensorObj = null;
			try {
				stSensorObj = (SmartCtrlInterfaces)regs.lookup(Const.STR_LOOKUP_SMART_BULB);
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
	 * Function to be remotely invoked by the leader to provide the current time to the leader
	 */
  public long provideTimeStamp() throws RemoteException{
	  
		return System.currentTimeMillis();
	}
	/**
	 * Function to adjust the offset of the time using the time recived remotely by the leader.
	 */
	public void setOffsetTimeVariable(Long time){

			motionSensorImpl.offsetValuefromTimeServer= (int) (System.currentTimeMillis()-time);
			System.out.println("The offsetvalue for time is set by Using TimeStamp from Master to :"+ motionSensorImpl.offsetValuefromTimeServer );
	}
	@Override
	public MessageTrans query_state(int device_id, Stack<Integer> SendLogicClock)
			throws RemoteException {
		// TODO Auto-generated method stub
		 if(device_id==Const.ID_SENSOR_MOTION){
			   System.out.println("The Current state is "+motionState);	
			   

				MessageTrans msg = new MessageTrans();
								
		        msg.QueryStateResult = motionState;
		        msg.LogicClocks = LClockMotion;
		        msg.ClockVlaue = ClockValueMotion;
		        
		        System.out.println("The logic clock of motion after query_state is "+ LClockMotion.toString());
		        System.out.println("The logic clock val of motion after query_state is "+ ClockValueMotion.toString());

		        return msg;
	            
	        }
		return null;
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

   	            	 LClockMotion.push(LClockMotion.peek()+1);      //local event add 1
   	               	 ClockValueMotion.put(Const.EVT_MOTION_CHANGED, LClockMotion.peek());
   	            	 gtwy.report_state(Const.ID_SENSOR_MOTION, motionState, LClockMotion, Const.EVT_GW_MOTION_MOTION_SENSED);

           } catch (Exception e) {
               System.out.println("exception");
               e.printStackTrace();
           }		    
           
    }
	  public static void runMotionChangedReport()
	    {
		   //read file
		  motionEventValueHash = MessageTrans.readLab2TestInput(2);
		  
		   lastTimeStamp = 0;
		   //  motionEventValueHash 
	      //timer//
		  for (Double time : motionEventValueHash.keySet()) {
		      Integer state = motionEventValueHash.get(time);
		      //change state 
		      motionState = state;
		      //report state
		      if(Const.ON == motionState)
		      {
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

		      System.out.println("Key = " + time + ", Value = " + state + "，motionState = " +motionState);
   
		  }
		  
	    	
	    }
	  /**
		  * Main method is used to select the operation to be performed by using command line arguments    
		  * @param args
		  * @throws InterruptedException
		  */
	  public static void main(String args[]) throws RemoteException{
	  		
	    	UpdateallProcessInformationAvailableInSystem();
	    	
	  		if((args.length==1) && args[0].equals(Const.CONFIG_IPS_FILE))
	  		{  
	  			 readConfigIPFile();
	    		regOver(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE);
		         CreateRegisterforGatewayLookup(); 
	  			 return;
	  		}
	  		
	  		if(args.length==2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals("part1"))
	  		{

	  			 readConfigIPFile();
		         LClockMotion.push(0);

		         reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE);
		         
		         CreateRegisterforGatewayLookup(); 
		         
		         System.out.println("Do you want to perform Leader Election please enter Y or N");
					Scanner sc = new Scanner(System.in);
					String i = sc.next();
					if(i.equals("Y" )|| i.equals("y")){
						motionSensorImpl inter = new motionSensorImpl();
						try {
							String s =Integer.toString(Const.ID_SENSOR_MOTION)+"delim"+Integer.toString(Const.ID_SENSOR_MOTION);
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
		         LClockMotion.push(0);

		         reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE);
		         CreateRegisterforGatewayLookup(); 
	  	         
		         runTasks();
	  			
			}
			
			if(args.length == 2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.LAB2_TEST_INPUT_FILE))
			{	
				 readConfigIPFile();
			//	 runMotionChangedReport();
		         LClockMotion.push(0);
		  
		         reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE);
		         CreateRegisterforGatewayLookup(); 
		         
		         //motion operate clock sync algorithm
				 motionSensorImpl inter = new motionSensorImpl();
				 String s =Integer.toString(Const.ID_SENSOR_MOTION)+"delim"+Integer.toString(Const.ID_SENSOR_MOTION);
				 inter.ringAlgorithm(s);			
					
				 //wait enter
				 while(!FlagClockSynchronizationFinished)
		  		 {
					  System.out.println("waiting leader election finishes");

					
		  		 } 	    
				 if(FlagClockSynchronizationFinished)
				 {
						System.out.println("FlagClockSynchronizationFinished enter");
						MessageTrans.waittimeInterval(10);  //wait 10ms
						runMotionChangedReport(); 	
				 }
			}
			
			//for Lab1 test
	  		if(args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.TEST_INPUT_FILE))
	  		{
	  			runTestCases(); // Two Command Line Arguments means the Application can run in Test case file Mode, Automatically taking from test case file 
	  		}
	  		
	      }
	  /*
	   * (non-Javadoc) configure which gateway replica is register this motion
	   * @see com.DevicesInterfaces.RMIDevicesInterfaces#configureGateWay(java.lang.String, int)
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