/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/observed/Attic/ObservableBuffer.java,v 1.2 2003/10/09 20:50:04 scolebourne Exp $
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
package org.apache.commons.collections.observed;

import org.apache.commons.collections.Buffer;

/**
 * Decorates a <code>Buffer</code> implementation to observe modifications.
 * <p>
 * Each modifying method call made on this <code>Buffer</code> is forwarded to a
 * {@link ModificationHandler}.
 * The handler manages the event, notifying listeners and optionally vetoing changes.
 * The default handler is
 * {@link org.apache.commons.collections.observed.standard.StandardModificationHandler StandardModificationHandler}.
 * See this class for details of configuration available.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2003/10/09 20:50:04 $
 * 
 * @author Stephen Colebourne
 */
public class ObservableBuffer extends ObservableCollection implements Buffer {
    
    // Factories
    //-----------------------------------------------------------------------
    /**
     * Factory method to create an observable buffer.
     * <p>
     * A {@link StandardModificationHandler} will be created.
     * This can be accessed by {@link #getHandler()} to add listeners.
     *
     * @param buffer  the buffer to decorate, must not be null
     * @return the observed Buffer
     * @throws IllegalArgumentException if the buffer is null
     */
    public static ObservableBuffer decorate(final Buffer buffer) {
        return new ObservableBuffer(buffer, null);
    }

    /**
     * Factory method to create an observable buffer using a listener or a handler.
     * <p>
     * A lot of functionality is available through this method.
     * If you don't need the extra functionality, simply implement the
     * {@link org.apache.commons.collections.observed.standard.StandardModificationListener}
     * interface and pass it in as the second parameter.
     * <p>
     * Internally, an <code>ObservableBuffer</code> relies on a {@link ModificationHandler}.
     * The handler receives all the events and processes them, typically by
     * calling listeners. Different handler implementations can be plugged in
     * to provide a flexible event system.
     * <p>
     * The handler implementation is determined by the listener parameter via
     * the registered factories. The listener may be a manually configured 
     * <code>ModificationHandler</code> instance.
     * <p>
     * The listener is defined as an Object for maximum flexibility.
     * It does not have to be a listener in the classic JavaBean sense.
     * It is entirely up to the factory and handler as to how the parameter
     * is interpretted. An IllegalArgumentException is thrown if no suitable
     * handler can be found for this listener.
     * <p>
     * A <code>null</code> listener will create a {@link StandardModificationHandler}.
     *
     * @param buffer  the buffer to decorate, must not be null
     * @param listener  buffer listener, may be null
     * @return the observed buffer
     * @throws IllegalArgumentException if the buffer is null
     * @throws IllegalArgumentException if there is no valid handler for the listener
     */
    public static ObservableBuffer decorate(
            final Buffer buffer,
            final Object listener) {
        
        if (buffer == null) {
            throw new IllegalArgumentException("Buffer must not be null");
        }
        return new ObservableBuffer(buffer, listener);
    }

    // Constructors
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p>
     * The handler implementation is determined by the listener parameter via
     * the registered factories. The listener may be a manually configured 
     * <code>ModificationHandler</code> instance.
     * 
     * @param buffer  the buffer to decorate, must not be null
     * @param listener  the listener, may be null
     * @throws IllegalArgumentException if the buffer is null
     */
    protected ObservableBuffer(
            final Buffer buffer,
            final Object listener) {
        super(buffer, listener);
    }
    
    /**
     * Typecast the collection to a Buffer.
     * 
     * @return the wrapped collection as a Buffer
     */
    private Buffer getBuffer() {
        return (Buffer) getCollection();
    }

    // Buffer API
    //-----------------------------------------------------------------------
    public Object get() {
        return getBuffer().get();
    }

    //-----------------------------------------------------------------------
    public Object remove() {
        Object result = null;
        if (handler.preRemoveNext()) {
            result = getBuffer().remove();
            handler.postRemoveNext(result);
        }
        return result;
    }

}
