/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/UnboundedFifoBuffer.java,v 1.8 2003/09/07 08:48:46 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.collections;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * UnboundedFifoBuffer is a <strong>very</strong> efficient buffer implementation.
 * According to performance testing, it exhibits a constant access time, but it
 * also outperforms ArrayList when used for the same purpose.
 * <p>
 * The removal order of an <code>UnboundedFifoBuffer</code> is based on the insertion
 * order; elements are removed in the same order in which they were added.
 * The iteration order is the same as the removal order.
 * <p>
 * The {@link #remove()} and {@link #get()} operations perform in constant time.
 * The {@link #add(Object)} operation performs in amortized constant time.  All
 * other operations perform in linear time or worse.
 * <p>
 * Note that this implementation is not synchronized.  The following can be
 * used to provide synchronized access to your <code>UnboundedFifo</code>:
 * <pre>
 *   Buffer fifo = BufferUtils.synchronizedBuffer(new UnboundedFifo());
 * </pre>
 * <p>
 * This buffer prevents null objects from being added.
 * 
 * @since Commons Collections 2.1
 * @version $Revision: 1.8 $ $Date: 2003/09/07 08:48:46 $
 *
 * @author Avalon
 * @author Federico Barbieri
 * @author Berin Loritsch
 * @author Paul Jack
 * @author Stephen Colebourne
 */
public final class UnboundedFifoBuffer extends AbstractCollection implements Buffer {
    
    protected Object[] m_buffer;
    protected int m_head;
    protected int m_tail;

    /**
     * Constructs an UnboundedFifoBuffer with the default number of elements.
     * It is exactly the same as performing the following:
     *
     * <pre>
     *   new UnboundedFifoBuffer(32);
     * </pre>
     */
    public UnboundedFifoBuffer() {
        this(32);
    }

    /**
     * Constructs an UnboundedFifoBuffer with the specified number of elements.
     * The integer must be a positive integer.
     * 
     * @throws IllegalArgumentException  if the size is less than 1
     */
    public UnboundedFifoBuffer(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        m_buffer = new Object[size + 1];
        m_head = 0;
        m_tail = 0;
    }

    /**
     * Returns the number of elements stored in the buffer.
     *
     * @return this buffer's size
     */
    public int size() {
        int size = 0;

        if (m_tail < m_head) {
            size = m_buffer.length - m_head + m_tail;
        } else {
            size = m_tail - m_head;
        }

        return size;
    }

    /**
     * Returns true if this buffer is empty; false otherwise.
     *
     * @return true if this buffer is empty
     */
    public boolean isEmpty() {
        return (size() == 0);
    }

    /**
     * Adds the given element to this buffer.
     *
     * @param element  the element to add
     * @return true, always
     * @throws NullPointerException  if the given element is null
     * @throws BufferOverflowException  if this buffer is full
     */
    public boolean add(final Object o) {
        if (null == o) {
            throw new NullPointerException("Attempted to add null object to buffer");
        }

        if (size() + 1 >= m_buffer.length) {
            Object[] tmp = new Object[((m_buffer.length - 1) * 2) + 1];

            int j = 0;
            for (int i = m_head; i != m_tail;) {
                tmp[j] = m_buffer[i];
                m_buffer[i] = null;

                j++;
                i++;
                if (i == m_buffer.length) {
                    i = 0;
                }
            }

            m_buffer = tmp;
            m_head = 0;
            m_tail = j;
        }

        m_buffer[m_tail] = o;
        m_tail++;
        if (m_tail >= m_buffer.length) {
            m_tail = 0;
        }
        return true;
    }

    /**
     * Returns the next object in the buffer.
     *
     * @return the next object in the buffer
     * @throws BufferUnderflowException  if this buffer is empty
     */
    public Object get() {
        if (isEmpty()) {
            throw new BufferUnderflowException("The buffer is already empty");
        }

        return m_buffer[m_head];
    }

    /**
     * Removes the next object from the buffer
     *
     * @return the removed object
     * @throws BufferUnderflowException  if this buffer is empty
     */
    public Object remove() {
        if (isEmpty()) {
            throw new BufferUnderflowException("The buffer is already empty");
        }

        Object element = m_buffer[m_head];

        if (null != element) {
            m_buffer[m_head] = null;

            m_head++;
            if (m_head >= m_buffer.length) {
                m_head = 0;
            }
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
        if (index >= m_buffer.length)
            index = 0;
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
        if (index < 0)
            index = m_buffer.length - 1;
        return index;
    }

    /**
     * Returns an iterator over this buffer's elements.
     *
     * @return an iterator over this buffer's elements
     */
    public Iterator iterator() {
        return new Iterator() {

            private int index = m_head;
            private int lastReturnedIndex = -1;

            public boolean hasNext() {
                return index != m_tail;

            }

            public Object next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                lastReturnedIndex = index;
                index = increment(index);
                return m_buffer[lastReturnedIndex];
            }

            public void remove() {
                if (lastReturnedIndex == -1)
                    throw new IllegalStateException();

                // First element can be removed quickly
                if (lastReturnedIndex == m_head) {
                    UnboundedFifoBuffer.this.remove();
                    lastReturnedIndex = -1;
                    return;
                }

                // Other elements require us to shift the subsequent elements
                int i = lastReturnedIndex + 1;
                while (i != m_tail) {
                    if (i >= m_buffer.length) {
                        m_buffer[i - 1] = m_buffer[0];
                        i = 0;
                    } else {
                        m_buffer[i - 1] = m_buffer[i];
                        i++;
                    }
                }

                lastReturnedIndex = -1;
                m_tail = decrement(m_tail);
                m_buffer[m_tail] = null;
                index = decrement(index);
            }

        };
    }
    
}
