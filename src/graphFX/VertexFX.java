package graphFX;

import bayesianNetwork.Bayes;
import bayesianNetwork.Edge;
import bayesianNetwork.Vertex;
import core.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.math.BigDecimal;
import java.util.Optional;

public class VertexFX extends StackPane {

    // Data
    public Vertex vertex;

    // Javafx Components
    Button button = new Button();
    Label label = new Label();
    ContextMenu contextMenu;
    public ObservableList<EdgeFX> edges = FXCollections.observableArrayList();

    public VertexFX(AnchorPane parent,Vertex v){

        vertex=v;

        setLayoutX(vertex.getXPosition());
        setLayoutY(vertex.getYPosition());
        translateXProperty().bind(widthProperty().divide(-2));
        translateYProperty().bind(heightProperty().divide(-2));

        button.setText(vertex.getTag());

        label.setTranslateY(-32);
        label.setText(vertex.getCF().toString());
        button.getStyleClass().add("node");
        
        // Components to StackPane
        getChildren().add(button);
        getChildren().add(label);

        // Seteamos los handlers
        setHandlers(parent);
    }

    private void setHandlers(AnchorPane parent){

        EventHandler<MouseEvent> mousePressedHandler = e -> {

            if (e.isSecondaryButtonDown()) {

                if (contextMenu == null) {
                    contextMenu = new ContextMenu();

                    MenuItem item1 = new MenuItem("Editar Nodo");
                    MenuItem item2 = new MenuItem("Relacionar Nodo");
                    MenuItem item3 = new MenuItem("Borrar Nodo");
                    MenuItem item4 = new MenuItem("Calcular CF");

                    // Set Value
                    item1.setOnAction(mouseEvent -> {
                        // Dialog for CF of Vertex
                        TextInputDialog dialog = new TextInputDialog("");
                        dialog.setTitle("Nodo");
                        dialog.setHeaderText("Valor del CF");
                        dialog.setContentText("Ingrese el valor de CF:");

                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent()) {
                            label.setText(result.get());
                            vertex.setCF(new BigDecimal(result.get()));
                        }
                    });

                    // Select Vertex
                    item2.setOnAction(mouseEvent -> {
                        Controller.selectedVertex = this;
                    });

                    // Delete Vertex
                    item3.setOnAction(mouseEvent -> {
                        for (EdgeFX edge : edges) {
                            parent.getChildren().remove(edge);
                        }
                        Controller.bayesnet.deleteVertex(vertex);
                        parent.getChildren().remove(this);

                    });

                    item4.setOnAction(mouseEvent-> {

                        boolean c = Controller.bayesnet.checkRB();

                        if (!c) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Advertencia");
                            alert.setHeaderText("Revisar la Red Bayesiana");
                            alert.setContentText("Ha ocurrido un error al construirlo.");

                            alert.showAndWait();

                        }else{
                            System.out.println(Controller.bayesnet.CF(vertex));
                            for (Node child : parent.getChildren()) {
                                if (child.getClass() == VertexFX.class) {
                                    ((VertexFX) child).label.setText(((VertexFX) child).vertex.getCF().toString());
                                }
                            }
                        }
                    });

                    // Type of Menu
                    if(Bayes.isGoal(vertex)){
                        contextMenu.getItems().addAll(item4, item2, item3);
                    }else
                    if (Bayes.isFact(vertex)) {
                        contextMenu.getItems().addAll(item1, item2, item3);
                    } else {
                        contextMenu.getItems().addAll(item2, item3);
                    }

                    contextMenu.show(button, e.getScreenX(), e.getScreenY());
                } else {
                    contextMenu.hide();
                    contextMenu = null;
                }
            }else  if (e.isPrimaryButtonDown()) {



                if (Controller.selectedVertex != null && !Controller.selectedVertex.vertex.equals(vertex)) {

                    // Already exists a connection
                    if(!Controller.bayesnet.existEdgeBetween(vertex,Controller.selectedVertex.vertex)) {

                        EdgeFX edge = new EdgeFX(Controller.selectedVertex.getLayoutX(),
                                Controller.selectedVertex.getLayoutY(),
                                getLayoutX(),
                                getLayoutY());
                        edge.x1Property().bind(Controller.selectedVertex.layoutXProperty());
                        edge.y1Property().bind(Controller.selectedVertex.layoutYProperty());
                        edge.x2Property().bind(layoutXProperty());
                        edge.y2Property().bind(layoutYProperty());
                        Controller.selectedVertex.edges.add(edge);


                        //DIALOGO
                        TextInputDialog dialog = new TextInputDialog("");
                        dialog.setTitle("Arista");
                        dialog.setHeaderText("Valor de la Arista");
                        dialog.setContentText("Ingrese el valor de la arista:");

                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent()) {

                            if (result.get().matches("[-+]?\\d*\\.?\\d+")) {
                                Controller.selectedVertex.vertex.addEdge(new Edge(new BigDecimal(result.get()), vertex));
                                edge.setContent(result.get());
                            } else {
                                Controller.selectedVertex.vertex.addEdge(new Edge(new BigDecimal("0.0"), vertex));
                                edge.setContent("0.0");
                            }
                        }

                        edges.add(edge);
                        parent.getChildren().add(edge);



                    }else{
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Advertencia");
                        alert.setHeaderText("Revisar la Red Bayesiana");
                        alert.setContentText("Ha ocurrido un error al construirlo.");

                        alert.showAndWait();
                    }

                    Controller.selectedVertex = null;

                }


                if(contextMenu!=null){
                    contextMenu.hide();
                    contextMenu = null;
                }

            }
        };

        EventHandler<MouseEvent> dragVertexHandler = e -> {

            if (e.isPrimaryButtonDown()) {
                setCursor(Cursor.CLOSED_HAND);

                double posx = e.getSceneX();
                double posY = e.getSceneY();

                posx=clamp(posx,32,640-32);
                posY=clamp(posY,64,480-32);

                relocate(posx, posY);
                vertex.setPosition(getLayoutX(),getLayoutY());
            }
        };
        EventHandler<MouseEvent> leaveMouseHandler = e -> {
            setCursor(Cursor.DEFAULT);
        };


        button.setOnMousePressed(mousePressedHandler);
        button.setOnMouseDragged(dragVertexHandler);
        button.setOnMouseReleased(leaveMouseHandler);
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

}
