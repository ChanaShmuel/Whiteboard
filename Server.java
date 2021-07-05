import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Server implements ServerInterface{



    public static void main(String args[]) {
        try {
            ServerInterface myInterface = (ServerInterface) UnicastRemoteObject.exportObject(new Server(), 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("ServerInterface", myInterface);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void foo() throws RemoteException {
        System.out.println("in foo");
    }

    @Override
    public boolean signup(String username, String password) throws RemoteException {
        if(username == null || username.isEmpty() || password == null || password.isEmpty())
            return false;
        boolean success = DB.update("INSERT INTO users(username,password) VALUES (?,?)", statement -> {
            statement.setString(1, username);
            statement.setString(2, password);
        }) == 1;
        return success;
    }

    @Override
    public boolean login(String username, String password) throws RemoteException {

        if(username == null || username.isEmpty() || password == null || password.isEmpty())
            return false;
        Container<String> stringContainer = new Container<>();
        DB.query("SELECT password FROM users WHERE username=?",statement -> statement.setString(1, username),
                resultSet -> stringContainer.value = resultSet.getString(1));
        String passwordFromDb = stringContainer.value;

        return passwordFromDb != null && passwordFromDb.equals(password);
    }

    public static class Container<T>{
        public T value;
    }
}

