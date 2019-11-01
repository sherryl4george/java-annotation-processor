package implementation;

import annotations.*;

/***
 * Iterator interface with necessary annotations
 * @param <T>
 */
@IteratorPattern.Iterator(typeVariableReturnIndex = 0)
public interface Iterator<T>{
    // HasNext methiod
    @IteratorPattern.Iterator.hasNext
    boolean hasNext();
    // Next
    @IteratorPattern.Iterator.next
    T next();
}

