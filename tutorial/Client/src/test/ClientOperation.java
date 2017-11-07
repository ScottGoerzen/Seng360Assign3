package test;

import java.io.File;
import java.io.*;
import java.io.FileNotFoundException;
import java.rmi.*;
import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.SecretKeySpec;


import javax.swing.*;

import test.RMIInterface;
import test.RMICInterface;
import test.MD5Hash;

public class ClientOperation extends UnicastRemoteObject implements RMICInterface {
    private static final long serialVersionUID = 1l;
    private static RMIInterface look_up;
    private static SecretKeySpec secretKey;
    private static Cipher cipher;

    private boolean[] params;
    private static int numParams = 3;

    protected ClientOperation () throws RemoteException {
        super();

        params = new boolean[numParams];
        params[0] = true; params[1] = true; params[2] = true;
    }


    @Override
    public String helloTo(String name) throws RemoteException {

        System.out.println(name + " is trying to contact!");

        return "hi";

    }

    @Override
    public void MsgENC(byte[] msg) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Scanner s = new Scanner(System.in);
System.out.println("Encrypted:: "+msg);
        //Incoming message is decrypted and printed to the terminal
        System.out.println("[Server] " + decryptFile(msg));
       // System.out.print("> ");


        //String response;

        //response = s.nextLine().trim();

        //servers reponse in encrypted before being returned
        //byte[] enc = encryptFile(response);

        //System.out.println("[Server Encrypted] " + new String(enc));

        //return enc;
    }

    @Override
    public void Msg(String msg) {
        System.out.println("[Server] " + msg);
        //System.out.print("> ");


    }

    //This method encrypts the passed in string with and AES symmetric key and returns the encrypted byte[]
    public static byte[] encryptFile(String s) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        //System.out.println("Encrypting string: " + s);
        //Set Cipher to Encrypt mode in multi step encryption
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        //Encryptes string in the final step of the multi step encryption
        byte[] output = cipher.doFinal(s.getBytes());

        return output;
    }

    //This method decrypts the passed in byte[] with AES symmetric key and returns the decrypted string
    public static String decryptFile(byte[] s) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        //System.out.println("Decrypting string: " + s);
        //Set Cipher to Dercypt mode in multi step encryption
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        //Decrypts byte[] in the final step of the multi step decryption
        byte[] output = cipher.doFinal(s);

        return new String(output);
    }

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Scanner s = new Scanner(System.in);
        //String realPass = "Rocket";
        String realPass;
        MD5Hash hasher = new MD5Hash();

        //Reads real password hash from an access controlled file and stores as realPass
        Scanner f = new Scanner(new File("test/ClientPassword.txt"));
        realPass = f.nextLine().trim();

        //Finds the server on the ip //localhost/MyServer
        look_up = (RMIInterface) Naming.lookup("//localhost/MyServer");

        //Prompts the client for a name and a password. the Name is just an identifier that is not really cared about
        //The password is compared to the hash from the file validates the client as the correct person
        String name = JOptionPane.showInputDialog("What is your name?");
        String pass = hasher.md5Hash(JOptionPane.showInputDialog("What is your password?"));

        int tries = 0;

        while (pass.compareTo(realPass)!=0) {
            JOptionPane.showMessageDialog(null, "Wrong Password");
            if (tries > 4) {
                System.out.println("[Server] Connection ended");
                JOptionPane.showMessageDialog(null, "Groot is upset with you. Wrong Password!!!");
                return;
            }
            pass = hasher.md5Hash(JOptionPane.showInputDialog("What is your password? " + (5-tries) + " tries remaining"));
            tries++;
        }

        //only progresses in main if the correct password has been entered
        if (pass.compareTo(realPass)!=0) return;



        //Authenticates with 'handshake' to server and gets a key for AES session key back
        String algorithm = "AES";
        secretKey = look_up.helloTo(name);
        cipher = Cipher.getInstance(algorithm);

        ClientOperation client = new ClientOperation();

        Naming.rebind("//localhost/MyClient", client);
        System.out.println("[System] Client Ready");

        //Options menu for selection security options
        int choice = -2;
        while (choice != -1) {
            String[] options = { "Confidentiality: "+client.params[0], "Integrity: "+client.params[1], "Availability: "+client.params[2], "Done" };
            choice = JOptionPane.showOptionDialog(null, "Select client paramaters", "Options", 0,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0:
                    if (client.params[0])client.params[0] = false;
                    else client.params[0] = true;
                    break;
                case 1:
                    if (client.params[1])client.params[1] = false;
                    else client.params[1] = true;
                    break;
                case 2:
                    if (client.params[2])client.params[2] = false;
                    else client.params[2] = true;
                    break;
                case 3:
                    choice = -1;
                    break;
                default:
                    break;
            }
        }

        //Infinite loop simply waits for client input to send to the server
        while (true) {
            //System.out.print("> ");

            String msg = s.nextLine().trim();

            //If input message is '-Quit' then the client connection is terminated
            if (msg.compareTo("-Quit")==0) {
                System.out.println("[System] Connection ended");
                return;
            } else if (msg.compareTo("-cf")==0) {
                client.params[0] = false;
                continue;
            } else if (msg.compareTo("-ct")==0) {
                client.params[0] = true;
                continue;
            } /*else if (msg.compareTo("-if")==0) {
                client.params[1] = false;
                continue;
            } else if (msg.compareTo("-it")==0) {
                client.params[1] = true;
                continue;
            } else if (msg.compareTo("-af")==0) {
                client.params[2] = false;
                continue;
            } else if (msg.compareTo("-at")==0) {
                client.params[2] = true;
                continue;
            }*/

            //encrypts message typed by client
            byte[] encoded = encryptFile(msg);

            //System.out.println("[Message Encrypted] " + new String(encoded));

            //sends encryped message to the server, gets back a response, decrypts and prints out the servers message
            //System.out.println("[Server] " + decryptFile(look_up.Msg(encoded, name)));
            if (client.params[0]) look_up.MsgENC(encoded, name);
            else if (!client.params[0]) look_up.Msg(msg);

        }



    }
}
