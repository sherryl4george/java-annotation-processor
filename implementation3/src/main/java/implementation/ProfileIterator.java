package implementation;

import annotations.IteratorPattern;

/***
 * Iterator interface with necessary annotations
 */
@IteratorPattern.Iterator(returnType = Profile.class)
interface ProfileIterator {
    // HasNext methiod
    @IteratorPattern.Iterator.hasNext
    boolean hasNext();
    // Next
    @IteratorPattern.Iterator.next
    Profile getNext();
    void reset();
}
