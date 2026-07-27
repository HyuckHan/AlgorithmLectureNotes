import java.util.Arrays;

public final class FibonacciDP {
    private FibonacciDP() {}
    // F(0)=0, F(1)=1. Theta(n) time, Theta(1) auxiliary space.
    public static long fib(int n) {
        if (n < 0 || n > 92) throw new IllegalArgumentException("n must be 0..92");
        if (n <= 1) return n;
        long prev2=0,prev1=1;
        for(int i=2;i<=n;i++){long cur=Math.addExact(prev2,prev1);prev2=prev1;prev1=cur;}
        return prev1;
    }
    private static long memoRec(int n, long[] memo) {
        if (memo[n] != Long.MIN_VALUE) return memo[n];
        memo[n] = Math.addExact(memoRec(n - 1, memo), memoRec(n - 2, memo));
        return memo[n];
    }
    // Base states are initialized before the first call.
    public static long fibMemo(int n) {
        if (n < 0 || n > 92) throw new IllegalArgumentException("n must be 0..92");
        long[] memo = new long[n + 1];
        Arrays.fill(memo, Long.MIN_VALUE);
        memo[0] = 0;
        if (n >= 1) memo[1] = 1;
        return memoRec(n, memo);
    }
    public static void main(String[] args) {
        assert fib(0)==0 && fib(1)==1 && fib(10)==55 && fib(50)==12586269025L;
        assert fibMemo(0)==0 && fibMemo(1)==1 && fibMemo(10)==55
            && fibMemo(50)==12586269025L;
        try { fib(-1); assert false; } catch (IllegalArgumentException expected) {}
        try { fibMemo(-1); assert false; } catch (IllegalArgumentException expected) {}
        try { fib(93); assert false; } catch (IllegalArgumentException expected) {}
    }
}
