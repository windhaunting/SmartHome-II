package com.BackendDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import java.util.Scanner;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

import com.BackendDataBaseInterfaces.RMIBackendDataBaseInterfaces;
import com.DevicesInterfaces.RMIDevicesInterfaces;
import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;



public class BackendDatabaseimpl implements RMIBackendDataBaseInterfaces,Runnable {

	public static String queryRecord="";
	public static boolean Initiator=false;
	public static String ipAddress="localhost";
    public static ArrayList<Long> storeTimeStamps = new ArrayList<Long>();
    public static int offsetValuefromTimeServer=0;
    static HashMap<Integer,String> processRecord = new HashMap<Integer,String>();
	private static boolean FlagClockSynchronizationFinished=false;
	@Override
	/**
	 * Function to set flag when the election algorithm is completed so that after election is completed we can 
	 *     perform further processing for accesing the input file.
	 *     the process will be buzy waiting till election completes and FlagClockSynchronizationFinished becomes true.
	 */
	public void setFlagClockSync() throws RemoteException {
		System.out.println("came into flag");
		FlagClockSynchronizationFinished=true;
	}	
	public void run() {
		
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
	 * Function to create and update data into the log file.
	 *   if the file does not exist then new file is created and then values will be added. 
	 *   	if the file exists the values will be appended in the end.
	 */
	public void updateDatabase(String timeStamp, String deviceType,
			String deviceID, String currentStatus, String typeLog,String event) throws RemoteException {
		if(typeLog.equals(Const.LOGTYPE_PART1)){
			synchronized(this){
		try {
		    // Tab delimited file will be written to data with the name tab-file.csv
		   	File f = new File("event-log1.csv");
		    	FileWriter fos = null;
		    	PrintWriter dos=null;
		  	  if(!f.exists()){
		  	   fos = new FileWriter("event-log1.csv",true);
		  	   dos = new PrintWriter(fos);
		  	   dos.println("DeviceID\tTimeStamp\tDeviceType\tCurrentStatus"); 
		  	  }else{
		  		 fos = new FileWriter("event-log1.csv",true);
		    	 dos = new PrintWriter(fos);
		  	  } 
		  	dos.print("Machine"+deviceID+"\t");
		    dos.print(timeStamp+"\t");
		    dos.print(deviceType+"\t");
		    dos.print(currentStatus+"\t");
		    dos.println();
		    dos.close();
		    fos.close();
		    } catch (IOException e) {
		    System.out.println("Error Printing Tab Delimited File");
		    }
		}
		}
		if(typeLog.equals(Const.LOGTYPE_PART23)){
			synchronized(this){
			try {
			    // Tab delimited file will be written to data with the name tab-file.csv
			   	File f = new File("event-log2.csv");
			    	FileWriter fos = null;
			    	PrintWriter dos=null;
			  	  if(!f.exists()){
			  	   fos = new FileWriter("event-log2.csv",true);
			  	   dos = new PrintWriter(fos);
			  	   dos.println("DeviceID\tDeviceType\tTimeStamp\tCurrentStatus\tInferredActivity"); 
			  	  }else{
			  		 fos = new FileWriter("event-log2.csv",true);
			    	 dos = new PrintWriter(fos);
			  	  } 
			  	dos.print("Machine"+deviceID+"\t");
			  	dos.print(deviceType+"\t");
			    dos.print(timeStamp+"\t");
			    dos.print(currentStatus+"\t");
			    dos.print(event+"\t");
			    dos.println();
			    dos.close();
			    fos.close();
			    } catch (IOException e) {
			    System.out.println("Error Printing Tab Delimited File");
			    }
			}
		}
	}
	/**
	 * Function to create a Registry so that Gateway can perform remote actions on Database
	 */
	 private static void CreateRegisterForGatewayLookup()
	    {
		  try {
				String s=InetAddress.getLocalHost().toString();
				String[] ip=s.split("/"); 
				ipAddress=ip[ip.length-1];
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 BackendDatabaseimpl htobj = new BackendDatabaseimpl();
	        try{
	        	RMIBackendDataBaseInterfaces stub = (RMIBackendDataBaseInterfaces) UnicastRemoteObject.exportObject(htobj,0);
	            Registry reg;
	            try{
	                reg = LocateRegistry.createRegistry(Const.DATABASE_PORT);       //heater port 1099 here;
	                System.out.println("Backend  Data Base java RMI registry created.");
	                System.out.println("Ready to Log data from other devices and sensors");
	            }
	            catch(Exception e){
	                System.out.println(" Backend Data Base Using existing registry");
	                reg = LocateRegistry.getRegistry();
	            }
	            reg.rebind(Const.STR_LOOKUP_DATABASE, stub);
	        }catch(RemoteException e)
	        {
	            e.printStackTrace();
	        }
	    }
	 /**
	  * Function that helps to get registered at the Gateway. Gate way performs book keeping of all the machines
	  * @param type
	  * @param name
	  */
	 public static void reg(int type, String name){
		 
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
	            gtwy.register(type,name,ipAddress);

	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            System.out.println("exception");
	            e.printStackTrace();
	        }
	    }
	 /**
	  * Main method is used to select the operation to be performed by using command line arguments    
	  * @param args
	  * @throws InterruptedException
	  */
	 public static void main(String[] args) {
		UpdateallProcessInformationAvailableInSystem();
		readConfigIPFile();

  		if((args.length==1) && args[0].equals(Const.CONFIG_IPS_FILE))
  		{  
			BackendDatabaseimpl.CreateRegisterForGatewayLookup();	

			reg(Const.TYPE_SMART_DATABASE,Const.NAME_DATABASE);
			return;
  		}
		if(args.length==2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals("part1"))
  		{
		BackendDatabaseimpl.CreateRegisterForGatewayLookup();	

		reg(Const.TYPE_SMART_DATABASE,Const.NAME_DATABASE);

	      System.out.println("Do you want to perform Leader Election please enter Y or N");
					Scanner sc = new Scanner(System.in);
					String i = sc.next();
					if(i.equals("Y" )|| i.equals("y")){
						BackendDatabaseimpl inter = new BackendDatabaseimpl();
						try {
							String s =Integer.toString(Const.ID_DATABASE)+"delim"+Integer.toString(Const.ID_DATABASE);
							inter.ringAlgorithm(s);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} 
  		}
		
		if(args.length==2 && args[0].equals(Const.CONFIG_IPS_FILE) && (args[1].equals("part2")) || (args[1].equals(Const.LAB2_TEST_INPUT_FILE)))
  		{
			BackendDatabaseimpl.CreateRegisterForGatewayLookup();	

			reg(Const.TYPE_SMART_DATABASE,Const.NAME_DATABASE);
  		}

					
	}
	/**
	 * Method that helps to query the database log file and returns a tuple of the information.
	 *   The key to search in the Machine+"id"
	 */
	public String getStatusFromLog(String dId,String typeLog) {
		if(typeLog.equals("RECORD EVENT FROM SENSOR")){
	
		String strFile = "event-log1.csv";
	      CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(strFile));   //CSVReader class is used from Opencsv Dependency library
			
	      String [] nextLine;
	      try {
	    	  
			while ((nextLine = reader.readNext()) != null) {
			    String[] s1=nextLine[0].split("\t");
			    if(s1[0].equals(dId)){
			      queryRecord=nextLine[0];
			    }
			   
			    
			}
			 reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return queryRecord;
		}
		if(typeLog.equals("RECORD EVENT FROM MODE")){
			
			String strFile = "event-log2.csv";
		      CSVReader reader = null;
			try {
				reader = new CSVReader(new FileReader(strFile));
				
		      String [] nextLine;
		      try {
		    	  
				while ((nextLine = reader.readNext()) != null) {
				    String[] s1=nextLine[0].split("\t");
				    if(s1[0].equals(dId)){
				      queryRecord=nextLine[0];
				    }    
				}
				 reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return queryRecord;
			}
		return "No such Id";
	}

/**
 * This method creates is used to be accessed by the leader to inform that it is the leader.
 *       If the method is accessed by itself remotely then we can start the time sever from this method.
 */
	public void electionResult(String winner) throws RemoteException {
		System.out.println("The election is won by "+ winner);
		System.out.println("The Leader and Time Server is"+ winner);
		
		if(winner.equals("DATABASE")){
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
		    int n = BackendDatabaseimpl.storeTimeStamps.size();
		    // Iterating manually is faster than using an enhanced for loop.
		    for (int i = 0; i < n; i++)
		        sum += BackendDatabaseimpl.storeTimeStamps.get(i);
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
	public void ringAlgorithm(String s) throws RemoteException {	
		if(Initiator){
			helperForRingAlgorithm(s);
		}
		if(!Initiator){        
	//	if(motionSensorImpl.count==0){
			String appendID =s+"delim"+Const.ID_DATABASE;
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(Const.DOOR_IP,Const.DOOR_PORT);
		} catch (RemoteException e) {

		}
			RMIDevicesInterfaces stSensorObj = null;
			try {
				stSensorObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_DOOR);
			} catch (RemoteException | NotBoundException e) {
				
				String s4= appendID+"delim"+Const.ID_DATABASE;
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
	public long provideTimeStamp() throws RemoteException{
		  
		return System.currentTimeMillis();
	}
	/**
	 * Function to adjust the offset of the time using the time recived remotely by the leader.
	 */
	public void setOffsetTimeVariable(Long time){

		BackendDatabaseimpl.offsetValuefromTimeServer= (int) (System.currentTimeMillis()-time);
			System.out.println("The offsetvalue for time is set by Using TimeStamp from Master to :"+ BackendDatabaseimpl.offsetValuefromTimeServer );
			
	}
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
}
