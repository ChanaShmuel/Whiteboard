import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;

public class LoginScene extends Scene {

    private final LoginSceneListener listener;
    private final ServerInterface server;
    private TextField txtUserName, txtPassword;
    private Label lblMessage;
    private Button btnLogin;

    public LoginScene(LoginSceneListener listener, ServerInterface server){
        super(new Group(), 400, 200);
        this.server = server;
        this.listener = listener;

        //build GUI
        Group root = (Group) this.getRoot();
        VBox box = new VBox();
        box.setSpacing(10);
        box.setLayoutX(20);
        box.setPrefWidth(this.getWidth() - 20*2);
        //box.setAlignment(Pos.CENTER);
        Label lblUserName = new Label("username:");
        box.getChildren().add(lblUserName);
        txtUserName = new TextField();
        box.getChildren().add(txtUserName);
        Label lblPassword = new Label("password:");
        box.getChildren().add(lblPassword);
        txtPassword = new PasswordField();
        box.getChildren().add(txtPassword);
        btnLogin = new Button("login");
        lblMessage = new Label();
        box.getChildren().add(lblMessage);

        btnLogin.setOnAction(actionEvent -> {
            String username = txtUserName.getText();
            String password = txtPassword.getText();
            if(username.length() < 3 || password.length() < 3){
                lblMessage.setText("must enter username and password");
                return;
            }
            lblMessage.setText("please wait...");
            btnLogin.setDisable(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(server.login(username, password)) {
                            listener.onLogin(username, password);
                        }else{
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    btnLogin.setDisable(false);
                                    lblMessage.setText("invalid username or password");
                                }
                            });

                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        });
        box.getChildren().add(btnLogin);
        root.getChildren().add(box);

    }

    public interface LoginSceneListener{
        void onLogin(String username, String password);
    }
}
