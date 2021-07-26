
import java.rmi.Remote;
import java.rmi.RemoteException;
/*
Our server doesn't know anything about JavaFX.
It is independent of the graphics implementation.
 */
public interface ServerInterface extends Remote {
    void foo() throws RemoteException;
    boolean signup(String username, String password) throws RemoteException;
    boolean login(String username, String password) throws RemoteException;

    int addShape(double[] coords, String color, String type, String username, String password) throws RemoteException;
    int addText(String text, String color, String username, String password) throws RemoteException;
}
