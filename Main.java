import javafx.application.Application;
import javafx.stage.Stage;



public class Main extends Application implements LoginScene.LoginSceneListener {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Whiteboard App");
        //primaryStage.setScene(new LoginScene(this));
        primaryStage.setScene(new DrawScene("elad", "qwe123"));
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
        primaryStage.setScene(new DrawScene(username, password));
    }
}
