import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class DrawBoard extends Rectangle {

    private ObservableList<Node> list;
    private Color color;
    private int shape;
    public static final int POINT = 1;
    public static final int LINE = 2;
    public static final int TEXT = 3;
    public static final int RECTANGLE = 4;
    private final Listener listener;
    private Line tempLine;
    private Rectangle tempRectangle;

    public DrawBoard(ObservableList<Node> list, Listener listener) {
        this.listener = listener;
        shape = POINT;
        this.list = list;
        this.setStroke(Color.DARKGRAY);
        this.setFill(Color.WHITE);
        registerToEvent();
    }

    private void registerToEvent(){



        this.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            double x = mouseEvent.getSceneX(), y = mouseEvent.getSceneY();
            switch (shape){
                case POINT:
                    drawPoint(x, y);
                    break;
                case LINE:
                    drawLine(x, y);
                    break;
                case TEXT:
                    drawText(x, y);
                    break;
                case RECTANGLE:
                    drawRectangle(x, y);
                    break;
                default:
                    System.out.println("unknown shape...");
                    break;

            }

        });
        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            double x = mouseEvent.getSceneX(), y = mouseEvent.getSceneY();
            if(shape == LINE && tempLine != null){
                drawLine(x, y);
            }else if(shape == RECTANGLE && tempRectangle != null){
                drawRectangle(x, y);
            }


        });
        this.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            tempLine = null;
            tempRectangle = null;
        });
    }



    public void setDimensions(double width, double height){
        this.setX(5);
        this.setY(50);//MUST BE CONSTANT
        this.setWidth(width - 5*2);
        this.setHeight(height - this.getY() - 5);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    private void drawRectangle(double x, double y){
        if(tempRectangle == null){
            tempRectangle = new Rectangle(x, y, 0, 0);
            tempRectangle.setStroke(color);
            tempRectangle.setFill(Color.TRANSPARENT);
            tempRectangle.setMouseTransparent(true);
            list.add(tempRectangle);
        }else{
            //make sure (x,y) is inside DrawBoard
            tempRectangle.setWidth(x - tempRectangle.getX());
            tempRectangle.setHeight(y - tempRectangle.getY());
        }
    }


    private void drawPoint(double x, double y){
        Circle c = new Circle();
        c.setCenterX(x);
        c.setCenterY(y);
        c.setRadius(5);
        c.setFill(color);
        list.add(c);
    }

    private void drawLine(double x, double y){
        if(tempLine == null){
            tempLine = new Line(x, y, x, y);
            tempLine.setStroke(color);
            list.add(tempLine);
        }else{
            //make sure (x,y) is inside DrawBoard
            tempLine.setEndX(x);
            tempLine.setEndY(y);
        }
    }

    private void drawText(double x, double y){
        String text = listener.getText();
        if(text == null || text.isEmpty())
            return;
        Text t = new Text(text);
        t.setX(x);
        t.setY(y);
        t.setFill(color);
        list.add(t);
        listener.doneText();

    }

    public static interface Listener{
        String getText();
        void doneText();
    }
}
