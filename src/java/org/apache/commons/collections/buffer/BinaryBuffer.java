/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/buffer/Attic/BinaryBuffer.java,v 1.2 2004/01/01 23:56:51 psteitz Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUnderflowException;

/**
 * Binary heap implementation of <code>Buffer</code> that provides for
 * removal based on <code>Comparator</code> ordering.
 * <p>
 * The removal order of a binary heap is based on either the natural sort
 * order of its elements or a specified {@link Comparator}.  The 
 * {@link #remove()} method always returns the first element as determined
 * by the sort order.  (The <code>ascendingOrder</code> flag in the constructors
 * can be used to reverse the sort order, in which case {@link #remove()}
 * will always remove the last element.)  The removal order is 
 * <i>not</i> the same as the order of iteration; elements are
 * returned by the iterator in no particular order.
 * <p>
 * The {@link #add(Object)} and {@link #remove()} operations perform
 * in logarithmic time.  The {@link #get()} operation performs in constant
 * time.  All other operations perform in linear time or worse.
 * <p>
 * Note that this implementation is not synchronized.  Use 
 * {@link org.apache.commons.collections.BufferUtils#synchronizedBuffer(Buffer)} or
 * {@link org.apache.commons.collections.buffer.SynchronizedBuffer#decorate(Buffer)}
 * to provide synchronized access to a <code>BinaryBuffer</code>:
 *
 * <pre>
 * Buffer heap = SynchronizedBuffer.decorate(new BinaryBuffer());
 * </pre>
 *
 * @since Commons Collections 3.0 (previously BinaryHeap v1.0)
 * @version $Revision: 1.2 $ $Date: 2004/01/01 23:56:51 $
 * 
 * @author Peter Donald
 * @author Ram Chidambaram
 * @author Michael A. Smith
 * @author Paul Jack
 * @author Stephen Colebourne
 */
public class BinaryBuffer extends AbstractCollection implements Buffer {

    /**
     * The default capacity for a binary heap.
     */
    private static final int DEFAULT_CAPACITY = 13;
    
    /**
     * The elements in this heap.
     */
    protected Object[] elements;
    /**
     * The number of elements currently in this heap.
     */
    protected int size;
    /**
     * If true, the first element as determined by the sort order will 
     * be returned.  If false, the last element as determined by the
     * sort order will be returned.
     */
    protected boolean ascendingOrder;
    /**
     * The comparator used to order the elements
     */
    protected Comparator comparator;

    //-----------------------------------------------------------------------
    /**
     * Constructs a new empty buffer that sorts in ascending order by the
     * natural order of the objects added.
     */
    public BinaryBuffer() {
        this(DEFAULT_CAPACITY, true, null);
    }

    /**
     * Constructs a new empty buffer that sorts in ascending order using the
     * specified comparator.
     * 
     * @param comparator  the comparator used to order the elements,
     *  null means use natural order
     */
    public BinaryBuffer(Comparator comparator) {
        this(DEFAULT_CAPACITY, true, comparator);
    }

    /**
     * Constructs a new empty buffer specifying the sort order and using the
     * natural order of the objects added.
     *
     * @param ascendingOrder  if <code>true</code> the heap is created as a 
     * minimum heap; otherwise, the heap is created as a maximum heap
     */
    public BinaryBuffer(boolean ascendingOrder) {
        this(DEFAULT_CAPACITY, ascendingOrder, null);
    }

    /**
     * Constructs a new empty buffer specifying the sort order and comparator.
     *
     * @param ascendingOrder  true to use the order imposed by the given 
     *   comparator; false to reverse that order
     * @param comparator  the comparator used to order the elements,
     *  null means use natural order
     */
    public BinaryBuffer(boolean ascendingOrder, Comparator comparator) {
        this(DEFAULT_CAPACITY, ascendingOrder, comparator);
    }

    /**
     * Constructs a new empty buffer that sorts in ascending order by the
     * natural order of the objects added, specifying an initial capacity.
     *  
     * @param capacity  the initial capacity for the buffer, greater than zero
     * @throws IllegalArgumentException if <code>capacity</code> is &lt;= <code>0</code>
     */
    public BinaryBuffer(int capacity) {
        this(capacity, true, null);
    }

    /**
     * Constructs a new empty buffer that sorts in ascending order using the
     * specified comparator and initial capacity.
     *
     * @param capacity  the initial capacity for the buffer, greater than zero
     * @param comparator  the comparator used to order the elements,
     *  null means use natural order
     * @throws IllegalArgumentException if <code>capacity</code> is &lt;= <code>0</code>
     */
    public BinaryBuffer(int capacity, Comparator comparator) {
        this(capacity, true, comparator);
    }

