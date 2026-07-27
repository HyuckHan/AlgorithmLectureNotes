import java.util.List;

public final class BinaryTreeDemo {
    private BinaryTreeDemo() {}
    public static void main(String[] args) {
        BinaryTree<String> b = new BinaryTree<>("B",
            new BinaryTree<>("D"), new BinaryTree<>("E"));
        BinaryTree<String> c = new BinaryTree<>("C",
            new BinaryTree<>("F"), new BinaryTree<>("G"));
        BinaryTree<String> t = new BinaryTree<>("A", b, c);
        assert t.size() == 7 && t.height() == 2;
        assert t.preorder().equals(List.of("A","B","D","E","C","F","G"));
        assert t.inorder().equals(List.of("D","B","E","A","F","C","G"));
        assert t.postorder().equals(List.of("D","E","B","F","G","C","A"));
        assert t.levelOrder().equals(List.of("A","B","C","D","E","F","G"));
        assert t.leftSubtree().size() == 3 && t.rightSubtree().size() == 3;
        assert new BinaryTree<Integer>().height() == -1;
        try { new BinaryTree<String>(null); assert false; }
        catch (NullPointerException expected) {}
    }
}
