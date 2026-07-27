import java.util.*;

public final class StringMatchingTest {
    private static final long BASE = 257, MOD = 1_000_000_007L;

    public static void main(String[] args) {
        basicCases();
        slideValues();
        collisionAndModulo();
        horspoolProgressRegression();
        unicodeCodeUnitPolicy();
        randomizedDifferential();
        System.out.println("Lecture 9 Java tests passed: 6000 randomized cases");
    }

    private static void basicCases() {
        String[][] cases = {
            {"",""}, {"","A"}, {"A",""}, {"ABC","ABCD"}, {"ABC","ABC"},
            {"ABC","Z"}, {"ABCABC","ABC"}, {"AAAAA","AAA"}, {"XABC","X"},
            {"ABCX","X"}, {"BBBBB","B"}, {"ABCDABD","ABD"}
        };
        for (String[] c : cases) agree(c[0], c[1], BASE, MOD);
        check(StringMatchers.naiveAll("AAAAA","AAA"), List.of(0,1,2));
        if (StringMatchers.kmpFirst("ABC","Z") != -1 ||
                StringMatchers.naiveFirst("ABC","") != 0 ||
                StringMatchers.rabinKarpFirst("ABCX","X",BASE,MOD) != 3 ||
                StringMatchers.horspoolFirst("XABC","X") != 0)
            throw new AssertionError("first-match wrapper policy");
    }

    private static void slideValues() {
        if (baseFive("cad") != 53) throw new AssertionError("cad must be 53");
        check(StringMatchers.buildLps("BAABABAA"), new int[]{0,0,0,1,2,1,2,3});
        check(StringMatchers.buildLps("AAAAA"), new int[]{0,1,2,3,4});
        check(StringMatchers.buildLps("ABCDABD"), new int[]{0,0,0,0,1,2,0});
        check(StringMatchers.buildLps("AAAAABAAAAAC"),
                new int[]{0,1,2,3,4,0,1,2,3,4,5,0});

        int[] tiger = StringMatchers.buildHorspoolShift("TIGER");
        if (tiger['T']!=4 || tiger['I']!=3 || tiger['G']!=2 ||
                tiger['E']!=1 || tiger['R']!=5 || tiger['X']!=5)
            throw new AssertionError("TIGER table");
        int[] rational = StringMatchers.buildHorspoolShift("RATIONAL");
        if (rational['R']!=7 || rational['A']!=1 || rational['T']!=5 ||
                rational['I']!=4 || rational['O']!=3 || rational['N']!=2 ||
                rational['L']!=8 || rational['X']!=8)
            throw new AssertionError("RATIONAL table");

        String t = "acebbceeaabceedb", p = "eeaab";
        long[] hashes = baseFiveHashes(t, p.length(), 113);
        check(hashes, new long[]{17,87,65,33,91,42,63,21,39,86,94,58});
        if (baseFive(p) != 3001 || baseFive(p) % 113 != 63)
            throw new AssertionError("Rabin-Karp slide values");
        check(StringMatchers.rabinKarpAll(t,p,5,113), List.of(6));
    }

    private static void collisionAndModulo() {
        String pattern = "ab";
        long q = 3, base = 2;
        String collision = null;
        long target = simpleHash(pattern, base, q);
        outer: for (char a='a'; a<='d'; a++) for (char b='a'; b<='d'; b++) {
            String x = "" + a + b;
            if (!x.equals(pattern) && simpleHash(x,base,q)==target) { collision=x; break outer; }
        }
        if (collision == null) throw new AssertionError("collision not found");
        check(StringMatchers.rabinKarpAll(collision, pattern, base, q), List.of());
        agree("zzzzzzzzabzzzz", pattern, 2, 3);
        agree("baaaaaab", "aaab", 5, 7); // rolling subtraction needs normalization
        agree("A".repeat(20_000) + "B", "A".repeat(200) + "B", BASE, MOD);
    }

    private static void horspoolProgressRegression() {
        @SuppressWarnings("unchecked")
        List<Integer>[] result = (List<Integer>[]) new List<?>[1];
        Throwable[] failure = new Throwable[1];
        Thread worker = new Thread(() -> {
            try {
                result[0] = StringMatchers.horspoolAll("A".repeat(200_000), "B");
            } catch (Throwable t) {
                failure[0] = t;
            }
        }, "horspool-progress-regression");
        worker.setDaemon(true);
        worker.start();
        try {
            worker.join(2_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        }
        if (worker.isAlive())
            throw new AssertionError("Horspool did not advance after a mismatch");
        if (failure[0] != null) throw new AssertionError(failure[0]);
        check(result[0], List.of());
    }

    private static void unicodeCodeUnitPolicy() {
        String text = "\uD83D\uDE00A\uD83D\uDE00"; // 😀A😀: UTF-16 offsets 0 and 3
        String pattern = "\uD83D\uDE00";
        agree(text, pattern, BASE, MOD);
        check(StringMatchers.naiveAll(text, pattern), List.of(0, 3));
    }

    private static void randomizedDifferential() {
        Random rnd = new Random(0x5EED09L);
        for (int test = 0; test < 6000; test++) {
            int n = rnd.nextInt(31), m = rnd.nextInt(12);
            String text = randomString(rnd,n), pattern = randomString(rnd,m);
            agree(text, pattern, test % 2 == 0 ? 257 : 5, test % 2 == 0 ? MOD : 113);
        }
    }

    private static void agree(String text, String pattern, long base, long q) {
        List<Integer> oracle = StringMatchers.naiveAll(text, pattern);
        check(StringMatchers.rabinKarpAll(text, pattern, base, q), oracle);
        check(StringMatchers.kmpAll(text, pattern), oracle);
        check(StringMatchers.horspoolAll(text, pattern), oracle);
        int first = oracle.isEmpty() ? StringMatchers.NOT_FOUND : oracle.get(0);
        if (StringMatchers.naiveFirst(text, pattern) != first ||
                StringMatchers.rabinKarpFirst(text, pattern, base, q) != first ||
                StringMatchers.kmpFirst(text, pattern) != first ||
                StringMatchers.horspoolFirst(text, pattern) != first)
            throw new AssertionError("all-match/first-match wrapper disagreement");
    }
    private static String randomString(Random rnd, int n) {
        String alphabet = "ABCD";
        StringBuilder b = new StringBuilder(n);
        for (int i=0;i<n;i++) b.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
        return b.toString();
    }
    private static long baseFive(String s) {
        long value=0;
        for (int i=0;i<s.length();i++) value=5*value+(s.charAt(i)-'a');
        return value;
    }
    private static long[] baseFiveHashes(String t, int m, long q) {
        long[] out = new long[t.length()-m+1];
        for (int s=0;s<out.length;s++) out[s]=baseFive(t.substring(s,s+m))%q;
        return out;
    }
    private static long simpleHash(String s, long base, long q) {
        long h=0;
        for(int i=0;i<s.length();i++) h=(base*h+s.charAt(i)+1)%q;
        return h;
    }
    private static void check(List<Integer> actual, List<Integer> expected) {
        if (!actual.equals(expected)) throw new AssertionError(actual+" != "+expected);
    }
    private static void check(int[] actual, int[] expected) {
        if (!Arrays.equals(actual,expected)) throw new AssertionError(Arrays.toString(actual));
    }
    private static void check(long[] actual, long[] expected) {
        if (!Arrays.equals(actual,expected)) throw new AssertionError(Arrays.toString(actual));
    }
    private StringMatchingTest() {}
}
