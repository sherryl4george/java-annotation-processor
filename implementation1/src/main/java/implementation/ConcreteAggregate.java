package implementation;

import annotations.IteratorPattern;

import java.util.NoSuchElementException;

/***
 * Concrete Aggregate with annoations implemented
 * Has a private inner class which implements iterator
 * @param <E>
 */
@SuppressWarnings("unchecked")
@IteratorPattern.ConcreteAggregate
public class ConcreteAggregate<E> implements Aggregate<E> { // E = Type parameter
    // Hiding the representation.
    private Object[] elementData; // represented as object array
    private int idx = 0;
    private int size;
    //
    public ConcreteAggregate(int size) {
        if (size < 0)
            throw new IllegalArgumentException("size: " + size);
        this.size = size;
        elementData = new Object[size];
    }

    public boolean add(E element) {
        if (idx < size) {
            elementData[idx++] = element;
            return true;
        } else
            return false;
    }
    public int getSize() {
        return size;
    }
    // Factory method for instantiating Iterator1.
    @IteratorPattern.ConcreteAggregate.IteratorFactory(returnType = ConcreteIterator.class)
    @Override
    public Iterator<E> createIterator() {
        return new ConcreteIterator<E>();
    }
    //
    // Implementing Iterator1 as inner class.
    //
    @IteratorPattern.ConcreteIterator
    private class ConcreteIterator<E> implements Iterator<E> {
        // Holds the current position in the traversal.
        private int cursor = 0; // index of next element to return
        //
        @Override
        public boolean hasNext() {
            return cursor < size;
        }
        @Override
        public E next() { // E = Type of element returned by this method
            if (cursor >= size)
                throw new NoSuchElementException();
            return (E) elementData[cursor++]; // cast from Object to E
        }
    }
}