package implementation;

import annotations.IteratorPattern;

/***
 * Aggregate with annoations implemented
 * Has public BTIterator factory methods
 */
@IteratorPattern.Aggregate
abstract class BinaryTree {
    Node root;
    // Concrete implementation of addition logic
    private Node addRecursive(Node current, int value) {
        if (current == null) {
            return new Node(value);
        }

        if (value < current.val) {
            current.left = addRecursive(current.left, value);
        } else if (value > current.val) {
            current.right = addRecursive(current.right, value);
        } else {
            // value already exists
            return current;
        }

        return current;
    }

    public void add(int value) {
        root = addRecursive(root, value);
    }

    // IteratorFactory methods
    @IteratorPattern.Aggregate.IteratorFactory
    abstract BTIterator iterator();

    // IteratorFactory methods
    @IteratorPattern.Aggregate.IteratorFactory
    abstract BTIterator inOrderIterator();
}
