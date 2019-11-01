package implementation;

import annotations.IteratorPattern;
/***
 * Aggregate with annoations implemented
 * Has public Iterator factory methods
 */
@IteratorPattern.Aggregate
public interface SocialNetwork {
    // FactoryMethods
    @IteratorPattern.Aggregate.IteratorFactory
    ProfileIterator createFriendsIterator(String profileEmail);
    // FactoryMethods
    @IteratorPattern.Aggregate.IteratorFactory
    ProfileIterator createCoworkersIterator(String profileEmail);
}
