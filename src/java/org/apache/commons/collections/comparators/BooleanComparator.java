/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/comparators/BooleanComparator.java,v 1.8 2003/10/05 23:21:07 scolebourne Exp $
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
package org.apache.commons.collections.comparators;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A {@link Comparator} for {@link Boolean}s.
 * 
 * @see #getTrueFirstComparator
 * @see #getFalseFirstComparator
 * @see #getBooleanComparator
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.8 $ $Date: 2003/10/05 23:21:07 $
 * 
 * @author Rodney Waldhoff
 */
public final class BooleanComparator implements Comparator, Serializable {

    /**
     * Creates a <code>BooleanComparator</code>
     * that sorts <code>false</code> values before 
     * <code>true</code> values.
     * 
     * Equivalent to {@link #BooleanComparator(boolean) BooleanComparator(false)}.
     */
    public BooleanComparator() {
        this(false);
    }

    /**
     * Creates a <code>BooleanComparator</code>
     * that sorts <code><i>trueFirst</i></code> values before 
     * <code>&#x21;<i>trueFirst</i></code> values.
     * 
     * @param trueFirst when <code>true</code>, sort 
     *        <code>true</code> {@link Boolean}s before
     *        <code>false</code> {@link Boolean}s.
     */
    public BooleanComparator(boolean trueFirst) {
        this.trueFirst = trueFirst;
    }

    /**
     * Compares two arbitrary Objects. When both arguments
     * are {@link Boolean}, this method is equivalent to 
     * {@link #compare(Boolean,Boolean) compare((Boolean)<i>o1</i>,(Boolean)<i>o2</i>)}.
     * When either argument is not a {@link Boolean}, this methods throws
     * a {@link ClassCastException}.
     * 
     * @throws ClassCastException when either argument is not 
     *         a {@link Boolean}
     */
    public int compare(Object o1, Object o2) {
        return compare((Boolean)o1,(Boolean)o2);
    }
    
    /**
     * Compares two non-<code>null</code> {@link Boolean}s
     * according to the value of {@link #sortsTrueFirst}.
     * 
     * @throws NullPointerException when either argument <code>null</code>
     */
    public int compare(Boolean b1, Boolean b2) {
        boolean v1 = b1.booleanValue();
        boolean v2 = b2.booleanValue();

        return (v1 ^ v2) ? ( (v1 ^ trueFirst) ? 1 : -1 ) : 0;
    }

    /**
     * Implement a hash code for this comparator that is consistent with
     * {@link #equals equals}.
     *
     * @return a hash code for this comparator.
     */
    public int hashCode() {
        int hash = "BooleanComparator".hashCode();
        return trueFirst ? -1 * hash : hash;
    }

    /**
     * Returns <code>true</code> iff <i>that</i> Object is 
     * is a {@link Comparator} whose ordering is known to be 
     * equivalent to mine.
     * <p>
     * This implementation returns <code>true</code>
     * iff <code><i>that</i></code> is a {@link BooleanComparator} 
     * whose {@link #sortsTrueFirst} value is equal to mine.
     */
    public boolean equals(Object that) {
        return (this == that) || 
               ((that instanceof BooleanComparator) && 
                (this.trueFirst == ((BooleanComparator)that).trueFirst));
    }

    /**
     * Returns <code>true</code> iff
     * I sort <code>true</code> values before 
     * <code>false</code> values.  In other words,
     * returns <code>true</code> iff
     * {@link #compare(Boolean,Boolean) compare(Boolean.FALSE,Boolean.TRUE)}
     * returns a positive value.
     */
    public boolean sortsTrueFirst() {
        return trueFirst;
    }
    
    /**
     * Returns a BooleanComparator instance that sorts 
     * <code>true</code> values before <code>false</code> values.
     * <p />
     * Clients are encouraged to use the value returned from 
     * this method instead of constructing a new instance 
     * to reduce allocation and garbage collection overhead when
     * multiple BooleanComparators may be used in the same 
     * virtual machine.
     */
    public static BooleanComparator getTrueFirstComparator() {
        return TRUE_FIRST;
    }
    
    /**
     * Returns a BooleanComparator instance that sorts 
     * <code>false</code> values before <code>true</code> values.
     * <p />
     * Clients are encouraged to use the value returned from 
     * this method instead of constructing a new instance 
     * to reduce allocation and garbage collection overhead when
     * multiple BooleanComparators may be used in the same 
     * virtual machine.
     */
    public static BooleanComparator getFalseFirstComparator() {
        return FALSE_FIRST;
    }
        
    /**
     * Returns a BooleanComparator instance that sorts 
     * <code><i>trueFirst</i></code> values before 
     * <code>&#x21;<i>trueFirst</i></code> values.
     * <p />
     * Clients are encouraged to use the value returned from 
     * this method instead of constructing a new instance 
     * to reduce allocation and garbage collection overhead when
     * multiple BooleanComparators may be used in the same 
     * virtual machine.
     * 
     * @param trueFirst when <code>true</code>, sort 
     *        <code>true</code> {@link Boolean}s before
     *        <code>false</code> {@link Boolean}s.
     * @return a cached BooleanComparator instance
     */
    public static BooleanComparator getBooleanComparator(boolean trueFirst) {
        return trueFirst ? TRUE_FIRST : FALSE_FIRST;
    }
    
    /** <code>true</code> iff <code>true</code> values sort before <code>false</code> values. */
    private boolean trueFirst = false;

    /** My static "true first" reference. */
    private static final BooleanComparator TRUE_FIRST = new BooleanComparator(true);

    /** My static "false first" reference. */
    private static final BooleanComparator FALSE_FIRST = new BooleanComparator(false);
}
