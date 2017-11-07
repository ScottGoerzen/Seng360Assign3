package test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.rmi.*;
import java.security.InvalidKeyException;


public interface RMICInterface extends Remote {
    public String helloTo (String name) throws RemoteException;
    public void Msg (byte[] msg) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
}
