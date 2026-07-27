import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public final class ChainedHashMap<K,V> {
    private static final double MAX_LOAD = 1.0;
    private static final int MIN_CAPACITY = 4;

    private static final class Entry<K,V> {
        final K key;
        V value;
        Entry<K,V> next;
        Entry(K key, V value, Entry<K,V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry<K,V>[] buckets;
    private int size;

    public ChainedHashMap() { this(MIN_CAPACITY); }

    public ChainedHashMap(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("capacity");
        buckets = newBucketArray(Math.max(MIN_CAPACITY, capacity));
    }

    @SuppressWarnings("unchecked")
    private static <K,V> Entry<K,V>[] newBucketArray(int capacity) {
        return (Entry<K,V>[]) new Entry<?,?>[capacity];
    }

    private int index(Object key, int capacity) {
        return Math.floorMod(key.hashCode(), capacity);
    }

    private Entry<K,V> find(Object key) {
        Objects.requireNonNull(key, "key");
        for (Entry<K,V> e = buckets[index(key, buckets.length)]; e != null; e = e.next)
            if (e.key.equals(key)) return e;
        return null;
    }

    public V get(K key) {
        Entry<K,V> e = find(key);
        return e == null ? null : e.value;
    }

    public boolean containsKey(K key) { return find(key) != null; }

    public V put(K key, V value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        Entry<K,V> old = find(key);
        if (old != null) {
            V previous = old.value;
            old.value = value;
            return previous;
        }
        if ((size + 1.0) / buckets.length > MAX_LOAD) resize(buckets.length * 2);
        int j = index(key, buckets.length);
        buckets[j] = new Entry<>(key, value, buckets[j]);
        size++;
        assert validate();
        return null;
    }

    public V remove(K key) {
        Objects.requireNonNull(key, "key");
        int j = index(key, buckets.length);
        Entry<K,V> previous = null;
        for (Entry<K,V> e = buckets[j]; e != null; e = e.next) {
            if (e.key.equals(key)) {
                if (previous == null) buckets[j] = e.next;
                else previous.next = e.next;
                size--;
                assert validate();
                return e.value;
            }
            previous = e;
        }
        return null;
    }

    private void resize(int newCapacity) {
        Entry<K,V>[] old = buckets;
        buckets = newBucketArray(newCapacity);
        int oldSize = size;
        size = 0;
        for (Entry<K,V> head : old)
            for (Entry<K,V> e = head; e != null; e = e.next) {
                int j = index(e.key, newCapacity);
                buckets[j] = new Entry<>(e.key, e.value, buckets[j]);
                size++;
            }
        if (size != oldSize) throw new AssertionError("rehash lost entries");
    }

    public void clear() {
        buckets = newBucketArray(MIN_CAPACITY);
        size = 0;
    }

    public int size() { return size; }
    public int capacity() { return buckets.length; }
    public double loadFactor() { return (double) size / buckets.length; }

    public boolean validate() {
        int counted = 0;
        HashMap<K,Boolean> seen = new HashMap<>();
        for (int j = 0; j < buckets.length; j++) {
            Entry<K,V> slow = buckets[j], fast = buckets[j];
            while (fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
                if (slow == fast) return false;
            }
            for (Entry<K,V> e = buckets[j]; e != null; e = e.next) {
                if (e.key == null || e.value == null || index(e.key, buckets.length) != j) return false;
                if (seen.put(e.key, Boolean.TRUE) != null) return false;
                counted++;
            }
        }
        return counted == size && loadFactor() <= MAX_LOAD;
    }

    private record BadHashKey(int id) {
        @Override public int hashCode() { return 7; }
    }

    public static void main(String[] args) {
        ChainedHashMap<BadHashKey,Integer> collisions = new ChainedHashMap<>(4);
        assert collisions.size() == 0 && collisions.get(new BadHashKey(1)) == null;
        for (int i = 0; i < 40; i++) assert collisions.put(new BadHashKey(i), i) == null;
        assert collisions.validate() && collisions.size() == 40;
        assert collisions.put(new BadHashKey(10), 999) == 10;
        assert collisions.get(new BadHashKey(10)) == 999;
        assert collisions.remove(new BadHashKey(20)) == 20 && !collisions.containsKey(new BadHashKey(20));

        ChainedHashMap<Integer,Integer> actual = new ChainedHashMap<>(4);
        Map<Integer,Integer> expected = new HashMap<>();
        Random random = new Random(7);
        for (int step = 0; step < 20_000; step++) {
            int key = random.nextInt(401) - 200;
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
        int grown = actual.capacity();
        actual.clear();
        assert actual.size() == 0 && actual.capacity() <= grown && actual.validate();
    }
}
