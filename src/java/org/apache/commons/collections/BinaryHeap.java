/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/BinaryHeap.java,v 1.10 2002/08/15 20:04:31 pjack Exp $
 * $Revision: 1.10 $
 * $Date: 2002/08/15 20:04:31 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
import java.util.Comparator;

/**
 * Binary heap implementation of {@link PriorityQueue} and {@link Buffer}.
 *
 * The removal order of a binary heap is based on either the natural sort
 * order of its elements or a specified {@link Comparator}.  The 
 * {@link #remove()} method always returns the first element as determined
 * by the sort order.  (The <Code>isMinHeap</Code> flag in the constructors
 * can be used to reverse the sort order, in which case {@link #remove()}
 * will always remove the last element.)  The removal order is 
 * <I>not</I> the same as the order of iteration; elements are
 * returned by the iterator in no particular order.<P>
 *
 * The {@link #add(Object)} and {@link #remove()} operations perform
 * in logarithmic time.  The {@link #get()} operation performs in constant
 * time.  All other operations perform in linear time or worse.<P>
 *
 * Note that this implementation is not synchronized.  Use 
 * {@link BufferUtils#synchronizedBuffer(Buffer)} to provide
 * synchronized access to a <Code>BinaryHeap</Code>:
 *
 * <Pre>
 * Buffer heap = BufferUtils.synchronizedBuffer(new BinaryHeap());
 * </Pre>
 *
 * @since 1.0
 * @author  <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author  <a href="mailto:ram.chidambaram@telus.com">Ram Chidambaram</a>
 * @author  <a href="mailto:mas@apache.org">Michael A. Smith</a>
 * @author  Paul Jack
 */
