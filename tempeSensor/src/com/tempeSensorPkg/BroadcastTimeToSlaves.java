package com.tempeSensorPkg;

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

public class BroadcastTimeToSlaves implements Runnable {

	private int port;
	private String ipAddress;
	private String Lookup;
	private String idtoSend;
	private long timeToSend;
	private String catageory="SENSOR";
	private String MY_NAME="MOTION SENSOR";
	/**
	 * Constructor to initialize the variable
	 * @param s
	 */
	public BroadcastTimeToSlaves(long time){
		this.timeToSend=time;
	}
	/**
	 * Create a seperate thread to iterate throughout the hashmap to broadcast message to all other remaining systems
	 *  regarding the winner of the election.
	 *  
	 *  A checking condition is provided to avoid broadcasting time to itself. 
	 */
	public void run() {
		Iterator ite = tempeSensorImpl.processRecord.keySet().iterator();
      	
        while(ite.hasNext()){
        	Integer id = (Integer)ite.next();
        	switch(id)
    		{
    		case Const.ID_DEVICE_BULB:
    			this.port=Const.SMART_BULB_SENSOR_PORT;
    			this.Lookup=Const.STR_LOOKUP_SMART_BULB;
    			this.idtoSend=new Integer(Const.ID_DEVICE_BULB).toString();
    			this.catageory="DEVICE";
    			this.ipAddress = tempeSensorImpl.processRecord.get(id);
    			break;
    		case Const.ID_SENSOR_TEMPERATURE:
    			this.port=Const.TEMP_SENSOR_PORT;
    			this.Lookup=Const.STR_LOOKUP_TEMP;
    			this.idtoSend=new Integer(Const.ID_SENSOR_TEMPERATURE).toString();
    			this.catageory="SENSOR";
    			this.ipAddress = tempeSensorImpl.processRecord.get(id);
    			break;
    		case Const.ID_DEVICE_OUTLET:
    			this.port=Const.HEATER_PORT;
    			this.Lookup=Const.STR_LOOKUP_HEATER;
    			this.idtoSend=new Integer(Const.ID_DEVICE_OUTLET).toString();
    			this.catageory="DEVICE";
    			this.ipAddress = tempeSensorImpl.processRecord.get(id);
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
    			this.ipAddress = tempeSensorImpl.processRecord.get(id);
    			break;
    		case Const.ID_DOOR:
    			this.port=Const.DOOR_PORT;
    			this.Lookup=Const.STR_LOOKUP_DOOR;
    			this.idtoSend=new Integer(Const.ID_DOOR).toString();
    			this.catageory="SENSOR";
    			this.ipAddress = tempeSensorImpl.processRecord.get(id);
    			break;
    		case Const.ID_GATEWAY:
    			this.port=Const.GATEWAY_PORT;
    			this.Lookup=Const.STR_LOOKUP_GATEWAY;
    			this.idtoSend=new Integer(Const.ID_GATEWAY).toString();
    			this.catageory="GATEWAY";
    			this.ipAddress = tempeSensorImpl.processRecord.get(id);
    			break;
    		default:
    			break;
    		}
		if(catageory.equals("GATEWAY")){	
		Registry regs = null;

		try {
			regs = LocateRegistry.getRegistry(ipAddress, port);

	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		 GatewayAllInterfaces stGatewayObj = null;
		try {
			stGatewayObj = (GatewayAllInterfaces)regs.lookup(Lookup);

		} catch (RemoteException | NotBoundException e) {
			
		}

		try {
			stGatewayObj.setOffsetTimeVariable(timeToSend);
			
		} catch (NullPointerException | RemoteException e) {

		}
		
	}	
		else if(catageory.equals("SENSOR") && !id.equals(Const.ID_SENSOR_TEMPERATURE)){
			
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(ipAddress, port);
		} catch (RemoteException e) {

		}
			 RMIDevicesInterfaces stSensorObj = null;
			try {
				stSensorObj = (RMIDevicesInterfaces)regs.lookup(Lookup);
			} catch (RemoteException | NotBoundException e) {

			}

			try {
				stSensorObj.setOffsetTimeVariable(timeToSend);
			} catch (NullPointerException | RemoteException e) {

			}	
		}
		else if(catageory.equals("DEVICE")){
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(ipAddress, port);
		} catch (RemoteException e) {

		}
			SmartCtrlInterfaces stDeviceObj = null;
			try {
				stDeviceObj = (SmartCtrlInterfaces)regs.lookup(Lookup);
			} catch (RemoteException | NotBoundException e) {

			}

			try {
				stDeviceObj.setOffsetTimeVariable(timeToSend);
	            
			} catch (NullPointerException | RemoteException e) {

			}
			
		}
		else if(catageory.equals("DATABASE")){

			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(ipAddress, port);
		} catch (RemoteException e) {

		}
			RMIBackendDataBaseInterfaces stDatabaseObj = null;
			try {
				stDatabaseObj = (RMIBackendDataBaseInterfaces)regs.lookup(Lookup);
			} catch (RemoteException | NotBoundException e) {
			
			}
			try {
				stDatabaseObj.setOffsetTimeVariable(timeToSend);
			} catch (NullPointerException | RemoteException e) {

			}
			
		
		}
	
	
		
	}
	          }


}
