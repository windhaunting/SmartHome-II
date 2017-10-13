/**
 * Interfaces implemented by Gateway which can be accessed by Sensors or Devices
 */
package com.GatewayInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Stack;

public interface GatewayAllInterfaces extends Remote {
	
  public  void register(int type, String name,String ip) throws RemoteException;
  public  void report_state(int device_id,  int state) throws RemoteException;
  public  void change_mode(String mode) throws RemoteException;
  public void report_state(int device_id, int state, double time) throws RemoteException;
  public  void FinishCurrentTimeEventToGateway(int finishOne) throws RemoteException;
  public void electionResult(String winner) throws RemoteException;
  public void ringAlgorithm(String s) throws RemoteException;
  public long provideTimeStamp() throws RemoteException;
  public void setOffsetTimeVariable(Long time) throws RemoteException;
  

  public  void register(int type, int device_id, String name,String ip, Stack<Integer> SendLogicClock, String event) throws RemoteException; 		//added vector clock
  public void report_state(int device_id, int state, Stack<Integer> SendLogicClock, String event) throws RemoteException;   //added lamport logic clock

  public void setFlagClockSync() throws RemoteException;
  
  public void takeControlOfDevices(int type,String name, String ip_Address) throws RemoteException;
  
  public void makeDataConsistent(int state,int device_id) throws RemoteException;
   public void HeartMessage(String message) throws RemoteException;
}
