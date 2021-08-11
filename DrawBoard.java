import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class DrawBoard extends Rectangle {

    // limits of the board
    private static final int MARGIN = 5;
    private static final int LIMIT_Y = 50;
    public static final int POINT_RADIUS = 5;
    private final ServerInterface server;

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
    private final String username, password;
    private volatile boolean go = true;
    private final ColorPicker colorPicker;
    private boolean isDrawing;

    public DrawBoard(ObservableList<Node> list, Listener listener, ServerInterface server, String username, String password, ColorPicker colorPicker) {
        this.listener = listener;
        this.username = username;
        this.password = password;
        shape = POINT;
        this.list = list;
        this.server = server;
        this.setStroke(Color.DARKGRAY);
        this.setFill(Color.WHITE);
        list.add(this);
        registerToEvent();
        getShapes();
        this.colorPicker = colorPicker;
        isDrawing = false;
    }


    private void getShapes(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (go) {
                    try {
                        List<ShapeData> shapeDataList = server.getShapes();
                        final List<Shape> shapes = new ArrayList<>();
                        for (int i = 0; i < shapeDataList.size(); i++) {
                            ShapeData shapeData = shapeDataList.get(i);
                            Shape shape = null;
                            Color color;
                            color = Color.web(shapeData.color);
                            switch (shapeData.type) {
                                case "point":
                                    Circle circle = new Circle();
                                    circle.setCenterX(shapeData.coords[0]);
                                    circle.setCenterY(shapeData.coords[1]);
                                    circle.setRadius(POINT_RADIUS);
                                    circle.setFill(color);
                                    shape = circle;
                                    shapes.add(circle);
                                    break;
                                case "line":
                                    Line line = new Line();
                                    line.setStartX(shapeData.coords[0]);
                                    line.setStartY(shapeData.coords[1]);
                                    line.setEndX(shapeData.coords[2]);
                                    line.setEndY(shapeData.coords[3]);
                                    line.setStroke(color);
                                    line.setMouseTransparent(true);
                                    shape = line;
                                    shapes.add(line);
                                    break;
                                case "rectangle":
                                    Rectangle rectangle = new Rectangle();
                                    rectangle.setX(shapeData.coords[0]);
                                    rectangle.setY(shapeData.coords[1]);
                                    rectangle.setWidth(shapeData.coords[2]);
                                    rectangle.setHeight(shapeData.coords[3]);
                                    rectangle.setStroke(color);
                                    rectangle.setMouseTransparent(true);
                                    rectangle.setFill(Color.TRANSPARENT);
                                    shape = rectangle;
                                    shapes.add(rectangle);
                                    break;
                                case "text":
                                    Text text = new Text();
                                    text.setX(shapeData.coords[0]);
                                    text.setY(shapeData.coords[1]);
                                    text.setText(shapeData.text);
                                    text.setFill(color);
                                    shape = text;
                                    shapes.add(text);
                                    break;
                                case "ellipse":
                                    Ellipse ellipse = new Ellipse();
                                    ellipse.setCenterX(shapeData.coords[0]);
                                    ellipse.setCenterY(shapeData.coords[1]);
                                    ellipse.setRadiusX(shapeData.coords[2]);
                                    ellipse.setRadiusY(shapeData.coords[3]);
                                    ellipse.setStroke(color);
                                    ellipse.setMouseTransparent(true);
                                    ellipse.setFill(Color.TRANSPARENT);
                                    shape = ellipse;
                                    shapes.add(ellipse);
                                    break;
                                default:
                                    throw new RuntimeException("what is this shape?");
                            }
                            shape.setId(String.valueOf(shapeData.id));
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (list.size() >= 2 && !isDrawing && !colorPicker.isFocused()) {
                                    //Node bar = list.get(0);
                                    //Node drawBoard = list.get(1);
                                    while (list.size() > 2){
                                        list.remove(2);
                                    }
                                    //list.clear();
                                    //list.add(bar);
                                    //list.add(drawBoard); //we could have done: list.add(this) like in the constructor
                                    list.addAll(shapes);
                                }

                            }
                        });
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void registerToEvent(){

        this.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            double x = mouseEvent.getSceneX(), y = mouseEvent.getSceneY();

            switch (shape){
                case POINT:
                    drawPoint(x, y);
                    break;
                case LINE:
                    isDrawing = true;
                    drawLine(x, y);
                    break;
                case TEXT:
                    drawText(x, y);
                    break;
                case RECTANGLE:
                    isDrawing = true;
                    drawRectangle(x, y);
                    break;
                case Ellipse:
                    isDrawing = true;
                    drawEllipse(x, y);
                    break;
                case PEN:
                    freeDraw(x, y);
                default:
                    throw new RuntimeException("unknown shape...");

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
            if(tempLine != null){
                sendShape(tempLine);
                tempLine = null;
            }
            if(tempRectangle != null){
                sendShape(tempRectangle);
                tempRectangle = null;
            }
            if(tempEllipse != null) {
                sendShape(tempEllipse);
                tempEllipse = null;
            }
            isDrawing = false;
        });
    }


    private void sendShape(Shape shape){
        new Thread(new Runnable() {
            @Override
            public void run() {
                double[] coords = null;
                String type = null;
                String textString = null;
                if(shape instanceof Circle){
                    Circle circle = (Circle)shape;
                    coords = new double[]{circle.getCenterX(), circle.getCenterY()};
                    type = "point";
                }else if(shape instanceof Rectangle){
                    Rectangle rectangle = (Rectangle) shape;
                    coords = new double[]{rectangle.getX(), rectangle.getY(),
                            rectangle.getWidth(), rectangle.getHeight()};
                    type = "rectangle";
                }else if(shape instanceof Ellipse){
                    Ellipse ellipse = (Ellipse)shape;
                    coords = new double[]{ellipse.getCenterX(), ellipse.getCenterY(),
                            ellipse.getRadiusX(), ellipse.getRadiusY()};
                    type = "ellipse";
                }else if(shape instanceof Line){
                    Line line = (Line) shape;
                    coords = new double[]{line.getStartX(), line.getStartY(),
                            line.getEndX(), line.getEndY()};
                    type = "line";
                }else if(shape instanceof Text){
                    Text text = (Text) shape;
                    textString = text.getText();
                    coords = new double[]{text.getX(), text.getY()};
                    type = "text";
                }
                else{
                    throw new RuntimeException("unknown shape " + shape);
                }
                int id = 0;
                try {
                    id = server.addShape(coords,color.toString(),type, username, password, textString);
                    shape.setId(String.valueOf(id));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
            tempEllipse.setMouseTransparent(true);//transparent means it doesn't handle mouse events so the user cannot click on it.
            //we want to allow geometries to overlap.

            tempEllipse.setStroke(color);
            tempEllipse.setFill(Color.TRANSPARENT);
            list.add(tempEllipse);
        }else {
            boolean withinTheWhiteRectangle = x <= this.getWidth()+ MARGIN &&
                    (tempEllipse.getCenterX() - (x - tempEllipse.getCenterX())) >= MARGIN && //x - tempEllipse.getCenterX()  is tempEllipse.getRadiusX()
                    (tempEllipse.getCenterY() - (y - tempEllipse.getCenterY())) >= LIMIT_Y &&//y - tempEllipse.getCenterY()  is tempEllipse.getRadiusY()
                    y <= this.getHeight()+LIMIT_Y &&
                    y>=LIMIT_Y &&
                    x>= MARGIN;
            if(withinTheWhiteRectangle) {
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
        c.setRadius(POINT_RADIUS);
        c.setFill(color);
        list.add(c);
        sendShape(c);
    }

    private void drawLine(double x, double y){
        if(tempLine == null){
            tempLine = new Line(x, y, x, y);
            //MouseTransparent means ignoring click (not handling the event and passing it to the board behind them)
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
        final Text t = new Text(text);
        t.setX(x);
        t.setY(y);
        t.setFill(color);
        list.add(t);
        listener.doneText();

        sendShape(t);


    }

    public void onStop() {
        go = false;
    }


    public interface Listener {
        String getText();
        void doneText();
    }

}
