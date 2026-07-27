import java.util.Arrays;

public final class StateSpaceSearchDemo {
    private StateSpaceSearchDemo() {}
    public static void main(String[] args) {
        System.out.println("5P4 = " + PermutationGenerator.generate(5, 4).size());
        System.out.println("4-Queens = " + new NQueensSolver().solve(4).solutions.size());
        int[] ap = new ArithmeticProgressionSearch().solve(new int[]{4,1,3,5,7}, true).sequence;
        System.out.println("AP = " + Arrays.toString(ap));
        KnapsackBranchAndBound.Item[] items = {
            new KnapsackBranchAndBound.Item("A",2,40),
            new KnapsackBranchAndBound.Item("B",5,30),
            new KnapsackBranchAndBound.Item("C",10,50),
            new KnapsackBranchAndBound.Item("D",5,10)
        };
        KnapsackBranchAndBound.Result k = new KnapsackBranchAndBound().solve(items,16);
        System.out.println("Knapsack = " + k.selected + ", profit " + k.profit);
    }
}
