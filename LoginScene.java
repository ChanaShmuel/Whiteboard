import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginScene extends Scene {

    private final LoginSceneListener listener;
    private TextField txtUserName, txtPassword;

    public LoginScene(LoginSceneListener listener){
        super(new Group(), 600, 400);
        this.listener = listener;
        Group root = (Group) this.getRoot();
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        txtUserName = new TextField();
        box.getChildren().add(txtUserName);
        txtPassword = new PasswordField();
        box.getChildren().add(txtPassword);
        Button btnLogin = new Button("login");
        btnLogin.setOnAction(actionEvent -> {
            String username = txtUserName.getText();
            String password = txtPassword.getText();
            if(username.length() < 3 || password.length() < 3){
                return;
            }
            listener.onLogin(username, password);
        });
        box.getChildren().add(btnLogin);
        root.getChildren().add(box);

    }

    public static interface LoginSceneListener{
        void onLogin(String username, String password);
    }
}
