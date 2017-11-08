package test;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import test.RMIInterface;

import javax.swing.*;



public class ServerOperation extends UnicastRemoteObject implements RMIInterface {
    private static final long serialVersionUID = 1l;
    private static RMICInterface look_up;
    private String name;
    private boolean client;

    public boolean[] params;
    private static int numParams = 2;

    //AES crypto stuff
    private SecretKeySpec secretKey;
    private Cipher cipher;

    protected ServerOperation(String secret, int length) throws RemoteException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
        super();

        params = new boolean[this.numParams];
        params[0] = true; params[1] = true;

        client = false;

        byte[] key = new byte[length];
        String algorithm = "AES";
        key = fixSecret(secret, length);
        this.secretKey = new SecretKeySpec(key, algorithm);
        this.cipher = Cipher.getInstance(algorithm);
    }

    public boolean[] getParams () throws RemoteException {
        return params;
    }

    //This method fixes the length of the key for AES encryption to the passed in length (16)
    private byte[] fixSecret(String s, int length) throws UnsupportedEncodingException {
        if (s.length() < length) {
            int missingLength = length - s.length();
            for (int i = 0; i < missingLength; i++) {
                s += " ";
            }
        }
        return s.substring(0, length).getBytes("UTF-8");
    }

    //This method encrypts the passed in string with and AES symmetric key and returns the encrypted byte[]
    private byte[] encryptFile(String s) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        //System.out.println("Encrypting string: " + s);
        //Set Cipher to Encrypt mode in multi step encryption
        this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);

        //Encryptes string in the final step of the multi step encryption
        byte[] output = this.cipher.doFinal(s.getBytes());

        return output;
    }

    //This method decrypts the passed in byte[] with AES symmetric key and returns the decrypted string
    private String decryptFile(byte[] s) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        //System.out.println("Decrypting string: " + s);
        //Set Cipher to Dercypt mode in multi step encryption
        this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);

        //Decrypts byte[] in the final step of the multi step decryption
        byte[] output = this.cipher.doFinal(s);

        //converts byte[] back to a String
        String l = new String(output);

        return l;
    }


    //This method is the inital 'handshake' when the client contacts the server, and the server returns the session key to the client
    @Override
    public SecretKeySpec helloTo(String name) throws RemoteException, NotBoundException, MalformedURLException {

        System.out.println(name + " is trying to contact!");
        this.name = name;
        client = true;

        //Returns session key for AES encryption/decryption
        return secretKey;

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
        if (mac.compareTo(verify)==0) System.out.println("[Client: "+name+"] " + msg+" [Integrity Checked]");
        else {
            System.out.println("[ERROR] MESSAGE INTEGRITY COMPROMISED!");
            System.out.println("[Client: "+name+"] " + msg+" [Integrity Failed]");
        }
    }

    //Recieves a message with integrity checks and encryption for confidentiality. Decrypts message, then verifies integrity against MAC
    @Override
    public void MsgINTENC(String mac, byte[] msg) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String text = decryptFile(msg);
        String verify = MAC(text);
        if (mac.compareTo(verify)==0) System.out.println("[Client: "+name+"] " + text+" [Integrity Checked and Confidential]");
        else {
            System.out.println("[ERROR] MESSAGE INTEGRITY COMPROMISED!");
            System.out.println("[Client: "+name+"] " + text+" [Integrity Failed but Confidential]");
        }
    }


    //This method is the main communication between client and server. The client calls this method to pass its msg to the server
    //where the server decrypts, prints, and then either generatres and automatic response or waits for user input to respond.
    @Override
    public void MsgENC(byte[] msg, String name) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Scanner s = new Scanner(System.in);
        //Incoming message is decrypted and printed to the terminal
        System.out.println("[Client: "+name+"] " + decryptFile(msg)+" [Confidential]");
    }

    @Override
    public void Msg(String msg) {
        System.out.println("[Client: "+name+"] " + msg);
    }

    public void RemoveClient(String name) throws RemoteException {
        this.client = false;
        System.out.println("[System] Client "+name+" has disconnected");
    }

    public static void main(String[] args) {
        try {

            //String realPass = "GROOT";
            String realPass;

            //Reads hash of password from access controled file and stores it in the value realPass
            MD5Hash hasher = new MD5Hash();
            Scanner f = new Scanner(new File("test/ServerPassword.txt"));
            realPass = f.nextLine().trim();

            //Prompts user loggin on for a password and hashes input
            String pass = hasher.md5Hash(JOptionPane.showInputDialog("What is your password?"));

            int tries = 0;

            //while the password has input does not equal the hash read from the file and there are tries left, it promps the user for a new password
            while (pass.compareTo(realPass)!=0) {
                JOptionPane.showMessageDialog(null, "Wrong Password");
                if (tries > 4) {
                    System.out.println("[Server] Connection ended");
                    JOptionPane.showMessageDialog(null, "HOW COULD YOU OFFEND THE GALAXIES. Wrong Password!!!");
                    return;
                }
                pass = hasher.md5Hash(JOptionPane.showInputDialog("What is your password? " + (5-tries) + " tries remaining"));
                tries++;
            }

            //only progresses in main if the correct password has been entered
            if (pass.compareTo(realPass)!=0) System.exit(1);

            //Creates the server with a secret key and length for AES encryption on the ip //localhost/MyServer
            ServerOperation server = new ServerOperation("!@#$MySecr3tPassw0rd", 16);
            Naming.rebind("//localhost/MyServer", server);
            System.out.println("[System] Server Ready");



            //Options menu for selection security options
            int choice = -2;
            while (choice != -1) {
                String[] options = { "Confidentiality: "+server.params[0], "Integrity: "+server.params[1], "Done" };
                choice = JOptionPane.showOptionDialog(null, "Select server paramaters", "Options", 0,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                switch (choice) {
                    case 0:
                        if (server.params[0])server.params[0] = false;
                        else server.params[0] = true;
                        break;
                    case 1:
                        if (server.params[1])server.params[1] = false;
                        else server.params[1] = true;
                        break;
                    case 2:
                        choice = -1;
                        break;
                    default:
                        break;
                }
            }

            //Prompts user to say when they are ready. Gives the client time to be started before the server tries to find them
            Scanner s = new Scanner(System.in);

            //infinite loop to wait for user input
            while (true) {

                    //reads user text in from the console
                    String text = s.nextLine().trim();

                    //If input message is '-Quit' then the client connection is terminated
                    if (text.compareTo("-Quit")==0) {
                        System.out.println("[System] Connection ended");
                        System.exit(1);
                    //Checks for commands to change security paramaters c -> confidentiality; i -> integrity; f -> false; t -> true
                    } else if (text.compareTo("-cf")==0) {
                        server.params[0] = false;
                        continue;
                    } else if (text.compareTo("-ct")==0) {
                        server.params[0] = true;
                        continue;
                    } else if (text.compareTo("-if")==0) {
                        server.params[1] = false;
                        continue;
                    } else if (text.compareTo("-it")==0) {
                        server.params[1] = true;
                        continue;
                    }



                    //encrypts message typed by client
                    byte[] encoded = server.encryptFile(text);

                    //takes message and gets back a MAC for that message
                    String maced = server.MAC(text);

                    //System.out.println("[Message Encrypted] " + new String(encoded));

                    //sends encryped message to the server, gets back a response, decrypts and prints out the servers message

                if (server.client) {
                    //look_up = (RMICInterface) Naming.lookup("//localhost/"+server.name);
                    look_up = (RMICInterface) Naming.lookup("//localhost/MyClient");

                    //Sends a message to the client based on the desired security preferences
                    if (server.params[0] && server.params[1]) look_up.MsgINTENC(maced, encoded);
                    else if (server.params[0]) look_up.MsgENC(encoded);
                    else if (server.params[1]) look_up.MsgINT(maced, text);
                    else look_up.Msg(text);
                } else {
                    look_up = null;
                    System.out.println("[System] No Client connected. You have no friends. ;(");
                }

            }


        } catch (Exception e) {

            System.out.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
