/*
 *  Copyright 2005 The Apache Software Foundation
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
import org.apache.commons.collections.BufferUnderflowException;

/**
 * Decorates another <code>Buffer</code> to make {@link #get()} and
 * {@link #remove()} block (until timeout expires) when the <code>Buffer</code>
 * is empty.
 * <p>
 * If either <code>get</code> or <code>remove</code> is called on an empty
 * <code>Buffer</code>, the calling thread waits (until timeout expires) for
 * notification that an <code>add</code> or <code>addAll</code> operation
 * has completed.
 * <p>
 * When one or more entries are added to an empty <code>Buffer</code>, all
 * threads blocked in <code>get</code> or <code>remove</code> are notified.
 * There is no guarantee that concurrent blocked <code>get</code> or
 * <code>remove</code> requests will be "unblocked" and receive data in the
 * order that they arrive.
 * 
 * @author James Carman
 * @version $Revision: $ $Date: $
 * @since Commons Collections 3.2
 */
public class TimeoutBuffer extends BlockingBuffer {

    /** The serialization lock. */
    private static final long serialVersionUID = 1719328905017860541L;

    /** The timeout length. */
    private final long timeout;

    /**
     * Decorates the specified buffer adding timeout behaviour.
     *
     * @param buffer  the buffer to decorate, must not be null
     * @param timeout  the timeout value in milliseconds
     * @return the decorated buffer
     * @throws IllegalArgumentException if the buffer is null
     * @throws IllegalArgumentException if the timeout is negative
     */
    public static Buffer decorate(Buffer buffer, long timeout) {
        return new TimeoutBuffer(buffer, timeout);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param buffer  the buffer to decorate, must not be null
     * @param timeout  the timeout value in milliseconds
     * @throws IllegalArgumentException if the buffer is null
     * @throws IllegalArgumentException if the timeout is negative
     */
    protected TimeoutBuffer(Buffer buffer, long timeout) {
        super(buffer);
        if (timeout < 0) {
            throw new IllegalArgumentException("The timeout cannot be negative");
        }
        this.timeout = timeout;
    }

    /**
     * Gets the length of the timeout.
     *
     * @return the timeout value
     */
    public long getTimeout() {
        return timeout;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next value from the buffer, waiting until an object is
     * added for up to the specified timeout value if the buffer is empty.
     *
     * @throws BufferUnderflowException if an interrupt is received
     * @throws BufferUnderflowException if the timeout expires
     */
    public Object get() {
        return get(timeout);
    }

    /**
     * Removes the next value from the buffer, waiting until an object is
     * added for up to the specified timeout value if the buffer is empty.
     *
     * @throws BufferUnderflowException if an interrupt is received
     * @throws BufferUnderflowException if the timeout expires
     */
    public Object remove() {
        return remove(timeout);
    }

}
