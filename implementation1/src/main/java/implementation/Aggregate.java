package implementation;

import annotations.*;

@IteratorPattern.Aggregate
public interface Aggregate<E> {
    @IteratorPattern.Aggregate.IteratorFactory
    Iterator<E> createIterator();
    boolean add(E element);
}
