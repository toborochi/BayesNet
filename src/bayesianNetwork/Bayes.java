package bayesianNetwork;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.control.TextInputDialog;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Bayes {
    private static List<Vertex> vertices = new ArrayList<>();

    public void addVertex(Vertex v){
        vertices.add(v);
    }

    public List<Vertex> getGoals(){
        return vertices.stream().filter(vertex -> isGoal(vertex)).collect(Collectors.toList());
    }

    public void deleteVertex(Vertex v){
        for (Vertex vertex : getPrevious(v)) {
            vertex.getAdjacent().removeIf(node->node.getTo().equals(v));
        }
        vertices.remove(v);
    }

    public static List<Vertex> getPrevious(Vertex v){
        List<Vertex> adyacentes = new ArrayList<>();

        for (Vertex vertex : vertices) {
            for (Edge edge : vertex.getAdjacent()) {
                if(edge.getTo().equals(v)){
                    adyacentes.add(vertex);
                }
            }
        }

        return adyacentes;
    }

    public static boolean isFact(Vertex v){
        return (getPrevious(v).size()==0 && v.getAdjacent().size()>0);
    }

    public static boolean isGoal(Vertex v){
        return (getPrevious(v).size()>0 && v.getAdjacent().size()==0);
    }
    
    
    public void show(){
        for (Vertex vertex : vertices) {
            System.out.print(vertex.getTag()+"("+vertex.getCF()+")");
            for (Edge edge : vertex.getAdjacent()) {
                System.out.print(" with ");
                System.out.print("["+edge.getValue()+"] to ");
                System.out.println(edge.getTo().getTag()+"("+edge.getTo().getCF()+")");
            }
        }
    }

    public BigDecimal CF(Vertex v){
        reset();
        return _CF(v);
    }


    // CF Implementado con la 2da chance
    public BigDecimal _CF(Vertex v){

        if(isFact(v)){

            if(v.getCF().compareTo(new BigDecimal("-1.0"))==0){
                v.setCF(consulta(v));
            }

            return v.getCF();
        }

        BigDecimal ac = new BigDecimal("0.0");

        List<Vertex> adyacentes = getAdyacentes(v);

        for (Vertex adyacente : adyacentes) {
            ac = ac.add(_CF(adyacente).multiply(prob(adyacente,v)));
        }



        return v.getCF();

    }

    // Check if all vertices are facts
    boolean allFacts(List<Vertex> v){
        for (Vertex vertex : v) {
            if(!isFact(vertex)){
                return false;
            }
        }
        return true;
    }

    BigDecimal consulta(Vertex v){
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Hecho");
        dialog.setHeaderText("Valor del Hecho "+v.getTag());
        dialog.setContentText("Ingrese el valor del Hecho:");

        Optional<String> result = dialog.showAndWait();


        String data = result.get();
        if(!result.get().matches("[-+]?\\d*\\.?\\d+")){
            data="0.0";
        }

        return new BigDecimal(data);
    }

    BigDecimal prob(Vertex u,Vertex v){

        for (Edge arista : u.getAdjacent()) {
            if(arista.getTo().equals(v)){
                return arista.getValue();
            }
        }

        return BigDecimal.ZERO;
    }

    BigDecimal suma(List<Vertex> ady, Vertex v){
        BigDecimal s = new BigDecimal("0.0");

        for (Vertex vertice : ady) {
            for (Edge arista : vertice.getAdjacent()) {
                if(arista.getTo().equals(v)){
                    s = s.add(arista.getValue());
                }
            }
        }

        return s;
    }

    public List<Vertex> getAdyacentes(Vertex v){
        List<Vertex> adyacentes = new ArrayList<>();

        for (Vertex vertex : vertices) {
            for (Edge arista : vertex.getAdjacent()) {
                if(arista.getTo().equals(v)){
                    adyacentes.add(vertex);
                }
            }
        }

        return adyacentes;
    }

    public void reset(){
        for (Vertex vertex : vertices) {
            if(!isFact(vertex) && !isGoal(vertex))
            {
                vertex.setCF(new BigDecimal("-1.0"));
                System.out.println(vertex.getTag()+" val: "+vertex.getCF().toString());
            }
        }
    }

    public boolean checkRB(){

        if(vertices.size()==0){
            return false;
        }

        for (Vertex vertex : vertices) {
            if(!isFact(vertex)){
                if(getAdyacentes(vertex).size()==0)
                {
                    return false;
                }else{
                    BigDecimal s = suma(getAdyacentes(vertex),vertex);
                    if(s.compareTo(new BigDecimal("1.0"))!=0){
                        return false;
                    }
                }

            }
        }

        return true;
    }

    public String guardarRB(){
        Gson gson = new Gson();
        String json = gson.toJson(vertices);
        return json;
    }

    public List<Vertex> cargarRB(String json){

        Gson gson = new Gson();

        Type founderListType = new TypeToken<List<Vertex>>(){}.getType();
        List<Vertex> a = gson.fromJson(json, founderListType);

        return a;

    }

    public void clear(){
        vertices.clear();;
    }

    public boolean existEdgeBetween(Vertex u,Vertex v){

        for (Edge edge : u.getAdjacent()) {
            if(edge.getTo().getTag().compareTo(v.getTag())==0)
                return true;
        }

        for (Edge edge : v.getAdjacent()) {
            if(edge.getTo().getTag().compareTo(u.getTag())==0)
                return true;
        }
        return false;
    }

    public Edge getEdgeBetween(Vertex u,Vertex v){

        for (Vertex vertex : vertices) {
            if(vertex.getTag().compareTo(u.getTag())==0){
                for (Edge edge : vertex.getAdjacent()) {
                    if(edge.getTo().getTag().compareTo(v.getTag())==0){
                        return edge;
                    }
                }
            }
        }
        return null;
    }

    public void deleteEdge(Vertex u,Vertex v){
        if(u==null)return;
        if(v==null)return;

        u.getAdjacent().removeIf(edge -> edge.getTo().equals(v));

    }
}
