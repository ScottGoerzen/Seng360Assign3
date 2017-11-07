package test;

import java.io.*;
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
    public boolean auto;
    private String name;

    private boolean[] params;
    private static int numParams = 3;

    //AES crypto stuff
    private SecretKeySpec secretKey;
    private Cipher cipher;

    protected ServerOperation(String secret, int length) throws RemoteException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
        super();

        params = new boolean[this.numParams];
        params[0] = true; params[1] = true; params[2] = true;

        byte[] key = new byte[length];
        String algorithm = "AES";
        key = fixSecret(secret, length);
        this.secretKey = new SecretKeySpec(key, algorithm);
        this.cipher = Cipher.getInstance(algorithm);
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
    public SecretKeySpec helloTo(String name) throws RemoteException {

        System.out.println(name + " is trying to contact!");


        //Returns session key for AES encryption/decryption
        return secretKey;

    }

    //This method is the main communication between client and server. The client calls this method to pass its msg to the server
    //where the server decrypts, prints, and then either generatres and automatic response or waits for user input to respond.
    @Override
    public void MsgENC(byte[] msg, String name) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Scanner s = new Scanner(System.in);
System.out.println("Encrypted:: "+msg);
        //Incoming message is decrypted and printed to the terminal
        System.out.println("[Client: "+name+"] " + decryptFile(msg));
        /* String response;

        //if auto responses have been enabled, the server generates and automatic string based on a random number of 'O's in the string 'I AM GROOT'
       if (auto) {
            response = "I AM GR";
            for (int i = 0; i < (int) (Math.random() * 1000); i++) {
                response += "O";
            }
            response += "T";

            System.out.println("[Server] " + response);

        //else the server waits for a user typed input
        } else {
            //response = s.nextLine().trim();
        }

        //servers reponse in encrypted before being returned
        byte[] enc = encryptFile(response);*/

        //System.out.println("[Server Encrypted] " + new String(enc));

        //return enc;
    }

    @Override
    public void Msg(String msg) {
        System.out.println("[Server] " + msg);
    }

    public static void main(String[] args) {
        try {

            //String realPass = "GROOT";
            String realPass;
            MD5Hash hasher = new MD5Hash();

            //Reads hash of password from access controled file and stores it in the value realPass
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
            if (pass.compareTo(realPass)!=0) return;

            //Creates the server with a secret key and length for AES encryption on the ip //localhost/MyServer
            ServerOperation server = new ServerOperation("!@#$MySecr3tPassw0rd", 16);
            Naming.rebind("//localhost/MyServer", server);
            System.out.println("[System] Server Ready");

            //Options menu for selection security options
            int choice = -2;
            while (choice != -1) {
                String[] options = { "Confidentiality: "+server.params[0], "Integrity: "+server.params[1], "Availability: "+server.params[2], "Done" };
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
                        if (server.params[2])server.params[2] = false;
                        else server.params[2] = true;
                        break;
                    case 3:
                        choice = -1;
                        break;
                    default:
                        break;
                }
            }

            //Prompts user in commandline if they wish to have the server automatically generate responses
            Scanner s = new Scanner(System.in);
            System.out.println("You Ready?");

            String res = s.nextLine().toLowerCase().trim();
            //if (res.compareTo("y") == 0) server.auto = true;
            //else if (res.compareTo("n")==0) server.auto = false;
            //while (!server.hasClient);

            look_up = (RMICInterface) Naming.lookup("//localhost/MyClient");

            while (true) {
                //System.out.print("> ");

                String text = s.nextLine().trim();

                //If input message is '-Quit' then the client connection is terminated
                if (text.compareTo("-Quit")==0) {
                    System.out.println("[System] Connection ended");
                    break;
                } else if (text.compareTo("-cf")==0) {
                    server.params[0] = false;
                    continue;
                } else if (text.compareTo("-ct")==0) {
                    server.params[0] = true;
                    continue;
                } /*else if (text.compareTo("-if")==0) {
                    server.params[1] = false;
                    continue;
                } else if (text.compareTo("-it")==0) {
                    server.params[1] = true;
                    continue;
                } else if (text.compareTo("-af")==0) {
                    server.params[2] = false;
                    continue;
                } else if (text.compareTo("-at")==0) {
                    server.params[2] = true;
                    continue;
                }*/

                //encrypts message typed by client
                byte[] encoded = server.encryptFile(text);

                //System.out.println("[Message Encrypted] " + new String(encoded));

                //sends encryped message to the server, gets back a response, decrypts and prints out the servers message
                //System.out.println("[Client "+server.name+":] " + server.decryptFile(look_up.Msg(encoded)));

                if (server.params[0]) look_up.MsgENC(encoded);
                else if (!server.params[0]) look_up.Msg(text);

            }


        } catch (Exception e) {

            System.out.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
