package test;
import java.rmi.*;


public interface RMIInterface extends Remote {
    public String helloTo (String name) throws RemoteException;
}
