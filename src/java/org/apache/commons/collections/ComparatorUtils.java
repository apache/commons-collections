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


import java.util.Comparator;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.TransformingComparator;


/**
 *  Provides convenient static utility methods for <Code>Comparator</Code>
 *  objects.<P>
 *
 *  Most of the utility in this class can also be found in the 
 *  <Code>comparators</Code> package; this class merely provides a 
 *  convenient central place if you have use for more than one class
 *  in the <Code>comparators</Code> subpackage.<P>
 *
 *  Note that <I>every</I> method in this class allows you to specify
 *  <Code>null</Code> instead of a comparator, in which case 
 *  {@link #NATURAL} will be used.
 *
 *  @author Paul Jack
 *  @version $Id$
 */
public class ComparatorUtils {


    /**
     *  Comparator for natural sort order.
     *
     *  @see ComparableComparator#getInstance
     */
    final public static Comparator NATURAL = 
      ComparableComparator.getInstance();


    /**
     *  Returns a comparator that reverses the order of the given 
     *  comparator.
     *
     *  @param comparator  the comparator whose order to reverse
     *  @return  a comparator who reverses that order
     *  @see ReverseComparator
     */
    public static Comparator reverse(Comparator comparator) {
        if (comparator == null) comparator = NATURAL;
        return new ReverseComparator(comparator);
    }


    /**
     *  Allows the given comparator to compare <Code>null</Code> values.<P>
     *
     *  The returned comparator will consider a null value to be less than
     *  any nonnull value, and equal to any other null value.  Two nonnull
     *  values will be evaluated with the given comparator.<P>
     *
     *  @param comparator the comparator that wants to allow nulls
     *  @return  a version of that comparator that allows nulls
     *  @see NullComparator
     */
    public static Comparator nullLow(Comparator comparator) {
        if (comparator == null) comparator = NATURAL;
        return new NullComparator(comparator, false);
    }


    /**
     *  Allows the given comparator to compare <Code>null</Code> values.<P>
     *
     *  The returned comparator will consider a null value to be greater than
     *  any nonnull value, and equal to any other null value.  Two nonnull
     *  values will be evaluated with the given comparator.<P>
     *
     *  @param comparator the comparator that wants to allow nulls
     *  @return  a version of that comparator that allows nulls
     *  @see NullComparator
     */
    public static Comparator nullHigh(Comparator comparator) {
        if (comparator == null) comparator = NATURAL;
        return new NullComparator(comparator, true);
    }


    
    /**
     *  Passes transformed objects to the given comparator.<P>
     *
     *  Objects passed to the returned comparator will first be transformed
     *  by the given transformer before they are compared by the given
     *  comparator.<P>
     *
     *  @param comparator  the sort order to use
     *  @param t  the transformer to use
     *  @return  a comparator that transforms its input objects before 
     *    comparing them
     *  @see  TransformingComparator
     */
    public static Comparator transform(Comparator comparator, Transformer t) {
        if (comparator == null) comparator = NATURAL;
        return new TransformingComparator(t, comparator);
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
        if (comparator == null) comparator = NATURAL;
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
        if (comparator == null) comparator = NATURAL;
        int c = comparator.compare(o1, o2);
        return (c > 0) ? o1 : o2;        
    }
}
