#include <assert.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>

/* Returns one LCS using tie-up backtracking.
 * Theta(m*n) time/table space. Caller frees *out. */
static bool lcs(const char *x, const char *y, char **out) {
    if (!x || !y || !out) return false;
    size_t m=strlen(x),n=strlen(y),w=n+1;
    size_t *dp=calloc((m+1)*w,sizeof(*dp)); if(!dp)return false;
    for(size_t i=1;i<=m;++i)for(size_t j=1;j<=n;++j)
        dp[i*w+j]=(x[i-1]==y[j-1])?dp[(i-1)*w+j-1]+1:
            (dp[(i-1)*w+j]>=dp[i*w+j-1]?dp[(i-1)*w+j]:dp[i*w+j-1]);
    size_t len=dp[m*w+n];char *s=malloc(len+1);if(!s){free(dp);return false;}s[len]='\0';
    size_t i=m,j=n,k=len;
    while(i&&j){if(x[i-1]==y[j-1]){s[--k]=x[i-1];--i;--j;}
        else if(dp[(i-1)*w+j]>=dp[i*w+j-1])--i;else --j;}
    free(dp);*out=s;return true;
}
static bool is_subsequence(const char *s,const char *t){while(*s&&*t){if(*s==*t)++s;++t;}return *s=='\0';}
int main(void){
    char *s=NULL;assert(lcs("ABCBDAB","BDCABA",&s));assert(strlen(s)==4);
    assert(is_subsequence(s,"ABCBDAB")&&is_subsequence(s,"BDCABA"));assert(strcmp(s,"BCBA")==0);free(s);
    assert(lcs("","ABC",&s)&&strcmp(s,"")==0);free(s);
    assert(!lcs(NULL,"A",&s));
}
