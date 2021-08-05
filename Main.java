import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

//Client side:
public class Main extends Application implements LoginScene.LoginSceneListener {

    private Stage primaryStage;
    private ServerInterface server;

    @Override
    public void start(Stage primaryStage) throws Exception{

        //some RMI stuff:
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
        //primaryStage.setScene(new DrawScene("hana", "qwe123", server));
        primaryStage.show();
    }




    /*
    launch() creates an instance of this class (Application) and also an instance of Stage,
    and then it invokes the method "start" on the application instance, sending it the created Stage object.
     */
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                primaryStage.setScene(new DrawScene(username, password, server));
            }
        });

    }


    @Override
    public void stop() throws Exception {
        Scene scene = primaryStage.getScene();
        if(scene != null && scene instanceof DrawScene){
            DrawScene drawScene = (DrawScene) scene;
            drawScene.onStop();
        }
    }
}
