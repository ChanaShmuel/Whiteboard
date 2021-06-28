import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Random;

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
        colorPicker.setMinHeight(42);
        bar.getChildren().add(colorPicker);


        //local inner class
        class BarButton extends Button{
            public BarButton(String path){
                Image img = new Image(path);
                ImageView view = new ImageView(img);
                view.setPreserveRatio(true);
                this.setGraphic(view);
                this.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));
                this.setMinHeight(42);
                this.setMinWidth(42);
                bar.getChildren().add(this);
            }
        }

        class ShapeButton extends BarButton {
            public ShapeButton(String path, int action){
                super(path);
                this.setOnAction(actionEvent -> {
                    drawBoard.setShape(action);
                });
            }
        }

        new ShapeButton("icon/rectangle.png", DrawBoard.RECTANGLE);
        new ShapeButton("icon/ellipse.png", DrawBoard.Ellipse);
        new ShapeButton("icon/line.png", DrawBoard.LINE);
        new ShapeButton("icon/point.png", DrawBoard.POINT);
        BarButton btnUndo = new BarButton("icon/point.png");
        btnUndo.setOnAction(actionEvent -> {
            if(root.getChildren().size() > 2) {

                /*
                root.getChildren().clear();
                root.getChildren().add(bar);
                root.getChildren().add(drawBoard);
                */

                ObservableList<Node> l = root.getChildren();
                l.remove(l.size()-1);
                l.add(0, new Button());
                l.remove(0);


                /*
                Button stam = new Button();
                stam.setVisible(false);
                ObservableList<Node> l = root.getChildren();
                l.add(2, stam);
                l.remove(l.size() - 1);
                */

            }
        });



        //text field:
        textField = new TextField();
        bar.getChildren().add(textField);
        textField.setOnMouseClicked(mouseEvent -> {
            drawBoard.setShape(DrawBoard.TEXT);
        });
        textField.setMinHeight(42);



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
