/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/BufferUtils.java,v 1.12 2003/05/11 13:29:16 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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

import org.apache.commons.collections.decorators.BlockingBuffer;
import org.apache.commons.collections.decorators.PredicatedBuffer;
import org.apache.commons.collections.decorators.SynchronizedBuffer;
import org.apache.commons.collections.decorators.TransformedBuffer;
import org.apache.commons.collections.decorators.TypedBuffer;
import org.apache.commons.collections.decorators.UnmodifiableBuffer;

/**
 * Contains static utility methods for operating on {@link Buffer} objects.
 *
 * @since Commons Collections 2.1
 * @version $Revision: 1.12 $ $Date: 2003/05/11 13:29:16 $
 * 
 * @author Paul Jack
 * @author Stephen Colebourne
 */
public class BufferUtils {

    /**
     * An empty unmodifiable buffer.
     */
    public static final Buffer EMPTY_BUFFER = UnmodifiableBuffer.decorate(new ArrayStack());
    
    /**
     * <code>BufferUtils</code> should not normally be instantiated.
     */
    public BufferUtils() {
    }

    /**
     * Returns a synchronized buffer backed by the given buffer.
     * Much like the synchronized collections returned by 
     * {@link java.util.Collections}, you must manually synchronize on 
     * the returned buffer's iterator to avoid non-deterministic behavior:
     *  
     * <pre>
     * Buffer b = BufferUtils.synchronizedBuffer(myBuffer);
     * synchronized (b) {
     *     Iterator i = b.iterator();
     *     while (i.hasNext()) {
     *         process (i.next());
     *     }
     * }
     * </pre>
     *
     * @param buffer  the buffer to synchronize, must not be null
     * @return a synchronized buffer backed by that buffer
     * @throws IllegalArgumentException  if the Buffer is null
     */
    public static Buffer synchronizedBuffer(Buffer buffer) {
        return SynchronizedBuffer.decorate(buffer);
    }

    /**
     * Returns a synchronized buffer backed by the given buffer that will
     * block on {@link Buffer#get()} and {@link Buffer#remove()} operations.
     * If the buffer is empty, then the {@link Buffer#get()} and 
     * {@link Buffer#remove()} operations will block until new elements
     * are added to the buffer, rather than immediately throwing a 
     * <code>BufferUnderflowException</code>.
     *
     * @param buffer  the buffer to synchronize, must not be null
     * @return a blocking buffer backed by that buffer
     * @throws IllegalArgumentException  if the Buffer is null
     */
    public static Buffer blockingBuffer(Buffer buffer) {
        return BlockingBuffer.decorate(buffer);
    }

    /**
     * Returns an unmodifiable buffer backed by the given buffer.
     *
     * @param buffer  the buffer to make unmodifiable, must not be null
     * @return an unmodifiable buffer backed by that buffer
     * @throws IllegalArgumentException  if the Buffer is null
     */
    public static Buffer unmodifiableBuffer(Buffer buffer) {
        return UnmodifiableBuffer.decorate(buffer);
    }

    /**
     * Returns a predicated buffer backed by the given buffer.  Elements are
     * evaluated with the given predicate before being added to the buffer.
     * If the predicate evaluation returns false, then an 
     * IllegalArgumentException is raised and the element is not added to
     * the buffer.
     *
     * @param buffer  the buffer to predicate, must not be null
     * @param predicate  the predicate used to evaluate new elements, must not be null
     * @return a predicated buffer
     * @throws IllegalArgumentException  if the Buffer or Predicate is null
     */
    public static Buffer predicatedBuffer(Buffer buffer, Predicate predicate) {
        return PredicatedBuffer.decorate(buffer, predicate);
    }

    /**
     * Returns a typed buffer backed by the given buffer.
     * <p>
     * Only elements of the specified type can be added to the buffer.
     *
     * @param buffer  the buffer to predicate, must not be null
     * @param type  the type to allow into the buffer, must not be null
     * @return a typed buffer
     * @throws IllegalArgumentException  if the buffer or type is null
     */
    public static Buffer typedBuffer(Buffer buffer, Class type) {
        return TypedBuffer.decorate(buffer, type);
    }

    /**
     * Returns a transformed buffer backed by the given buffer.
     * <p>
     * Each object is passed through the transformer as it is added to the
     * Buffer. It is important not to use the original buffer after invoking this 
     * method, as it is a backdoor for adding untransformed objects.
     *
     * @param buffer  the buffer to predicate, must not be null
     * @param transformer  the transformer for the buffer, must not be null
     * @return a transformed buffer backed by the given buffer
     * @throws IllegalArgumentException  if the Buffer or Transformer is null
     */
    public static Buffer transformedBuffer(Buffer buffer, Transformer transformer) {
        return TransformedBuffer.decorate(buffer, transformer);
    }
    
}
