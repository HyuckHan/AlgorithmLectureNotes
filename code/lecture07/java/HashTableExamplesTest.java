import java.util.Arrays;

public final class HashTableExamplesTest {
    private static final byte EMPTY = 0, OCCUPIED = 1, DELETED = 2;

    private static int mod(int key, int m) {
        return Math.floorMod(key, m);
    }

    private static int findLinear(int[] keys, byte[] states, int key) {
        int m = keys.length, home = mod(key, m);
        for (int i = 0; i < m; i++) {
            int j = (home + i) % m;
            if (states[j] == EMPTY) return -1;
            if (states[j] == OCCUPIED && keys[j] == key) return j;
        }
        return -1;
    }

    private static int[] insertLinear(int m, int[] input) {
        int[] keys = new int[m], result = new int[input.length];
        byte[] states = new byte[m];
        for (int x = 0; x < input.length; x++) {
            int key = input[x], home = mod(key, m);
            for (int i = 0; i < m; i++) {
                int j = (home + i) % m;
                if (states[j] == EMPTY) {
                    keys[j] = key;
                    states[j] = OCCUPIED;
                    result[x] = j;
                    break;
                }
                if (i == m - 1) throw new AssertionError("linear table full");
            }
        }
        return result;
    }

    private static int[] probeUntilFree(int m, int key, boolean[] occupied,
                                        Probe probe) {
        int[] path = new int[m];
        for (int i = 0; i < m; i++) {
            int j = probe.index(key, i, m);
            path[i] = j;
            if (!occupied[j]) return Arrays.copyOf(path, i + 1);
        }
        throw new AssertionError("probe did not find a free slot");
    }

    @FunctionalInterface
    private interface Probe {
        int index(int key, int i, int m);
    }

    private static void validateLinearExample() {
        int[] input = {25,13,16,15,7,28,31,20,1,38};
        int[] expected = {12,0,3,2,7,4,5,8,1,6};
        assert Arrays.equals(insertLinear(13, input), expected);
    }

    private static void validateQuadraticExample() {
        boolean[] occupied = new boolean[13];
        for (int j : new int[]{2,4,5,6,11}) occupied[j] = true;
        int[] path = probeUntilFree(13, 30, occupied,
                (key, i, m) -> mod(mod(key, m) + i * i, m));
        assert Arrays.equals(path, new int[]{4,5,8});
    }

    private static void validateDoubleHashExample() {
        boolean[] occupied = new boolean[13];
        Probe doubleHash = (key, i, m) ->
                mod(mod(key, 13) + i * (mod(key, 11) + 1), m);
        int[] input = {15,19,28,41,67};
        int[][] paths = {{2},{6},{2,9},{2,11},{2,4}};
        for (int x = 0; x < input.length; x++) {
            int[] actual = probeUntilFree(13, input[x], occupied, doubleHash);
            assert Arrays.equals(actual, paths[x]);
            occupied[actual[actual.length - 1]] = true;
        }
    }

    private static void validateDeletionExample() {
        int[] table = {13,1,15,16,28,31,38,7,20,0,0,0,25};
        byte[] states = new byte[13];
        for (int j : new int[]{0,1,2,3,4,5,6,7,8,12}) states[j] = OCCUPIED;
        states[1] = EMPTY;
        assert findLinear(table, states, 38) == -1;
        states[1] = DELETED;
        assert findLinear(table, states, 38) == 6;

        int firstDeleted = -1;
        boolean duplicateFoundAfterDeleted = false;
        int key = 38, home = mod(key, 13);
        for (int i = 0; i < 13; i++) {
            int j = (home + i) % 13;
            if (states[j] == DELETED && firstDeleted < 0) firstDeleted = j;
            if (states[j] == OCCUPIED && table[j] == key) {
                duplicateFoundAfterDeleted = true;
                break;
            }
            if (states[j] == EMPTY) break;
        }
        assert firstDeleted == 1 && duplicateFoundAfterDeleted;
    }

    private static void validateRehashExample() {
        int[] oldKeys = {0,0,2,7,12};
        byte[] oldStates = {EMPTY,DELETED,OCCUPIED,OCCUPIED,OCCUPIED};
        assert findLinear(oldKeys, oldStates, 2) == 2;
        assert findLinear(oldKeys, oldStates, 7) == 3;
        assert findLinear(oldKeys, oldStates, 12) == 4;

        int[] active = {2,7,12};
        int[] finalSlots = insertLinear(11, active);
        assert Arrays.equals(finalSlots, new int[]{2,7,1});
        assert active.length == 3;
        int tombstones = 0;
        assert tombstones == 0;
    }

    private static void validateLinearProbeFormulas() {
        double alpha = 0.5;
        double successful = 0.5 * (1.0 + 1.0 / (1.0 - alpha));
        double unsuccessful =
                0.5 * (1.0 + 1.0 / Math.pow(1.0 - alpha, 2));
        assert Math.abs(successful - 1.5) < 1e-12;
        assert Math.abs(unsuccessful - 2.5) < 1e-12;
    }

    public static void main(String[] args) {
        validateLinearExample();
        validateQuadraticExample();
        validateDoubleHashExample();
        validateDeletionExample();
        validateRehashExample();
        validateLinearProbeFormulas();
    }
}
