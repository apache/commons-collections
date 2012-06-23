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
import org.apache.commons.collections.collection.AbstractCollectionDecorator;

/**
 * Decorates another <code>Buffer</code> to provide additional behaviour.
 * <p>
 * Methods are forwarded directly to the decorated buffer.
 *
 * @param <E> the type of the elements in the buffer
 * @since Commons Collections 3.0
 * @version $Revision$
 *
 * @author Stephen Colebourne
 */
public abstract class AbstractBufferDecorator<E> extends AbstractCollectionDecorator<E> implements
        Buffer<E> {

    /** Serialization version */
    private static final long serialVersionUID = -2629815475789577029L;

    /**
     * Constructor only used in deserialization, do not use otherwise.
     * @since Commons Collections 3.1
     */
    protected AbstractBufferDecorator() {
        super();
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param buffer  the buffer to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    protected AbstractBufferDecorator(Buffer<E> buffer) {
        super(buffer);
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
    
    /**
     * {@inheritDoc}
     */
    public E get() {
        return decorated().get();
    }

    /**
     * {@inheritDoc}
     */
    public E remove() {
        return decorated().remove();
    }

}
