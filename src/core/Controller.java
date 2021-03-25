package core;

import bayesianNetwork.Bayes;
import bayesianNetwork.Edge;
import bayesianNetwork.Vertex;
import com.google.gson.Gson;
import graphFX.EdgeFX;
import graphFX.VertexFX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

public class Controller {

    @FXML
    public  AnchorPane grafo;
    public static Bayes bayesnet = new Bayes();
    public  static VertexFX selectedVertex;
    private int counter = 0;

    public AnchorPane workspace;



    @FXML
    void initialize(){
        workspace=grafo;
    }

    public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.isPrimaryButtonDown()) {

            // Get mouse position on clicked
            double x_mouse = mouseEvent.getX();
            double y_mouse = mouseEvent.getY();

            // Create normal vertex
            Vertex v = new Vertex(str(counter));
            v.setPosition(x_mouse,y_mouse);

            // Create component of vertex
            VertexFX vfx = new VertexFX(grafo,v);
            bayesnet.addVertex(v);
            grafo.getChildren().add(vfx);
            counter++;

            if (selectedVertex != null) {
                EdgeFX edge = new EdgeFX(selectedVertex.getLayoutX(),selectedVertex.getLayoutY(),vfx.getLayoutX(),vfx.getLayoutY());
                edge.x1Property().bind(selectedVertex.layoutXProperty());
                edge.y1Property().bind(selectedVertex.layoutYProperty());
                edge.x2Property().bind(vfx.layoutXProperty());
                edge.y2Property().bind(vfx.layoutYProperty());

                //DIALOGO
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("Arista");
                dialog.setHeaderText("Valor de la Arista");
                dialog.setContentText("Ingrese el valor de la arista:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {

                    if(result.get().matches("[-+]?\\d*\\.?\\d+")){
                        selectedVertex.vertex.addEdge(new Edge(new BigDecimal(result.get()), v));
                        edge.setContent(result.get());
                    }else{
                        selectedVertex.vertex.addEdge(new Edge(new BigDecimal("0.0"), v));
                        edge.setContent("0.0");
                    }
                }

                selectedVertex.edges.add(edge);
                vfx.edges.add(edge);
                grafo.getChildren().add(edge);

                selectedVertex=null;
            }
        }
    }

    public void guardar(ActionEvent actionEvent) throws IOException {
        String data = bayesnet.guardarRB();
        System.out.println(data);
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Red Bayesiana", "*.rbayes"));

        File selectedFile = fileChooser.showSaveDialog(Main.escena.getWindow());

        if(selectedFile==null){
            System.out.println("CANCELED");
        }else{
            File file = new File(selectedFile.getPath());
            OutputStream out = new FileOutputStream(file);
            Writer w = new OutputStreamWriter(out, "UTF-8");
            w.write(data);
            w.close();
            out.close();
        }
    }

    public void cargar(ActionEvent actionEvent) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(Main.escena.getWindow());
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Red Bayesiana", "*.rbayes"));

        if(selectedFile==null){

        }else{
            // Clean Workspace
            limpiar(actionEvent);

            // Get data

            File file = new File(selectedFile.getPath());
            Scanner scan = new Scanner(file);
            String content = "";

            while(scan.hasNext()) content += scan.nextLine();

            System.out.println(content);
            scan.close();

            // Load Bayes network
            List<Vertex> c = bayesnet.cargarRB(content);

            // Map for saving the nodes references from AnchorPane
            HashMap<String, VertexFX> vertices = new HashMap<>();

            for (Vertex vertex : c) {
                VertexFX v = new VertexFX(grafo,vertex);
                vertices.put(vertex.getTag(),v);
                bayesnet.addVertex(vertex);
                grafo.getChildren().add(v);
            }


            int m = 0;
            System.out.println(vertices.size());
            for (Map.Entry<String, VertexFX> stringVertexFXEntry : vertices.entrySet()) {

                String tag = stringVertexFXEntry.getKey();
                VertexFX from = stringVertexFXEntry.getValue();


                int n = getStringValue(tag);
                //System.out.println(tag+": "+n);
                m = Math.max(m,n);

                //System.out.println(from);
                for (Edge edge : from.vertex.getAdjacent()) {
                    VertexFX to = vertices.get(edge.getTo().getTag());
                    //System.out.println(vertices.get(edge.getTo().getTag()));

                    // Instantiate Edges
                    EdgeFX edgefx = new EdgeFX(from.getLayoutX(),from.getLayoutY(),to.getLayoutX(),to.getLayoutY());
                    edgefx.x1Property().bind(from.layoutXProperty());
                    edgefx.y1Property().bind(from.layoutYProperty());
                    edgefx.x2Property().bind(to.layoutXProperty());
                    edgefx.y2Property().bind(to.layoutYProperty());

                    // Add edgefx reference to both nodes
                    edgefx.setContent(edge.getValue().toString());
                    from.edges.add(edgefx);
                    to.edges.add(edgefx);
                    grafo.getChildren().add(edgefx);
                }
            }
            System.out.println("MAYOR: "+m);
            // Set counter to the bigger tag
            counter=m+1;
        }
    }

    int getStringValue(String s){
        System.out.print(s+": ");
        int ac = 0;
        for(int i=0;i<s.length();++i){
            char x = s.charAt(i);
            int y = x - 'A';
            ac+=y+26*i;
        }
        System.out.println(ac);
        return ac;
    }

    public void limpiar(ActionEvent actionEvent) {
        bayesnet.show();

        grafo.getChildren().removeIf(child -> child.getClass() == VertexFX.class || child.getClass() == EdgeFX.class);
        bayesnet.clear();
        counter=0;
        selectedVertex=null;
    }

    static String str(int i) {
        return i < 0 ? "" : str((i / 26) - 1) + (char)(65 + i % 26);
    }

    public void mostrar_info(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the Project");
        alert.setHeaderText("Bayesian Network Editor");
        alert.setContentText("Subject: Expert Systems\n" +
                "Author: Añez Vladimirovna Leonardo Henry");

        alert.showAndWait();
    }

    public void ejecutar(ActionEvent actionEvent) {
        // Calculate
        if(bayesnet.checkRB()){
            for (Vertex goal : bayesnet.getGoals()) {
                bayesnet.CF(goal);
            }

        // Refresh edges
        for (Node child : grafo.getChildren()) {
            if (child.getClass() == VertexFX.class) {
                ((VertexFX) child).label.setText(((VertexFX) child).vertex.getCF().toString());
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Metas");
        alert.setHeaderText("Metas Calculadas!");

        String msg = "";
        for (Vertex goal : bayesnet.getGoals()) {
            BigDecimal cf = new BigDecimal("100.0").multiply(goal.getCF());
            msg+="Meta "+goal.getTag()+" se cumple al "+cf+"%\n";
        }

        alert.setContentText(msg);

        alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Revisar la Red Bayesiana");
            alert.setContentText("Ha ocurrido un error al construirlo.");

            alert.showAndWait();
        }
    }
}
