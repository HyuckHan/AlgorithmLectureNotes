#include <assert.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdlib.h>

typedef struct { long long sum; size_t *rows, *cols, length; } PathResult;

/* 0-based rectangular matrix; right/down only.
 * Theta(rows*cols) time and table space; path owns allocated arrays. */
static bool min_path(const long long *a, size_t rows, size_t cols, PathResult *out) {
    if (!a || !out || rows == 0 || cols == 0) return false;
    size_t n = rows * cols;
    long long *dp = malloc(n * sizeof(*dp));
    unsigned char *parent = calloc(n, 1); /* 1=up, 2=left */
    if (!dp || !parent) { free(dp); free(parent); return false; }
    dp[0] = a[0];
    for (size_t j=1;j<cols;++j){dp[j]=dp[j-1]+a[j];parent[j]=2;}
    for (size_t i=1;i<rows;++i){dp[i*cols]=dp[(i-1)*cols]+a[i*cols];parent[i*cols]=1;}
    for (size_t i=1;i<rows;++i) for(size_t j=1;j<cols;++j){
        size_t k=i*cols+j; long long up=dp[k-cols], left=dp[k-1];
        if (up <= left) { dp[k]=up+a[k]; parent[k]=1; } /* tie: up */
        else { dp[k]=left+a[k]; parent[k]=2; }
    }
    size_t len=rows+cols-1;
    out->rows=malloc(len*sizeof(*out->rows)); out->cols=malloc(len*sizeof(*out->cols));
    if(!out->rows||!out->cols){free(out->rows);free(out->cols);free(dp);free(parent);return false;}
    size_t i=rows-1,j=cols-1,pos=len;
    while(pos){--pos;out->rows[pos]=i;out->cols[pos]=j;if(i==0&&j==0)break;if(parent[i*cols+j]==1)--i;else --j;}
    out->sum=dp[n-1];out->length=len;free(dp);free(parent);return true;
}

static void release(PathResult *r){free(r->rows);free(r->cols);}

int main(void){
    const long long m[]={6,7,12,5,5,3,11,18,7,17,3,3,8,10,14,9};
    PathResult r={0}; assert(min_path(m,4,4,&r)); assert(r.sum==40); assert(r.length==7);
    long long sum=0;for(size_t k=0;k<r.length;++k)sum+=m[r.rows[k]*4+r.cols[k]];assert(sum==40);release(&r);
    const long long one[]={-5};assert(min_path(one,1,1,&r)&&r.sum==-5);release(&r);
    assert(!min_path(NULL,1,1,&r));assert(!min_path(one,0,1,&r));
}
