import javax.crypto.spec.*;
import java.security.*;
import javax.crypto.*;
import java.io.*;
  
public class generateKeys
{
   public static void main(String []args) throws Exception {
      //KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");

      //KeyPair kp = kpg.genKeyPair();

      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
      keyGen.initialize(2048, random);

      KeyPair pair = keyGen.generateKeyPair();
      PrivateKey priv = pair.getPrivate();
      PublicKey pub = pair.getPublic();

  
      byte[] publicKey = pub.getEncoded();
      byte[] privateKey = priv.getEncoded();



      FileOutputStream fos = new FileOutputStream("public.key");
      fos.write(publicKey);
      fos.close();
      fos = new FileOutputStream("private.key");
      fos.write(privateKey);
      fos.close();
   }
}
