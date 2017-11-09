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
import test.doRSA;

public class ClientOperation extends UnicastRemoteObject implements RMICInterface {
    private static final long serialVersionUID = 1l;
    private static RMIInterface look_up;
    private static SecretKeySpec secretKey;
    private static Cipher cipher;

    private boolean shutdown;

    private boolean[] params;
    private static int numParams = 3;

    protected ClientOperation () throws RemoteException {
        super();

        shutdown = false;

        params = new boolean[numParams];
        params[0] = true; params[1] = true; params[2] = true;
    }


    @Override
    public String helloTo(String name) throws RemoteException {

        System.out.println(name + " is trying to contact!");

        return "hi";

    }

    //Message authentication code generator. Takes in a string, hashes the string, encrypts, then converts that to a string.
    public String MAC(String msg) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        MD5Hash hasher = new MD5Hash();
        msg = hasher.md5Hash(msg);
        return new String(encryptFile(msg));
    }

    //Recieves a message with integrity checks. Runs the incoming msg through the MAC generator and compares the output to that of
    //the MAC that was given by the message sender
    @Override
    public void MsgINT(String mac, String msg) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String verify = MAC(msg);
        if (mac.compareTo(verify)==0) System.out.println("[Server] " + msg+" [Integrity Checked]");
        else {
            System.out.println("[ERROR] MESSAGE INTEGRITY COMPROMISED!");
            System.out.println("[Server] " + msg+" [Integrity Failed]");
        }
    }

    //Recieves a message with integrity checks and encryption for confidentiality. Decrypts message, then verifies integrity against MAC
    @Override
    public void MsgINTENC(String mac, byte[] msg) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String text = decryptFile(msg);
        String verify = MAC(text);
        if (mac.compareTo(verify)==0) System.out.println("[Server] " + text+" [Integrity Checked and Confidential]");
        else {
            System.out.println("[ERROR] MESSAGE INTEGRITY COMPROMISED!");
            System.out.println("[Server] " + text+" [Integrity Failed but Confidential]");
        }
    }

    //This method is the main communication between client and server. The client calls this method to pass its msg to the server
    //where the server decrypts, prints, and then either generatres and automatic response or waits for user input to respond.
    @Override
    public void MsgENC(byte[] msg) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        //Incoming message is decrypted and printed to the terminal
        System.out.println("[Server] " + decryptFile(msg)+" [Confidential]");
    }

    @Override
    public void Msg(String msg) throws RemoteException {
        System.out.println("[Server] " + msg);
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

    @Override
    public void quit (RMIInterface server, RMICInterface client, String name) throws NoSuchObjectException, RemoteException {
        server.RemoveClient(name);
        shutdown = true;
        //UnicastRemoteObject.unexportObject(client, true);
    }

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Scanner s = new Scanner(System.in);
        //String realPass = "Rocket";

        //Reads real password hash from an access controlled file and stores as realPass
        String realPass;
        MD5Hash hasher = new MD5Hash();
        Scanner f = new Scanner(new File("test/HiddenClient/ClientPassword.txt"));
        realPass = f.nextLine().trim();

        //Finds the server on the ip //localhost/MyServer
        look_up = (RMIInterface) Naming.lookup("//localhost/MyServer");

        //Prompts the client for a name and a password. the Name is just an identifier that is not really cared about
        //The password is compared to the hash from the file validates the client as the correct person
        String name = JOptionPane.showInputDialog("What is your name?");
        String pass = hasher.md5Hash(JOptionPane.showInputDialog("What is your password?"));

        int tries = 0;
        ClientOperation client = new ClientOperation();

        while (pass.compareTo(realPass)!=0) {
            JOptionPane.showMessageDialog(null, "Wrong Password");
            if (tries > 4) {
                JOptionPane.showMessageDialog(null, "Groot is upset with you. Wrong Password!!!");
                client.quit(look_up, client, name);
            }
            pass = hasher.md5Hash(JOptionPane.showInputDialog("What is your password? " + (5-tries) + " tries remaining"));
            tries++;
        }

        //only progresses in main if the correct password has been entered
        if (pass.compareTo(realPass)!=0) System.exit(1);





        //Creates 'client server' for the server to find on ip //localhost/MyClient


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

        //Authenticates with 'handshake' to server and gets a key for AES session key back
        String algorithm = "AES";
        if (client.params[2]) { //if true use RSA Encryption for handshake
			secretKey = look_up.helloTo(doRSA.encrypt(doRSA.getPublicKey("test/Public/publicServer.key"), name));
			System.out.println("//True");
		} else {//otherwise pass key through plaintext
			secretKey = look_up.helloTo(name);
			System.out.println("//false");
		}
		cipher = Cipher.getInstance(algorithm);

        Naming.rebind("//localhost/MyClient", client);//+name, client);
        System.out.println("[System] Client Ready");



        boolean[] servParam = look_up.getParams();
        if (client.params[0] != servParam[0] || client.params[1] != servParam[1] || client.params[3] != servParam[2]) {
            System.out.println("[System] Wrong security options; Closing connection");
            client.quit(look_up, client, name);
        }

        String affirm = "has connected";
        look_up.MsgINTENC(client.MAC(affirm) , encryptFile(affirm));

        //Infinite loop simply waits for client input to send to the server
        while (true) {
            if(client.shutdown) System.exit(1);

            String msg = s.nextLine().trim();

            //If input message is '-Quit' then the client connection is terminated
            if (msg.compareTo("-Quit")==0) {
                System.out.println("[System] Connection ended");
                //System.exit(1);
                s.close();
                client.quit(look_up, client, name);
            }


            //encrypts message typed by client
            byte[] encoded = encryptFile(msg);

            //Creates MAC from the desired message
            String maced = client.MAC(msg);

            //sends message to the server with selected security properties
            if (!client.shutdown) {
                if (client.params[0] && client.params[1]) look_up.MsgINTENC(maced, encoded);
                else if (client.params[0]) look_up.MsgENC(encoded, name);
                else if (client.params[1]) look_up.MsgINT(maced, msg);
                else look_up.Msg(msg);
            }

        }



    }
}
