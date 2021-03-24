package bayesianNetwork;

import java.math.BigDecimal;

public class Edge {

    private BigDecimal value;
    private Vertex to;

    // Constructor
    public Edge(BigDecimal value,Vertex vertexTo){
        this.value=value;
        this.to=vertexTo;
    }

    // Value
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    // To
    public Vertex getTo() { return to; }
    public void setTo(Vertex to) { this.to = to; }
}
