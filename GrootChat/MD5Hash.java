package GrootChat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class MD5Hash {
  
    private final static String salt="^&*jgdaGYY65ad486gf65#44fashdgjkjkj668!^!^zfsduyifuidsf7773489";


    /**
     * Useful if you want to hash from command line
     * @param args the string to be hashed and printed to console
     */
    public static void main(String[] args) {
        System.out.println(md5Hash(args[0]));
    }

    /**
     * converts to md5 hashed string
     * @param message message to be hashed
     * @return hashed message
     */
    public static String md5Hash(String message) {
        String md5 = "";
        if(null == message) 
            return null;
         
        message = message+salt;//adding salt
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(message.getBytes(), 0, message.length());
            md5 = new BigInteger(1, digest.digest()).toString(16);
  
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }//md5Hash
}//MD5Hash