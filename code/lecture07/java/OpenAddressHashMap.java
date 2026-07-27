import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public final class OpenAddressHashMap<K,V> {
    private static final byte EMPTY = 0, OCCUPIED = 1, DELETED = 2;
    private static final int MIN_CAPACITY = 8;
    private static final double MAX_PROBING_LOAD = 0.65;

    private Object[] keys, values;
    private byte[] states;
    private int size, tombstones;

    public OpenAddressHashMap() { this(MIN_CAPACITY); }

    public OpenAddressHashMap(int requestedCapacity) {
        int capacity = MIN_CAPACITY;
        while (capacity < requestedCapacity) capacity <<= 1;
        keys = new Object[capacity];
        values = new Object[capacity];
        states = new byte[capacity];
    }

    private int home(Object key) {
        int h = key.hashCode();
        h ^= h >>> 16;
        return h & (keys.length - 1);
    }

    @SuppressWarnings("unchecked")
    public V get(K key) {
        int j = findIndex(Objects.requireNonNull(key, "key"));
        return j < 0 ? null : (V) values[j];
    }

    public boolean containsKey(K key) { return findIndex(Objects.requireNonNull(key, "key")) >= 0; }

    private int findIndex(Object key) {
        int start = home(key);
        for (int i = 0; i < keys.length; i++) {
            int j = (start + i) & (keys.length - 1);
            if (states[j] == EMPTY) return -1;
            if (states[j] == OCCUPIED && keys[j].equals(key)) return j;
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public V put(K key, V value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        if ((size + tombstones + 1.0) / keys.length > MAX_PROBING_LOAD)
            resize(keys.length << 1);

        int start = home(key), firstDeleted = -1;
        for (int i = 0; i < keys.length; i++) {
            int j = (start + i) & (keys.length - 1);
            if (states[j] == OCCUPIED && keys[j].equals(key)) {
                V previous = (V) values[j];
                values[j] = value;
                return previous;
            }
            if (states[j] == DELETED && firstDeleted < 0) firstDeleted = j;
            if (states[j] == EMPTY) {
                place(firstDeleted >= 0 ? firstDeleted : j, key, value);
                assert validate();
                return null;
            }
        }
        if (firstDeleted >= 0) {
            place(firstDeleted, key, value);
            assert validate();
            return null;
        }
        resize(keys.length << 1);
        return put(key, value);
    }

    private void place(int j, K key, V value) {
        if (states[j] == DELETED) tombstones--;
        keys[j] = key;
        values[j] = value;
        states[j] = OCCUPIED;
        size++;
    }

    @SuppressWarnings("unchecked")
    public V remove(K key) {
        int j = findIndex(Objects.requireNonNull(key, "key"));
        if (j < 0) return null;
        V previous = (V) values[j];
        keys[j] = values[j] = null;
        states[j] = DELETED;
        size--;
        tombstones++;
        if (tombstones > size && keys.length > MIN_CAPACITY) resize(keys.length);
        assert validate();
        return previous;
    }

    private void resize(int newCapacity) {
        Object[] oldKeys = keys, oldValues = values;
        byte[] oldStates = states;
        keys = new Object[newCapacity];
        values = new Object[newCapacity];
        states = new byte[newCapacity];
        int oldSize = size;
        size = tombstones = 0;
        for (int i = 0; i < oldKeys.length; i++)
            if (oldStates[i] == OCCUPIED) reinsert(oldKeys[i], oldValues[i]);
        if (size != oldSize) throw new AssertionError("rehash lost entries");
    }

    private void reinsert(Object key, Object value) {
        int start = home(key);
        for (int i = 0; i < keys.length; i++) {
            int j = (start + i) & (keys.length - 1);
            if (states[j] == EMPTY) {
                keys[j] = key;
                values[j] = value;
                states[j] = OCCUPIED;
                size++;
                return;
            }
        }
        throw new AssertionError("new table has no slot");
    }

    public void clear() {
        keys = new Object[MIN_CAPACITY];
        values = new Object[MIN_CAPACITY];
        states = new byte[MIN_CAPACITY];
        size = tombstones = 0;
    }

    public int size() { return size; }
    public int capacity() { return keys.length; }
    public int tombstones() { return tombstones; }
    public double loadFactor() { return (double) size / keys.length; }
    public double probingLoad() { return (double) (size + tombstones) / keys.length; }

    public boolean validate() {
        int active = 0, deleted = 0;
        HashMap<Object,Boolean> seen = new HashMap<>();
        for (int j = 0; j < keys.length; j++) {
            if (states[j] == OCCUPIED) {
                active++;
                if (keys[j] == null || values[j] == null || seen.put(keys[j], Boolean.TRUE) != null) return false;
                int start = home(keys[j]);
                boolean reachable = false;
                for (int i = 0; i < keys.length; i++) {
                    int p = (start + i) & (keys.length - 1);
                    if (states[p] == EMPTY) break;
                    if (p == j) { reachable = true; break; }
                }
                if (!reachable) return false;
            } else if (states[j] == DELETED) {
                deleted++;
                if (keys[j] != null || values[j] != null) return false;
            } else if (states[j] != EMPTY || keys[j] != null || values[j] != null) return false;
        }
        return active == size && deleted == tombstones && probingLoad() <= 1.0;
    }

    private record BadHashKey(int id) {
        @Override public int hashCode() { return 1; }
    }

    public static void main(String[] args) {
        OpenAddressHashMap<BadHashKey,Integer> collisions = new OpenAddressHashMap<>(8);
        for (int i = 0; i < 50; i++) collisions.put(new BadHashKey(i), i);
        assert collisions.validate() && collisions.size() == 50;
        for (int i = 10; i < 30; i++) assert collisions.remove(new BadHashKey(i)) == i;
        for (int i = 50; i < 70; i++) collisions.put(new BadHashKey(i), i);
        assert collisions.validate() && collisions.get(new BadHashKey(40)) == 40;
        assert collisions.put(new BadHashKey(40), 4000) == 40;

        OpenAddressHashMap<Integer,Integer> actual = new OpenAddressHashMap<>(8);
        Map<Integer,Integer> expected = new HashMap<>();
        Random random = new Random(17);
        for (int step = 0; step < 25_000; step++) {
            int key = random.nextInt(601) - 300;
            int value = random.nextInt();
            switch (random.nextInt(3)) {
                case 0 -> { assert Objects.equals(actual.put(key, value), expected.put(key, value)); }
                case 1 -> { assert Objects.equals(actual.remove(key), expected.remove(key)); }
                default -> { assert Objects.equals(actual.get(key), expected.get(key)); }
            }
            assert actual.size() == expected.size() && actual.validate();
            for (Map.Entry<Integer,Integer> e : expected.entrySet())
                assert Objects.equals(actual.get(e.getKey()), e.getValue());
        }
        actual.clear();
        assert actual.size() == 0 && actual.tombstones() == 0 && actual.validate();
    }
}
