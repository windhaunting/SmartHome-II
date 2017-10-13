package com.GatewayServerImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.GatewayInterface.Const;
import com.UserOperationInterface.UserOperationInterface;

public class SendNotification {
	public static void sendNotification(){
		Registry regs = null;
		try {
				regs = LocateRegistry.getRegistry(Const.USER_OPERATION_IP, Const.USER_OPERATION_PORT);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			UserOperationInterface gtwy=null;
			
			try {
				gtwy = (UserOperationInterface)regs.lookup(Const.STR_LOOKUP_USER_OPERATION);
				gtwy.text_message("Intruder present in home");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("exception");
				e.printStackTrace();
			}	
	}
	public static void main(){
		sendNotification();
	}
}
