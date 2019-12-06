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
package org.apache.commons.collections4.deque;


import org.apache.commons.collections4.BoundedCollection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.List;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.Iterator;
/**
 * CircularDeque is a deque with a fixed size that replaces its oldest
 * element if full.
 * <p>
 * The removal order of a {@link CircularDeque} is based on the
 * insertion order; elements are removed in the same order in which they
 * were added. The iteration order is the same as the removal order.
 * </p>
 * <p>
 * The {@link #add(Object)}, {@link #remove()}, {@link #peek()}, {@link #poll},
 * {@link #offer(Object)} operations all perform in constant time.
 * All other operations perform in linear time or worse.
 * </p>
 * <p>
 * This deque prevents null objects from being added.
 * </p>
 *
 * @param <E> the type of elements in this collection
 * @since 4.5
 */
public class CircularDeque<E> extends AbstractCollection<E>
        implements Deque<E>, BoundedCollection<E>, Serializable {

    /** Underlying storage array. */
    private transient E[] elements;

    /** Array index of first (oldest) deque element. */
    private transient int head = 0;

    /**
     * Index mod maxElements of the array position following the last deque
     * element. Deque elements start at elements[head] and "wrap around"
     * elements[maxElements-1], ending at elements[decrement(tail)].
     * For example, elements = {c,a,b}, start=1, end=1 corresponds to
     * the deque [a,b,c].
     */
    private transient int tail = 0;

    /** Flag to indicate if the deque is currently full. */
    private transient boolean full = false;

    /** Capacity of the deque. */
    private final int maxElements;

    /**
     * Constructor that creates a deque with the default size of 32.
     */
    public CircularDeque() {
        this(32);
    }

    /**
     * Constructor that creates a deque with the specified size.
     *
     * @param size  the size of the deque (cannot be changed)
     * @throws IllegalArgumentException  if the size is &lt; 1
     */
    @SuppressWarnings("unchecked")
    public CircularDeque(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        elements = (E[]) new Object[size];
        maxElements = elements.length;
    }

    /**
     * Constructor that creates a deque from the specified collection.
     * The collection size also sets the deque size.
     *
     * @param coll  the collection to copy into the deque, may not be null
     * @throws NullPointerException if the collection is null
     */
    public CircularDeque(final Collection<? extends E> coll) {
        if (coll == null) {
            throw new NullPointerException("Collection must not be null");
        }

        elements = (E[]) new Object[coll.size()];
        maxElements = elements.length;

        addAll(coll);
    }

    /**
     * Write the deque out using a custom routine.
     *
     * @param out  the output stream
     * @throws IOException if an I/O error occurs while writing to the output stream
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(size());
        for (final E e : this) {
            out.writeObject(e);
        }
    }

    /**
     * Read the deque in using a custom routine.
     *
     * @param in the input stream
     * @throws IOException if an I/O error occurs while writing to the output stream
     * @throws ClassNotFoundException if the class of a serialized object can not be found
     */
    @SuppressWarnings("unchecked")
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        elements = (E[]) new Object[maxElements];
        final int size = in.readInt();
        for (int i = 0; i < size; i++) {
            elements[i] = (E) in.readObject();
        }
        head = 0;
        full = size == maxElements;
        if (full) {
            tail = 0;
        } else {
            tail = size;
        }
    }

    /**
     * Returns true if this deque is empty; false otherwise.
     *
     * @return true if this deque is empty
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns {@code true} if the capacity limit of this deque has been reached,
     * i.e. the number of elements stored in the deque equals its maximum size.
     *
     * @return {@code true} if the capacity limit has been reached, {@code false} otherwise
     * @since 4.5
     */
    public boolean isAtFullCapacity() {
        return size() == maxElements;
    }

    /**
     * Increase the internal index.
     *
     * @param index  the index to increment
     * @return the updated index
     */
    private int increaseIndex(int index) {
        index++;
        if (index >= maxElements) {
            index = 0;
        }
        return index;
    }

    /**
     * Decrease the internal index.
     *
     * @param index  the index to decrement
     * @return the updated index
     */
    private int decreaseIndex(int index) {
        index--;
        if (index < 0) {
            index = maxElements - 1;
        }
        return index;
    }

    /**
     * Adds the given element before the head of this deque. If the deque is full,
     * the tail element will be overridden so that a new element can be inserted.
     *
     * @param e  the element to add
     */
    @Override
    public void addFirst(final E e) {
        if (isAtFullCapacity()) {
            remove();
        }

        if (head == 0) {
            head = maxElements - 1;
        } else {
            head--;
        }

        elements[head] = e;

        if (tail == head) {
            full = true;
        }
    }

    /**
     * Adds the given element at the tail of this deque. If the deque is full,
     * the head element will be overridden so that a new element can be inserted.
     *
     * @param e  the element to add
     */
    @Override
    public void addLast(final E e) {
        if (isAtFullCapacity()) {
            removeFirst();
        }

        elements[tail++] = e;

        if (tail >= maxElements) {
            tail = 0;
        }

        if (tail == head) {
            full = true;
        }
    }

    @Override
    public boolean offerFirst(final E e) {
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(final E e) {
        addLast(e);
        return true;
    }

    @Override
    public E removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }

        final E element = elements[head];

        elements[head++] = null;

        if (head >= maxElements) {
            head = 0;
        }
        full = false;

        return element;
    }

    @Override
    public E removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }

        final E element = elements[tail];
        elements[tail--] = null;

        if (tail < 0) {
            tail = maxElements - 1;
        }
        full = false;

        return element;
    }

    @Override
    public E pollFirst() {
        if (isEmpty()) {
            return null;
        }
        return removeFirst();
    }

    @Override
    public E pollLast() {
        if (isEmpty()) {
            return null;
        }
        return removeLast();
    }

    @Override
    public E getFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("deque is empty");
        }
        return elements[head];
    }

    @Override
    public E getLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("deque is empty");
        }

        return elements[decreaseIndex(tail)];
    }

    @Override
    public E peekFirst() {
        if (isEmpty()) {
            return null;
        }
        return elements[head];
    }

    @Override
    public E peekLast() {
        if (isEmpty()) {
            return null;
        }

        return elements[decreaseIndex(tail)];
    }

    @Override
    public boolean removeFirstOccurrence(final Object o) {
        int i = head;
        while (true) {
            if (Objects.equals(o,elements[i])) {
                delete(i);
                return true;
            }
            if (i == decreaseIndex(tail)) {
                return false;
            }
            i = increaseIndex(i);
        }
    }

    @Override
    public boolean removeLastOccurrence(final Object o) {
        int i = decreaseIndex(tail);
        while (true) {
            if (Objects.equals(o,elements[i])) {
                delete(i);
                return true;
            }
            if (i == head) {
                return false;
            }
            i = decreaseIndex(i);
        }
    }

     @Override
     public boolean add(final E e) {
         if (isAtFullCapacity()) {
             remove();
         }

         elements[tail++] = e;

         if (tail >= maxElements) {
             tail = 0;
         }

         if (tail == head) {
             full = true;
         }

         return true;
     }

    @Override
    public boolean offer(final E e) {
        return offerLast(e);
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public void push(final E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }


    /**
     * Removes the element at the specified position in the elements array,
     * adjusting head and tail as necessary.  This can result in motion of
     * elements backwards or forwards in the array.
     *
     * <p>This method is called delete rather than remove to emphasize
     * that its semantics differ from those of {@link List#remove(int)}.
     *
     * @return true if elements moved backwards
     */
    private boolean delete(final int i) {
        if (i == head) {
            elements[i] = null;
            head = increaseIndex(head);
            full = false;
            return true;
        }

        if (i == decreaseIndex(tail)) {
            elements[i] = null;
            tail = decreaseIndex(tail);
            full = false;
            return true;
        }

        final int front = i - head;
        final int back  = tail - i - 1;

        if (head < tail) {
            if (front < back) {
                System.arraycopy(elements, head, elements, head + 1, front);
                head++;
            } else {
                System.arraycopy(elements, i + 1, elements, i, back);
                tail--;
            }
        } else if (head >tail) {
            if (i > head) {
                System.arraycopy(elements, head, elements, head + 1, front);
                head++;
            } else if (i < tail) {
                System.arraycopy(elements, i + 1, elements, i, back);
                tail--;
            } else {
                throw new ConcurrentModificationException();
            }
        }
        full = false;
        return true;
    }

    /**
     * Returns the number of elements stored in the deque.
     *
     * @return this deque's size
     */
    @Override
    public int size() {
        int size = 0;

        if (tail < head) {
            size = maxElements - head + tail;
        } else if (tail == head) {
            size = full ? maxElements : 0;
        } else {
            size = tail - head;
        }

        return size;
    }

    /**
     * {@inheritDoc}
     * <p>
     * A {@code CircularDeque} can never be full, thus this returns always
     * {@code false}.
     *
     * @return always returns {@code false}
     */
    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public int maxSize() {
        return maxElements;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private int index = head;
            private int lastReturnedIndex = -1;
            private boolean isFirst = full;

            @Override
            public boolean hasNext() {
                return isFirst || index != tail;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                isFirst = false;
                lastReturnedIndex = index;
                index = increaseIndex(index);
                return elements[lastReturnedIndex];
            }

            @Override
            public void remove() {
                if (lastReturnedIndex == -1) {
                    throw new IllegalStateException();
                }

                // First element can be removed quickly
                if (lastReturnedIndex == head) {
                    CircularDeque.this.remove();
                    lastReturnedIndex = -1;
                    return;
                }

                int pos = lastReturnedIndex + 1;
                if (head < lastReturnedIndex && pos < tail) {
                    // shift in one part
                    System.arraycopy(elements, pos, elements, lastReturnedIndex, tail - pos);
                } else {
                    // Other elements require us to shift the subsequent elements
                    while (pos != tail) {
                        if (pos >= maxElements) {
                            elements[pos - 1] = elements[0];
                            pos = 0;
                        } else {
                            elements[decreaseIndex(pos)] = elements[pos];
                            pos = increaseIndex(pos);
                        }
                    }
                }

                lastReturnedIndex = -1;
                tail = decreaseIndex(tail);
                elements[tail] = null;
                full = false;
                index = decreaseIndex(index);
            }

        };
    }


    @Override
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    private class DescendingIterator implements Iterator<E> {
        /*
         * This class is nearly a mirror-image of DeqIterator, using
         * tail instead of head for initial cursor, and head instead of
         * tail for fence.
         */
        private int cursor = tail;
        private int fence = head;
        private int lastRet = -1;
        private boolean isFull = full;

        public boolean hasNext() {
            return isFull || cursor != fence;
        }

        public E next() {
            if (!hasNext())
                throw new NoSuchElementException();
            cursor = decreaseIndex(cursor);
            @SuppressWarnings("unchecked")
            E result = (E) elements[cursor];
            if (head != fence )
                throw new ConcurrentModificationException();
            lastRet = cursor;
            return result;
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            if (!delete(lastRet)) {
                cursor = increaseIndex(cursor);
                fence = head;
            }
            lastRet = -1;
            isFull = false;
        }
    }
}
