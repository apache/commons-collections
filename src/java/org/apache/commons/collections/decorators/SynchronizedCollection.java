/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/decorators/Attic/SynchronizedCollection.java,v 1.4 2003/08/31 17:24:46 scolebourne Exp $
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
package org.apache.commons.collections.decorators;

import java.util.Collection;
import java.util.Iterator;

/**
 * <code>SynchronizedCollection</code> decorates another <code>Collection</code>
 * to synchronize its behaviour for a multi-threaded environment.
 * <p>
 * Iterators must be manually synchronized:
 * <pre>
 * synchronized (coll) {
 *   Iterator it = coll.iterator();
 *   // do stuff with iterator
 * }
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2003/08/31 17:24:46 $
 * 
 * @author Stephen Colebourne
 */
public class SynchronizedCollection implements Collection {

    /** The collection to decorate */
    protected final Collection collection;
    /** The object to lock on, needed for List/SortedSet views */
    protected final Object lock;

    /**
     * Factory method to create a synchronized collection.
     * 
     * @param coll  the collection to decorate, must not be null
     * @throws IllegalArgumentException if collection is null
     */
    public static Collection decorate(Collection coll) {
        return new SynchronizedCollection(coll);
    }
    
    /**
     * Constructor that wraps (not copies).
     * 
     * @param coll  the collection to decorate, must not be null
     * @throws IllegalArgumentException if the collection is null
     */
    protected SynchronizedCollection(Collection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        this.collection = collection;
        this.lock = this;
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param coll  the collection to decorate, must not be null
     * @param lock  the lock object to use, must not be null
     * @throws IllegalArgumentException if the collection is null
     */
    protected SynchronizedCollection(Collection collection, Object lock) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        this.collection = collection;
        this.lock = lock;
    }

    //-----------------------------------------------------------------------
    public boolean add(Object object) {
        synchronized (lock) {
            return collection.add(object);
        }
    }

    public boolean addAll(Collection coll) {
        synchronized (lock) {
            return collection.addAll(coll);
        }
    }

    public void clear() {
        synchronized (lock) {
            collection.clear();
        }
    }

    public boolean contains(Object object) {
        synchronized (lock) {
            return collection.contains(object);
        }
    }

    public boolean containsAll(Collection coll) {
        synchronized (lock) {
            return collection.containsAll(coll);
        }
    }

    public boolean isEmpty() {
        synchronized (lock) {
            return collection.isEmpty();
        }
    }

    /**
     * Iterators must be manually synchronized.
     * <pre>
     * synchronized (coll) {
     *   Iterator it = coll.iterator();
     *   // do stuff with iterator
     * }
     * 
     * @return an iterator that must be manually synchronized on the collection
     */
    public Iterator iterator() {
        return collection.iterator();
    }

    public Object[] toArray() {
        synchronized (lock) {
            return collection.toArray();
        }
    }

    public Object[] toArray(Object[] object) {
        synchronized (lock) {
            return collection.toArray(object);
        }
    }

    public boolean remove(Object object) {
        synchronized (lock) {
            return collection.remove(object);
        }
    }

    public boolean removeAll(Collection coll) {
        synchronized (lock) {
            return collection.removeAll(coll);
        }
    }

    public boolean retainAll(Collection coll) {
        synchronized (lock) {
            return collection.retainAll(coll);
        }
    }

    public int size() {
        synchronized (lock) {
            return collection.size();
        }
    }

    public boolean equals(Object object) {
        synchronized (lock) {
            if (object == this) {
                return true;
            }
            return collection.equals(object);
        }
    }

    public int hashCode() {
        synchronized (lock) {
            return collection.hashCode();
        }
    }

    public String toString() {
        synchronized (lock) {
            return collection.toString();
        }
    }

}
