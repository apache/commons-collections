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
package org.apache.commons.collections;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 * The BoundedFifoBuffer is a <strong>very</strong> efficient implementation of
 * Buffer that does not alter the size of the buffer at runtime.
 * <p>
 * The removal order of a <code>BoundedFifoBuffer</code> is based on the 
 * insertion order; elements are removed in the same order in which they
 * were added.  The iteration order is the same as the removal order.
 * <p>
 * The {@link #add(Object)}, {@link #remove()} and {@link #get()} operations
 * all perform in constant time.  All other operations perform in linear
 * time or worse.
 * <p>
 * Note that this implementation is not synchronized.  The following can be
 * used to provide synchronized access to your <code>BoundedFifoBuffer</code>:
 * <pre>
 *   Buffer fifo = BufferUtils.synchronizedBuffer(new BoundedFifoBuffer());
 * </pre>
 * <p>
 * This buffer prevents null objects from being added.
 *
 * @author Avalon
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author Paul Jack
 * @author Stephen Colebourne
 * @since 2.1
 * @version $Id: BoundedFifoBuffer.java,v 1.5.2.1 2004/05/22 12:14:02 scolebourne Exp $
 */
public class BoundedFifoBuffer extends AbstractCollection implements Buffer {
    private final Object[] m_elements;
    private int m_start = 0;
    private int m_end = 0;
    private boolean m_full = false;

    /**
     * Constructs a new <code>BoundedFifoBuffer</code> big enough to hold
     * 32 elements.
     */
    public BoundedFifoBuffer() {
        this(32);
    }

    /**
     * Constructs a new <code>BoundedFifoBuffer</code> big enough to hold
     * the specified number of elements.
     *
     * @param size  the maximum number of elements for this fifo
     * @throws IllegalArgumentException  if the size is less than 1
     */
    public BoundedFifoBuffer(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        m_elements = new Object[size];
    }

    /**
     * Constructs a new <code>BoundedFifoBuffer</code> big enough to hold all
     * of the elements in the specified collection. That collection's
     * elements will also be added to the buffer.
     *
     * @param coll  the collection whose elements to add
     */
    public BoundedFifoBuffer(Collection coll) {
        this(coll.size());
        addAll(coll);
    }

    /**
     * Returns the number of elements stored in the buffer.
     *
     * @return this buffer's size
     */
    public int size() {
        int size = 0;

        if (m_end < m_start) {
            size = m_elements.length - m_start + m_end;
        } else if (m_end == m_start) {
            size = (m_full ? m_elements.length : 0);
        } else {
            size = m_end - m_start;
        }

        return size;
    }

    /**
     * Returns true if this buffer is empty; false otherwise.
     *
     * @return true if this buffer is empty
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Clears this buffer.
     */
    public void clear() {
        m_full = false;
        m_start = 0;
        m_end = 0;
        Arrays.fill(m_elements, null);
    }

    /**
     * Adds the given element to this buffer.
     *
     * @param element  the element to add
     * @return true, always
     * @throws NullPointerException  if the given element is null
     * @throws BufferOverflowException  if this buffer is full
     */
    public boolean add(Object element) {
        if (null == element) {
            throw new NullPointerException("Attempted to add null object to buffer");
        }

        if (m_full) {
            throw new BufferOverflowException("The buffer cannot hold more than " + m_elements.length + " objects.");
        }

        m_elements[m_end++] = element;

        if (m_end >= m_elements.length) {
            m_end = 0;
        }

        if (m_end == m_start) {
            m_full = true;
        }

        return true;
    }

    /**
     * Returns the least recently inserted element in this buffer.
     *
     * @return the least recently inserted element
     * @throws BufferUnderflowException  if the buffer is empty
     */
    public Object get() {
        if (isEmpty()) {
            throw new BufferUnderflowException("The buffer is already empty");
        }

        return m_elements[m_start];
    }

    /**
     * Removes the least recently inserted element from this buffer.
     *
     * @return the least recently inserted element
     * @throws BufferUnderflowException  if the buffer is empty
     */
    public Object remove() {
        if (isEmpty()) {
            throw new BufferUnderflowException("The buffer is already empty");
        }

        Object element = m_elements[m_start];

        if (null != element) {
            m_elements[m_start++] = null;

            if (m_start >= m_elements.length) {
                m_start = 0;
            }

            m_full = false;
        }

        return element;
    }

    /**
     * Increments the internal index.
     * 
     * @param index  the index to increment
     * @return the updated index
     */
    private int increment(int index) {
        index++; 
        if (index >= m_elements.length) {
            index = 0;
        }
        return index;
    }

    /**
     * Decrements the internal index.
     * 
     * @param index  the index to decrement
     * @return the updated index
     */
    private int decrement(int index) {
        index--;
        if (index < 0) {
            index = m_elements.length - 1;
        }
        return index;
    }

    /**
     * Returns an iterator over this buffer's elements.
     *
     * @return an iterator over this buffer's elements
     */
    public Iterator iterator() {
        return new Iterator() {

            private int index = m_start;
            private int lastReturnedIndex = -1;
            private boolean isFirst = m_full;

            public boolean hasNext() {
                return isFirst || (index != m_end);
                
            }

            public Object next() {
                if (!hasNext()) throw new NoSuchElementException();
                isFirst = false;
                lastReturnedIndex = index;
                index = increment(index);
                return m_elements[lastReturnedIndex];
            }

            public void remove() {
                if (lastReturnedIndex == -1) throw new IllegalStateException();

                // First element can be removed quickly
                if (lastReturnedIndex == m_start) {
                    BoundedFifoBuffer.this.remove();
                    lastReturnedIndex = -1;
                    return;
                }

                // Other elements require us to shift the subsequent elements
                int i = lastReturnedIndex + 1;
                while (i != m_end) {
                    if (i >= m_elements.length) {
                        m_elements[i - 1] = m_elements[0];
                        i = 0;
                    } else {
                        m_elements[i - 1] = m_elements[i];
                        i++;
                    }
                }

                lastReturnedIndex = -1;
                m_end = decrement(m_end);
                m_elements[m_end] = null;
                m_full = false;
                index = decrement(index);
            }

        };
    }

}
