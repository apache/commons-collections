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
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import org.apache.commons.collections.iterators.EntrySetMapIterator;
import org.apache.commons.collections.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections.set.UnmodifiableSet;

/**
 * Decorates another <code>Map</code> to ensure it can't be altered.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.9 $ $Date: 2004/02/18 01:13:19 $
 * 
 * @author Stephen Colebourne
 */
public final class UnmodifiableMap
        extends AbstractMapDecorator implements IterableMap, Unmodifiable {

    /**
     * Factory method to create an unmodifiable map.
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    public static Map decorate(Map map) {
        if (map instanceof Unmodifiable) {
            return map;
        }
        return new UnmodifiableMap(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    private UnmodifiableMap(Map map) {
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

    public MapIterator mapIterator() {
        if (map instanceof IterableMap) {
            MapIterator it = ((IterableMap) map).mapIterator();
            return UnmodifiableMapIterator.decorate(it);
        } else {
            MapIterator it = new EntrySetMapIterator(map);
            return UnmodifiableMapIterator.decorate(it);
        }
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

}
