/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/decorators/Attic/SynchronizedList.java,v 1.4 2003/05/07 12:18:55 scolebourne Exp $
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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
import java.util.List;
import java.util.ListIterator;

/**
 * <code>SynchronizedList</code> decorates another <code>List</code>
 * to synchronize its behaviour for a multi-threaded environment.
 * <p>
 * Methods are synchronized, then forwarded to the decorated list.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2003/05/07 12:18:55 $
 * 
 * @author Stephen Colebourne
 */
public class SynchronizedList extends SynchronizedCollection implements List {

    /**
     * Factory method to create a synchronized list.
     * 
     * @param list  the list to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    public static List decorate(List list) {
        return new SynchronizedList(list);
    }
    
    /**
     * Constructor that wraps (not copies).
     * 
     * @param list  the list to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    protected SynchronizedList(List list) {
        super(list);
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param list  the list to decorate, must not be null
     * @param lock  the lock to use, must not be null
     * @throws IllegalArgumentException if list is null
     */
    protected SynchronizedList(List list, Object lock) {
        super(list, lock);
    }

    /**
     * Gets the decorated list.
     * 
     * @return the decorated list
     */
    protected List getList() {
        return (List) collection;
    }

    //-----------------------------------------------------------------------
    public void add(int index, Object object) {
        synchronized (lock) {
            getList().add(index, object);
        }
    }

    public boolean addAll(int index, Collection coll) {
        synchronized (lock) {
            return getList().addAll(index, coll);
        }
    }

    public Object get(int index) {
        synchronized (lock) {
            return getList().get(index);
        }
    }

    public int indexOf(Object object) {
        synchronized (lock) {
            return getList().indexOf(object);
        }
    }

    public int lastIndexOf(Object object) {
        synchronized (lock) {
            return getList().lastIndexOf(object);
        }
    }

    /**
     * Iterators must be manually synchronized.
     * <pre>
     * synchronized (coll) {
     *   ListIterator it = coll.listIterator();
     *   // do stuff with iterator
     * }
     * 
     * @return an iterator that must be manually synchronized on the collection
     */
    public ListIterator listIterator() {
        return getList().listIterator();
    }

    /**
     * Iterators must be manually synchronized.
     * <pre>
     * synchronized (coll) {
     *   ListIterator it = coll.listIterator(3);
     *   // do stuff with iterator
     * }
     * 
     * @return an iterator that must be manually synchronized on the collection
     */
    public ListIterator listIterator(int index) {
        return getList().listIterator(index);
    }

    public Object remove(int index) {
        synchronized (lock) {
            return getList().remove(index);
        }
    }

    public Object set(int index, Object object) {
        synchronized (lock) {
            return getList().set(index, object);
        }
    }

    public List subList(int fromIndex, int toIndex) {
        synchronized (lock) {
            List list = getList().subList(fromIndex, toIndex);
            // the lock is passed into the constructor here to ensure that the sublist is
            // synchronized on the same lock as the parent list
            return new SynchronizedList(list, lock);
        }
    }

}
