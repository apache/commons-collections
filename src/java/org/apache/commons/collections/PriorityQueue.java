/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import java.util.NoSuchElementException;

/**
 * Interface for priority queues.
 * This interface does not dictate whether it is min or max heap.
 *
 * @since 1.0
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
     *
     * @exception ClassCastException if the specified <code>element</code>'s
     * type prevents it from being compared to other items in the queue to
     * determine its relative priority.  
     */
    void insert( Object element );

    /**
     * Return element on top of heap but don't remove it.
     *
     * @return the element at top of heap
     * @exception NoSuchElementException if <code>isEmpty() == true</code>
     */
    Object peek() throws NoSuchElementException;

    /**
     * Return element on top of heap and remove it.
     *
     * @return the element at top of heap
     * @exception NoSuchElementException if <code>isEmpty() == true</code>
     */
    Object pop() throws NoSuchElementException;
}

