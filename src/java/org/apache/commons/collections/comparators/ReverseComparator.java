/*
 *  Copyright 2001-2004 The Apache Software Foundation
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
 * Reverses the order of another comparator by 
 * reversing the arguments to its {@link #compare compare} 
 * method.
 * 
 * @since Commons Collections 2.0
 * @version $Revision: 1.18 $ $Date: 2004/02/18 00:59:06 $
 *
 * @author Henri Yandell
 * @author Michael A. Smith
 * 
 * @see java.util.Collections#reverseOrder
 */
public class ReverseComparator implements Comparator,Serializable {

    /**
     * Creates a comparator that compares objects based on the inverse of their
     * natural ordering.  Using this Constructor will create a ReverseComparator
     * that is functionally identical to the Comparator returned by
     * java.util.Collections.<b>reverseOrder()</b>.
     * 
     * @see java.util.Collections#reverseOrder
     */
    public ReverseComparator() {
        this(null);
    }

    /**
     * Creates a comparator that inverts the comparison
     * of the given comparator.  If you pass in <code>null</code>,
     * the ReverseComparator defaults to reversing the
     * natural order, as per 
     * {@link java.util.Collections#reverseOrder}</b>.
     * 
     * @param comparator Comparator to reverse
     */
    public ReverseComparator(Comparator comparator) {
        if(comparator != null) {
            this.comparator = comparator;
        } else {
            this.comparator = ComparableComparator.getInstance();
        }
    }

    public int compare(Object o1, Object o2) {
        return comparator.compare(o2, o1);
    }

    /**
     * Implement a hash code for this comparator that is consistent with
     * {@link #equals}.
     * 
     * @since Commons Collections 3.0
     */
    public int hashCode() {
        return "ReverseComparator".hashCode() ^ comparator.hashCode();
    }

    /**
     * Returns <code>true</code> iff <i>that</i> Object is 
     * is a {@link Comparator} whose ordering is known to be 
     * equivalent to mine.
     * <p>
     * This implementation returns <code>true</code>
     * iff <code><i>that</i>.{@link Object#getClass getClass()}</code>
     * equals <code>this.getClass()</code>, and the underlying 
     * comparators are equal.  Subclasses may want to override
     * this behavior to remain consistent with the 
     * {@link Comparator#equals} contract.
     * 
     * @since Commons Collections 3.0
     */
    public boolean equals(Object that) {
        if(this == that) {
            return true;
        } else if(null == that) {
            return false;
        } else if(that.getClass().equals(this.getClass())) {
            ReverseComparator thatrc = (ReverseComparator)that;
            return comparator.equals(thatrc.comparator);
        } else {
            return false;
        }
    }

    // use serialVersionUID from Collections 2.0 for interoperability
    private static final long serialVersionUID = 2858887242028539265L;

    private Comparator comparator;
}
