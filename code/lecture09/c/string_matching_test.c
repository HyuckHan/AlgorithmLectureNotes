#include "string_matching.h"
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static void same(const unsigned char *t,size_t n,const unsigned char *p,size_t m,
                 uint64_t base,uint64_t q) {
    SmMatches a={0},r={0},k={0},h={0};
    assert(sm_naive_all(t,n,p,m,&a));
    assert(sm_rabin_karp_all(t,n,p,m,base,q,&r));
    assert(sm_kmp_all(t,n,p,m,&k));
    assert(sm_horspool_all(t,n,p,m,&h));
    assert(a.count==r.count&&a.count==k.count&&a.count==h.count);
    for(size_t i=0;i<a.count;i++)assert(a.data[i]==r.data[i]&&a.data[i]==k.data[i]&&a.data[i]==h.data[i]);
    sm_matches_destroy(&a);sm_matches_destroy(&r);sm_matches_destroy(&k);sm_matches_destroy(&h);
}
static void basics(void) {
    same(NULL,0,NULL,0,257,1000000007);
    same(NULL,0,(const unsigned char *)"A",1,257,1000000007);
    same((const unsigned char *)"A",1,NULL,0,257,1000000007);
    same((const unsigned char *)"ABC",3,(const unsigned char *)"ABCD",4,257,1000000007);
    same((const unsigned char *)"ABC",3,(const unsigned char *)"Z",1,257,1000000007);
    same((const unsigned char *)"XABC",4,(const unsigned char *)"X",1,257,1000000007);
    same((const unsigned char *)"ABCX",4,(const unsigned char *)"X",1,257,1000000007);
    same((const unsigned char *)"AAAAA",5,(const unsigned char *)"AAA",3,257,1000000007);
    same((const unsigned char *)"ABCABC",6,(const unsigned char *)"ABC",3,5,113);
    assert(sm_naive_first((const unsigned char *)"ABC",3,(const unsigned char *)"BC",2)==1);
    assert(sm_naive_first((const unsigned char *)"ABC",3,NULL,0)==0);
    assert(sm_kmp_first((const unsigned char *)"ABC",3,(const unsigned char *)"Z",1)==SM_NOT_FOUND);
}
static void slide_values(void) {
    const unsigned char p[]="BAABABAA";size_t lps[8],want[]={0,0,0,1,2,1,2,3};
    assert(sm_build_lps(p,8,lps));for(size_t i=0;i<8;i++)assert(lps[i]==want[i]);
    const unsigned char a[]="AAAAA",b[]="ABCDABD";size_t la[5],lb[7];
    size_t wa[]={0,1,2,3,4},wb[]={0,0,0,0,1,2,0};
    assert(sm_build_lps(a,5,la)&&sm_build_lps(b,7,lb));
    for(size_t i=0;i<5;i++)assert(la[i]==wa[i]);
    for(size_t i=0;i<7;i++)assert(lb[i]==wb[i]);
    const unsigned char chain[]="AAAAABAAAAAC";
    size_t lc[12],wc[]={0,1,2,3,4,0,1,2,3,4,5,0};
    assert(sm_build_lps(chain,12,lc));
    for(size_t i=0;i<12;i++)assert(lc[i]==wc[i]);
    size_t s[256];sm_build_horspool_shift((const unsigned char *)"TIGER",5,s);
    assert(s['T']==4&&s['I']==3&&s['G']==2&&s['E']==1&&s['R']==5&&s['X']==5);
    sm_build_horspool_shift((const unsigned char *)"RATIONAL",8,s);
    assert(s['R']==7&&s['A']==1&&s['T']==5&&s['I']==4&&s['O']==3&&s['N']==2&&s['L']==8&&s['X']==8);
    same((const unsigned char *)"acebbceeaabceedb",16,(const unsigned char *)"eeaab",5,5,113);
    same((const unsigned char *)"baaaaaab",8,(const unsigned char *)"aaab",4,5,7);
    /* With code(byte)=byte+1, "bc" and "ab" collide for base 2 modulo 3. */
    assert((((uint64_t)'b'+1)*2+(uint64_t)'c'+1)%3==
           (((uint64_t)'a'+1)*2+(uint64_t)'b'+1)%3);
    same((const unsigned char *)"bc",2,(const unsigned char *)"ab",2,2,3);
}
static void horspool_progress_regression(void) {
    enum { N = 200000 };
    unsigned char *text=malloc(N);
    assert(text);
    memset(text,'A',N);
    SmMatches matches={0};
    assert(sm_horspool_all(text,N,(const unsigned char *)"B",1,&matches));
    assert(matches.count==0);
    sm_matches_destroy(&matches);
    free(text);
}
static void byte_policy(void) {
    const unsigned char text[]={0xf0,0x9f,0x98,0x80,'A',0xf0,0x9f,0x98,0x80};
    const unsigned char pattern[]={0xf0,0x9f,0x98,0x80};
    SmMatches out={0};
    assert(sm_naive_all(text,sizeof text,pattern,sizeof pattern,&out));
    assert(out.count==2&&out.data[0]==0&&out.data[1]==5);
    sm_matches_destroy(&out);
    same(text,sizeof text,pattern,sizeof pattern,257,1000000007);
}
static uint32_t rng_state=0x5eed09u;
static uint32_t next_u32(void){rng_state=rng_state*1664525u+1013904223u;return rng_state;}
static void randomized(void) {
    unsigned char t[31],p[12];
    for(size_t test=0;test<6000;test++){
        size_t n=next_u32()%31,m=next_u32()%12;
        for(size_t i=0;i<n;i++)t[i]=(unsigned char)('A'+next_u32()%4);
        for(size_t i=0;i<m;i++)p[i]=(unsigned char)('A'+next_u32()%4);
        same(t,n,p,m,test%2?5:257,test%2?113:1000000007);
    }
}
int main(void){
    basics();
    slide_values();
    horspool_progress_regression();
    byte_policy();
    randomized();
    puts("Lecture 9 C tests passed: 6000 randomized cases");
}
