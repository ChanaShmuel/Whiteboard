import javafx.application.Application;
import javafx.stage.Stage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Main extends Application implements LoginScene.LoginSceneListener {

    private Stage primaryStage;
    private ServerInterface server;

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            Registry registry = LocateRegistry.getRegistry(null);//ip address or domain of server
            server = (ServerInterface) registry.lookup("ServerInterface");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Whiteboard App");
        primaryStage.setScene(new LoginScene(this, server));
        //primaryStage.setScene(new DrawScene("elad", "qwe123", server));
        primaryStage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }


    /**
     * the login or signup was completed successfully.
     * @param username
     * @param password
     */
    @Override
    public void onLogin(String username, String password) {
        System.out.println(username + " " + password);
        primaryStage.setScene(new DrawScene(username, password, server));
    }
}
