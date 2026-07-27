#include <assert.h>
#include <limits.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static void swap_int(int *a, int *b) { int t = *a; *a = *b; *b = t; }

static void insertion_sort(int *a, size_t lo, size_t hi) {
    for (size_t i = lo + 1; i < hi; ++i) {
        int key = a[i]; size_t j = i;
        while (j > lo && a[j - 1] > key) { a[j] = a[j - 1]; --j; }
        a[j] = key;
    }
}

static void partition3(int *a, size_t lo, size_t hi, int pivot,
                       size_t *lt_out, size_t *gt_out) {
    size_t lt = lo, scan = lo, gt = hi;
    while (scan < gt) {
        if (a[scan] < pivot) swap_int(&a[lt++], &a[scan++]);
        else if (a[scan] > pivot) swap_int(&a[scan], &a[--gt]);
        else ++scan;
    }
    *lt_out = lt; *gt_out = gt;
}

static int select_range(int *a, size_t lo, size_t hi, size_t target) {
    for (;;) {
        size_t n = hi - lo;
        if (n <= 5) { insertion_sort(a, lo, hi); return a[target]; }
        size_t groups = 0;
        for (size_t start = lo; start < hi; start += 5) {
            size_t end = start + 5 < hi ? start + 5 : hi;
            insertion_sort(a, start, end);
            size_t median = start + (end - start - 1) / 2; /* lower median */
            swap_int(&a[lo + groups], &a[median]);
            ++groups;
        }
        /* Lower median: ceil(groups/2)-th in 1-based rank. */
        int pivot = select_range(a, lo, lo + groups,
                                 lo + (groups - 1) / 2);
        size_t lt, gt;
        partition3(a, lo, hi, pivot, &lt, &gt);
        if (target < lt) hi = lt;
        else if (target >= gt) lo = gt;
        else return pivot;
    }
}

/* Zero-based rank; modifies a; false means invalid arguments. */
bool deterministic_select_int(int *a, size_t n, size_t rank, int *result) {
    if (a == NULL || result == NULL || n == 0 || rank >= n) return false;
    *result = select_range(a, 0, n, rank);
    return true;
}

static int compare_int(const void *pa, const void *pb) {
    int a = *(const int *)pa, b = *(const int *)pb;
    return (a > b) - (a < b);
}

static void verify_all_ranks(const int *input, size_t n) {
    int *oracle = malloc(n * sizeof *oracle);
    int *work = malloc(n * sizeof *work);
    assert(oracle != NULL && work != NULL);
    memcpy(oracle, input, n * sizeof *oracle);
    qsort(oracle, n, sizeof *oracle, compare_int);
    for (size_t rank = 0; rank < n; ++rank) {
        memcpy(work, input, n * sizeof *work);
        int result = 0;
        assert(deterministic_select_int(work, n, rank, &result));
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
    for (size_t c = 0; c < sizeof cases / sizeof cases[0]; ++c) {
        verify_all_ranks(cases[c].a, cases[c].n);
    }

    int out;
    int valid[] = {1, 2};
    assert(!deterministic_select_int(valid, 2, 2, &out));
    assert(!deterministic_select_int(valid, 0, 0, &out));
    assert(!deterministic_select_int(valid, 2, 0, NULL));
    assert(!deterministic_select_int(NULL, 0, 0, &out));
    puts("DeterministicSelect C tests passed");
    return 0;
}
