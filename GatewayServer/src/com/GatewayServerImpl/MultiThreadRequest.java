package com.GatewayServerImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.DevicesInterfaces.RMIDevicesInterfaces;
import com.GatewayInterface.Const;
import com.GatewayInterface.MessageTrans;
import com.SmartCtrlIntfPkg.SmartCtrlInterfaces;

public class MultiThreadRequest extends Thread {
	
	private int device_id;
	private int port;
	private String ClientId;      //locate device's id
	private boolean queryChangeFlag;		//=false is query, true is change state
	private int state_changed;             //for query_state, state_changed is as TasksOrTestcaseFunction, TasksOrTestcaseFunction = 1 for TestCase;
	private double currenTimeStamp;
	private Stack<Integer> LClockGW;
	
	private HashMap<String,Integer> ClockGWVlaue;
	
//	private String event;
	
	public MultiThreadRequest(boolean queryChangeFlag, String clientId, int port, int device_id, int state_changed, double time,
			Stack<Integer> LClockGW, HashMap<String,Integer> ClockGWVlaue)
	{
		this.queryChangeFlag = queryChangeFlag;
		this.port = port;
		this.ClientId = clientId;
		this.device_id = device_id;
		this.state_changed = state_changed;
		this.currenTimeStamp = time;
		this.LClockGW = LClockGW;
		this.ClockGWVlaue = ClockGWVlaue;
		//this.event = event;
	}
	
