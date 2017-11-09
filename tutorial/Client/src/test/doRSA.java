package test;

import javax.crypto.spec.*;
import java.security.*;
import javax.crypto.*;
import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
  
public class doRSA {
	public static void main(String []args) throws Exception{
		if (args.length == 1 && args[0].equals("1")) {
			try {
				genKeys();
				System.out.println("Keys Generated");
			} catch (Exception e) {
				System.out.println("Key Generation failed");
				e.printStackTrace();
			}
			System.exit(1);
		}
		
		System.out.println("To generate keys call 'java doRSA 1'");
		/*byte[] bytes = Files.readAllBytes(new File("public.key").toPath());
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
		bytes = Files.readAllBytes(new File("private.key").toPath());
		PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));*/
		
		PublicKey publicKey = getPublicKey("public.key");
		PrivateKey privateKey = getPrivateKey("private.key");
		
		byte[] bytes = encrypt(publicKey, "yoyoyoy");
		System.out.println(new String(bytes));
		System.out.println(decrypt(privateKey, bytes));
		
		
		
    }
    
	public static PublicKey getPublicKey (String path) {
		try {
			byte[] bytes = Files.readAllBytes(new File(path).toPath());
			return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
		} catch (Exception e) {
			System.out.println("Failed to get public key\n");
			e.printStackTrace();
		}
		return null;
	}
	
	public static PrivateKey getPrivateKey (String path) {
		try {
			byte[] bytes = Files.readAllBytes(new File(path).toPath());
			return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
		} catch (Exception e) {
			System.out.println("Failed to get private key\n");
			e.printStackTrace();
		}
		return null;
	}
	
    public static void genKeys () throws Exception {
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
   
	public static byte[] encrypt(PrivateKey privateKey, String message) {
		
		try {
			Cipher cipher = Cipher.getInstance("RSA");  
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);  

			return cipher.doFinal(message.getBytes()); 
		} catch (Exception e) {
			System.out.println("Failed to encrypt with private key\n");
			e.printStackTrace();
		}
		
		return null;
		
		 
    }
    
    public static String decrypt(PublicKey publicKey, byte [] encrypted) {
		
		try {
			Cipher cipher = Cipher.getInstance("RSA");  
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
        
			return new String(cipher.doFinal(encrypted));
		} catch (Exception e) {
			System.out.println("Failed to decrypt with public key\n");
			e.printStackTrace();
		}
		
		return null;
    }
	
	public static byte[] encrypt(PublicKey publicKey, String message) {
		
		try {
			Cipher cipher = Cipher.getInstance("RSA");  
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);  

			return cipher.doFinal(message.getBytes());  
		} catch (Exception e) {
			System.out.println("Failed to encrypt with publicKey key\n");
			e.printStackTrace();
		}
		
		return null;
    }
    
    public static String decrypt(PrivateKey privateKey, byte [] encrypted) {
		
		try {
			Cipher cipher = Cipher.getInstance("RSA");  
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
			return new String(cipher.doFinal(encrypted));
		} catch (Exception e) {
			System.out.println("Failed to decrypt with private key\n");
			e.printStackTrace();
		}
		
		return null;
    }
}
