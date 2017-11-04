package test;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import test.RMIInterface;

public class ServerOperation extends UnicastRemoteObject implements RMIInterface {
        private static final long serialVersionUID = 1l;

        protected ServerOperation() throws RemoteException {
            super();
        }

    @Override
    public String helloTo(String name) throws RemoteException {

        System.out.println(name + " is trying to contact!");

        return "Server says hello to " + name;
    }

    public static void main(String[] args) {
        try {

            Naming.rebind("//localhost/MyServer", new ServerOperation());
            System.out.println("Server Ready");

        } catch (Exception e) {

            System.out.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
