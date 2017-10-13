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

/**
 * Configure the Ipaddress of the gateway replication at appropriate sensors and
 * devices so that they can contact the replication for load balance
 * 
 * @author ashaik
 */
public class UnicastControllingGatewayIPAddress implements Runnable {

	private int port;
	private String ipAddress;
	private String Lookup;
	private String idtoSend;
	private String catageory = "GATEWAY";
	private static int id = 0;

	public UnicastControllingGatewayIPAddress(int id_indicate) {
		UnicastControllingGatewayIPAddress.id = id_indicate;
	}

	/**
	 * Create a seperate thread to iterate throughout the hashmap to broadcast
	 * message to all other remaining systems regarding the winner of the
	 * election.
	 */
/*	public void run() {
	
        	switch(id)
    		{
    		case Const.ID_DEVICE_BULB:
    			this.port=Const.SMART_BULB_SENSOR_PORT;
    			this.Lookup=Const.STR_LOOKUP_SMART_BULB;
    			this.idtoSend=new Integer(Const.ID_DEVICE_BULB).toString();
    			this.catageory="DEVICE";
    			this.ipAddress = GwServerInterfaceImpl.processRecord.get(id);
    			break;
    		case Const.ID_SENSOR_TEMPERATURE:
    			this.port=Const.TEMP_SENSOR_PORT;
    			this.Lookup=Const.STR_LOOKUP_TEMP;
    			this.idtoSend=new Integer(Const.ID_SENSOR_TEMPERATURE).toString();
    			this.catageory="SENSOR";
    			this.ipAddress = GwServerInterfaceImpl.processRecord.get(id);
    			break;
    		case Const.ID_DEVICE_OUTLET:
    			this.port=Const.HEATER_PORT;
    			this.Lookup=Const.STR_LOOKUP_HEATER;
    			this.idtoSend=new Integer(Const.ID_DEVICE_OUTLET).toString();
    			this.catageory="DEVICE";
    			this.ipAddress = GwServerInterfaceImpl.processRecord.get(id);
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
    			this.ipAddress = GwServerInterfaceImpl.processRecord.get(id);
    			break;
    		case Const.ID_DOOR:
    			System.out.println("came into door");
    			this.port=Const.DOOR_PORT;
    			this.Lookup=Const.STR_LOOKUP_DOOR;
    			this.idtoSend=new Integer(Const.ID_DOOR).toString();
    			this.catageory="SENSOR";
    			this.ipAddress = GwServerInterfaceImpl.processRecord.get(id);
    			break;
    		case Const.ID_GATEWAY:
    			this.port=Const.GATEWAY_PORT;
    			this.Lookup=Const.STR_LOOKUP_GATEWAY;
    			this.idtoSend=new Integer(Const.ID_GATEWAY).toString();
    			this.catageory="GATEWAY";
    			this.ipAddress = GwServerInterfaceImpl.processRecord.get(id);
    			break;
    		default:
    			break;
    		}

		if(catageory.equals("SENSOR")){
	System.out.println("came into sensor");
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
				stSensorObj.configureGateWay(Const.GATEWAY_SERVER_IP_REP,Const.GATEWAY_PORT_REP);
				stSensorObj=null;
			} catch (NullPointerException | RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}	
		}
		
		if(catageory.equals("DEVICE")){
			System.out.println("came into Device");
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
				stDeviceObj.configureGateWay(Const.GATEWAY_SERVER_IP_REP,Const.GATEWAY_PORT_REP);
				stDeviceObj=null;
			} catch (NullPointerException | RemoteException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
			}
			
		}
		if(catageory.equals("DATABASE")){
			System.out.println("came into Database");
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(ipAddress, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			stDatabaseObj.configureGateWay(Const.GATEWAY_SERVER_IP_REP,Const.GATEWAY_PORT_REP);
			stDatabaseObj=null;
			} catch (NullPointerException | RemoteException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
			}
			
		}
	          }  */
	
