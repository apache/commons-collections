/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import java.util.Collection;
/**
 * Contains static utility methods for operating on {@link Buffer} objects.
 *
 * @author Paul Jack
 * @author Stephen Colebourne
 * @version $Id: BufferUtils.java,v 1.9.2.1 2004/05/22 12:14:02 scolebourne Exp $
 * @since 2.1
 */
public class BufferUtils {

    /**
     * Restrictive constructor
     */
    private BufferUtils() {
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
    public static Buffer synchronizedBuffer(final Buffer buffer) {
        return new SynchronizedBuffer(buffer);
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
        return new SynchronizedBuffer(buffer) {

            public synchronized boolean add(Object o) {
                boolean r = collection.add(o);
                notify();
                return r;
            }

            public synchronized boolean addAll(Collection c) {
                boolean r = collection.addAll(c);
                notifyAll();
                return r;
            }

            public synchronized Object get() {
                while (collection.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new BufferUnderflowException();
                    }
                }
                return ((Buffer)collection).get();
            }

            public synchronized Object remove() {
                while (collection.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new BufferUnderflowException();
                    }
                }
                return ((Buffer)collection).remove();
            }
        };
    }

    /**
     * Returns an unmodifiable buffer backed by the given buffer.
     *
     * @param buffer  the buffer to make unmodifiable, must not be null
     * @return an unmodifiable buffer backed by that buffer
     * @throws IllegalArgumentException  if the Buffer is null
     */
    public static Buffer unmodifiableBuffer(Buffer buffer) {
        return new UnmodifiableBuffer(buffer);
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
    public static Buffer predicatedBuffer(Buffer buffer, final Predicate predicate) {
        return new PredicatedBuffer(buffer, predicate);
    }



    static class SynchronizedBuffer 
            extends CollectionUtils.SynchronizedCollection
            implements Buffer {

        public SynchronizedBuffer(Buffer b) {
            super(b);
        }

        public synchronized Object get() {
            return ((Buffer)collection).get();
        }

        public synchronized Object remove() {
            return ((Buffer)collection).remove();
        }        
    }


    static class UnmodifiableBuffer 
            extends CollectionUtils.UnmodifiableCollection
            implements Buffer {

        public UnmodifiableBuffer(Buffer b) {
            super(b);
        }

        public Object get() {
            return ((Buffer)collection).get();
        }

        public Object remove() {
            throw new UnsupportedOperationException();
        }

    }


    static class PredicatedBuffer 
            extends CollectionUtils.PredicatedCollection
            implements Buffer {

        public PredicatedBuffer(Buffer b, Predicate p) {
            super(b, p);
        }

        public Object get() {
            return ((Buffer)collection).get();
        }

        public Object remove() {
            return ((Buffer)collection).remove();
        }

    }


}
