#include <assert.h>
#include <stdbool.h>
#include <stddef.h>

typedef struct { long long sum; size_t start, end; } MaxSubarrayResult;
static size_t length(MaxSubarrayResult r){return r.end-r.start+1;}
static bool better(MaxSubarrayResult a, MaxSubarrayResult b){
    return a.sum>b.sum||(a.sum==b.sum&&(length(a)<length(b)||(length(a)==length(b)&&a.start<b.start)));
}
/* Nonempty, 0-based inclusive interval. Theta(n) time, Theta(1) space.
 * long long reduces but cannot eliminate overflow risk. */
static bool max_subarray(const long long *a,size_t n,MaxSubarrayResult *out){
    if(!a||!out||n==0)return false;
    MaxSubarrayResult ending={a[0],0,0},best=ending;
    for(size_t i=1;i<n;++i){
        MaxSubarrayResult extend={ending.sum+a[i],ending.start,i},restart={a[i],i,i};
        ending=better(restart,extend)?restart:extend;if(better(ending,best))best=ending;
    }*out=best;return true;
}
int main(void){
    const long long a[]={-2,1,-3,4,-1,2,1,-5,4};MaxSubarrayResult r;
    assert(max_subarray(a,9,&r)&&r.sum==6&&r.start==3&&r.end==6);
    const long long neg[]={-8,-3,-6,-2,-5,-4};assert(max_subarray(neg,6,&r)&&r.sum==-2&&r.start==3);
    const long long one[]={7};assert(max_subarray(one,1,&r)&&r.sum==7);assert(!max_subarray(one,0,&r));
}
