/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/iterators/ObjectArrayListIterator.java,v 1.4 2003/02/19 20:14:27 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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
package org.apache.commons.collections.iterators;

import java.util.NoSuchElementException;

/**
 * Implements a {@link ListIterator ListIterator} over an array of objects.
 * <p>
 * This iterator does not support {@link #add} or {@link #remove}, as the object array 
 * cannot be structurally modified. The {@link #set} method is supported however.
 * <p>
 * The iterator implements a {@link #reset} method, allowing the reset of the iterator
 * back to the start if required.
 *
 * @see org.apache.commons.collections.iterators.ObjectArrayIterator
 * @see java.util.Iterator
 * @see java.util.ListIterator
 *
 * @since Commons Collections 2.2
 * @version $Revision: 1.4 $ $Date: 2003/02/19 20:14:27 $
 * 
 * @author <a href="mailto:neilotoole@users.sourceforge.net">Neil O'Toole</a>
 * @author Stephen Colebourne
 */
public class ObjectArrayListIterator extends ObjectArrayIterator implements ResetableListIterator {

    /**
     * Holds the index of the last item returned by a call to <code>next()</code> 
     * or <code>previous()</code>. This is set to <code>-1</code> if neither method
     * has yet been invoked. <code>lastItemIndex</code> is used to to implement the
     * {@link #set} method.
     */
    protected int lastItemIndex = -1;

    /**
     * Constructor for use with <code>setArray</code>.
     * <p>
     * Using this constructor, the iterator is equivalent to an empty iterator
     * until {@link #setArray} is  called to establish the array to iterate over.
     */
    public ObjectArrayListIterator() {
        super();
    }

    /**
     * Constructs an ObjectArrayListIterator that will iterate over the values in the
     * specified array.
     *
     * @param array the array to iterate over
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     */
    public ObjectArrayListIterator(Object[] array) {
        super(array);
    }

    /**
     * Constructs an ObjectArrayListIterator that will iterate over the values in the
     * specified array from a specific start index.
     *
     * @param array  the array to iterate over
     * @param start  the index to start iterating at
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     * @throws IndexOutOfBoundsException if the start index is out of bounds
     */
    public ObjectArrayListIterator(Object[] array, int start) {
        super(array, start);
    }
    
    /**
     * Construct an ObjectArrayListIterator that will iterate over a range of values 
     * in the specified array.
     *
     * @param array  the array to iterate over
     * @param start  the index to start iterating at
     * @param end  the index (exclusive) to finish iterating at
     * @throws IndexOutOfBoundsException if the start or end index is out of bounds
     * @throws IllegalArgumentException if end index is before the start
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     */
    public ObjectArrayListIterator(Object[] array, int start, int end) {
        super(array, start, end);
    }

    // ListIterator interface
    //-------------------------------------------------------------------------

    /**
     * Returns true if there are previous elements to return from the array.
     *
     * @return true if there is a previous element to return
     */
    public boolean hasPrevious() {
        return (this.index > this.startIndex);
    }

    /**
     * Gets the previous element from the array.
     * 
     * @return the previous element
     * @throws NoSuchElementException if there is no previous element
     */
    public Object previous() {
        if (hasPrevious() == false) {
            throw new NoSuchElementException();
        }
        this.lastItemIndex = --this.index;
        return this.array[this.index];
    }

    /**
     * Gets the next element from the array.
     * 
     * @return the next element
     * @throws NoSuchElementException if there is no next element
     */
    public Object next() {
        if (hasNext() == false) {
            throw new NoSuchElementException();
        }
        this.lastItemIndex = this.index;
        return this.array[this.index++];
    }

    /**
     * Gets the next index to be retrieved.
     * 
     * @return the index of the item to be retrieved next
     */
    public int nextIndex() {
        return this.index;
    }

    /**
     * Gets the index of the item to be retrieved if {@link #previous()} is called.
     * 
     * @return the index of the item to be retrieved next
     */
    public int previousIndex() {
        return this.index - 1;
    }

    /**
     * This iterator does not support modification of its backing array's size, and so will
     * always throw an {@link UnsupportedOperationException} when this method is invoked.
     *
     * @param obj  the object to add
     * @throws UnsupportedOperationException always thrown.
     */
    public void add(Object obj) {
        throw new UnsupportedOperationException("add() method is not supported");
    }

    /**
     * Sets the element under the cursor.
     * <p>
     * This method sets the element that was returned by the last call 
     * to {@link #next()} of {@link #previous()}. 
     * 
     * <b>Note:</b> {@link ListIterator} implementations that support <code>add()</code>
     * and <code>remove()</code> only allow <code>set()</code> to be called once per call 
     * to <code>next()</code> or <code>previous</code> (see the {@link ListIterator}
     * javadoc for more details). Since this implementation does not support 
     * <code>add()</code> or <code>remove()</code>, <code>set()</code> may be
     * called as often as desired.
     * 
     * @param obj  the object to set into the array
     * @throws IllegalStateException if next() has not yet been called.
     * @throws ClassCastException if the object type is unsuitable for the array
     */
    public void set(Object obj) {
        if (this.lastItemIndex == -1) {
            throw new IllegalStateException("must call next() or previous() before a call to set()");
        }

        this.array[this.lastItemIndex] = obj;
    }

    /**
     * Resets the iterator back to the start index.
     */
    public void reset() {
        super.reset();
        this.lastItemIndex = -1;
    }

}
