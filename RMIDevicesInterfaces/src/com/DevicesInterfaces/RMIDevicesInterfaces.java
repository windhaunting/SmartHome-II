/**
 * Interfaces implemented by Devices which can be accessed by Gateway
 */
package com.DevicesInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Stack;

import com.GatewayInterface.MessageTrans;

public interface RMIDevicesInterfaces extends Remote{
	public int query_state(int device_id) throws RemoteException;
	public int query_state(double time, int device_id) throws RemoteException;
	public void NotifySensorEventAction(double time) throws RemoteException;

	public void electionResult(String winner) throws RemoteException;
	public void ringAlgorithm(String s) throws RemoteException;
	public long provideTimeStamp() throws RemoteException;
	public void setOffsetTimeVariable(Long time) throws RemoteException;
	

		
	public MessageTrans query_state(int device_id, Stack<Integer> SendLogicClock) throws RemoteException;

	 public void setFlagClockSync() throws RemoteException;
	 
	 public void configureGateWay(String GatewayIPaddress,int GatewayPort) throws RemoteException;
	 
	 //////////// 
	 public void NotifySensorsDevicesCrashed(int CrashesId) throws RemoteException;

}

