import java.util.*;

public final class StringMatchingDemo {
    public static void main(String[] args) {
        String text = "acebbceeaabceedb";
        String pattern = "eeaab";
        System.out.println("Naive      " + StringMatchers.naiveAll(text, pattern));
        System.out.println("Rabin-Karp " + StringMatchers.rabinKarpAll(text, pattern, 5, 113));
        System.out.println("KMP        " + StringMatchers.kmpAll(text, pattern));
        System.out.println("Horspool   " + StringMatchers.horspoolAll(text, pattern));
        System.out.println("LPS(BAABABAA) = " +
            Arrays.toString(StringMatchers.buildLps("BAABABAA")));
    }
    private StringMatchingDemo() {}
}
