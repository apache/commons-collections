/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/iterators/ArrayIterator.java,v 1.6 2003/09/29 22:37:40 scolebourne Exp $
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
package org.apache.commons.collections.iterators;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;

/** 
 * Implements an {@link java.util.Iterator Iterator} over any array.
 * <p>
 * The array can be either an array of object or of primitives. If you know 
 * that you have an object array, the 
 * {@link org.apache.commons.collections.iterators.ObjectArrayIterator ObjectArrayIterator}
 * class is a better choice, as it will perform better.
 * <p>
 * The iterator implements a {@link #reset} method, allowing the reset of 
 * the iterator back to the start if required.
 *
 * @since Commons Collections 1.0
 * @version $Revision: 1.6 $ $Date: 2003/09/29 22:37:40 $
 *
 * @author James Strachan
 * @author Mauricio S. Moura
 * @author Michael A. Smith
 * @author Neil O'Toole
 * @author Stephen Colebourne
 */
public class ArrayIterator implements ResetableIterator {

    /** The array to iterate over */    
    protected Object array;
    /** The start index to loop from */
    protected int startIndex = 0;
    /** The end index to loop to */
	protected int endIndex = 0;
    /** The current iterator index */
	protected int index = 0;
    
    // Constructors
    // ----------------------------------------------------------------------
    /**
     * Constructor for use with <code>setArray</code>.
     * <p>
     * Using this constructor, the iterator is equivalent to an empty iterator
     * until {@link #setArray(Object)} is  called to establish the array to iterate over.
     */
    public ArrayIterator() {
        super();
    }
   
    /**
     * Constructs an ArrayIterator that will iterate over the values in the
     * specified array.
     *
     * @param array the array to iterate over.
     * @throws IllegalArgumentException if <code>array</code> is not an array.
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     */
    public ArrayIterator(final Object array) {
        super();
        setArray(array);
    }

    /**
     * Constructs an ArrayIterator that will iterate over the values in the
     * specified array from a specific start index.
     *
     * @param array  the array to iterate over.
     * @param startIndex  the index to start iterating at.
     * @throws IllegalArgumentException if <code>array</code> is not an array.
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public ArrayIterator(final Object array, final int startIndex) {
        super();
        setArray(array);
        checkBound(startIndex, "start");
        this.startIndex = startIndex;
        this.index = startIndex;
    }

    /**
     * Construct an ArrayIterator that will iterate over a range of values 
     * in the specified array.
     *
     * @param array  the array to iterate over.
     * @param startIndex  the index to start iterating at.
     * @param endIndex  the index to finish iterating at.
     * @throws IllegalArgumentException if <code>array</code> is not an array.
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     * @throws IndexOutOfBoundsException if either index is invalid
     */
    public ArrayIterator(final Object array, final int startIndex, final int endIndex) {
        super();
        setArray(array);
        checkBound(startIndex, "start");
        checkBound(endIndex, "end");
        if (endIndex < startIndex) {
            throw new IllegalArgumentException("End index must not be less than start index.");
        }
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.index = startIndex;
    }

    /**
     * Checks whether the index is valid or not.
     * 
     * @param bound  the index to check
     * @param type  the index type (for error messges)
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    protected void checkBound(final int bound, final String type ) {
        if (bound > this.endIndex) {
            throw new ArrayIndexOutOfBoundsException(
              "Attempt to make an ArrayIterator that " + type +
              "s beyond the end of the array. "
            );
        }
        if (bound < 0) {
            throw new ArrayIndexOutOfBoundsException(
              "Attempt to make an ArrayIterator that " + type +
              "s before the start of the array. "
            );
        }
    }

    // Iterator interface
    //-----------------------------------------------------------------------
    /**
     * Returns true if there are more elements to return from the array.
     *
     * @return true if there is a next element to return
     */
    public boolean hasNext() {
        return (index < endIndex);
    }

    /**
     * Returns the next element in the array.
     *
     * @return the next element in the array
     * @throws NoSuchElementException if all the elements in the array
     *  have already been returned
     */
    public Object next() {
        if (hasNext() == false) {
            throw new NoSuchElementException();
        }
        return Array.get(array, index++);
    }

    /**
     * Throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    public void remove() {
        throw new UnsupportedOperationException("remove() method is not supported");
    }

    // Properties
    //-----------------------------------------------------------------------
    /**
     * Gets the array that this iterator is iterating over. 
     *
     * @return the array this iterator iterates over, or <code>null</code> if
     *  the no-arg constructor was used and {@link #setArray(Object)} has never
     *  been called with a valid array.
     */
    public Object getArray() {
        return array;
    }
    
    /**
     * Sets the array that the ArrayIterator should iterate over.
     * <p>
     * If an array has previously been set (using the single-arg constructor
     * or this method) then that array is discarded in favour of this one.
     * Iteration is restarted at the start of the new array.
     * Although this can be used to reset iteration, the {@link #reset()} method
     * is a more effective choice.
     *
     * @param array the array that the iterator should iterate over.
     * @throws IllegalArgumentException if <code>array</code> is not an array.
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     */
    public void setArray(final Object array) {
        // Array.getLength throws IllegalArgumentException if the object is not
        // an array or NullPointerException if the object is null.  This call
        // is made before saving the array and resetting the index so that the
        // array iterator remains in a consistent state if the argument is not
        // an array or is null.
        this.endIndex = Array.getLength(array);
        this.startIndex = 0;
        this.array = array;
        this.index = 0;
    }
    
    /**
     * Resets the iterator back to the start index.
     */
    public void reset() {
        this.index = this.startIndex;
    }

}
