/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/BinaryHeap.java,v 1.3 2002/02/10 08:07:42 jstrachan Exp $
 * $Revision: 1.3 $
 * $Date: 2002/02/10 08:07:42 $
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

import java.util.NoSuchElementException;

/**
 * Binary heap implementation of {@link PriorityQueue}.
 *
 * @author  <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author  <a href="mailto:ram.chidambaram@telus.com">Ram Chidambaram</a>
 */
public final class BinaryHeap
    implements PriorityQueue
{
    protected final static int      DEFAULT_CAPACITY   = 13;

    protected int                   m_size;
    protected Comparable[]          m_elements;
    protected boolean               m_isMinHeap;

    public BinaryHeap()
    {
        this( DEFAULT_CAPACITY, true );
    }

    public BinaryHeap( final int capacity )
    {
        this( capacity, true );
    }

    public BinaryHeap( final boolean isMinHeap )
    {
        this( DEFAULT_CAPACITY, isMinHeap );
    }

    public BinaryHeap( final int capacity, final boolean isMinHeap )
    {
        m_isMinHeap = isMinHeap;

        //+1 as 0 is noop
        m_elements = new Comparable[ capacity + 1 ];
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
     * @return true if queue is empty else false.
     */
    public boolean isEmpty()
    {
        return ( 0 == m_size );
    }

    /**
     * Test if queue is full.
     *
     * @return true if queue is full else false.
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
    public void insert( final Comparable element )
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
     * @exception NoSuchElementException if isEmpty() == true
     */
    public Comparable peek() throws NoSuchElementException
    {
        if( isEmpty() ) throw new NoSuchElementException();
        else return m_elements[ 1 ];
    }

    /**
     * Return element on top of heap and remove it.
     *
     * @return the element at top of heap
     * @exception NoSuchElementException if isEmpty() == true
     */
    public Comparable pop() throws NoSuchElementException
    {
        final Comparable result = peek();
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
     * @param element the element
     */
    protected void percolateDownMinHeap( final int index )
    {
        final Comparable element = m_elements[ index ];

        int hole = index;

        while( (hole * 2) <= m_size )
        {
            int child = hole * 2;

            //if we have a right child and that child can not be percolated
            //up then move onto other child
            if( child != m_size &&
                m_elements[ child + 1 ].compareTo( m_elements[ child ] ) < 0 )
            {
                child++;
            }

            //if we found resting place of bubble then terminate search
            if( m_elements[ child ].compareTo( element ) >= 0 )
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
     * @param element the element
     */
    protected void percolateDownMaxHeap( final int index )
    {
        final Comparable element = m_elements[ index ];

        int hole = index;

        while( (hole * 2) <= m_size )
        {
            int child = hole * 2;

            //if we have a right child and that child can not be percolated
            //up then move onto other child
            if( child != m_size &&
                m_elements[ child + 1 ].compareTo( m_elements[ child ] ) > 0 )
            {
                child++;
            }

            //if we found resting place of bubble then terminate search
            if( m_elements[ child ].compareTo( element ) <= 0 )
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
    protected void percolateUpMinHeap( final Comparable element )
    {
        int hole = ++m_size;

        m_elements[ hole ] = element;

        while( hole > 1 &&
               element.compareTo( m_elements[ hole / 2 ] ) < 0 )
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
    protected void percolateUpMaxHeap( final Comparable element )
    {
        int hole = ++m_size;

        while( hole > 1 &&
               element.compareTo( m_elements[ hole / 2 ] ) > 0 )
        {
            //save element that is being pushed down
            //as the element "bubble" is percolated up
            final int next = hole / 2;
            m_elements[ hole ] = m_elements[ next ];
            hole = next;
        }

        m_elements[ hole ] = element;
    }

    protected void grow()
    {
        final Comparable[] elements =
            new Comparable[ m_elements.length * 2 ];
        System.arraycopy( m_elements, 0, elements, 0, m_elements.length );
        m_elements = elements;
    }

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
}

