package com.GatewayServerImpl;

public class LRUCacheImpl implements Runnable {
	
	//timeStamp, deviceType,deviceID,currentStatus,typeLog,inferredActivity
	private String timeStamp;
	private String deviceType;
	private Integer deviceID;
	private String currentStatus;
	private String typeLog;
	private String inferredActivity;
	private String StorageString;
	
	public LRUCacheImpl (String timeStam,String deviceTyp,String deviceI,String currentStatu,String typeLo,String inferredActivit){
		
		this.timeStamp=timeStam;
		this.deviceType=deviceTyp;
		this.deviceID=Integer.parseInt(deviceI);
		this.currentStatus=currentStatu;
		this.typeLog=typeLo;
		this.inferredActivity=inferredActivit;
		this.StorageString="Machine"+deviceI+"\t"+deviceTyp+"\t"+timeStam+"\t"+currentStatu;
		
	}
	public LRUCacheImpl (String deviceI,String deviceTyp,String timeStam,String currentStatu){
		
		this.deviceType=deviceTyp;
		this.deviceID=Integer.parseInt(deviceI.substring(7));
		this.currentStatus=currentStatu;
		this.StorageString="Machine"+deviceI+"\t"+deviceTyp+"\t"+timeStam+"\t"+currentStatu;
		
	}
	public void run(){
		
		if(GwServerInterfaceImpl.LRUcache.size() < GwServerInterfaceImpl.LRUCacheMaxSize ){
		GwServerInterfaceImpl.LRUcache.put(deviceID,StorageString);
		System.out.println("Cache has space and updated");
		}
		else{
			GwServerInterfaceImpl.LRUcache.remove(GwServerInterfaceImpl.CacheHelperQueue.getLast());
			GwServerInterfaceImpl.LRUcache.put(deviceID,StorageString);
			System.out.println("Cache doesnot have space but i have cleverly made space by LRU..!!!!!");
		}
		
		
	}
}
