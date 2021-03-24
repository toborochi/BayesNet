package bayesianNetwork;

import javafx.util.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Vertex {

    private BigDecimal CF = new BigDecimal("-1.0");
    private String tag;
    private Pair<Double,Double> position;
    private List<Edge> adjacent = new ArrayList<>();

    // CF
    public BigDecimal getCF() { return CF; }
    public void setCF(BigDecimal CF) { this.CF = CF; }

    // Tag
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    // Position
    public void setPosition(double x,double y) {
        this.position = new Pair<>(x,y);
    }
    public double getXPosition(){ return position.getKey(); }
    public double getYPosition(){
        return position.getValue();
    }

    // Edges
    public void deleteEdge(Edge edge){
        adjacent.remove(edge);
    }
    public void addEdge(Edge edge){
        adjacent.add(edge);
    }
    public List<Edge> getAdjacent() { return adjacent; }

    // Constructors
    public Vertex(String tag){
        this.tag=tag;
    }

    @Override
    public boolean equals(Object obj) {

        Vertex v = (Vertex) obj;

        if (v == null) {
            return false;
        }

        if (v.getClass() != this.getClass()) {
            return false;
        }

        if(v.tag.compareTo(tag)!=0){
            return false;
        }

        return true;

    }
}
