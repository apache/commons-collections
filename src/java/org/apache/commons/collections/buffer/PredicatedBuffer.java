/*
 *  Copyright 2003-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.buffer;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.collection.PredicatedCollection;

/**
 * Decorates another <code>Buffer</code> to validate that additions
 * match a specified predicate.
 * <p>
 * If an object cannot be added to the collection, an IllegalArgumentException
 * is thrown.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2004/02/18 00:58:18 $
 * 
 * @author Stephen Colebourne
 * @author Paul Jack
 */
public class PredicatedBuffer extends PredicatedCollection implements Buffer {

    /**
     * Factory method to create a predicated (validating) buffer.
     * <p>
     * If there are any elements already in the buffer being decorated, they
     * are validated.
     * 
     * @param buffer  the buffer to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @throws IllegalArgumentException if buffer or predicate is null
     * @throws IllegalArgumentException if the buffer contains invalid elements
     */
    public static Buffer decorate(Buffer buffer, Predicate predicate) {
        return new PredicatedBuffer(buffer, predicate);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the collection being decorated, they
     * are validated.
     * 
     * @param buffer  the buffer to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @throws IllegalArgumentException if buffer or predicate is null
     * @throws IllegalArgumentException if the buffer contains invalid elements
     */
    protected PredicatedBuffer(Buffer buffer, Predicate predicate) {
        super(buffer, predicate);
    }

    /**
     * Gets the buffer being decorated.
     * 
     * @return the decorated buffer
     */
    protected Buffer getBuffer() {
        return (Buffer) getCollection();
    }

    //-----------------------------------------------------------------------
    public Object get() {
        return getBuffer().get();
    }

    public Object remove() {
        return getBuffer().remove();
    }

}
