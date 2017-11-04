package com.seng360;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import com.seng360.ChatInterface;

public class Chat extends UnicastRemoteObject implements ChatInterface {
    private static final long serialVersionUID = 1l;
    public String name;
    public ChatInterface client = null;

    public Chat (String n) throws RemoteException, MalformedURLException {
        super();
        this.name = n;
        Naming.rebind("rmi://localhost/ABC", this);
System.out.println("IM HERE");
    }

    public String getName() throws RemoteException {
        return this.name;
    }

    public void setClient(ChatInterface c) {
        client = c;

    }

    public ChatInterface getClient() {
        return client;
    }

    public void send(String s) throws RemoteException {
        System.out.println(s);
    }

    /*public static void main(String[] args) {
        try {
            Naming.rebind("//localhost/ABC", new Chat());
            System.out.println("Server Ready");

        } catch (Exception e) {

            System.out.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }*/
}
