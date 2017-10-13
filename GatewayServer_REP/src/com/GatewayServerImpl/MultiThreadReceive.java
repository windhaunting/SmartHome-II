package com.GatewayServerImpl;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;
import com.SmartCtrlIntfPkg.SmartCtrlInterfaces;

public class MultiThreadReceive extends Thread {

    public String clientId;
    public int port;
   
    public MultiThreadReceive(String clientId, int port )
    {
        this.clientId = clientId;
        this.port = port;
    }
    
    public void run()
    {
      synchronized(this)
  	  {
        System.out.println("MultiThreadReceive begin here.");
     //   if (System.getSecurityManager() == null) {
        //    System.setSecurityManager(new SecurityManager());
      //  }
       try {

			GwServerInterfaceImplRep  gwImpl = new GwServerInterfaceImplRep();
			GatewayAllInterfaces stub2 = (GatewayAllInterfaces)UnicastRemoteObject.exportObject(gwImpl,0);
			Registry reg;
            try {
            	reg = LocateRegistry.createRegistry(Const.GATEWAY_PORT_REP);
                System.out.println("MultiThreadReceive java RMI registry created.");
            } catch(Exception e) {
            	System.out.println("Using existing registry");
            	reg = LocateRegistry.getRegistry();
            }
            
           reg.rebind(Const.STR_LOOKUP_GATEWAY_REP, stub2);	

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
    }
}
