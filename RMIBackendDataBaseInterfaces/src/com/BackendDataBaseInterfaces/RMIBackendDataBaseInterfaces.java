package com.BackendDataBaseInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIBackendDataBaseInterfaces extends Remote {
	
	public void updateDatabase(String timeStamp, String deviceType,String deviceID, String currentStatus, String typeLog,String event) throws RemoteException;
	public String getStatusFromLog(String timeStamp,String typeInfo) throws RemoteException;
	public void electionResult(String winner) throws RemoteException;
	public void ringAlgorithm(String s) throws RemoteException;
	public long provideTimeStamp() throws RemoteException;
	public void setOffsetTimeVariable(Long time) throws RemoteException;
	
	 public void setFlagClockSync() throws RemoteException;
	 public void configureGateWay(String GatewayIPaddress,int GatewayPort) throws RemoteException;
	
}

