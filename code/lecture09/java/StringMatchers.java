import java.util.*;

public final class StringMatchers {
    public static final int NOT_FOUND = -1;

    public static List<Integer> naiveAll(String text, String pattern) {
        require(text, pattern);
        int n = text.length(), m = pattern.length();
        if (m == 0) return List.of(0);
        List<Integer> out = new ArrayList<>();
        for (int s = 0; s + m <= n; s++) {
            int j = 0;
            while (j < m && text.charAt(s + j) == pattern.charAt(j)) j++;
            if (j == m) out.add(s);
        }
        return checked(text, pattern, out);
    }

    public static List<Integer> rabinKarpAll(String text, String pattern,
                                             long base, long modulus) {
        require(text, pattern);
        checkHashParameters(base, modulus);
        int n = text.length(), m = pattern.length();
        if (m == 0) return List.of(0);
        if (m > n) return List.of();
        long h = 1, pHash = 0, tHash = 0;
        for (int j = 1; j < m; j++) h = mulMod(h, base, modulus);
        for (int j = 0; j < m; j++) {
            pHash = addMod(mulMod(pHash, base, modulus), code(pattern.charAt(j)), modulus);
            tHash = addMod(mulMod(tHash, base, modulus), code(text.charAt(j)), modulus);
        }
        List<Integer> out = new ArrayList<>();
        for (int s = 0; s + m <= n; s++) {
            if (pHash == tHash && equalsAt(text, pattern, s)) out.add(s);
            if (s + m < n) {
                long leading = mulMod(code(text.charAt(s)), h, modulus);
                long remainder = subMod(tHash, leading, modulus);
                tHash = addMod(mulMod(base, remainder, modulus),
                               code(text.charAt(s + m)), modulus);
            }
        }
        return checked(text, pattern, out);
    }

    public static int[] buildLps(String pattern) {
        Objects.requireNonNull(pattern, "pattern");
        int m = pattern.length();
        int[] lps = new int[m];
        for (int i = 1, len = 0; i < m;) {
            if (pattern.charAt(i) == pattern.charAt(len)) lps[i++] = ++len;
            else if (len > 0) len = lps[len - 1];
            else lps[i++] = 0;
        }
        validateLps(pattern, lps);
        return lps;
    }

    public static List<Integer> kmpAll(String text, String pattern) {
        require(text, pattern);
        int n = text.length(), m = pattern.length();
        if (m == 0) return List.of(0);
        int[] lps = buildLps(pattern);
        List<Integer> out = new ArrayList<>();
        for (int i = 0, j = 0; i < n;) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
                if (j == m) {
                    out.add(i - m);
                    j = lps[j - 1]; // preserve overlapping occurrences
                }
            } else if (j > 0) j = lps[j - 1];
            else i++;
        }
        return checked(text, pattern, out);
    }

    public static int[] buildHorspoolShift(String pattern) {
        Objects.requireNonNull(pattern, "pattern");
        int m = pattern.length();
        int[] shift = new int[Character.MAX_VALUE + 1];
        Arrays.fill(shift, Math.max(1, m));
        for (int j = 0; j < m - 1; j++) shift[pattern.charAt(j)] = m - 1 - j;
        return shift;
    }

    public static List<Integer> horspoolAll(String text, String pattern) {
        require(text, pattern);
        int n = text.length(), m = pattern.length();
        if (m == 0) return List.of(0);
        if (m > n) return List.of();
        int[] shift = buildHorspoolShift(pattern);
        List<Integer> out = new ArrayList<>();
        for (int s = 0; s + m <= n;) {
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) j--;
            if (j < 0) out.add(s);
            int amount = shift[text.charAt(s + m - 1)];
            if (amount <= 0) throw new AssertionError("nonpositive shift");
            s += amount;
        }
        return checked(text, pattern, out);
    }

    public static int naiveFirst(String text, String pattern) {
        return first(naiveAll(text, pattern));
    }
    public static int rabinKarpFirst(String text, String pattern, long base, long modulus) {
        return first(rabinKarpAll(text, pattern, base, modulus));
    }
    public static int kmpFirst(String text, String pattern) {
        return first(kmpAll(text, pattern));
    }
    public static int horspoolFirst(String text, String pattern) {
        return first(horspoolAll(text, pattern));
    }

    private static int first(List<Integer> matches) {
        return matches.isEmpty() ? NOT_FOUND : matches.get(0);
    }
    private static void require(String text, String pattern) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(pattern, "pattern");
    }
    private static long code(char c) { return (long)c + 1; }
    private static void checkHashParameters(long base, long q) {
        if (base <= 0 || q <= 1) throw new IllegalArgumentException("base/modulus");
    }
    private static long addMod(long a, long b, long q) {
        a %= q; b %= q;
        return a >= q - b ? a - (q - b) : a + b;
    }
    private static long subMod(long a, long b, long q) {
        a %= q; b %= q;
        return a >= b ? a - b : q - (b - a);
    }
    private static long mulMod(long a, long b, long q) {
        a %= q; b %= q;
        long result = 0;
        while (b > 0) {
            if ((b & 1L) != 0) result = addMod(result, a, q);
            a = addMod(a, a, q);
            b >>>= 1;
        }
        return result;
    }
    private static boolean equalsAt(String text, String pattern, int s) {
        for (int j = 0; j < pattern.length(); j++)
            if (text.charAt(s + j) != pattern.charAt(j)) return false;
        return true;
    }
    private static List<Integer> checked(String text, String pattern, List<Integer> out) {
        int previous = -1;
        for (int s : out) {
            if (s <= previous || s < 0 || s + pattern.length() > text.length() ||
                    !equalsAt(text, pattern, s))
                throw new AssertionError("invalid match output");
            previous = s;
        }
        return List.copyOf(out);
    }
    private static void validateLps(String p, int[] lps) {
        for (int i = 0; i < lps.length; i++) {
            int best = 0;
            for (int len = 1; len <= i; len++)
                if (p.regionMatches(0, p, i + 1 - len, len)) best = len;
            if (lps[i] != best) throw new AssertionError("invalid LPS at " + i);
        }
    }
    private StringMatchers() {}
}
