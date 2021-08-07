import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;

public class LoginScene extends Scene {

    private final LoginSceneListener listener;
    private final ServerInterface server;
    private TextField txtUserName, txtPassword;
    private Label lblMessage;
    private Button btnLogin, btnSignup;

    public LoginScene(LoginSceneListener listener, ServerInterface server){
        super(new Group(), 400, 200);
        this.server = server;
        this.listener = listener;

        //build GUI
        Group root = (Group) this.getRoot();

        // vbox to put the labels and text field and buttons
        VBox box = new VBox();
        box.setSpacing(10);
        box.setLayoutX(20);
        box.setPrefWidth(this.getWidth() - 20*2);
        //box.setAlignment(Pos.CENTER);

        // username
        Label lblUserName = new Label("username:");
        box.getChildren().add(lblUserName);
        txtUserName = new TextField();
        txtUserName.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().getCode() == 10)
                txtPassword.requestFocus();  // if click enter move to password box
        });
        box.getChildren().add(txtUserName);

        // password
        Label lblPassword = new Label("password:");
        box.getChildren().add(lblPassword);
        txtPassword = new PasswordField();
        txtPassword.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().getCode() == 10)   // if click enter it'll click on the login btn
                login();
        });
        box.getChildren().add(txtPassword);

        // hbox to put the two buttons login and signup one near the other
        HBox boxForButtons = new HBox();

        boxForButtons.setSpacing(10);   // put spaces between the two buttons

        // login
        btnLogin = new Button("login");
        lblMessage = new Label();
        box.getChildren().add(lblMessage);

        btnLogin.setOnAction(actionEvent -> {
            login();
        });
        boxForButtons.getChildren().add(btnLogin);

        // signup
        btnSignup = new Button("signup");
        btnSignup.setOnAction(actionEvent -> {
            login(false);
        });

        boxForButtons.getChildren().add(btnSignup);
        box.getChildren().add(boxForButtons);

        root.getChildren().add(box);

    }


    private void login(){
        login(true);
    }

    private void login(boolean isLogin){
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
                    if((isLogin && server.login(username, password)) || (!isLogin && server.signup(username,password))) {
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
    }

    public interface LoginSceneListener{
        void onLogin(String username, String password);
    }
}
