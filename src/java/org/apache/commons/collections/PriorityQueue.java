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
 * Interface for priority queues.
 * This interface does not dictate whether it is min or max heap.
 *
 * @author  <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface PriorityQueue
{
    /**
     * Clear all elements from queue.
     */
    void clear();

    /**
     * Test if queue is empty.
     *
     * @return true if queue is empty else false.
     */
    boolean isEmpty();

    /**
     * Insert an element into queue.
     *
     * @param element the element to be inserted
     */
    void insert( Comparable element );

    /**
     * Return element on top of heap but don't remove it.
     *
     * @return the element at top of heap
     * @exception NoSuchElementException if isEmpty() == true
     */
    Comparable peek() throws NoSuchElementException;

    /**
     * Return element on top of heap and remove it.
     *
     * @return the element at top of heap
     * @exception NoSuchElementException if isEmpty() == true
     */
    Comparable pop() throws NoSuchElementException;
}

