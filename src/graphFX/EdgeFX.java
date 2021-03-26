package graphFX;

import bayesianNetwork.Edge;
import bayesianNetwork.Vertex;
import core.Controller;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

public class EdgeFX extends Group {
    private final Pane content  = new Pane();
    private Polyline mainLine = new Polyline();
    private Polyline headA = new Polyline();
    private Polyline headB = new Polyline();
    private Button button = new Button("Borrar");

    public double getX1() {
        return x1.get();
    }

    public SimpleDoubleProperty x1Property() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1.set(x1);
    }

    public double getY1() {
        return y1.get();
    }

    public SimpleDoubleProperty y1Property() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1.set(y1);
    }

    public double getX2() {
        return x2.get();
    }

    public SimpleDoubleProperty x2Property() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2.set(x2);
    }

    public double getY2() {
        return y2.get();
    }

    public SimpleDoubleProperty y2Property() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2.set(y2);
    }

    private SimpleDoubleProperty x1 = new SimpleDoubleProperty();
    private SimpleDoubleProperty y1 = new SimpleDoubleProperty();
    private SimpleDoubleProperty x2 = new SimpleDoubleProperty();
    private SimpleDoubleProperty y2 = new SimpleDoubleProperty();
    private final double ARROW_SCALER = 24;
    private final double ARROWHEAD_ANGLE = Math.toRadians(20);
    private final double ARROWHEAD_LENGTH = 10;



    private double[] scale(double x1, double y1, double x2, double y2) {
        double theta = Math.atan2(y2 - y1, x2 - x1);
        return new double[]{

                x1 + Math.cos(theta) * ARROW_SCALER,
                y1 + Math.sin(theta) * ARROW_SCALER,

        };
    }

    private void update() {


        double[] start = scale(x1.get(), y1.get(), x2.get(), y2.get());
        double[] end = scale(x2.get(), y2.get(), x1.get(), y1.get());

        double x1 = start[0];
        double y1 = start[1];
        double x2 = end[0];
        double y2 = end[1];

        mainLine.getPoints().setAll(x1, y1, x2, y2);

        double theta = Math.atan2(y2 - y1, x2 - x1);
        double x = 0;
        double y = 0;


        x = x2 - Math.cos(theta + ARROWHEAD_ANGLE) * ARROWHEAD_LENGTH;
        y = y2 - Math.sin(theta + ARROWHEAD_ANGLE) * ARROWHEAD_LENGTH;
        headA.getPoints().setAll(x, y, x2, y2);
        x = x2 - Math.cos(theta - ARROWHEAD_ANGLE) * ARROWHEAD_LENGTH;
        y = y2 - Math.sin(theta - ARROWHEAD_ANGLE) * ARROWHEAD_LENGTH;
        headA.getPoints().addAll(x, y);
    }

    public void setContent(String v){
        content.getChildren().setAll(new Label(" "+v+" "));
    }

    public EdgeFX(double x1, double y1, double x2, double y2, AnchorPane parent) {
        this.x1.set(x1);
        this.y1.set(y1);
        this.x2.set(x2);
        this.y2.set(y2);

        mainLine.setStrokeWidth(2);
        headA.setStrokeWidth(2);
        headB.setStrokeWidth(2);

        getChildren().addAll(mainLine, headA, headB);


        for (SimpleDoubleProperty s : new SimpleDoubleProperty[]{this.x1, this.y1, this.x2, this.y2}) {
            s.addListener((l, o, n) -> update());
        }



        content.setStyle("-fx-alignment: center ;-fx-background-color: #FFF;-fx-font-size: 12;-fx-border-color: black;-fx-font-weight: bold;");
        getChildren().addAll(content);

        //node coordinates = the arrow's mid-point minus 1/2 the width/height, so the content is bang in the centre
        content.layoutXProperty().bind(x2Property().add(x1Property()).divide(2).subtract(content.widthProperty().divide(2)));
        content.layoutYProperty().bind(y2Property().add(y1Property()).divide(2).subtract(content.heightProperty().divide(2)));

        getChildren().add(button);
        button.setStyle("-fx-font-size:8");
        button.layoutXProperty().bind(x2Property().add(x1Property()).divide(2).subtract(content.widthProperty().divide(2)));
        button.layoutYProperty().bind(y2Property().add(y1Property()).divide(2).subtract(content.heightProperty().divide(2)).subtract(-22));

        button.setOnMousePressed(mouseEvent -> {

            // Looking for vertexfx that contains this edge
            List<VertexFX> found = new ArrayList<>();

            for (Node child : parent.getChildren()) {
                if(child.getClass()==VertexFX.class){
                    VertexFX v = (VertexFX) child;
                    if(v.edges.contains(this)){
                        found.add(v);
                    }
                }
            }

            System.out.println(found.size());

            // Delete edgesFX
            found.get(0).edges.remove(this);
            found.get(1).edges.remove(this);
            parent.getChildren().remove(this);

            // Vertex from Bayes class
            Vertex u = found.get(0).vertex;
            Vertex v = found.get(1).vertex;

            // Delete edges between nodes (u,v)
            Controller.bayesnet.deleteEdge(u,v);
            Controller.bayesnet.deleteEdge(v,u);


            //Edge a = Controller.bayesnet.getEdgeBetween(u,v);
            //Edge b = Controller.bayesnet.getEdgeBetween(v,u);
            //System.out.println(a);
            //System.out.println(b);


        });



        update();
    }
}
