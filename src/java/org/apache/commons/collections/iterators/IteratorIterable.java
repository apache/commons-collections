/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.collections.iterators;

import java.util.Iterator;

/**
 * Adapter to make an {@link Iterator Iterator} instance appear to be an
 * {@link Iterable Iterable} instance. Unlike normal iterable instance, the
 * {@link #iterator()} method always returns the same iterator instance. This
 * prohibits this iterator to be only usable for one iterative operation.
 * 
 * @since Commons Collections 4.0
 * @version $Revision: $ $Date: $
 */
public class IteratorIterable<E> implements Iterable<E> {

    /**
     * Factory method to create an {@link Iterator Iterator} from another
     * iterator over objects of a different subtype.
     */
    private static <E> Iterator<E> createTypesafeIterator(
            final Iterator<? extends E> iterator) {
        return new Iterator<E>() {
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public E next() {
                return iterator.next();
            }

            public void remove() {
                iterator.remove();
            }
        };
    }

    /** the iterator being used. */
    private final Iterator<E> iterator;

    /**
     * Constructs a new <code>IteratorIterable</code> that will use the given
     * iterator.
     * 
     * @param iterator the iterator to use.
     */
    public IteratorIterable(Iterator<? extends E> iterator) {
        super();
        this.iterator = createTypesafeIterator(iterator);
    }

    /**
     * Gets the iterator wrapped by this iterable.
     * 
     * @return the iterator
     */
    public Iterator<E> iterator() {
        return iterator;
    }
}
