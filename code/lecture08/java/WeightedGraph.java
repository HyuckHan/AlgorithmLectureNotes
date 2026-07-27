import java.util.List;

/** Explicit weighted-graph facade used when an API should not imply unit weights. */
public final class WeightedGraph {
    private final Graph graph;
    public WeightedGraph(int vertices, boolean directed) {
        graph = new Graph(vertices, directed);
    }
    public void addEdge(int u, int v, long weight) { graph.addEdge(u, v, weight); }
    public int vertices() { return graph.vertices(); }
    public boolean directed() { return graph.directed(); }
    public List<Graph.Edge> edgesFrom(int u) { return graph.edgesFrom(u); }
    public Graph asGraph() { return graph; }
    public void validate() { graph.validate(); }
}