	public void run(){
		synchronized(this){
		if(UnicastControllingGatewayIPAddress.id == Const.ID_DATABASE){

			System.out.println("came into Database");
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(Const.DATABASE_IP,Const.DATABASE_PORT);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			RMIBackendDataBaseInterfaces stDatabaseObj = null;
			try {
				stDatabaseObj = (RMIBackendDataBaseInterfaces)regs.lookup(Const.STR_LOOKUP_DATABASE);
			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//System.out.println("The Host is Dead");
			}
			try {
			stDatabaseObj.configureGateWay(Const.GATEWAY_SERVER_IP_REP,Const.GATEWAY_PORT_REP);
			stDatabaseObj=null;
			} catch (NullPointerException | RemoteException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
			}
			
		}
		else if(UnicastControllingGatewayIPAddress.id == Const.ID_DEVICE_BULB){

			System.out.println("came into Device");
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(Const.CLIENT_SMART_BULB_IP, Const.SMART_BULB_SENSOR_PORT);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			SmartCtrlInterfaces stDeviceObj = null;
			try {
				stDeviceObj = (SmartCtrlInterfaces)regs.lookup(Const.STR_LOOKUP_SMART_BULB);
				
			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//System.out.println("The Host is Dead");
			}

			try {
				stDeviceObj.configureGateWay(Const.GATEWAY_SERVER_IP_REP,Const.GATEWAY_PORT_REP);
				stDeviceObj=null;
			} catch (NullPointerException | RemoteException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
			}
			
		
		}
		else if(UnicastControllingGatewayIPAddress.id == Const.ID_DEVICE_OUTLET){
			System.out.println("came into Device");
			Registry regs = null;
			try {
				regs = LocateRegistry.getRegistry(Const.CLIENT_SMART_HEATER_IP, Const.HEATER_PORT);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			SmartCtrlInterfaces stDeviceObj = null;
			try {
				stDeviceObj = (SmartCtrlInterfaces)regs.lookup(Const.STR_LOOKUP_HEATER);
				
			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//System.out.println("The Host is Dead");
			}

			try {
				stDeviceObj.configureGateWay(Const.GATEWAY_SERVER_IP_REP,Const.GATEWAY_PORT_REP);
				stDeviceObj=null;
			} catch (NullPointerException | RemoteException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
			}
			
		

		}
		else if(UnicastControllingGatewayIPAddress.id == Const.ID_SENSOR_MOTION){
			System.out.println("came into sensor");
					Registry regs = null;
					try {
						regs = LocateRegistry.getRegistry(Const.CLIENT_SENSOR_MOTION_IP, Const.MOTION_SENSOR_PORT);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					 RMIDevicesInterfaces stSensorObj = null;
					try {
						stSensorObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_MOTION);
					} catch (RemoteException | NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//System.out.println("The Host is Dead");
					}

					try {
						stSensorObj.configureGateWay(Const.GATEWAY_SERVER_IP_REP,Const.GATEWAY_PORT_REP);
						stSensorObj=null;
					} catch (NullPointerException | RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}	
				
		}
		else if(UnicastControllingGatewayIPAddress.id == Const.ID_DOOR){

			System.out.println("came into sensor");
					Registry regs = null;
					try {
						regs = LocateRegistry.getRegistry(Const.DOOR_IP, Const.DOOR_PORT);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					 RMIDevicesInterfaces stSensorObj = null;
					try {
						stSensorObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_DOOR);
					} catch (RemoteException | NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//System.out.println("The Host is Dead");
					}

					try {
						stSensorObj.configureGateWay(Const.GATEWAY_SERVER_IP_REP,Const.GATEWAY_PORT_REP);
						stSensorObj=null;
					} catch (NullPointerException | RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}	
				
		
		}
		else if(UnicastControllingGatewayIPAddress.id == Const.ID_SENSOR_TEMPERATURE){
			System.out.println("came into sensor");
		Registry regs = null;
		try {
			regs = LocateRegistry.getRegistry(Const.CLIENT_SENSOR_TMPERATURE_IP, Const.TEMP_SENSOR_PORT);
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		 RMIDevicesInterfaces stSensorTempObj = null;
		try {
			stSensorTempObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_TEMP);
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println("The Host is Dead");
		}

		try {
			stSensorTempObj.configureGateWay(Const.GATEWAY_SERVER_IP_REP,Const.GATEWAY_PORT_REP);
			stSensorTempObj=null;
		} catch (NullPointerException | RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}	
	
			
		}
		
	}
	}
	
}
