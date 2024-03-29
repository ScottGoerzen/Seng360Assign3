package GrootChat;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.rmi.*;
import java.security.InvalidKeyException;


public interface RMICInterface extends Remote {
    public void MsgENC (byte[] msg) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
    public void Msg (String msg) throws RemoteException;
    public void MsgINT (String mac, String msg) throws RemoteException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
    public void MsgINTENC (String mac, byte[] msg) throws RemoteException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
    public void quit (RMIInterface server, RMICInterface client, String name) throws NoSuchObjectException, RemoteException;
}
