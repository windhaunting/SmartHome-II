/**
 * Interface to be implemented by User operation and can be accessed by Gateway.
 */
package com.UserOperationInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserOperationInterface extends Remote {

	void text_message(String message) throws RemoteException;

}
