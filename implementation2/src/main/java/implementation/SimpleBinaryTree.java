package implementation;

import annotations.IteratorPattern;

import java.util.NoSuchElementException;
import java.util.Stack;

/***
 * Concrete Aggregate with annoations implemented
 * Has a private inner class which implements iterator
 */
@IteratorPattern.ConcreteAggregate
public class SimpleBinaryTree extends BinaryTree {
    // Overirden factoryMethods
    @Override
    @IteratorPattern.ConcreteAggregate.IteratorFactory(returnType = PreOrderBinaryTreeBTIterator.class)
    public BTIterator iterator() {
        return new PreOrderBinaryTreeBTIterator();
    }

    // Overirden factoryMethods
    @Override
    @IteratorPattern.ConcreteAggregate.IteratorFactory(returnType = PreOrderBinaryTreeBTIterator.class)
    public BTIterator inOrderIterator() {
        return new InOrderBinaryTreeBTIterator();
    }

    //private concreteITerator
    @IteratorPattern.ConcreteIterator
    private class PreOrderBinaryTreeBTIterator implements BTIterator {
        Stack<Node> stack = new Stack<>();

        /** Constructor */
        public PreOrderBinaryTreeBTIterator() {
            if (root != null) {
                stack.push(root); // add to end of queue
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }


        @Override
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException("All nodes have been visited!");
            }

            Node res = stack.pop(); // retrieve and remove the head of queue
            if (res.right != null) stack.push(res.right);
            if (res.left != null) stack.push(res.left);

            return res.val;
        }
    }

    //private concreteITerator
    @IteratorPattern.ConcreteIterator
    private class InOrderBinaryTreeBTIterator implements BTIterator {
        Stack<Node> stack = new Stack<>();
        Node curr;

        /** Constructor */
        public InOrderBinaryTreeBTIterator() {
            if (root != null) {
                curr = root;
            }
        }

        @Override
        public boolean hasNext() {
            return (curr != null || stack.size() > 0);
        }


        @Override
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException("All nodes have been visited!");
            }

            /* Reach the left most Node of the
            curr Node */
            while (curr !=  null)
            {
                /* place pointer to a tree node on
                   the stack before traversing
                  the node's left subtree */
                stack.push(curr);
                curr = curr.left;
            }

            /* Current must be NULL at this point */
            curr = stack.pop();

            int data = curr.val;

            /* we have visited the node and its
               left subtree.  Now, it's right
               subtree's turn */
            curr = curr.right;
            return data;
        }
    }
}

