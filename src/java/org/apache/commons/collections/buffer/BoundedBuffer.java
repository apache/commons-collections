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
import org.apache.commons.collections.BufferOverflowException;
import org.apache.commons.collections.BufferUnderflowException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;

/**
 * A wrapper class for buffers which makes them bounded.
 * @author James Carman
 * @since 3.2
 */
public class BoundedBuffer extends SynchronizedBuffer {

    private static final long serialVersionUID = 1536432911093974264L;

    private final int maximumSize;
    private final long timeout;

    /**
     * Factory method to create a bounded buffer.
     * @param buffer the buffer to decorate, must not be null
     * @param maximumSize the maximum size
     * @return a new bounded buffer
     * @throws IllegalArgumentException if the buffer is null
     */
    public static Buffer decorate( Buffer buffer, int maximumSize ) {
        return new BoundedBuffer( buffer, maximumSize );
    }

    /**
     * Factory method to create a bounded buffer that blocks for a maximum
     * amount of time.
     * @param buffer the buffer to decorate, must not be null
     * @param maximumSize the maximum size
     * @param timeout the maximum amount of time to wait.
     * @return a new bounded buffer
     * @throws IllegalArgumentException if the buffer is null
     */
    public static Buffer decorate( Buffer buffer, int maximumSize, long timeout ) {
        return new BoundedBuffer( buffer, maximumSize, timeout );
    }

    /**
     * Constructor that wraps (not copies) another buffer, making it bounded.
     * @param buffer the buffer to wrap, must not be null
     * @param maximumSize the maximum size of the buffer
     * @throws IllegalArgumentException if the buffer is null
     */
    protected BoundedBuffer( Buffer buffer, int maximumSize ) {
        this( buffer, maximumSize, -1 );
    }

    /**
     * Constructor that wraps (not copies) another buffer, making it bounded waiting only up to
     * a maximum amount of time.
     * @param buffer the buffer to wrap, must not be null
     * @param maximumSize the maximum size of the buffer
     * @param timeout the maximum amount of time to wait
     * @throws IllegalArgumentException if the buffer is null
     */
    protected BoundedBuffer( Buffer buffer, int maximumSize, long timeout ) {
        super( buffer );
        this.maximumSize = maximumSize;
        this.timeout = timeout;
    }

    public Object remove() {
        synchronized( lock ) {
            Object returnValue = getBuffer().remove();
            lock.notifyAll();
            return returnValue;
        }
    }

    public boolean add( Object o ) {
        synchronized( lock ) {
            timeoutWait( 1 );
            return getBuffer().add( o );
        }
    }

    public boolean addAll( final Collection c ) {
        synchronized( lock ) {
            timeoutWait( c.size() );
            return getBuffer().addAll( c );
        }
    }

    public Iterator iterator() {
        return new NotifyingIterator( collection.iterator() );
    }

    private void timeoutWait( final int nAdditions ) {
        synchronized( lock ) {
            if( timeout < 0 && getBuffer().size() + nAdditions > maximumSize ) {
                throw new BufferOverflowException( "Buffer size cannot exceed " + maximumSize + "." );
            }
            final long expiration = System.currentTimeMillis() + timeout;
            long timeLeft = expiration - System.currentTimeMillis();
            while( timeLeft > 0 && getBuffer().size() + nAdditions > maximumSize ) {
                try {
                    lock.wait( timeLeft );
                    timeLeft = expiration - System.currentTimeMillis();
                }
                catch( InterruptedException e ) {
                    PrintWriter out = new PrintWriter( new StringWriter() );
                    e.printStackTrace( out );
                    throw new BufferUnderflowException( "Caused by InterruptedException: " + out.toString() );
                }
            }
            if( getBuffer().size() + nAdditions > maximumSize ) {
                throw new BufferOverflowException( "Timeout expired." );
            }
        }
    }

    private class NotifyingIterator implements Iterator {

        private final Iterator i;

        public NotifyingIterator( Iterator i ) {
            this.i = i;
        }

        public void remove() {
            synchronized( lock ) {
                i.remove();
                lock.notifyAll();
            }
        }

        public boolean hasNext() {
            return i.hasNext();
        }

        public Object next() {
            return i.next();
        }
    }
}

