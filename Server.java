import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.nio.ByteBuffer;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
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
                while ((nextByte = inputStream.read()) != -1) {//-1 means end of stream
                    if (actuallyRead == 255)
                        throw new RuntimeException("configuration file is too long");
                    buffer[++actuallyRead] = (byte) nextByte;
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
    public void undo(String username, String password) throws RemoteException {
        if(username == null || username.isEmpty() || password == null || password.isEmpty())
            return;
        if(!login(username,password))
            return;
        DB.update("DELETE FROM shapes WHERE username=? AND id=(SELECT MAX(id) FROM (SELECT id FROM shapes WHERE username=?)tblTemp)",
                statement -> {
            statement.setString(1, username);
            statement.setString(2, username);
                });
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
        final Container<String> stringContainer = new Container<>();
        DB.QueryListener queryListener = resultSet -> stringContainer.value = resultSet.getString(1);
        DB.query("SELECT password FROM users WHERE username=?",statement -> statement.setString(1, username),
                queryListener);
        String passwordFromDb = stringContainer.value;
        return passwordFromDb != null && passwordFromDb.equals(password);
    }

    /**
     *
     * @param coords Geometry information of the shape, depends on the type of the shape
     * @param color the color of the shape as hexadecimal
     * @param type either ellipse, point, text, rectangle, line, etc.
     * @param username the user created the shape
     * @param password
     * @param text relevant only if type is "text"
     * @return the id of the newly inserted row.
     * @throws RemoteException
     */
    @Override
    public int addShape(double[] coords, String color, String type, String username, String password, String text) throws RemoteException {
        if(!login(username,password))
            return -1;
        int id = DB.executeUpdateReturnGeneratedKeys("insert into shapes(geometry,username,type,color,text) VALUES (?,?,?,?,?)", statement -> {
            SerialBlob blob = new SerialBlob(doubleArrayToByteArray(coords));
            statement.setBlob(1, blob);
            statement.setString(2, username);
            statement.setString(3, type);
            statement.setString(4, color);
            statement.setString(5, text);
        });
        return id;
    }

    @Override
    public List<ShapeData> getShapes()  throws RemoteException{
        final List<ShapeData> shapes = new ArrayList<>();
        DB.query("SELECT geometry,username,type,color,text,id FROM shapes", statement -> {},resultSet -> {
            ShapeData shapeData = new ShapeData();
            Blob blob = resultSet.getBlob(1);
            SerialBlob serialBlob = new SerialBlob(blob);
            shapeData.coords = byteArrayToDoubleArray(serialBlob.getBytes(1, (int) blob.length()));
            shapeData.username = resultSet.getString(2);
            shapeData.type = resultSet.getString(3);
            shapeData.color = resultSet.getString(4);
            if(shapeData.type.equals("text")){
                shapeData.text = resultSet.getString(5);
            }
            shapeData.id = resultSet.getInt(6);
            shapes.add(shapeData);
        });
        return shapes;
    }

    public static byte[] doubleArrayToByteArray(double[] doubleArray){
        byte[] result = new byte[doubleArray.length * 8];
        int pos = 0;
        for (int i = 0; i < doubleArray.length; i++) {
            ByteBuffer.wrap(result).putDouble(pos, doubleArray[i]);
            pos += 8;
        }

        return result;
    }

    public static double[] byteArrayToDoubleArray(byte[] byteArray){
        double[] result = new double[byteArray.length / 8];
        for (int i = 0; i < result.length; i++) {
            result[i] = ByteBuffer.wrap(byteArray).getDouble(i*8);
        }
        return result;
    }





    public static class Container<T>{
        public T value;
    }
}

