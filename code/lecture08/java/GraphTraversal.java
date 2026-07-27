import java.util.*;

public final class GraphTraversal {
    public record BFSResult(int[] order, int[] dist, int[] parent) {}
    public record DFSResult(int[] discoveryOrder, int[] discover, int[] finish,
                            int[] parent) {}

    public static BFSResult bfs(Graph g, int s) {
        int n = g.vertices(), count = 0;
        int[] color = new int[n], dist = new int[n], parent = new int[n], order = new int[n];
        Arrays.fill(dist, -1); Arrays.fill(parent, -1);
        ArrayDeque<Integer> q = new ArrayDeque<>();
        color[s] = 1; dist[s] = 0; q.add(s); // discovered on enqueue
        while (!q.isEmpty()) {
            int u = q.remove();
            order[count++] = u;
            for (Graph.Edge e : g.edgesFrom(u)) if (color[e.to()] == 0) {
                int v = e.to();
                color[v] = 1; dist[v] = dist[u] + 1; parent[v] = u; q.add(v);
            }
            color[u] = 2;
        }
        return new BFSResult(Arrays.copyOf(order, count), dist, parent);
    }

    public static DFSResult dfs(Graph g) {
        int n = g.vertices();
        int[] color = new int[n], d = new int[n], f = new int[n], p = new int[n];
        Arrays.fill(p, -1);
        List<Integer> order = new ArrayList<>();
        int[] time = {0};
        for (int u = 0; u < n; u++) if (color[u] == 0) visit(g, u, color, d, f, p, time, order);
        return new DFSResult(order.stream().mapToInt(Integer::intValue).toArray(), d, f, p);
    }

    private static void visit(Graph g, int u, int[] color, int[] d, int[] f,
                              int[] p, int[] time, List<Integer> order) {
        color[u] = 1; d[u] = ++time[0]; order.add(u);
        for (Graph.Edge e : g.edgesFrom(u)) if (color[e.to()] == 0) {
            p[e.to()] = u; visit(g, e.to(), color, d, f, p, time, order);
        }
        color[u] = 2; f[u] = ++time[0];
    }

    public static void validateBfs(Graph g, int s, BFSResult r) {
        if (r.dist()[s] != 0 || r.parent()[s] != -1) throw new AssertionError();
        for (int v = 0; v < g.vertices(); v++) if (r.parent()[v] >= 0) {
            if (g.weight(r.parent()[v], v) == null) throw new AssertionError("parent edge");
            if (r.dist()[v] != r.dist()[r.parent()[v]] + 1) throw new AssertionError("layer");
        }
    }

    public static void validateDfs(DFSResult r) {
        for (int v = 0; v < r.discover().length; v++) {
            if (!(r.discover()[v] < r.finish()[v])) throw new AssertionError("d/f");
            int p = r.parent()[v];
            if (p >= 0 && !(r.discover()[p] < r.discover()[v] && r.finish()[v] < r.finish()[p]))
                throw new AssertionError("interval nesting");
        }
    }
    private GraphTraversal() {}
}
