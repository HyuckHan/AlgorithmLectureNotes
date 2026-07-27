public final class HashTableDemo {
    private record StudentKey(int id, String campus) {
        @Override public int hashCode() { return id % 3; } // deliberate collisions
    }

    public static void main(String[] args) {
        ChainedHashMap<StudentKey,String> chained = new ChainedHashMap<>(4);
        OpenAddressHashMap<StudentKey,String> open = new OpenAddressHashMap<>(8);
        StudentKey a = new StudentKey(25, "A");
        StudentKey b = new StudentKey(13, "B");
        StudentKey c = new StudentKey(16, "C");
        chained.put(a, "Ada"); chained.put(b, "Babbage"); chained.put(c, "Curie");
        open.put(a, "Ada"); open.put(b, "Babbage"); open.put(c, "Curie");
        assert chained.get(new StudentKey(25, "A")).equals("Ada");
        assert open.get(new StudentKey(25, "A")).equals("Ada");
        assert chained.put(a, "Augusta").equals("Ada");
        assert open.put(a, "Augusta").equals("Ada");
        assert chained.remove(b).equals("Babbage");
        assert open.remove(b).equals("Babbage");
        assert chained.validate() && open.validate();
    }
}
