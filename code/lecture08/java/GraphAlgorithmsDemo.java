import java.util.*;

public final class GraphAlgorithmsDemo {
    public static void main(String[] args) {
        traversalAndRepresentation();
        topological();
        mst();
        shortestPaths();
        System.out.println("Lecture 8 Java graph tests passed");
    }

    private static void traversalAndRepresentation() {
        Graph g = new Graph(8, false);
        int[][] edges = {{0,1},{0,2},{0,3},{1,4},{2,4},{2,5},{3,6},{4,7},{6,7}};
        for (int[] e : edges) g.addEdge(e[0], e[1], 1);
        g.validate();
        var b = GraphTraversal.bfs(g, 0);
        check(b.order(), new int[]{0,1,2,3,4,5,6,7});
        check(b.dist(), new int[]{0,1,1,1,2,2,2,3});
        check(b.parent(), new int[]{-1,0,0,0,1,2,3,4});
        GraphTraversal.validateBfs(g, 0, b);
        validateBfsTrace(g);
        var d = GraphTraversal.dfs(g);
        check(d.discoveryOrder(), new int[]{0,1,4,2,5,7,6,3});
        check(d.discover(), new int[]{1,2,4,10,3,5,9,8});
        check(d.finish(), new int[]{16,15,7,11,14,6,12,13});
        check(d.parent(), new int[]{-1,0,4,6,1,2,7,4});
        GraphTraversal.validateDfs(d);
        validateDfsSnapshots(d);
        Graph zero = new Graph(2, true); zero.addEdge(0, 1, 0);
        if (!Objects.equals(zero.weight(0, 1), 0L) || zero.weight(1, 0) != null)
            throw new AssertionError("zero/no-edge");
    }

    private static void topological() {
        Graph dag = new Graph(6, true);
        int[][] e = {{0,1},{0,3},{1,2},{1,4},{2,5},{3,5},{4,5}};
        for (int[] x : e) dag.addEdge(x[0], x[1], 1);
        int[] k = TopologicalSort.kahn(dag).orElseThrow();
        check(k, new int[]{0,1,2,3,4,5}); TopologicalSort.validate(dag, k);
        check(k, lexicographicallySmallestTopologicalOrder(dag));
        validateKahnHeapTrace(dag);
        TopologicalSort.validate(dag, TopologicalSort.dfs(dag).orElseThrow());
        Graph cycle = new Graph(3, true);
        cycle.addEdge(0,1,1); cycle.addEdge(1,2,1); cycle.addEdge(2,0,1);
        if (TopologicalSort.kahn(cycle).isPresent() || TopologicalSort.dfs(cycle).isPresent())
            throw new AssertionError("cycle accepted");
        validateDsuTrace();
    }

    private static void mst() {
        Graph g = new Graph(7, false);
        long[][] e = {{0,1,8},{0,2,9},{0,3,11},{1,4,10},{2,3,13},
            {2,4,5},{2,5,12},{3,5,8},{3,6,8},{5,6,7}};
        for (long[] x : e) g.addEdge((int)x[0], (int)x[1], x[2]);
        var p = MinimumSpanningTree.prim(g, 0);
        var k = MinimumSpanningTree.kruskal(g);
        MinimumSpanningTree.validate(g, p); MinimumSpanningTree.validate(g, k);
        if (!p.connected() || !k.connected() || p.weight() != 48 || k.weight() != 48)
            throw new AssertionError("MST weight");
        if (!p.edges().equals(List.of(
                new MinimumSpanningTree.MstEdge(0,1,8),
                new MinimumSpanningTree.MstEdge(0,2,9),
                new MinimumSpanningTree.MstEdge(2,4,5),
                new MinimumSpanningTree.MstEdge(0,3,11),
                new MinimumSpanningTree.MstEdge(3,5,8),
                new MinimumSpanningTree.MstEdge(5,6,7))))
            throw new AssertionError("Prim trace " + p.edges());
        if (!k.edges().equals(List.of(
                new MinimumSpanningTree.MstEdge(2,4,5),
                new MinimumSpanningTree.MstEdge(5,6,7),
                new MinimumSpanningTree.MstEdge(0,1,8),
                new MinimumSpanningTree.MstEdge(3,5,8),
                new MinimumSpanningTree.MstEdge(0,2,9),
                new MinimumSpanningTree.MstEdge(0,3,11))))
            throw new AssertionError("Kruskal trace " + k.edges());
        validateKruskalDgRejection();
    }

