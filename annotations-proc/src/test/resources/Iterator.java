import annotations.IteratorPattern;


@IteratorPattern.Iterator(typeVariableReturnIndex = 0)
public interface Iterator<T>{
    @IteratorPattern.Iterator.hasNext
    boolean hasNext();
    @IteratorPattern.Iterator.next
    T next();
}

