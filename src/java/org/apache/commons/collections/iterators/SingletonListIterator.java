/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.iterators;

import java.util.ListIterator;
import java.util.NoSuchElementException;
/** 
 * <p><code>SingletonIterator</code> is an {@link ListIterator} over a single 
 * object instance.</p>
 *
 * @since 2.1
 * @author <a href="mailto:scolebourne@joda.org">Stephen Colebourne</a>
 * @version $Id: SingletonListIterator.java,v 1.2.2.1 2004/05/22 12:14:04 scolebourne Exp $
 */
public class SingletonListIterator implements ListIterator {

    private boolean first = true;
    private boolean nextCalled = false;
    private Object object;

    /**
     * Constructs a new <Code>SingletonListIterator</Code>.
     *
     * @param object  the single object to return from the iterator
     */
    public SingletonListIterator(Object object) {
        super();
        this.object = object;
    }

    /**
     * Is another object available from the iterator.
     * <p>
     * This returns true if the single object hasn't been returned yet.
     * 
     * @return true if the single object hasn't been returned yet
     */
    public boolean hasNext() {
        return first;
    }

    /**
     * Is a previous object available from the iterator.
     * <p>
     * This returns true if the single object has been returned.
     * 
     * @return true if the single object has been returned
     */
    public boolean hasPrevious() {
        return !first;
    }

    /**
     * Returns the index of the element that would be returned by a subsequent
     * call to <tt>next</tt>.
     *
     * @return 0 or 1 depending on current state. 
     */
    public int nextIndex() {
        return (first ? 0 : 1);
    }

    /**
     * Returns the index of the element that would be returned by a subsequent
     * call to <tt>previous</tt>. A return value of -1 indicates that the iterator is currently at
     * the start.
     *
     * @return 0 or -1 depending on current state. 
     */
    public int previousIndex() {
        return (first ? -1 : 0);
    }

    /**
     * Get the next object from the iterator.
     * <p>
     * This returns the single object if it hasn't been returned yet.
     *
     * @return the single object
     * @throws NoSuchElementException if the single object has already 
     *    been returned
     */
    public Object next() {
        if (!first) {
            throw new NoSuchElementException();
        }
        first = false;
        nextCalled = true;
        return object;
    }

    /**
     * Get the previous object from the iterator.
     * <p>
     * This returns the single object if it has been returned.
     *
     * @return the single object
     * @throws NoSuchElementException if the single object has not already 
     *    been returned
     */
    public Object previous() {
        if (first) {
            throw new NoSuchElementException();
        }
        first = true;
        return object;
    }

    /**
     * Remove always throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported by this iterator");
    }
    
    /**
     * Add always throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    public void add(Object obj) {
        throw new UnsupportedOperationException("add() is not supported by this iterator");
    }
    
    /**
     * Set sets the value of the singleton.
     *
     * @param obj  the object to set
     * @throws IllegalStateException if <tt>next</tt> has not been called
     */
    public void set(Object obj) {
        if (nextCalled == false) {
            throw new IllegalStateException();
        }
        this.object = obj;
    }
    
}
