/*
 * Copyright 2001-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.comparators;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Reverses the order of another comparator.
 * 
 * @since 2.0
 * @author bayard@generationjava.com
 * @author <a href="mailto:mas@apache.org">Michael A. Smith</a>
 * @version $Id: ReverseComparator.java,v 1.8.2.1 2004/05/22 12:14:04 scolebourne Exp $
 */
public class ReverseComparator implements Comparator,Serializable {

    private Comparator comparator;

    /**
     * Creates a comparator that compares objects based on the inverse of their
     * natural ordering.  Using this Constructor will create a ReverseComparator
     * that is functionaly identical to the Comparator returned by
     * java.util.Collections.<b>reverseOrder()</b>.
     * 
     * @see java.util.Collections#reverseOrder()
     */
    public ReverseComparator() {
        this(null);
    }

    /**
     * Creates a reverse comparator that inverts the comparison
     * of the passed in comparator.  If you pass in a null,
     * the ReverseComparator defaults to reversing the
     * natural order, as per 
     * java.util.Collections.<b>reverseOrder()</b>.
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

}
