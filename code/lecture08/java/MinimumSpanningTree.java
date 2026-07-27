import java.util.*;

public final class MinimumSpanningTree {
    public record MstEdge(int u, int v, long weight) {}
    public record Result(List<MstEdge> edges, long weight, boolean connected) {}

    public static Result prim(Graph g, int root) {
        requireUndirected(g);
        int n = g.vertices();
        long[] key = new long[n]; int[] parent = new int[n]; boolean[] in = new boolean[n];
        Arrays.fill(key, Long.MAX_VALUE); Arrays.fill(parent, -1); key[root] = 0;
        PriorityQueue<long[]> pq = new PriorityQueue<>(
            Comparator.<long[]>comparingLong(x -> x[0]).thenComparingLong(x -> x[1]));
        pq.add(new long[]{0, root});
        List<MstEdge> out = new ArrayList<>(); long total = 0;
        while (!pq.isEmpty()) {
            long[] item = pq.remove(); int u = (int)item[1];
            if (in[u] || item[0] != key[u]) continue;
            in[u] = true;
            if (parent[u] >= 0) { out.add(new MstEdge(parent[u], u, key[u])); total += key[u]; }
            for (Graph.Edge e : g.edgesFrom(u)) if (!in[e.to()] &&
                    (e.weight() < key[e.to()] ||
                     e.weight() == key[e.to()] && u < parent[e.to()])) {
                key[e.to()] = e.weight(); parent[e.to()] = u;
                pq.add(new long[]{key[e.to()], e.to()});
            }
        }
        return new Result(List.copyOf(out), total, out.size() == Math.max(0, n - 1));
    }

    public static Result kruskal(Graph g) {
        requireUndirected(g);
        List<MstEdge> edges = new ArrayList<>();
        for (int u = 0; u < g.vertices(); u++)
            for (Graph.Edge e : g.edgesFrom(u)) if (u < e.to())
                edges.add(new MstEdge(u, e.to(), e.weight()));
        edges.sort(Comparator.comparingLong(MstEdge::weight)
            .thenComparingInt(MstEdge::u).thenComparingInt(MstEdge::v));
        DisjointSet dsu = new DisjointSet(g.vertices());
        List<MstEdge> out = new ArrayList<>(); long total = 0;
        for (MstEdge e : edges) if (dsu.union(e.u(), e.v())) {
            out.add(e); total += e.weight();
            if (out.size() == g.vertices() - 1) break;
        }
        return new Result(List.copyOf(out), total, out.size() == Math.max(0, g.vertices() - 1));
    }

    public static void validate(Graph g, Result r) {
        DisjointSet d = new DisjointSet(g.vertices()); long sum = 0;
        for (MstEdge e : r.edges()) {
            if (!Objects.equals(g.weight(e.u(), e.v()), e.weight()) || !d.union(e.u(), e.v()))
                throw new AssertionError("invalid MST edge");
            sum += e.weight();
        }
        if (sum != r.weight() || r.connected() && r.edges().size() != g.vertices() - 1)
            throw new AssertionError("MST invariant");
    }
    private static void requireUndirected(Graph g) {
        if (g.directed()) throw new IllegalArgumentException("MST needs undirected graph");
    }
    private MinimumSpanningTree() {}
}
