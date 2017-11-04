package test;

import java.rmi.*;


public interface RMIInterface extends Remote {
    public String helloTo (String name) throws RemoteException;
    public String Msg (String msg, String name) throws RemoteException;
}
