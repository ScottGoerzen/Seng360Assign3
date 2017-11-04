package com.seng360;

import com.seng360.ChatInterface;

import java.rmi.*;
import java.util.*;
import java.rmi.registry.*;
import java.lang.SecurityManager;

public class ChatServer {
    public static void main(String[] args) {
        //if (System.getSecurityManager() == null) {
          //  System.setSecurityManager(new SecurityManager());
        //}

        try {
            Scanner s = new Scanner(System.in);
            System.out.println("Enter your name and press enter: ");
            String name = s.nextLine().trim();

            Chat server = new Chat(name);
//System.out.println("Blah");
            //Chat stub = (Chat) UnicastRemoteObject.exportObject(server, 10029);
System.out.println("Blah");
System.out.println("Blah");
System.out.println("Blah");
System.out.println("boop");
            /*Object context = null;
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                context = sm.getSecurityContext();
                sm.checkAccept("localhost", 10280);
            }*/
            //LocateRegistry.getRegistry(10280);
            //Naming.rebind("//localhost/ABC", server);
            //Naming.rebind("//localhost/ABC", new Chat(name));
 System.out.println("Hi :)");

            System.out.println("[System] Chat Remote Object is ready:");

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
