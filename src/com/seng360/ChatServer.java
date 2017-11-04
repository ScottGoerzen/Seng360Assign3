package com.seng360;

import com.seng360.ChatInterface;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.lang.SecurityManager;

public class ChatServer {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            Scanner s = new Scanner(System.in);
            System.out.println("Enter your name and press enter: ");
            String name = s.nextLine().trim();

            Chat server = new Chat(name);

            Chat stub = (Chat) UnicastRemoteObject.exportObject(server, 10280);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);

            /*Object context = null;
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                context = sm.getSecurityContext();
                sm.checkAccept("localhost", 10280);
            }*/

            //Naming.rebind("rmi://localhost/ABC", server);

            System.out.println("[System] Chat Remove Object is ready:");

            while (true) {
                String msg = s.nextLine().trim();
                if (server.getClient()!=null) {
                    ChatInterface client = server.getClient();
                    msg = "[" + server.getName()+ "]" + msg;
                    client.send(msg);
                }
            }
        } catch (Exception e) {
            System.out.println("[System] Server failed: " + e);
        }
    }
}
