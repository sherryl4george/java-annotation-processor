package implementation;

/***
 * // https://www.geeksforgeeks.org/inorder-tree-traversal-without-recursion/
 * // http://n00tc0d3r.blogspot.com/2013/08/implement-iterator-for-binarytree-ii.html
 * // https://www.baeldung.com/java-binary-tree
 * A binary tree implementation
 * Two iterators are created which will run in parallel yielding
 * pre order and inorder traversals of the tree
 * Tree is show as below
 */

public class Impl2Main {
    BinaryTree bt = new SimpleBinaryTree();

    /*
         6
       /   \
      4     8
     / \   / \
    3  5   7  9
     */

    public void run(){
        bt.add(6);
        bt.add(4);
        bt.add(8);
        bt.add(3);
        bt.add(5);
        bt.add(7);
        bt.add(9);

        // Get PreOrderIterator
        BTIterator preOrderBTIterator = bt.iterator();
        // Get InOrderIterator
        BTIterator inOrderBTIterator = bt.inOrderIterator();
        System.out.println("         6\n" +
                "       /   \\\n" +
                "      4     8\n" +
                "     / \\   / \\\n" +
                "    3  5   7  9");
        System.out.println("PreOrder\tInorder");
        // Iterate through iterator
        while(preOrderBTIterator.hasNext()){
            System.out.println(preOrderBTIterator.next() + "\t\t\t" + inOrderBTIterator.next());
        }
    }

    public static void main(String[] args) {
        Impl2Main impl2Main = new Impl2Main();
        impl2Main.run();
    }
}
