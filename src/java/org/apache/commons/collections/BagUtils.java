/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/BagUtils.java,v 1.9 2003/04/04 22:22:29 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections;

import java.util.Comparator;
import java.util.Set;

/**
 * Provides utility methods and decorators for {@link Bag} 
 * and {@link SortedBag} instances.
 *
 * @since Commons Collections 2.1
 * @version $Revision: 1.9 $ $Date: 2003/04/04 22:22:29 $
 * 
 * @author Paul Jack
 * @author Stephen Colebourne
 * @author Andrew Freeman
 * @author Matthew Hawthorne
 */
public class BagUtils {

    /**
     * Instantiation of BagUtils is not intended or required.
     * However, some tools require an instance to operate.
     */
    public BagUtils() {
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of a Bag that validates elements before they are added.
     */
    static class PredicatedBag 
            extends CollectionUtils.PredicatedCollection 
            implements Bag {

        public PredicatedBag(Bag b, Predicate p) {
            super(b, p);
        }

        public boolean add(Object o, int count) {
            validate(o);
            return getBag().add(o, count);
        }

        public boolean remove(Object o, int count) {
            return getBag().remove(o, count);
        }

        public Set uniqueSet() {
            return getBag().uniqueSet();
        }

        public int getCount(Object o) {
            return getBag().getCount(o);
        }

        private Bag getBag() {
            return (Bag) collection;
        }
    }


    /**
     * Implementation of a Bag that is synchronized.
     */
    static class SynchronizedBag
            extends CollectionUtils.SynchronizedCollection
            implements Bag {

        public SynchronizedBag(Bag bag) {
            super(bag);
        }

        public synchronized boolean add(Object o, int count) {
            return getBag().add(o, count);
        }

        public synchronized boolean remove(Object o, int count) {
            return getBag().remove(o, count);
        }

        public synchronized Set uniqueSet() {
            return getBag().uniqueSet();
        }

        public synchronized int getCount(Object o) {
            return getBag().getCount(o);
        }

        private Bag getBag() {
            return (Bag) collection;
        }
    }


    /**
     * Implementation of a Bag that is unmodifiable.
     */
    static class UnmodifiableBag 
            extends CollectionUtils.UnmodifiableCollection
            implements Bag {

        public UnmodifiableBag(Bag bag) {
            super(bag);
        }

        public boolean add(Object o, int count) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object o, int count) {
            throw new UnsupportedOperationException();
        }

        public Set uniqueSet() {
            return getBag().uniqueSet();
        }

        public int getCount(Object o) {
            return getBag().getCount(o);
        }
        
        private Bag getBag() {
            return (Bag) collection;
        }
    }


    /**
     * Implementation of a SortedBag that validates elements before they are added.
     */
    static class PredicatedSortedBag 
            extends PredicatedBag 
            implements SortedBag {

        public PredicatedSortedBag(SortedBag sb, Predicate p) {
            super(sb, p);
        }

        public Comparator comparator() {
            return getSortedBag().comparator();
        }

        public Object first() {
            return getSortedBag().first();
        }

        public Object last() {
            return getSortedBag().last();
        }

        private SortedBag getSortedBag() {
            return (SortedBag) collection;
        }
    }


    /**
     * Implementation of a SortedBag that is synchronized.
     */
    static class SynchronizedSortedBag 
            extends SynchronizedBag
            implements SortedBag {

        public SynchronizedSortedBag(SortedBag bag) {
            super(bag);
        }

        public synchronized Comparator comparator() {
            return getSortedBag().comparator();
        }

        public synchronized Object first() {
            return getSortedBag().first();
        }

        public synchronized Object last() {
            return getSortedBag().last();
        }

        private SortedBag getSortedBag() {
            return (SortedBag) collection;
        }
    }


