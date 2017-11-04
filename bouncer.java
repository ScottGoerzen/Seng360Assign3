// the goal of this was to make a java program to hash plain text and checks it against another hash
// this code does not compile because the provider, FlexiCore, is out of date, from what I can tell.
// the code was found here ----> https://www.flexiprovider.de/examples/ExampleDigest.html
// info about MessageDigest can be found here ---> https://docs.oracle.com/javase/7/docs/api/java/security/MessageDigest.html

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.Security;

import de.flexiprovider.common.util.ByteUtils;
import de.flexiprovider.core.FlexiCoreProvider;

public class bouncer {

    public static void main(String[] args) throws Exception {

	Security.addProvider(new FlexiCoreProvider());

	File file = new File("md1.txt");
	byte[] buffer = new byte[(int) file.length()];
	FileInputStream fis = new FileInputStream(file);
	fis.read(buffer);
	fis.close();
										// FlexiCore causes its gotta match
	MessageDigest md = MessageDigest.getInstance("MD5", "FlexiCore");

	md.update(buffer);
	byte[] digest = md.digest();

	System.out.println("MD5 fingerprint: " + ByteUtils.toHexString(digest));
    }

}
