package com.GatewayServerImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;
import com.GatewayInterface.MessageTrans;


public class GateWayServer {
	public static void UpdateallProcessInformationAvailableInSystem(){
		GwServerInterfaceImplRep.processRecord.put(Const.ID_DATABASE, Const.DATABASE_IP);
		GwServerInterfaceImplRep.processRecord.put(Const.ID_DOOR, Const.DOOR_IP);
		GwServerInterfaceImplRep.processRecord.put(Const.ID_DEVICE_BULB, Const.CLIENT_SMART_BULB_IP);
		GwServerInterfaceImplRep.processRecord.put(Const.ID_DEVICE_OUTLET, Const.CLIENT_SMART_HEATER_IP);
		GwServerInterfaceImplRep.processRecord.put(Const.ID_GATEWAY, Const.GATEWAY_SERVER_IP);
		GwServerInterfaceImplRep.processRecord.put(Const.ID_SENSOR_MOTION,Const.CLIENT_SENSOR_MOTION_IP);
		GwServerInterfaceImplRep.processRecord.put(Const.ID_SENSOR_TEMPERATURE,Const.CLIENT_SENSOR_TMPERATURE_IP);
	}

	public static void main(String[] args) throws InterruptedException, IOException
	{ 
		UpdateallProcessInformationAvailableInSystem();
		
		GwServerInterfaceImplRep.storeIpAddressSensorsDevices.put(Const.ID_GATEWAY,Const.GATEWAY_SERVER_IP);
		GwServerInterfaceImplRep GatewayExec  = new GwServerInterfaceImplRep();
		GwServerInterfaceImplRep.startThreadRecieve();
  		if((args.length==1) && args[0].equals(Const.CONFIG_IPS_FILE))
		{  
			while(true){
				Scanner user_input = new Scanner( System.in );
				System.out.println("querying data base..........");
				System.out.println("Enter Device ID\n 1 for Temperature \n 2 for Motion \n 3 for Door");
				int rollno=user_input.nextInt();  
		    	//String machId = "Machine"+Integer.toString(i);
				String machId = "Machine"+rollno;
		    	Thread t1= new Thread(new RequestStatusFromDatabase(machId,Const.LOGTYPE_PART1));	
				t1.start();
		    //	Thread t2= new Thread(new RequestStatusFromDatabase(machId,Const.LOGTYPE_PART23));	
			//	t2.start();
		 
			}
		}
		//GwServerInterfaceImpl GatewayExec  = new GwServerInterfaceImpl();
		if(args.length==2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals("part1"))
		{
		//	GwServerInterfaceImplRep.startThreadRecieve();
			
		/*	System.out.println("Do you want to perform Leader Election please enter Y or N");
			Scanner sc = new Scanner(System.in);
			String i = sc.next();
			if(i.equals("Y" )|| i.equals("y")){
				GwServerInterfaceImpl.startElection(GwServerInterfaceImpl.processRecord);
			}
			
			Thread.sleep(10000);
			
			if(GwServerInterfaceImpl.IWON && GwServerInterfaceImpl.conditionVariable ){
				System.out.println("Gate Way has won the election");
				Thread t = new Thread(new BroadcastResult());
				t.start();
			}   */
			
			  System.out.println("Do you want to perform Leader Election please enter 1");
				Scanner sc = new Scanner(System.in);
				String i = sc.next();
				if(i.equals("1" )){
					try {
						String s =Integer.toString(Const.ID_GATEWAY)+"delim"+Integer.toString(Const.ID_GATEWAY);
						
						GatewayExec.ringAlgorithm(s);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(i.equals("2"))
				{
					Thread t= new Thread(new RequestStatusFromDatabase("Machine58","RECORD EVENT FROM SENSOR"));	
					t.start();
				}
					  
			
		}
		if(args.length == 2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals("part2"))
		{
			System.out.println("Manually operate event to test logic clock algorithm");
			
		//	GwServerInterfaceImplRep.startThreadRecieve();
			GatewayExec.runTasks();
		}
		
		if(args.length == 2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.LAB2_TEST_INPUT_FILE))
		{
			//GwServerInterfaceImplRep.startThreadRecieve();
			  
			  while(!GwServerInterfaceImplRep.FlagClockSynchronizationFinished)
	  		  {
				  System.out.println("waiting leader election finishes");
	  		  }
				
			  if(GwServerInterfaceImplRep.FlagClockSynchronizationFinished)
			  {
				  System.out.println("FlagClockSynchronizationFinished enter");
					
					MessageTrans.waittimeInterval(10);  //wait 10ms
					
					GwServerInterfaceImplRep.runGatewayQuery(); 	 
			  }
			 
			  Thread.sleep(3000);
			  
			    System.out.println("Finished TestCase1");
			    System.out.println("Begin to query database for each sensor current state");
			    for(int i = Const.ID_SENSOR_TEMPERATURE; i < Const.ID_GATEWAY; i++)
			    {
			    	String machId = "Machine"+Integer.toString(i);
			    	Thread t1= new Thread(new RequestStatusFromDatabase(machId,Const.LOGTYPE_PART1));	
					t1.start();
			    	Thread t2= new Thread(new RequestStatusFromDatabase(machId,Const.LOGTYPE_PART23));	
					t2.start();
			    }

		}
		
		//for lab1 test
		if(args.length==2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.TEST_INPUT_FILE))
		{
			GatewayExec.runTestCases();
		}
		
		//gw replica
		if(args.length == 2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.LAB3_TEST))
		{
			GatewayExec.FaultTolerantTask();
			
		}
		

	}
}