    /**
     * Constructs a new empty buffer that specifying initial capacity and
     * sort order, using the natural order of the objects added.
     *
     * @param capacity  the initial capacity for the buffer, greater than zero
     * @param ascendingOrder if <code>true</code> the heap is created as a 
     *  minimum heap; otherwise, the heap is created as a maximum heap.
     * @throws IllegalArgumentException if <code>capacity</code> is <code>&lt;= 0</code>
     */
    public BinaryBuffer(int capacity, boolean ascendingOrder) {
        this(capacity, ascendingOrder, null);
    }

    /**
     * Constructs a new empty buffer that specifying initial capacity,
     * sort order and comparator.
     *
     * @param capacity  the initial capacity for the buffer, greater than zero
     * @param ascendingOrder  true to use the order imposed by the given 
     *   comparator; false to reverse that order
     * @param comparator  the comparator used to order the elements,
     *  null means use natural order
     * @throws IllegalArgumentException if <code>capacity</code> is <code>&lt;= 0</code>
     */
    public BinaryBuffer(int capacity, boolean ascendingOrder, Comparator comparator) {
        super();
        if (capacity <= 0) {
            throw new IllegalArgumentException("invalid capacity");
        }
        this.ascendingOrder = ascendingOrder;

        //+1 as 0 is noop
        this.elements = new Object[capacity + 1];
        this.comparator = comparator;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether the heap is ascending or descending order.
     * 
     * @return true if ascending order (a min heap)
     */
    public boolean isAscendingOrder() {
        return ascendingOrder;
    }
    
    /**
     * Gets the comparator being used for this buffer, null is natural order.
     * 
     * @return the comparator in use, null is natural order
     */
    public Comparator comparator() {
        return comparator;
    }
    
    //-----------------------------------------------------------------------
    /**
     * Returns the number of elements in this buffer.
     *
     * @return the number of elements in this buffer
     */
    public int size() {
        return size;
    }

    /**
     * Clears all elements from the buffer.
     */
    public void clear() {
        elements = new Object[elements.length]; // for gc
        size = 0;
    }

    /**
     * Adds an element to the buffer.
     * <p>
     * The element added will be sorted according to the comparator in use.
     *
     * @param element  the element to be added
     */
    public boolean add(Object element) {
        if (isAtCapacity()) {
            grow();
        }
        // percolate element to it's place in tree
        if (ascendingOrder) {
            percolateUpMinHeap(element);
        } else {
            percolateUpMaxHeap(element);
        }
        return true;
    }

    /**
     * Gets the next element to be removed without actually removing it (peek).
     *
     * @return the next element
     * @throws BufferUnderflowException if the buffer is empty
     */
    public Object get() {
        if (isEmpty()) {
            throw new BufferUnderflowException();
        } else {
            return elements[1];
        }
    }

    /**
     * Gets and removes the next element (pop).
     *
     * @return the next element
     * @throws BufferUnderflowException if the buffer is empty
     */
    public Object remove() {
        final Object result = get();
        elements[1] = elements[size--];

        // set the unused element to 'null' so that the garbage collector
        // can free the object if not used anywhere else.(remove reference)
        elements[size + 1] = null;

        if (size != 0) {
            // percolate top element to it's place in tree
            if (ascendingOrder) {
                percolateDownMinHeap(1);
            } else {
                percolateDownMaxHeap(1);
            }
        }

        return result;
    }

    //-----------------------------------------------------------------------
    /**
     * Tests if the buffer is at capacity.
     *
     * @return <code>true</code> if buffer is full; <code>false</code> otherwise.
     */
    protected boolean isAtCapacity() {
        //+1 as element 0 is noop
        return elements.length == size + 1;
    }

    
    /**
     * Percolates element down heap from the position given by the index.
     * <p>
     * Assumes it is a mimimum heap.
     *
     * @param index the index for the element
     */
    protected void percolateDownMinHeap(final int index) {
        final Object element = elements[index];
        int hole = index;

        while ((hole * 2) <= size) {
            int child = hole * 2;

            // if we have a right child and that child can not be percolated
            // up then move onto other child
            if (child != size && compare(elements[child + 1], elements[child]) < 0) {
                child++;
            }

            // if we found resting place of bubble then terminate search
            if (compare(elements[child], element) >= 0) {
                break;
            }

            elements[hole] = elements[child];
            hole = child;
        }

        elements[hole] = element;
    }

    /**
     * Percolates element down heap from the position given by the index.
     * <p>
     * Assumes it is a maximum heap.
     *
     * @param index the index of the element
     */
    protected void percolateDownMaxHeap(final int index) {
        final Object element = elements[index];
        int hole = index;

        while ((hole * 2) <= size) {
            int child = hole * 2;

            // if we have a right child and that child can not be percolated
            // up then move onto other child
            if (child != size && compare(elements[child + 1], elements[child]) > 0) {
                child++;
            }

            // if we found resting place of bubble then terminate search
            if (compare(elements[child], element) <= 0) {
                break;
            }

            elements[hole] = elements[child];
            hole = child;
        }

        elements[hole] = element;
    }

    /**
     * Percolates element up heap from the position given by the index.
     * <p>
     * Assumes it is a minimum heap.
     *
     * @param index the index of the element to be percolated up
     */
    protected void percolateUpMinHeap(final int index) {
        int hole = index;
        Object element = elements[hole];
        while (hole > 1 && compare(element, elements[hole / 2]) < 0) {
            // save element that is being pushed down
            // as the element "bubble" is percolated up
            final int next = hole / 2;
            elements[hole] = elements[next];
            hole = next;
        }
        elements[hole] = element;
    }

    /**
     * Percolates a new element up heap from the bottom.
     * <p>
     * Assumes it is a minimum heap.
     *
     * @param element the element
     */
    protected void percolateUpMinHeap(final Object element) {
        elements[++size] = element;
        percolateUpMinHeap(size);
    }

    /**
     * Percolates element up heap from from the position given by the index.
     * <p>
     * Assume it is a maximum heap.
     *
     * @param element the element
     */
    protected void percolateUpMaxHeap(final int index) {
        int hole = index;
        Object element = elements[hole];

        while (hole > 1 && compare(element, elements[hole / 2]) > 0) {
            // save element that is being pushed down
            // as the element "bubble" is percolated up
            final int next = hole / 2;
            elements[hole] = elements[next];
            hole = next;
        }

        elements[hole] = element;
    }

    /**
     * Percolates a new element up heap from the bottom.
     * <p>
     * Assume it is a maximum heap.
     *
     * @param element the element
     */
    protected void percolateUpMaxHeap(final Object element) {
        elements[++size] = element;
        percolateUpMaxHeap(size);
    }

    /**
     * Compares two objects using the comparator if specified, or the
     * natural order otherwise.
     * 
     * @param a  the first object
     * @param b  the second object
     * @return -ve if a less than b, 0 if they are equal, +ve if a greater than b
     */
    protected int compare(Object a, Object b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        } else {
            return ((Comparable) a).compareTo(b);
        }
    }

