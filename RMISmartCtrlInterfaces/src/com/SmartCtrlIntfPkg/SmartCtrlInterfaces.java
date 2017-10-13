/**
 * Interfaces to be used by the Smart Devices to create a Registry and Allow the GAteway to change their State.
 */
package com.SmartCtrlIntfPkg;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Stack;

public interface SmartCtrlInterfaces extends Remote {
	public boolean change_state(int device_id, int state) throws RemoteException;

	public void electionResult(String winner) throws RemoteException;
	public void ringAlgorithm(String s) throws RemoteException;
	public long provideTimeStamp() throws RemoteException;
	public void setOffsetTimeVariable(Long time) throws RemoteException;
	
	public boolean change_state(int device_id, int state, Stack<Integer> LogicClock) throws RemoteException;

	public void setFlagClockSync() throws RemoteException;
	public void configureGateWay(String GatewayIPaddress,int GatewayPort) throws RemoteException;
	
	 //////////// 
	 public void NotifySensorsDevicesCrashed(int CrashesId) throws RemoteException;
}
