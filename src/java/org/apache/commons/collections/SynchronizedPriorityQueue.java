/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/SynchronizedPriorityQueue.java,v 1.8 2003/08/31 17:26:44 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections;

import java.util.NoSuchElementException;

/**
 * A thread safe version of the PriorityQueue.
 * Provides synchronized wrapper methods for all the methods 
 * defined in the PriorityQueue interface.
 *
 * @deprecated Moved to decorators subpackage. Due to be removed in v4.0.
 * @since Commons Collections 1.0
 * @version $Revision: 1.8 $ $Date: 2003/08/31 17:26:44 $
 * 
 * @author  <a href="mailto:ram.chidambaram@telus.com">Ram Chidambaram</a> 
 */
public final class SynchronizedPriorityQueue implements PriorityQueue {

    /**
     * The underlying priority queue.
     */
    protected final PriorityQueue m_priorityQueue;

    /**
     * Constructs a new synchronized priority queue.
     *
     * @param priorityQueue  the priority queue to synchronize
     */
    public SynchronizedPriorityQueue(final PriorityQueue priorityQueue) {
        m_priorityQueue = priorityQueue;
    }

    /**
     * Clear all elements from queue.
     */
    public synchronized void clear() {
        m_priorityQueue.clear();
    }

    /**
     * Test if queue is empty.
     *
     * @return true if queue is empty else false.
     */
    public synchronized boolean isEmpty() {
        return m_priorityQueue.isEmpty();
    }

    /**
     * Insert an element into queue.
     *
     * @param element the element to be inserted
     */
    public synchronized void insert(final Object element) {
        m_priorityQueue.insert(element);
    }

    /**
     * Return element on top of heap but don't remove it.
     *
     * @return the element at top of heap
     * @throws NoSuchElementException if isEmpty() == true
     */
    public synchronized Object peek() throws NoSuchElementException {
        return m_priorityQueue.peek();
    }

    /**
     * Return element on top of heap and remove it.
     *
     * @return the element at top of heap
     * @throws NoSuchElementException if isEmpty() == true
     */
    public synchronized Object pop() throws NoSuchElementException {
        return m_priorityQueue.pop();
    }

    /**
     * Returns a string representation of the underlying queue.
     *
     * @return a string representation of the underlying queue
     */
    public synchronized String toString() {
        return m_priorityQueue.toString();
    }
    
}
