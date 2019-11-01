package implementation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/***
 * Impl2Main Driver to call all the implementations
 * Implementation1 - Simple Generic List Traversal
 * Implementation2 - BinaryTree PreOrder and InOrder traversal
 * Implementation1 - Social Media Spammer
 * All implementation are copied from the internet and used as is
 * by adding annotations to them.
 * No changes to the implementation or code are added from my end
 * other than for annotations
 */
public class Main {
    private static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Invoking 3 Implementations");
        System.out.println("Invoking 3 Implementations");

        logger.info("Implementation1 - Simple Generic List Traversal");
        System.out.println("\n\n********** Implementation1 - Simple Generic List Traversal");
        // Implementation 1
        Driver.main(null);
        logger.info("Implementation1 - Simple Generic List Traversal - Ended");

        logger.info("Implementation2 - BinaryTree PreOrder and InOrder traversal");
        System.out.println("\n\n********** Implementation2 - BinaryTree PreOrder and InOrder traversal");
        // Implementation 2
        Impl2Main.main(null);
        logger.info("Implementation2 - BinaryTree PreOrder and InOrder traversal - Ended");

        logger.info("Implementation3 - Social Media Spammer");
        System.out.println("\n\n********** Implementation3 - Social Media Spammer");
        // Implementation 3
        Demo.main(null);
        logger.info("Implementation3 - Social Media Spammer - Ended");

    }
}
