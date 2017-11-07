package test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.rmi.*;
import java.security.InvalidKeyException;


public interface RMIInterface extends Remote {
    public SecretKeySpec helloTo (String name) throws RemoteException;
    public void Msg (byte[] msg, String name) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
}
