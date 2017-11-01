package com.seng360;

import com.seng360.ChatInterface;

import java.rmi.*;
import java.util.*;

public class ChatServer {
    public static void main(String[] args) {
        try {
            System.setSecurityManager(new RMISecurityManager());
            Scanner s = new Scanner(System.in);
            System.out.println("Enter your name and press enter: ");
            String name = s.nextLine().trim();

            Chat server = new Chat(name);

            Naming.rebind("rmi://localhost/ABC", server);

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
