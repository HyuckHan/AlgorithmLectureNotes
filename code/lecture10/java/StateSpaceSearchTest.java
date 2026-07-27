import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

public final class StateSpaceSearchTest {
    private static void check(boolean b, String m) { if (!b) throw new AssertionError(m); }
    private static String subsetKey(int[] x) { return Arrays.toString(x); }

    public static void main(String[] args) {
        testPermutation();
        testQueens();
        testSubset();
        testColoring();
        testArithmetic();
        testKnapsack();
        testAStar();
        randomized();
        System.out.println("Lecture 10 Java tests passed: 2000 oracle cases + 600 A*/Dijkstra grids");
    }

    private static void testPermutation() {
        var p = PermutationGenerator.generate(5,4);
        check(p.size()==120,"5P4");
        Set<String> seen=new HashSet<>();
        for(int[] x:p){check(x.length==4&&PermutationGenerator.valid(x,5),"perm valid");seen.add(Arrays.toString(x));}
        check(seen.size()==120,"perm unique");
        check(PermutationGenerator.combinations(5,4).size()==5,"5C4");
        check(PermutationGenerator.generate(0,0).size()==1,"empty permutation");
    }
    private static void testQueens() {
        int[] expected={1,0,0,2};
        for(int n=1;n<=4;n++){
            var r=new NQueensSolver().solve(n);
            check(r.solutions.size()==expected[n-1],"queen count "+n);
            for(int[] s:r.solutions)check(NQueensSolver.valid(s),"queen valid");
        }
        check(new NQueensSolver().solve(8).solutions.size()==92,"8 queens");
    }
    private static void testSubset() {
        int[] w={3,4,5,6};
        var a=new SubsetSumSolver().solve(w,9,true);
        var b=new SubsetSumSolver().solve(w,9,false);
        Set<String> sa=new HashSet<>(),sb=new HashSet<>();
        for(int[] x:a.indexSolutions){check(SubsetSumSolver.sum(w,x)==9,"subset sum");sa.add(subsetKey(x));}
        for(int[] x:b.indexSolutions)sb.add(subsetKey(x));
        check(sa.equals(sb)&&sa.size()==2,"subset oracle");
        check(new SubsetSumSolver().solve(w,0,true).indexSolutions.size()==1,"target zero");
    }
    private static void testColoring() {
        int[][] g={{0,1,1,1},{1,0,1,0},{1,1,0,1},{1,0,1,0}};
        var s=new GraphColoringSolver();
        check(s.color(g,2)==null,"not 2 colorable");
        int[] c=s.color(g,3);check(GraphColoringSolver.valid(g,c),"3 coloring");
    }
    private static void testArithmetic() {
        var solver=new ArithmeticProgressionSearch();
        int[] a=solver.solve(new int[]{4,1,3,5,7},true).sequence;
        check(Arrays.equals(a,new int[]{1,3,5,7}),"AP example");
        check(ArithmeticProgressionSearch.valid(a),"AP valid");
        check(solver.solve(new int[0],true).sequence.length==0,"AP empty");
    }
    private static KnapsackBranchAndBound.Item[] exampleItems() {
        return new KnapsackBranchAndBound.Item[]{
            new KnapsackBranchAndBound.Item("A",2,40),new KnapsackBranchAndBound.Item("B",5,30),
            new KnapsackBranchAndBound.Item("C",10,50),new KnapsackBranchAndBound.Item("D",5,10)};
    }
    private static void testKnapsack() {
        var r=new KnapsackBranchAndBound().solve(exampleItems(),16);
        check(r.profit==90&&r.weight==12&&r.selected.contains("A")&&r.selected.contains("C"),"knapsack");
        check(Math.abs(KnapsackBranchAndBound.bound(exampleItems(),16,0,0,0)-115.0)<1e-9,"root bound");
        verifyExampleBounds(exampleItems(),16);
    }
    private static boolean[][] grid() {
        boolean[][] b=new boolean[5][7];
        int[][] x={{0,3},{1,3},{2,1},{2,2},{2,3},{3,5}};
        for(int[] p:x)b[p[0]][p[1]]=true;return b;
    }
    private static void testAStar() {
        var a=new AStarGrid().search(grid(),0,0,4,6,false);
        var d=new AStarGrid().search(grid(),0,0,4,6,true);
        check(a.cost==10&&d.cost==10&&a.path.size()==11,"A* cost");
        verifyPath(grid(),a,0,0,4,6);
        verifyPath(grid(),d,0,0,4,6);
        check(a.goalDiscoveredBeforeExtraction&&a.goalExtracted,
                "goal must be discovered before termination by extraction");
        check(new AStarGrid().search(new boolean[1][1],0,0,0,0,false).cost==0,"start goal");
        verifyManhattan(grid(),4,6);
        testInconsistentHeuristicPolicy();
    }

