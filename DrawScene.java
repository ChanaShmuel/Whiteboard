import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class DrawScene extends Scene implements DrawBoard.Listener {

    private TextField textField;
    private DrawBoard drawBoard;
    private final String username, password;

    public DrawScene(String username, String password){
        super(new Group(), 600, 400);
        this.username = username;
        this.password = password;
        Group root = (Group) this.getRoot();
        Color initialColor = Color.BLUE;
        HBox bar = new HBox();
        root.getChildren().add(bar);

        //color picker:
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(initialColor);
        colorPicker.setOnAction(actionEvent -> {
            drawBoard.setColor(colorPicker.getValue());
        });
        bar.getChildren().add(colorPicker);

        class MyButton extends Button{
            public MyButton(String path, int action){
                super(path);
                this.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));
                this.setMinHeight(40);
                this.setMinWidth(40);
                this.setOnAction(actionEvent -> {
                    drawBoard.setShape(action);
                });
                bar.getChildren().add(this);
            }
        }

        new MyButton("rectangle", DrawBoard.RECTANGLE);
        new MyButton("ellips", DrawBoard.Ellipse);
        new MyButton("line", DrawBoard.LINE);
        new MyButton("point", DrawBoard.POINT);


        //text field:
        textField = new TextField();
        bar.getChildren().add(textField);
        textField.setOnMouseClicked(mouseEvent -> {
            drawBoard.setShape(DrawBoard.TEXT);
        });



        //draw board:
        drawBoard = new DrawBoard(root.getChildren(), this);
        drawBoard.setColor(initialColor);
        drawBoard.setDimensions(this.getWidth(), this.getHeight());
        root.getChildren().add(drawBoard);




        this.setFill(Color.LIGHTGRAY);
    }


    @Override
    public String getText() {
        return textField.getText();
    }

    @Override
    public void doneText() {
        textField.setText("");
    }
}
