import java.util.HashMap;
import java.util.Map;

public final class MutableKeyExample {
    static final class StudentKey {
        int id;

        StudentKey(int id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof StudentKey other
                    && id == other.id;
        }
    }

    public static void main(String[] args) {
        Map<StudentKey,String> map = new HashMap<>();
        StudentKey key = new StudentKey(10);
        map.put(key, "Ada");

        key.id = 20; // dangerous: hash/equality-relevant mutation

        assert map.size() == 1;
        assert map.get(key) == null;
        assert map.get(new StudentKey(10)) == null;
        assert map.get(new StudentKey(20)) == null;
    }
}
