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
import java.lang.Comparable;
import java.util.Comparator;

/**
 * A Comparator that compares Comparable objects.
 * Throws ClassCastExceptions if the objects are not 
 * Comparable, or if they are null.
 * Throws ClassCastException if the compareTo of both 
 * objects do not provide an inverse result of each other 
 * as per the Comparable javadoc.  This Comparator is useful, for example,
 * for enforcing the natural order in custom implementations
 * of SortedSet and SortedMap.
 *
 * @since 2.0
 * @author bayard@generationjava.com
 * @version $Id: ComparableComparator.java,v 1.5.2.1 2004/05/22 12:14:04 scolebourne Exp $
 */
public class ComparableComparator implements Comparator,Serializable {

    private static final ComparableComparator instance = 
        new ComparableComparator();

    /**
     *  Return a shared instance of a ComparableComparator.  Developers are
     *  encouraged to use the comparator returned from this method instead of
     *  constructing a new instance to reduce allocation and GC overhead when
     *  multiple comparable comparators may be used in the same VM.
     **/
    public static ComparableComparator getInstance() {
        return instance;
    }

    private static final long serialVersionUID=-291439688585137865L;

    public ComparableComparator() {
    }

    public int compare(Object o1, Object o2) {
        if( (o1 == null) || (o2 == null) ) {
            throw new ClassCastException(
                "There were nulls in the arguments for this method: "+
                "compare("+o1 + ", " + o2 + ")"
                );
        }
        
        if(o1 instanceof Comparable) {
            if(o2 instanceof Comparable) {
                int result1 = ((Comparable)o1).compareTo(o2);
                int result2 = ((Comparable)o2).compareTo(o1);

                // enforce comparable contract
                if(result1 == 0 && result2 == 0) {
                    return 0;
                } else
                if(result1 < 0 && result2 > 0) {
                    return result1;
                } else
                if(result1 > 0 && result2 < 0) {
                    return result1;
                } else {
                    // results inconsistent
                    throw new ClassCastException("o1 not comparable to o2");
                }
            } else {
                // o2 wasn't comparable
                throw new ClassCastException(
                    "The first argument of this method was not a Comparable: " +
                    o2.getClass().getName()
                    );
            }
        } else 
        if(o2 instanceof Comparable) {
            // o1 wasn't comparable
            throw new ClassCastException(
                "The second argument of this method was not a Comparable: " +
                o1.getClass().getName()
                );
        } else {
            // neither were comparable
            throw new ClassCastException(
                "Both arguments of this method were not Comparables: " +
                o1.getClass().getName() + " and " + o2.getClass().getName()
                );
        }
    }

}
