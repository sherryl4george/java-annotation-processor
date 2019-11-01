package implementation;

import annotations.IteratorPattern;

/***
 * BTIterator interface with necessary annotations
 */
@IteratorPattern.Iterator(returnType = Integer.class)
public interface BTIterator {
    // HasNext methiod
    @IteratorPattern.Iterator.hasNext
    boolean hasNext();
    // Next
    @IteratorPattern.Iterator.next
    Integer next();
}

