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

        //point:
        Button btnShapePoint = new Button("draw point");
        btnShapePoint.setOnAction(actionEvent -> {
            drawBoard.setShape(DrawBoard.POINT);
        });
        bar.getChildren().add(btnShapePoint);


        //line:
        Button btnShapeLine = new Button("draw line");
        btnShapeLine.setOnAction(actionEvent -> {
            drawBoard.setShape(DrawBoard.LINE);
        });
        bar.getChildren().add(btnShapeLine);


        class MyButton extends Button{
            public MyButton(String path, int action){
                //super(name);
                BackgroundImage backgroundImage = new BackgroundImage( new Image( getClass().getResource(path).toExternalForm()),
                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
                Background background = new Background(backgroundImage);
                this.setBackground(background);
                this.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));
                this.setMinHeight(40);
                this.setMinWidth(40);
                this.setOnAction(actionEvent -> {
                    drawBoard.setShape(action);
                });
                bar.getChildren().add(this);
            }
        }

        //rectangle:
        new MyButton("/img_rectangle.jpeg", DrawBoard.RECTANGLE);



        /*
        Button btnShapeRectangle = new Button("rectangle");
        btnShapeRectangle.setOnAction(actionEvent -> {
            drawBoard.setShape(DrawBoard.RECTANGLE);
        });
        bar.getChildren().add(btnShapeRectangle);
        */






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
