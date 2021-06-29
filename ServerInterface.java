import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    void foo() throws RemoteException;
    boolean signup(String username, String password) throws RemoteException;
    boolean login(String username, String password) throws RemoteException;
}
