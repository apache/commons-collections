/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/SetUtils.java,v 1.14 2003/05/11 13:29:16 scolebourne Exp $
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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.decorators.PredicatedSet;
import org.apache.commons.collections.decorators.PredicatedSortedSet;
import org.apache.commons.collections.decorators.TransformedSet;
import org.apache.commons.collections.decorators.TransformedSortedSet;
import org.apache.commons.collections.decorators.TypedSet;
import org.apache.commons.collections.decorators.TypedSortedSet;

/**
 * Provides static utility methods and decorators for {@link Set} 
 * and {@link SortedSet} instances.
 *
 * @since Commons Collections 2.1
 * @version $Revision: 1.14 $ $Date: 2003/05/11 13:29:16 $
 * 
 * @author Paul Jack
 * @author Stephen Colebourne
 * @author Neil O'Toole
 * @author Matthew Hawthorne
 */
public class SetUtils {

    /**
     * An empty unmodifiable set.
     * This uses the {@link Collections Collections} implementation 
     * and is provided for completeness.
     */
    public static final Set EMPTY_SET = Collections.EMPTY_SET;
    /**
     * An empty unmodifiable sorted set.
     * This is not provided in the JDK.
     */
    public static final SortedSet EMPTY_SORTED_SET = Collections.unmodifiableSortedSet(new TreeSet());

    /**
     * <code>SetUtils</code> should not normally be instantiated.
     */
    public SetUtils() {
    }

    //-----------------------------------------------------------------------
    /**
     * Tests two sets for equality as per the <code>equals()</code> contract
     * in {@link java.util.Set#equals(java.lang.Object)}.
     * <p>
     * This method is useful for implementing <code>Set</code> when you cannot
     * extend AbstractSet. The method takes Collection instances to enable other
     * collection types to use the Set implementation algorithm.
     * <p>
     * The relevant text (slightly paraphrased as this is a static method) is:
     * <blockquote>
     * <p>Two sets are considered equal if they have
     * the same size, and every member of the first set is contained in
     * the second. This ensures that the <tt>equals</tt> method works
     * properly across different implementations of the <tt>Set</tt>
     * interface.</p>
     * 
     * <p>
     * This implementation first checks if the two sets are the same object: 
     * if so it returns <tt>true</tt>.  Then, it checks if the two sets are
     * identical in size; if not, it returns false. If so, it returns
     * <tt>a.containsAll((Collection) b)</tt>.</p>
     * </blockquote>
     * 
     * @see java.util.Set
     * @param set1  the first set, may be null
     * @param set2  the second set, may be null
     * @return whether the sets are equal by value comparison
     */
    public static boolean isEqualSet(final Collection set1, final Collection set2) {
        if (set1 == set2) {
            return true;
        }
        if (set1 == null || set2 == null || set1.size() != set2.size()) {
            return false;
        }

        return set1.containsAll(set2);
    }

    /**
     * Generates a hashcode using the algorithm specified in 
     * {@link java.util.Set#hashCode()}.
     * <p>
     * This method is useful for implementing <code>Set</code> when you cannot
     * extend AbstractSet. The method takes Collection instances to enable other
     * collection types to use the Set implementation algorithm.
     * 
     * @see java.util.Set#hashCode()
     * @param set  the set to calculate the hashcode for, may be null
     * @return the hash code
     */
    public static int hashCodeForSet(final Collection set) {
        if (set == null) {
            return 0;
        }
        int hashCode = 0;
        Iterator it = set.iterator();
        Object obj = null;

        while (it.hasNext()) {
            obj = it.next();
            if (obj != null) {
                hashCode += obj.hashCode();
            }
        }
        return hashCode;
    }
    
    //-----------------------------------------------------------------------
    /**
     * Returns a synchronized set backed by the given set.
     * <p>
     * You must manually synchronize on the returned buffer's iterator to 
     * avoid non-deterministic behavior:
     *  
     * <pre>
     * Set s = SetUtils.synchronizedSet(mySet);
     * synchronized (s) {
     *     Iterator i = s.iterator();
     *     while (i.hasNext()) {
     *         process (i.next());
     *     }
     * }
     * </pre>
     * 
     * This method uses the implementation in {@link java.util.Collections Collections}.
     * 
     * @param set  the set to synchronize, must not be null
     * @return a synchronized set backed by the given set
     * @throws IllegalArgumentException  if the set is null
     */
    public static Set synchronizedSet(Set set) {
        return Collections.synchronizedSet(set);
    }

    /**
     * Returns an unmodifiable set backed by the given set.
     * <p>
     * This method uses the implementation in {@link java.util.Collections Collections}.
     *
     * @param set  the set to make unmodifiable, must not be null
     * @return an unmodifiable set backed by the given set
     * @throws IllegalArgumentException  if the set is null
     */
    public static Set unmodifiableSet(Set set) {
        return Collections.unmodifiableSet(set);
    }

