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
package org.apache.commons.collections.map;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import org.apache.commons.collections.set.UnmodifiableSet;

/**
 * Decorates another <code>SortedMap</code> to ensure it can't be altered.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.6 $ $Date: 2004/02/18 01:13:19 $
 * 
 * @author Stephen Colebourne
 */
public final class UnmodifiableSortedMap
        extends AbstractSortedMapDecorator implements Unmodifiable {

    /**
     * Factory method to create an unmodifiable sorted map.
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    public static SortedMap decorate(SortedMap map) {
        if (map instanceof Unmodifiable) {
            return map;
        }
        return new UnmodifiableSortedMap(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    private UnmodifiableSortedMap(SortedMap map) {
        super(map);
    }

    //-----------------------------------------------------------------------
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map mapToCopy) {
        throw new UnsupportedOperationException();
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public Set entrySet() {
        Set set = super.entrySet();
        return UnmodifiableEntrySet.decorate(set);
    }

    public Set keySet() {
        Set set = super.keySet();
        return UnmodifiableSet.decorate(set);
    }

    public Collection values() {
        Collection coll = super.values();
        return UnmodifiableCollection.decorate(coll);
    }

    //-----------------------------------------------------------------------
    public Object firstKey() {
        return getSortedMap().firstKey();
    }

    public Object lastKey() {
        return getSortedMap().lastKey();
    }

    public Comparator comparator() {
        return getSortedMap().comparator();
    }

    public SortedMap subMap(Object fromKey, Object toKey) {
        SortedMap map = getSortedMap().subMap(fromKey, toKey);
        return new UnmodifiableSortedMap(map);
    }

    public SortedMap headMap(Object toKey) {
        SortedMap map = getSortedMap().headMap(toKey);
        return new UnmodifiableSortedMap(map);
    }

    public SortedMap tailMap(Object fromKey) {
        SortedMap map = getSortedMap().tailMap(fromKey);
        return new UnmodifiableSortedMap(map);
    }

}
