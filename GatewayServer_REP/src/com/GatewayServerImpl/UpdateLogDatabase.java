package com.GatewayServerImpl;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;

import com.BackendDataBaseInterfaces.RMIBackendDataBaseInterfaces;
import com.GatewayInterface.Const;
import com.SmartCtrlIntfPkg.SmartCtrlInterfaces;


public class UpdateLogDatabase implements Runnable {

	public static String DatabaseIP= Const.DATABASE_IP;
	public static int port=Const.DATABASE_PORT;
	private String timeStamp="";
	private String deviceType="";
	private String deviceID="";
	private String currentStatus="";
	private String typeLog="";
	private String inferredActivity="";
	
	public UpdateLogDatabase(String tStamp, String dType,
			String dID, String cStatus, String typeInfo,String inferredActivitty){
		java.util.Date date= new java.util.Date();
		this.timeStamp=tStamp;
		this.deviceType=dType;
		this.deviceID=dID;
		this.currentStatus=cStatus;
		this.typeLog=typeInfo;
		this.inferredActivity=inferredActivitty;
	}
	public void run() {
		Registry regs = null;
	//	System.out.println("Started updating thread");
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
            stDatabaseObj.updateDatabase(timeStamp, deviceType,deviceID,currentStatus,typeLog,inferredActivity);
		} catch (NullPointerException | RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
	}

}