    /**
     * Increases the size of the heap to support additional elements
     */
    protected void grow() {
        final Object[] array = new Object[elements.length * 2];
        System.arraycopy(elements, 0, array, 0, elements.length);
        elements = array;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an iterator over this heap's elements.
     *
     * @return an iterator over this heap's elements
     */
    public Iterator iterator() {
        return new Iterator() {

            private int index = 1;
            private int lastReturnedIndex = -1;

            public boolean hasNext() {
                return index <= size;
            }

            public Object next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                lastReturnedIndex = index;
                index++;
                return elements[lastReturnedIndex];
            }

            public void remove() {
                if (lastReturnedIndex == -1) {
                    throw new IllegalStateException();
                }
                elements[ lastReturnedIndex ] = elements[ size ];
                elements[ size ] = null;
                size--;  
                if( size != 0 && lastReturnedIndex <= size) {
                    int compareToParent = 0;
                    if (lastReturnedIndex > 1) {
                        compareToParent = compare(elements[lastReturnedIndex], 
                            elements[lastReturnedIndex / 2]);  
                    }
                    if (ascendingOrder) {
                        if (lastReturnedIndex > 1 && compareToParent < 0) {
                            percolateUpMinHeap(lastReturnedIndex); 
                        } else {
                            percolateDownMinHeap(lastReturnedIndex);
                        }
                    } else {  // max heap
                        if (lastReturnedIndex > 1 && compareToParent > 0) {
                            percolateUpMaxHeap(lastReturnedIndex); 
                        } else {
                            percolateDownMaxHeap(lastReturnedIndex);
                        }
                    }          
                }
                index--;
                lastReturnedIndex = -1; 
            }

        };
    }

    /**
     * Returns a string representation of this heap.  The returned string
     * is similar to those produced by standard JDK collections.
     *
     * @return a string representation of this heap
     */
    public String toString() {
        final StringBuffer sb = new StringBuffer();

        sb.append("[ ");

        for (int i = 1; i < size + 1; i++) {
            if (i != 1) {
                sb.append(", ");
            }
            sb.append(elements[i]);
        }

        sb.append(" ]");

        return sb.toString();
    }

}
