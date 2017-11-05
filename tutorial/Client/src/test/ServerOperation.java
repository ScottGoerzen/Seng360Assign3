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
    public boolean auto;
    private String name;

    //AES crypto stuff
    private SecretKeySpec secretKey;
    private Cipher cipher;

    protected ServerOperation(String secret, int length) throws RemoteException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
        super();
        byte[] key = new byte[length];
        String algorithm = "AES";
        key = fixSecret(secret, length);
        this.secretKey = new SecretKeySpec(key, algorithm);
        this.cipher = Cipher.getInstance(algorithm);
    }

    private byte[] fixSecret(String s, int length) throws UnsupportedEncodingException {
        if (s.length() < length) {
            int missingLength = length - s.length();
            for (int i = 0; i < missingLength; i++) {
                s += " ";
            }
        }
        return s.substring(0, length).getBytes("UTF-8");
    }

    public byte[] encryptFile(String s) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        System.out.println("Encrypting string: " + s);
        this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        //Base64.Encoder base64Encoder = Base64.getEncoder();

        byte[] output = this.cipher.doFinal(s.getBytes());

//System.out.println(new String(base64Encoder(output)));

        return output;
    }

    public String decryptFile(byte[] s) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        System.out.println("Decrypting string: " + s);
        this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        ;
        byte[] output = this.cipher.doFinal(s);

        String l = new String(output);

System.out.println(l);

        return l;
    }


    @Override
    public SecretKeySpec helloTo(String name) throws RemoteException {

        System.out.println(name + " is trying to contact!");
        return secretKey;

    }

    @Override
    public byte[] Msg(byte[] msg, String name) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Scanner s = new Scanner(System.in);
        System.out.println("[Client: "+name+"] " + decryptFile(msg));
        String response;
        if (auto) {
            response = "I AM GR";
            for (int i = 0; i < (int) (Math.random() * 1000); i++) {
                response += "O";
            }
            response += "T";
        } else {
            response = s.nextLine().trim();
        }

        return encryptFile(response);
    }

    public static void main(String[] args) {
        try {

            //String realPass = "GROOT";
            String realPass;
            MD5Hash hasher = new MD5Hash();

            Scanner f = new Scanner(new File("test/ServerPassword.txt"));

            realPass = f.nextLine().trim();

            //String name = JOptionPane.showInputDialog("What is your name?");
            String pass = hasher.md5Hash(JOptionPane.showInputDialog("What is your password?"));

            int tries = 0;

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

            ServerOperation server = new ServerOperation("!@#$MySecr3tPassw0rd", 16);
            Naming.rebind("//localhost/MyServer", server);
            System.out.println("Server Ready");

            Scanner s = new Scanner(System.in);
            System.out.println("Do you wish to enable auto server responses? (y/n)");
            String res = s.nextLine().toLowerCase().trim();
            if (res.compareTo("y")==0) server.auto = true;
            else server.auto = false;



        } catch (Exception e) {

            System.out.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
