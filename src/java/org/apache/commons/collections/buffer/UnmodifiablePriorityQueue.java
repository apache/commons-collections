/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/buffer/Attic/UnmodifiablePriorityQueue.java,v 1.3 2003/12/03 12:27:36 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

import java.util.NoSuchElementException;

import org.apache.commons.collections.PriorityQueue;
import org.apache.commons.collections.Unmodifiable;

/**
 * <code>UnmodifiablePriorityQueue</code> decorates another <code>PriorityQueue</code>
 * to ensure it can't be altered.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/12/03 12:27:36 $
 * 
 * @author Stephen Colebourne
 */
public final class UnmodifiablePriorityQueue implements PriorityQueue, Unmodifiable {

    /** The priority queue to decorate */
    protected final PriorityQueue priorityQueue;

    /**
     * Factory method to create an unmodifiable priority queue.
     * 
     * @param priorityQueue  the priority queue to decorate, must not be null
     * @throws IllegalArgumentException if priority queue is null
     */
    public static PriorityQueue decorate(PriorityQueue priorityQueue) {
        if (priorityQueue instanceof Unmodifiable) {
            return priorityQueue;
        }
        if (priorityQueue == null) {
            throw new IllegalArgumentException("PriorityQueue must not be null");
        }
        return new UnmodifiablePriorityQueue(priorityQueue);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructs a new synchronized priority queue.
     *
     * @param priorityQueue  the priority queue to synchronize
     */
    private UnmodifiablePriorityQueue(PriorityQueue priorityQueue) {
        super();
        this.priorityQueue = priorityQueue;
    }

    //-----------------------------------------------------------------------
    /**
     * Clear all elements from queue - Unsupported as unmodifiable.
     */
    public synchronized void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Test if queue is empty.
     *
     * @return true if queue is empty else false.
     */
    public synchronized boolean isEmpty() {
        return priorityQueue.isEmpty();
    }

    /**
     * Insert an element into queue - Unsupported as unmodifiable.
     *
     * @param element the element to be inserted
     */
    public synchronized void insert(Object element) {
        throw new UnsupportedOperationException();
    }

    /**
     * Return element on top of heap but don't remove it.
     *
     * @return the element at top of heap
     * @throws NoSuchElementException if isEmpty() == true
     */
    public synchronized Object peek() throws NoSuchElementException {
        return priorityQueue.peek();
    }

    /**
     * Return element on top of heap and remove it - Unsupported as unmodifiable.
     *
     * @return the element at top of heap
     * @throws NoSuchElementException if isEmpty() == true
     */
    public synchronized Object pop() throws NoSuchElementException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a string representation of the underlying queue.
     *
     * @return a string representation of the underlying queue
     */
    public synchronized String toString() {
        return priorityQueue.toString();
    }
    
}
