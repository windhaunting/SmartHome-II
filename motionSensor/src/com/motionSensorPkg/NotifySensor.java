/**
 * Helper Class to Run the NotifySensorEventAction(double time) Method in a seperate thread.
 */
package com.motionSensorPkg;

public class NotifySensor implements Runnable {
 
	public static double time=0.0;
  public NotifySensor(double t){
	  this.time=t;
  }
	public void run() {

		motionSensorImpl.currentMotionTimeStamp = time;
		motionSensorImpl.startNextEventAction = true;
		System.out.println("Motion receiveNotifyEvent");
		
	}

}
