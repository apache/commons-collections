/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.buffer;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.collection.SynchronizedCollection;

/**
 * Decorates another <code>Buffer</code> to synchronize its behaviour
 * for a multi-threaded environment.
 * <p>
 * Methods are synchronized, then forwarded to the decorated buffer.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @param <E> the type of the elements in the buffer
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public class SynchronizedBuffer<E>
        extends SynchronizedCollection<E>
        implements Buffer<E> {

    /** Serialization version */
    private static final long serialVersionUID = -6859936183953626253L;

    /**
     * Factory method to create a synchronized buffer.
     * 
     * @param <T> the type of the elements in the buffer
     * @param buffer  the buffer to decorate, must not be null
     * @return a new synchronized Buffer
     * @throws IllegalArgumentException if buffer is null
     */
    public static <T> Buffer<T> decorate(Buffer<T> buffer) {
        return new SynchronizedBuffer<T>(buffer);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param buffer  the buffer to decorate, must not be null
     * @throws IllegalArgumentException if the buffer is null
     */
    protected SynchronizedBuffer(Buffer<E> buffer) {
        super(buffer);
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param buffer  the buffer to decorate, must not be null
     * @param lock  the lock object to use, must not be null
     * @throws IllegalArgumentException if the buffer is null
     */
    protected SynchronizedBuffer(Buffer<E> buffer, Object lock) {
        super(buffer, lock);
    }

    /**
     * Gets the buffer being decorated.
     * 
     * @return the decorated buffer
     */
    @Override
    protected Buffer<E> decorated() {
        return (Buffer<E>) super.decorated();
    }

    //-----------------------------------------------------------------------
    public E get() {
        synchronized (lock) {
            return decorated().get();
        }
    }

    public E remove() {
        synchronized (lock) {
            return decorated().remove();
        }
    }

}
