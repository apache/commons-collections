/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/buffer/BlockingBuffer.java,v 1.1 2003/11/16 00:05:44 scolebourne Exp $
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

import java.util.Collection;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUnderflowException;

/**
 * Decorates another <code>Buffer</code> to make {@link #get()} and
 * {@link #remove()} block when the <code>Buffer</code> is empty.
 * <p>
 * If either <code>get</code> or <code>remove</code> is called on an empty
 * <code>Buffer</code>, the calling thread waits for notification that
 * an {@link #add} or {@link #addAll} operation has completed.
 * <p>
 * When one or more entries are added to an empty <code>Buffer</code>,
 * all threads blocked in <code>get</code> or <code>remove</code> are notified.
 * There is no guarantee that concurrent blocked <code>get</code> or 
 * <code>remove</code> requests will be "unblocked" and receive data in the 
 * order that they arrive.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/11/16 00:05:44 $
 * 
 * @author Stephen Colebourne
 * @author Janek Bogucki
 * @author Phil Steitz
 */
public class BlockingBuffer extends SynchronizedBuffer {
    
    /**
     * Factory method to create a blocking buffer.
     * 
     * @param buffer  the buffer to decorate, must not be null
     * @throws IllegalArgumentException if buffer is null
     */
    public static Buffer decorate(Buffer buffer) {
        return new BlockingBuffer(buffer);
    }

    //-----------------------------------------------------------------------    
    /**
     * Constructor that wraps (not copies).
     * 
     * @param buffer  the buffer to decorate, must not be null
     * @throws IllegalArgumentException if the buffer is null
     */
    protected BlockingBuffer(Buffer buffer) {
        super(buffer);
    }

    //-----------------------------------------------------------------------
    public boolean add(Object o) {
        synchronized (lock) {
            boolean result = collection.add(o);
            notifyAll();
            return result;
        }
    }
    
    public boolean addAll(Collection c) {
        synchronized (lock) {
            boolean result = collection.addAll(c);
            notifyAll();
            return result;
        }
    }
    
    public Object get() {
        synchronized (lock) {
            while (collection.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new BufferUnderflowException();
                }
            }
            return getBuffer().get();
        }
    }
    
    public Object remove() {
        synchronized (lock) {
            while (collection.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new BufferUnderflowException();
                }
            }
            return getBuffer().remove();
        }
    }
    
}
