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
package org.apache.commons.collections.set;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.iterators.UnmodifiableIterator;

/**
 * Decorates another <code>SortedSet</code> to ensure it can't be altered.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.5 $ $Date: 2004/02/18 01:14:27 $
 * 
 * @author Stephen Colebourne
 */
public final class UnmodifiableSortedSet extends AbstractSortedSetDecorator implements Unmodifiable {

    /**
     * Factory method to create an unmodifiable set.
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    public static SortedSet decorate(SortedSet set) {
        if (set instanceof Unmodifiable) {
            return set;
        }
        return new UnmodifiableSortedSet(set);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    private UnmodifiableSortedSet(SortedSet set) {
        super(set);
    }

    //-----------------------------------------------------------------------
    public Iterator iterator() {
        return UnmodifiableIterator.decorate(getCollection().iterator());
    }

    public boolean add(Object object) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    //-----------------------------------------------------------------------
    public SortedSet subSet(Object fromElement, Object toElement) {
        SortedSet sub = getSortedSet().subSet(fromElement, toElement);
        return new UnmodifiableSortedSet(sub);
    }

    public SortedSet headSet(Object toElement) {
        SortedSet sub = getSortedSet().headSet(toElement);
        return new UnmodifiableSortedSet(sub);
    }

    public SortedSet tailSet(Object fromElement) {
        SortedSet sub = getSortedSet().tailSet(fromElement);
        return new UnmodifiableSortedSet(sub);
    }

}
