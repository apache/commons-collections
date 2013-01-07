/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.buffer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUnderflowException;

/**
 * UnboundedFifoBuffer is a very efficient implementation of
 * {@link Buffer} that can grow to any size.
 * According to performance testing, it exhibits a constant access time, but it
 * also outperforms ArrayList when used for the same purpose.
 * <p>
 * The removal order of an {@link UnboundedFifoBuffer} is based on the insertion
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
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @since 3.0 (previously in main package v2.1)
 * @version $Id$
 */
public class UnboundedFifoBuffer<E> extends AbstractCollection<E> implements Buffer<E>, Serializable {
    // invariant: buffer.length > size()
    //   ie.buffer always has at least one empty entry

    /** Serialization version */
    private static final long serialVersionUID = -3482960336579541419L;

    /** The array of objects in the buffer. */
    protected transient E[] buffer;

    /** The current head index. */
    protected transient int head;

    /** The current tail index. */
    protected transient int tail;

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
    @SuppressWarnings("unchecked")
    public UnboundedFifoBuffer(final int initialSize) {
        if (initialSize <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        buffer = (E[]) new Object[initialSize + 1];
        head = 0;
        tail = 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Write the buffer out using a custom routine.
     *
     * @param out  the output stream
     * @throws IOException if an I/O error occurs while writing to the output stream
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(size());
        out.writeInt(buffer.length);
        for (final E e : this) {
            out.writeObject(e);
        }
    }

    /**
     * Read the buffer in using a custom routine.
     *
     * @param in  the input stream
     * @throws IOException if an I/O error occurs while reading from the input stream
     * @throws ClassNotFoundException if the class of a serialized object can not be found
     */
    @SuppressWarnings("unchecked")
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final int size = in.readInt();
        final int length = in.readInt();
        buffer = (E[]) new Object[length];
        for (int i = 0; i < size; i++) {
            buffer[i] = (E) in.readObject();
        }
        head = 0;
        tail = size;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the number of elements stored in the buffer.
     *
     * @return this buffer's size
     */
    @Override
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
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Adds the given element to this buffer.
     *
     * @param obj  the element to add
     * @return true, always
     * @throws NullPointerException  if the given element is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean add(final E obj) {
        if (obj == null) {
            throw new NullPointerException("Attempted to add null object to buffer");
        }

        if (size() + 1 >= buffer.length) {
            // copy contents to a new buffer array
            final E[] tmp = (E[]) new Object[(buffer.length - 1) * 2 + 1];
            int j = 0;
            // move head to element zero in the new array
            for (int i = head; i != tail;) {
                tmp[j] = buffer[i];
                buffer[i] = null;

                j++;
                i = increment(i);
            }
            buffer = tmp;
            head = 0;
            tail = j;
        }

        buffer[tail] = obj;
        tail = increment(tail);
        return true;
    }

    /**
     * Returns the next object in the buffer.
     *
     * @return the next object in the buffer
     * @throws BufferUnderflowException  if this buffer is empty
     */
    public E get() {
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
    public E remove() {
        if (isEmpty()) {
            throw new BufferUnderflowException("The buffer is already empty");
        }

        final E element = buffer[head];
        if (element != null) {
            buffer[head] = null;
            head = increment(head);
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
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private int index = head;
            private int lastReturnedIndex = -1;

            public boolean hasNext() {
                return index != tail;

            }

            public E next() {
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
                int i = increment(lastReturnedIndex);
                while (i != tail) {
                    buffer[decrement(i)] = buffer[i];
                    i = increment(i);
                }

                lastReturnedIndex = -1;
                tail = decrement(tail);
                buffer[tail] = null;
                index = decrement(index);
            }

        };
    }

}