    /**
     * Returns a predicated set backed by the given set.  Only objects
     * that pass the test in the given predicate can be added to the set.
     * It is important not to use the original set after invoking this 
     * method, as it is a backdoor for adding unvalidated objects.
     *
     * @param set  the set to predicate, must not be null
     * @param predicate  the predicate for the set, must not be null
     * @return a predicated set backed by the given set
     * @throws IllegalArgumentException  if the Set or Predicate is null
     */
    public static Set predicatedSet(Set set, Predicate predicate) {
        return PredicatedSet.decorate(set, predicate);
    }

    /**
     * Returns a typed set backed by the given set.
     * <p>
     * Only objects of the specified type can be added to the set.
     * 
     * @param set  the set to limit to a specific type, must not be null
     * @param type  the type of objects which may be added to the set
     * @return a typed set backed by the specified set
     */
    public static Set typedSet(Set set, Class type) {
        return TypedSet.decorate(set, type);
    }
    
    /**
     * Returns a transformed set backed by the given set.
     * <p>
     * Each object is passed through the transformer as it is added to the
     * Set. It is important not to use the original set after invoking this 
     * method, as it is a backdoor for adding untransformed objects.
     *
     * @param set  the set to predicate, must not be null
     * @param transformer  the transformer for the set, must not be null
     * @return a transformed set backed by the given set
     * @throws IllegalArgumentException  if the Set or Transformer is null
     */
    public static Set transformedSet(Set set, Transformer transformer) {
        return TransformedSet.decorate(set, transformer);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Returns a synchronized sorted set backed by the given sorted set.
     * <p>
     * You must manually synchronize on the returned buffer's iterator to 
     * avoid non-deterministic behavior:
     *  
     * <pre>
     * Set s = SetUtils.synchronizedSet(mySet);
     * synchronized (s) {
     *     Iterator i = s.iterator();
     *     while (i.hasNext()) {
     *         process (i.next());
     *     }
     * }
     * </pre>
     * 
     * This method uses the implementation in {@link java.util.Collections Collections}.
     * 
     * @param set  the sorted set to synchronize, must not be null
     * @return a synchronized set backed by the given set
     * @throws IllegalArgumentException  if the set is null
     */
    public static SortedSet synchronizedSortedSet(SortedSet set) {
        return Collections.synchronizedSortedSet(set);
    }

    /**
     * Returns an unmodifiable sorted set backed by the given sorted set.
     * <p>
     * This method uses the implementation in {@link java.util.Collections Collections}.
     *
     * @param set  the sorted set to make unmodifiable, must not be null
     * @return an unmodifiable set backed by the given set
     * @throws IllegalArgumentException  if the set is null
     */
    public static SortedSet unmodifiableSortedSet(SortedSet set) {
        return Collections.unmodifiableSortedSet(set);
    }

    /**
     * Returns a predicated sorted set backed by the given sorted set.  
     * Only objects that pass the test in the given predicate can be added
     * to the sorted set.
     * It is important not to use the original sorted set after invoking this 
     * method, as it is a backdoor for adding unvalidated objects.
     *
     * @param set  the sorted set to predicate, must not be null
     * @param predicate  the predicate for the sorted set, must not be null
     * @return a predicated sorted set backed by the given sorted set
     * @throws IllegalArgumentException  if the Set or Predicate is null
     */
    public static SortedSet predicatedSortedSet(SortedSet set, Predicate predicate) {
        return PredicatedSortedSet.decorate(set, predicate);
    }

    /**
     * Returns a typed sorted set backed by the given set.
     * <p>
     * Only objects of the specified type can be added to the set.
     * 
     * @param set  the set to limit to a specific type, must not be null
     * @param type  the type of objects which may be added to the set
     * @return a typed set backed by the specified set
     */
    public static SortedSet typedSortedSet(SortedSet set, Class type) {
        return TypedSortedSet.decorate(set, type);
    }
    
    /**
     * Returns a transformed sorted set backed by the given set.
     * <p>
     * Each object is passed through the transformer as it is added to the
     * Set. It is important not to use the original set after invoking this 
     * method, as it is a backdoor for adding untransformed objects.
     *
     * @param set  the set to predicate, must not be null
     * @param transformer  the transformer for the set, must not be null
     * @return a transformed set backed by the given set
     * @throws IllegalArgumentException  if the Set or Transformer is null
     */
    public static SortedSet transformedSortedSet(SortedSet set, Transformer transformer) {
        return TransformedSortedSet.decorate(set, transformer);
    }
    
}
