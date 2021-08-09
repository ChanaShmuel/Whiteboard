
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/*
Our server doesn't know anything about JavaFX.
It is independent of the graphics implementation.
 */
public interface ServerInterface extends Remote {
    void undo(String username, String password) throws RemoteException;
    boolean signup(String username, String password) throws RemoteException;
    boolean login(String username, String password) throws RemoteException;

    int addShape(double[] coords, String color, String type, String username, String password, String text) throws RemoteException;
    List<ShapeData> getShapes() throws RemoteException;


}
