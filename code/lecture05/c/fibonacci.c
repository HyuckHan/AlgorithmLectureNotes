#include <assert.h>
#include <limits.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdlib.h>

/* F(0)=0, F(1)=1. Valid through n=92 for signed long long.
 * Memoized/bottom-up: Theta(n) time, Theta(n) table.
 * Rolling: Theta(n) time, Theta(1) auxiliary space. */
static bool fib_rolling(int n, long long *out) {
    if (!out || n < 0 || n > 92) return false;
    if (n <= 1) { *out = n; return true; }
    long long prev2 = 0, prev1 = 1;
    for (int i = 2; i <= n; ++i) {
        long long current = prev2 + prev1;
        prev2 = prev1; prev1 = current;
    }
    *out = prev1; return true;
}

static long long memo_rec(int n, long long *memo) {
    if (memo[n] != LLONG_MIN) return memo[n];
    memo[n] = memo_rec(n - 1, memo) + memo_rec(n - 2, memo);
    return memo[n];
}

static bool fib_memo(int n, long long *out) {
    if (!out || n < 0 || n > 92) return false;
    long long *memo = calloc((size_t)n + 1, sizeof(*memo));
    if (!memo) return false;
    for (int i = 0; i <= n; ++i) memo[i] = LLONG_MIN;
    memo[0] = 0;
    if (n >= 1) memo[1] = 1;
    *out = memo_rec(n, memo);
    free(memo); return true;
}

int main(void) {
    long long x = -1;
    assert(fib_rolling(0, &x) && x == 0);
    assert(fib_rolling(1, &x) && x == 1);
    assert(fib_rolling(10, &x) && x == 55);
    assert(fib_memo(0, &x) && x == 0);
    assert(fib_memo(1, &x) && x == 1);
    assert(fib_memo(50, &x) && x == 12586269025LL);
    assert(!fib_rolling(-1, &x));
    assert(!fib_rolling(93, &x));
    assert(!fib_memo(3, NULL));
}
