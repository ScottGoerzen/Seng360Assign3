package test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.security.InvalidKeyException;


public interface RMIInterface extends Remote {
    public SecretKeySpec helloTo (String name) throws RemoteException, NotBoundException, MalformedURLException;
	public Object[] helloTo (byte[] EncryptedName) throws RemoteException, NotBoundException, MalformedURLException;
    public void MsgENC (byte[] msg, String name) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
    public void Msg (String msg) throws RemoteException;
    public boolean[] getParams () throws RemoteException;
    public void MsgINT (String mac, String msg) throws RemoteException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
    public void MsgINTENC (String mac, byte[] msg) throws RemoteException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
    public void RemoveClient(String name) throws RemoteException;
}