    private static void shortestPaths() {
        Graph g = new Graph(6, true);
        long[][] e = {{0,1,4},{0,2,2},{2,1,1},{1,3,5},{2,3,8},
            {2,4,10},{3,4,2},{3,5,6},{4,5,3}};
        for (long[] x : e) g.addEdge((int)x[0], (int)x[1], x[2]);
        var d = ShortestPaths.dijkstra(g, 0);
        check(d.dist(), new long[]{0,3,2,8,10,13});
        check(d.parent(), new int[]{-1,2,0,1,3,4});
        check(ShortestPaths.path(d, 5), new int[]{0,2,1,3,4,5});
        ShortestPaths.validate(g, 0, d);
        if (2 + 1 + 5 + 2 + 3 != d.dist()[5]) throw new AssertionError("path weight");
        validateNegativeEdgeCounterexample();
        validateDagShortestPath();

        Graph bf = new Graph(5, true);
        long[][] be = {{3,4,2},{1,3,-2},{2,3,3},{0,1,4},{0,2,5},{2,4,6}};
        for (long[] x : be) bf.addEdge((int)x[0], (int)x[1], x[2]);
        var br = ShortestPaths.bellmanFord(bf, 0);
        check(br.dist(), new long[]{0,4,5,2,4});
        if (br.negativeCycle()) throw new AssertionError();
        validateBellmanFordPasses(be);
        Graph neg = new Graph(3, true);
        neg.addEdge(0,1,1); neg.addEdge(1,2,-2); neg.addEdge(2,1,-2);
        if (!ShortestPaths.bellmanFord(neg, 0).negativeCycle()) throw new AssertionError();
        try { ShortestPaths.dijkstra(neg, 0); throw new AssertionError("negative accepted"); }
        catch (IllegalArgumentException expected) {}
    }

    private static void validateBfsTrace(Graph g) {
        List<List<Integer>> wantQueues = List.of(
            List.of(0), List.of(1,2,3), List.of(2,3,4), List.of(3,4,5),
            List.of(4,5,6), List.of(5,6,7), List.of(6,7), List.of(7), List.of());
        List<List<Integer>> queues = new ArrayList<>();
        List<Integer> output = new ArrayList<>();
        boolean[] discovered = new boolean[g.vertices()];
        ArrayDeque<Integer> q = new ArrayDeque<>();
        q.add(0); discovered[0] = true; queues.add(List.copyOf(q));
        while (!q.isEmpty()) {
            int u = q.remove();
            output.add(u); // Output only after dequeue.
            for (Graph.Edge e : g.edgesFrom(u)) if (!discovered[e.to()]) {
                discovered[e.to()] = true; q.add(e.to());
            }
            queues.add(List.copyOf(q));
        }
        if (!queues.equals(wantQueues) ||
                !output.equals(List.of(0,1,2,3,4,5,6,7)))
            throw new AssertionError("BFS queue/output trace");
    }

    private static void validateDfsSnapshots(GraphTraversal.DFSResult d) {
        int[][] snapshots = {{0},{0,1,4},{0,1,4},{0,1,4,7},{0,1},{}};
        for (int[] stack : snapshots)
            for (int i = 1; i < stack.length; i++)
                if (d.parent()[stack[i]] != stack[i - 1])
                    throw new AssertionError("impossible DFS stack");
        if (!(d.finish()[5] < d.finish()[2] && d.finish()[2] < d.finish()[4] &&
              d.finish()[3] < d.finish()[6] && d.finish()[6] < d.finish()[7]))
            throw new AssertionError("DFS finish trace");
    }

    private static int[] lexicographicallySmallestTopologicalOrder(Graph g) {
        int n = g.vertices();
        int[] perm = new int[n], answer = new int[0];
        boolean[] used = new boolean[n];
        return firstValidPermutation(g, 0, perm, used, answer);
    }