    private static void verifyPath(boolean[][] blocked, AStarGrid.Result result,
                                   int sr, int sc, int gr, int gc) {
        if(result.cost==Integer.MAX_VALUE){
            check(result.path.isEmpty()&&!result.goalExtracted,"no-path result");
            return;
        }
        check(result.goalExtracted&&!result.path.isEmpty(),"goal extraction/path");
        int[] first=result.path.get(0),last=result.path.get(result.path.size()-1);
        check(first[0]==sr&&first[1]==sc&&last[0]==gr&&last[1]==gc,"path endpoints");
        int cost=0;
        for(int i=0;i<result.path.size();i++){
            int[] p=result.path.get(i);
            check(!blocked[p[0]][p[1]],"path obstacle");
            if(i>0){
                int[] q=result.path.get(i-1);
                check(Math.abs(p[0]-q[0])+Math.abs(p[1]-q[1])==1,"parent adjacency");
                cost++;
            }
        }
        check(cost==result.cost,"reconstructed path cost");
    }

    private record GraphEntry(int node, int g, int f) implements Comparable<GraphEntry> {
        @Override public int compareTo(GraphEntry other) {
            int c=Integer.compare(f,other.f);
            return c!=0?c:Integer.compare(node,other.node);
        }
    }
    private static int graphAStar(boolean reopen) {
        final int inf=1_000_000;
        int[][] w={
                {inf,3,1,inf},
                {inf,inf,inf,3},
                {inf,1,inf,100},
                {inf,inf,inf,inf}
        };
        int[] h={5,0,4,0},g={0,inf,inf,inf};
        boolean[] closed=new boolean[4];
        PriorityQueue<GraphEntry> open=new PriorityQueue<>();
        open.add(new GraphEntry(0,0,h[0]));
        while(!open.isEmpty()){
            GraphEntry e=open.remove();
            if(e.f()!=g[e.node()]+h[e.node()]||closed[e.node()])continue;
            closed[e.node()]=true;
            if(e.node()==3)return e.g();
            for(int v=0;v<4;v++)if(w[e.node()][v]<inf){
                int ng=e.g()+w[e.node()][v];
                if(ng<g[v]&&(!closed[v]||reopen)){
                    g[v]=ng;
                    if(closed[v])closed[v]=false;
                    open.add(new GraphEntry(v,ng,ng+h[v]));
                }
            }
        }
        return inf;
    }
    private static void testInconsistentHeuristicPolicy() {
        check(graphAStar(false)==6,"inconsistent no-reopen counterexample");
        check(graphAStar(true)==5,"inconsistent heuristic requires reopening");
    }

    private static void verifyExampleBounds(KnapsackBranchAndBound.Item[] x, long capacity) {
        for (int level=0; level<=x.length; level++) {
            int prefixes=1<<level;
            for (int mask=0; mask<prefixes; mask++) {
                long w=0,p=0;
                for(int i=0;i<level;i++)if((mask&(1<<i))!=0){w+=x[i].weight;p+=x[i].profit;}
                if(w>capacity)continue;
                long best=p;
                int rest=1<<(x.length-level);
                for(int suffix=0;suffix<rest;suffix++){
                    long sw=w,sp=p;
                    for(int j=level;j<x.length;j++)if((suffix&(1<<(j-level)))!=0){sw+=x[j].weight;sp+=x[j].profit;}
                    if(sw<=capacity)best=Math.max(best,sp);
                }
                double ub=KnapsackBranchAndBound.bound(x,capacity,level,w,p);
                check(ub+1e-9>=best,"unsafe knapsack bound");
            }
        }
    }

