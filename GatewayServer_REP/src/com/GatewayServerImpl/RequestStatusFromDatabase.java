package com.GatewayServerImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.Queue;

import com.BackendDataBaseInterfaces.RMIBackendDataBaseInterfaces;
import com.GatewayInterface.Const;

public class RequestStatusFromDatabase implements Runnable {

	public static String DatabaseIP= Const.DATABASE_IP;
	public static int port=Const.DATABASE_PORT;
	private static String typeLog="";
	private String deviceId="";
	public RequestStatusFromDatabase(String dId,String typeInfo){
		this.deviceId=dId;
		this.typeLog=typeInfo;
	}
	public void run(){
		/**
		 * 
		 * Least Recently used update in the queue hashmap 
		 *public static Queue CacheHelperQueue = new LinkedList<Integer>();
		 */
	//	GwServerInterfaceImpl.l
		Registry regs = null;
		//System.out.println("Started updating thread");
		try {
			regs = LocateRegistry.getRegistry(DatabaseIP, port);
		//	System.out.println("Started updating thread 1");
		//	regs = LocateRegistry.getRegistry(ClientId);
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		 RMIBackendDataBaseInterfaces stDatabaseObj = null;
		try {
		//	System.out.println("Started updating thread 3");
			stDatabaseObj = (RMIBackendDataBaseInterfaces)regs.lookup(Const.STR_LOOKUP_DATABASE);
		//	System.out.println("Started updating thread 2");
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println("The Host is Dead");
		}

		try {
           String s= stDatabaseObj.getStatusFromLog(deviceId,typeLog);
           System.out.println("The Current entry in Database is" + s);
		} catch (NullPointerException | RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
	}

}
