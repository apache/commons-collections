/* 
 * Copyright (C) The Apache Software Foundation. All rights reserved. 
 * 
 * This software is published under the terms of the Apache Software License 
 * version 1.1, a copy of which has been included with this distribution in 
 * the LICENSE file. 
 */ 
package org.apache.commons.collections;

import java.util.NoSuchElementException;

/**
 * A thread safe version of the PriorityQueue.
 * Provides synchronized wrapper methods for all the methods 
 * defined in the PriorityQueue interface.
 *
 * @author  <a href="mailto:ram.chidambaram@telus.com">Ram Chidambaram</a> 
 */
public final class SynchronizedPriorityQueue 
    implements PriorityQueue
{
    protected final PriorityQueue   m_priorityQueue;

    public SynchronizedPriorityQueue( final PriorityQueue priorityQueue )
    {
        m_priorityQueue = priorityQueue;
    }

    /**
     * Clear all elements from queue.
     */
    public synchronized void clear()
    {
        m_priorityQueue.clear();
    }

    /**
     * Test if queue is empty.
     *
     * @return true if queue is empty else false.
     */
    public synchronized boolean isEmpty()
    {
        return m_priorityQueue.isEmpty();
    }

    /**
     * Insert an element into queue.
     *
     * @param element the element to be inserted
     */
    public synchronized void insert( final Comparable element )
    {
        m_priorityQueue.insert( element );
    }

    /**
     * Return element on top of heap but don't remove it.
     *
     * @return the element at top of heap
     * @exception NoSuchElementException if isEmpty() == true
     */
    public synchronized Comparable peek() throws NoSuchElementException
    {
        return m_priorityQueue.peek();
    }

    /**
     * Return element on top of heap and remove it.
     *
     * @return the element at top of heap
     * @exception NoSuchElementException if isEmpty() == true
     */
    public synchronized Comparable pop() throws NoSuchElementException
    {
        return m_priorityQueue.pop();
    }

    public synchronized String toString()
    {
        return m_priorityQueue.toString();
    }
}
