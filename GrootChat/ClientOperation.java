package GrootChat;

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

/**
 *
 */
public class ClientOperation extends UnicastRemoteObject implements RMICInterface {
	
	private static final long serialVersionUID = 1l;
	
    //to connect to the server
	private static RMIInterface look_up;
	//symmetric key
    private static SecretKeySpec secretKey;
	//cipher for the jey
    private static Cipher cipher;
	//if we should shutdown
    private boolean shutdown;
	//confidentiality, integrity, authentication
    private boolean[] params;
    private static int numParams = 3;

    /**
     * Constructor
     * @throws RemoteException
     */
    protected ClientOperation () throws RemoteException {
        super();

        shutdown = false;
		
		//Assume we want to be secure
        params = new boolean[numParams];
        params[0] = true; params[1] = true; params[2] = true;
    }

    /**
     * Message authentication code generator. Takes in a string, hashes the string, encrypts, then converts that to a string.
     * @param msg takes in a string message
     * @return returns a message authentication code for integrity checking
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    //Message authentication code generator. Takes in a string, hashes the string, encrypts, then converts that to a string.
    public String MAC(String msg) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        MD5Hash hasher = new MD5Hash();
        msg = hasher.md5Hash(msg);
        return new String(encryptFile(msg));
    }

    /**
     * Recieves a message with integrity checks. Runs the incoming msg through the MAC generator and compares the output to that of
     * the MAC that was given by the message sender
     * @param mac takes in a message authentication code to compare for integrity
     * @param msg takes in the message from the client
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    @Override
    public void MsgINT(String mac, String msg) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String verify = MAC(msg);
        if (mac.compareTo(verify)==0) System.out.println("[Server] " + msg+" [Integrity Checked]");
        else {
            System.out.println("[ERROR] MESSAGE INTEGRITY COMPROMISED!");
            System.out.println("[Server] " + msg+" [Integrity Failed]");
        }
    }

    /**
     * Recieves a message with integrity checks and encryption for confidentiality. Decrypts message, then verifies integrity against MAC
     * @param mac takes in a message authentication code to compare for integrity
     * @param msg takes in the encrypted message from the server
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
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

    /**
     * This method is the main communication between client and server. The server calls this method to pass its msg to the client.
     * Encrypted Version
     * @param msg takes in the encrypted message from the server
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    @Override
    public void MsgENC(byte[] msg) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        //Incoming message is decrypted and printed to the terminal
        System.out.println("[Server] " + decryptFile(msg)+" [Confidential]");
    }

    /**
     * @param msg takes in msg from the client
     * Unencryped Version, lame
     */
    @Override
    public void Msg(String msg) throws RemoteException {
        System.out.println("[Server] " + msg);
    }

    /**
     * This method encrypts the passed in string with and AES symmetric key and returns the encrypted byte[]
     * @param s is a string that contains the message to encrypt
     * @return A byte[] that contains the encrypted file
     * @throws InvalidKeyException
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] encryptFile(String s) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        //Set Cipher to Encrypt mode in multi step encryption
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        //Encryptes string in the final step of the multi step encryption
        byte[] output = cipher.doFinal(s.getBytes());

        return output;
    }

    /**
     * This method decrypts the passed in byte[] with AES symmetric key and returns the decrypted string
     * @param s is a byte[] that contains a message to decrypt
     * @return A string that contains the decrpted message
     * @throws InvalidKeyException
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String decryptFile(byte[] s) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        //System.out.println("Decrypting string: " + s);
        //Set Cipher to Dercypt mode in multi step encryption
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        //Decrypts byte[] in the final step of the multi step decryption
        byte[] output = cipher.doFinal(s);

        return new String(output);
    }

    /**
     * Quits and tells program to shutdown
     * @param server Takes in the server to send a quit message to
     * @param client Takes in the client (itself)
     * @param name brings in the name of the client to give to the server to say who is disconnecting
     * @throws NoSuchObjectException
     * @throws RemoteException
     */
    @Override
    public void quit (RMIInterface server, RMICInterface client, String name) throws NoSuchObjectException, RemoteException {
        server.RemoveClient(name);
        shutdown = true;
    }


    /**
     * Main method. Does everything.
     * @param args
     * @throws MalformedURLException
     * @throws RemoteException
     * @throws NotBoundException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Scanner s = new Scanner(System.in);

        //Reads real password hash from an access controlled file and stores as realPass
        String realPass;
        MD5Hash hasher = new MD5Hash();
        Scanner f = new Scanner(new File("GrootChat/HiddenClient/ClientPassword.txt"));
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
                System.exit(0);
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
            String[] options = { "Confidentiality: "+client.params[0], "Integrity: "+client.params[1], "Authentication: "+client.params[2], "Done" };
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
		//if true use RSA Encryption for handshake
        if (client.params[2]) {
			
			//pass the server our name, encrypted under the server public key
			//get back the name we passed them to make sure they are who they say they are
			//and also get a symmetric session key to use for future communications
			Object[] returned = look_up.helloTo(doRSA.encrypt(doRSA.getPublicKey("GrootChat/Public/publicServer.key"), name));
			
			//decrypt name and check if it matches
			String returnedName = doRSA.decrypt(doRSA.getPrivateKey("GrootChat/HiddenClient/privateClient.key"), (byte[])returned[1]);
			if (!name.equals(returnedName)) {
				//if it does not match, quit
				System.out.println("[System] Incorrect Expected Return; Closing connection");
				client.quit(look_up, client, name);
			}
			
			//if it does match, then decrypt the session key
			byte[] encryptedKey = (byte[])returned[0]; 
			String wild = doRSA.decrypt(doRSA.getPrivateKey("GrootChat/HiddenClient/privateClient.key"), encryptedKey);
			secretKey = new SecretKeySpec(Base64.getDecoder().decode(wild), "AES");
		
		} else {//otherwise pass key through plaintext
			secretKey = look_up.helloTo(name);
		}
		
		//we are using AES for the session key
		cipher = Cipher.getInstance(algorithm);
		//bind to server
        Naming.rebind("//localhost/MyClient", client);//+name, client);
        System.out.println("[System] Client Ready");

		//if security paramaters do not match, quit
        boolean[] servParam = look_up.getParams();
        if (client.params[0] != servParam[0] || client.params[1] != servParam[1] || client.params[2] != servParam[2]) {
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
            if (msg.compareTo("-Quit")==0 && !client.shutdown) {
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
