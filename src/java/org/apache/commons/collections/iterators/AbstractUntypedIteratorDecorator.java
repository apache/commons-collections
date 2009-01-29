/**
 * 
 */
package org.apache.commons.collections.iterators;

import java.util.Iterator;

/**
 * Provides basic behaviour for decorating an iterator with extra functionality
 * without committing the generic type of the Iterator implementation.
 * <p>
 * All methods are forwarded to the decorated iterator.
 * 
 * @since Commons Collections 5
 * @version $Revision$ $Date$
 * 
 * @author James Strachan
 * @author Stephen Colebourne
 * @author Matt Benson
 */
public abstract class AbstractUntypedIteratorDecorator<I, O> implements Iterator<O> {

    /** The iterator being decorated */
    protected final Iterator<I> iterator;

    /**
     * Create a new AbstractUntypedIteratorDecorator.
     */
    protected AbstractUntypedIteratorDecorator(Iterator<I> iterator) {
        super();
        if (iterator == null) {
            throw new IllegalArgumentException("Iterator must not be null");
        }
        this.iterator = iterator;
    }

    /**
     * Gets the iterator being decorated.
     * 
     * @return the decorated iterator
     */
    protected Iterator<I> getIterator() {
        return iterator;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public void remove() {
        iterator.remove();
    }

}