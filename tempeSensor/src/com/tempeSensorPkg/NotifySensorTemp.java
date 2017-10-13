/**
 * Helper Class to run NotifySensorEventAction(double time) from Temperature Sensor to avoid Blocking of the code
 */
package com.tempeSensorPkg;

public class NotifySensorTemp implements Runnable {
 
	public static double time=0.0;
    public NotifySensorTemp(double t){
	  this.time=t;
    }
	public void run() {

		tempeSensorImpl.currentTempTimeStamp = time;
		tempeSensorImpl.startNextEventAction = true;
		System.out.println("Motion receiveNotifyEvent");
		
	}

}
