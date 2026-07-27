import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public final class Quickselect {
    private Quickselect() {}

    /**
     * RandomizedSelect for a zero-based relative rank in a[0..a.length).
     * Modifies the array; the caller supplies the RNG policy.
     */
    public static int select(int[] a, int rank, Random random) {
        Objects.requireNonNull(a, "array");
        Objects.requireNonNull(random, "random");
        if (rank < 0 || rank >= a.length) {
            throw new IllegalArgumentException("rank outside [0, n)");
        }
        int lo = 0, hi = a.length;
        while (true) {
            if (hi - lo == 1) return a[lo];
            int pivot = a[lo + random.nextInt(hi - lo)];
            int[] equal = partition3(a, lo, hi, pivot);
            if (rank < equal[0]) hi = equal[0];
            else if (rank >= equal[1]) lo = equal[1];
            else return pivot;
        }
    }

    /** Returns [lt, gt), the interval equal to pivot. */
    private static int[] partition3(int[] a, int lo, int hi, int pivot) {
        int lt = lo, scan = lo, gt = hi;
        while (scan < gt) {
            if (a[scan] < pivot) swap(a, lt++, scan++);
            else if (a[scan] > pivot) swap(a, scan, --gt);
            else scan++;
        }
        return new int[] {lt, gt};
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void verifyAllRanks(int[] input, long seed) {
        int[] oracle = input.clone();
        Arrays.sort(oracle);
        for (int rank = 0; rank < input.length; rank++) {
            int actual = select(input.clone(), rank, new Random(seed + rank));
            require(actual == oracle[rank],
                    "rank " + rank + ": " + actual + " != " + oracle[rank]);
        }
    }

    private static void expectIllegalRank(int[] input, int rank) {
        try {
            select(input, rank, new Random(1));
            throw new AssertionError("invalid rank accepted: " + rank);
        } catch (IllegalArgumentException expected) {
            require("rank outside [0, n)".equals(expected.getMessage()),
                    "unexpected invalid-rank message");
        }
    }

    public static void main(String[] args) {
        int[][] cases = {
            {7},
            {2, 1},
            {1, 2, 3, 4, 5, 6},
            {6, 5, 4, 3, 2, 1},
            {4, 4, 4, 4, 4, 4},
            {4, 2, 4, 1, 4, 3, 2, 4, 1},
            {31, 8, 48, 73, 11, 3, 20},
            {Integer.MAX_VALUE, 0, Integer.MIN_VALUE, -1, 1, Integer.MAX_VALUE}
        };
        for (long seed = 1; seed <= 32; seed++) {
            for (int[] input : cases) verifyAllRanks(input, seed * 101);
        }

        expectIllegalRank(new int[] {1, 2}, -1);
        expectIllegalRank(new int[] {1, 2}, 2);
        expectIllegalRank(new int[0], 0);
        try {
            select(null, 0, new Random(1));
            throw new AssertionError("null array accepted");
        } catch (NullPointerException expected) {
            require("array".equals(expected.getMessage()), "unexpected null-array message");
        }
        try {
            select(new int[] {1}, 0, null);
            throw new AssertionError("null RNG accepted");
        } catch (NullPointerException expected) {
            require("random".equals(expected.getMessage()), "unexpected null-RNG message");
        }
        System.out.println("RandomizedSelect Java tests passed");
    }
}
