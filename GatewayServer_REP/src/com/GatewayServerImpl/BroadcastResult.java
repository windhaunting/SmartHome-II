package com.GatewayServerImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Iterator;

import com.BackendDataBaseInterfaces.RMIBackendDataBaseInterfaces;
import com.DevicesInterfaces.RMIDevicesInterfaces;
import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;
import com.SmartCtrlIntfPkg.SmartCtrlInterfaces;

public class BroadcastResult implements Runnable {

	private int port;
	private String ipAddress;
	private String Lookup;
	private String idtoSend;
	private String catageory="GATEWAY";
	private String MY_NAME="Motion Sensor";
	/**
	 * Constructor to initialize the variable
	 * @param s
	 */
	public BroadcastResult(String s){
		this.MY_NAME=s;
	}
	/**
	 * Create a seperate thread to iterate throughout the hashmap to broadcast message to all other remaining systems
	 *  regarding the winner of the election.
	 */
	public void run() {
		Iterator ite = GwServerInterfaceImplRep.processRecord.keySet().iterator();
      	
        while(ite.hasNext()){
        	Integer id = (Integer)ite.next();
        	switch(id)
    		{
    		case Const.ID_DEVICE_BULB:
    			this.port=Const.SMART_BULB_SENSOR_PORT;
    			this.Lookup=Const.STR_LOOKUP_SMART_BULB;
    			this.idtoSend=new Integer(Const.ID_DEVICE_BULB).toString();
    			this.catageory="DEVICE";
    			this.ipAddress = GwServerInterfaceImplRep.processRecord.get(id);
    			break;
    		case Const.ID_SENSOR_TEMPERATURE:
    			this.port=Const.TEMP_SENSOR_PORT;
    			this.Lookup=Const.STR_LOOKUP_TEMP;
    			this.idtoSend=new Integer(Const.ID_SENSOR_TEMPERATURE).toString();
    			this.catageory="SENSOR";
    			this.ipAddress = GwServerInterfaceImplRep.processRecord.get(id);
    			break;
    		case Const.ID_DEVICE_OUTLET:
    			this.port=Const.HEATER_PORT;
    			this.Lookup=Const.STR_LOOKUP_HEATER;
    			this.idtoSend=new Integer(Const.ID_DEVICE_OUTLET).toString();
    			this.catageory="DEVICE";
    			this.ipAddress = GwServerInterfaceImplRep.processRecord.get(id);
    			break;
    		case Const.ID_SENSOR_MOTION:
    			this.port=Const.MOTION_SENSOR_PORT;
    			this.Lookup=Const.STR_LOOKUP_MOTION;
    			this.idtoSend=new Integer(Const.ID_SENSOR_MOTION).toString();
    			this.catageory="SENSOR";
    			break;
    		case Const.ID_DATABASE:
    			this.port=Const.DATABASE_PORT;
    			this.Lookup=Const.STR_LOOKUP_DATABASE;
    			this.idtoSend=new Integer(Const.ID_DATABASE).toString();
    			this.catageory="DATABASE";
    			this.ipAddress = GwServerInterfaceImplRep.processRecord.get(id);
    			break;
    		case Const.ID_DOOR:
    			System.out.println("came into door");
    			this.port=Const.DOOR_PORT;
    			this.Lookup=Const.STR_LOOKUP_DOOR;
    			this.idtoSend=new Integer(Const.ID_DOOR).toString();
    			this.catageory="SENSOR";
    			this.ipAddress = GwServerInterfaceImplRep.processRecord.get(id);
    			break;
    		case Const.ID_GATEWAY:
    			this.port=Const.GATEWAY_PORT;
    			this.Lookup=Const.STR_LOOKUP_GATEWAY;
    			this.idtoSend=new Integer(Const.ID_GATEWAY).toString();
    			this.catageory="GATEWAY";
    			this.ipAddress = GwServerInterfaceImplRep.processRecord.get(id);
    			break;
    		default:
    			break;
    		}
		if(catageory.equals("GATEWAY")){	
		Registry regs = null;
		//System.out.println("Started updating thread");
		try {
			regs = LocateRegistry.getRegistry(ipAddress, port);
		
		//	regs = LocateRegistry.getRegistry(ClientId);
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		 GatewayAllInterfaces stGatewayObj = null;
		try {
			//System.out.println("Started updating thread 3");
			stGatewayObj = (GatewayAllInterfaces)regs.lookup(Lookup);
			//System.out.println("Started updating thread 2");
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println("The Host is Dead");
		}

		try {
			stGatewayObj.electionResult(MY_NAME);
			stGatewayObj.setFlagClockSync();
		} catch (NullPointerException | RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
	}	
		else if(catageory.equals("SENSOR")){
	
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(ipAddress, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			 RMIDevicesInterfaces stSensorObj = null;
			try {
				stSensorObj = (RMIDevicesInterfaces)regs.lookup(Lookup);
			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//System.out.println("The Host is Dead");
			}

			try {
				stSensorObj.electionResult(MY_NAME);
				stSensorObj.setFlagClockSync();
			} catch (NullPointerException | RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}	
		}
		else if(catageory.equals("DEVICE")){
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(ipAddress, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			SmartCtrlInterfaces stDeviceObj = null;
			try {
				stDeviceObj = (SmartCtrlInterfaces)regs.lookup(Lookup);
			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//System.out.println("The Host is Dead");
			}

			try {
				stDeviceObj.electionResult(MY_NAME);
				stDeviceObj.setFlagClockSync();
			} catch (NullPointerException | RemoteException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
			}
			
		}
		else{

			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(ipAddress, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
			RMIBackendDataBaseInterfaces stDatabaseObj = null;
			try {
				stDatabaseObj = (RMIBackendDataBaseInterfaces)regs.lookup(Lookup);
			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//System.out.println("The Host is Dead");
			}
			try {
			stDatabaseObj.electionResult(MY_NAME);
			stDatabaseObj.setFlagClockSync();
			} catch (NullPointerException | RemoteException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
			}
			
		
		}
	
	
		
	}
	          }
}
