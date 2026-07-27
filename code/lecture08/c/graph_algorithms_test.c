#include "graph.h"
#include <assert.h>
#include <stdio.h>

static void add(Graph *g, size_t u, size_t v, int64_t w) { assert(graph_add_edge(g,u,v,w)); }
static void traversal_test(void) {
    Graph *g = graph_create(8,false); assert(g);
    size_t e[][2]={{0,1},{0,2},{0,3},{1,4},{2,4},{2,5},{3,6},{4,7},{6,7}};
    for (size_t i=0;i<9;i++) add(g,e[i][0],e[i][1],1);
    assert(graph_validate(g));
    int d[8]; ptrdiff_t p[8]; size_t o[8]; assert(graph_bfs(g,0,d,p,o));
    int wantd[]={0,1,1,1,2,2,2,3}; ptrdiff_t wantp[]={-1,0,0,0,1,2,3,4};
    size_t wanto[]={0,1,2,3,4,5,6,7};
    for(size_t i=0;i<8;i++){assert(d[i]==wantd[i]);assert(p[i]==wantp[i]);assert(o[i]==wanto[i]);}
    size_t disc[8],fin[8]; assert(graph_dfs(g,disc,fin,p));
    size_t wantdisc[]={1,2,4,10,3,5,9,8}, wantfin[]={16,15,7,11,14,6,12,13};
    ptrdiff_t wantdp[]={-1,0,4,6,1,2,7,4};
    for(size_t i=0;i<8;i++){assert(disc[i]==wantdisc[i]);assert(fin[i]==wantfin[i]);assert(p[i]==wantdp[i]);}
    graph_destroy(g);
}
static void topo_test(void) {
    Graph *g=graph_create(6,true); assert(g);
    size_t e[][2]={{0,1},{0,3},{1,2},{1,4},{2,5},{3,5},{4,5}};
    for(size_t i=0;i<7;i++)add(g,e[i][0],e[i][1],1);
    size_t o[6]; assert(graph_topological_kahn(g,o));
    size_t want[]={0,1,2,3,4,5}; for(size_t i=0;i<6;i++)assert(o[i]==want[i]);
    graph_destroy(g);
    g=graph_create(3,true); add(g,0,1,1);add(g,1,2,1);add(g,2,0,1);
    assert(!graph_topological_kahn(g,o)); graph_destroy(g);
}
static void dsu_trace_test(void) {
    DisjointSet d={0}; assert(dsu_init(&d,4));
    assert(dsu_find(&d,0)!=dsu_find(&d,3));
    assert(dsu_union(&d,0,1)&&dsu_find(&d,0)!=dsu_find(&d,3));
    assert(dsu_union(&d,2,3)&&dsu_find(&d,0)!=dsu_find(&d,3));
    assert(dsu_union(&d,1,2)&&dsu_find(&d,0)==dsu_find(&d,3));
    assert(!dsu_union(&d,0,3)); dsu_destroy(&d);
}
static void mst_test(void) {
    Graph *g=graph_create(7,false); assert(g);
    int64_t e[][3]={{0,1,8},{0,2,9},{0,3,11},{1,4,10},{2,3,13},
      {2,4,5},{2,5,12},{3,5,8},{3,6,8},{5,6,7}};
    for(size_t i=0;i<10;i++)add(g,(size_t)e[i][0],(size_t)e[i][1],e[i][2]);
    MstResult p={0},k={0};assert(graph_prim(g,0,&p));assert(graph_kruskal(g,&k));
    assert(p.connected&&k.connected&&p.count==6&&k.count==6&&p.weight==48&&k.weight==48);
    MstEdge wp[]={{0,1,8},{0,2,9},{2,4,5},{0,3,11},{3,5,8},{5,6,7}};
    MstEdge wk[]={{2,4,5},{5,6,7},{0,1,8},{3,5,8},{0,2,9},{0,3,11}};
    for(size_t i=0;i<6;i++){
        assert(p.edges[i].u==wp[i].u&&p.edges[i].v==wp[i].v&&p.edges[i].weight==wp[i].weight);
        assert(k.edges[i].u==wk[i].u&&k.edges[i].v==wk[i].v&&k.edges[i].weight==wk[i].weight);
    }
    mst_result_destroy(&p);mst_result_destroy(&k);graph_destroy(g);
}
static void dag_shortest_trace_test(void) {
    int64_t d[]={0,GRAPH_INF,GRAPH_INF,GRAPH_INF};
    ptrdiff_t p[]={-1,-1,-1,-1};
    int64_t e[][3]={{0,1,3},{0,2,2},{1,3,-4},{2,3,1}};
    for(size_t u=0;u<4;u++)for(size_t i=0;i<4;i++)if((size_t)e[i][0]==u){
        size_t v=(size_t)e[i][1];int64_t cand=d[u]+e[i][2];
        if(cand<d[v]){d[v]=cand;p[v]=(ptrdiff_t)u;}
    }
    int64_t want[]={0,3,2,-1};ptrdiff_t wantp[]={-1,0,0,1};
    for(size_t i=0;i<4;i++){assert(d[i]==want[i]);assert(p[i]==wantp[i]);}
}
static void bellman_ford_pass_test(void) {
    int64_t e[][3]={{3,4,2},{1,3,-2},{2,3,3},{0,1,4},{0,2,5},{2,4,6}};
    int64_t want[][5]={{0,GRAPH_INF,GRAPH_INF,GRAPH_INF,GRAPH_INF},
      {0,4,5,GRAPH_INF,11},{0,4,5,2,11},{0,4,5,2,4},{0,4,5,2,4}};
    int64_t d[5];for(size_t i=0;i<5;i++)d[i]=want[0][i];
    for(size_t pass=1;pass<=4;pass++){
        for(size_t i=0;i<6;i++){size_t u=(size_t)e[i][0],v=(size_t)e[i][1];
            if(d[u]!=GRAPH_INF&&d[u]+e[i][2]<d[v])d[v]=d[u]+e[i][2];}
        for(size_t i=0;i<5;i++)assert(d[i]==want[pass][i]);
    }
}
static void shortest_test(void) {
    Graph *g=graph_create(6,true);assert(g);
    int64_t e[][3]={{0,1,4},{0,2,2},{2,1,1},{1,3,5},{2,3,8},{2,4,10},{3,4,2},{3,5,6},{4,5,3}};
    for(size_t i=0;i<9;i++)add(g,(size_t)e[i][0],(size_t)e[i][1],e[i][2]);
    int64_t d[6],want[]={0,3,2,8,10,13};ptrdiff_t p[6],wantp[]={-1,2,0,1,3,4};assert(graph_dijkstra(g,0,d,p));
    for(size_t i=0;i<6;i++){assert(d[i]==want[i]);assert(p[i]==wantp[i]);}
    size_t path[6],len=0;assert(graph_reconstruct_path(6,0,5,p,path,&len)&&len==6);
    size_t wantpath[]={0,2,1,3,4,5};for(size_t i=0;i<6;i++)assert(path[i]==wantpath[i]);
    graph_destroy(g);
    g=graph_create(5,true); int64_t b[][3]={{3,4,2},{1,3,-2},{2,3,3},{0,1,4},{0,2,5},{2,4,6}};
    for(size_t i=0;i<6;i++)add(g,(size_t)b[i][0],(size_t)b[i][1],b[i][2]);
    bool neg=false;assert(graph_bellman_ford(g,0,d,p,&neg)&&!neg);
    int64_t bw[]={0,4,5,2,4};for(size_t i=0;i<5;i++)assert(d[i]==bw[i]);graph_destroy(g);
    g=graph_create(3,true);add(g,0,1,1);add(g,1,2,-2);add(g,2,1,-2);
    assert(graph_bellman_ford(g,0,d,p,&neg)&&neg);assert(!graph_dijkstra(g,0,d,p));graph_destroy(g);
}
int main(void){
    traversal_test();topo_test();dsu_trace_test();mst_test();
    dag_shortest_trace_test();bellman_ford_pass_test();shortest_test();
    puts("Lecture 8 C graph tests passed");
}