    /**
     * Implementation of a SortedBag that is unmodifiable.
     */
    static class UnmodifiableSortedBag
            extends UnmodifiableBag
            implements SortedBag {

        public UnmodifiableSortedBag(SortedBag bag) {
            super(bag);
        }

        public Comparator comparator() {
            return getSortedBag().comparator();
        }

        public Object first() {
            return getSortedBag().first();
        }

        public Object last() {
            return getSortedBag().last();
        }

        private SortedBag getSortedBag() {
            return (SortedBag) collection;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a synchronized (thread-safe) bag backed by the given bag.
     * In order to guarantee serial access, it is critical that all 
     * access to the backing bag is accomplished through the returned bag.
     * <p>
     * It is imperative that the user manually synchronize on the returned
     * bag when iterating over it:
     *
     * <pre>
     * Bag bag = BagUtils.synchronizedBag(new HashBag());
     * ...
     * synchronized(bag) {
     *     Iterator i = bag.iterator(); // Must be in synchronized block
     *     while (i.hasNext())
     *         foo(i.next());
     *     }
     * }
     * </pre>
     *
     * Failure to follow this advice may result in non-deterministic 
     * behavior.
     *
     * @param bag  the bag to synchronize, must not be null
     * @return a synchronized bag backed by that bag
     * @throws IllegalArgumentException  if the Bag is null
     */
    public static Bag synchronizedBag(Bag bag) {
        return new SynchronizedBag(bag);
    }

    /**
     * Returns an unmodifiable view of the given bag.  Any modification
     * attempts to the returned bag will raise an 
     * {@link UnsupportedOperationException}.
     *
     * @param bag  the bag whose unmodifiable view is to be returned, must not be null
     * @return an unmodifiable view of that bag
     * @throws IllegalArgumentException  if the Bag is null
     */
    public static Bag unmodifiableBag(Bag bag) {
        return new UnmodifiableBag(bag);
    }
    
    /**
     * Returns a predicated bag backed by the given bag.  Only objects
     * that pass the test in the given predicate can be added to the bag.
     * It is important not to use the original bag after invoking this 
     * method, as it is a backdoor for adding unvalidated objects.
     *
     * @param bag  the bag to predicate, must not be null
     * @param predicate  the predicate for the bag, must not be null
     * @return a predicated bag backed by the given bag
     * @throws IllegalArgumentException  if the Bag or Predicate is null
     */
    public static Bag predicatedBag(Bag bag, Predicate predicate) {
        return new PredicatedBag(bag, predicate);
    }

    /**
     * Returns a typed bag backed by the given bag.
     * <p>
     * Only objects of the specified type can be added to the bag.
     * 
     * @param bag  the bag to limit to a specific type, must not be null
     * @param type  the type of objects which may be added to the bag
     * @return a typed bag backed by the specified bag
     */
    public static Bag typedBag(Bag bag, Class type) {
        return predicatedBag(bag, new CollectionUtils.InstanceofPredicate(type));
    }
    
    //-----------------------------------------------------------------------
    /**
     * Returns a synchronized (thread-safe) sorted bag backed by the given 
     * sorted bag.
     * In order to guarantee serial access, it is critical that all 
     * access to the backing bag is accomplished through the returned bag.
     * <p>
     * It is imperative that the user manually synchronize on the returned
     * bag when iterating over it:
     *
     * <pre>
     * SortedBag bag = BagUtils.synchronizedSortedBag(new TreeBag());
     * ...
     * synchronized(bag) {
     *     Iterator i = bag.iterator(); // Must be in synchronized block
     *     while (i.hasNext())
     *         foo(i.next());
     *     }
     * }
     * </pre>
     *
     * Failure to follow this advice may result in non-deterministic 
     * behavior.
     *
     * @param bag  the bag to synchronize, must not be null
     * @return a synchronized bag backed by that bag
     * @throws IllegalArgumentException  if the SortedBag is null
     */
    public static SortedBag synchronizedSortedBag(SortedBag bag) {
        return new SynchronizedSortedBag(bag);
    }
    
    /**
     * Returns an unmodifiable view of the given sorted bag.  Any modification
     * attempts to the returned bag will raise an 
     * {@link UnsupportedOperationException}.
     *
     * @param bag  the bag whose unmodifiable view is to be returned, must not be null
     * @return an unmodifiable view of that bag
     * @throws IllegalArgumentException  if the SortedBag is null
     */
    public static SortedBag unmodifiableSortedBag(SortedBag bag) {
        return new UnmodifiableSortedBag(bag);
    }
    
    /**
     * Returns a predicated sorted bag backed by the given sorted bag.  
     * Only objects that pass the test in the given predicate can be 
     * added to the bag.
     * It is important not to use the original bag after invoking this 
     * method, as it is a backdoor for adding unvalidated objects.
     *
     * @param bag  the sorted bag to predicate, must not be null
     * @param predicate  the predicate for the bag, must not be null
     * @return a predicated bag backed by the given bag
     * @throws IllegalArgumentException  if the SortedBag or Predicate is null
     */
    public static SortedBag predicatedSortedBag(SortedBag bag, Predicate predicate) {
        return new PredicatedSortedBag(bag, predicate);
    }
    
    /**
     * Returns a typed sorted bag backed by the given bag.
     * <p>
     * Only objects of the specified type can be added to the bag.
     * 
     * @param bag  the bag to limit to a specific type, must not be null
     * @param type  the type of objects which may be added to the bag
     * @return a typed bag backed by the specified bag
     */
    public static SortedBag typedSortedBag(SortedBag bag, Class type) {
        return predicatedSortedBag(bag, new CollectionUtils.InstanceofPredicate(type));
    }
        
}
