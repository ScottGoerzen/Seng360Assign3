package test;

import java.rmi.*;
import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import javax.swing.JOptionPane;

import test.RMIInterface;

public class ClientOperation {
    private static RMIInterface look_up;

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        Scanner s = new Scanner(System.in);
        look_up = (RMIInterface) Naming.lookup("//localhost/MyServer");
        String txt = JOptionPane.showInputDialog("What is your name?");
        //String txt

        String response = look_up.helloTo(txt);
        System.out.println(response);
        //JOptionPane.showMessageDialog(null, response);

        while (true) {
            String msg = s.nextLine().trim();
            System.out.println("[Server] " + look_up.Msg(msg, txt));
        }

        //look_up = (RMIInterface) Naming.lookup("//localhost/MyServer");
        //String txt = JOptionPane.showInputDialog("What is your name?");
        //System.out.println("What is your name?");

        //String name = s.nextLine().trim();
        //look_up.helloTo(name);
        //String msg;

        //while (true) {
        //    msg = s.nextLine().trim();
        //}


        //String response = look_up.helloTo(txt);
        //JOptionPane.showMessageDialog(null, response);

        //while (true) {
//

  //      }

    }
}
