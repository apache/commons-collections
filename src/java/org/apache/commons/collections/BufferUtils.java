/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/BufferUtils.java,v 1.2 2002/07/03 01:59:50 mas Exp $
 * $Revision: 1.2 $
 * $Date: 2002/07/03 01:59:50 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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


import java.util.Collection;
import java.util.Iterator;


/**
 *  Contains static utility methods for operating on {@link Buffer} objects.
 *
 *  @author Paul Jack
 *  @version $Id: BufferUtils.java,v 1.2 2002/07/03 01:59:50 mas Exp $
 */
public class BufferUtils {


    /**
     *  Returns a synchronized buffer backed by the given buffer.
     *  Much like the synchronized collections returned by 
     *  {@link java.util.Collections}, you must manually synchronize on 
     *  the returned buffer's iterator to avoid non-deterministic behavior:
     *  
     *  <Pre>
     *  Buffer b = BufferUtils.synchronizedBuffer(myBuffer);
     *  synchronized (b) {
     *      Iterator i = b.iterator();
     *      while (i.hasNext()) {
     *          process (i.next());
     *      }
     *  }
     *  </Pre>
     *
     *  @param b  the buffer to synchronize
     *  @return  a synchronized buffer backed by that buffer
     */
    public static Buffer synchronizedBuffer(final Buffer b) {
        return new SynchronizedBuffer(b);
    }


    /**
     *  Returns a synchronized buffer backed by the given buffer that will
     *  block on {@link Buffer.get()} and {@link Buffer.remove()} operations.
     *  If the buffer is empty, then the {@link Buffer.get()} and 
     *  {@link Buffer.remove()} operations will block until new elements
     *  are added to the buffer, rather than immediately throwing a 
     *  <Code>BufferUnderflowException</Code>.
     *
     *  @param buf  the buffer to synchronize
     *  @return  a blocking buffer backed by that buffer
     */
    public static Buffer blockingBuffer(Buffer buf) {
        return new SynchronizedBuffer(buf) {

            public synchronized boolean add(Object o) {
                boolean r = b.add(o);
                notify();
                return r;
            }

            public synchronized boolean addAll(Collection c) {
                boolean r = b.addAll(c);
                notifyAll();
                return r;
            }

            public synchronized Object get() {
                while (b.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new BufferUnderflowException();
                    }
                }
                return b.get();
            }

            public synchronized Object remove() {
                while (b.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new BufferUnderflowException();
                    }
                }
                return b.remove();
            }
        };
    }


    private static class SynchronizedBuffer implements Buffer {

            Buffer b;


            public SynchronizedBuffer(Buffer b) {
                 this.b = b;
            }

            public synchronized int size() {
                return b.size();
            }

            public synchronized boolean isEmpty() {
                return b.isEmpty();
            }

            public synchronized boolean contains(Object o) {
                return b.contains(o);
            }

            public Iterator iterator() {
                return b.iterator();
            }

            public synchronized Object[] toArray() {
                return b.toArray();
            }

            public synchronized Object[] toArray(Object[] o) {
                return b.toArray(o);
            }

            public synchronized boolean add(Object o) {
                return b.add(o);
            }

            public synchronized boolean remove(Object o) {
                return b.remove(o);
            }

            public synchronized boolean containsAll(Collection c) {
                return b.containsAll(c);
            }

            public synchronized boolean addAll(Collection c) {
                return b.addAll(c);
            }

            public synchronized boolean removeAll(Collection c) {
                return b.removeAll(c);
            }

            public synchronized boolean retainAll(Collection c) {
                return b.retainAll(c);
            }

            public synchronized void clear() {
                b.clear();
            }

            public synchronized boolean equals(Object o) {
                return b.equals(o);
            }

            public synchronized int hashCode() {
                return b.hashCode();
            }

            public synchronized String toString() {
                return b.toString();
            }

            public synchronized Object get() {
                return b.get();
            }

            public synchronized Object remove() {
                return b.remove();
            }          

        
    }


    /**
     *  Returns an unmodifiable buffer backed by the given buffer.
     *
     *  @param b  the buffer to make unmodifiable
     *  @return  an unmodifiable buffer backed by that buffer
     */
    public static Buffer unmodifiableBuffer(Buffer b) {
        return new BufferDecorator(b) {
            public boolean addAll(Collection c) {
                throw new UnsupportedOperationException();
            } 

            public boolean removeAll(Collection c) {
                throw new UnsupportedOperationException();
            } 

            public boolean retainAll(Collection c) {
                throw new UnsupportedOperationException();
            } 

            public boolean add(Object o) {
                throw new UnsupportedOperationException();
            } 

            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            } 

            public void clear() {
                throw new UnsupportedOperationException();
            }

            public Object remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    /**
     *  Returns a predicated buffer backed by the given buffer.  Elements are
     *  evaluated with the given predicate before being added to the buffer.
     *  If the predicate evaluation returns false, then an 
     *  IllegalArgumentException is raised and the element is not added to
     *  the buffer.
     *
     *  @param buf  the buffer to predicate
     *  @param p  the predicate used to evaluate new elements
     *  @return  a predicated buffer
     */
    public static Buffer predicatedBuffer(Buffer buf, final Predicate p) {
        if (buf == null) {
            throw new IllegalArgumentException("Buffer must not be null.");
        }
        if (p == null) {
            throw new IllegalArgumentException("Predicate must not be null.");
        }
        return new BufferDecorator(buf) {

            public boolean add(Object o) {
                test(o);
                return b.add(o);
            }

            public boolean addAll(Collection c) {
                Iterator iterator = c.iterator();
                while (iterator.hasNext()) {
                    test(iterator.next());
                }
                return b.addAll(c);
            }

            private void test(Object o) {
                if (!p.evaluate(o)) {
                    throw new IllegalArgumentException("Invalid: " + o);
                }
            }
        };
    }


    private static class BufferDecorator implements Buffer {

        Buffer b;

        BufferDecorator(Buffer b) {
            this.b = b;
        }

        public int size() {
            return b.size();
        }

        public boolean isEmpty() {
            return b.isEmpty();
        }

        public boolean contains(Object o) {
            return b.contains(o);
        }

        public Iterator iterator() {
            return b.iterator();
        }

        public Object[] toArray() {
            return b.toArray();
        }

        public Object[] toArray(Object[] o) {
            return b.toArray(o);
        }

        public boolean add(Object o) {
            return b.add(o);
        }

        public boolean remove(Object o) {
            return b.remove(o);
        }

        public boolean containsAll(Collection c) {
            return b.containsAll(c);
        }

        public boolean addAll(Collection c) {
            return b.addAll(c);
        }

        public boolean removeAll(Collection c) {
            return b.removeAll(c);
        }

        public boolean retainAll(Collection c) {
            return b.retainAll(c);
        }

        public void clear() {
            b.clear();
        }

        public boolean equals(Object o) {
            return b.equals(o);
        }

        public int hashCode() {
            return b.hashCode();
        }

        public String toString() {
            return b.toString();
        }

        public Object get() {
            return b.get();
        }

        public Object remove() {
            return b.remove();
        }
    }


}