public final class BinaryHeap extends AbstractCollection
    implements PriorityQueue, Buffer
{

    /**
     *  The default capacity for a binary heap.
     */
    protected final static int      DEFAULT_CAPACITY   = 13;

    /**
     *  The number of elements currently in this heap.
     */
    protected int                   m_size;

    /**
     *  The elements in this heap.
     */
    protected Object[]              m_elements;

    /**
     *  If true, the first element as determined by the sort order will 
     *  be returned.  If false, the last element as determiend by the
     *  sort order will be returned.
     */
    protected boolean               m_isMinHeap;
    private Comparator              m_comparator;

    /**
     *  Create a new minimum binary heap.
     */
    public BinaryHeap()
    {
        this( DEFAULT_CAPACITY, true );
    }

    /**
     *  Constructs a new <Code>BinaryHeap</Code> that will use the given
     *  comparator to order its elements.
     */
    public BinaryHeap( Comparator comparator )
    {
        this();
        m_comparator = comparator;
    }

    /**
     *  Create a new minimum binary heap with the specified initial capacity.
     *  
     *  @param capacity the initial capacity for the heap.  This value must
     *  be greater than zero.
     *
     *  @exception IllegalArgumentException 
     *   if <code>capacity</code> is &lt;= <code>0</code>
     **/
    public BinaryHeap( final int capacity )
    {
        this( capacity, true );
    }

    /**
     *  Constructs a new <Code>BinaryHeap</Code>.
     *
     *  @param capacity  the initial capacity for the heap
     *  @param comparator  the comparator to use to order elements
     *  @exception IllegalArgumentException 
     *   if <code>capacity</code> is <code>&lt;= 0</code>
     */
    public BinaryHeap( final int capacity, Comparator comparator )
    {
        this( capacity );
        m_comparator = comparator;
    }

    /**
     *  Create a new minimum or maximum binary heap
     *
     *  @param isMinHeap if <code>true</code> the heap is created as a 
     *  minimum heap; otherwise, the heap is created as a maximum heap.
     **/
    public BinaryHeap( final boolean isMinHeap )
    {
        this( DEFAULT_CAPACITY, isMinHeap );
    }

    /**
     *  Constructs a new <Code>BinaryHeap</Code>.
     *
     *  @param isMinHeap  true to use the order imposed by the given 
     *    comparator; false to reverse that order
     *  @param comparator  the comparator to use to order elements
     */
    public BinaryHeap( final boolean isMinHeap, Comparator comparator )
    {
        this( isMinHeap );
        m_comparator = comparator;
    }

    /**
     *  Create a new minimum or maximum binary heap with the specified 
     *  initial capacity.
     *
     *  @param capacity the initial capacity for the heap.  This value must 
     *  be greater than zero.
     *
     *  @param isMinHeap if <code>true</code> the heap is created as a 
     *  minimum heap; otherwise, the heap is created as a maximum heap.
     *
     *  @exception IllegalArgumentException 
     *   if <code>capacity</code> is <code>&lt;= 0</code>
     **/
    public BinaryHeap( final int capacity, final boolean isMinHeap )
    {
        if( capacity <= 0 ) {
            throw new IllegalArgumentException( "invalid capacity" );
        }
        m_isMinHeap = isMinHeap;

        //+1 as 0 is noop
        m_elements = new Object[ capacity + 1 ];
    }

    /**
     *  Constructs a new <Code>BinaryHeap</Code>.
     *
     *  @param capacity  the initial capacity for the heap
     *  @param isMinHeap  true to use the order imposed by the given 
     *    comparator; false to reverse that order
     *  @param comparator  the comparator to use to order elements
     *  @exception IllegalArgumentException 
     *   if <code>capacity</code> is <code>&lt;= 0</code>
     */
    public BinaryHeap( final int capacity, final boolean isMinHeap,
                       Comparator comparator ) 
    {
        this( capacity, isMinHeap );
        m_comparator = comparator;
    }

    /**
     * Clear all elements from queue.
     */
    public void clear()
    {
        m_size = 0;
    }

    /**
     * Test if queue is empty.
     *
     * @return <code>true</code> if queue is empty; <code>false</code> 
     * otherwise.
     */
    public boolean isEmpty()
    {
        return ( 0 == m_size );
    }

    /**
     * Test if queue is full.
     *
     * @return <code>true</code> if queue is full; <code>false</code>
     * otherwise.
     */
    public boolean isFull()
    {
        //+1 as element 0 is noop
        return ( m_elements.length == m_size+1 );
    }

    /**
     * Insert an element into queue.
     *
     * @param element the element to be inserted
     */
    public void insert( final Object element )
    {
        if( isFull() ) grow();

        //percolate element to it's place in tree
        if( m_isMinHeap ) percolateUpMinHeap( element );
        else percolateUpMaxHeap( element );
    }

    /**
     * Return element on top of heap but don't remove it.
     *
     * @return the element at top of heap
     * @exception NoSuchElementException if <code>isEmpty() == true</code>
     */
    public Object peek() throws NoSuchElementException
    {
        if( isEmpty() ) throw new NoSuchElementException();
        else return m_elements[ 1 ];
    }

    /**
     * Return element on top of heap and remove it.
     *
     * @return the element at top of heap
     * @exception NoSuchElementException if <code>isEmpty() == true</code>
     */
    public Object pop() throws NoSuchElementException
    {
        final Object result = peek();
        m_elements[ 1 ] = m_elements[ m_size-- ];

        //set the unused element to 'null' so that the garbage collector
        //can free the object if not used anywhere else.(remove reference)
        m_elements[ m_size + 1 ] = null;

        if( m_size != 0 )
        {
            //percolate top element to it's place in tree
            if( m_isMinHeap ) percolateDownMinHeap( 1 );
            else percolateDownMaxHeap( 1 );
        }

        return result;
    }

    /**
     * Percolate element down heap from top.
     * Assume it is a maximum heap.
     *
     * @param index the index for the element
     */
    protected void percolateDownMinHeap( final int index )
    {
        final Object element = m_elements[ index ];

        int hole = index;

        while( (hole * 2) <= m_size )
        {
            int child = hole * 2;

            //if we have a right child and that child can not be percolated
            //up then move onto other child
            if( child != m_size && 
                compare( m_elements[ child + 1 ], m_elements[ child ] ) < 0 )
            {
                child++;
            }

            //if we found resting place of bubble then terminate search
            if( compare( m_elements[ child ], element ) >= 0 )
            {
                break;
            }

            m_elements[ hole ] = m_elements[ child ];
            hole = child;
        }

        m_elements[ hole ] = element;
    }

    /**
     * Percolate element down heap from top.
     * Assume it is a maximum heap.
     *
     * @param index the index of the element
     */
    protected void percolateDownMaxHeap( final int index )
    {
        final Object element = m_elements[ index ];

        int hole = index;

        while( (hole * 2) <= m_size )
        {
            int child = hole * 2;

            //if we have a right child and that child can not be percolated
            //up then move onto other child
            if( child != m_size &&
                compare( m_elements[ child + 1 ], m_elements[ child ] ) > 0 )
            {
                child++;
            }

            //if we found resting place of bubble then terminate search
            if( compare( m_elements[ child ], element ) <= 0 )
            {
                break;
            }

            m_elements[ hole ] = m_elements[ child ];
            hole = child;
        }

        m_elements[ hole ] = element;
    }

    /**
     * Percolate element up heap from bottom.
     * Assume it is a maximum heap.
     *
     * @param element the element
     */
    protected void percolateUpMinHeap( final Object element )
    {
        int hole = ++m_size;

        m_elements[ hole ] = element;

        while( hole > 1 &&
               compare( element,  m_elements[ hole / 2 ] ) < 0 )
        {
            //save element that is being pushed down
            //as the element "bubble" is percolated up
            final int next = hole / 2;
            m_elements[ hole ] = m_elements[ next ];
            hole = next;
        }

        m_elements[ hole ] = element;
    }

    /**
     * Percolate element up heap from bottom.
     * Assume it is a maximum heap.
     *
     * @param element the element
     */
    protected void percolateUpMaxHeap( final Object element )
    {
        int hole = ++m_size;

        while( hole > 1 &&
               compare( element, m_elements[ hole / 2 ] ) > 0 )
        {
            //save element that is being pushed down
            //as the element "bubble" is percolated up
            final int next = hole / 2;
            m_elements[ hole ] = m_elements[ next ];
            hole = next;
        }

        m_elements[ hole ] = element;
    }

    private int compare(Object a, Object b) {
        if(m_comparator != null) {
            return m_comparator.compare(a, b);
        } else {
            return ((Comparable)a).compareTo(b);
        }
    }

    /**
     *  Increase the size of the heap to support additional elements
     **/
    protected void grow()
    {
        final Object[] elements = new Object[ m_elements.length * 2 ];
        System.arraycopy( m_elements, 0, elements, 0, m_elements.length );
        m_elements = elements;
    }

    /**
     *  Returns a string representation of this heap.  The returned string
     *  is similar to those produced by standard JDK collections.
     *
     *  @return  a string representation of this heap
     */
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();

        sb.append( "[ " );

        for( int i = 1; i < m_size + 1; i++ )
        {
            if( i != 1 ) sb.append( ", " );
            sb.append( m_elements[ i ] );
        }

        sb.append( " ]" );

        return sb.toString();
    }


    /**
     *  Returns an iterator over this heap's elements.
     *
     *  @return an iterator over this heap's elements
     */
    public Iterator iterator() {
        return new Iterator() {

            private int index = 1;
            private int lastReturnedIndex = -1;

            public boolean hasNext() {
                return index <= m_size;
            }

            public Object next() {
                if (!hasNext()) throw new NoSuchElementException();
                lastReturnedIndex = index;
                index++;
                return m_elements[lastReturnedIndex];
            }

            public void remove() {
                if (lastReturnedIndex == -1) throw new IllegalStateException();
                m_elements[ lastReturnedIndex ] = m_elements[ m_size ];
                m_elements[ m_size ] = null;
                m_size--;
                if( m_size != 0 )
                {
                    //percolate top element to it's place in tree
                    if( m_isMinHeap ) percolateDownMinHeap( lastReturnedIndex );
                    else percolateDownMaxHeap( lastReturnedIndex );
                }
                index--;
                lastReturnedIndex = -1;        
            }

        };
    }


    /**
     *  Adds an object to this heap.  Same as {@link #insert(Object)}.
     *
     *  @param o  the object to add
     *  @return true, always
     */
    public boolean add(Object o) {
        insert(o);
        return true;
    }


    /**
     *  Returns the priority element.  Same as {@link #peek()}.
     *
     *  @return the priority element
     *  @throws BufferUnderflowException if this heap is empty
     */
    public Object get() {
        try {
            return peek();
        } catch (NoSuchElementException e) {
            throw new BufferUnderflowException();
        }
    }


    /**
     *  Removes the priority element.  Same as {@link #pop()}.
     *
     *  @return the removed priority element
     *  @throws BufferUnderflowException if this heap is empty
     */
    public Object remove() {
        try {
            return pop();
        } catch (NoSuchElementException e) {
            throw new BufferUnderflowException();
        }
    }


    /**
     *  Returns the number of elements in this heap.
     *
     *  @return the number of elements in this heap
     */
    public int size() {
        return m_size;
    }

    /**
     *  Used by testing code.
     *
     *  @return  the otherwise private comparator
     */
    Comparator comparator() {
        return m_comparator;
    }
}

