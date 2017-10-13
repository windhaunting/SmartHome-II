package com.heaterPkg;

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

public class requestTimeBerkleyAlgo implements Runnable {

	private int port;
	private String ipAddress;
	private String Lookup;
	private String idtoSend;
	private String catageory="GATEWAY";
	private String MY_NAME="Gateway Sensor";
	/**
	 * Create a seperate thread to iterate throughout the hashmap to request time from other remaining systems
	 *  regarding the winner of the election.
	 *  
	 *  A checking condition is provided to avoid request time from itself. 
	 */
	public void run() {
		Iterator ite = HeaterImpl.processRecord.keySet().iterator();
      	
        while(ite.hasNext()){
        	Integer id = (Integer)ite.next();
        	switch(id)
    		{
    		case Const.ID_DEVICE_BULB:
    			this.port=Const.SMART_BULB_SENSOR_PORT;
    			this.Lookup=Const.STR_LOOKUP_SMART_BULB;
    			this.idtoSend=new Integer(Const.ID_DEVICE_BULB).toString();
    			this.catageory="DEVICE";
    			this.ipAddress = HeaterImpl.processRecord.get(id);
    			break;
    		case Const.ID_SENSOR_TEMPERATURE:
    			this.port=Const.TEMP_SENSOR_PORT;
    			this.Lookup=Const.STR_LOOKUP_TEMP;
    			this.idtoSend=new Integer(Const.ID_SENSOR_TEMPERATURE).toString();
    			this.catageory="SENSOR";
    			this.ipAddress = HeaterImpl.processRecord.get(id);
    			break;
    		case Const.ID_DEVICE_OUTLET:
    			this.port=Const.HEATER_PORT;
    			this.Lookup=Const.STR_LOOKUP_HEATER;
    			this.idtoSend=new Integer(Const.ID_DEVICE_OUTLET).toString();
    			this.catageory="DEVICE";
    			this.ipAddress = HeaterImpl.processRecord.get(id);
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
    			this.ipAddress = HeaterImpl.processRecord.get(id);
    			break;
    		case Const.ID_DOOR:
    			this.port=Const.DOOR_PORT;
    			this.Lookup=Const.STR_LOOKUP_DOOR;
    			this.idtoSend=new Integer(Const.ID_DOOR).toString();
    			this.catageory="SENSOR";
    			this.ipAddress = HeaterImpl.processRecord.get(id);
    			break;
    		case Const.ID_GATEWAY:
    			this.port=Const.GATEWAY_PORT;
    			this.Lookup=Const.STR_LOOKUP_GATEWAY;
    			this.idtoSend=new Integer(Const.ID_GATEWAY).toString();
    			this.catageory="GATEWAY";
    			this.ipAddress = HeaterImpl.processRecord.get(id);
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

		} catch (Exception e) {
			
		}

		try {
			HeaterImpl.storeTimeStamps.add(stGatewayObj.provideTimeStamp());
			
		} catch (Exception e) {

		}
		
	}	
		else if(catageory.equals("SENSOR")){
			
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(ipAddress, port);
		} catch (RemoteException e) {

		}
			 RMIDevicesInterfaces stSensorObj = null;
			try {
				stSensorObj = (RMIDevicesInterfaces)regs.lookup(Lookup);
			} catch (Exception e) {

			}

			try {
				HeaterImpl.storeTimeStamps.add(stSensorObj.provideTimeStamp());
			} catch (Exception e) {

			}	
		}
		else if(catageory.equals("DEVICE") && !id.equals(Const.ID_DEVICE_OUTLET)){
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(ipAddress, port);
		} catch (RemoteException e) {

		}
			SmartCtrlInterfaces stDeviceObj = null;
			try {
				stDeviceObj = (SmartCtrlInterfaces)regs.lookup(Lookup);
			} catch (Exception e) {

			}

			try {
				HeaterImpl.storeTimeStamps.add(stDeviceObj.provideTimeStamp());
	            
			} catch (Exception e) {

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
			} catch (Exception e) {
			
			}
			try {
				HeaterImpl.storeTimeStamps.add(stDatabaseObj.provideTimeStamp());
			} catch (Exception e) {

			}
			
		
		}
	
	
		
	}
	          }

}
