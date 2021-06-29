import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Server implements ServerInterface{
    private Map<String, String> users;

    public Server() {
        this.users = new HashMap<>();
        users.put("elad", "qwe123");
    }

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
        if(users.containsKey(username))
            return false;
        users.put(username, password);
        return true;
    }

    @Override
    public boolean login(String username, String password) throws RemoteException {
        try {
            Thread.sleep(5000);//simulate IO slowness
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(username == null || username.isEmpty() || password == null || password.isEmpty())
            return false;
        if(!users.containsKey(username))
            return false;
        String existingPassword = users.get(username);
        return existingPassword.equals(password);
    }
}
