/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/BagUtils.java,v 1.16 2003/11/27 22:55:16 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections;

import org.apache.commons.collections.bag.PredicatedBag;
import org.apache.commons.collections.bag.PredicatedSortedBag;
import org.apache.commons.collections.bag.SynchronizedBag;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.commons.collections.bag.TransformedBag;
import org.apache.commons.collections.bag.TransformedSortedBag;
import org.apache.commons.collections.bag.TypedBag;
import org.apache.commons.collections.bag.TypedSortedBag;
import org.apache.commons.collections.bag.UnmodifiableBag;
import org.apache.commons.collections.bag.UnmodifiableSortedBag;
import org.apache.commons.collections.observed.ModificationListener;
import org.apache.commons.collections.observed.ObservableBag;
import org.apache.commons.collections.observed.ObservableSortedBag;

/**
 * Provides utility methods and decorators for
 * {@link Bag} and {@link SortedBag} instances.
 *
 * @since Commons Collections 2.1
 * @version $Revision: 1.16 $ $Date: 2003/11/27 22:55:16 $
 * 
 * @author Paul Jack
 * @author Stephen Colebourne
 * @author Andrew Freeman
 * @author Matthew Hawthorne
 */
public class BagUtils {

    /**
     * An empty unmodifiable bag.
     */
    public static final Bag EMPTY_BAG = UnmodifiableBag.decorate(new HashBag());

    /**
     * An empty unmodifiable sorted bag.
     */
    public static final Bag EMPTY_SORTED_BAG = UnmodifiableSortedBag.decorate(new TreeBag());

    /**
     * Instantiation of BagUtils is not intended or required.
     * However, some tools require an instance to operate.
     */
    public BagUtils() {
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
        return SynchronizedBag.decorate(bag);
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
        return UnmodifiableBag.decorate(bag);
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
        return PredicatedBag.decorate(bag, predicate);
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
        return TypedBag.decorate(bag, type);
    }
    
    /**
     * Returns a transformed bag backed by the given bag.
     * <p>
     * Each object is passed through the transformer as it is added to the
     * Bag. It is important not to use the original bag after invoking this 
     * method, as it is a backdoor for adding untransformed objects.
     *
     * @param bag  the bag to predicate, must not be null
     * @param transformer  the transformer for the bag, must not be null
     * @return a transformed bag backed by the given bag
     * @throws IllegalArgumentException  if the Bag or Transformer is null
     */
    public static Bag transformedBag(Bag bag, Transformer transformer) {
        return TransformedBag.decorate(bag, transformer);
    }
    
    /**
     * Returns an observable bag where changes are notified to listeners.
     * <p>
     * This method creates an observable bag and attaches the specified listener.
     * If more than one listener or other complex setup is required then the
     * ObservableBag class should be accessed directly.
     *
     * @deprecated TO BE REMOVED BEFORE v3.0
     * @param bag  the bag to decorate, must not be null
     * @param listener  bag listener, must not be null
     * @return the observed bag
     * @throws IllegalArgumentException if the bag or listener is null
     * @throws IllegalArgumentException if there is no valid handler for the listener
     */
    public static ObservableBag observableBag(Bag bag, ModificationListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener must not be null");
        }
        return ObservableBag.decorate(bag, listener);
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
        return SynchronizedSortedBag.decorate(bag);
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
        return UnmodifiableSortedBag.decorate(bag);
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
        return PredicatedSortedBag.decorate(bag, predicate);
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
        return TypedSortedBag.decorate(bag, type);
    }
    
    /**
     * Returns a transformed sorted bag backed by the given bag.
     * <p>
     * Each object is passed through the transformer as it is added to the
     * Bag. It is important not to use the original bag after invoking this 
     * method, as it is a backdoor for adding untransformed objects.
     *
     * @param bag  the bag to predicate, must not be null
     * @param transformer  the transformer for the bag, must not be null
     * @return a transformed bag backed by the given bag
     * @throws IllegalArgumentException  if the Bag or Transformer is null
     */
    public static SortedBag transformedSortedBag(SortedBag bag, Transformer transformer) {
        return TransformedSortedBag.decorate(bag, transformer);
    }
    
    /**
     * Returns an observable sorted bag where changes are notified to listeners.
     * <p>
     * This method creates an observable sorted bag and attaches the specified listener.
     * If more than one listener or other complex setup is required then the
     * ObservableSortedBag class should be accessed directly.
     *
     * @deprecated TO BE REMOVED BEFORE v3.0
     * @param bag  the bag to decorate, must not be null
     * @param listener  bag listener, must not be null
     * @return the observed bag
     * @throws IllegalArgumentException if the bag or listener is null
     * @throws IllegalArgumentException if there is no valid handler for the listener
     */
    public static ObservableSortedBag observableSortedBag(SortedBag bag, ModificationListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener must not be null");
        }
        return ObservableSortedBag.decorate(bag, listener);
    }
        
}
