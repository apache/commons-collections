/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/set/SynchronizedSortedSet.java,v 1.1 2003/11/16 00:05:45 scolebourne Exp $
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
package org.apache.commons.collections.set;

import java.util.Comparator;
import java.util.SortedSet;

import org.apache.commons.collections.collection.SynchronizedCollection;

/**
 * Decorates another <code>SortedSet</code> to synchronize its behaviour
 * for a multi-threaded environment.
 * <p>
 * Methods are synchronized, then forwarded to the decorated set.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/11/16 00:05:45 $
 * 
 * @author Stephen Colebourne
 */
public class SynchronizedSortedSet extends SynchronizedCollection implements SortedSet {

    /**
     * Factory method to create a synchronized set.
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    public static SortedSet decorate(SortedSet set) {
        return new SynchronizedSortedSet(set);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    protected SynchronizedSortedSet(SortedSet set) {
        super(set);
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param set  the set to decorate, must not be null
     * @param lock  the lock object to use, must not be null
     * @throws IllegalArgumentException if set is null
     */
    protected SynchronizedSortedSet(SortedSet set, Object lock) {
        super(set, lock);
    }

    /**
     * Gets the decorated set.
     * 
     * @return the decorated set
     */
    protected SortedSet getSortedSet() {
        return (SortedSet) collection;
    }

    //-----------------------------------------------------------------------
    public SortedSet subSet(Object fromElement, Object toElement) {
        synchronized (lock) {
            SortedSet set = getSortedSet().subSet(fromElement, toElement);
            // the lock is passed into the constructor here to ensure that the
            // subset is synchronized on the same lock as the parent
            return new SynchronizedSortedSet(set, lock);
        }
    }

    public SortedSet headSet(Object toElement) {
        synchronized (lock) {
            SortedSet set = getSortedSet().headSet(toElement);
            // the lock is passed into the constructor here to ensure that the
            // headset is synchronized on the same lock as the parent
            return new SynchronizedSortedSet(set, lock);
        }
    }

    public SortedSet tailSet(Object fromElement) {
        synchronized (lock) {
            SortedSet set = getSortedSet().tailSet(fromElement);
            // the lock is passed into the constructor here to ensure that the
            // tailset is synchronized on the same lock as the parent
            return new SynchronizedSortedSet(set, lock);
        }
    }

    public Object first() {
        synchronized (lock) {
            return getSortedSet().first();
        }
    }

    public Object last() {
        synchronized (lock) {
            return getSortedSet().last();
        }
    }

    public Comparator comparator() {
        synchronized (lock) {
            return getSortedSet().comparator();
        }
    }

}
