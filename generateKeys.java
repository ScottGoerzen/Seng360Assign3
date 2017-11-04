import javax.crypto.spec.*;
import java.security.*;
import javax.crypto.*;
import java.io.*;
  
public class generateKeys
{
   public static void main(String []args) throws Exception {
      KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(1024);
      KeyPair kp = kpg.genKeyPair();
  
      byte[] publicKey = kp.getPublic().getEncoded();
      byte[] privateKey = kp.getPrivate().getEncoded();
  
      FileOutputStream fos = new FileOutputStream("public.key");
      fos.write(publicKey);
      fos.close();
      fos = new FileOutputStream("private.key");
      fos.write(privateKey);
      fos.close();
   }
}
