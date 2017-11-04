package test;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import test.RMIInterface;

public class ServerOperation extends UnicastRemoteObject implements RMIInterface {
    private static final long serialVersionUID = 1l;
    private RMIInterfaceClient client;
    private String name;

    protected ServerOperation() throws RemoteException {
        super();
    }

    @Override
    public String helloTo(String name) throws RemoteException {

        System.out.println(name + " is trying to contact!");
        return "Server says hello to " + name;

    }

    @Override
    public String Msg(String msg, String name) throws RemoteException {
        Scanner s = new Scanner(System.in);
        System.out.println("[Client "+name+"] " + msg);;
        String response = "I AM GR";
        for (int i = 0; i < (int)(Math.random()*1000); i++) {
            response += "O";
        }
        response += "T";

        return response;
    }

    public static void main(String[] args) {
        try {
            //Scanner s = new Scanner(System.in);
            Naming.rebind("//localhost/MyServer", new ServerOperation());
            System.out.println("Server Ready");

           // while (true) {
             //   String msg = s.nextLine().trim();
            //}

        } catch (Exception e) {

            System.out.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
