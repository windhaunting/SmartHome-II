/**
 * The User Can set mode to be AWAY or HOME and Report it to the Gateway. Gateway keeps track of the mode can can 
 * Respond to the Motion Sensor behavior depending on the Mode.
 */
package com.userPkg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;
import com.UserOperationInterface.UserOperationInterface;


public class UserOperation implements UserOperationInterface  {
	/**
	 *text_message(String message) --> Remotely called by the Gateway during AWAY mode to indicate the "Presense of an Intruder"
	 */
	public void text_message(String message) throws RemoteException {
		
		System.out.println(message);
		
	}
	/**
	 * readConfigIPFile() -->  An Helper function used to Read the IP Addresses of various machines(when operated on diff machines) 
	 *      from configuration file.
	 * The default value of IP address of the Machines is localhost.
	 */
	public static void readConfigIPFile(){
		String filename = Const.CONFIG_IPS_FILE;
		String workingDirectory = System.getProperty("user.dir");
		File file = new File(workingDirectory, filename);
		//System.out.println("Final filepath : " + file.getAbsolutePath());
		try {
			if (file.createNewFile()) {
				System.out.println("File is created!");
			} else {
				System.out.println("Read Ip Address from Configuration File");
			}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
 
        BufferedReader reader = null;   
        try {   
           // System.out.println("read begin：");   
            reader = new BufferedReader(new FileReader(file));   
            String tempString = null;   
            int line = 1;   
            while ((tempString = reader.readLine()) != null) {   
                  
                switch(line)
                {
	                case 1:
	                	Const.GATEWAY_SERVER_IP = tempString;
	                	//System.out.println("line " + line + ": " + tempString); 
	                	break;
	                default:
	                		break;
                }
                line++;
            } 
            
           // System.out.println(Const.GATEWAY_SERVER_IP+"outside");
            reader.close();   
        } catch (IOException e) {   
            e.printStackTrace();   
        } finally {   
            if (reader != null) {   
                try {   
                    reader.close();   
                } catch (IOException e1) {   
                }   
            }   
        }   
       
	}
	/**
	 * chngMode(String  mode) --> Helper method that is to be called in Main function.
	 *  It helps in looking up for Gateway  and can initiate the change_mode(String mode) implemented in Gateway.  
	 * @param mode
	 */
	public static void chngMode(String  mode){
		Registry regs = null;
		try {
				regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
			GatewayAllInterfaces gtwy=null;
			
			try {
				gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
				//gtwy.register(type, name);
				gtwy.change_mode(mode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("exception");
				e.printStackTrace();
			}				
	}
	public static void main(String[] args) throws Exception{
		if(0 == args.length)
		{  
			 System.out.println("lack of input parameter");
			 return;
		}
		
		if(args[0].equals(Const.CONFIG_IPS_FILE))
		{
			readConfigIPFile();  // Run Task1 and Task2 if only one command line Argument of configuration file
		} //Read the Ip address from Configuration file
		/**
		 * Creating a registry so that the Gateway can initiate Remote method call.
		 */
		UserOperation usrobj = new UserOperation();
		try{
			UserOperationInterface stub = (UserOperationInterface) UnicastRemoteObject.exportObject(usrobj,0);
			Registry reg;
			try{
				reg = LocateRegistry.createRegistry(Const.USER_OPERATION_PORT);       //heater port 1099 here;
				System.out.println("User Operation java RMI registry created.");
			}
			catch(Exception e){
            	System.out.println("User Opearation Using existing registry");
            	reg = LocateRegistry.getRegistry();
			}
			reg.rebind(Const.STR_LOOKUP_USER_OPERATION, stub);
		}catch(RemoteException e) 
		{
        	e.printStackTrace();
        }
		/**
		 * while(true)  loop is used so that the user can be able to enter the Mode of either in HOME or AWAY.
		 * As Soon as the Mode is changed the Gateway is accessed remotely to update the Mode at Gateway. 
		 */
		while(true){
			Scanner in= new Scanner(System.in);
		 System.out.println("Please Enter AWAY if going to Vacation is Present Enter HOME if came home");
	    String mode= in.nextLine();
	     
	    chngMode(mode); //call the Helper method which in turn performs RMI.
		}
	}

	

}
