/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Commons" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
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
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Comparator;

import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.collections.comparators.TransformingComparator;
/**
 * Provides convenient static utility methods for <Code>Comparator</Code>
 * objects.
 * <p>
 * Most of the utility in this class can also be found in the 
 * <code>comparators</code> package. This class merely provides a 
 * convenient central place if you have use for more than one class
 * in the <code>comparators</code> subpackage.
 * <p>
 * Note that <i>every</i> method in this class allows you to specify
 * <code>null</code> instead of a comparator, in which case 
 * {@link #NATURAL} will be used.
 *
 * @since 2.1
 * @author Paul Jack
 * @author <a href="mailto:scolebourne@joda.org">Stephen Colebourne</a>
 * @version $Id: $
 */
public class ComparatorUtils {

    /**
     * Restrictive constructor
     */
    private ComparatorUtils() {
    }

    /**
     * Comparator for natural sort order.
     *
     * @see ComparableComparator#getInstance
     */
    final public static Comparator NATURAL = ComparableComparator.getInstance();

    /**
     * Gets a comparator that uses the natural order of the objects.
     *
     * @return  a comparator which uses natural order
     */
    public static Comparator naturalComparator() {
        return NATURAL;
    }

    /**
     * Gets a comparator that compares using two {@link Comparator}s.
     * <p>
     * The second comparator is used if the first comparator returns
     * that equal.
     *
     * @param comparator1  the first comparator to use, not null
     * @param comparator2  the first comparator to use, not null
     * @return a combination comparator over the comparators
     * @throws NullPointerException if either comparator is null
     */
    public static Comparator chainedComparator(Comparator comparator1, Comparator comparator2) {
        return chainedComparator(new Comparator[] {comparator1, comparator2});
    }

    /**
     * Gets a comparator that compares using an array of {@link Comparator}s.
     * <p>
     * The second comparator is used if the first comparator returns
     * that equal and so on.
     *
     * @param iterators  the comparators to use, not null or empty or contain nulls
     * @return a combination comparator over the comparators
     * @throws NullPointerException if comparators array is null or contains a null
     */
    public static Comparator chainedComparator(Comparator[] comparators) {
        ComparatorChain chain = new ComparatorChain();
        for (int i = 0; i < comparators.length; i++) {
            if (comparators[i] == null) {
                throw new NullPointerException("Comparator cannot be null");
            }
            chain.addComparator(comparators[i]);
        }
        return chain;
    }

    /**
     * Gets a comparator that compares using a collection of {@link Comparator}s.
     * <p>
     * The second comparator is used if the first comparator returns
     * that equal and so on.
     *
     * @param comparators  the comparators to use, not null or empty or contain nulls
     * @return a combination comparator over the comparators
     * @throws NullPointerException if comparators collection is null or contains a null
     * @throws ClassCastException if the comparators collection contains the wrong object type
     */
    public static Comparator chainedComparator(Collection comparators) {
        return chainedComparator(
            (Comparator[]) comparators.toArray(new Comparator[comparators.size()])
        );
    }

    /**
     * Gets a comparator that reverses the order of the given 
     * comparator.
     *
     * @param comparator  the comparator whose order to reverse
     * @return  a comparator who reverses that order
     * @see ReverseComparator
     */
    public static Comparator reversedComparator(Comparator comparator) {
        if (comparator == null) {
            comparator = NATURAL;
        }
        return new ReverseComparator(comparator);
    }

    /**
     * Gets a Comparator that controls the comparison of <code>null</code> values.
     * <p>
     * The returned comparator will consider a null value to be less than
     * any nonnull value, and equal to any other null value.  Two nonnull
     * values will be evaluated with the given comparator.<P>
     *
     * @param comparator the comparator that wants to allow nulls
     * @return  a version of that comparator that allows nulls
     * @see NullComparator
     */
    public static Comparator nullLowComparator(Comparator comparator) {
        if (comparator == null) {
            comparator = NATURAL;
        }
        return new NullComparator(comparator, false);
    }

    /**
     * Gets a Comparator that controls the comparison of <code>null</code> values.
     * <p>
     * The returned comparator will consider a null value to be greater than
     * any nonnull value, and equal to any other null value.  Two nonnull
     * values will be evaluated with the given comparator.<P>
     *
     * @param comparator the comparator that wants to allow nulls
     * @return  a version of that comparator that allows nulls
     * @see NullComparator
     */
    public static Comparator nullHighComparator(Comparator comparator) {
        if (comparator == null) {
            comparator = NATURAL;
        }
        return new NullComparator(comparator, true);
    }

    /**
     * Gets a Comparator that passes transformed objects to the given comparator.
     * <p>
     * Objects passed to the returned comparator will first be transformed
     * by the given transformer before they are compared by the given
     * comparator.
     *
     * @param comparator  the sort order to use
     * @param transformer  the transformer to use
     * @return  a comparator that transforms its input objects before comparing them
     * @see  TransformingComparator
     */
    public static Comparator transformedComparator(Comparator comparator, Transformer transformer) {
        if (comparator == null) {
            comparator = NATURAL;
        }
        return new TransformingComparator(transformer, comparator);
    }

    /**
     *  Returns the smaller of the given objects according to the given 
     *  comparator.
     * 
     *  @param o1  the first object to compare
     *  @param o2  the second object to compare
     *  @param comparator  the sort order to use
     *  @return  the smaller of the two objects
     */
    public static Object min(Object o1, Object o2, Comparator comparator) {
        if (comparator == null) {
            comparator = NATURAL;
        }
        int c = comparator.compare(o1, o2);
        return (c < 0) ? o1 : o2;
    }

    /**
     *  Returns the smaller of the given objects according to the given 
     *  comparator.
     * 
     *  @param o1  the first object to compare
     *  @param o2  the second object to compare
     *  @param comparator  the sort order to use
     *  @return  the smaller of the two objects
     */
    public static Object max(Object o1, Object o2, Comparator comparator) {
        if (comparator == null) {
            comparator = NATURAL;
        }
        int c = comparator.compare(o1, o2);
        return (c > 0) ? o1 : o2;
    }
}
