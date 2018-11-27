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
package org.apache.commons.collections4.iterators;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.ResettableListIterator;

/**
 * Implements a {@link java.util.ListIterator} over an array.
 * <p>
 * The array can be either an array of object or of primitives. If you know
 * that you have an object array, the {@link ObjectArrayListIterator}
 * class is a better choice, as it will perform better.
 *
 * <p>
 * This iterator does not support {@link #add(Object)} or {@link #remove()}, as the array
 * cannot be changed in size. The {@link #set(Object)} method is supported however.
 *
 * @see org.apache.commons.collections4.iterators.ArrayIterator
 * @see java.util.Iterator
 * @see java.util.ListIterator
 *
 * @since 3.0
 */
public class ArrayListIterator<E> extends ArrayIterator<E>
        implements ResettableListIterator<E> {

    /**
     * Holds the index of the last item returned by a call to <code>next()</code>
     * or <code>previous()</code>. This is set to <code>-1</code> if neither method
     * has yet been invoked. <code>lastItemIndex</code> is used to to implement
     * the {@link #set} method.
     */
    private int lastItemIndex = -1;

    // Constructors
    // ----------------------------------------------------------------------
    /**
     * Constructs an ArrayListIterator that will iterate over the values in the
     * specified array.
     *
     * @param array the array to iterate over
     * @throws IllegalArgumentException if <code>array</code> is not an array.
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     */
    public ArrayListIterator(final Object array) {
        super(array);
    }

    /**
     * Constructs an ArrayListIterator that will iterate over the values in the
     * specified array from a specific start index.
     *
     * @param array  the array to iterate over
     * @param startIndex  the index to start iterating at
     * @throws IllegalArgumentException if <code>array</code> is not an array.
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     * @throws IndexOutOfBoundsException if the start index is out of bounds
     */
    public ArrayListIterator(final Object array, final int startIndex) {
        super(array, startIndex);
    }

    /**
     * Construct an ArrayListIterator that will iterate over a range of values
     * in the specified array.
     *
     * @param array  the array to iterate over
     * @param startIndex  the index to start iterating at
     * @param endIndex  the index (exclusive) to finish iterating at
     * @throws IllegalArgumentException if <code>array</code> is not an array.
     * @throws IndexOutOfBoundsException if the start or end index is out of bounds
     * @throws IllegalArgumentException if end index is before the start
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     */
    public ArrayListIterator(final Object array, final int startIndex, final int endIndex) {
        super(array, startIndex, endIndex);
    }

    // ListIterator interface
    //-----------------------------------------------------------------------
    /**
     * Returns true if there are previous elements to return from the array.
     *
     * @return true if there is a previous element to return
     */
    @Override
    public boolean hasPrevious() {
        return this.index > this.startIndex;
    }

    /**
     * Gets the previous element from the array.
     *
     * @return the previous element
     * @throws NoSuchElementException if there is no previous element
     */
    @Override
    @SuppressWarnings("unchecked")
    public E previous() {
        if (hasPrevious() == false) {
            throw new NoSuchElementException();
        }
        this.lastItemIndex = --this.index;
        return (E) Array.get(this.array, this.index);
    }

    /**
     * Gets the next element from the array.
     *
     * @return the next element
     * @throws NoSuchElementException if there is no next element
     */
    @Override
    @SuppressWarnings("unchecked")
    public E next() {
        if (hasNext() == false) {
            throw new NoSuchElementException();
        }
        this.lastItemIndex = this.index;
        return (E) Array.get(this.array, this.index++);
    }

    /**
     * Gets the next index to be retrieved.
     *
     * @return the index of the item to be retrieved next
     */
    @Override
    public int nextIndex() {
        return this.index - this.startIndex;
    }

    /**
     * Gets the index of the item to be retrieved if {@link #previous()} is called.
     *
     * @return the index of the item to be retrieved next
     */
    @Override
    public int previousIndex() {
        return this.index - this.startIndex - 1;
    }

    /**
     * This iterator does not support modification of its backing collection, and so will
     * always throw an {@link UnsupportedOperationException} when this method is invoked.
     *
     * @param o  the element to add
     * @throws UnsupportedOperationException always thrown.
     * @see java.util.ListIterator#set
     */
    @Override
    public void add(final Object o) {
        throw new UnsupportedOperationException("add() method is not supported");
    }

    /**
     * Sets the element under the cursor.
     * <p>
     * This method sets the element that was returned by the last call
     * to {@link #next()} of {@link #previous()}.
     * <p>
     * <b>Note:</b> {@link java.util.ListIterator} implementations that support
     * <code>add()</code> and <code>remove()</code> only allow <code>set()</code> to be called
     * once per call to <code>next()</code> or <code>previous</code> (see the {@link java.util.ListIterator}
     * javadoc for more details). Since this implementation does
     * not support <code>add()</code> or <code>remove()</code>, <code>set()</code> may be
     * called as often as desired.
     *
     * @param o  the element to set
     * @throws IllegalStateException if {@link #next()} or {@link #previous()} has not been called
     * before {@link #set(Object)}
     * @see java.util.ListIterator#set
     */
    @Override
    public void set(final Object o) {
        if (this.lastItemIndex == -1) {
            throw new IllegalStateException("must call next() or previous() before a call to set()");
        }

        Array.set(this.array, this.lastItemIndex, o);
    }

    /**
     * Resets the iterator back to the start index.
     */
    @Override
    public void reset() {
        super.reset();
        this.lastItemIndex = -1;
    }

}
