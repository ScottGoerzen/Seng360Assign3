package GrootChat;

import java.security.*;
import javax.crypto.*;
import java.io.*;
import java.nio.file.Files;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 *
 */
public class doRSA {

	/**
     * main method only for testing
	 * @param args pass in one argument to generate keys, otherwise, test keys
	 * @throws Exception
	 */
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
		
		PublicKey publicKey = getPublicKey("public.key");
		PrivateKey privateKey = getPrivateKey("private.key");
		
		byte[] bytes = encrypt(publicKey, "yoyoyoy");
		System.out.println(new String(bytes));
		System.out.println(decrypt(privateKey, bytes));
		
		
		
    }

    /**
     * fetches public key from path
     * @param path Path the the file with public key
     * @return requested public key
     */
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

    /**
     * fetches private key from path
     * @param path Path the the file with private key
     * @return requested public key
     */
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

    /**
     * Generate keys
     * @throws Exception
     */
    public static void genKeys () throws Exception {

		//setup
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyGen.initialize(2048, random);

		//generate
		KeyPair pair = keyGen.generateKeyPair();
		PrivateKey priv = pair.getPrivate();
		PublicKey pub = pair.getPublic();

		//turns into byte array
		byte[] publicKey = pub.getEncoded();
		byte[] privateKey = priv.getEncoded();

		//writes to files
		FileOutputStream fos = new FileOutputStream("public.key");
		fos.write(publicKey);
		fos.close();
		fos = new FileOutputStream("private.key");
		fos.write(privateKey);
		fos.close();
    }

    /**
     * encrypts using private key using RSA and the given key
     * @param privateKey Private key used to encrypt
     * @param message String to encrypt
     * @return encrypted bytes
     */
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

    /**
     * decrypts using public key
     * @param publicKey the public key used to decrypt
     * @param encrypted the encrypted bytes to decode
     * @return the original message in string format
     */
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

    /**
     * encrypts using public key
     * @param publicKey The public key used to encrypt the message
     * @param message The message to be encrypted
     * @return The encrypted bytes
     */
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

    /**
     * decrypts using private key
     * @param privateKey The private key used to decrypt
     * @param encrypted The bytes to be decrypted
     * @return The original message
     */
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
