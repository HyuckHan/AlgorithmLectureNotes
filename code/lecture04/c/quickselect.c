#include <assert.h>
#include <limits.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static void swap_int(int *a, int *b) {
    int t = *a; *a = *b; *b = t;
}

/* Partitions [lo, hi) into < pivot, == pivot, > pivot. */
static void partition3(int *a, size_t lo, size_t hi, int pivot,
                       size_t *lt_out, size_t *gt_out) {
    size_t lt = lo, scan = lo, gt = hi;
    while (scan < gt) {
        if (a[scan] < pivot) swap_int(&a[lt++], &a[scan++]);
        else if (a[scan] > pivot) swap_int(&a[scan], &a[--gt]);
        else ++scan;
    }
    *lt_out = lt; *gt_out = gt; /* equal range is [lt, gt) */
}

/*
 * RandomizedSelect: selects zero-based relative rank from a[0..n).
 * The array is modified; the random pivot policy is internal to the algorithm.
 * Returns false for NULL pointers, n == 0, or rank >= n.
 * RNG policy belongs to the caller: seed rand() once (not per call).
 */
bool quickselect_int(int *a, size_t n, size_t rank, int *result) {
    if (a == NULL || result == NULL || n == 0 || rank >= n) return false;
    size_t lo = 0, hi = n;
    for (;;) {
        if (hi - lo == 1) { *result = a[lo]; return true; }
        size_t pivot_index = lo + (size_t)rand() % (hi - lo);
        int pivot = a[pivot_index];
        size_t lt, gt;
        partition3(a, lo, hi, pivot, &lt, &gt);
        if (rank < lt) hi = lt;
        else if (rank >= gt) lo = gt;
        else { *result = pivot; return true; }
    }
}

static int compare_int(const void *pa, const void *pb) {
    int a = *(const int *)pa, b = *(const int *)pb;
    return (a > b) - (a < b);
}

static void verify_all_ranks(const int *input, size_t n, unsigned seed) {
    int *oracle = malloc(n * sizeof *oracle);
    int *work = malloc(n * sizeof *work);
    assert(oracle != NULL && work != NULL);
    memcpy(oracle, input, n * sizeof *oracle);
    qsort(oracle, n, sizeof *oracle, compare_int);
    for (size_t rank = 0; rank < n; ++rank) {
        memcpy(work, input, n * sizeof *work);
        srand(seed + (unsigned)rank);
        int result = 0;
        assert(quickselect_int(work, n, rank, &result));
        assert(result == oracle[rank]);
    }
    free(work);
    free(oracle);
}

int main(void) {
    const int one[] = {7};
    const int two[] = {2, 1};
    const int ascending[] = {1, 2, 3, 4, 5, 6};
    const int descending[] = {6, 5, 4, 3, 2, 1};
    const int equal[] = {4, 4, 4, 4, 4, 4};
    const int duplicates[] = {4, 2, 4, 1, 4, 3, 2, 4, 1};
    const int not_five[] = {31, 8, 48, 73, 11, 3, 20};
    const int extremes[] = {INT_MAX, 0, INT_MIN, -1, 1, INT_MAX};
    const struct { const int *a; size_t n; } cases[] = {
        {one, 1}, {two, 2}, {ascending, 6}, {descending, 6},
        {equal, 6}, {duplicates, 9}, {not_five, 7}, {extremes, 6}
    };
    for (unsigned seed = 1; seed <= 32; ++seed) {
        for (size_t c = 0; c < sizeof cases / sizeof cases[0]; ++c) {
            verify_all_ranks(cases[c].a, cases[c].n, seed * 101U);
        }
    }

    int out;
    int valid[] = {1, 2};
    assert(!quickselect_int(valid, 2, 2, &out));
    assert(!quickselect_int(valid, 0, 0, &out));
    assert(!quickselect_int(valid, 2, 0, NULL));
    assert(!quickselect_int(NULL, 0, 0, &out));
    puts("RandomizedSelect C tests passed");
    return 0;
}
