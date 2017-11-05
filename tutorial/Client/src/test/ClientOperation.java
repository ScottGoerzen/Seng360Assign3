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
import test.MD5Hash;

public class ClientOperation {
    private static RMIInterface look_up;
    private static SecretKeySpec secretKey;
    private static Cipher cipher;

    public static byte[] encryptFile(String s) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        System.out.println("Encrypting string: " + s);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] output = cipher.doFinal(s.getBytes());

        return output;
    }

    public static String decryptFile(byte[] s) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        System.out.println("Decrypting string: " + s);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] output = cipher.doFinal(s);


        return new String(output);
    }

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Scanner s = new Scanner(System.in);
        //String realPass = "Rocket";
        String realPass;
        MD5Hash hasher = new MD5Hash();

        Scanner f = new Scanner(new File("test/ClientPassword.txt"));

        realPass = f.nextLine().trim();


        look_up = (RMIInterface) Naming.lookup("//localhost/MyServer");
        String name = JOptionPane.showInputDialog("What is your name?");
        String pass = hasher.md5Hash(JOptionPane.showInputDialog("What is your password?"));
        //String pass = JOptionPane("What is your password?");
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

        //String response = look_up.helloTo(name);
        String algorithm = "AES";
        secretKey = look_up.helloTo(name);
        cipher = Cipher.getInstance(algorithm);
        //System.out.println(response);
        //JOptionPane.showMessageDialog(null, response);

        while (true) {
            String msg = s.nextLine().trim();

            if (msg.compareTo("-Quit")==0) {
                System.out.println("[Server] Connection ended");
            }

            byte[] encoded = encryptFile(msg);

            System.out.println("[Server] " + decryptFile(look_up.Msg(encoded, name)));

        }



    }
}