    private static void verifyManhattan(boolean[][] b,int gr,int gc) {
        int rows=b.length,cols=b[0].length;
        int[][] dir={{-1,0},{0,-1},{0,1},{1,0}};
        for(int r=0;r<rows;r++)for(int c=0;c<cols;c++)if(!b[r][c]){
            int h=Math.abs(r-gr)+Math.abs(c-gc);
            AStarGrid.Result oracle=new AStarGrid().search(b,r,c,gr,gc,true);
            if(oracle.cost!=Integer.MAX_VALUE)check(h<=oracle.cost,"Manhattan admissible");
            for(int[] d:dir){
                int nr=r+d[0],nc=c+d[1];
                if(nr>=0&&nr<rows&&nc>=0&&nc<cols&&!b[nr][nc]){
                    int hv=Math.abs(nr-gr)+Math.abs(nc-gc);
                    check(h<=1+hv,"Manhattan consistent");
                }
            }
        }
    }
    private static void randomized() {
        Random rnd=new Random(20260723L);
        for(int t=0;t<2000;t++){
            int n=1+rnd.nextInt(10);int[] w=new int[n];
            for(int i=0;i<n;i++)w[i]=1+rnd.nextInt(12);
            int target=rnd.nextInt(30);
            var p=new SubsetSumSolver().solve(w,target,true);
            var q=new SubsetSumSolver().solve(w,target,false);
            Set<String> a=new HashSet<>(),b=new HashSet<>();
            for(int[] x:p.indexSolutions)a.add(subsetKey(x));
            for(int[] x:q.indexSolutions)b.add(subsetKey(x));
            check(a.equals(b),"random subset oracle");
            int[] values=new int[2+rnd.nextInt(8)];
            for(int i=0;i<values.length;i++)values[i]=rnd.nextInt(20)-5;
            int withBound=new ArithmeticProgressionSearch().solve(values,true).sequence.length;
            int withoutBound=new ArithmeticProgressionSearch().solve(values,false).sequence.length;
            check(withBound==withoutBound,"random AP bound oracle");
        }
        for(int t=0;t<500;t++){
            int n=1+rnd.nextInt(9);long capacity=1+rnd.nextInt(30);
            KnapsackBranchAndBound.Item[] items=new KnapsackBranchAndBound.Item[n];
            for(int i=0;i<n;i++)items[i]=new KnapsackBranchAndBound.Item("I"+i,1+rnd.nextInt(12),rnd.nextInt(30));
            long oracle=0;
            for(int mask=0;mask<(1<<n);mask++){
                long w=0,p=0;
                for(int i=0;i<n;i++)if((mask&(1<<i))!=0){w+=items[i].weight;p+=items[i].profit;}
                if(w<=capacity)oracle=Math.max(oracle,p);
            }
            check(new KnapsackBranchAndBound().solve(items,capacity).profit==oracle,"random knapsack oracle");
        }
        for(int t=0;t<600;t++){
            int rows=2+rnd.nextInt(7),cols=2+rnd.nextInt(7);
            boolean[][] blocked=new boolean[rows][cols];
            for(int r=0;r<rows;r++)for(int c=0;c<cols;c++)
                blocked[r][c]=rnd.nextInt(4)==0;
            blocked[0][0]=false;blocked[rows-1][cols-1]=false;
            AStarGrid.Result a=new AStarGrid().search(blocked,0,0,rows-1,cols-1,false);
            AStarGrid.Result d=new AStarGrid().search(blocked,0,0,rows-1,cols-1,true);
            check(a.cost==d.cost,"random A*/Dijkstra cost");
            verifyPath(blocked,a,0,0,rows-1,cols-1);
            verifyPath(blocked,d,0,0,rows-1,cols-1);
        }
    }
}
