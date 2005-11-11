/*
 *  Copyright 2001-2005 The Apache Software Foundation
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

/**
 * Decorates another <code>Buffer</code> to make {@link #get()} and
 * {@link #remove()} block (until timeout expires) when the <code>Buffer</code> is empty.
 * <p>
 * If either <code>get</code> or <code>remove</code> is called on an empty
 * <code>Buffer</code>, the calling thread waits (until timeout expires) for notification that
 * an <code>add</code> or <code>addAll</code> operation has completed.
 * <p>
 * When one or more entries are added to an empty <code>Buffer</code>,
 * all threads blocked in <code>get</code> or <code>remove</code> are notified.
 * There is no guarantee that concurrent blocked <code>get</code> or
 * <code>remove</code> requests will be "unblocked" and receive data in the
 * order that they arrive.
 * <p>
 * This class is Serializable from Commons Collections 3.2.
 *
 * @author James Carman
 * @version $Revision$ $Date$
 * @since Commons Collections 3.2
 */
public class TimeoutBuffer extends BlockingBuffer {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------
    private static final long serialVersionUID = 1719328905017860541L;

    private final long timeout;

//----------------------------------------------------------------------------------------------------------------------
// Static Methods
//----------------------------------------------------------------------------------------------------------------------

    public static Buffer decorate( Buffer buffer, long timeout ) {
        return new TimeoutBuffer( buffer, timeout );
    }

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public TimeoutBuffer( Buffer buffer, long timeout ) {
        super( buffer );
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }
//----------------------------------------------------------------------------------------------------------------------
// Buffer Implementation
//----------------------------------------------------------------------------------------------------------------------

    public Object get() {
        return get( timeout );
    }

    public Object remove() {
        return remove( timeout );
    }

    public boolean equals( Object o ) {
        if( this == o ) {
            return true;
        }
        if( o == null || getClass() != o.getClass() ) {
            return false;
        }
        if( !super.equals( o ) ) {
            return false;
        }
        final TimeoutBuffer that = ( TimeoutBuffer ) o;
        if( timeout != that.timeout ) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + ( int ) ( timeout ^ ( timeout >>> 32 ) );
        return result;
    }
}

