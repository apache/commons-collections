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
 * A {@link Comparator Comparator} that compares 
 * {@link Comparable Comparable} objects.
 * <p />
 * This Comparator is useful, for example,
 * for enforcing the natural order in custom implementations
 * of SortedSet and SortedMap.
 * <p />
 * Note: In the 2.0 and 2.1 releases of Commons Collections, 
 * this class would throw a {@link ClassCastException} if
 * either of the arguments to {@link #compare compare}
 * were <code>null</code>, not {@link Comparable Comparable},
 * or for which {@link Comparable#compareTo compareTo} gave
 * inconsistent results.  This is no longer the case.  See
 * {@link #compare} for details.
 *
 * @since Commons Collections 2.0
 * @version $Revision: 1.14 $ $Date: 2004/02/18 00:59:06 $
 *
 * @author Henri Yandell
 *
 * @see java.util.Collections#reverseOrder
 */
public class ComparableComparator implements Comparator, Serializable {

    /**
     *  Return a shared instance of a ComparableComparator.  Developers are
     *  encouraged to use the comparator returned from this method instead of
     *  constructing a new instance to reduce allocation and GC overhead when
     *  multiple comparable comparators may be used in the same VM.
     **/
    public static ComparableComparator getInstance() {
        return instance;
    }

    public ComparableComparator() {
    }

    /**
     * Compare the two {@link Comparable Comparable} arguments.
     * This method is equivalent to:
     * <pre>(({@link Comparable Comparable})o1).{@link Comparable#compareTo compareTo}(o2)</pre>
     * @throws NullPointerException when <i>o1</i> is <code>null</code>, 
     *         or when <code>((Comparable)o1).compareTo(o2)</code> does
     * @throws ClassCastException when <i>o1</i> is not a {@link Comparable Comparable}, 
     *         or when <code>((Comparable)o1).compareTo(o2)</code> does
     */
    public int compare(Object o1, Object o2) {
        return ((Comparable)o1).compareTo(o2);
    }

    /**
     * Implement a hash code for this comparator that is consistent with
     * {@link #equals}.
     *
     * @return a hash code for this comparator.
     * @since Commons Collections 3.0
     */
    public int hashCode() {
        return "ComparableComparator".hashCode();
    }

    /**
     * Returns <code>true</code> iff <i>that</i> Object is 
     * is a {@link Comparator Comparator} whose ordering is 
     * known to be equivalent to mine.
     * <p>
     * This implementation returns <code>true</code>
     * iff <code><i>that</i>.{@link Object#getClass getClass()}</code>
     * equals <code>this.getClass()</code>.  Subclasses may want to override
     * this behavior to remain consistent with the {@link Comparator#equals}
     * contract.
     * @since Commons Collections 3.0
     */
    public boolean equals(Object that) {
        return (this == that) || 
               ((null != that) && (that.getClass().equals(this.getClass())));
    }

    private static final ComparableComparator instance = 
        new ComparableComparator();

    private static final long serialVersionUID=-291439688585137865L;

}
