/*
 * Copyright 1999-2004 The Apache Software Foundation
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
package org.apache.commons.collections;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
/**
 * Provides static utility methods and decorators for {@link Set} 
 * and {@link SortedSet} instances.
 *
 * @author Paul Jack
 * @author Stephen Colebourne
 * @version $Id: SetUtils.java,v 1.7.2.1 2004/05/22 12:14:02 scolebourne Exp $
 * @since 2.1
 */
public class SetUtils {

    /**
     * Prevents instantiation.
     */
    private SetUtils() {
    }


    static class PredicatedSet 
            extends CollectionUtils.PredicatedCollection
            implements Set {

        public PredicatedSet(Set set, Predicate predicate) {
            super(set, predicate);
        }

    }


    static class PredicatedSortedSet 
            extends PredicatedSet 
            implements SortedSet {

        public PredicatedSortedSet(SortedSet set, Predicate predicate) {
            super(set, predicate);
        }

        public SortedSet subSet(Object o1, Object o2) {
            SortedSet sub = getSortedSet().subSet(o1, o2);
            return new PredicatedSortedSet(sub, predicate);
        }

        public SortedSet headSet(Object o1) {
            SortedSet sub = getSortedSet().headSet(o1);
            return new PredicatedSortedSet(sub, predicate);
        }

        public SortedSet tailSet(Object o1) {
            SortedSet sub = getSortedSet().tailSet(o1);
            return new PredicatedSortedSet(sub, predicate);
        }

        public Object first() {
            return getSortedSet().first();
        }

        public Object last() {
            return getSortedSet().last();
        }

        public Comparator comparator() {
            return getSortedSet().comparator();
        }

        private SortedSet getSortedSet() {
            return (SortedSet)collection;
        }

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
        return new PredicatedSet(set, predicate);
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
        return new PredicatedSortedSet(set, predicate);
    }

}
