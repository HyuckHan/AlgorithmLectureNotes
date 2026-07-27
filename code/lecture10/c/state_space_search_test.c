#include "state_space_search.h"
#include <assert.h>
#include <inttypes.h>
#include <stdio.h>
#include <string.h>

static uint64_t rng_state = UINT64_C(0x10a57a7e);
static uint32_t next_u32(void) {
    rng_state ^= rng_state << 13;
    rng_state ^= rng_state >> 7;
    rng_state ^= rng_state << 17;
    return (uint32_t)rng_state;
}
static uint64_t subset_set(const int64_t *w, size_t n, int64_t target) {
    uint64_t set = 0U, mask, limit = UINT64_C(1) << n;
    for (mask = 0U; mask < limit; mask++) {
        size_t i; int64_t sum = 0;
        for (i = 0U; i < n; i++) if ((mask & (UINT64_C(1) << i)) != 0U) sum += w[i];
        if (sum == target) set |= UINT64_C(1) << mask;
    }
    return set;
}
static void test_permutation(void) {
    uint64_t count; ss_metrics m;
    assert(ss_permutation_count(5U,4U,&count,&m)==SS_OK && count==120U);
    assert(ss_permutation_count(0U,0U,&count,&m)==SS_OK && count==1U);
}
static void test_queens(void) {
    const uint64_t expected[] = {1U,0U,0U,2U,10U,4U,40U,92U};
    size_t n; ss_metrics m; uint64_t count;
    for (n=1U;n<=8U;n++) {
        assert(ss_n_queens_count(n,&count,&m)==SS_OK);
        assert(count==expected[n-1U]);
    }
}
static void test_subset(void) {
    const int64_t w[]={3,4,5,6}; uint64_t a[16],b[16];
    size_t na,nb,i; ss_metrics ma,mb; uint64_t sa=0U,sb=0U;
    assert(ss_subset_sum_masks(w,4U,9,a,16U,&na,true,&ma)==SS_OK);
    assert(ss_subset_sum_masks(w,4U,9,b,16U,&nb,false,&mb)==SS_OK);
    for(i=0U;i<na;i++)sa|=UINT64_C(1)<<a[i];
    for(i=0U;i<nb;i++)sb|=UINT64_C(1)<<b[i];
    assert(na==2U && sa==sb);
}
static void test_knapsack(void) {
    const ss_item items[]={{2,40},{5,30},{10,50},{5,10}};
    ss_knapsack_result r;
    assert(ss_knapsack_bnb(items,4U,16,&r)==SS_OK);
    assert(r.profit==90 && r.weight==12);
}
static void test_astar(void) {
    unsigned char grid[35]={0}; int64_t a,d; ss_metrics ma,md;
    const size_t obs[]={3U,10U,15U,16U,17U,26U}; size_t i;
    for(i=0U;i<sizeof(obs)/sizeof(obs[0]);i++)grid[obs[i]]=1U;
    assert(ss_astar_grid(grid,5U,7U,0U,34U,false,&a,&ma)==SS_OK);
    assert(ss_astar_grid(grid,5U,7U,0U,34U,true,&d,&md)==SS_OK);
    assert(a==10 && d==10);
}
static int64_t graph_astar_policy(bool reopen) {
    const int64_t inf=INT64_C(1000000);
    const int64_t w[4][4]={
        {INT64_C(1000000),3,1,INT64_C(1000000)},
        {INT64_C(1000000),INT64_C(1000000),INT64_C(1000000),3},
        {INT64_C(1000000),1,INT64_C(1000000),100},
        {INT64_C(1000000),INT64_C(1000000),INT64_C(1000000),INT64_C(1000000)}
    };
    const int64_t h[4]={5,0,4,0};
    int64_t g[4]={0,INT64_C(1000000),INT64_C(1000000),INT64_C(1000000)};
    unsigned char open[4]={1U,0U,0U,0U},closed[4]={0U,0U,0U,0U};
    for (;;) {
        size_t u=4U,v;
        int64_t best=inf;
        for(v=0U;v<4U;v++)if(open[v]!=0U && g[v]+h[v]<best){
            best=g[v]+h[v];u=v;
        }
        if(u==4U)return inf;
        open[u]=0U;closed[u]=1U;
        if(u==3U)return g[u];
        for(v=0U;v<4U;v++)if(w[u][v]<inf){
            int64_t ng=g[u]+w[u][v];
            if(ng<g[v]&&(closed[v]==0U||reopen)){
                g[v]=ng;
                if(closed[v]!=0U)closed[v]=0U;
                open[v]=1U;
            }
        }
    }
}
static void test_astar_policy(void) {
    assert(graph_astar_policy(false)==6);
    assert(graph_astar_policy(true)==5);
}
static void randomized_astar(void) {
    size_t t;
    for(t=0U;t<600U;t++){
        unsigned char grid[64]={0U};
        size_t rows=2U+next_u32()%7U,cols=2U+next_u32()%7U,cells=rows*cols,i;
        int64_t a,d;ss_metrics ma,md;
        for(i=0U;i<cells;i++)grid[i]=(unsigned char)(next_u32()%4U==0U);
        grid[0]=0U;grid[cells-1U]=0U;
        {
            ss_status sa=ss_astar_grid(grid,rows,cols,0U,cells-1U,false,&a,&ma);
            ss_status sd=ss_astar_grid(grid,rows,cols,0U,cells-1U,true,&d,&md);
            assert(sa==sd);
            if(sa==SS_OK)assert(a==d);
            else assert(sa==SS_NO_SOLUTION);
        }
    }
}
static void randomized_subset(void) {
    size_t t;
    for(t=0U;t<2000U;t++) {
        int64_t w[6]; size_t n=1U+next_u32()%6U,i,count; int64_t target=next_u32()%20U;
        uint64_t masks[64],got=0U,want; ss_metrics m;
        for(i=0U;i<n;i++)w[i]=1+(int64_t)(next_u32()%9U);
        assert(ss_subset_sum_masks(w,n,target,masks,64U,&count,true,&m)==SS_OK);
        for(i=0U;i<count;i++)got|=UINT64_C(1)<<masks[i];
        want=subset_set(w,n,target);
        assert(got==want);
    }
}
int main(void) {
    test_permutation();test_queens();test_subset();test_knapsack();test_astar();
    test_astar_policy();randomized_subset();randomized_astar();
    puts("Lecture 10 C tests passed: 2000 oracle cases + 600 A*/Dijkstra grids");
    return 0;
}
