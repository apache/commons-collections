/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.commons.collections;


import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * The BoundedFifoBuffer is a <strong>very</strong> efficient implementation of
 * Buffer that does not alter the size of the buffer at runtime.<P>
 *
 * The removal order of a <Code>BoundedFifoBuffer</Code> is based on the 
 * insertion order; elements are removed in the same order in which they
 * were added.  The iteration order is the same as the removal order.<P>
 *
 * The {@link add(Object}, {@link remove()} and {@link get()} operations
 * all perform in constant time.  All other operations perform in linear
 * time or worse.
 *
 * Note that this implementation is not synchronized.  The following can be
 * used to provide synchronized access to your <COde>BoundedFifoBuffer</Code>:
 *
 * <Pre>
 *   Buffer fifo = BufferUtils.synchronizedBuffer(new BoundedFifoBuffer());
 * </Pre>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author Paul Jack
 * @version $Id: BoundedFifoBuffer.java,v 1.1 2002/07/03 01:57:08 mas Exp $
 */
public class BoundedFifoBuffer extends AbstractCollection implements Buffer
{
    private final Object[] m_elements;
    private int m_start = 0;
    private int m_end = 0;
    private boolean m_full = false;


    /**
     *  Constructs a new <Code>BoundedFifoBuffer</Code> big enough to hold
     *  the specified number of elements.
     *
     *  @param size  the maximum number of elements for this fifo
     */
    public BoundedFifoBuffer( int size )
    {
        m_elements = new Object[ size ];
    }


    /**
     *  Constructs a new <Code>BoundedFifoBuffer</Code> big enough to hold
     *  32 elements.
     */
    public BoundedFifoBuffer()
    {
        this( 32 );
    }


    /**
     *  Constructs a new <Code>BoundedFifoBuffer</Code> big enough to hold all
     *  of the elements in the specified collection.  That collection's
     *  elements will also be added to the fifo.
     *
     *  @param c  the collection whose elements to add
     */
    public BoundedFifoBuffer(Collection c) {
        this(c.size());
        addAll(c);
    }


    /**
     *  Returns this fifo's size.
     *
     *  @return this fifo's size
     */
    public int size()
    {
        int size = 0;

        if( m_end < m_start )
        {
            size = m_elements.length - m_start + m_end;
        }
        else if( m_end == m_start )
        {
            size = ( m_full ? m_elements.length : 0 );
        }
        else
        {
            size = m_end - m_start;
        }

        return size;
    }


    /**
     *  Returns true if this fifo is empty; false otherwise.
     *
     *  @return true if this fifo is empty
     */
    public boolean isEmpty()
    {
        return size() == 0;
    }


    /**
     *  Adds the given element to this fifo.
     *
     *  @param element  the element to add
     *  @return true, always
     *  @throws NullPointerException if the given element is null
     *  @throws BufferOverflowException if this fifo is full
     */
    public boolean add( Object element )
    {
        if( null == element )
        {
            throw new NullPointerException( "Attempted to add null object to buffer" );
        }

        if( m_full )
        {
            throw new BufferOverflowException( "The buffer cannot hold more than "
                                               + m_elements.length + " objects." );
        }

        m_elements[ m_end++ ] = element;

        if( m_end >= m_elements.length )
        {
            m_end = 0;
        }

        if( m_end == m_start )
        {
            m_full = true;
        }

        return true;
    }


    /**
     *  Returns the least recently inserted element in this fifo.
     *
     *  @return the least recently inserted element
     *  @throws BufferUnderflowException if the fifo is empty
     */
    public Object get() {
        if( isEmpty() )
        {
            throw new BufferUnderflowException( "The buffer is already empty" );
        }

        return m_elements[ m_start ];
    }


    /**
     *  Removes the least recently inserted element from this fifo.
     *
     *  @return the least recently inserted element
     *  @throws BufferUnderflowException if the fifo is empty
     */
    public Object remove()
    {
        if( isEmpty() )
        {
            throw new BufferUnderflowException( "The buffer is already empty" );
        }

        Object element = m_elements[ m_start ];

        if( null != element )
        {
            m_elements[ m_start++ ] = null;

            if( m_start >= m_elements.length )
            {
                m_start = 0;
            }

            m_full = false;
        }

        return element;
    }


    private int increment(int index) {
        index++; 
        if (index >= m_elements.length) index = 0;
        return index;
    }


    private int decrement(int index) {
        index--;
        if (index < 0) index = m_elements.length - 1;
        return index;
    }


    /**
     *  Returns an iterator over this fifo's elements.
     *
     *  @return an iterator over this fifo's elements
     */
    public Iterator iterator() {
        return new Iterator() {

            private int index = m_start;
            private int lastReturnedIndex = -1;
            private boolean isFirst = m_full;

            public boolean hasNext() {
                return isFirst || (index != m_end);
                
            }

            public Object next() {
                if (!hasNext()) throw new NoSuchElementException();
                isFirst = false;
                lastReturnedIndex = index;
                index = increment(index);
                return m_elements[lastReturnedIndex];
            }

            public void remove() {
                if (lastReturnedIndex == -1) throw new IllegalStateException();

                // First element can be removed quickly
                if (lastReturnedIndex == m_start) {
                    BoundedFifoBuffer.this.remove();
                    lastReturnedIndex = -1;
                    return;
                }

                // Other elements require us to shift the subsequent elements
                int i = lastReturnedIndex + 1;
                while (i != m_end) {
                    if (i >= m_elements.length) {
                        m_elements[i - 1] = m_elements[0];
                        i = 0;
                    } else {
                        m_elements[i - 1] = m_elements[i];
                        i++;
                    }
                }

                lastReturnedIndex = -1;
                m_end = decrement(m_end);
                m_elements[m_end] = null;
                m_full = false;
                index = decrement(index);
            }

        };
    }


    /**
     *  Clears this fifo.
     */
    public void clear() {
        m_full = false;
        m_start = 0;
        m_end = 0;
        Arrays.fill(m_elements, null);
    }

}
