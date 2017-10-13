package com.presencePkg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedHashMap;
import java.util.Scanner;

import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;
import com.GatewayInterface.MessageTrans;

public class PresenceImpl implements Runnable {
	
	public static int Presence_state = Const.ON;
	public static String ipAddress="localhost";
	
    private static LinkedHashMap<Double, Integer> presenceEventValueHash = new LinkedHashMap<Double, Integer>();
    private static double lastTimeStamp = 0;
    
	
	private static void runPresenceChangedReport()
	{
		MessageTrans.waittimeInterval(2000);   //because it's intruder identifer, the intruder is random, so it's also random

		presenceEventValueHash = MessageTrans.readLab2TestInput(3);
		  
		   lastTimeStamp = 0;
		   //  motionEventValueHash 
	      //timer//
		  for (Double time : presenceEventValueHash.keySet()) {
		      Integer state = presenceEventValueHash.get(time);
		      //change state 
		      Presence_state = state;
		      //report state
			  Thread thread = new Thread(){
			    	    public void run(){
			    	    	reportChangeForLab2Part3();	
			    	    }
			    	  };
			      thread.start();
       		
		      Integer interVal = (int)(time-lastTimeStamp)*1000;
		      //wait time;
		      MessageTrans.waittimeInterval(interVal);
		      
		      lastTimeStamp = time;
		      System.out.println("Key = " + time + ", Value = " + state + "，doorState = " +Presence_state);
 
		  }

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
		        gtwy.report_state(Const.ID_SENSOR_PRESENCE, Presence_state);
		        
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("exception");
				e.printStackTrace();
			}	
	}
	
	
	private static void readConfigIPFile()
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
				 gtwy.register(Const.TYPE_SENSOR,Const.NAME_SENSOR_PRESENCE,ipAddress);
			
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("exception");
				e.printStackTrace();
			}	
	}


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
	    	            System.out.println("Need to Report the Prsence State Enter Y or N");
	    	            if(in1.nextLine().equals("Y"))
	    	            {
		                     gtwy.report_state(Const.ID_SENSOR_PRESENCE, Presence_state);

	    	            }
			            Thread.sleep(2000);
	    	        }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("exception");
				e.printStackTrace();
			}				
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		report(Presence_state);

	}
	
	/*
	 * main function for presence sensor operation
	 */
	public static void main(String[] args) throws RemoteException {
		if(0 == args.length)
		{  
			 System.out.println("lack of input parameter");
			 return;
		}
		
		if(args[0].equals(Const.CONFIG_IPS_FILE))
		{
			readConfigIPFile();  // Run Task1 and Task2 if only one command line Argument of configuration file
			
			reg(Const.TYPE_SENSOR,Const.NAME_SENSOR_PRESENCE);
			System.out.println("243  bulb enter here");

			PresenceImpl mrt = new PresenceImpl();
	        Thread t = new Thread(mrt);
	        t.start();

		}
		
		if(args.length==2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.LAB2_TEST_INPUT_FILE))
		{	
			readConfigIPFile();  // Run Task1 and Task2 if only one command line Argument of configuration file
			
			reg(Const.TYPE_SENSOR,Const.NAME_SENSOR_PRESENCE);
			System.out.println("243  bulb enter here");

			  Thread thread = new Thread(){
		    	    public void run(){
		    	    	runPresenceChangedReport();	
		    	    }
		    	  };
		      thread.start();
		      
		}
			
	}
	

}
