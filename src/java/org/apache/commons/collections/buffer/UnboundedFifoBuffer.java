/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/buffer/UnboundedFifoBuffer.java,v 1.4 2004/01/04 18:56:37 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2004 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections.buffer;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUnderflowException;

/**
 * UnboundedFifoBuffer is a very efficient buffer implementation.
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
 * used to provide synchronized access to your <code>UnboundedFifoBuffer</code>:
 * <pre>
 *   Buffer fifo = BufferUtils.synchronizedBuffer(new UnboundedFifoBuffer());
 * </pre>
 * <p>
 * This buffer prevents null objects from being added.
 * 
 * @since Commons Collections 3.0 (previously in main package v2.1)
 * @version $Revision: 1.4 $ $Date: 2004/01/04 18:56:37 $
 *
 * @author Avalon
 * @author Federico Barbieri
 * @author Berin Loritsch
 * @author Paul Jack
 * @author Stephen Colebourne
 */
public class UnboundedFifoBuffer extends AbstractCollection implements Buffer {
    
    protected Object[] buffer;
    protected int head;
    protected int tail;

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
     * @param initialSize  the initial size of the buffer
     * @throws IllegalArgumentException  if the size is less than 1
     */
    public UnboundedFifoBuffer(int initialSize) {
        if (initialSize <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        buffer = new Object[initialSize + 1];
        head = 0;
        tail = 0;
    }

    /**
     * Returns the number of elements stored in the buffer.
     *
     * @return this buffer's size
     */
    public int size() {
        int size = 0;

        if (tail < head) {
            size = buffer.length - head + tail;
        } else {
            size = tail - head;
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
     * @param obj  the element to add
     * @return true, always
     * @throws NullPointerException  if the given element is null
     * @throws BufferOverflowException  if this buffer is full
     */
    public boolean add(final Object obj) {
        if (obj == null) {
            throw new NullPointerException("Attempted to add null object to buffer");
        }

        if (size() + 1 >= buffer.length) {
            Object[] tmp = new Object[((buffer.length - 1) * 2) + 1];

            int j = 0;
            for (int i = head; i != tail;) {
                tmp[j] = buffer[i];
                buffer[i] = null;

                j++;
                i++;
                if (i == buffer.length) {
                    i = 0;
                }
            }

            buffer = tmp;
            head = 0;
            tail = j;
        }

        buffer[tail] = obj;
        tail++;
        if (tail >= buffer.length) {
            tail = 0;
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

        return buffer[head];
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

        Object element = buffer[head];

        if (null != element) {
            buffer[head] = null;

            head++;
            if (head >= buffer.length) {
                head = 0;
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
        if (index >= buffer.length) {
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
            index = buffer.length - 1;
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

            private int index = head;
            private int lastReturnedIndex = -1;

            public boolean hasNext() {
                return index != tail;

            }

            public Object next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                lastReturnedIndex = index;
                index = increment(index);
                return buffer[lastReturnedIndex];
            }

            public void remove() {
                if (lastReturnedIndex == -1) {
                    throw new IllegalStateException();
                }

                // First element can be removed quickly
                if (lastReturnedIndex == head) {
                    UnboundedFifoBuffer.this.remove();
                    lastReturnedIndex = -1;
                    return;
                }

                // Other elements require us to shift the subsequent elements
                int i = lastReturnedIndex + 1;
                while (i != tail) {
                    if (i >= buffer.length) {
                        buffer[i - 1] = buffer[0];
                        i = 0;
                    } else {
                        buffer[i - 1] = buffer[i];
                        i++;
                    }
                }

                lastReturnedIndex = -1;
                tail = decrement(tail);
                buffer[tail] = null;
                index = decrement(index);
            }

        };
    }
    
}
