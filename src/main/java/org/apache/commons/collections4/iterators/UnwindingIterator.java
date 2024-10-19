package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A class to unwind an iterator of iterators.
 * @param <T> The type of the object returned from the iterator.
 */
public class UnwindingIterator<T> implements Iterator<T> {
    /** The innermost iterator */
    private final Iterator<Iterator<T>> inner;
    /** The iterator extracted from the inner iterator */
    private Iterator<T> outer;

    /**
     * Constructs an iterator from an iterator of iterators.
     * <p>
     *     Unlike the IteratorChain<T> the inner iterators are not be constructed until needed so that any overhead
     *     involved in constructing an enclosed Iterator<T> is amortized across the entire operation and not front
     *     loaded on the constructor or first call.
     * </p>
     * @param it The iterator of iterators to unwind.
     */
    public UnwindingIterator(final Iterator<Iterator<T>> it) {
        this.inner = it;
    }

    @Override
    public boolean hasNext() {
        if (outer == null) {
            if (!inner.hasNext()) {
                return false;
            }
            outer = inner.next();
        }
        while (!outer.hasNext()) {
            if (!inner.hasNext()) {
                return false;
            }
            outer = inner.next();
        }
        return true;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return outer.next();
    }
}
