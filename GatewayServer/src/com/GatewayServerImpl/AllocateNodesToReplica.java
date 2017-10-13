package com.GatewayServerImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;

public class AllocateNodesToReplica implements Runnable {
	
	private int idToSend=0;
	private String ipToSend="localhost";
	private String nameToSend="";

	public AllocateNodesToReplica(int id,String Name ,String ip_Address){
		this.idToSend=id;
		this.ipToSend=ip_Address;
		this.nameToSend=Name;
	}
	@Override
	public void run() {

        Registry regs = null;
        try {
            regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP_REP, Const.GATEWAY_PORT_REP);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        GatewayAllInterfaces gtwy=null;
        
        try {
            gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY_REP);
            gtwy.takeControlOfDevices(idToSend,nameToSend,ipToSend);  
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("exception");
            e.printStackTrace();
        }	
	}
}
