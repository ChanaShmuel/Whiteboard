import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;



public class DrawBoard extends Rectangle {

    // limits of the board
    private static final int MARGIN = 5;
    private static final int LIMIT_Y = 50;

    private ObservableList<Node> list;
    private Color color;
    private int shape;
    public static final int POINT = 1;
    public static final int LINE = 2;
    public static final int TEXT = 3;
    public static final int RECTANGLE = 4;
    public static final int Ellipse = 5;
    public static final int PEN = 6;

    private final Listener listener;
    private Line tempLine;
    private Rectangle tempRectangle;
    private Ellipse tempEllipse;
    public DrawBoard(ObservableList<Node> list, Listener listener) {
        this.listener = listener;
        shape = POINT;
        this.list = list;
        this.setStroke(Color.DARKGRAY);
        this.setFill(Color.WHITE);
        registerToEvent();
    }


    public void registerToEvent(){

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
                case Ellipse:
                    drawEllipse(x, y);
                    break;
                case PEN:
                    freeDraw(x, y);
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
            }else if (shape == Ellipse && tempEllipse != null)
                drawEllipse(x, y);


        });
        this.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            //SEND TO SERVER
            tempLine = null;
            tempRectangle = null;
            tempEllipse = null;
        });
    }



    public void setDimensions(double width, double height){
        this.setX(MARGIN);
        this.setY(LIMIT_Y); //MUST BE CONSTANT
        this.setWidth(width - MARGIN *2);
        this.setHeight(height - this.getY() - MARGIN);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    private void freeDraw(double x, double y){

    }

    private void drawEllipse(double x, double y){
        if (tempEllipse == null){
            tempEllipse = new Ellipse(x, y, 0, 0 );
            tempEllipse.setMouseTransparent(true);
            tempEllipse.setStroke(color);
            tempEllipse.setFill(Color.TRANSPARENT);
            list.add(tempEllipse);
        }else {

            if(x <= this.getWidth()+ MARGIN &&
                    (tempEllipse.getCenterX() - (x - tempEllipse.getCenterX())) >= MARGIN && //x - tempEllipse.getCenterX()  is tempEllipse.getRadiusX()
                    (tempEllipse.getCenterY() - (y - tempEllipse.getCenterY())) >= LIMIT_Y &&//y - tempEllipse.getCenterY()  is tempEllipse.getRadiusY()
                    y <= this.getHeight()+LIMIT_Y &&
                    y>=LIMIT_Y &&
                    x>= MARGIN) {
                tempEllipse.setRadiusX(x - tempEllipse.getCenterX());
                tempEllipse.setRadiusY(y - tempEllipse.getCenterY());
            }
        }
    }

    private void drawRectangle(double x, double y){
        if(tempRectangle == null){
            tempRectangle = new Rectangle(x, y, 0, 0);
            tempRectangle.setMouseTransparent(true);
            tempRectangle.setStroke(color);
            tempRectangle.setFill(Color.TRANSPARENT);
            list.add(tempRectangle);
        }else{
            //make sure (x,y) is inside DrawBoard
            if(x <= this.getWidth()+ MARGIN && y <= this.getHeight()+LIMIT_Y && y>=LIMIT_Y && x>= MARGIN) {
                tempRectangle.setWidth(x - tempRectangle.getX());
                tempRectangle.setHeight(y - tempRectangle.getY());
            }

        }
    }


    private void drawPoint(double x, double y){
        Circle c = new Circle();
        c.setCenterX(x);
        c.setCenterY(y);
        c.setRadius(5);
        c.setFill(color);
        list.add(c);

        //SEND TO SERVER
    }

    private void drawLine(double x, double y){
        if(tempLine == null){
            tempLine = new Line(x, y, x, y);
            tempLine.setMouseTransparent(true);
            tempLine.setStroke(color);
            list.add(tempLine);
        }else{
            //make sure (x,y) is inside DrawBoard
            if(x <= this.getWidth()+ MARGIN && y <= this.getHeight()+LIMIT_Y && y>=LIMIT_Y && x>= MARGIN) {
                tempLine.setEndX(x);
                tempLine.setEndY(y);
            }
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

    public interface Listener {
        String getText();
        void doneText();
    }

}
