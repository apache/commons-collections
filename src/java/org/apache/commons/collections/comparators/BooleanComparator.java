/*
 *  Copyright 2003-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.comparators;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A {@link Comparator} for {@link Boolean} objects.
 * 
 * @see #getTrueFirstComparator
 * @see #getFalseFirstComparator
 * @see #getBooleanComparator
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.12 $ $Date: 2004/04/27 22:57:00 $
 * 
 * @author Rodney Waldhoff
 */
public final class BooleanComparator implements Comparator, Serializable {

    /**
     * Creates a <code>BooleanComparator</code> that sorts
     * <code>false</code> values before <code>true</code> values.
     * <p>
     * Equivalent to {@link #BooleanComparator(boolean) BooleanComparator(false)}.
     * <p>
     * Please use the static factory instead whenever possible.
     */
    public BooleanComparator() {
        this(false);
    }

    /**
     * Creates a <code>BooleanComparator</code> that sorts
     * <code><i>trueFirst</i></code> values before 
     * <code>&#x21;<i>trueFirst</i></code> values.
     * <p>
     * Please use the static factories instead whenever possible.
     * 
     * @param trueFirst when <code>true</code>, sort 
     *  <code>true</code> boolean values before <code>false</code>
     */
    public BooleanComparator(boolean trueFirst) {
        this.trueFirst = trueFirst;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares two arbitrary Objects.
     * When both arguments are <code>Boolean</code>, this method is equivalent to 
     * {@link #compare(Boolean,Boolean) compare((Boolean)<i>o1</i>,(Boolean)<i>o2</i>)}.
     * When either argument is not a <code>Boolean</code>, this methods throws
     * a {@link ClassCastException}.
     * 
     * @throws ClassCastException when either argument is not <code>Boolean</code>
     */
    public int compare(Object o1, Object o2) {
        return compare((Boolean)o1,(Boolean)o2);
    }
    
    /**
     * Compares two non-<code>null</code> <code>Boolean</code> objects
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
    
    //-----------------------------------------------------------------------
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
     * <code>true</code> <code>Boolean</code>s before <code>false</code>
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