    private static int[] firstValidPermutation(Graph g, int at, int[] perm,
                                                boolean[] used, int[] none) {
        if (at == perm.length) {
            try { TopologicalSort.validate(g, perm); return perm.clone(); }
            catch (AssertionError invalid) { return none; }
        }
        for (int v = 0; v < perm.length; v++) if (!used[v]) {
            used[v] = true; perm[at] = v;
            int[] result = firstValidPermutation(g, at + 1, perm, used, none);
            used[v] = false;
            if (result.length != 0) return result;
        }
        return none;
    }

    private static void validateDsuTrace() {
        DisjointSet d = new DisjointSet(4); // A=0, B=1, C=2, D=3
        if (d.find(0) == d.find(3)) throw new AssertionError("initial AD roots");
        if (!d.union(0,1) || d.find(0) == d.find(3)) throw new AssertionError("after AB");
        if (!d.union(2,3) || d.find(0) == d.find(3)) throw new AssertionError("after CD");
        if (!d.union(1,2) || d.find(0) != d.find(3)) throw new AssertionError("after BC");
        if (d.union(0,3)) throw new AssertionError("cycle edge AD accepted");
    }

    private static void validateKahnHeapTrace(Graph g) {
        List<List<Integer>> want = List.of(
            List.of(0), List.of(1,3), List.of(2,3,4),
            List.of(3,4), List.of(4), List.of(5));
        int[] indegree = new int[g.vertices()];
        for (long[] e : g.arcs()) indegree[(int)e[1]]++;
        PriorityQueue<Integer> zero = new PriorityQueue<>();
        for (int v = 0; v < g.vertices(); v++) if (indegree[v] == 0) zero.add(v);
        List<List<Integer>> actual = new ArrayList<>();
        while (!zero.isEmpty()) {
            actual.add(zero.stream().sorted().toList());
            int u = zero.remove();
            for (Graph.Edge e : g.edgesFrom(u))
                if (--indegree[e.to()] == 0) zero.add(e.to());
        }
        if (!actual.equals(want) || !zero.isEmpty())
            throw new AssertionError("Kahn heap trace " + actual);
    }

    private static void validateKruskalDgRejection() {
        DisjointSet d = new DisjointSet(7);
        if (!d.union(2,4) || !d.union(5,6) || !d.union(0,1) || !d.union(3,5))
            throw new AssertionError("Kruskal prefix");
        if (d.find(3) != d.find(6) || d.union(3,6))
            throw new AssertionError("DG must be rejected after DF and FG");
    }

    private static void validateDagShortestPath() {
        long[] d = {0, ShortestPaths.INF, ShortestPaths.INF, ShortestPaths.INF};
        int[] p = {-1,-1,-1,-1}; // s=0, a=1, b=2, c=3; topo s,a,b,c
        long[][] edges = {{0,1,3},{0,2,2},{1,3,-4},{2,3,1}};
        for (int u : new int[]{0,1,2,3}) for (long[] e : edges)
            if (e[0] == u && d[u] + e[2] < d[(int)e[1]]) {
                d[(int)e[1]] = d[u] + e[2]; p[(int)e[1]] = u;
            }
        check(d, new long[]{0,3,2,-1});
        check(p, new int[]{-1,0,0,1});
    }

    private static void validateNegativeEdgeCounterexample() {
        long finalizedA = 2, tentativeB = 5, edgeBA = -10;
        if (tentativeB + edgeBA != -5 || !(tentativeB + edgeBA < finalizedA))
            throw new AssertionError("negative-edge counterexample");
    }

    private static void validateBellmanFordPasses(long[][] edges) {
        long I = ShortestPaths.INF;
        long[][] want = {{0,I,I,I,I},{0,4,5,I,11},{0,4,5,2,11},
                         {0,4,5,2,4},{0,4,5,2,4}};
        long[] d = want[0].clone();
        for (int pass = 1; pass <= 4; pass++) {
            for (long[] e : edges) {
                int u = (int)e[0], v = (int)e[1];
                if (d[u] != I && d[u] + e[2] < d[v]) d[v] = d[u] + e[2];
            }
            check(d, want[pass]);
        }
    }

    private static void check(int[] a, int[] b) { if (!Arrays.equals(a,b)) throw new AssertionError(Arrays.toString(a)); }
    private static void check(long[] a, long[] b) { if (!Arrays.equals(a,b)) throw new AssertionError(Arrays.toString(a)); }
}
