package com.GatewayServerImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.NoSuchElementException;

import com.BackendDataBaseInterfaces.RMIBackendDataBaseInterfaces;
import com.GatewayInterface.Const;

public class RequestStatusFromDatabase implements Runnable {

	public static String DatabaseIP= Const.DATABASE_IP;
	public static int port=Const.DATABASE_PORT;
	private static String typeLog="";
	private String deviceId;
	private Integer device_id;
	public RequestStatusFromDatabase(String dId,String typeInfo){
		this.device_id=Integer.parseInt(dId.substring(7));
		this.deviceId=dId;
		this.typeLog=typeInfo;
	}
	//@SuppressWarnings("unchecked")
	public void run(){
		
		if(GwServerInterfaceImpl.CacheHelperQueue.contains(device_id)){
		
			GwServerInterfaceImpl.CacheHelperQueue.remove(device_id);
			GwServerInterfaceImpl.CacheHelperQueue.addFirst(device_id);
			
		}
		if(GwServerInterfaceImpl.LRUcache.containsKey(device_id)){
			String s = GwServerInterfaceImpl.LRUcache.get(device_id);
			 System.out.println("The Current entry in Database is" + s);
			 System.out.println("Cache hit Lucky Guy...!!!");
		}
		else{
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
			/**
			 * As we came here because of a cache miss we will update the cache after we query from the backend
			 */
			System.out.println("Cache Miss Unlucky Guy...!!!");
           String s= stDatabaseObj.getStatusFromLog(deviceId,typeLog);
           System.out.println("The Current entry in Database is" + s);
           String[] DataBaseRow = s.split("\t");
           Thread t = new Thread(new LRUCacheImpl(DataBaseRow[0],DataBaseRow[1],DataBaseRow[2],DataBaseRow[3]));
           t.start();
           
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		}
	}

}
