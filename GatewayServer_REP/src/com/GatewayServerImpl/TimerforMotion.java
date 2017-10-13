package com.GatewayServerImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Stack;

import com.GatewayInterface.Const;
import com.SmartCtrlIntfPkg.SmartCtrlInterfaces;

public class TimerforMotion {

	public static void switchONbulb(int bulbState, Stack<Integer> LClockGW){
	
		SmartCtrlInterfaces stHeaterObj = null;
		try {
			Registry regs = null;
			regs = LocateRegistry.getRegistry(Const.CLIENT_SMART_BULB_IP,Const.SMART_BULB_SENSOR_PORT);
			
			stHeaterObj = (SmartCtrlInterfaces)regs.lookup(Const.STR_LOOKUP_SMART_BULB);
			
			
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean result = false;
		try {
			//result = stHeaterObj.change_state(Const.ID_DEVICE_BULB,bulbState);	
			result = stHeaterObj.change_state(Const.ID_DEVICE_BULB,bulbState,LClockGW);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(result)
		{
			System.out.println(Const.ID_DEVICE_BULB + "bulb light is changed successfully");
		}
		
	}
		
	public static void main(Stack<Integer> LClockGW) throws InterruptedException{
		 switchONbulb(Const.ON, LClockGW);
		
			Thread.sleep(5000);
			
		 switchONbulb(Const.OFF, LClockGW);	
	
		
	}
}
