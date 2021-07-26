import java.io.*;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Server implements ServerInterface{


    public static final String SERVER_INTERFACE = "ServerInterface";
    private static int counter = 0;

    public static void main(String args[]) {
        if(!readConfigurationFile())
            throw new RuntimeException("failed to read configuration file");
        try {
            ServerInterface myInterface = (ServerInterface) UnicastRemoteObject.exportObject(new Server(), 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(SERVER_INTERFACE, myInterface);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

    }

    public static boolean readConfigurationFile(){
        File confFile = new File("conf.txt");
        if(confFile.exists()) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(confFile);
                byte[] buffer = new byte[256];
                int nextByte;
                int actuallyRead = -1;
                while ((nextByte = inputStream.read()) != -1) {
                    buffer[++actuallyRead] = (byte) nextByte;
                    if (actuallyRead == 255)
                        throw new RuntimeException("configuration file is too long");
                }
                if(actuallyRead > -1) {
                    String configurationString = new String(buffer, 0, actuallyRead+1);
                    String[] parts = configurationString.split("&");
                    if(parts.length >= 3){
                        DB.URL_TO_MYSQL_DB = parts[0];
                        DB.SQL_USERNAME = parts[1];
                        DB.SQL_PASSWORD = parts[2];
                    }
                    return true;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return false;
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

    @Override
    public int addShape(double[] coords, String color, String type, String username, String password) throws RemoteException {
        if(!login(username,password))
            return -1;

        return 0;
    }

    @Override
    public int addText(String text, String color, String username, String password) throws RemoteException {
        if(!login(username,password))
            return -1;

        return 0;
    }

    public static class Container<T>{
        public T value;
    }
}