	public void run()
    {
	  synchronized(this)
	  {
		System.out.println("MultiThreadRequest begin here");
		Registry regs = null;
		try {
				regs = LocateRegistry.getRegistry(ClientId, port);
			//	regs = LocateRegistry.getRegistry(ClientId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Client Implemententation's class namecliImpl
		 switch(device_id)
		 {
			 case Const.ID_DEVICE_OUTLET: 				//change state
			 {
				 SmartCtrlInterfaces stHeaterObj = null;
					try {
						stHeaterObj = (SmartCtrlInterfaces)regs.lookup(Const.STR_LOOKUP_HEATER);
					} catch (RemoteException | NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boolean result = false;
					try {
						
						if(1 == state_changed)    //for lab1
						{
							result = stHeaterObj.change_state(device_id, state_changed);
						}
						else if((0 == state_changed) || (2 == state_changed))          //for lab2 
						{
							result = stHeaterObj.change_state(device_id, state_changed, LClockGW);
						
							LClockGW.add(LClockGW.peek()+1);
							ClockGWVlaue.put(Const.EVT_GW_HEATER_CHANGE, LClockGW.peek());
					    }
						
						int res  = (result) ? 1 : 0;
						GwServerInterfaceImpl.Device_States.put(device_id, res);

						
					} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}
		
					break;
			 	}
			 case Const.ID_DEVICE_BULB: 			//change state
			 {
				 SmartCtrlInterfaces stBulbObj = null;
					try {
						stBulbObj = (SmartCtrlInterfaces)regs.lookup(Const.STR_LOOKUP_SMART_BULB);
					} catch (RemoteException | NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boolean result = false;
					try {
						if(1 == state_changed)   	 //for lab1
						{
							result = stBulbObj.change_state(device_id, state_changed);
						}
						else if((0 == state_changed) || (2 == state_changed))           //for lab2 
						{

							result = stBulbObj.change_state(device_id, state_changed, LClockGW);
							LClockGW.add(LClockGW.peek()+1);
							ClockGWVlaue.put(Const.EVT_GW_BULB_CHANGE, LClockGW.peek());
							/////fw Log into file
							if(result)
							{
								int logicClk = LClockGW.peek();
						         String logicClockTime = Integer.toString(logicClk);
						         
						         String id= Integer.toString(device_id);
						         
						         String devType = "";
						         if(device_id <= 4)
						         {
						        	 devType = "Sensor";		 
						         }
						         else
						         {
						             devType = "Device";		 
						         }
						         
						         String inferredResult = "";
						         if(ClockGWVlaue.containsKey(Const.EVT_GW_MOTION_MOTION_SENSED) && ClockGWVlaue.containsKey(Const.EVT_DOOR_OPEN_SENSED))
						         {
							         if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) < ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
							        		 GwServerInterfaceImpl.Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.ON )
							         {
							        	 inferredResult = "User left home";
							        	 UpdateLogDatabase updateLog = new UpdateLogDatabase(logicClockTime,devType,id,Const.EVT_BULB_CHANGED,Const.LOGTYPE_PART23,inferredResult);
								         Thread t = new Thread(updateLog);
								         t.start();
								         t.join();
							         }
							         else  if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) < ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
							        		 GwServerInterfaceImpl.Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.OFF )
							         {
							        	 inferredResult = "Intruder left home";
							        	 UpdateLogDatabase updateLog = new UpdateLogDatabase(logicClockTime,devType,id,Const.EVT_BULB_CHANGED,Const.LOGTYPE_PART23,inferredResult);
								         Thread t = new Thread(updateLog);
								         t.start();
								         t.join();
	
							         }
							         else  if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) > ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
							        		 GwServerInterfaceImpl.Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.ON )
							         {
	
							        	 inferredResult = "User came home";
							        	 UpdateLogDatabase updateLog = new UpdateLogDatabase(logicClockTime,devType,id,Const.EVT_BULB_CHANGED,Const.LOGTYPE_PART23,inferredResult);
								         Thread t = new Thread(updateLog);
								         t.start();
								         t.join();

							         
							         }
							         else  if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) > ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
							        		 GwServerInterfaceImpl.Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.OFF )
							         {
							        	 inferredResult = "Intruder entered home";
							        	 inferredResult = "User came home";
							        	 UpdateLogDatabase updateLog = new UpdateLogDatabase(logicClockTime,devType,id,Const.EVT_BULB_CHANGED,Const.LOGTYPE_PART23,inferredResult);
								         Thread t = new Thread(updateLog);
								         t.start();
								         t.join();
							         }
							        
							        
						         }
							}
							
						}
						
						int res  = (result) ? 1 : 0;
						GwServerInterfaceImpl.Device_States.put(device_id, res);		
							
					} catch (RemoteException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				//	System.out.println("MultiThreadRequest change_state enter here");
				 break;
			 }
			 case Const.ID_SENSOR_TEMPERATURE: 				//query state
			 {
				 RMIDevicesInterfaces stTempObj = null;
				try {
						stTempObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_TEMP);
				} catch (RemoteException | NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
				int tempDegree = 0;
				try {
						
					if(0 == state_changed)			//for Task1
					{
						
					}
					else if(1 == state_changed)    //for TA's Testcase Function only
					{
						tempDegree = stTempObj.query_state(device_id);
						
						String outValue = Integer.toString(tempDegree);
		               if(GwServerInterfaceImpl.GatewayOutputHash.containsKey(currenTimeStamp))
		               {
		            	   String motion = GwServerInterfaceImpl.GatewayOutputHash.get(currenTimeStamp);
		            	   outValue = outValue.concat(","+motion);
		               }
		               System.out.println(" currenTimeStamp Temp here" + currenTimeStamp + "outValue " + outValue);
		               	 //output to the OutputHash file
		                GwServerInterfaceImpl.GatewayOutputHash.put(currenTimeStamp, outValue);
		                GwServerInterfaceImpl.GatewayOutputArrayStr.add(currenTimeStamp);
					}
					else if((0 == state_changed) ||(2 == state_changed))   
					{
						
						
						MessageTrans Retmsg = new MessageTrans();
						Retmsg = stTempObj.query_state(device_id, LClockGW);
						tempDegree = Retmsg.QueryStateResult;
						NotifiyHeaterOperation(tempDegree, LClockGW);


						LClockGW.add(LClockGW.peek()+1);
						ClockGWVlaue.put(Const.EVT_GW_TEMP_QUERY, LClockGW.peek());
						//Update logic clock
						Retmsg.LamportLogicClock(Retmsg.LogicClocks, LClockGW, Retmsg.RecvEvent, ClockGWVlaue);
						LClockGW = Retmsg.LogicClocks;
						ClockGWVlaue = Retmsg.ClockVlaue;
								
						System.out.println("The logic clock of query temp state Gateway Front-End is "+ LClockGW.toString());
					//	System.out.println("The logic clock of query temp state Gateway Front-End is "+ ClockGWVlaue.toString());

						//System.out.println(device_id + "Query result" + tempDegree);
					}
					
					GwServerInterfaceImpl.Device_States.put(device_id, tempDegree);


				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 break;
			 }
			 case Const.ID_SENSOR_MOTION: 			//query state
			 {
				 RMIDevicesInterfaces stMotionObj = null;
				try {
					stMotionObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_MOTION);
				} catch (RemoteException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int result = 0;
				try {
						

						if(1 == state_changed)   	//for TA's Testcase Function only
						{
							result = stMotionObj.query_state(device_id);
							
							String motionValue = Integer.toString(result);		//motion no or yes
							String outValue = motionValue;
					        if(GwServerInterfaceImpl.GatewayOutputHash.containsKey(currenTimeStamp))
					        {
					            String temp = GwServerInterfaceImpl.GatewayOutputHash.get(currenTimeStamp);
					            outValue = temp.concat("," + motionValue);
					        }
					        System.out.println(" currenTimeStamp motion here" + currenTimeStamp + "outValue " + outValue);
					        //output to the OutputHash file
					        GwServerInterfaceImpl.GatewayOutputHash.put(currenTimeStamp, outValue);
			                GwServerInterfaceImpl.GatewayOutputArrayStr.add(currenTimeStamp);
						}
						else if((0 == state_changed) ||(2 == state_changed))
						{
							MessageTrans Retmsg = new MessageTrans();
							
							Retmsg = stMotionObj.query_state(device_id, LClockGW);
							result = Retmsg.QueryStateResult;
														
							 if(result==Const.ON && GwServerInterfaceImpl.userMode.equals(Const.USER_MODE_HOME)){
									TimerforMotion.main(LClockGW);
								 }
								 if(result==Const.ON && GwServerInterfaceImpl.userMode.equals(Const.USER_MODE_AWAY)){
									SendNotification.main();
								 }
								 
							if(result == Const.NO_MOTION)
						    {
								Retmsg.RecvEvent = Const.EVT_GW_MOTION_MOTION_SENSED;

						    }
						    else
						    {
						        Retmsg.RecvEvent = Const.EVT_GW_MOTION_NO_MOTION_SENSED;
						    }
						//	System.out.println("The 189 logic motion e<<"+ Retmsg.RecvEvent);
							
							LClockGW.add(LClockGW.peek()+1);
							ClockGWVlaue.put(Const.EVT_GW_MOTION_QUERY, LClockGW.peek());
							
							//Update logic clock
							MessageTrans.LamportLogicClock(Retmsg.LogicClocks, LClockGW, Retmsg.RecvEvent, ClockGWVlaue);
							LClockGW = MessageTrans.LogicClocks;
							ClockGWVlaue = MessageTrans.ClockVlaue;
								
							System.out.println("The logic clock of query motion state Gateway Front-End is "+ LClockGW.toString());
							System.out.println("The logic clock of query motion state Gateway Front-End is "+ ClockGWVlaue.toString());

							/////fw Log into file
							int logicClk = LClockGW.peek();
					         String logicClockTime = Integer.toString(logicClk);
					         
					         String id= Integer.toString(device_id);
					         
					         String devType = "";
					         if(device_id <= 4)
					         {
					        	 devType = "Sensor";		 
					         }
					         else
					         {
					             devType = "Device";		 
					         }
					         
					         String inferredResult = "";
					         if(ClockGWVlaue.containsKey(Const.EVT_GW_MOTION_MOTION_SENSED) && ClockGWVlaue.containsKey(Const.EVT_DOOR_OPEN_SENSED))
					         {
						         if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) < ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
						        		 GwServerInterfaceImpl.Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.ON )
						         {
						        	 inferredResult = "User left home";
						        	 UpdateLogDatabase updateLog = new UpdateLogDatabase(logicClockTime,devType,id,Retmsg.RecvEvent,Const.LOGTYPE_PART23,inferredResult);
							         Thread t = new Thread(updateLog);
							         t.start();
							         t.join();
						         }
						         else  if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) < ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
						        		 GwServerInterfaceImpl.Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.OFF )
						         {
						        	 inferredResult = "Intruder left home";
						        	 UpdateLogDatabase updateLog = new UpdateLogDatabase(logicClockTime,devType,id,Retmsg.RecvEvent,Const.LOGTYPE_PART23,inferredResult);
							         Thread t = new Thread(updateLog);
							         t.start();
							         t.join();
	
						         }
						         else  if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) > ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
						        		 GwServerInterfaceImpl.Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.ON )
						         {
	
						        	 inferredResult = "User came home";
						        	 UpdateLogDatabase updateLog = new UpdateLogDatabase(logicClockTime,devType,id,Retmsg.RecvEvent,Const.LOGTYPE_PART23,inferredResult);
							         Thread t = new Thread(updateLog);
							         t.start();
							         t.join();
						         
						         }
						         else  if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) > ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
						        		 GwServerInterfaceImpl.Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.OFF )
						         {
						        	 inferredResult = "Intruder entered home";
						        	 UpdateLogDatabase updateLog = new UpdateLogDatabase(logicClockTime,devType,id,Retmsg.RecvEvent,Const.LOGTYPE_PART23,inferredResult);
							         Thread t = new Thread(updateLog);
							         t.start();
							         t.join();

						         }
						        
					         }
						}
						
						GwServerInterfaceImpl.Device_States.put(device_id, result);

						System.out.println(device_id + "Query motion result " + result);
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 break;
			 }
			 case Const.ID_DOOR: 			//query state
			 {
				 RMIDevicesInterfaces stDoorObj = null;
				try {
					stDoorObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_DOOR);
				} catch (RemoteException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int result = 0;
				try {
					
					if(1 == state_changed)    //for lab1 
					{
						result = stDoorObj.query_state(device_id);

					}
					else if((0 == state_changed) || (2 == state_changed))
					{
						MessageTrans Retmsg = new MessageTrans();
						
						Retmsg = stDoorObj.query_state(device_id, LClockGW);
						result = Retmsg.QueryStateResult;
						if(result == Const.OPEN)
					    {
							Retmsg.RecvEvent = Const.EVT_DOOR_OPEN_SENSED;

					    }
					    else
					    {
					        Retmsg.RecvEvent = Const.EVT_GW_DOOR_CLOSED_SENSED;
					    }
						
						LClockGW.add(LClockGW.peek()+1);
						ClockGWVlaue.put(Const.EVT_GW_DOOR_QUERY, LClockGW.peek());
						
						//Update Logic clock
						MessageTrans.LamportLogicClock(Retmsg.LogicClocks, LClockGW, Retmsg.RecvEvent, ClockGWVlaue);
						LClockGW = MessageTrans.LogicClocks;
						ClockGWVlaue = MessageTrans.ClockVlaue;
						
						System.out.println("The logic clock of query door state Gateway Front-End is "+ LClockGW.toString());
					//	System.out.println("The logic clock of query door state Gateway Front-End is "+ ClockGWVlaue.toString());
						
						/////fw Log into file
						int logicClk = LClockGW.peek();
				         String logicClockTime = Integer.toString(logicClk);
				         
				         String id= Integer.toString(device_id);
				         
				         String devType = "";
				         if(device_id <= 4)
				         {
				        	 devType = "Sensor";		 
				         }
				         else
				         {
				             devType = "Device";		 
				         }
				         
				         String inferredResult = "";
				         if(ClockGWVlaue.containsKey(Const.EVT_GW_MOTION_MOTION_SENSED) && ClockGWVlaue.containsKey(Const.EVT_DOOR_OPEN_SENSED))
				         {
					         if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) < ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
					        		 GwServerInterfaceImpl.Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.ON )
					         {
					        	 inferredResult = "User left home";
					        	 UpdateLogDatabase updateLog = new UpdateLogDatabase(logicClockTime,devType,id,Retmsg.RecvEvent,Const.LOGTYPE_PART23,inferredResult);
						         Thread t = new Thread(updateLog);
						         t.start();
						         t.join();
					         }
					         else  if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) < ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
					        		 GwServerInterfaceImpl.Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.OFF )
					         {
					        	 inferredResult = "User left home";
					        	 UpdateLogDatabase updateLog = new UpdateLogDatabase(logicClockTime,devType,id,Retmsg.RecvEvent,Const.LOGTYPE_PART23,inferredResult);
						         Thread t = new Thread(updateLog);
						         t.start();
						         t.join();
	
					         }
					         else  if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) > ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
					        		 GwServerInterfaceImpl.Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.ON )
					         {
	
					        	 inferredResult = "User came home";
					        	 inferredResult = "User left home";
					        	 UpdateLogDatabase updateLog = new UpdateLogDatabase(logicClockTime,devType,id,Retmsg.RecvEvent,Const.LOGTYPE_PART23,inferredResult);
						         Thread t = new Thread(updateLog);
						         t.start();
						         t.join();
					         
					         }
					         else  if(ClockGWVlaue.get(Const.EVT_GW_MOTION_MOTION_SENSED) > ClockGWVlaue.get(Const.EVT_DOOR_OPEN_SENSED) &&  
					        		 GwServerInterfaceImpl.Device_States.get(Const.ID_SENSOR_PRESENCE) == Const.OFF )
					         {
					        	 inferredResult = "Intruder entered home";
					        	 inferredResult = "User left home";
					        	 UpdateLogDatabase updateLog = new UpdateLogDatabase(logicClockTime,devType,id,Retmsg.RecvEvent,Const.LOGTYPE_PART23,inferredResult);
						         Thread t = new Thread(updateLog);
						         t.start();
						         t.join();
					         }
					        
				         }
						
					}

					GwServerInterfaceImpl.Device_States.put(device_id, result);

					System.out.println(device_id + "Query door result " + result);
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 break;
			 }
			 default:break;
    	}
     }
    }
	
	public static void NotifiyHeaterOperation(int tempDegree, Stack<Integer> logicClk)
	{	
			Registry regs = null;
				try {
					regs = LocateRegistry.getRegistry(Const.CLIENT_SMART_HEATER_IP, Const.HEATER_PORT);
				//	regs = LocateRegistry.getRegistry(ClientId);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
				SmartCtrlInterfaces stHeaterObj = null;
			try {
				stHeaterObj = (SmartCtrlInterfaces)regs.lookup(Const.STR_LOOKUP_HEATER);
				
			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		if(tempDegree < Const.TASK1_TEMP_LOW_THRESHOLD)
		{
				//turn on the heater 
				boolean notifyResult = false;
				try {
					notifyResult = stHeaterObj.change_state(Const.ID_DEVICE_OUTLET, Const.ON, logicClk);
					if(notifyResult)
					{
						System.out.println("heater is ON now");
						//don't forget to update state
						GwServerInterfaceImpl.Device_States.put(Const.ID_DEVICE_OUTLET, Const.ON);
											
					}
			} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}
		else if(tempDegree > Const.TASK1_TEMP_HIGH_THRESHOLD)
		{
				//turn off the heater 
				boolean notifyResult = false;
				try {
					notifyResult = stHeaterObj.change_state(Const.ID_DEVICE_OUTLET, Const.OFF,logicClk);
					if(notifyResult)
					{
						System.out.println("heater is OFF now");
						//don't forget to update state
						GwServerInterfaceImpl.Device_States.put(Const.ID_DEVICE_OUTLET, Const.OFF);
	
					
					}
			} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}
		return;
	}

	
}
